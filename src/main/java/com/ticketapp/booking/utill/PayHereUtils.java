package com.ticketapp.booking.utill;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

public class PayHereUtils {

    private static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating MD5 hash", e);
        }
    }

    // PayHere Hash Formula: MD5(merchant_id + order_id + amount + currency + MD5(merchant_secret))
    public static String generateHash(String merchantId, String orderId, double amount, String currency, String merchantSecret) {
        DecimalFormat df = new DecimalFormat("0.00");
        String formattedAmount = df.format(amount);
        String hashedSecret = getMd5(merchantSecret);
        String source = merchantId + orderId + formattedAmount + currency + hashedSecret;
        return getMd5(source);
    }
    public static boolean verifyNotifyHash(String merchantId,
                                           String orderId,
                                           String payhereAmount,
                                           String payhereCurrency,
                                           String statusCode,
                                           String merchantSecret,
                                           String receivedMd5sig){

        String hashedSecret = getMd5(merchantSecret);
        String source = merchantId + orderId + payhereAmount + payhereCurrency + statusCode + hashedSecret;
        String calculatedHash = getMd5(source);

        return calculatedHash.equalsIgnoreCase(receivedMd5sig);

    }
}