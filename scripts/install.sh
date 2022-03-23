# login as unisys
mkdir /opt/eseal
mkdir /opt/eseal/log
cp eseal.jar /opt/eseal
cp eseal.conf /opt/eseal
cp eseal.service /opt/eseal
chmod +x eseal.jar

# to override default logging - there is already logback.xml and logback jars inside the executable JAR
# https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/reference/html/howto-logging.html
cp logback.xml /opt/eseal

# by this point program should work, if executed via shell: ./eseal-0.0.1-SNAPSHOT.jar
# logs should be written to /opt/eseal/log/eseal.log 
