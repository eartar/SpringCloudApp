# SpringCloudApp

Spring Study on Microservices, successor to the Spring Pet Clinic Study.

The aim of the project is to get a microservices project up and running in the AWS EC2. The project is command line based and lacks UI. 

The developed microservices;

Config Server
- For other microservices to get config files from github(public for portfolio).
- Deployment(EC2) configs are targeted for AWS deployments while the default packages are for local testing using cubectl.
- Some config variables are encrypted using Spring Security to test out the system.

Discovery
 - Eureka from Netflix OSS is deployed for microservice discovery
 
Gateway
 - Spring Cloud Gateway and Spring Security is utilized. The access token verification is done on the gateway.
 - Multiple security filters are implemented to explore different possibilities. Only through the gateway the remaining ecosystem is made reachable to the outside world. 
 
Users
 - A microservice for handling user entity related information.
 
Accounts
 - Mostly migrated to users, just keeping it up for Spring Actuator.
 
Albums
 - A microservice for handling album entity related information. 

Source code utilizes the following technologies;
AWS S3 w/ Docker
Spring MVC
Spring Bootstrap
Spring Cloud
Spring Security
Spring Data JPA
Kubernetes(Cubectl) w/ Docker for local testing
Maven
h2 database w/ MySQL
