
# Orders service

## Introducción
Este proyecto es un microservicio de ejemplo para la gestión de órdenes, desarrollado en Java. El microservicio implementa un flujo CI/CD que abarca tres ambientes: 'develop', 'staging' y 'main'. Además, realiza pruebas automatizadas y despliega contenedores en Amazon ECS (Elastic Container Service).

## Configuraciones de GitHub Actions
Puedes encontrar las configuraciones de GitHub Actions en el directorio `.github/workflows` de este repositorio. Estos archivos YAML definen los flujos de trabajo automatizados que se ejecutan en respuesta a eventos específicos, como push o pull request. Estos flujos de trabajo incluyen tareas como compilación, pruebas y despliegue.

Para que el workflow funcione correctamente, es necesario configurar los secretos y variables de entorno utilizados en los workflows. Puedes encontrar más información sobre cómo configurar y personalizar GitHub Actions en la documentación oficial de GitHub Actions.

