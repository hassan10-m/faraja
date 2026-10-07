# Editable diagrams

Open the .mmd files in a Mermaid-capable Markdown editor or renderer. They contain no credentials or patient records. `erd.mmd` shows key relationships, not every column; use the Word data dictionary and SQL migration for the full schema. Audit actor is a text snapshot and has no foreign-key link to users.

The workflow diagram shows a demonstration sequence. The application does not enforce every arrow as a clinical dependency: visits can be closed without all tests or payments completing. Architecture and sequence diagrams show database operations passing through the Java backend.
