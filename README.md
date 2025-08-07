Vienna U-Bahn Route Finder 
--------------------------

Application built with JavaFX that allows users to search for routes between stations on the Vienna U-Bahn (subway) given a starting station and a destination station. 

Users can specify stations to avoid, as well as specify stations to visit on the way in a specific order. Users have 3 modes to retrieve a route: route with least stations passed, route with shortest distance and all routes without backtracking (unless there are waypoint stations set). 

When selecting 'all routes' mode, a user can cycle through all routes one route at a time. When using 'route with shortest distance', user can set a penalty (weighed in km) for changing lane.
<img width="1372" height="1014" alt="Screenshot_20250806_222000" src="https://github.com/user-attachments/assets/c0ba52ba-b959-4950-a0f1-d52858230748" />

Build
-----
The following dependencies are required:
  - Java (JDK 21 or higher recommended)
  - Maven

Clone the repository, change current directory into it and use Maven run the project:
```
git clone https://github.com/DanielsNagornuks-SETU/vienna-u-bahn-route-finder.git
cd vienna-u-bahn-route-finder
mvn javafx:run
```
