# energy-data

Java (Spring Boot) application for fetching, parsing, and saving hourly energy consumption data by type.

## Description

The data is fetched from www.agora-energiewende.de as some mix of JS, JSON, and HTML elements, parsed and saved in
database.
The project is configured to be used with MySQL databases but can be easily switched to any other SQL database supported
by Hibernate (see application.properties).

(This application fetches energy data for Germany, some or all names for energy types will be only in german)

GUI:

<img width="595" alt="Screenshot 2023-03-15 at 09 58 47" src="https://user-images.githubusercontent.com/86569730/225266371-24fc191a-3990-4052-8553-19dd6427f5cf.png">


Example of data:

<img width="598" alt="Energy data example" src="https://user-images.githubusercontent.com/86569730/198019640-a0772f3c-3933-455c-affe-db547edb9a8f.png">

<img width="430" alt="Energy type data example" src="https://user-images.githubusercontent.com/86569730/198019576-28584b4d-588f-43fd-b5c6-9f4c96db02c0.png">

## Installation

The project can be simply launched from your IDE by running EnergyDataApplication.
All dependencies should be downloaded automatically by Maven.

### Properties

This application needs a database to function, connection properties can be configured in **application.properties**,
or you can create **application-local.properties** and configure it there (when using ....local.properties don't forget
to change **active profile** in run configuration).

Example of application-local.properties:

```java-properties
# Database connection and driver
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.url=jdbc:mysql://localhost:3309/test-energy?rewriteBatchedStatements=true

# Use this to generate tables automatically
spring.jpa.hibernate.ddl-auto=update
```

### Database type

This application is configured to be used with MySQL database, but can be changed (not tested) to another database.
For that, you need to add JBDC Driver dependency for your database.

For example for PostgreSQL:

```XML
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.5.4</version>
</dependency>
```

You also need to update your application properties (see *Properties* section), specifically *spring.datasource.driver-class-name*.
