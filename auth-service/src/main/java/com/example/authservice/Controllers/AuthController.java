package com.example.authservice.Controllers;

import com.example.authservice.Models.User;
import com.example.authservice.Repository.UserRepository;
import com.example.authservice.Security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// Controller que cuida do cadastro, login e validação do token
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Endpoint pra cadastrar um novo usuário no banco
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Vê se o nome de usuário já está sendo usado
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }
        // Criptografa a senha antes de salvar pra segurança do usuário
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Se o cara não passou uma role, a gente bota "USER" por padrão
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // Endpoint pra fazer login e ganhar o token de acesso (JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // Valida se o usuário e senha batem
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // Busca o cara no banco de dados
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
        // Gera o token JWT com a role dele embutida
        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        // Devolve o token pro cliente
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // Endpoint pros outros microsserviços usarem pra checar se um token é quente/válido
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        try {
            // Extrai o nome de usuário e valida o token
            String username = jwtService.extractUsername(token);
            User user = userRepository.findByUsername(username).orElseThrow();
            if (jwtService.validateToken(token, user.getUsername())) {
                // Se estiver tudo certo, devolve o username e a role do cara
                Map<String, String> response = new HashMap<>();
                response.put("username", username);
                response.put("role", user.getRole());
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido ou expirado");
        }
        return ResponseEntity.status(401).body("Token inválido");
    }
}
