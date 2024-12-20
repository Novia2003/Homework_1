﻿# Домашняя работа №13
# Project Documentation

## Описание проекта

Проект реализует API с поддержкой функционала регистрации, логина, логаута, сброса пароля и разграничения доступа на основе ролей пользователя (USER и ADMIN).

### Функционал:

- [**Регистрация пользователей**](https://github.com/Novia2003/Homework_1/blob/5bcba6981a13ca75e7b042cbf92231d056611b5d/spring-app/src/main/java/ru/tbank/controller/AuthController.java#L24)
- [**Аутентификация и авторизация через токены**](https://github.com/Novia2003/Homework_1/blob/5bcba6981a13ca75e7b042cbf92231d056611b5d/spring-app/src/main/java/ru/tbank/controller/AuthController.java#L34)
- [**Логаут с аннулированием токенов**](https://github.com/Novia2003/Homework_1/blob/5bcba6981a13ca75e7b042cbf92231d056611b5d/spring-app/src/main/java/ru/tbank/controller/AuthController.java#L41)
- [**Сброс пароля с проверкой кода верификации**](https://github.com/Novia2003/Homework_1/blob/5bcba6981a13ca75e7b042cbf92231d056611b5d/spring-app/src/main/java/ru/tbank/controller/AuthController.java#L57)
- [**Разграничение прав доступа к эндпоинтам на основе ролей пользователя**](https://github.com/Novia2003/Homework_1/blob/fj_2024_lesson_13/spring-app/src/main/java/ru/tbank/configuration/security/SecurityConfig.java)
  
### Основные DTO:
- [**Тело запроса для регистрации пользователя**](https://github.com/Novia2003/Homework_1/blob/fj_2024_lesson_13/spring-app/src/main/java/ru/tbank/dto/registration/RegistrationDTO.java)
- [**Тело запроса для авторизации**](https://github.com/Novia2003/Homework_1/blob/fj_2024_lesson_13/spring-app/src/main/java/ru/tbank/dto/login/LoginRequestDTO.java)
- [**Ответ с jwt после успешной авторизации**](https://github.com/Novia2003/Homework_1/blob/fj_2024_lesson_13/spring-app/src/main/java/ru/tbank/dto/login/TokenResponseDTO.java)
- [**Тело запроса для смены пароля**](https://github.com/Novia2003/Homework_1/blob/fj_2024_lesson_13/spring-app/src/main/java/ru/tbank/dto/reset/PasswordResetDTO.java)
   
### Тесты:
- [**Проверка функциональности регистрации, логина, логаута и сброса пароля**](https://github.com/Novia2003/Homework_1/blob/fj_2024_lesson_13/spring-app/src/test/java/ru/tbank/controller/AuthControllerTest.java)
- [**К "admin" эндпоинтам есть доступ только у пользователей с ролью ADMIN**](https://github.com/Novia2003/Homework_1/blob/fj_2024_lesson_13/spring-app/src/test/java/ru/tbank/controller/AdminControllerTest.java)
