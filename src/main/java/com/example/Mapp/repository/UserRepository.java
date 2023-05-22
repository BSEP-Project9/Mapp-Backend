package com.example.Mapp.repository;

import com.example.Mapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPassword(String email, String password);

    User findOneByEmail(String email);

    Optional<User> findByEmail(String username);
}
