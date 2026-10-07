# Faraja group handover

## Yaliyomo / Contents

- `Source-Code/`: complete editable Java, HTML, CSS, JavaScript, SQL, tests, Maven configuration, Dockerfile and Windows launcher.
- `Documentation/Faraja-System-Documentation.docx`: editable analysis, requirements, architecture, data dictionary, API, testing, setup and limitations.
- `Presentation/Faraja-Presentation.pptx`: 12 editable slides with speaker notes.
- `Documentation/QUICK-START-SWAHILI.md`: instructions for another group member.
- `Documentation/DEPLOY-RAILWAY.md`: optional public hosting guide.
- `Documentation/PRESENTATION-NOTES.md`: demo script and viva questions.
- `Diagrams/`: editable Mermaid architecture, ER, workflow and sequence diagram sources.

## Current status

The project owner supplied Maven output showing 8 tests passed and confirmed the local patient, consultation, laboratory, pharmacy, payment, history and audit workflows. This package contains that tested source release; application logic has not been changed for documentation. Public hosting and a second-PC verification have not yet been completed.

## Group handover

Extract the entire ZIP first. Each member should follow QUICK-START-SWAHILI.md. Do not run from inside the ZIP. Fill names, registration numbers, course and lecturer in the report and presentation. Record actual contributions; suggested allocations are not evidence that a member performed work.

Source code and database contents are separate. The package contains no live patient database and no personal passwords. Flyway initializes a new database on first startup. Use fictional records for demonstrations.

Keep your currently working folder and database until the shared copy has been verified. Do not overwrite existing migrations or recreate the current database just to read these documents.
