# wallet-service

O `wallet-service` é um microsserviço do sistema **AmigoPay**, responsável por gerenciar a **carteira de saldo dos usuários**, bem como **validar e executar transferências** a partir de eventos Kafka.

---

## Responsabilidades

- Criar carteira para novos usuários (evento `user.created`)
- Executar transferências quando um pagamento é iniciado (evento `payment.initiated`)
- Validar saldo disponível
- Emitir eventos de sucesso ou rejeição:
    - `payment.done`
    - `payment.rejected` (quando o saldo for insuficiente)

---

## Estrutura de Pastas

```markdown
wallet-service/
├── common/                     # Componentes utilitários reutilizáveis (ex: mensagens, constantes, utils, etc.)
├── config/                     # Configurações específicas do microserviço (ex: beans, CORS, Swagger, etc.)
├── exception/                  # Definição de exceções personalizadas e tratadores globais
├── messaging/
│   ├── config/                 # Configuração do Kafka (producer, consumer factory, propriedades, etc.)
│   ├── consumer/              
│   │   ├── handle/             # Consumers dos tópicos Kafka, com lógica de delegação para handlers
│   ├── event/                  # Modelos dos eventos Kafka utilizados no serviço (ex: PaymentInitiatedEvent)
│   ├── producer/              
│   │   ├── impl/               # Implementações dos produtores Kafka, responsáveis por emitir eventos
├── wallet/
│   ├── controller/             # Controladores REST (caso existam endpoints públicos relacionados à carteira)
│   ├── dto/                    # Objetos de transferência de dados para comunicação externa e interna
│   ├── entity/                 # Entidades JPA persistidas no banco de dados (ex: Wallet)
│   ├── enums/                  # Enumerações relacionadas ao domínio de carteira
│   ├── mapper/                 # Conversão entre entidades, DTOs e eventos
│   ├── repository/             # Interfaces de repositório Spring Data JPA
│   ├── service/                
│   │   ├── impl/               # Implementações da camada de serviço (lógica de negócio)
│   ├── validation/             # Validadores de negócio (ex: validação de saldo, existência de carteira, etc.)
└── test/                       # Testes unitários e de integração
```

---

## Eventos Kafka

### Eventos Consumidos

| Tópico              | Origem            | Ação                                      |
| ------------------- | ----------------- | ----------------------------------------- |
| `user.created`      | `user-service`    | Cria nova carteira para o usuário         |
| `payment.initiated` | `payment-service` | Valida e executa a transferência de saldo |

### Eventos Emitidos

| Tópico             | Destino(s)                                                | Conteúdo                            |
| ------------------ | --------------------------------------------------------- | ----------------------------------- |
| `payment.done`     | `notification-service`, `feed-service`, `payment-service` | Informações da transação finalizada |
| `payment.rejected` | `payment-service`                                         | Informações sobre rejeição          |

---

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.x
- Kafka
- PostgreSQL
- Docker & Docker Compose
- JPA (Hibernate)
- JUnit & Mockito

---

## Banco de Dados

- Cada carteira é associada a um único userId (UUID)
- A carteira contém o balance (saldo atual)
- Transações de débito são atômicas e validadas contra o saldo disponíve

---

## Princípios e Boas Práticas

- Arquitetura orientada a eventos com Kafka
- Separação de responsabilidades (SRP + SOLID)
- Design limpo e modular
-  unitários nas camadas de serviço e validação
- Desacoplamento entre domínios usando eventos
- Validação explícita de regras de negócio antes da persistência

---

## Autor

Desenvolvido por **Eduardo Sartori** — *Software Developer*

- Especialista em: Java, Spring Boot, Microservices, Kafka, SQL Server, PostgreSQL
- Em constante evolução, com foco em arquitetura distribuída e boas práticas profissionais
- Contato: [linkedin.com/in/eduardosartori](https://www.linkedin.com/in/oeduardosartori)

---