
# User-API
A basic User API for creating users, generate JWT token, secure routes.

## Getting started

These are the instructions to setup and run the API on your local environment.

### Prev-Steps
```
* Install your favorite editor, this time I used [IntelliJ IDEA](https://www.jetbrains.com/es-es/idea/download/)
* Install [Java 1.8](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html))
* Install [GIT](https://git-scm.com/)
```

### 1. Clone the repository
```sh
git clone https://github.com/sordoneza/user-api.git
```

### 2. Run the application

The project includes the script file to generate the database once you start the application, using the following command
it will download if not already installed maven, followed by download project dependecies, and finnally will start the application

##### On Unix based systems
```sh
./mvnw spring-boot:run
```
##### On Windows

```sh
mvnw.cmd spring-boot:run
```

## Testing Endpoints 
The project includes Swagger UI which helps documenting endpoints and required parameters.
Once the application is running you could direct to http://localhost:8080/swagger-ui/index.html
so you could go over the listed API


