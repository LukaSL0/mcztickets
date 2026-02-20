# Sistema de Microserviços para Venda de Ingressos

Uma plataforma distribuída de venda de ingressos para eventos, construída com arquitetura de microserviços Spring Boot, apresentando autenticação JWT, gerenciamento de eventos, processamento de pedidos e pagamentos.

## 🏗️ Arquitetura

Este sistema consiste em 4 microserviços independentes que se comunicam via APIs REST usando OpenFeign, acessados através de um API Gateway:

```
                                           ┌─────────────────┐
                                           │   API Gateway   │
                                           │     :8080       │
                                           └────────┬────────┘
                                                    │
                    ┌───────────────────────────────┼──────────────────────────────┐
                    │                               │                              │
                    ▼                               ▼                              ▼
             ┌─────────────┐                 ┌─────────────┐                ┌─────────────┐
             │    Auth     │                 │   Events    │                │   Orders    │
             │   :8081     │                 │   :8082     │ ◀───────────▶ │   :8083     │
             └─────────────┘                 └─────────────┘                └──────┬──────┘
                                                                                   │
                                                                                   ▼
                                                                            ┌─────────────┐
                                                                            │  Payments   │
                                                                            │   :8084     │
                                                                            └─────────────┘
```

### API Gateway (Porta 8080)
**Ponto de entrada único para todos os serviços**

O API Gateway atua como proxy reverso, roteando requisições para os microserviços apropriados. Adiciona o header `X-Gateway-Request: MCZTickets-Gateway` em todas as requisições:

- `http://localhost:8080/auth/**` → Auth Service (8081)
- `http://localhost:8080/events/**` → Events Service (8082)
- `http://localhost:8080/orders/**` → Orders Service (8083)
- `http://localhost:8080/payments/**` → Payments Service (8084)

---

## 📦 Serviços

### 1. Auth Service (Porta 8081)
**Autenticação JWT e gerenciamento de usuários**

#### Endpoints de Autenticação (`/auth`):
| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `POST` | `/auth` | Criar novo usuário (alternativo) | ❌ Não |
| `POST` | `/auth/register` | Registrar novo usuário | ❌ Não |
| `POST` | `/auth/login` | Autenticar usuário | ❌ Não |
| `POST` | `/auth/refresh` | Renovar access token | ❌ Não |
| `POST` | `/auth/logout` | Encerrar sessão (revoga tokens) | ✅ Sim |

#### Endpoints de Usuários (`/users`):
| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `GET` | `/users` | Listar todos os usuários | ✅ Sim |
| `GET` | `/users/{userId}` | Buscar usuário por ID | ✅ Sim |
| `PATCH` | `/users/{userId}/role` | Atualizar role do usuário | ✅ Sim |

#### Funcionalidades:
- **JWT Authentication**: Access tokens com expiração configurável
- **Refresh Tokens**: Tokens de renovação armazenados em banco de dados
- **Criptografia BCrypt**: Senhas armazenadas com hash seguro
- **Roles de Usuário**: `USER`, `ORGANIZER`, `ADMIN`
- **Validação de Conflitos**: Username e email únicos

#### Exemplo de Response (`AuthResponseDto`):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "expiresIn": 3600,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "João Silva",
    "username": "joaosilva",
    "email": "joao@email.com",
    "role": "USER"
  }
}
```

---

### 2. Events Service (Porta 8082)
**Criação de eventos e gerenciamento de inventário de ingressos**

#### Endpoints:
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/events` | Listar todos os eventos |
| `GET` | `/events/{eventId}` | Buscar detalhes do evento |
| `POST` | `/events` | Criar novo evento |
| `POST` | `/events/{eventId}/reserve` | Reservar ingressos (operação atômica) |

#### Funcionalidades:
- Catálogo de eventos com precificação
- Reserva de ingressos thread-safe usando locking otimista
- Rastreamento de disponibilidade de ingressos em tempo real
- Prevenção de overbooking com constraints de banco de dados

#### Lógica de Negócio:
```sql
-- Reserva atômica de ingressos
UPDATE events 
SET available_tickets = available_tickets - :tickets
WHERE id = :eventId 
AND available_tickets >= :tickets
```

---

### 3. Orders Service (Porta 8083)
**Gerenciamento do ciclo de vida de pedidos**

