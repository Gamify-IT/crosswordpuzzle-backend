# Crosswordpuzzle-Backend
This repo serves to persist the crosswordpuzzle data in a db and to communicate with different Microservices.
It also can check if out of given questions a crosswordpuzzle can be created.

# Development
## Getting started
Make sure you have the following installed:

- Java: [JDK 1.17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher
- Maven: [Maven 3.6.3](https://maven.apache.org/download.cgi)
- Postgres : [Postgres](https://www.postgresql.org/download/)

First you have to change the spring.datasource.username and the spring.datasource.password in the application.properties file. If you changed the properties of the postgres db, you also have to change spring.datasource.url.

### Run 
```sh
mvn install
```
in the folder of the project.
Go to the target folder and run 
```sh
java -jar crossword-service-0.0.1-SNAPSHOT.jar
```

### testing database
to setup a database with docker for testing you can use
```sh
docker run -d -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=test  --rm --name crosswordpuzzle-database postgres
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

### Project structure

| File / Directory                     | Description                          |
|--------------------------------------|--------------------------------------|
| `pom.xml`                            | Maven configuration file.            |
| `src/main`                           | Source folder with the java files.   |
| `src/test`                           | Folder with the uni tests.           |

