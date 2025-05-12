# Customer Management API

This is a Spring Boot-based REST API for managing customer data, including support for membership tier calculation based on spending and purchase history.

---

##  Build & Run Instructions

### Prerequisites
- Java 21+
- Gradle (or use the Gradle wrapper: `./gradlew`)

#### Option 1: Using Gradle Wrapper
```bash
./gradlew bootRun
```
#### Option 2: Build JAR and Run
```bash
./gradlew clean build

java -jar build/libs/customer-0.0.1-SNAPSHOT.jar
```
### After Server Started Sucessfully

#### Import the Postman Collection 
```
classpath: resources/collections/postman.json
```

##  H2 Database Console

```
URL: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb

Username: sa

Password: password

Ensure the application is running before accessing the console.

```
###  Sample Requests

#### Create Customer

````
POST /customers
Content-Type: application/json

{
"name": "test",
"email": "testuser@example.com",
"annualSpend": 12000,
"lastPurchaseDate": "2024-12-01T00:00:00"
}
````

#### Get Customer by ID
```
GET /customers/{id}
```

#### Get Customer by Name
```
GET /customers?name=test

```

#### Get Customer by Email
```
GET /customers?email=testuser@example.com
```

#### Update Customer
```
PUT /customers/{id}
Content-Type: application/json

{
"name": "test Updated",
"email": "test.updated@example.com",
"annualSpend": 8000,
"lastPurchaseDate": "2025-01-01T00:00:00"
}
```

#### Delete Customer
```
DELETE /customers/{id}
```

### Assumptions
 
- Customer.id is auto-generated; must not be included in create requests.

- Membership tier is based on:

- Platinum: Annual spend ≥ 10,000 and last purchase within 6 months

- Gold: Annual spend ≥ 1,000 and within 12 months

- Silver: Everyone else

- All endpoints return a CustomerResponse that includes the calculated tier.

- Validation errors return descriptive messages.

- Global exception handling is in place for known and unknown issues.

### Testing

``
./gradlew test
``

### Project Structure Overview

- controller/: REST API controllers

- service/: Business logic and Implementations

- repository/: Spring Data JPA interfaces

- model/: JPA entity models and DTOs

- exception/: Custom exceptions and global handler

- config/ : Config files

- resources/application.yml: Configuration (including H2 console)