FROM openjdk:11

# port exposed
EXPOSE 8080

ADD gg.jar .

CMD java -jar gg.jar