# Obtención de contenedor base con Java 8.
FROM java:8

# Exposición del puerto de la imagen cuando se realice el contendor
EXPOSE 8080

# Dentro del FS del host donde va a tener el chroot el contenedor.
VOLUME /tmp

# Añadir al contenedor el fichero .jar con la aplicación
ADD /target/ManagementProductsAPIRestServer-0.0.1-SNAPSHOT.jar web.jar

# Punto de entrada del contenedor. Al arrancar el contendor arrancará desde este comando.
ENTRYPOINT ["java", "-jar", "/web.jar"]
