# build webapp
cd ./src/main/webapp
npm run build

# build server
cd ../../..
./mvnw clean package

# build docker
docker build --tag=gstochabock .

# release docker image
docker tag gstochabock:latest fundreas/gstochabock:latest
docker push fundreas/gstochabock:latest

# deploy on google cloud
docker tag gstochabock:latest gcr.io/gstochabock/gstochabock-spring:latest
docker push gcr.io/gstochabock/gstochabock-spring:latest