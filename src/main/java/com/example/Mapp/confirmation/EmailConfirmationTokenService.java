package com.example.Mapp.confirmation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailConfirmationTokenService {

    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;

    public void saveConfirmationToken(EmailConfirmationToken token){
        emailConfirmationTokenRepository.save(token);
    }

    public EmailConfirmationToken findByUserIdAndType(Long id, String type) {
        return emailConfirmationTokenRepository.findByUserIdAndType(id, type);
    }

    public List<EmailConfirmationToken> findAllByUserIdAndType(Long userId, String type){
        return emailConfirmationTokenRepository.findAllByUserIdAndType(userId, type);
    }

    public void delete(EmailConfirmationToken emailConfirmationToken) {
        emailConfirmationTokenRepository.delete(emailConfirmationToken);
    }

    public List<EmailConfirmationToken> findAllByUserIdAndTypeAndLinkStatus(Long userId, String type, String linkStatus){
        return emailConfirmationTokenRepository.findAllByUserIdAndTypeAndLinkStatus(userId, type, linkStatus);
    }
}
