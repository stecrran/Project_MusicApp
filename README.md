MusicApp is a Spring Boot & Vue.js web application that allows users to manage and browse music collections. 
It includes role-based authentication, music playlist personalisation, and RESTful APIs.

git clone https://github.com/stecrran/Project_MusicApp.git
cd Project_MusicApp


CREATE DATABASE musicappdb;
Ensure all SQL scripts are executed on the musicappdb Database in order to avoid Constraint Violations.


application.properties (located in src/main/resources) contains the following:
spring.application.name=musicapp
spring.h2.console.enabled=true
spring.datasource.url=jdbc:mysql://localhost:3306/musicappdb
spring.jpa.hibernate.ddl-auto=update
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.show-sql=false
spring.jpa.defer-datasource-initialization=true
server.port=9091
jwt.secret=9Mk5/eAvEQKH0kpd7qlhBhdjhFIWaL9JNdQAqxZVxAbrN/I7x3PpWxy0l6pFdFnHyWZm61FPJCH4neg5z6JGlA==


Running the Application
Run the Spring Boot application with: mvn spring-boot:run

The application will be available at: http://localhost:9091

Credentials for login to application:
Username: Admin
Password: Admin

Click on "Connect to Spotify" on Spotify Connect page.
You wil be directed to Spotify to log in, if not already logged in to Spotify.
Once connected, any Spotify playlists will be displayed on the Spotify Connect page. From here, songs can be played. Any song played will be added to User Playlist and displayed on the User Playlist page.
Upcoming concerts can be accessed by country on the Gigs page. Click on event to be directed to Tickmaster page.
Spotify Top 50 Chart is dispayed on the Charts page.
Click the Home button or uMusicApp logo to go to Homepage.
" " 
