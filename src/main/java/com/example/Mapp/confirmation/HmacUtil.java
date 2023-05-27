package com.example.Mapp.confirmation;

import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.datatype.DatatypeConstants;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class HmacUtil {

    private static final String  SECRET_KEY = "tajni_kljuc";

    public  String signToken(String token) throws NoSuchAlgorithmException, InvalidKeyException {
        try {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKeySpec);
            byte[] hmacBytes = hmacSHA256.doFinal(token.getBytes(StandardCharsets.UTF_8));
            String hmacString = Base64.getEncoder().encodeToString(hmacBytes);
            return hmacString;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean verifySignature(String receivedToken, String receivedHmac) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] hmacBytes = hmacSHA256.doFinal(receivedToken.getBytes(StandardCharsets.UTF_8));
        String calculatedHmac = Base64.getEncoder().encodeToString(hmacBytes);
        boolean isValid = calculatedHmac.equals(receivedHmac);
        return isValid;
    }
}
