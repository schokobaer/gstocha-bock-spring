# Gstocha-Bock Spring

## Build
```
./build.sh
```
or doing the steps manually:
```
# Build webapp
cd src/main/webapp
npm run build

# Build server
cd ../../..
mvnw clean package

# Dockerize
docker build --tag=gstochabock-spring .
```

## Run
### Java
```
cd target
java -jar gstocha-bock-0.0.1-SNAPSHOT.jar
```

### Docker
```
docker run -d -p 8080:8080 -v tmp:/tmp --name=gstochabock gstochabock-spring
```

## TODO:
  - Dornbirn Config
  - Display starter from last round on lastRoundHover
  - Exception handling on server
  - wait after the last round 2 seconds to show the game result
  - shorter ids or usage of terms of a dictionary

## Bugs:
  