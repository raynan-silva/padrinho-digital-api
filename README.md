# 🐾 Padrinho Digital - API Backend

**Backend robusto e escalável para conectar corações generosos a animais que precisam de amor e cuidado**

[![Vídeo de Apresentação](https://img.shields.io/badge/▶️-Vídeo%20de%20Apresentação-red)](https://drive.google.com/file/d/1rFa5vZ1ksvYSBQuLu9WqXAop5gFPWb2F/view?usp=sharing)

---

## 📋 Sobre o Projeto

**Padrinho Digital API** é o backend da plataforma Padrinho Digital, desenvolvido por alunos da **Universidade de Vila Velha**. A API fornece todos os serviços necessários para conectar pessoas generosas a ONGs e abrigos de animais através de um sistema completo de apadrinhamento, transparência financeira e comunicação em tempo real.

---

## 🔗 Repositório do Frontend

Frontend disponível em: [padrinho-digital](https://github.com/raynan-silva/padrinho-digital)

### 🎯 Objetivo da API

Criar uma arquitetura robusta e segura que:

- 🔐 Autentique e autorize diferentes tipos de usuários
- 💰 Processe e gerencie contribuições financeiras
- 📊 Forneça relatórios transparentes de despesas
- 💬 Suporte comunicação em tempo real entre padrinhos e ONGs
- 🎖️ Implemente sistema de gamificação
- 📱 Oferece endpoints para mobile e web

---

## 🚀 Funcionalidades Principais da API

### 🔐 Autenticação e Autorização
- ✅ **Login JWT**: Autenticação segura com tokens JWT
- ✅ **Recuperação de Senha**: Email com link seguro de reset
- ✅ **Validação de Dados**: Validação automática de entrada
- ✅ **Controle de Acesso**: RBAC (Role-Based Access Control)
- ✅ **Refresh Tokens**: Renovação automática de sessões

### 🐕 Gerenciamento de Animais
- ✅ **CRUD Completo**: Criar, ler, atualizar e deletar pets
- ✅ **Galeria de Fotos**: Upload e gerenciamento de imagens
- ✅ **Histórico de Saúde**: Rastreamento completo do bem-estar
- ✅ **Status e Categorias**: Classificação detalhada
- ✅ **Associação com ONGs**: Vínculo automático

### 💰 Sistema de Apadrinhamento
- ✅ **Criar Apadrinhamentos**: Definir valor mensal
- ✅ **Contribuições Recorrentes**: Automação de pagamentos
- ✅ **Histórico Completo**: Rastreamento de todas as transações
- ✅ **Relatórios Transparentes**: Visualização de impacto
- ✅ **Simulação de Custos**: Cálculo de despesas

### 🏥 Controle de Custos e Campanhas
- ✅ **Registrar Custos**: Despesas por animal/ONG
- ✅ **Campanhas de Arrecadação**: Criação e gestão
- ✅ **Doações Único**: Suporte a contribuições pontuais
- ✅ **Selos de Reconhecimento**: Gamificação de contribuições

### 💬 Comunicação em Tempo Real
- ✅ **Chat WebSocket**: Mensagens instantâneas
- ✅ **Histórico Persistente**: Armazenamento de conversas
- ✅ **Notificações Push**: Alertas de novas mensagens
- ✅ **Múltiplos Canais**: Mensagens privadas e de grupo

### 👥 Gerenciamento de Usuários
- ✅ **Padrinho (Godfather)**: Doadores e patrocinadores
- ✅ **ONG (Organization)**: Organizações sem fins lucrativos
- ✅ **Voluntário**: Equipes de suporte
- ✅ **Admin**: Administradores do sistema
- ✅ **Perfil Completo**: Dados pessoais e preferências

### 🎖️ Sistema de Gamificação
- ✅ **Conquistas**: Badges por ações relevantes
- ✅ **Pontuação**: Sistema de pontos
- ✅ **Milestones**: Celebração de marcos
- ✅ **Ranking**: Leaderboard de padrinhos
- ✅ **Selos Especiais**: Reconhecimento visual

---

## 🛠️ Stack Tecnológico

### Backend

```json
{
  "Framework": "Spring Boot 3.5.6",
  "Linguagem": "Java 25",
  "Build Tool": "Maven",
  "Banco de Dados": "PostgreSQL 15+",
  "ORM": "Hibernate JPA",
  "Migrations": "Flyway",
  "Autenticação": "Spring Security + JWT",
  "Comunicação Real-Time": "Spring WebSocket + STOMP",
  "Email": "Spring Mail",
  "Validação": "Hibernate Validator",
  "Utilitários": "Lombok",
  "Testes": "JUnit 5 + Spring Security Test"
}
```

### Dependências Principais

```xml
<!-- Spring Boot Starters -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-websocket
spring-boot-starter-validation
spring-boot-starter-mail
spring-boot-starter-thymeleaf

<!-- Database -->
postgresql
flyway-core
flyway-database-postgresql

<!-- JWT & Security -->
java-jwt (Auth0)
spring-security

<!-- Utilities -->
lombok
hibernate-validator
```

---

## 📦 Instalação e Setup

### Pré-requisitos

- **Java 25** ou superior
- **Maven 3.9+**
- **PostgreSQL 15+**
- **Git**

### Passos de Instalação

#### 1. Clone o repositório

```bash
git clone https://github.com/raynan-silva/padrinho-digital-api.git
cd padrinho-digital-api
```

#### 2. Configure o Banco de Dados

Crie um banco PostgreSQL:

```sql
CREATE DATABASE padrinho_digital;
```

#### 3. Configure as Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto ou configure variáveis do sistema:

```bash
# Banco de Dados
DB_URL=jdbc:postgresql://localhost:5432/padrinho_digital
DB_USER=postgres
DB_PASSWORD=sua_senha

# JWT & Segurança
JWT_SECRET=sua_chave_secreta_super_segura_aqui

# Email (Configurar com seu provedor)
MAIL_HOST=smtp.seuprovedador.com
MAIL_PORT=587
MAIL_USER=seu_email@example.com
MAIL_PASS=sua_senha_de_app

# URLs do Frontend
FRONTEND_RESET_URL=http://localhost:5173/change-password
FRONTEND_LOGIN_URL=http://localhost:5173/login
```

Ou configure no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/padrinho_digital
spring.datasource.username=postgres
spring.datasource.password=sua_senha

api.security.token.secret=sua_chave_secreta_super_segura_aqui

spring.mail.host=smtp.seuprovededor.com
spring.mail.port=587
spring.mail.username=seu_email@example.com
spring.mail.password=sua_senha_de_app

app.frontend.reset-url=http://localhost:5173/change-password
app.frontend.login-url=http://localhost:5173/login
```

#### 4. Instale as Dependências

```bash
mvn clean install
```

O Maven baixará automaticamente todas as dependências do `pom.xml`.

#### 5. Execute as Migrations

As migrations Flyway executarão automaticamente ao iniciar a aplicação:

```bash
mvn spring-boot:run
```

Ou compile e execute o JAR:

```bash
mvn package
java -jar target/padrinho-digital-api-0.0.1-SNAPSHOT.jar
```

#### 6. Verifique se está funcionando

A API estará disponível em: `http://localhost:8080`

Verifique a saúde:

```bash
curl http://localhost:8080/api/health
```

---

## 📁 Estrutura do Projeto

```
src/main/java/com/dnnr/padrinho_digital_api/
├── controllers/                      # Endpoints REST
│   ├── admin/                        # Endpoints administrativos
│   ├── auth/                         # Autenticação e autorização
│   ├── chat/                         # Mensagens em tempo real
│   ├── cost/                         # Gerenciamento de custos
│   ├── donation_campaign/            # Campanhas de doação
│   ├── gamification/                 # Sistema de pontos e conquistas
│   ├── godfather/                    # Gerenciamento de padrinhos
│   ├── history/                      # Histórico de transações
│   ├── ong/                          # Gerenciamento de ONGs
│   ├── pet/                          # Gerenciamento de animais
│   ├── profile/                      # Perfil do usuário
│   ├── sponsorship/                  # Sistema de apadrinhamento
│   ├── user/                         # Gerenciamento de usuários
│   └── volunteer/                    # Gerenciamento de voluntários
│
├── services/                         # Lógica de negócio
│   ├── admin/                        # Serviços administrativos
│   ├── auth/                         # Autenticação e segurança
│   ├── chat/                         # Processamento de mensagens
│   ├── cost/                         # Cálculo de despesas
│   ├── donation_campaign/            # Lógica de campanhas
│   ├── gamification/                 # Cálculo de pontos/badges
│   ├── godfather/                    # Serviços de padrinhos
│   ├── history/                      # Processamento de histórico
│   ├── mail/                         # Envio de emails
│   ├── ong/                          # Serviços de ONGs
│   ├── pet/                          # Serviços de animais
│   ├── sponsorship/                  # Lógica de apadrinhamento
│   ├── user/                         # Gerenciamento de usuários
│   ├── volunteer/                    # Serviços de voluntários
│   └── mappers/                      # Conversão DTO ↔ Entity
│
├── entities/                         # Modelos de Dados (JPA)
│   ├── chat/                         # Entidade ChatMessage
│   ├── donation_campaign/            # Entidade DonationCampaign
│   ├── godfather/                    # Entidade Godfather
│   ├── ong/                          # Entidade Organization
│   ├── pet/                          # Entidade Pet
│   ├── photo/                        # Entidade Photo
│   ├── sponsorship/                  # Entidade Sponsorship
│   └── users/                        # Entidade User
│
├── dtos/                             # Data Transfer Objects
│   ├── auth/                         # DTOs de autenticação
│   ├── chat/                         # DTOs de mensagens
│   ├── cost/                         # DTOs de custos
│   ├── donation_campaign/            # DTOs de campanhas
│   ├── gamification/                 # DTOs de gamificação
│   ├── godfather/                    # DTOs de padrinhos
│   ├── history/                      # DTOs de histórico
│   ├── ong/                          # DTOs de ONGs
│   ├── pet/                          # DTOs de animais
│   ├── photo/                        # DTOs de fotos
│   ├── report/                       # DTOs de relatórios
│   ├── sponsorship/                  # DTOs de apadrinhamento
│   ├── user/                         # DTOs de usuários
│   └── volunteer/                    # DTOs de voluntários
│
├── repositories/                     # Data Access Layer (JPA)
│   ├── chat/
│   ├── donation_campaign/
│   ├── godfather/
│   ├── history/
│   ├── ong/
│   ├── pet/
│   ├── sponsorship/
│   ├── user/
│   └── volunteer/
│
├── config/                           # Configurações da Aplicação
│   ├── SecurityConfig.java           # Spring Security
│   ├── WebSocketConfig.java          # WebSocket + STOMP
│   ├── CorsConfig.java               # CORS
│   └── ...
│
├── infra/                            # Infraestrutura
│   ├── security/                     # Filtros e componentes de segurança
│   │   ├── SecurityFilter.java       # Filtro JWT
│   │   ├── SecurityConfiguration.java
│   │   └── ...
│   └── handler/                      # Manipuladores de exceções
│       ├── RestExceptionHandler.java
│       └── ...
│
├── exceptions/                       # Exceções Customizadas
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   ├── UnauthorizedException.java
│   └── ...
│
├── scheduler/                        # Tarefas Agendadas
│   ├── PaymentScheduler.java         # Agendamento de cobranças
│   ├── NotificationScheduler.java    # Envio de notificações
│   └── ...
│
├── resources/
│   ├── application.properties         # Configurações principais
│   ├── db/migration/                  # Scripts Flyway
│   │   ├── V1__create_table_users.sql
│   │   ├── V2__seed_admin_user.sql
│   │   ├── V3__create_table_ong.sql
│   │   ├── ...
│   │   └── V14__chat_message.sql
│   └── templates/
│       └── password-reset-template.html
│
└── PadrinhoDigitalApiApplication.java # Classe principal
```

---

## 🔐 Sistema de Autenticação e Segurança

### Fluxo de Autenticação

```
1. Usuário faz POST /api/auth/login com email e senha
   ↓
2. API valida credenciais no banco
   ↓
3. Se válido: JWT Token é gerado e retornado
   ↓
4. Cliente armazena token (localStorage/sessionStorage)
   ↓
5. Requisições subsequentes incluem: Authorization: Bearer {token}
   ↓
6. SecurityFilter valida token em cada requisição
   ↓
7. Se válido: Usuário acessa recurso protegido
   Se inválido: 401 Unauthorized
```

### Tipos de Usuários e Permissões

| Tipo | Permissões | Endpoints |
|------|-----------|-----------|
| **PADRINHO** | Apadrinhar, fazer doações, ver relatórios, chat | `/api/godfather/*`, `/api/sponsorship/*` |
| **GERENTE_ONG** | Gerenciar animais, voluntários, publicar relatórios | `/api/ong/*`, `/api/pet/*`, `/api/volunteer/*` |
| **VOLUNTARIO** | Ver animais, registrar atividades | `/api/pet/*` (read-only), `/api/volunteer/*` |
| **ADMIN** | Gerenciar tudo, configurações globais, relatórios | `/api/admin/*` |

### Endpoints de Autenticação

```bash
# Login
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "senha123"
}

# Registrar Padrinho
POST /api/auth/register/godfather
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "cpf": "123.456.789-10"
}

# Registrar ONG
POST /api/auth/register/ong
{
  "name": "ONG Protetora de Pets",
  "email": "contato@ong.com",
  "cnpj": "12.345.678/0001-90",
  "address": "Rua A, 123"
}

# Recuperar Senha
POST /api/auth/forgot-password
{
  "email": "user@example.com"
}

# Resetar Senha
POST /api/auth/reset-password
{
  "token": "abc123xyz",
  "newPassword": "novaSenha123"
}

# Logout
POST /api/auth/logout
```

### Segurança Implementada

- ✅ **JWT (JSON Web Tokens)**: Autenticação stateless
- ✅ **Spring Security**: Framework robusto de segurança
- ✅ **CORS**: Configurado para aceitar frontend
- ✅ **Password Hashing**: Bcrypt para senhas
- ✅ **Token Expiration**: Expiração automática de tokens
- ✅ **Refresh Tokens**: Renovação sem novo login
- ✅ **HTTPS Ready**: Suporte a SSL/TLS

---

## 💬 Sistema de Comunicação em Tempo Real

### Tecnologias

- **Spring WebSocket**: Suporte nativo a WebSocket
- **STOMP**: Protocolo para mensagens estruturadas
- **SockJS**: Fallback para navegadores antigos

### Fluxo de Mensagens

```
Cliente (Frontend)
       ↓
   WebSocket
       ↓
  STOMP Handler
       ↓
  ChatService
       ↓
  ChatRepository
       ↓
  PostgreSQL (Histórico)
       ↓
  Broadcast para receptor
```

### Endpoints WebSocket

```
# Conectar
ws://localhost:8080/ws

# Tópicos de Assinatura
/user/{userId}/queue/messages      # Mensagens privadas
/topic/ong/{ongId}/messages        # Grupo da ONG
/topic/notifications               # Notificações globais

# Enviar Mensagens
/app/chat.send                      # Enviar mensagem
/app/chat.history/{recipientId}    # Solicitar histórico
```

### DTOs de Chat

```typescript
// Enviar Mensagem
{
  "senderId": "user-123",
  "recipientId": "user-456",
  "content": "Olá! Como vai?",
  "timestamp": "2024-01-15T10:30:00Z"
}

// Receber Mensagem
{
  "id": "msg-123",
  "senderName": "João Silva",
  "recipientId": "user-456",
  "content": "Olá! Tudo bem?",
  "timestamp": "2024-01-15T10:30:00Z",
  "read": false
}
```

---

## 💰 Sistema de Contribuições e Apadrinhamento

### Fluxo de Apadrinhamento

```
Padrinho escolhe Pet
       ↓
   Define valor mensal
       ↓
   Cria Sponsorship (status: PENDING_PAYMENT)
       ↓
   ProcessaPagamento (integração futura com gateway)
       ↓
   Scheduler marca status: ACTIVE
       ↓
   Padrinho recebe relatórios mensais
       ↓
   Chat aberto com ONG
```

### Endpoints Principais

```bash
# Criar Apadrinhamento
POST /api/sponsorship
{
  "petId": "pet-123",
  "ongId": "ong-456",
  "monthlyValue": 50.00,
  "startDate": "2024-01-01"
}

# Listar Apadrinhamentos do Padrinho
GET /api/sponsorship/my

# Visualizar Detalhes
GET /api/sponsorship/{sponsorshipId}

# Cancelar Apadrinhamento
PUT /api/sponsorship/{sponsorshipId}/cancel

# Histórico de Pagamentos
GET /api/sponsorship/{sponsorshipId}/history

# Doação Única
POST /api/donation
{
  "sponsorshipId": "sponsorship-123",
  "value": 100.00,
  "message": "Contribuição extra para o Fluffy"
}
```

---

## 📊 Endpoints de Relatórios e Análises

### Relatórios de Custos

```bash
# Custos por Animal
GET /api/cost/pet/{petId}

# Custos por ONG (período)
GET /api/cost/ong/{ongId}?startDate=2024-01-01&endDate=2024-01-31

# Relatório de Transparência
GET /api/reports/transparency/{sponsorshipId}

# Dashboard Admin
GET /api/admin/dashboard
```

### Resposta de Relatório

```json
{
  "sponsorshipId": "sponsorship-123",
  "petName": "Fluffy",
  "ongName": "ONG Protetora",
  "totalContributed": 500.00,
  "expenses": [
    {
      "description": "Ração Premium",
      "amount": 150.00,
      "date": "2024-01-05",
      "category": "ALIMENTACAO"
    },
    {
      "description": "Consulta Veterinária",
      "amount": 200.00,
      "date": "2024-01-10",
      "category": "SAUDE"
    }
  ],
  "totalExpenses": 350.00,
  "balance": 150.00
}
```

---

## 🎖️ Sistema de Gamificação

### Estrutura de Pontos

```bash
# Conquistas Disponíveis
- Primeiro Apadrinhamento: 100 pontos
- Apadrinhar 5 Animais: 250 pontos
- Contribuição Milionária: 500 pontos
- Chat com ONG: 25 pontos/mês
- Adoção Bem-sucedida: 300 pontos

# Endpoints
GET /api/gamification/achievements        # Listar conquistas
GET /api/gamification/my-achievements    # Minhas conquistas
GET /api/gamification/leaderboard        # Ranking
GET /api/gamification/points/{userId}    # Meus pontos
```

### Resposta de Gamificação

```json
{
  "userId": "user-123",
  "totalPoints": 1250,
  "level": "Protetor Platina",
  "achievements": [
    {
      "id": "achievement-1",
      "title": "Primeiro Apadrinhamento",
      "description": "Você adotou seu primeiro animal!",
      "points": 100,
      "unlockedAt": "2024-01-15"
    }
  ],
  "rank": 5,
  "nextLevel": "Herói dos Animais"
}
```

---

## 📱 API REST - Principais Endpoints

### Autenticação

```
POST   /api/auth/login
POST   /api/auth/register/godfather
POST   /api/auth/register/ong
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
POST   /api/auth/logout
```

### Usuários

```
GET    /api/users/profile
PUT    /api/users/profile
GET    /api/users/{userId}
DELETE /api/users/{userId}
```

### Animais (Pets)

```
GET    /api/pet                    # Listar todos
POST   /api/pet                    # Criar (ONG)
GET    /api/pet/{petId}            # Detalhes
PUT    /api/pet/{petId}            # Editar (ONG)
DELETE /api/pet/{petId}            # Deletar (ONG)
POST   /api/pet/{petId}/photo      # Upload foto
GET    /api/pet/{petId}/history    # Histórico de saúde
```

### Apadrinhamento

```
POST   /api/sponsorship            # Criar apadrinhamento
GET    /api/sponsorship            # Listar
GET    /api/sponsorship/{id}       # Detalhes
PUT    /api/sponsorship/{id}       # Editar
DELETE /api/sponsorship/{id}       # Cancelar
GET    /api/sponsorship/{id}/history # Histórico de pagamentos
```

### Campanhas

```
GET    /api/donation-campaign      # Listar campanhas
POST   /api/donation-campaign      # Criar (ONG)
GET    /api/donation-campaign/{id} # Detalhes
POST   /api/donation               # Fazer doação
```

### Chat

```
GET    /api/chat/conversations     # Minhas conversas
GET    /api/chat/{conversationId}  # Histórico
POST   /api/chat/{recipientId}     # Iniciar conversa
WebSocket /ws                       # Conexão em tempo real
```

### ONGs

```
GET    /api/ong                    # Listar ONGs
POST   /api/ong                    # Criar (Admin)
GET    /api/ong/{ongId}            # Detalhes
PUT    /api/ong/{ongId}            # Editar (Gerente)
GET    /api/ong/{ongId}/dashboard  # Dashboard ONG
```

### Administração

```
GET    /api/admin/users            # Listar usuários
GET    /api/admin/ongs             # Listar ONGs
GET    /api/admin/dashboard        # Dashboard
GET    /api/admin/reports          # Relatórios
POST   /api/admin/fees             # Configurar taxas
```

---

## ⚙️ Configuração Avançada

### Application.properties Completo

```properties
# ========== SERVIDOR ==========
server.port=8080
server.servlet.context-path=/api

# ========== DATASOURCE ==========
spring.datasource.url=jdbc:postgresql://localhost:5432/padrinho_digital
spring.datasource.username=postgres
spring.datasource.password=12345678
spring.datasource.driver-class-name=org.postgresql.Driver

# ========== JPA/HIBERNATE ==========
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# ========== FLYWAY ==========
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.validate-on-migrate=true

# ========== JWT ==========
api.security.token.secret=my-secret-key-change-in-production
api.security.token.expiration=86400000  # 24 horas em ms

# ========== EMAIL ==========
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# ========== FRONTEND ==========
app.frontend.reset-url=http://localhost:5173/change-password
app.frontend.login-url=http://localhost:5173/login

# ========== LOGGING ==========
logging.level.root=INFO
logging.level.com.dnnr.padrinho_digital_api=DEBUG
logging.level.org.springframework.security=DEBUG

# ========== CORS ==========
app.cors.allowed-origins=http://localhost:5173,https://padrinho-digital.com
app.cors.allowed-methods=GET,POST,PUT,DELETE
app.cors.allowed-headers=*
app.cors.allow-credentials=true
```

---

## 🚀 Build e Deploy

### Build Local

```bash
# Compilar projeto
mvn clean install

# Executar com Maven
mvn spring-boot:run

# Criar JAR executável
mvn package

# Executar JAR
java -jar target/padrinho-digital-api-0.0.1-SNAPSHOT.jar
```

### Deploy em Produção

#### Docker

```dockerfile
FROM openjdk:25-slim
COPY target/padrinho-digital-api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
docker build -t padrinho-digital-api .
docker run -p 8080:8080 padrinho-digital-api
```

#### Heroku

```bash
heroku create padrinho-digital-api
heroku addons:create heroku-postgresql:hobby-dev
git push heroku main
```

#### AWS

- **EC2**: Deploy JAR em instância
- **RDS**: PostgreSQL gerenciado
- **S3**: Armazenamento de fotos
- **SES**: Serviço de email

---

## 🗄️ Banco de Dados

### Migrations Flyway

Todas as migrations estão em `src/main/resources/db/migration/`:

| Versão | Descrição |
|--------|-----------|
| V1 | Criar tabela users |
| V2 | Seed admin user |
| V3 | Criar tabela ong |
| V4 | Password recovery |
| V5 | Criar tabela pet |
| V6 | Criar tabela volunteer |
| V7 | Criar tabela admin |
| V8 | Criar tabela photo |
| V9 | Criar tabela sponsorship |
| V10 | Criar tabela cost |
| V11 | Criar tabela donation |
| V12 | Criar tabela seal (gamificação) |
| V13 | Update status sponsorship_history |
| V14 | Chat message |

### Diagrama de Entidades

```
Users (base)
├── Godfather (Padrinho)
├── Volunteer (Voluntário)
├── ONG (Organization)
└── Admin

ONG
├── Pet (vários)
│   ├── Photo (múltiplas)
│   └── Sponsorship (múltiplos padrinhos)
│       ├── Cost
│       ├── Donation
│       └── History
│
└── DonationCampaign

Chat
├── ChatMessage (histórico)

Gamification
├── Achievement
└── Seal (Selos)
```

---

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
mvn test

# Teste específico
mvn test -Dtest=UserServiceTest

# Com cobertura
mvn test jacoco:report
```

### Exemplos de Teste

```java
@SpringBootTest
@AutoConfigureMockMvc
class SponsorshipServiceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private SponsorshipService sponsorshipService;
    
    @Test
    void testCreateSponsorship() throws Exception {
        // Arrange
        SponsorshipDTO dto = new SponsorshipDTO(...);
        
        // Act
        mockMvc.perform(post("/api/sponsorship")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        
        // Assert
            .andExpect(status().isCreated());
    }
}
```

---

## 🔧 Troubleshooting

### Erro: Conexão com Banco de Dados Recusada

**Solução:**
```bash
# Verificar se PostgreSQL está rodando
# Windows: Services
# Linux: sudo systemctl status postgresql
# Mac: brew services list

# Verificar credenciais em application.properties
# Recriar banco de dados se necessário
```

### Erro: JWT Token Expirado

**Solução:**
- Aumentar expiração em `application.properties`
- Implementar refresh token (já disponível)
- Fazer novo login

### Erro: CORS Bloqueado

**Solução:**
```properties
# Configurar URLs permitidas em application.properties
app.cors.allowed-origins=http://localhost:5173
```

### Erro: Email não Envia

**Solução:**
- Verificar credenciais de email em `application.properties`
- Usar "Senha de Aplicação" se usar Gmail
- Ativar "Menos segurança" ou "SMTP" se necessário

### Erro: WebSocket não Conecta

**Solução:**
- Verificar se proxy/firewall bloqueia WebSocket
- Verificar URL do WebSocket no frontend
- SockJS fazer fallback automaticamente

---

## 📚 Documentação Adicional

### API Documentation

- Swagger/Springdoc OpenAPI (pode ser adicionado)
- Postman Collection disponível
- Documentação de DTOs em cada controller

### Padrões de Código

- **Naming**: camelCase para variáveis, PascalCase para classes
- **Architecture**: MVC (Model-View-Controller) com DTOs
- **Error Handling**: Custom exceptions com mensagens claras
- **Logging**: Usando SLF4J (padrão Spring)

---

## 🔐 Boas Práticas de Segurança

✅ **Implementadas:**
- Autenticação JWT robusta
- Senhas com hash Bcrypt
- CORS configurado
- Validação de entrada
- Rate limiting (recomendado)
- HTTPS em produção

⚠️ **Recomendações:**
- Usar HTTPS sempre em produção
- Armazenar dados sensíveis em variáveis de ambiente
- Fazer backup regular do banco de dados
- Implementar CAPTCHA para registro
- Adicionar rate limiting
- Fazer logs de segurança

---

## 📈 Performance e Escalabilidade

### Otimizações Implementadas

- Lazy loading em relacionamentos JPA
- Query optimization com projections
- Paginação em listagens
- Cache com Spring Cache (extensível)
- Connection pooling com HikariCP
- Índices no banco de dados

### Recomendações para Escala

- Redis para cache distribuído
- Kafka para processamento assíncrono
- Load balancing com Nginx
- CDN para fotos/assets
- Microserviços para domínios críticos

---

## 🎓 Tecnologias Aprendidas e Aplicadas

- ✅ **Spring Boot 3**: Framework robusto Java
- ✅ **Spring Security**: Autenticação e autorização
- ✅ **Spring Data JPA**: ORM e persistência
- ✅ **Spring WebSocket**: Comunicação em tempo real
- ✅ **JWT**: Autenticação stateless
- ✅ **PostgreSQL**: Banco relacional robusto
- ✅ **Flyway**: Versionamento de banco
- ✅ **Lombok**: Redução de boilerplate
- ✅ **Hibernate Validator**: Validação de dados
- ✅ **Spring Mail**: Envio de emails
- ✅ **REST API Design**: Endpoints bem estruturados
- ✅ **Error Handling**: Tratamento robusto de erros
- ✅ **Testing**: JUnit 5 e Spring Test

---

## 🚦 Status do Projeto

- ✅ Autenticação e Autorização
- ✅ CRUD de Usuários
- ✅ Sistema de Apadrinhamento
- ✅ Chat em Tempo Real
- ✅ Gerenciamento de Custos
- ✅ Sistema de Gamificação
- ✅ Email de Recuperação de Senha
- ⏳ Integração com Gateway de Pagamento
- ⏳ Notificações Push
- ⏳ API de Relatórios Avançados

---

## 👥 Contribuidores

Projeto desenvolvido por alunos da **Universidade de Vila Velha**.

---

## 📝 Licença

Este projeto é de código aberto e está disponível sob a licença MIT.

---

## 🤝 Como Contribuir

1. Faça um Fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 💬 Suporte e Contato

Para dúvidas, sugestões ou reportar bugs:

- Abra uma [Issue](https://github.com/raynan-silva/padrinho-digital-api/issues)
- Entre em contato com a equipe de desenvolvimento

---

## 🎓 Contexto Acadêmico

Este projeto foi desenvolvido como trabalho acadêmico da **Universidade de Vila Velha**, explorando:

- **Engenharia de Software**: Padrões de design e arquitetura
- **Banco de Dados**: Design relacional e otimização
- **Segurança da Informação**: Autenticação, criptografia, validação
- **Desenvolvimento Backend**: Java, Spring, REST APIs
- **DevOps**: Deploy, CI/CD, containerização
- **Gestão de Projeto**: Agile, versionamento com Git

### Requisitos Não-Funcionais Implementados

✅ **Segurança**: Autenticação robusta, dados sensíveis protegidos, validação de entrada  
✅ **Performance**: Queries otimizadas, paginação, lazy loading  
✅ **Escalabilidade**: Arquitetura pronta para crescimento, preparada para microsserviços  
✅ **Usabilidade**: Mensagens de erro claras, validações intuitivas  
✅ **Manutenibilidade**: Código limpo, bem estruturado, documentado  
✅ **Disponibilidade**: Tratamento de exceções, fallbacks, resiliência  

---

**Desenvolvido com ❤️ para conectar corações generosos a animais que precisam de amor**

