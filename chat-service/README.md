# Chat Service
The chat service is responsible for delivering messages. Users connect to it via WebSockets (STOMP) to send and receive messages. 
This application is designed to be scalable, using Kafka so users can communicate with each other 
even if they are connected to different instances of the service.

## Technologies Used

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring WebSockets](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [Kafka](https://kafka.apache.org/)
- [Java JWT](https://github.com/jwtk/jjwt)
- [Lombok](https://projectlombok.org/)
