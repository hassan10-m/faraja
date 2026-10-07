# Update an existing Faraja installation

1. Stop the running app with Ctrl+C. Keep the existing project folder as a backup.
2. Extract this ZIP to a separate folder. It contains the complete updated source, not just a patch.
3. Use the same MySQL database and the same faraja_app credentials. Do not delete the database or Flyway history. This release preserves the existing V1 migration unchanged.
4. Inside the new `faraja-dispensary` folder, run `mvn test`. The tests use mocks and do not modify MySQL.
5. Double-click `START-FARAJA.cmd`. Enter `faraja_app`, then its actual MySQL password. Password input is hidden. Since your administrator already exists, press Enter at the initial administrator password prompt.
6. Open http://localhost:8080 and press Ctrl+F5 to reload JavaScript and CSS. Use your existing admin login.
7. Follow TESTING.md. Keep the old folder until the new release passes the workflow checks.

The launcher starts in its own folder so commands do not accidentally run from C:\. It prompts each time rather than embedding your password in source code. `mvn spring-boot:run` still works with environment variables, exactly as before.

## Fixes and additions

- Controller reads go through transactional service methods; no public repository field access through Spring proxies.
- Fetch sends the masked CSRF token returned by Spring; HTML login and logout retain CSRF protection.
- Patient IDs are visible, and linked forms provide readable patient/visit/medicine/invoice selectors.
- Blank optional values are sent as JSON null. Decimal inputs accept decimal amounts and measurements.
- Role-aware navigation and action forms, patient search and clinical history, appointment cancellation and visit closure.
- Nurse vitals and doctor consultation are separate so saving consultation does not erase nurse measurements.
- Stock receipts, atomic stock deductions, payment locking, invoice paid/balance columns and printable receipts/reports.
- Self-service password change and Bean Validation for key request fields.
- Service regression tests, security tests, portable launcher and Docker packaging instructions.

## Verification status

This revision passed JavaScript syntax checking and 45 role/page render checks using mocked API responses in the creation environment. Java/Maven tests and a real MySQL end-to-end run could not be executed there because the build tools/dependencies were unavailable and the dependency download was blocked. The earlier version was started successfully on the user's Windows PC; that does not verify this revision. Run the included tests and acceptance workflow before presenting it as verified. No public deployment has been performed.
