# IWCN_Gestion_de_productos Docker Avanzado

En esta práctica vamos a utilizar docker de manera avanzada. Para ello vamos a utilizar
nuevas características de docker.

### Creación de un Docker file a partir de una Imagen History

      #!/bin/bash
      docker history --no-trunc "$1" | \
      sed -n -e 's,.*/bin/sh -c #(nop) \(MAINTAINER .*[^ ]\) *0 B,\1,p' | \
      head -1
      docker inspect --format='{{range $e := .Config.Env}}
      ENV {{$e}}
      {{end}}{{range $e,$v := .Config.ExposedPorts}}
      EXPOSE {{$e}}
      {{end}}{{range $e,$v := .Config.Volumes}}
      VOLUME {{$e}}
      {{end}}{{with .Config.User}}USER {{.}}{{end}}
      {{with .Config.WorkingDir}}WORKDIR {{.}}{{end}}
      {{with .Config.Entrypoint}}ENTRYPOINT {{json .}}{{end}}
      {{with .Config.Cmd}}CMD {{json .}}{{end}}
      {{with .Config.OnBuild}}ONBUILD {{json .}}{{end}}' "$1"

El comando history de docker muestra la historia de una imagen y de ahí sacar un Dockerfile.

### Creación de un DockerFile para el servidor web compilado en un archivo .jar

#### Obtener el fichero .jar

Primero deberemos crear un fichero .jar empaquetando todo el proyecto java realizado con maven.

Maven gestión del ciclo de vida de una aplicación en las que se incluye la gestión de dependencias.

      mvn clean install verify package

Una vez empaquetada la aplicación nos aparecerá en target el archivo jar con todas las dependencias, los
test pasados y completamente empaquetada para directamente ejecutar la aplicación web desde el fichero .jar

      ManagementProductsAPIRestServer-0.0.1-SNAPSHOT.jar

#### Realizar el fichero Dockerfile para la aplicación java 8.

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

####
