# energy-data
Java (Spring Boot) console application for fetching, parsing and saving hourly energy consumption data by type.


The data is fetched from www.agora-energiewende.de as some mix of JS, JSON and HTML elements, parsed and saved in database.
Project is configured to be used with MySQL databases, but can be easily switched to any other SQL databases supported by Hibernate (see application.properties).

(This application fetches energy data for Germany, some or all names for energy types will be only in german)

Example:

<img width="595" alt="Screenshot 2023-03-15 at 09 58 47" src="https://user-images.githubusercontent.com/86569730/225266371-24fc191a-3990-4052-8553-19dd6427f5cf.png">



Example of data:

<img width="598" alt="Energy data example" src="https://user-images.githubusercontent.com/86569730/198019640-a0772f3c-3933-455c-affe-db547edb9a8f.png">

<img width="430" alt="Energy type data example" src="https://user-images.githubusercontent.com/86569730/198019576-28584b4d-588f-43fd-b5c6-9f4c96db02c0.png">
