package com.example.authservice;

import com.example.authservice.Security.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// Teste unitário puro para garantir que a geração e validação de tokens JWT funcionam sem precisar de banco de dados
class AuthServiceApplicationTests {

    private final JwtService jwtService = new JwtService();

    @Test
    void testJwtFlow() {
        String username = "ebacUser";
        String role = "ADMIN";

        // 1. Gera o token
        String token = jwtService.generateToken(username, role);
        Assertions.assertNotNull(token);

        // 2. Valida o token
        Assertions.assertTrue(jwtService.validateToken(token, username));

        // 3. Valida a extração de dados
        Assertions.assertEquals(username, jwtService.extractUsername(token));
    }
}
