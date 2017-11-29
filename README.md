# IWCN_Gestion_de_productos Docker Avanzado

En esta práctica vamos a utilizar docker de manera avanzada. Para ello vamos a utilizar
nuevas características de docker.

### Creación de un DockerFile para el servidor web compilado en un archivo .jar

La realización de la práctica avanzada se basa en la creación de un fichero Dockerfile
para usar nuestro servidor web dockerizado.

#### Obtener el fichero .jar

Primero deberemos crear un fichero .jar empaquetando todo el proyecto java 8 Spring boot realizado
con maven. Maven es un gestor del ciclo de vida de una aplicación en la que se incluye la
gestión de dependencias.

      mvn clean install verify package

Una vez empaquetada la aplicación nos aparecerá en target el archivo .jar con todas las dependencias, los
test pasados y completamente empaquetada para directamente ejecutar la aplicación web desde el fichero .jar
con un tomcat embebido.

      ManagementProductsAPIRestServer-0.0.1-SNAPSHOT.jar

#### Realizar el fichero Dockerfile para la aplicación java 8.

Crearemos un fichero Dockerfile a partir de una máquina que tenga la JVM de Java 8 dentro del contendor
y en este contendor añadiremos el fichero .jar con el servidor REST web.

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

#### Creación de la imagen a partir del fichero Dockerfile

Una vez tenemos el fichero Dockerfile creamos la imagen desde el fichero Dockerfile con build
apuntando donde está el Dockerfile en el path de la opción build.

    $ docker build -t web .

    Sending build context to Docker daemon 52.72 MB
    Step 1/5 : FROM java:8
     ---> d23bdf5b1b1b
    Step 2/5 : EXPOSE 8080
     ---> Using cache
     ---> 2447580cd6d0
    Step 3/5 : VOLUME /tmp
     ---> Using cache
     ---> 2d6c1053e085
    Step 4/5 : ADD /target/ManagementProductsAPIRestServer-0.0.1-SNAPSHOT.jar web.jar
     ---> Using cache
     ---> dc385db516cb
    Step 5/5 : ENTRYPOINT java -jar /web.jar
     ---> Using cache
     ---> 8e7fcc7c91ba
    Successfully built 8e7fcc7c91ba

    $ docker images
    web                 latest              80b748cbc2d7        3 minutes ago       695 MB

Una vez creada la imagen  creamos el contendor. La creación del contendor se realiza enlazando con el contenedor
de la base de datos ya creado en la primera parte de la práctica de docker. El contendor de la base de datos debe
estar arrancado y crearemos el contendor del servidor web a partir de la imagen a la vez que enlazamos con el contendor
de la base de datos.

      $ docker run --name <nombre-contenedor> -p 8080:8080 --link <nombre-contenedor-a-linkar> -d <nombre-imagen-a-crear-el-contenedor>
      $ docker run --name web -p 8080:8080 --link mysql -d web

#### Nota respecto a JPA en el docker.

Cuando conectamos la base de datos en el docker debemos cambiar la url del JPA, ya que localhost no la cogerá en la red dockerizada. Hay que
usar el nombre del contendor para referirnos a la base de datos dentro de la red docker. En el fichero application.yml del servidor web cambiaremos
la url de la base de datos, añadiendo el nombre del contendor de la base de datos en la url del JPA en lugar de localhost.

      server:
        port: 8080

      spring:
          jpa:
              database: MYSQL
              hibernate:
                  ddl-auto: create

          datasource:
              url: jdbc:mysql://<name-db-container>/example
              username: root
              password: 123456
              driver-class-name: com.mysql.jdbc.Driver

El resultado final de los contendores funcionando es:

    CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                  PORTS                    NAMES
    dab8cbd52b36        web                 "java -jar /web.jar"     12 minutes ago      Up 12 minutes           0.0.0.0:8080->8080/tcp   web
    dd4f55f4c6e4        mysql:latest        "docker-entrypoint..."   6 days ago          Up 12 minutes           0.0.0.0:3306->3306/tcp   mysql
    21b359d98dc9        mariadb             "docker-entrypoint..."   9 days ago          Exited (0) 9 days ago                            mariadb

Para finalizar realizaremos una petición REST desde el navegador a el servidor web que estará escuchando en localhost:8080.

      localhost:8080/add POST
      {
            "code": 1,
            "name": "pepe",
            "description" : "example",
            "price" : 1.0
      }
      localhost:8080/list
      [
            {
                  "code": 1,
                  "name": "pepe",
                  "description" : "example",
                  "price" : 1.0
            },
            {
                  "code": 1,
                  "name": "pepe",
                  "description" : "example",
                  "price" : 1.0
            }

      ]

### Subida de la imagen a Dockerhub.

Para subir una imagen a dockerhub, debemos crear una cuenta en la página web de [dockerhub](https://hub.docker.com)
Una vez dados de alta en dockerhub hay que loguearse en la máquina local.

      $ docker login // Nos logueamos en dockerhub en local.

Creamos un tag de la imagen con el identificador de docker hub-

      $ docker tag <imagen> <idHub>/<tag>


Cuando creamos la etiqueta de la imagen ya podemos subir la imagen a dockerhub.

      $ docker push fjgutierrezs91/web
      The push refers to a repository [docker.io/fjgutierrezs91/web]
      0634d953f411: Pushed
      35c20f26d188: Mounted from library/java
      c3fe59dd9556: Mounted from library/java
      6ed1a81ba5b6: Mounted from library/java
      a3483ce177ce: Mounted from library/java
      ce6c8756685b: Mounted from library/java
      30339f20ced0: Mounted from library/java
      0eb22bfb707d: Mounted from library/java
      a2ae92ffcd29: Mounted from library/java
      latest: digest: sha256:9a53d8854bff59ac4e2c6cd3bad7ae1dc3800546e984c05ef07fb09bb26926f5 size: 2212
