# build webapp
cd ./src/main/webapp
npm run build

# build server
cd ../../..
./mvnw clean package

# build docker
docker build --tag=gstocha-bock .

# release docker image
docker tag gstocha-bock:latest fundreas/gstocha-bock:latest
docker push fundreas/gstocha-bock:latest

# deploy on google cloud
#docker tag gstocha-bock:latest gcr.io/gstochabock/gstochabock-spring:latest
#docker push gcr.io/gstochabock/gstochabock-spring:latest