# SpringCloudApp

## Spring Study on Microservices.

The aim of the project is to get a microservices project up in AWS EC2 ecosystem. The project is command line based and lacks UI. Implements Spring Security using JWT, and the ecosystem is only accessible through the API Gateway.

## Developed Microservices

Config
- For other microservices to get config files from github(public for portfolio).
   - Config properties can be found at: https://github.com/eartar/SCAConfig
- Deployment(EC2) configs are targeted for AWS deployments while the default packages are for local testing using cubectl.
- Some config variables are encrypted using Spring Security to test out the system.

Discovery
 - Eureka from Netflix OSS is deployed for microservice discovery. The dashboard is secured with Spring Security.
 
Gateway
 - Spring Cloud Gateway as a Load Balancer and Spring Security is utilized. The JWT verification is done on the gateway.
 - Multiple security filters are implemented to explore different possibilities. Only through the gateway the remaining ecosystem is made reachable to the outside world. 
 
Users
 - A microservice for handling user entity related information.
 
Accounts
 - Mostly migrated to users, just keeping it up for Spring Actuator.
 
Albums
 - A microservice for handling album entity related information. 


Also;
- Uses RabbitMQ. Bus-refresh is also enabled in the cubectl version to simulate an environment when the config files may change. 
- All modules have Spring Actuator enabled, mainly for troubleshooting
- All messaging to the outside world utilize asymmetric encryption with a RSA keypair generated locally.
- Uses Feign Client coupled with Netflix OSS Hystrix as a circuit breaker to prevent access if the target microservice is crashed or under heavy load.
- Sleuth & Zipkin is entegrated to help with troubleshooting.
- Logstash, Kibana and ElasticSearch are also entergrated, because why not! Also available in AWS but honestly, not properly tested in EC2 instance because of costs. 
- All the microservices are put into EC2 after being dockerized. The docker images can be found at dockerhub(https://hub.docker.com/u/eartar).
- In EC2, Elastic Search & Kibana service and MySQL service are deployed from their respective public dockerhub images and configured.

## Utilized Technologies
 - AWS EC2 w/ Docker
 - Spring MVC
 - Spring Bootstrap
 - Spring Cloud
 - Spring Security
 - Spring Data JPA
 - Kubernetes(Cubectl) w/ Docker for local testing
 - Maven
 - H2 database w/ MySQL
 - Sleuth & Zipkin
 - Feign & Netflix OSS Hystrix
 - ELK Stack's Kibana, ElasticSearch and Logstash
