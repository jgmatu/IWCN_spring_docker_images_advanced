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
