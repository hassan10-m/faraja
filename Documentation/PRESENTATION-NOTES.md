# Group presentation and viva

Duration: about 10–12 minutes. Fill actual names and allocate speaking sections before presenting. The PowerPoint has speaker notes explaining caveats and code points.

## Suggested order

1. Problem and objectives (1 minute): explain registration-to-payment coordination without claiming an unperformed field study.
2. Stack and architecture (2 minutes): HTML → Fetch → Controller → Service → Repository → MySQL.
3. Demonstration (4 minutes): use one fictional patient; register, start visit, vitals, consultation, test/result, prescription/dispensing, invoice/payment, history.
4. Code and relationships (2 minutes): show ApiController, ClinicService, Patient entity/repository and V1 migration.
5. Tests, portability and limits (1–3 minutes): show real Maven output, explain the shared setup instructions and state actual hosting status.

## Before the demonstration

- Start MySQL and Faraja before your speaking slot. Keep the server terminal open.
- Use fictional patient data; prepare staff accounts and a medicine with adequate stock and a future expiry date.
- Rehearse using the intended role accounts, not only administrator.
- Have the source code and report available offline. Maven's first build needs dependency downloads.
- Add your own genuine screenshots of the working app to the presentation if required. No screenshots have been fabricated in this package.

## Questions and answers to understand

**Why Java and MySQL?** They match the course stack. Java implements the business rules, while MySQL stores relational records permanently.

**Is the frontend static?** Spring serves normal HTML files, but JavaScript Fetch requests read and write actual backend data dynamically.

**What does a repository do?** It exposes persistence operations. Spring Data supplies implementations; Hibernate maps entity objects to database tables.

**Why a service layer?** It keeps workflow rules and transaction boundaries in one place instead of mixing them with HTTP handling.

**How are duplicate dispensing and overspending controlled?** Prescription/invoice rows are locked during relevant updates. Stock deduction is conditional on available quantity, and payments cannot exceed remaining balance. Explain these by opening the actual methods.

**What is a foreign key?** A constraint relating a child record to a valid parent, for example visits.patient_id to patients.id.

**How does a new PC get tables?** Flyway applies versioned SQL to a new database. Hibernate validates the resulting schema on startup.

**Does the ZIP copy patients?** No. Code and stored database records are separate. A database dump/restore is a separate operation.

**Are all tests integration tests?** No. The eight automated tests cover mocked service and security scenarios. Local manual checks were reported by the project owner. Real database concurrency, load and hosted checks are separate.

**What remains to improve?** Department-specific response DTOs, medicine batches, invoice lines, advanced reporting, stronger account administration, and production operations.

## Evidence to collect

Save a screenshot of your actual BUILD SUCCESS, the fictional patient history, stock before/after, paid invoice and audit entry. Keep the tested version and date. Record second-PC and public URL results only after those checks are performed.
