package com.abuamar.order_management_service.service.impl

import com.abuamar.order_management_service.rest.RoomClient
import com.abuamar.order_management_service.rest.UserClient
import com.abuamar.order_management_service.domain.entity.MasterOrderEntity
import com.abuamar.order_management_service.domain.dto.req.ReqCreateOrder
import com.abuamar.order_management_service.domain.dto.req.ReqUpdateOrder
import com.abuamar.order_management_service.domain.dto.res.ResOrder
import com.abuamar.order_management_service.domain.enum.OrderStatus
import com.abuamar.order_management_service.exception.CustomException
import com.abuamar.order_management_service.repository.MasterOrderRepository
import com.abuamar.order_management_service.service.OrderService
import com.abuamar.order_management_service.util.AppConstants
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class OrderServiceImpl(
    private val orderRepository: MasterOrderRepository,
    private val roomClient: RoomClient,
    private val userClient: UserClient,
    private val httpServletRequest: HttpServletRequest
) : OrderService {
    private fun getAuthenticatedUserId(): String {
        return httpServletRequest.getHeader(AppConstants.HEADER_USER_ID)
            ?: throw CustomException(
                AppConstants.ERR_AUTH_HEADER_MISSING,
                HttpStatus.UNAUTHORIZED.value()
            )
    }

    private fun isAdmin(): Boolean {
        return httpServletRequest.getHeader(AppConstants.HEADER_USER_AUTHORITY) == AppConstants.ROLE_ADMIN
    }

    private fun requireAdmin() {
        if (!isAdmin()) {
            throw CustomException(
                AppConstants.ERR_UNAUTHORIZED,
                HttpStatus.FORBIDDEN.value()
            )
        }
    }
    
    private fun mapToResOrder(order: MasterOrderEntity): ResOrder {
        // Fetch user details via Feign Client
        val user = userClient.getUserById(order.userId).body!!.data!!

        // Fetch room details via Feign Client
        val room = roomClient.getRoomById(order.roomId).body!!.data!!

        return ResOrder(
            id = order.id,
            orderNumber = order.orderNumber,
            user = user,
            room = room,
            checkInDate = order.checkInDate,
            checkOutDate = order.checkOutDate,
            nights = order.nights,
            guestCount = order.guestCount,
            totalAmount = order.totalAmount,
            specialRequests = order.specialRequests,
            status = order.status,
            paymentStatus = order.paymentStatus,
            createdAt = order.createdAt!!,
            updatedAt = order.updatedAt!!,
            createdBy = order.createdBy ?: AppConstants.SYSTEM_USER,
            updatedBy = order.updatedBy ?: AppConstants.SYSTEM_USER
        )
    }
    
    private fun generateOrderNumber(): String {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val todayOrderCount = orderRepository.countOrdersToday()
        val sequence = todayOrderCount + 1
        
        return "ORD-$today-${sequence.toString().padStart(5, '0')}"
    }

    private fun validateUserExists(userId: Int) {
        val user = userClient.getUserById(userId).body?.data
            ?: throw CustomException(
                "${AppConstants.ERR_USER_NOT_FOUND_EXTERNAL} with id $userId",
                HttpStatus.NOT_FOUND.value()
            )
    }
    
    override fun getAllOrders(): List<ResOrder> {
        val orders = orderRepository.findAllActive()
        
        if (orders.isEmpty()) {
            throw CustomException(
                AppConstants.ERR_NO_ORDERS_FOUND,
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        return orders.map { mapToResOrder(it) }
    }

    override fun getOrderById(id: Int): ResOrder {
        val order = orderRepository.findActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ORDER_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        return mapToResOrder(order)
    }

    override fun getOrdersByUserId(userId: Int): List<ResOrder> {
        // Validate user exists
        validateUserExists(userId)

        val orders = orderRepository.findByUserId(userId)
        
        if (orders.isEmpty()) {
            throw CustomException(
                "${AppConstants.ERR_NO_ORDERS_FOUND} for user ID: $userId",
                HttpStatus.NOT_FOUND.value()
            )
        }
        
        return orders.map { mapToResOrder(it) }
    }

    @Transactional
    override fun createOrder(request: ReqCreateOrder): ResOrder {
        val userId = getAuthenticatedUserId()
        
        // Validate dates
        val checkIn = request.checkInDate.toLocalDate()
        val checkOut = request.checkOutDate.toLocalDate()
        
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw CustomException(
                AppConstants.ERR_INVALID_DATE_RANGE,
                HttpStatus.BAD_REQUEST.value()
            )
        }

        // Calculate nights
        val nights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()

        // Get room details via Feign Client
        val room = roomClient.getRoomById(request.roomId).body!!.data!!

        // Validate room status
        if (room.status != "AVAILABLE") {
            throw CustomException(
                AppConstants.ERR_ROOM_STATUS_UNAVAILABLE,
                HttpStatus.BAD_REQUEST.value()
            )
        }

        // Validate guest count
        if (request.guestCount > room.capacity) {
            throw CustomException(
                "${AppConstants.ERR_GUEST_COUNT_EXCEEDS} (max: ${room.capacity})",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        // Check for conflicting orders
        val conflicts = orderRepository.findConflictingOrders(
            request.roomId,
            request.checkInDate,
            request.checkOutDate
        )
        
        if (conflicts.isNotEmpty()) {
            throw CustomException(
                AppConstants.ERR_ROOM_NOT_AVAILABLE,
                HttpStatus.BAD_REQUEST.value()
            )
        }

        // Validate user exists
        // validateUserExists(request.userId)

        // Calculate total amount
        val totalAmount = nights * room.price

        // Generate order number
        val orderNumber = generateOrderNumber()

        // Create entity
        val order = MasterOrderEntity(
            orderNumber = orderNumber,
            userId = request.userId,
            roomId = request.roomId,
            checkInDate = request.checkInDate,
            checkOutDate = request.checkOutDate,
            nights = nights,
            guestCount = request.guestCount,
            totalAmount = totalAmount,
            specialRequests = request.specialRequests
        )

        order.createdBy = getAuthenticatedUserId()
        order.createdAt = Timestamp(System.currentTimeMillis())

        order.updatedBy = order.createdBy
        order.updatedAt = Timestamp(System.currentTimeMillis())

        order.isActive = true
        order.isDelete = false

        val savedOrder = orderRepository.save(order)
        return mapToResOrder(savedOrder)
    }

    @Transactional
    override fun updateOrder(id: Int, request: ReqUpdateOrder): ResOrder {
        val userId = getAuthenticatedUserId()
        
        val order = orderRepository.findActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ORDER_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        // Update room if provided
        if (request.roomId != null && request.roomId != order.roomId) {
            val room = roomClient.getRoomById(request.roomId).body!!.data!!

            if (room.status != "AVAILABLE") {
                throw CustomException(
                    AppConstants.ERR_ROOM_STATUS_UNAVAILABLE,
                    HttpStatus.BAD_REQUEST.value()
                )
            }

            order.roomId = request.roomId
        }

        // Update dates and recalculate
        val checkInDate = request.checkInDate ?: order.checkInDate
        val checkOutDate = request.checkOutDate ?: order.checkOutDate

        if (request.checkInDate != null || request.checkOutDate != null) {
            val checkIn = checkInDate.toLocalDate()
            val checkOut = checkOutDate.toLocalDate()

            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                throw CustomException(
                    AppConstants.ERR_INVALID_DATE_RANGE,
                    HttpStatus.BAD_REQUEST.value()
                )
            }

            // Memastikan tidak bentrok dengan order lain
            val conflicts = orderRepository.findConflictingOrders(
                order.roomId,
                Date.valueOf(checkIn),
                Date.valueOf(checkOut)
            ).filter { it.id != order.id }

            if (conflicts.isNotEmpty()) {
                throw CustomException(
                    AppConstants.ERR_ROOM_NOT_AVAILABLE,
                    HttpStatus.BAD_REQUEST.value()
                )
            }

            // Menghitung ulang tanggal
            order.checkInDate = Date.valueOf(checkIn)
            order.checkOutDate = Date.valueOf(checkOut)
            
            // Menghitung ulang malam
            val nights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
            order.nights = nights

            // Recalculate total
            val room = roomClient.getRoomById(order.roomId).body!!.data!!

            // Menghitung total harga
            order.totalAmount = nights * room.price
        }

        // Update guest count
        if (request.guestCount != null) {
            val room = roomClient.getRoomById(order.roomId).body!!.data!!

            if (request.guestCount > room.capacity) {
                throw CustomException(
                    "${AppConstants.ERR_GUEST_COUNT_EXCEEDS} (max: ${room.capacity})",
                    HttpStatus.BAD_REQUEST.value()
                )
            }
            order.guestCount = request.guestCount
        }

        // Update other fields
        request.specialRequests?.let { order.specialRequests = it }
        request.status?.let { order.status = it }
        request.paymentStatus?.let { order.paymentStatus = it }

        order.updatedBy = getAuthenticatedUserId()
        order.updatedAt = Timestamp(System.currentTimeMillis())

        val updatedOrder = orderRepository.save(order)
        return mapToResOrder(updatedOrder)
    }

    @Transactional
    override fun deleteOrder(id: Int) {
        requireAdmin()
        
        val userId = getAuthenticatedUserId()
        
        val order = orderRepository.findActiveById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ORDER_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        order.isDelete = true
        order.deletedAt = Timestamp(System.currentTimeMillis())
        order.deletedBy = getAuthenticatedUserId()

        orderRepository.save(order)
    }

    @Transactional
    override fun restoreOrder(id: Int): ResOrder {
        requireAdmin()

        val userId = getAuthenticatedUserId()

        val order = orderRepository.findById(id).orElseThrow {
            CustomException(
                "${AppConstants.ERR_ORDER_NOT_FOUND} with id $id",
                HttpStatus.NOT_FOUND.value()
            )
        }

        if (!order.isDelete) {
            throw CustomException(
                "${AppConstants.ERR_ORDER_ALREADY_ACTIVE} with id $id",
                HttpStatus.BAD_REQUEST.value()
            )
        }

        order.isDelete = false
        order.updatedBy = getAuthenticatedUserId()
        order.updatedAt = Timestamp(System.currentTimeMillis())

        val restoredOrder = orderRepository.save(order)
        return mapToResOrder(restoredOrder)
    }

    override fun checkRoomAvailability(roomId: Int, checkInDate: String, checkOutDate: String): Boolean {
        // Parse dates
        val requestedCheckIn = LocalDate.parse(checkInDate)
        val requestedCheckOut = LocalDate.parse(checkOutDate)

        // Find conflicting orders
        val conflicts = orderRepository.findConflictingOrders(
            roomId,
            Date.valueOf(requestedCheckIn),
            Date.valueOf(requestedCheckOut)
        )

        // Filter only orders with active reservation status
        // Exclude COMPLETED and CANCELLED (room is available after these statuses)
        val reservedOrders = conflicts.filter { order ->
            order.status in listOf(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.CHECKED_IN,
                OrderStatus.CHECKED_OUT
            )
        }

        // Return true if room is reserved (has conflicts), false if available
        return reservedOrders.isNotEmpty()
    }
}