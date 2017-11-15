FROM java:8
EXPOSE 8080

VOLUME /tmp

ADD /target/ManagementProductsAPIRestServer-0.0.1-SNAPSHOT.jar backend.jar

#ENV JAVA_OPTS=""

#RUN bash -c 'touch /backend.jar'

ENTRYPOINT ["java", "-jar", "/backend.jar"]
