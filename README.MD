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
docker run -d -p 8080:8080 -v C:\Users\Andreas\Desktop\tmp\docker:/data --name=gstochabock gstocha-bock
```

## TODO:
  - Display starter from last round on lastRoundHover
  - Exception handling on server
  - real logging on the server
  - last stich as table, not as list
  - show trumpf on GameResult
  - Tischname anzeigen
  - Puck always starts option

### Stylings:
  - Mobile: Hand cards in 2 rows, not overlapping
  - Shuffle Table: Force default display in RunningGames
  - Highlight the current played game in KulmiTable
  

## Bugs:
  - Untertrumpfen works in Webapp -> e.g. Laub Trumpf, EK, E8, E6 works
  - Changelog: Reverse should only happen once