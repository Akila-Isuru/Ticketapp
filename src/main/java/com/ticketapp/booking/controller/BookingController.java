package com.ticketapp.booking.controller;

import com.ticketapp.booking.dto.BookingRequestDTO;
import com.ticketapp.booking.dto.BookingResponseDTO;
import com.ticketapp.booking.service.BookingService;
import com.ticketapp.booking.utill.QRCodeGenerator;
import com.ticketapp.booking.utill.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin

public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<StandardResponse> createBooking(@RequestBody @Valid BookingRequestDTO bookingRequestDTO) throws Exception {

        BookingResponseDTO bookingResponseDTO = bookingService.createBooking(bookingRequestDTO);
        return new ResponseEntity<>(
                new StandardResponse(201,"Booking initiated successfully",bookingResponseDTO),
                HttpStatus.CREATED
        );

    }

    @PostMapping("/notify")
    public ResponseEntity<String> payhereNotify(
            @RequestParam(value = "merchant_id",required = false) String merchantId,
            @RequestParam("order_id") String orderId,
            @RequestParam("payment_id") String paymentId,
            @RequestParam(value = "payhere_amount",required = false) String payhereAmount,
            @RequestParam(value = "payhere_currency",required = false) String payhereCurrency,
            @RequestParam("status_code") String statusCode,
            @RequestParam(value = "md5sig",required = false) String md5sig
    ){
        boolean isSuccess = bookingService.updatePaymentStatus(
                merchantId, orderId, paymentId, payhereAmount, payhereCurrency, statusCode, md5sig
        );
        if(isSuccess){
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Unauthorized Request", HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<StandardResponse> getBookingByUserId(@PathVariable Long userId) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUserId(userId);
        return new ResponseEntity<>(
                new StandardResponse(200,"Bookings found",bookings),
                HttpStatus.OK
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<StandardResponse> getBookingByBookingId(@PathVariable Long bookingId) {
        BookingResponseDTO booking = bookingService.getBookingsById(bookingId);
        return new ResponseEntity<>(
                new StandardResponse(200,"Booking found",booking),
                HttpStatus.OK
        );
    }

    @PutMapping(path = "/cancel/{id}")
    public ResponseEntity<StandardResponse> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return new ResponseEntity<>(
                new StandardResponse(200,"Booking cancelled successfully and tickets restored",null),
                HttpStatus.OK
        );
    }
    @PutMapping("/pay/{bookingId}")
    public ResponseEntity<StandardResponse> processPayment(@PathVariable Long bookingId) {
        BookingResponseDTO bookingResponseDTO = bookingService.processMockPayment(bookingId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Payment completed successfully", bookingResponseDTO),
                HttpStatus.OK
        );
    }
    @GetMapping(value = "/{bookingId}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBookingQRCode(@PathVariable Long bookingId) throws Exception {
        BookingResponseDTO booking = bookingService.getBookingsById(bookingId);
        byte[] qrImage = QRCodeGenerator.generateQRCodeImage(booking.getOrderId(), 300, 300);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }


}
