package com.ticketapp.booking.controller;

import com.ticketapp.booking.dto.BookingRequestDTO;
import com.ticketapp.booking.dto.BookingResponseDTO;
import com.ticketapp.booking.service.BookingService;
import com.ticketapp.booking.utill.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

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
        if("2".equals(statusCode)){
            bookingService.updatePaymentStatus(orderId,paymentId,"PAID");
        }else{
            bookingService.updatePaymentStatus(orderId,paymentId,"FAILED");
        }
        return new ResponseEntity<>("OK",HttpStatus.OK);
    }


}
