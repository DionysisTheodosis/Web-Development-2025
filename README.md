# 🚗 Auto Repair Shop Management System

A comprehensive web-based management system for auto repair shops, developed as a university exercise for **Web Development 2025** at the University of the Aegean (ICSD).

## 📋 Project Overview

This application provides a complete solution for managing auto repair shop operations, including customer management, vehicle registration, mechanic assignments, and service appointments. The system features role-based access control (RBAC) with three user roles: **Secretary**, **Mechanic**, and **Customer**.

## ✨ Features

### ✅ Currently Implemented

- **User Management**
  - User authentication and authorization (JWT-based)
  - Role-based access control (Secretary, Mechanic, Customer)
  - User registration and login
  - User profile management
  - Secretary dashboard for managing all users
- **Car Management**
  - Vehicle registration and tracking
  - Car profile with detailed information
  - Multiple views (Grid/List) for car listings
  - Car owner association
  - CRUD operations for vehicles

- **Dashboard**
  - Role-specific dashboards
  - Statistics and overview panels
  - Quick access to key features

- **Internationalization (i18n)**
  - Multi-language support (English & Greek)
  - Dynamic language switching
  - Localized validation messages

- **UI/UX Features**
  - Responsive design for all screen sizes
  - Dark/Light theme switching
  - Modern, clean interface using PrimeVue components
  - Smooth animations and transitions
  - Custom landing page for anonymous users

- **Data Export**
  - Export data to CSV
  - Export data to Excel
  - PDF generation capabilities

### 🚧 Planned Features

- **Appointment System** _(Not Yet Implemented)_
  - Schedule appointments for vehicle services
  - Mechanic availability management
  - Appointment status tracking
  - Service cost calculation
  - Automated notifications

## 🛠️ Technology Stack

### Backend

- **Framework:** Spring Boot 3.4.4
- **Language:** Java 21
- **Database:** MySQL 8.0
- **ORM:** Spring Data JPA
- **Security:** Spring Security with JWT
- **API Documentation:** SpringDoc OpenAPI (Swagger)
- **Build Tool:** Maven
- **Additional Libraries:**
  - Lombok
  - Datafaker (for test data generation)
  - Apache POI (Excel export)
  - OpenCSV (CSV export)
  - OpenPDF (PDF generation)
  - JAXB (XML processing)

### Frontend

- **Framework:** Vue.js 3.5.22
- **State Management:** Pinia 3.0.1 with persistence
- **Routing:** Vue Router 4.5.0
- **UI Library:** PrimeVue 4.4.1 with PrimeIcons
- **Styling:** Tailwind CSS 3.4.17
- **HTTP Client:** Axios 1.13.4
- **Data Fetching:** TanStack Query (Vue Query) 5.90.5
- **Form Validation:** Vee-Validate 4.15.1 with Zod 4.1.12
- **Internationalization:** Vue I18n 11.1.3
- **Build Tool:** Vite 6.2.4
- **Additional Libraries:**
  - VueUse Core & Head
  - Chart.js 4.5.1
  - Prettier (code formatting)

### DevOps

- **Containerization:** Docker & Docker Compose
- **Web Server:** Nginx (for frontend serving)
- **Database:** MySQL 8.0 (containerized)

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Node.js 18+** and npm
- **Docker & Docker Compose** (for containerized deployment)
- **MySQL 8.0** (if running without Docker)

### Installation & Running

