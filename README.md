# TTApp

## Introduction
This is a demo app for table tennis tournaments. It has below features:
1. Register/login as a player
2. Register a new tournament
3. Admin will add the players in the tournament
4. Create a fixture of players for tournament
5. Update the score and finalize winner

## Tech Stack
* Java
* Maven
* Spring boot
* Mysql
* Thymelyf
* Docker

## Installation
This project is created with Maven, so you just need to import it to your IDE and build the project to resolve the dependencies and run it.

## Database configuration
1. Create a mysql database with the name Eg. `ttapp-db`.
2. Add the credentials to below environment variables.
```
POSTGRES_PASSWORD=password
POSTGRES_USERNAME=root
POSTGRES_DB_NAME=ttapp-db
POSTGRES_DB_HOST=localhost
POSTGRES_DB_PORT=3306
```
3. Or add the credentials to application properties.
```
spring.datasource.url=jdbc:mysql://localhost:3306/ttapp-db
spring.datasource.username=root
spring.datasource.password=password
```

## Usage
Run the project through the IDE and head out to [http://localhost:8080](http://localhost:8080)

or

run this command in the command line:
```
mvn spring-boot:run
```

## Gitpod Workspace
We have added support to run this on gitpod directly to check how this application works.

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/SM4991/TTApp/blob/master/install.gitpod.sh)

#### Steps to do
1. Click on the open in gitpod button, it will open a gitpod workspace. The services will start running automatically.
2. Once the services has started. Go to ports tab and click on the link with 8080 port.
3. This is the spring boot application url. Eg. **https://8080-sm4991-ttapp-k0gqb8u14ub.ws-us77.gitpod.io/**.
4. Start using it.
