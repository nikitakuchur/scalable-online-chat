# User Service
This service is responsible for storing user information and handling authorisation and authentication using JWT. 
It allows users to create new accounts, log in, and log out. 
When a user logs in, the application generates an access token and a refresh token 
that can be used to access secure endpoints and even other applications. 
It also provides functionality to refresh tokens and manage existing sessions.

## Technologies Used

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MongoDB](https://www.mongodb.com/)
- [Java JWT](https://github.com/jwtk/jjwt)
- [Lombok](https://projectlombok.org/)
- [Hibernate Validator](https://hibernate.org/validator/)
- [JUnit](https://junit.org/junit5/)
- [Testcontainers](https://testcontainers.com/)