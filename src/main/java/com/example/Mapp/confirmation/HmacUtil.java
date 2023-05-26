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

    public  String signToken(String token, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        try {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKeySpec);
            byte[] hmacBytes = hmacSHA256.doFinal(token.getBytes(StandardCharsets.UTF_8));
            String hmacString = Base64.getEncoder().encodeToString(hmacBytes);
            return hmacString;
            // Sada imate potpisani token (hmacString) koji možete poslati zajedno sa tokenom na server.
            // Na serveru ćete proveriti validnost tako što ćete ponoviti isti postupak i uporediti dobijeni HMAC potpis sa onim koji ste dobili od klijenta.
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean verifySignature(String receivedToken, String receivedHmac, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] hmacBytes = hmacSHA256.doFinal(receivedToken.getBytes(StandardCharsets.UTF_8));
        String calculatedHmac = Base64.getEncoder().encodeToString(hmacBytes);

// Uporedite dobijeni HMAC potpis sa primljenim HMAC potpisom
        boolean isValid = calculatedHmac.equals(receivedHmac);
        return isValid;
    }
}
