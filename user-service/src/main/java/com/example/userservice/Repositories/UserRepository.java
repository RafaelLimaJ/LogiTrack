package com.example.userservice.Repositories;

import com.example.userservice.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Interface pra acessar a tabela de usuários
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
