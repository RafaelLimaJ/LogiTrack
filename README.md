# LogiTrack - Sistema de Rastreamento de Entregas

Este projeto implementa uma arquitetura de microsserviços para orquestração de pedidos e rastreamento de entregas em tempo real, utilizando Java, Spring Boot, MySQL, MongoDB, Apache Kafka e AWS LocalStack (SQS e DynamoDB).

## Desenho da Arquitetura

O sistema é dividido em 5 microsserviços independentes:

1. **`auth-service`**: Cuida do login, cadastro de usuários e geração do token JWT. Possui banco de dados MySQL próprio.
2. **`user-service`**: CRUD de usuários do sistema. Protegido por autenticação JWT e validação de roles (RBAC).
3. **`order-service`**: Gerencia a criação e atualização de pedidos. Quando um pedido é criado com status "PENDENTE", publica um evento no Kafka.
4. **`delivery-service`**: Ouve os eventos do Kafka de novos pedidos e gera automaticamente a entrega correspondente. Permite alterar o status da entrega e publica atualizações no Kafka.
5. **`tracking-service`**: Consome eventos de entregas via Kafka e:
   - Salva a situação atualizada no **MongoDB**.
   - Dispara uma notificação para uma fila **AWS SQS** no LocalStack.
   - Grava um histórico permanente em uma tabela **AWS DynamoDB** no LocalStack.

---

## Como Rodar Localmente (Ambiente Completo)

### Pré-requisitos
- **Java 17** e **Maven** instalados localmente (se quiser compilar os jars manualmente).
- **Docker** e **Docker Compose** instalados e rodando na máquina.

### Passo 1: Compilar as aplicações Java
Rode o seguinte comando na raiz de cada uma das subpastas (`auth-service`, `user-service`, `order-service`, `delivery-service`, `tracking-service`):
```bash
mvn clean package -DskipTests
```
Isso vai gerar o arquivo `.jar` na pasta `target/` de cada microsserviço.

### Passo 2: Subir os containers do Docker
Na pasta raiz do projeto (onde está localizado o arquivo `docker-compose.yml`), execute o comando:
```bash
docker compose up --build
```
Esse comando irá compilar as imagens customizadas dos microsserviços e subir todas as dependências:
- MySQL (porta externa `3307`)
- MongoDB (porta externa `27017`)
- Apache Kafka (porta externa `9092`)
- LocalStack (porta externa `4566`)
- `auth-service` (porta `8080`)
- `user-service` (porta `8081`)
- `order-service` (porta `8082`)
- `delivery-service` (porta `8083`)
- `tracking-service` (porta `8084`)

---

## Documentação dos Endpoints (Swagger)

Com o Docker Compose ativo, você pode abrir o Swagger UI individual de cada serviço para testar as rotas:
- **Auth Service**: `http://localhost:8080/swagger-ui.html`
- **User Service**: `http://localhost:8081/swagger-ui.html`
- **Order Service**: `http://localhost:8082/swagger-ui.html`
- **Delivery Service**: `http://localhost:8083/swagger-ui.html`
- **Tracking Service**: `http://localhost:8084/swagger-ui.html`
