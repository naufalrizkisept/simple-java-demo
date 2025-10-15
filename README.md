# simple-java-demo
A messy API microservices using Redis (Using Kafka and Kibana is an afterthought)

To use this:

1. Make sure you've already installed Docker Desktop and add following images in your Docker:
   a. redis
   b. mysql
   c. apache/kafka
   d. kibana
   e. elasticsearch
2. Run docker-compose.yml using command terminal "docker compose up -d"
3. Make sure to run SQL query scripts in query folder attached in this repository.
4. Check the newest executed database's TCP/IP connection and JDBC connection.
5. Open this project then check "application-dev.properties"
6. Setting up username and password for database connection properties.
7. Run DemoApplication.java
