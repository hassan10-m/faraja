# Faraja Dispensary Management System

University group project: HTML5, CSS3, vanilla JavaScript, Java 17, Spring Boot, Spring Security, JPA/Hibernate, Flyway and MySQL 8. All browser pages are served by Spring Boot; no frontend build process.

## Run on Windows PowerShell

Install JDK 17+, Maven 3.9+ and MySQL 8. Check `java -version`, `mvn -version`, and that MySQL is running. Create a MySQL user permitted to create the Faraja database, or create the database first:

```sql
CREATE DATABASE IF NOT EXISTS faraja CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'faraja_app'@'localhost' IDENTIFIED BY 'choose-a-strong-password';
GRANT ALL PRIVILEGES ON faraja.* TO 'faraja_app'@'localhost';
```

In PowerShell, inside the project folder:

```powershell
$env:DB_USER="faraja_app"
$env:DB_PASSWORD="your-mysql-password"
$env:ADMIN_USER="admin"
$env:ADMIN_PASSWORD="your-unique-password-at-least-12-characters"
mvn spring-boot:run
```

Open http://localhost:8080 and sign in with your admin credentials. In CMD use `set DB_USER=...` etc. On macOS/Linux use `export DB_USER=...` etc. Do not commit actual passwords. Environment variable samples appear in `application.properties.example`. Flyway executes `V1__schema.sql` automatically on an empty database; JPA validates the resulting tables. The administrator is created on first run only. To change its password later, use Settings → Change your password; changing `ADMIN_PASSWORD` does not rewrite existing accounts.

## Workflow to demonstrate

1. Administrator creates staff accounts in Team & audit; assign `RECEPTIONIST`, `DOCTOR`, `NURSE`, `LAB`, `PHARMACIST`, `CASHIER`.
2. Receptionist registers a patient, copies the patient ID from directory, books an appointment and starts a visit.
3. Doctor writes consultation, orders a laboratory test and prescribes a medicine.
4. Laboratory staff records the result; pharmacist enters medicine inventory and dispenses a prescription; stock decreases once.
5. Cashier creates an invoice and records payment; reports summarize counts and money collected.
6. Administrator inspects the audit log. Use different staff accounts to verify restricted operations.

The database stores patients, appointments, visits, lab orders, medicines, prescriptions, invoices, payments, staff and audit events. SQL foreign keys prevent orphan records. HTML pages are in `src/main/resources/static`, Java REST controllers and service are in `src/main/java/tz/faraja`, migration in `src/main/resources/db/migration`. Fetch calls `/api/...`; it never connects to MySQL. Authentication uses server sessions and Spring Security CSRF protection. Passwords are BCrypt hashed. API errors appear in the page status area.

## Test and package

```bash
mvn test
mvn clean package
java -jar target/faraja-dispensary-1.0.0.jar
```

Manual smoke test: register a patient; start a visit; record vitals; order and complete a lab test; add a nonexpired medicine; prescribe and dispense it; confirm stock fell and a second dispense fails; issue invoice; pay part of it and confirm an overpayment is rejected; log in as a receptionist and confirm `/api/admin/users` returns 403. Back up your MySQL data before production use.

## Deployment notes

Provision a MySQL 8 database, configure `DB_URL`, `DB_USER`, `DB_PASSWORD`, `ADMIN_USER` and `ADMIN_PASSWORD`, then run the packaged JAR behind HTTPS with a trusted reverse proxy. Restrict database and app network access; provision backups, retention rules and staff training before handling real patient information. This is a university demonstration, not a clinically certified electronic health record. It does not integrate insurance, SMS, payment gateways or national patient IDs. JUnit service and MockMvc security tests are included; see TESTING.md for the real-database acceptance workflow.


## Existing installation

Read UPGRADE.md. You can double-click START-FARAJA.cmd to start from the right directory and enter database credentials without editing source. Settings supports changing your own password. The original V1 SQL migration is unchanged.

## Deployment package

An optional Dockerfile builds/tests the Maven application and packages the Java server. MySQL stays external. Build with `docker build -t faraja .` and supply the environment variables at runtime using your hosting provider's secret settings. Configure HTTPS, `SERVER_SERVLET_SESSION_COOKIE_SECURE=true`, persistent MySQL storage and backups on the chosen host. No public deployment is included or claimed.

## Group presentation

HTML pages are served from `static`; CSS defines the responsive layout; `js/app.js` performs Fetch requests. Controllers enforce roles and validate request data. ClinicService coordinates business actions in transactions. Repository interfaces issue JPA queries. Entities map primary/foreign keys and relationships; Flyway owns schema creation. Patient has a OneToMany visit list; visits use ManyToOne patient/clinician references; invoice-to-visit and visit-to-appointment are OneToOne. SQL indexes support names, appointments and queue lookups.
