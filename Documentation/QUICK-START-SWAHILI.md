# Ku-run Faraja kwenye PC nyingine

## 1. Fungua source code

Download ZIP yote, bonyeza **Extract All**, kisha fungua folder `Faraja-Group-Package/Source-Code`. Hapa kuna `pom.xml`, `src`, na `START-FARAJA.cmd`. `pom.xml` ni Maven XML configuration; haifunguliwi browser ili ku-run mfumo.

## 2. Hakikisha programu zipo

Install Java JDK 17, Maven 3.9+ na MySQL Server 8.0. Fungua PowerShell mpya baada ya installation:

```powershell
java -version
mvn -version
Get-Service *mysql*
```

MySQL service iwe Running. Amri hizi zinaandikwa PowerShell, si kwenye `mysql>`.

## 3. Tengeneza database kwenye PC mpya pekee

Fungua MySQL Command Line Client kama administrator wa MySQL. Kwenye `mysql>`, run SQL hizi. **Badilisha `CHAGUA_PASSWORD_YAKO_HAPA` ndani ya quotes kabla ya ku-run.** Hii itakuwa password ya database ya faraja_app; si lazima iwe password ya ku-login Faraja.

```sql
CREATE DATABASE faraja CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'faraja_app'@'localhost' IDENTIFIED BY 'CHAGUA_PASSWORD_YAKO_HAPA';
GRANT ALL PRIVILEGES ON faraja.* TO 'faraja_app'@'localhost';
exit;
```

Kama database/user tayari ipo, tumia credentials zake; usifute database. Huhitaji ku-import V1 SQL mwenyewe: Flyway itaunda tables kwenye database mpya wakati app inaanza.

## 4. Anzisha mfumo

Double-click `START-FARAJA.cmd` ndani ya `Source-Code`:

1. MySQL username: `faraja_app`.
2. MySQL password: andika password uliyoweka hatua ya 3; haitakuwa visible wakati unaandika.
3. Kwa database mpya: chagua password mpya ya administrator yenye characters angalau 12.
4. Kwa database iliyotumika tayari: bonyeza Enter kwenye initial administrator prompt. Tumia password yako ya zamani ya ku-login.

Subiri mstari `Started FarajaApplication`. Fungua **http://localhost:8080**. Username ya kwanza ni `admin`. Usi-close terminal wakati unatumia app; Ctrl+C inasimamisha server.

## 5. Njia ya Maven bila launcher

Ndani ya folder yenye pom.xml, fungua PowerShell. Commands zote hapa ni PowerShell:

```powershell
$env:DB_USER="faraja_app"
$dbSecret=Read-Host "Andika MySQL password" -AsSecureString
$env:DB_PASSWORD=[System.Net.NetworkCredential]::new('', $dbSecret).Password
$env:ADMIN_USER="admin"
$adminSecret=Read-Host "Initial admin password kwa database mpya; Enter kama ipo" -AsSecureString
$env:ADMIN_PASSWORD=[System.Net.NetworkCredential]::new('', $adminSecret).Password
mvn spring-boot:run
```

Usiandike password badala ya maandishi ya prompt. Andika password **baada ya prompt kuonekana**. Environment variables hizi ni za terminal hiyo; ku-restart PC kunahitaji kuzi-set tena au kutumia launcher.

macOS/Linux: configure DB_USER, DB_PASSWORD, ADMIN_USER na ADMIN_PASSWORD katika mazingira ya shell yako, kisha run `mvn spring-boot:run` ndani ya Source-Code.

## 6. Tests na demo

Ndani ya Source-Code run `mvn test`. Expected baseline: 8 tests, 0 failures/errors. Tests hazitumii MySQL halisi; endelea na manual scenarios katika `TESTING.md`. Administrator anatengeneza accounts za staff katika settings/Team & audit. Tumia patient wa mfano na kila role husika.

## Matatizo ya kawaida

| Ujumbe | Hatua |
|---|---|
| java/mvn not recognized | Install JDK/Maven; fungua terminal mpya. |
| pom.xml missing | Nenda folder Source-Code yenye pom.xml. |
| Access denied faraja_app | Hakikisha DB_PASSWORD ni password ya MySQL user, si ya login ya Faraja; test kwa MySQL client. |
| Communications link failure | Hakikisha MySQL service iko Running na DB_URL ina host/port sahihi. |
| Port 8080 already in use | Close instance yako nyingine au set PORT tofauti. |
| Internal Server Error | Angalia faraja-error.log; tafuta Caused by. Usitume password au taarifa halisi za wagonjwa. |

## Kuhamisha data

Kutuma ZIP hakuhamishi records. Kwa demo mpya tengeneza fictional records kwenye kila PC. Kama mnahitaji kuhamisha demo database, tumia MySQL dump/restore kama ilivyoelezwa kwenye report. Hifadhi copy ya database kabla ya restore.
