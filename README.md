# 🗂️ Sistema de Gestión de Residuos Sólidos

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.x-blue?style=flat&logo=react)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Supported-2496ED?style=flat&logo=docker)](https://www.docker.com/)

## 📋 Descripción General

**Sistema de Gestión de Residuos Sólidos** es una plataforma integral desarrollada como proyecto de grado que permite la gestión eficiente de residuos sólidos urbanos mediante tecnologías modernas y algoritmos de predicción.

### 🚀 Características Principales

- **🗃️ Gestión de Contenedores**: Control completo del inventario de contenedores y tipos de residuos
- **📊 Sistema de Predicción**: Algoritmos inteligentes para predecir patrones de llenado de contenedores
- **📈 Dashboard Interactivo**: Visualización de datos en tiempo real con gráficos y métricas
- **📑 Generación de Reportes**: Exportación de reportes en PDF con información detallada
- **👥 Gestión de Usuarios**: Sistema de autenticación con roles diferenciados
- **📧 Sistema de Notificaciones**: Alertas automáticas por email
- **🔄 Programación Automática**: Schedulers inteligentes para optimizar la recolección

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación principal
- **Spring Boot 3.x** - Framework de desarrollo
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **PostgreSQL** - Base de datos relacional
- **JWT** - Tokens de autenticación
- **iText** - Generación de reportes PDF
- **JavaMailSender** - Sistema de correos

### Frontend
- **React 18.x** - Biblioteca de interfaz de usuario
- **Chakra UI** - Componentes de diseño
- **Recharts** - Gráficos y visualizaciones
- **Axios** - Cliente HTTP
- **React Router** - Navegación
- **Vite** - Build tool y servidor de desarrollo

### DevOps
- **Docker & Docker Compose** - Containerización
- **Maven** - Gestión de dependencias Java
- **Git** - Control de versiones

## 📁 Estructura del Proyecto

```
tesis/
├── backend/                    # Aplicación Spring Boot
│   ├── src/main/java/com/tesisUrbe/backend/
│   │   ├── auth/              # Autenticación y seguridad
│   │   ├── entities/          # Modelos de datos
│   │   ├── prediction/        # Sistema de predicción
│   │   ├── reportsManagerPdf/ # Generación de reportes
│   │   ├── solidWasteManagement/ # Gestión de residuos
│   │   ├── usersManagement/   # Gestión de usuarios
│   │   └── emailManagement/   # Sistema de correos
│   ├── src/main/resources/
│   │   ├── application.yml    # Configuración principal
│   │   └── application-dev.yml # Configuración desarrollo
│   └── pom.xml               # Dependencias Maven
├── frontend/                  # Aplicación React
│   ├── src/
│   │   ├── components/       # Componentes reutilizables
│   │   ├── hooks/           # Custom hooks
│   │   ├── pages/           # Páginas principales
│   │   └── styles/          # Estilos CSS
│   ├── package.json         # Dependencias npm
│   └── vite.config.js       # Configuración Vite
├── docker-compose.yml        # Orquestación de contenedores
├── .env.example             # Variables de entorno ejemplo
└── README.md               # Este archivo
```

## 🚀 Cómo Empezar

### 📋 Prerrequisitos

- **Java 17** o superior
- **Node.js 18** o superior
- **PostgreSQL 15** (o Docker)
- **Maven 3.8** o superior
- **Git**

### ⚡ Inicio Rápido con Docker (Recomendado)

1. **Clonar el repositorio**
```bash
git clone https://github.com/copo12d/tesis.git
cd tesis
```

2. **Configurar variables de entorno**
```bash
cp .env.example .env
# Edita el archivo .env con tus configuraciones
```

3. **Levantar los servicios con Docker**
```bash
docker compose up -d
```

4. **¡Listo!** 🎉
   - **Frontend**: http://localhost:5173
   - **Backend API**: http://localhost:8080
   - **Base de datos**: localhost:5432

### 🔧 Instalación Manual

#### Backend

1. **Navegar al directorio backend**
```bash
cd backend
```

2. **Configurar base de datos**
   - Crear base de datos PostgreSQL
   - Configurar `application-dev.yml` con tus credenciales

3. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

#### Frontend

1. **Navegar al directorio frontend**
```bash
cd frontend
```

2. **Instalar dependencias**
```bash
npm install
```

3. **Ejecutar el servidor de desarrollo**
```bash
npm run dev
```

## 📚 Uso del Sistema

### 🔐 Autenticación
1. Accede a la aplicación en `http://localhost:5173`
2. Regístrate como nuevo usuario o inicia sesión
3. El sistema creará automáticamente un super admin en el primer arranque

### 📊 Dashboard
- **Métricas en tiempo real** de contenedores y residuos
- **Gráficos interactivos** de tendencias semanales
- **Alertas** de contenedores que requieren atención

### 🗃️ Gestión de Contenedores
- Agregar y gestionar contenedores
- Asignar tipos de residuos
- Monitorear niveles de llenado
- Programar recolecciones

### 📑 Reportes
- Generar reportes PDF personalizados
- Exportar datos de contenedores y residuos
- Análisis de eficiencia de recolección

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está desarrollado como trabajo de grado y está sujeto a las políticas académicas correspondientes.

## 👥 Equipo de Desarrollo

- **Angel** - Frontend Developer
- **José** - Backend Developer  
- **Luis** - Backend Developer

## 📞 Soporte

Para reportar problemas o sugerencias, por favor crea un [issue](https://github.com/copo12d/tesis/issues) en el repositorio.

---

⭐ **¡Si este proyecto te resulta útil, no olvides darle una estrella en GitHub!** ⭐
