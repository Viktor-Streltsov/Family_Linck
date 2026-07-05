# FamilyLink

Мобильное приложение для семейной коммуникации с AI-анализом настроения.

## Stack

**Backend:** Java 21, Spring Boot 3.5, PostgreSQL 16, Flyway, JPA, Swagger  
**Frontend:** React + Capacitor (в разработке)

## Запуск локально

### Требования
- JDK 21
- PostgreSQL 16
- Maven

### База данных
Создайте пользователя и базу в PostgreSQL:
\`\`\`sql
CREATE USER familylink WITH PASSWORD 'familylink_dev_password';
CREATE DATABASE familylink OWNER familylink;
\`\`\`

### Backend
\`\`\`bash
cd backend
./mvnw spring-boot:run
\`\`\`

Приложение доступно на http://localhost:8080  
Swagger UI: http://localhost:8080/swagger-ui.html

## Roadmap
- [x] User entity + Repository
- [ ] Регистрация / логин с JWT
- [ ] Семьи и приглашения
- [ ] Чат через WebSocket
- [ ] AI-анализ настроения
- [ ] Календарь и задачи