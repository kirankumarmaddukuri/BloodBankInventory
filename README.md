# Blood Bank Inventory & Emergency Donor Matching System

<div align="center">

<div align="center">

<img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
<img src="https://img.shields.io/badge/Spring_Boot-3.2.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
<img src="https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React"/>
<img src="https://img.shields.io/badge/PostgreSQL-14+-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>
<img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT"/>
<img src="https://img.shields.io/badge/Vite-5-646CFF?style=for-the-badge&logo=vite&logoColor=white" alt="Vite"/>

</div>

**A full-stack Blood Bank Management System for real-time inventory tracking, donor management, and emergency matching.**

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Business Rules](#-business-rules)
- [API Endpoints](#-api-endpoints)
- [Getting Started](#-getting-started)
- [Demo Credentials](#-demo-credentials)
- [Project Structure](#-project-structure)
- [Team](#-team)

---

## 🩸 Overview

It is a real-time Blood Bank Inventory and Emergency Donor Matching System designed to bridge the critical gap between blood donors, blood banks, and hospitals. The system enforces strict medical business rules (donation intervals, hemoglobin thresholds, compatibility matrices) and provides role-specific dashboards for all stakeholders.


---

## ✨ Features

### 🔐 User Management
- Role-based authentication with JWT tokens
- 5 distinct user roles with scoped access
- Encrypted password storage with BCrypt

### 🧑‍🤝‍🧑 Donor Management
- Donor registration with medical eligibility validation
- Donation appointment scheduling
- Donation history tracking
- Admin-controlled eligibility override (Eligible / Temporarily Deferred / Permanently Deferred)

### 🩸 Blood Unit Lifecycle
- **COLLECTED → TESTING → AVAILABLE → ISSUED / DISCARDED / EXPIRED**
- Automated expiry date calculation per component type
- Medical testing lab panel (HIV, Hepatitis, Malaria, Syphilis screening)
- Daily scheduled expiry detection
- Inventory tracking by blood group and component type

### 🏥 Blood Request Management
- Hospital blood requisition with priority levels (ROUTINE / URGENT / EMERGENCY)
- Admin processing workflow: PENDING → PROCESSING → FULFILLED
- Automatic blood compatibility matrix matching during fulfillment
- Real-time request status tracking for hospital staff

### 🚨 Emergency Matching
- Emergency broadcast system for critical blood shortages
- Automatic donor matching by blood group and eligibility status
- Real-time donor response tracking (ACCEPTED / DECLINED)
- 24-hour alert validity window

### 💉 Transfusion Tracking
- Full transfusion record with patient and staff details
- Adverse reaction flagging and incident reporting

### 📊 Live Dashboards
- **Admin:** Inventory stats, critical stock alerts, testing lab, donor search, hospital request command
- **Hospital Staff:** Requisition form, transfusion logger, live inventory preview, request tracker
- **Dispatcher:** Emergency broadcast, blood stock overview, eligible donor pool, crisis networks
- **Donor:** Donation stats, eligibility status, emergency ping notifications

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Backend Framework** | Spring Boot 3.2.4 |
| **Language** | Java 17 |
| **Security** | Spring Security + JWT (JJWT) |
| **Database** | PostgreSQL 14+ |
| **ORM** | Spring Data JPA / Hibernate |
| **Validation** | Jakarta Bean Validation |
| **Frontend** | React 18 + Vite 5 |
| **Styling** | Tailwind CSS |
| **HTTP Client** | Axios |
| **Icons** | Lucide React |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |
| **Build Tool** | Maven |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        React Frontend                       │
│              Role-based Dashboard (5 portals)               │
│         Axios → JWT Bearer Token → Spring Boot API          │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP (localhost:8080)
┌──────────────────────▼──────────────────────────────────────┐
│                   Spring Boot Backend                       │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────────┐  │
│  │  Controller │→ │   Service    │→ │    Repository     │  │
│  │    Layer    │  │  (Business   │  │  (Spring Data     │  │
│  │  (REST API) │  │    Logic)    │  │      JPA)         │  │
│  └─────────────┘  └──────────────┘  └─────────┬─────────┘  │
│  ┌─────────────────────────────┐              │            │
│  │   Spring Security + JWT     │              │            │
│  │  PreAuthorize (per role)    │              │            │
│  └─────────────────────────────┘              │            │
└──────────────────────────────────────┬────────┘────────────┘
                                       │ JDBC
┌──────────────────────────────────────▼────────────────────┐
│                     PostgreSQL Database                    │
│  users · donors · blood_units · blood_requests             │
│  transfusion_records · emergency_alerts                    │
│  donor_notifications · donation_appointments · inventory_logs │
└───────────────────────────────────────────────────────────┘
```

---

## 📏 Business Rules

| Rule | Value |
|---|---|
| Minimum donor weight | 50 kg |
| Minimum hemoglobin | 12.5 g/dL |
| Donation interval (whole blood) | 90 days |
| Donor age range | 18 – 65 years |
| Blood unit volume | 350 – 450 mL |
| Expiry warning threshold | 7 days |
| Critical stock threshold | < 5 units per blood group |
| Emergency alert validity | 24 hours |
| Emergency processing time | 30 minutes |
| Urgent processing time | 4 hours |
| Routine processing time | 24 hours |

### Blood Type Compatibility Matrix

| Recipient | Can Receive From |
|---|---|
| **O-** | O- |
| **O+** | O+, O- |
| **A-** | A-, O- |
| **A+** | A+, A-, O+, O- |
| **B-** | B-, O- |
| **B+** | B+, B-, O+, O- |
| **AB-** | AB-, A-, B-, O- |
| **AB+** | All blood types |

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

### Donors
| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/donors` | Admin, Blood Bank Admin, Emergency Coordinator |
| GET | `/api/donors/eligible?bloodGroup=O_NEG` | Admin, Blood Bank Admin, Emergency Coordinator |
| PUT | `/api/donors/{id}/eligibility` | Blood Bank Admin |
| GET | `/api/donors/my-profile` | Donor |
| GET | `/api/donors/my-history` | Donor |
| GET | `/api/donors/notifications` | Donor |
| PUT | `/api/donors/notifications/{id}/respond` | Donor |

### Blood Units
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/blood-units` | Blood Bank Admin |
| GET | `/api/blood-units/inventory` | Admin, Blood Bank Admin, Hospital Staff, Emergency Coordinator |
| GET | `/api/blood-units/testing` | Admin, Blood Bank Admin |
| GET | `/api/blood-units/expiring` | Admin, Blood Bank Admin |
| PUT | `/api/blood-units/{id}/test` | Blood Bank Admin |
| PUT | `/api/blood-units/{id}/discard` | Blood Bank Admin |

### Blood Requests
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/blood-requests` | Hospital Staff |
| GET | `/api/blood-requests` | Admin, Blood Bank Admin, Hospital Staff |
| PUT | `/api/blood-requests/{id}/process` | Blood Bank Admin |
| PUT | `/api/blood-requests/{id}/fulfill` | Blood Bank Admin |

### Emergency Alerts
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/emergency-alerts` | Emergency Coordinator |
| GET | `/api/emergency-alerts/active` | All authenticated |
| GET | `/api/emergency-alerts/{id}/donor-match` | Emergency Coordinator |
| PUT | `/api/emergency-alerts/{id}/fulfill` | Emergency Coordinator |

### Transfusions
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/transfusions` | Hospital Staff |
| GET | `/api/transfusions` | Hospital Staff, Admin |

---

## 🚀 Getting Started

### Prerequisites

| Tool | Minimum Version | Download |
|---|---|---|
| Java JDK | 17 | https://www.oracle.com/java/technologies/downloads/ |
| Apache Maven | 3.8+ | https://maven.apache.org/download.cgi |
| Node.js | 18+ | https://nodejs.org/ |
| PostgreSQL | 14+ | https://www.postgresql.org/download/ |

---

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO.git
cd YOUR_REPO
```

---

### 2. Set Up the Database

Open pgAdmin or any PostgreSQL client and create a new database:
```sql
CREATE DATABASE bloodbank;
```

The default credentials expected by the backend:
- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `bloodbank`
- **Username:** `postgres`
- **Password:** `postgres`

If your credentials differ, update `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

---

### 3. Run the Backend — First Time (Seed Data)

Open `backend/src/main/resources/application.properties` and set:
```properties
spring.jpa.hibernate.ddl-auto=create
```

Start the backend:
```bash
cd backend
mvn spring-boot:run
```

Wait for this success message in the console:
```
✅ ALL TABLES SEEDED SUCCESSFULLY! READY FOR DEMO!
  admin@gmail.com       / admin123
  manager@gmail.com     / manager123
  hospital@gmail.com    / hospital123
  donor@gmail.com       / donor123
  dispatcher@gmail.com  / dispatcher123
```

**Stop the server** (`Ctrl+C`), then change `application.properties` back to:
```properties
spring.jpa.hibernate.ddl-auto=update
```

Start the backend again:
```bash
mvn spring-boot:run
```

The backend is now live at → **http://localhost:8080**

Swagger UI is available at → **http://localhost:8080/swagger-ui.html**

---

### 4. Run the Frontend

Open a **new terminal**:

```bash
cd frontend
npm install
npm run dev
```

The frontend is now live at → **http://localhost:5173**

---

## 🔑 Demo Credentials

| Role | Email | Password | Dashboard |
|---|---|---|---|
| System Admin | `admin@gmail.com` | `admin123` | Full system access |
| Blood Bank Manager | `manager@gmail.com` | `manager123` | Inventory, donations, requests |
| Hospital Staff | `hospital@gmail.com` | `hospital123` | Request blood, log transfusions |
| Donor | `donor@gmail.com` | `donor123` | View history, emergency pings |
| Emergency Dispatcher | `dispatcher@gmail.com` | `dispatcher123` | Broadcast alerts, monitor stock |

---

## 📁 Project Structure

```
FlowSync/
│
├── backend/                                  ← Spring Boot Application
│   ├── pom.xml
│   └── src/main/java/com/bloodbank/
│       ├── BloodBankApplication.java
│       ├── config/
│       │   ├── SecurityConfig.java
│       │   └── DataSeeder.java               ← Seeds demo data on first run
│       ├── controller/                       ← REST API Layer
│       │   ├── AuthController.java
│       │   ├── DonorController.java
│       │   ├── BloodUnitController.java
│       │   ├── BloodRequestController.java
│       │   ├── EmergencyAlertController.java
│       │   └── TransfusionController.java
│       ├── service/                          ← Business Logic Layer
│       │   ├── BloodUnitService.java
│       │   ├── BloodRequestService.java
│       │   ├── EmergencyMatchingService.java
│       │   └── InventoryService.java
│       ├── entity/                           ← JPA Database Models
│       │   ├── User.java
│       │   ├── Donor.java
│       │   ├── BloodUnit.java
│       │   ├── BloodRequest.java
│       │   ├── TransfusionRecord.java
│       │   ├── EmergencyAlert.java
│       │   ├── DonorNotification.java
│       │   ├── DonationAppointment.java
│       │   ├── InventoryLog.java
│       │   └── enums/                        ← All system enumerations
│       ├── repository/                       ← Spring Data JPA Repositories
│       ├── security/                         ← JWT Filter, UserDetails, Token Service
│       ├── scheduler/                        ← Expiry auto-detection scheduler
│       ├── util/                             ← CompatibilityUtil (blood matrix)
│       └── dto/                              ← Request/Response DTOs
│
├── frontend/                                 ← React + Vite Application
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── App.jsx                           ← Routes and protected navigation
│       ├── main.jsx
│       ├── index.css                         ← Global styles and design tokens
│       ├── context/
│       │   └── AuthContext.jsx               ← JWT auth state management
│       ├── services/
│       │   └── api.js                        ← Axios client with JWT interceptor
│       ├── pages/
│       │   ├── Login.jsx
│       │   ├── Register.jsx
│       │   └── Dashboard.jsx                 ← Role-based dashboard (5 portals)
│       └── components/
│           ├── StatCard.jsx
│           └── RecordCard.jsx
│
├── README.md
└── .gitignore
```

---




