# Gstocha-Bock Spring

## Build
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
  - Feedback vom Weisen
  - Dornbirn Config
  - Display starter from last round on lastRoundHover
  - Logging on server
  - Exception handling on server

## Bugs:
  - Quartett not displayed correctly