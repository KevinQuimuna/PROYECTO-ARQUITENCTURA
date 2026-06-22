# Guía de DevOps - Fase 2 LogiFlow

## Infraestructura Continua (Fase 2)

Esta guía describe la infraestructura DevOps implementada para la Fase 2 del proyecto LogiFlow, que incluye todos los microservicios del sistema de logística.

## Repositorio GitHub

### Ramas

- **main**: Rama estable para producción
- **development**: Rama de integración continua

### Workflow de CI/CD

El pipeline de GitHub Actions se ejecuta automáticamente en:
- Push a las ramas `main` y `development`
- Pull Request hacia las ramas `main` y `development`

Archivo de configuración: `.github/workflows/ci.yml`

## Análisis Estático con SonarCloud

### Configuración

El análisis estático se ejecuta automáticamente en cada push/PR. La configuración está en `sonar-project.properties`:

```properties
sonar.host.url=https://sonarcloud.io
sonar.java.source=17
sonar.coverage.jacoco.xmlReportPaths=\
ms-flota-rest/target/site/jacoco/jacoco.xml,\
ms-taller-rest/target/site/jacoco/jacoco.xml,\
ms-clientes/target/site/jacoco/jacoco.xml,\
ms-pedidos/target/site/jacoco/jacoco.xml,\
ms-ruteo/target/site/jacoco/jacoco.xml,\
ms-seguimiento/target/site/jacoco/jacoco.xml,\
ms-auth/target/site/jacoco/jacoco.xml,\
graphql-gateway/target/site/jacoco/jacoco.xml
```

### Secrets Requeridos

Configura los siguientes secrets en tu repositorio de GitHub:

1. **SONAR_TOKEN**: Token de autenticación de SonarCloud
   - Obténlo desde: https://sonarcloud.io/account/security/
   
2. **SONAR_ORGANIZATION**: Nombre de tu organización en SonarCloud
   - Ejemplo: `mi-organizacion`
   
3. **SONAR_PROJECT_KEY**: Clave única del proyecto
   - Ejemplo: `mi-organizacion_logiflow`

### Quality Gate

SonarCloud evalúa automáticamente:
- Cobertura de código
- Duplicaciones
- Bugs
- Vulnerabilidades de seguridad
- Code smells
- Debt técnico

## Notificaciones de Telegram

### Configuración

El pipeline envía notificaciones automáticas a Telegram con:
- Estado del build (éxito/fallo)
- Cobertura de código por módulo
- Resumen de ejecución de SonarCloud
- Enlace al workflow en GitHub Actions

### Secrets Requeridos

Configura los siguientes secrets en tu repositorio de GitHub:

1. **TELEGRAM_BOT_TOKEN**: Token del bot de Telegram
   - Crea un bot con @BotFather en Telegram
   - Copia el token proporcionado
   
2. **TELEGRAM_CHAT_ID**: ID del chat donde recibir notificaciones
   - Obtén tu ID con @userinfobot en Telegram
   - Para grupos, usa el ID del grupo

### Formato del Mensaje

```
LogiFlow CI | repo=usuario/logiflow | rama=development | maven=success | cobertura_instr=ms-clientes:75.2%,ms-pedidos:68.5%,ms-ruteo:72.1% | SonarCloud ejecutado. Issues y duplicaciones: ver panel del proyecto en sonarcloud.io (Quality Gate). | commit=abc1234 | https://github.com/usuario/logiflow/actions/runs/12345
```

## Cobertura de Código con JaCoCo

### Configuración

Cada microservicio incluye configuración de JaCoCo en su `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Reportes

Los reportes de cobertura se generan en:
- `*/target/site/jacoco/jacoco.xml` (formato XML para SonarCloud)
- `*/target/site/jacoco/index.html` (reporte HTML local)

### Ejecución Local

Para generar reportes de cobertura localmente:

```bash
mvn clean verify
```

Luego abre el reporte HTML en tu navegador:
- `ms-clientes/target/site/jacoco/index.html`
- `ms-pedidos/target/site/jacoco/index.html`
- etc.

## Pipeline de GitHub Actions

### Flujo de Ejecución

1. **Checkout**: Clona el repositorio
2. **Setup JDK 17**: Configura Java 17 con cache de Maven
3. **Maven Verify**: Compila y ejecuta tests
4. **SonarCloud**: Ejecuta análisis estático (si los secrets están configurados)
5. **Resumen JaCoCo**: Calcula cobertura por módulo
6. **Notificar Telegram**: Envía resumen a Telegram (si los secrets están configurados)

### Visualización

Los resultados del pipeline están disponibles en:
- GitHub Actions: https://github.com/[usuario]/logiflow/actions
- SonarCloud: https://sonarcloud.io/dashboard?id=[project-key]

## Troubleshooting

### SonarCloud no se ejecuta

**Síntoma**: El pipeline omite el paso de SonarCloud

**Causa**: Faltan secrets de SonarCloud

**Solución**: Configura `SONAR_TOKEN`, `SONAR_ORGANIZATION`, y `SONAR_PROJECT_KEY` en los secrets del repositorio

### Telegram no envía notificaciones

**Síntoma**: El pipeline omite el paso de Telegram

**Causa**: Faltan secrets de Telegram

**Solución**: Configura `TELEGRAM_BOT_TOKEN` y `TELEGRAM_CHAT_ID` en los secrets del repositorio

### Cobertura baja en algún módulo

**Síntoma**: El reporte de cobertura muestra porcentajes bajos

**Causa**: Falta de tests unitarios

**Solución**: Agrega tests unitarios en los módulos con baja cobertura

### Error de compilación

**Síntoma**: El pipeline falla en el paso de Maven Verify

**Causa**: Error de compilación o test fallido

**Solución**: Ejecuta `mvn clean verify` localmente para identificar el problema

## Métricas Objetivo

Para la Fase 2, se recomiendan las siguientes métricas:

- **Cobertura de código**: > 70% por módulo
- **Duplicaciones**: < 5%
- **Bugs**: 0 críticos, < 5 mayores
- **Vulnerabilidades**: 0 críticas
- **Code Smells**: < 100 por módulo

## Próximos Pasos

Para la Fase 3, se podría considerar:

- Implementación de CD (Continuous Deployment)
- Integración con Docker Hub
- Despliegue automático en staging/producción
- Monitoreo con Prometheus y Grafana
- Alertas automáticas basadas en métricas
