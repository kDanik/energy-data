# energy-data
Java (Spring Boot) console application for fetching, parsing and saving hourly energy consumption data by type.


The data is fetched from www.agora-energiewende.de as some mix of JS, JSON and HTML elements, parsed and saved in database.
Project is configured to be used with MySQL databases, but can be easily switched to any other SQL databases supported by Hibernate (see application.properties).


Example:

<img width="913" alt="Screenshot 2022-10-26 at 13 49 58" src="https://user-images.githubusercontent.com/86569730/198019679-be474ea1-0dd8-4b81-b009-54f093007f86.png">



Example of data:

<img width="598" alt="Screenshot 2022-10-26 at 13 50 37" src="https://user-images.githubusercontent.com/86569730/198019640-a0772f3c-3933-455c-affe-db547edb9a8f.png">

<img width="430" alt="Screenshot 2022-10-26 at 13 51 43" src="https://user-images.githubusercontent.com/86569730/198019576-28584b4d-588f-43fd-b5c6-9f4c96db02c0.png">