#### Endpoints:
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/orders` | Listar todos os pedidos |
| `GET` | `/orders/{orderId}` | Buscar detalhes do pedido |
| `POST` | `/orders` | Criar novo pedido |
| `PATCH` | `/orders/{orderId}/status` | Atualizar status do pedido |

#### Funcionalidades:
- Criação de pedidos com cálculo automático de preço
- Integração com Events Service para validação e reserva
- Fluxo de status: `PENDING` → `COMPLETED` / `CANCELLED`
- Reserva de ingressos automaticamente quando status muda para `COMPLETED`

#### Fluxo de Trabalho:
1. Usuário cria pedido → Status: `PENDING`
2. Serviço de pagamento processa pagamento
3. Se pagamento bem-sucedido → Status: `COMPLETED` → Ingressos reservados
4. Se pagamento falhar → Status: `CANCELLED`

---

### 4. Payments Service (Porta 8084)
**Processamento de pagamentos e gerenciamento de transações**

#### Endpoints:
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/payments` | Listar todos os pagamentos |
| `GET` | `/payments/{paymentId}` | Buscar detalhes do pagamento |
| `POST` | `/payments` | Criar pagamento para um pedido |
| `POST` | `/payments/{paymentId}` | Processar pagamento |

#### Métodos de Pagamento:
- `CREDIT_CARD` - Cartão de Crédito
- `DEBIT_CARD` - Cartão de Débito
- `PIX` - PIX
- `BOLETO` - Boleto Bancário

#### Funcionalidades:
- Geração de ID de transação único (`TX-{timestamp}`)
- Fluxo de status: `PENDING` → `PROCESSING` → `COMPLETED` / `FAILED`
- Gateway de pagamento simulado (80% de taxa de sucesso)
- Atualização automática do status do pedido baseado no resultado
- Validação de conflitos (pagamento duplicado, pedido já finalizado)

#### Fluxo de Pagamento:
1. Criar pagamento → Status: `PENDING`
2. Processar pagamento → Status: `PROCESSING`
3. Resposta do gateway → Status: `COMPLETED` ou `FAILED`
4. Atualizar status do pedido de acordo

---

## 🛠️ Tecnologias

| Categoria | Tecnologia |
|-----------|------------|
| **Framework** | Spring Boot 3.x |
| **Linguagem** | Java 17+ |
| **Banco de Dados** | PostgreSQL |
| **ORM** | Spring Data JPA / Hibernate |
| **Segurança** | Spring Security + JWT (jjwt) |
| **Criptografia** | BCrypt |
| **API Gateway** | Spring Cloud Gateway MVC |
| **Comunicação** | OpenFeign (REST) |
| **Validação** | Jakarta Validation |
| **Ferramenta de Build** | Maven / Gradle |

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior
- PostgreSQL 14+
- Maven 3.8+

### Variáveis de Ambiente (Auth Service)
```properties
jwt.secret=<base64-encoded-secret>
jwt.expiration=3600000
jwt.refresh-expiration=604800000
```

### Ordem de Inicialização
1. **PostgreSQL** - Garanta que o banco está rodando
2. **Auth Service** (:8081)
3. **Events Service** (:8082)
4. **Orders Service** (:8083)
5. **Payments Service** (:8084)
6. **API Gateway** (:8080)

### Executando cada serviço
```bash
cd auth && mvn spring-boot:run
cd events && mvn spring-boot:run
cd orders && mvn spring-boot:run
cd payments && mvn spring-boot:run
cd gateway && mvn spring-boot:run
```

---

## 📝 Exemplos de Uso

### Registrar Usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "username": "joaosilva",
    "email": "joao@email.com",
    "password": "senha123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "password": "senha123"
  }'
```

### Listar Eventos (autenticado)
```bash
curl -X GET http://localhost:8080/events \
  -H "Authorization: Bearer {accessToken}"
```

### Criar Pedido
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {accessToken}" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "eventId": 1,
    "tickets": 2
  }'
```

### Criar e Processar Pagamento
```bash
# Criar pagamento
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {accessToken}" \
  -d '{
    "orderId": 1,
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "paymentMethod": "PIX"
  }'

# Processar pagamento
curl -X POST http://localhost:8080/payments/1 \
  -H "Authorization: Bearer {accessToken}"
```
