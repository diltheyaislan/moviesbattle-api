# Movies Battle API
Movies Battle API using Java and Spring
This API is a card game where the user needs to select the highest scoring card based on the website www.imdb.com<https://www.imdb.com/>.

## Screenshots

<>


## Running local

Via terminal:

```sh
.\gradlew bootRun
```


## Unit tests

Via terminal:

```sh
.\gradlew test
```
A test report is generated in '*.\build\reports\tests\test\index.html*'.


## API Documentation

The project uses `Swagger` with `sprigdoc-openapi` (<https://springdoc.org/>) for API Documentation.

Use **swagger.properties** file in */src/main/resources/* to define annotation constants.


### To access API Documentation via browser

 - **JSON**: <http://localhost:8080/v3/api-docs>
 - **Swagger UI**: <http://localhost:8080/swagger-ui/index.html>


## Postman collections
Postman environment variable and collection files are in the `/postman` directory.


## H2 console
H2 database has an embedded GUI console for browsing the contents of a database and running SQL queries. This console can be access by <http://localhost:8080/h2-console>.