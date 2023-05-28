package com.example.Mapp.confirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

    Optional<EmailConfirmationToken> findByToken(String token);

    EmailConfirmationToken findByUserIdAndType(Long userId, String type);

    List<EmailConfirmationToken> findAllByUserIdAndType(Long userId, String type);

    List<EmailConfirmationToken> findAllByUserIdAndTypeAndLinkStatus(Long userId, String type, String linkStatus);
}
