# Crosswordpuzzle-Backend
This repo serves to persist the crosswordpuzzle data in a db and to communicate with different Microservices.
It also can check if out of given questions a crosswordpuzzle can be created.

# Development
## Getting started
Make sure you have the following installed:

- Java: [JDK 1.17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher
- Maven: [Maven 3.6.3](https://maven.apache.org/download.cgi)
- Postgres : [Postgres](https://www.postgresql.org/download/)

- Alternatively you can use [Docker](https://www.docker.com/)

First you have to change the spring.datasource.username and the spring.datasource.password in the application.properties file. If you changed the properties of the postgres db, you also have to change spring.datasource.url.


### Run 
```sh
mvn install
```
in the folder of the project.
Then you can run it with
```sh
java -jar target/crossword-backend-0.0.1-SNAPSHOT.jar
```

#### Docker-compose

Start all dependencies with our docker-compose files.
Check the [manual for docker-compose](https://github.com/Gamify-IT/docs/blob/main/dev-manuals/docker-compose/docker-compose.md).

### As a single Docker container

Build the Docker container with
```sh
docker build  -t crosswordpuzzle-backend-dev .
```
And run it at port 8080 with 
```
docker run -d -p 8080:80 -e POSTGRES_URL="postgresql://host.docker.internal:5432/postgres" -e POSTGRES_USER="postgres" -e POSTGRES_PASSWORD="postgres" --name crosswordpuzzle-backend-dev crosswordpuzzle-backend-dev
```

To monitor, stop and remove the container you can use the following commands:
```sh
docker ps -a -f name=crosswordpuzzle-backend-dev
```
```sh
docker stop crosswordpuzzle-backend-dev
```
```sh
docker rm crosswordpuzzle-backend-dev
```

To run the prebuild container use
```sh
docker run -d -p 8000:80 -e POSTGRES_URL="postgresql://host.docker.internal:5432/postgres" -e POSTGRES_USER="postgres" -e POSTGRES_PASSWORD="postgres" --name crosswordpuzzle-backend ghcr.io/gamify-it/crosswordpuzzle-backend:latest
```


### testing database
to setup a database with docker for testing you can use
```sh
docker run -d -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=crosswordpuzzle  --rm --name crosswordpuzzle-database postgres
```
To stop and remove it simply type
```sh
docker stop crosswordpuzzle-database
```
You can add test-questions with a simple post request to `/inputTestData`. To do thiis with curl simply type
```sh
curl --header "Content-Type: application/json"   --request POST   --data '{}'   http://localhost:8080/inputTestData
```
You can also add individual questiions by sending a post reqest with the question-data to . To do this with curl you can use
```sh
curl --header "Content-Type: application/json"   --request POST   --data '[{"question":"Foo?","answer":"Bar"}]'   http://localhost:8080/questions/test
```

### Rest mappings
Rest mappings are defined in [`src/main/java/com/crosswordservice/controller/CrosswordController.java`](src/main/java/de/unistuttgart/crosswordbackend/crosswordbackend/controller/CrosswordController.java)

## Project structure

| File / Directory                     | Description                          |
|--------------------------------------|--------------------------------------|
| `pom.xml`                            | Maven configuration file.            |
| `src/main`                           | Source folder with the java files.   |
| `src/test`                           | Folder with the uni tests.           |

