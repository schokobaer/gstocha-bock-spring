# Gstocha-Bock Spring

## Build
```
# Build webapp
cd src/main/webapp
npm run build

# Build server
cd ../..
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
docker run -p 8080:8080 -v tmp:/tmp --name=gstochabock gstochabock-spring
```

