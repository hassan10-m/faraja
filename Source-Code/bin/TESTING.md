# Acceptance checklist

Use fictional patients. Run `mvn test` first: five service tests cover proxied reads, repeated dispensing, stock validation and overpayment; three MockMvc tests cover anonymous access, role restrictions and CSRF.

Then run the real MySQL application and record PASS/FAIL for each step:

1. Login as administrator; dashboard and Patients load without 500 errors. Incorrect password is rejected.
2. Register Test Patient, refresh Patients, confirm it remains. Blank name is rejected.
3. Create staff users for RECEPTIONIST, DOCTOR, NURSE, LAB, PHARMACIST and CASHIER in Settings. Use 12+ character passwords.
4. Receptionist books a future appointment and starts the patient's visit. Queue shows it. Cancel a different booked appointment.
5. Nurse records temperature 36.7, weight 60.5, blood pressure 120/80 and pulse 70. Doctor records complaint, diagnosis and plan. Verify nurse measurements remain.
6. Doctor orders a lab test. LAB account records a result. Doctor can read it. A receptionist cannot POST results.
7. Pharmacist adds a medicine with stock 10, future expiry and price 100.50, then receives 5 units. Doctor prescribes 3 units. Pharmacist dispenses; stock becomes 12. The same prescription cannot be dispensed twice. Insufficient or expired stock is rejected.
8. Cashier creates a visit invoice for TZS 1000. Record 400, then 600; balance is zero. Overpayment and negative payment are rejected. Print a receipt.
9. Doctor opens Patients -> History and finds the consultation; closes the consulted visit. Reports show the invoice/payment totals.
10. Admin audit log shows writes and actors. Settings password change rejects a wrong current password and accepts a valid change; next login uses the new password.
11. Stop/restart the app, confirm records remain. Open browser dev tools: requests use /api; there is no browser-to-MySQL connection.
12. Open on a narrow phone-sized window and check navigation, forms and horizontal table scrolling.

Remaining verification: multi-user MySQL concurrency tests, real-browser automation and deployment validation must be carried out on a configured host. The included mocked tests do not establish production readiness.
