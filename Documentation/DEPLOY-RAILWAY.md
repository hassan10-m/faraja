# Optional public deployment — Railway

Status: deployment instructions only. No account, hosting resources, domain or public application have been provisioned by this handover. Hosting may require a paid plan or available credits; check your account before provisioning.

## 1. Create a separate project

Sign in to your own Railway account. Create a new project named Faraja and add a MySQL service. Use the application's tested MySQL 8.0 baseline where available. Verify the database image/version before loading data; do not downgrade a populated database. Ensure the database has persistent storage. Use a fresh empty database for the first hosted deployment.

Add a separate empty application service named `Faraja-Web` in the same project. Do not deploy this application over another existing project.

## 2. Configure application variables

Set these on **Faraja-Web**, not on the database service. Reference your actual MySQL service name (the examples assume it is `MySQL`):

```text
DB_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
DB_USER=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
ADMIN_USER=admin
ADMIN_PASSWORD=<choose a new unique password of at least 12 characters>
SERVER_SERVLET_SESSION_COOKIE_SECURE=true
SERVER_FORWARD_HEADERS_STRATEGY=framework
```

Replace the ADMIN_PASSWORD placeholder with your own value in the hosting dashboard. Do not commit it or place it in the shared package. Use Railway reference variables so credentials remain in environment configuration. DB_URL must use JDBC MySQL syntax; a plain mysql:// connection URL is not a JDBC URL. Use the private host only between services in the same Railway environment.

Spring Boot reads PORT when supplied; otherwise the application listens on 8080. Initial administrator creation occurs only if the account does not already exist. Changing ADMIN_PASSWORD later does not reset that account.

## 3. Upload and deploy source

Install the official Railway CLI using its current official installation instructions. The CLI is a deployment tool, not part of the application's runtime stack. In Windows PowerShell open the included `Source-Code` folder containing the Dockerfile and pom.xml:

```powershell
railway login
railway link
railway up
```

When linking, select the new Faraja project/environment and Faraja-Web application service. If your installed command is a .cmd launcher, use `railway.cmd` instead. Railway should detect the Dockerfile: it compiles and runs tests in a Maven build stage, then starts the JAR with Java 17. Inspect build and runtime logs. Expect migration success followed by Started FarajaApplication. Do not repeatedly reset the database to hide migration errors.

Alternative: put Source-Code at the root of a private Git repository and connect that repository to Faraja-Web. Do not include passwords, dumps, logs or target files.

## 4. Add and verify a public URL

In the application service's Networking settings, generate a domain and ensure the target port matches the application's listening port. Open the HTTPS URL and sign in. Confirm:

- login and logout, CSRF-protected forms and role restrictions;
- create a fictional patient, refresh and find it;
- consultation, laboratory result, dispensing and stock change;
- invoice, partial payments, final balance, history and audit;
- persistence after an application restart.

Run a single application instance for this release: sessions are stored in application memory. A restart can require users to log in again. Persistent clinical data belongs in MySQL, not the container filesystem. Configure backups and test restoration separately.

This is a university demonstration release. The report lists outstanding privacy, administration and operational improvements. Do not present a successful build as proof of public deployment; record the actual working HTTPS URL after these checks.

## Official references

- https://docs.railway.com/guides/spring-boot
- https://docs.railway.com/databases/mysql
- https://docs.railway.com/variables
- https://docs.railway.com/networking/public-networking
- https://docs.railway.com/guides/cli

Provider UI and plans can change; consult these official instructions if labels differ.
