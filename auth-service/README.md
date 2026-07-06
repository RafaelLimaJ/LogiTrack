# Auth Service

Serviço de autenticação do LogiTrack.

## Funcionalidades
- Cadastro de usuários (`POST /auth/register`).
- Login com validação de senha encriptada (`POST /auth/login`).
- Validação de tokens JWT (`GET /auth/validate`).

## Tecnologias
- Java 17
- Spring Boot
- Spring Security + JWT
- MySQL (JPA)
- OpenAPI (Swagger UI)
