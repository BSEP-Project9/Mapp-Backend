package com.example.Mapp.confirmation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "confirmation")
public class EmailConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;
    @Column
    private LocalDateTime confirmedAt;

    @Column
    private Long userId;

    @Column
    private String type;

    @Column
    private String linkStatus;

    public EmailConfirmationToken(String token,
                             LocalDateTime issuedAt,
                             LocalDateTime confirmedAt,
                             Long userId,
                                  String type) {
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiredAt = confirmedAt;
        this.userId = userId;
        this.type = type;
    }

    public EmailConfirmationToken(String token,
                                  LocalDateTime issuedAt,
                                  LocalDateTime confirmedAt,
                                  Long userId,
                                  String type,
                                  String linkStatus) {
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiredAt = confirmedAt;
        this.userId = userId;
        this.type = type;
        this.linkStatus = linkStatus;
    }
}