#### Option 1: Using Docker Compose (Recommended)

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd icsd15066
   ```

2. **Ensure secrets are configured**

   The project uses Docker secrets for sensitive information. Make sure the following files exist in the `secrets/` directory:
   - `db_root_password.txt`
   - `db_user.txt`
   - `db_password.txt`
   - `jwt_secret.txt`

3. **Build and start all services**

   ```bash
   docker-compose up --build
   ```

4. **Access the application**
   - Frontend: [http://localhost](http://localhost)
   - Backend API: [http://localhost:8080](http://localhost:8080)
   - API Documentation: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

#### Option 2: Manual Setup

**Backend Setup**

1. **Navigate to the backend directory**

   ```bash
   cd backend/autorepair
   ```

2. **Configure MySQL database**

   Update `src/main/resources/application.properties` with your MySQL credentials:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/car-repair
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and run the backend**

   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

   Or on Windows:

   ```bash
   mvnw.cmd clean install
   mvnw.cmd spring-boot:run
   ```

**Frontend Setup**

1. **Navigate to the frontend directory**

   ```bash
   cd frontend/autorepair
   ```

2. **Install dependencies**

   ```bash
   npm install
   ```

3. **Configure API endpoint**

   Update the API base URL in your environment configuration if needed.

4. **Run the development server**

   ```bash
   npm run dev
   ```

5. **Access the application**
   - Frontend: [http://localhost:5173](http://localhost:5173) (default Vite port)

### Building for Production

**Frontend**

```bash
cd frontend/autorepair
npm run build
```

**Backend**

```bash
cd backend/autorepair
./mvnw clean package
```

## 📁 Project Structure

```
icsd15066/
├── backend/
│   └── autorepair/
│       ├── src/
│       │   └── main/
│       │       └── java/
│       │           └── gr/aegean/icsd/autorepair/
│       │               ├── appointment/     # Appointment entities (basic structure)
│       │               ├── car/             # Car management
│       │               ├── dashboard/       # Dashboard features
│       │               ├── shared/          # Shared utilities & configs
│       │               └── user/            # User management & auth
│       ├── Dockerfile
│       └── pom.xml
├── frontend/
│   └── autorepair/
│       ├── src/
│       │   ├── components/    # Reusable Vue components
│       │   ├── composables/   # Vue composables
│       │   ├── features/      # Feature-based modules
│       │   ├── layouts/       # Layout components
│       │   ├── views/         # Page views
│       │   ├── router/        # Vue Router configuration
│       │   ├── stores/        # Pinia stores
│       │   ├── langs/         # i18n translations
│       │   └── plugins/       # Vue plugins
│       ├── Dockerfile
│       └── package.json
├── secrets/               # Docker secrets
├── docker-compose.yml     # Docker Compose configuration
├── nginx.conf             # Nginx configuration
└── README.md              # This file
```

## 👥 User Roles

- **Secretary**: Full administrative access to manage users, cars, and appointments
- **Mechanic**: Access to view assigned appointments and car details
- **Customer**: Access to view own vehicles and book appointments

## 🔐 Default Credentials

After running the application, default users are created for testing:

_(Note: Check your application's data seeding logic for default credentials)_

## 🌐 API Documentation

When running the backend, interactive API documentation is available at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🧪 Testing

**Backend Tests**

```bash
cd backend/autorepair
./mvnw test
```

**Frontend Tests**
_(Add your test commands here when implemented)_

## 📝 Development Notes

- The application uses **JWT tokens** stored in HTTP-only cookies for authentication
- **CORS** is configured to allow requests from the frontend
- Database migrations are handled automatically by Spring Boot/JPA
- The frontend uses **Vite** for fast development and optimized production builds
- **PrimeVue** components follow a custom theme configuration
- **Pinia** state is persisted to localStorage for better UX

## 🎓 Academic Context

This project was developed as part of the **Web Development** course at the **Information and Communication Systems Department (ICSD)**, University of the Aegean, for the academic year **2025**.

### Learning Objectives Covered:

- Full-stack web application development
- RESTful API design and implementation
- Modern frontend framework (Vue.js)
- State management and routing
- Authentication and authorization
- Database design and ORM usage
- Containerization with Docker
- Responsive UI/UX design

## 📄 License

This project is for educational purposes as part of university coursework---

**Note:** This is an ongoing project. The appointments feature is currently under development and will be implemented in future iterations.

