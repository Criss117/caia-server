# C.A.I.A

Este es un proyecto de Spring Boot que utiliza Java 17 y PostgreSQL como base de datos.

## DOCUMENTACION

[Documento - Modelo C4, Historias de Usuario, etc](https://docs.google.com/document/d/1Ym52ATUNFAdyoGR-uNvlG5cYERNC8vxW/edit)

[Proyecto caia Video](https://drive.google.com/file/d/1kec3rqCJoFHEVblruzpj0E_ewy3MET1Z/view)

## Requisitos previos

- **Java 17**: Asegúrate de tener instalada la versión 17 de Java.
- **PostgreSQL**: Este proyecto utiliza PostgreSQL como base de datos.

## Configuración de la base de datos

La configuración de la base de datos se encuentra en el archivo `application.properties`, y las credenciales de la base de datos están en el archivo `application-dev.properties`. Debes crear este archivo a partir de la plantilla `application-dev.properties.template`.

### Pasos para configurar la base de datos

1. Copia el archivo `application-dev.properties.template` y renómbralo a `application-dev.properties`:

   ```bash
   cp src/main/resources/application-dev.properties.template src/main/resources/application-dev.properties
   ```

2. Edita el archivo `application-dev.properties` y completa las credenciales de la base de datos:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/tu_base_de_datos
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña

   security.jwt.key.private=tu_private_key
   security.jwt.user.generator=tu_user_generator
   ```

### Configuración de `application.properties`

En el archivo `application.properties` se ha configurado las siguientes propiedades

```properties
spring.application.name=caia-server
spring.profiles.active=dev

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.hibernate.format_sql=true

server.servlet.context-path=/api

```

- **spring.application.name=caia-server**: Define el nombre de la aplicación como caia-server.

- **spring.profiles.active=dev**: Activa el perfil de desarrollo (dev), cargando configuraciones específicas para este entorno.

- **spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect**: Configura Hibernate para usar el dialecto de PostgreSQL.

- **spring.jpa.show-sql=true**: Muestra las consultas SQL generadas por Hibernate en la consola.

- **spring.jpa.hibernate.ddl-auto=create**: Crea las tablas de base de datos cada vez que se inicia la aplicación (reemplaza las existentes).

- **spring.jpa.properties.hibernate.format_sql=true**: Formatea las consultas SQL en la consola para mejor legibilidad.

- **server.servlet.context-path=/api**: Establece /api como el prefijo para todas las rutas de la aplicación.

### Población de las tablas

Una vez que las tablas sean creadas, se poblaran automaticamente siempre que exista un archivo `import.sql` en la carpeta resources.

### Ejecución del proyecto

Una vez configurado el archivo `application-dev.properties`, puedes ejecutar el proyecto utilizando Eclipse o cualquier IDE compatible con Spring Boot.

```bash
./mvnw spring-boot:run
```

### Notas

- **Perfil de desarrollo**: El perfil activo es dev, lo cual es útil para configurar entornos locales y de pruebas.
- **Versión de Java**: Este proyecto está basado en Java 17. Asegúrate de que tu entorno esté configurado correctamente.

## CAIA – Crisis And Indecision Assembly

Bienvenido a CAIA, el sistema de gestión de conferencias que nadie pidió, pero que todos necesitan (aunque no lo admitan). Aquí podrás organizar, revisar y discutir ensayos en un ambiente de caos cuidadosamente estructurado. Porque ¿quién no disfruta de una buena crisis académica?

Características principales:

- **Gestión de usuarios**: Porque todos necesitan un título, incluso si es irrelevante.
- **Subida de ensayos**: Porque escribir un paper de última hora es una tradición.
- **Asignación de revisores**: O cómo pasarle el trabajo a alguien más.
- **Revisión de ensayos**: Donde el feedback es vago y las expectativas son altas.

Contribuciones son bienvenidas, pero advertidos quedan: aquí nada tiene sentido… y está bien.
