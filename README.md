# spring-data-jpa

Before run this project install redis via docker via the following command:

docker run -d --name redis-stack-server -p 6379:6379 redis/redis-stack-server:latest

Then run the redis-stack-server docker container.

# To build Docker image give below cmd (salehinrocky docker user name):
* docker build -t salehinrocky/spring-boot-caching-redis:1.0.0 .
# To run build image to give the following cmd:
* docker run -p 8080:9191 salehinrocky/spring-boot-caching-redis:1.0.0
# To give name of running container from an image give the following cmd: 
* docker run -p 8080:9191 --name spring-boot-caching-redis salehinrocky/spring-boot-caching-redis:1.0.0
_Here salehinrocky/spring-boot-caching-redis:1.0.0 is given image name_

# To test api that run inside docker container
* first try with http://localhost:8080/products if it not work then 
* use "host.docker.internal" instead localhost
