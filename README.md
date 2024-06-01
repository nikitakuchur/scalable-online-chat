# Scalable Online Chat

This chat application is designed using a microservices architecture to ensure scalability, flexibility, and reliability.

![Imgur](https://i.imgur.com/8z0mZtA.png)

The system comprises several key components:

- [Gateway](https://github.com/nikitakuchur/scalable-online-chat/tree/main/chat-gateway): 
Acts as the entry point for users. It manages routing and rate limiting, directing user requests to the appropriate services.
- [User Service](https://github.com/nikitakuchur/scalable-online-chat/tree/main/user-service): 
Handles user authentication and authorization. Users can sign up, log in, and log out via POST requests. It uses JWT for authentication and stores user data in a MongoDB database.
- [Chat Services](https://github.com/nikitakuchur/scalable-online-chat/tree/main/chat-service):
Multiple instances (Chat Service 1, Chat Service 2, etc.) handle real-time messaging via websockets. These services are designed to scale horizontally as needed.
- Kafka: Facilitates message brokering between chat services, ensuring reliable and efficient message delivery.
- [Chat Manager](https://github.com/nikitakuchur/scalable-online-chat/tree/main/chat-manager): 
Manages chats and saves messages to the database. It exposes endpoints for retrieving chat data, such as getting all chats, specific chat details, and messages within a chat.
- Database: MongoDB is used for storing chat messages and user information.

The application also has a [UI](https://github.com/nikitakuchur/scalable-online-chat/tree/main/chat-website) implemented using Next.js and TypeScript.

![Imgur](https://i.imgur.com/7PVUWL1.png)
