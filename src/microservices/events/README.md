# Events Service

Микросервис для обработки событий через Apache Kafka для системы CinemaAbyss.

## Функциональность

- **Producer (Продюсер)**: Публикация событий в Kafka топики
- **Consumer (Консьюмер)**: Чтение и обработка событий из Kafka топиков
- **REST API**: HTTP endpoints для создания событий
- **Три типа событий**:
  - Movie Events (события фильмов)
  - User Events (события пользователей)
  - Payment Events (события платежей)

## Технологии

- Java 17
- Spring Boot 3.2.0
- Spring Kafka
- Apache Kafka
- Maven

## API Endpoints

### Health Check

```bash
GET /api/events/health
```

Response:
```json
{
  "status": true
}
```

### Movie Events

```bash
POST /api/events/movie
Content-Type: application/json

{
  "movie_id": 1,
  "title": "Inception",
  "action": "viewed",
  "user_id": 123,
  "rating": 8.8,
  "genres": ["Sci-Fi", "Thriller"],
  "description": "A mind-bending thriller"
}
```

Response:
```json
{
  "status": "success",
  "partition": 0,
  "offset": 42,
  "event": { ... }
}
```

### User Events

```bash
POST /api/events/user
Content-Type: application/json

{
  "user_id": 123,
  "username": "john_doe",
  "email": "john@example.com",
  "action": "registered",
  "timestamp": "2023-12-01T14:30:00Z"
}
```

### Payment Events

```bash
POST /api/events/payment
Content-Type: application/json

{
  "payment_id": 1,
  "user_id": 123,
  "amount": 9.99,
  "status": "completed",
  "timestamp": "2023-12-01T14:30:00Z",
  "method_type": "credit_card"
}
```

## Kafka Топики

- `movie-events` - события фильмов
- `user-events` - события пользователей
- `payment-events` - события платежей

## Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `PORT` | Порт сервиса | 8082 |
| `KAFKA_BROKERS` | Адрес Kafka брокера | localhost:9092 |

## Запуск локально

### Предварительные требования

- JDK 17+
- Maven 3.6+
- Apache Kafka (запущен)

### Сборка

```bash
mvn clean package
```

### Запуск

```bash
java -jar target/events-service-1.0.0.jar
```

Или с помощью Maven:

```bash
mvn spring-boot:run
```

## Запуск с Docker

### Сборка образа

```bash
docker build -t events-service .
```

### Запуск контейнера

```bash
docker run -p 8082:8082 \
  -e KAFKA_BROKERS=kafka:9092 \
  events-service
```

## Запуск с Docker Compose

```bash
docker-compose up -d events-service
```

## Примеры использования

### 1. Отправка события просмотра фильма

```bash
curl -X POST http://localhost:8082/api/events/movie \
  -H "Content-Type: application/json" \
  -d '{
    "movie_id": 1,
    "title": "Inception",
    "action": "viewed",
    "user_id": 123
  }'
```

### 2. Отправка события регистрации пользователя

```bash
curl -X POST http://localhost:8082/api/events/user \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": 123,
    "username": "john_doe",
    "email": "john@example.com",
    "action": "registered",
    "timestamp": "2023-12-01T14:30:00Z"
  }'
```

### 3. Отправка события платежа

```bash
curl -X POST http://localhost:8082/api/events/payment \
  -H "Content-Type: application/json" \
  -d '{
    "payment_id": 1,
    "user_id": 123,
    "amount": 9.99,
    "status": "completed",
    "timestamp": "2023-12-01T14:30:00Z"
  }'
```

## Логирование

Сервис логирует все входящие запросы и обработанные события:

```
INFO  Получен запрос на создание события фильма: MovieEvent(...)
INFO  Отправка события фильма в Kafka: {...}
INFO  Событие фильма успешно отправлено в партицию 0 с offset 42
INFO  ========================================
INFO  Получено событие фильма из Kafka:
INFO  Топик: movie-events
INFO  Сообщение: {...}
INFO  ========================================
```

## Мониторинг

### Просмотр логов

```bash
# Docker
docker logs -f cinemaabyss-events-service

# Docker Compose
docker-compose logs -f events-service
```

### Kafka UI

Откройте http://localhost:8090 для просмотра:
- Топиков Kafka
- Сообщений в топиках
- Consumer groups
- Статистики

## Архитектура

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ HTTP POST /api/events/movie
       ▼
┌─────────────────────────┐
│   EventController       │
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│ EventProducerService    │◄──────┐
└───────────┬─────────────┘       │
            │                     │
            ▼                     │
┌─────────────────────────┐       │
│   Apache Kafka          │       │
│   (movie-events topic)  │       │
└───────────┬─────────────┘       │
            │                     │
            ▼                     │
┌─────────────────────────┐       │
│ EventConsumerService    │───────┘
│ (Logs to console)       │
└─────────────────────────┘
```

## Как работает MVP

1. **Клиент отправляет POST запрос** с событием на `/api/events/movie`, `/api/events/user` или `/api/events/payment`
2. **EventController принимает запрос** и валидирует данные
3. **EventProducerService публикует событие** в соответствующий Kafka топик
4. **Kafka сохраняет событие** и возвращает metadata (partition, offset)
5. **EventConsumerService читает событие** из того же топика
6. **Событие логируется в консоль** для проверки работы системы

Это MVP демонстрирует полный цикл работы с Kafka: Producer → Kafka → Consumer.

## Дальнейшее развитие

В production-версии Consumer может:
- Сохранять события в БД для аналитики
- Отправлять уведомления пользователям
- Обновлять рекомендательную систему
- Генерировать отчеты и метрики
- Интегрироваться с другими сервисами
