# Proxy Service (API Gateway)

API Gateway для системы CinemaAbyss, реализующий паттерн Strangler Fig для постепенной миграции от монолита к микросервисам.

## Функциональность

- **Единая точка входа** для всех API запросов
- **Strangler Fig Pattern** - постепенная миграция трафика с монолита на микросервисы
- **Feature Flag** - управление процентом трафика, направляемого в микросервисы
- **Маршрутизация запросов**:
  - `/api/movies/**` - маршрутизируется между монолитом и movies-service
  - `/api/users/**` - маршрутизируется в монолит
  - `/api/payments/**` - маршрутизируется в монолит
  - `/api/subscriptions/**` - маршрутизируется в монолит
  - `/api/events/**` - маршрутизируется в events-service

## Технологии

- Java 17
- Spring Boot 3.2.0
- Spring WebFlux (WebClient для HTTP запросов)
- Spring Boot Actuator (health checks)
- Maven

## Переменные окружения

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `PORT` | Порт сервиса | 8000 |
| `MONOLITH_URL` | URL монолита | http://localhost:8080 |
| `MOVIES_SERVICE_URL` | URL movies сервиса | http://localhost:8081 |
| `EVENTS_SERVICE_URL` | URL events сервиса | http://localhost:8082 |
| `GRADUAL_MIGRATION` | Включить постепенную миграцию | true |
| `MOVIES_MIGRATION_PERCENT` | Процент трафика в movies-service | 50 |

## Запуск локально

### Предварительные требования

- JDK 17+
- Maven 3.6+

### Сборка

```bash
mvn clean package
```

### Запуск

```bash
java -jar target/proxy-service-1.0.0.jar
```

Или с помощью Maven:

```bash
mvn spring-boot:run
```

## Запуск с Docker

### Сборка образа

```bash
docker build -t proxy-service .
```

### Запуск контейнера

```bash
docker run -p 8000:8000 \
  -e MONOLITH_URL=http://monolith:8080 \
  -e MOVIES_SERVICE_URL=http://movies-service:8081 \
  -e EVENTS_SERVICE_URL=http://events-service:8082 \
  -e GRADUAL_MIGRATION=true \
  -e MOVIES_MIGRATION_PERCENT=50 \
  proxy-service
```

## Запуск с Docker Compose

```bash
docker-compose up -d proxy-service
```

## API Endpoints

### Health Check

```bash
GET /health
```

Response:
```json
{
  "status": true
}
```

### Movies API

```bash
# Получить все фильмы
GET /api/movies

# Получить фильм по ID
GET /api/movies?id=1

# Создать фильм
POST /api/movies
Content-Type: application/json

{
  "title": "Inception",
  "description": "A mind-bending thriller",
  "rating": 8.8,
  "genres": ["Sci-Fi", "Thriller"]
}
```

### Users API

```bash
GET /api/users
POST /api/users
```

### Payments API

```bash
GET /api/payments
POST /api/payments
```

### Subscriptions API

```bash
GET /api/subscriptions
POST /api/subscriptions
```

### Events API

```bash
POST /api/events
```

## Паттерн Strangler Fig

Сервис реализует паттерн Strangler Fig для постепенной миграции:

1. **Gradual Migration OFF** (`GRADUAL_MIGRATION=false`)
   - Весь трафик `/api/movies` идет в movies-service

2. **Gradual Migration ON** (`GRADUAL_MIGRATION=true`)
   - Трафик распределяется процентно:
   - `MOVIES_MIGRATION_PERCENT=50` → 50% в movies-service, 50% в монолит
   - `MOVIES_MIGRATION_PERCENT=100` → 100% в movies-service

### Пример тестирования миграции

```bash
# Установить 25% трафика в микросервис
export MOVIES_MIGRATION_PERCENT=25

# Установить 100% трафика в микросервис
export MOVIES_MIGRATION_PERCENT=100

# Отправить несколько запросов и проверить логи
for i in {1..10}; do
  curl http://localhost:8000/api/movies
done
```

## Логирование

Сервис логирует каждый проксированный запрос с указанием целевого сервиса:

```
INFO  c.c.proxy.service.ProxyService : Routing movies request to: http://movies-service:8081
INFO  c.c.proxy.service.ProxyService : Routing request to monolith: http://monolith:8080
```

## Мониторинг

Spring Boot Actuator endpoints доступны для мониторинга:

```bash
# Health check
GET /actuator/health

# Application info
GET /actuator/info
```
