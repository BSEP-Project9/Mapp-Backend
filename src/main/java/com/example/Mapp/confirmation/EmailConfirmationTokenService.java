package com.example.Mapp.confirmation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailConfirmationTokenService {

    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;

    public void saveConfirmationToken(EmailConfirmationToken token){
        emailConfirmationTokenRepository.save(token);
    }

    public EmailConfirmationToken findByUserId(Long id) {
        return emailConfirmationTokenRepository.findByUserId(id);
    }
}
