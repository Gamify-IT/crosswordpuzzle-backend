# Crosswordpuzzle-Backend
This repo serves to persist the crosswordpuzzle data in a db and to communicate with different Microservices.
It also can check if out of given questions a crosswordpuzzle can be created.

# Development
## Getting started
> Beginning of additions (that work)

Make sure you have the following installed:

- Java: [JDK 1.17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher
- Maven: [Maven 3.6.3](https://maven.apache.org/download.cgi)
- Docker: [Docker](https://www.docker.com/)
- PostgreSQL: [PostgreSQL](https://www.postgresql.org/download/)

### Run
### Project build
To build the project, run:
```sh
mvn install
```

in the project folder.
Then go to the target folder:
```sh
cd target
```
and run:
```sh
java -jar crossword-backend-0.0.1-SNAPSHOT.jar
```
to start the application.


### Build with docker
To run your local changes as a docker container, with all necessary dependencies,
build the Docker container with:

```sh
docker compose up --build
```
You can remove the containers with:
```sh
docker compose down
```

### Run local with dependencies
To run your local build within your IDE, but also have the dependencies running in docker, follow the steps
to build the project, then run the dependencies in docker with the following:
```sh
docker compose -f docker-compose-dev.yaml up 
```
You can remove the containers with:
```sh
docker compose -f docker-compose-dev.yaml down
```

> End of additions

### Testing database
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

