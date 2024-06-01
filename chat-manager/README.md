# User Manager
This application is responsible for accessing and managing chats. It allows users to create, update, and delete chats. 
This service is secure, requiring users to acquire a JWT token before accessing any endpoints. 
Additionally, this service is responsible for retrieving all messages from Kafka and saving them into MongoDB.

## Technologies Used

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MongoDB](https://www.mongodb.com/)
- [Java JWT](https://github.com/jwtk/jjwt)
- [Kafka](https://kafka.apache.org/)
- [Lombok](https://projectlombok.org/)
- [Hibernate Validator](https://hibernate.org/validator/)
- [JUnit](https://junit.org/junit5/)
- [Testcontainers](https://testcontainers.com/)
