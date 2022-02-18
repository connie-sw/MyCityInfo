CSS436 - Prog.2

Submission Date: 10/29/2021
Author Name: Connie Seungwon Lee

Submission folder: Prog.2
Submission files: README.txt, MyCity.java, gson-2.8.8.jar 

Usage: this is a command line program
-> command: java -classpath ./gson-2.8.8.jar MyCity.java {CITY NAME}
-> example: ~/Prog.2$ java -classpath ./gson-2.8.8.jar MyCity.java bothell

Description:
- This application takes a city name.
- Regardless of space, it takes the input as one city name.
- By taking a city name, it shows the weather information of the city.
- The weather information includes: 
    - latitude & longitude for specific location info
    - general weather condition and its description
    - current temperature
    - feeling temperature
    - minimum temperature
    - maximum temperature
    - pressure
    - humidity
- Above information is from OpenWeatherMap API.
- With its location(latitude + longitude), it shows the city information.
- The city information includes:
    - Country with country code
    - Region with region code
    - Population
- Above information is from GeoDB Cities API.
- Exponential back-off is used for server error.
- For exponential back-off, maximum numbers of retry is 3
- For exponential back-off, waiting time is (2^n) * 1000, n >= 1  
- By retrying, waiting time is getting 2000, 4000, 8000, and so on.
