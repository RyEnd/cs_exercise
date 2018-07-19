# how to run this project


## you will --probably-- definitely need
* maven
* java runtime environment
* postman (or equivalent)

## steps
1. clone this project.
2. cd into the project root folder (campspot)
3. build a complete jar: `mvn clean install`
4. start the server: `java -jar ./target/campspot-0.0.1-SNAPSHOT.jar`
5. open up postman, or other similar API testing app.
6. set your url to `localhost:8080/getAvailableCamps`
7. set HTTP method to POST
8. paste the contents of `test-case.json` in the request body.
9. send a request!