[![Tecabix](https://www.tecabix.com/wp-content/uploads/2020/12/cropped-tecabix-logo-secundario-mini.png "Tecabix")](http://www.tecabix.com "Tecabix")


El nombre **Tecabix** esta compuesto por tres partes:
- El **tec** de la contracción de tecnología.
- El **abi** de la contracción de Abinadí. 
- La **x** en honor al sistema operativo Linux.

El **S**istema de **C**ontrol **E**scolar Tecabix (en adelante, "Tecabix SCE") esta basado en el arquetipo de **Tecabix Core**.

Tecabix SCE es de código abierto bajo la licencia de GNU con el objetivo de que el proyecto pueda ser estudiado y/o implementado con fines educativos.
Creemos que la educción debe ser publica y accesible para todo aquel que desee aprender.

El proyecto **Tecabix SCE** permite gestionar los recursos de una institución educativa, como son los maestros, estudiantes, siclos escolares, etc. 

## Ambientación de desarrollo
El proyecto fue desarrollado en Java 11 con eclipse, usando el motor de base de datos PostgreSQL, por lo que se recomienda usar estas mismas tecnologías para su desarrollo o implementación. 

- Instale la JDK 11, recomendamos openJDK de https://adoptopenjdk.net. 
- Instale PostgreSQL de https://www.postgresql.org/download/
- Instale eclipse de https://www.eclipse.org/downloads/

Una vez instalado lo anterior, es necesario que se ejecute los scripts que se encuentran en el siguiente repositorio:
https://github.com/RamirezUrrutiaAngel/tecabix-sce-sql

Configure los properties del proyecto acorde a sus necesidades.

Levanta el proyecto y diríjase a la siguiente dirección : 

http://localhost:8080/swagger-ui.html#/

Se le solicitara que se autentifique, use las siguientes credenciales para loguearse:

|   |   |
| ------------ | ------------ |
| Nombre de usuario   | root  |
| Contraseña  | admin  |

Si todo esta correcto, podrá ver el swagger.

[![swagger](https://www.tecabix.com/wp-content/uploads/2021/04/swagger.png "swagger")](https://www.tecabix.com/ "swagger")





