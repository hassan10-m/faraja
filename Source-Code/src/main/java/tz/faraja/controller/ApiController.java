package tz.faraja.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import tz.faraja.service.ClinicService;
import tz.faraja.model.*;
import java.math.*;
import java.time.*;
import java.util.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
@RestController @RequestMapping("/api") @EnableMethodSecurity public class ApiController  {
    private final ClinicService s;
    public ApiController(ClinicService s) {
        this.s=s;
    }
    public record Booking(@NotNull @Positive Long patientId,Long doctorId,@NotNull @Future LocalDateTime scheduledAt,@Size(max=500) String reason) {
    }
    public record Start(@NotNull @Positive Long patientId,Long appointmentId) {
    }
    public record LabRequest(@NotNull @Positive Long visitId,@NotBlank @Size(max=120) String testName) {
    }
    public record Result(@NotBlank String resultText) {
    }
    public record Rx(@NotNull @Positive Long visitId,@NotNull @Positive Long medicineId,@Min(1) int quantity,@NotBlank @Size(max=500) String instructions) {
    }
    public record Bill(@NotNull @Positive Long visitId,@NotNull @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal amount,@NotBlank @Size(max=500) String description) {
    }
    public record Pay(@NotNull @Positive Long invoiceId,@NotNull @DecimalMin(value="0.00",inclusive=false) @Digits(integer=10,fraction=2) BigDecimal amount,@NotBlank @Pattern(regexp="CASH|MOBILE_MONEY|BANK") String method,@Size(max=100) String reference) {
    }
    public record NewUser(@NotBlank @Size(max=80) String username,@NotBlank @Size(min=12,max=72) String password,@NotBlank @Size(max=120) String fullName,@NotBlank String role) {
    }
    @GetMapping("/csrf") public Object csrf(org.springframework.security.web.csrf.CsrfToken token) {
        return Map.of("token",token.getToken());
    }
    @GetMapping("/me") public Object me(Authentication a) {
        return Map.of("username",a.getName(),"roles",a.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
    @GetMapping("/dashboard") public Object dashboard() {
        return s.readDashboard();
    }
    @GetMapping("/patients") @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','LAB','PHARMACIST','CASHIER')") public Object patients() {
        return s.readPatients();
    }
    @PostMapping("/patients") @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')") public Object patient(@jakarta.validation.Valid @RequestBody Patient p,Authentication a) {
        return s.patient(p,a);
    }
    @GetMapping("/patients/{id}/history") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')") public Object history(@PathVariable Long id) {
        return s.readHistory(id);
    }
    @GetMapping("/appointments") @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')") public Object appointments() {
        return s.readAppointments();
    }
    @PostMapping("/appointments") @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')") public Object appointment(@Valid @RequestBody Booking b,Authentication a) {
        return s.appointment(b.patientId,b.doctorId,b.scheduledAt,b.reason,a);
    }
    @GetMapping("/visits") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','LAB','PHARMACIST','CASHIER','RECEPTIONIST')") public Object visits(Authentication a) {
        return s.readVisits(a);
    }
    @GetMapping("/queue") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','RECEPTIONIST')") public Object queue() {
        return s.readQueue();
    }
    @PostMapping("/visits") @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','NURSE')") public Object visit(@Valid @RequestBody Start b,Authentication a) {
        return s.visit(b.patientId,b.appointmentId,a);
    }
    @PutMapping("/visits/{id}/consultation") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')") public Object consult(@PathVariable Long id,@RequestBody Visit v,Authentication a) {
        return s.consult(id,v,a);
    }
    @PutMapping("/visits/{id}/vitals") @PreAuthorize("hasAnyRole('ADMIN','NURSE','DOCTOR')") public Object vitals(@PathVariable Long id,@RequestBody Visit v,Authentication a) {
        return s.vitals(id,v,a);
    }
    @GetMapping("/labs") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','LAB','NURSE')") public Object labs() {
        return s.readLabs();
    }
    @PostMapping("/labs") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')") public Object lab(@Valid @RequestBody LabRequest b,Authentication a) {
        return s.orderLab(b.visitId,b.testName,a);
    }
    @PutMapping("/labs/{id}/result") @PreAuthorize("hasAnyRole('ADMIN','LAB')") public Object result(@PathVariable Long id,@Valid @RequestBody Result b,Authentication a) {
        return s.result(id,b.resultText,a);
    }
    @GetMapping("/medicines") public Object medicines() {
        return s.readMedicines();
    }
    @PostMapping("/medicines") @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')") public Object medicine(@jakarta.validation.Valid @RequestBody Medicine m,Authentication a) {
        return s.medicine(m,a);
    }
    @GetMapping("/prescriptions") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST')") public Object prescriptions() {
        return s.readPrescriptions();
    }
    @PostMapping("/prescriptions") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')") public Object prescribe(@Valid @RequestBody Rx b,Authentication a) {
        return s.prescribe(b.visitId,b.medicineId,b.quantity,b.instructions,a);
    }
    @PutMapping("/prescriptions/{id}/dispense") @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')") public Object dispense(@PathVariable Long id,Authentication a) {
        return s.dispense(id,a);
    }
    @GetMapping("/invoices") @PreAuthorize("hasAnyRole('ADMIN','CASHIER','RECEPTIONIST')") public Object invoices() {
        return s.readInvoices();
    }
    @PostMapping("/invoices") @PreAuthorize("hasAnyRole('ADMIN','CASHIER')") public Object invoice(@Valid @RequestBody Bill b,Authentication a) {
        return s.invoice(b.visitId,b.amount,b.description,a);
    }
    @GetMapping("/payments") @PreAuthorize("hasAnyRole('ADMIN','CASHIER')") public Object payments() {
        return s.readPayments();
    }
    @PostMapping("/payments") @PreAuthorize("hasAnyRole('ADMIN','CASHIER')") public Object pay(@Valid @RequestBody Pay b,Authentication a) {
        return s.pay(b.invoiceId,b.amount,b.method,b.reference,a);
    }
    @GetMapping("/reports/summary") @PreAuthorize("hasAnyRole('ADMIN','CASHIER')") public Object report() {
        return s.readReport();
    }
    @GetMapping("/admin/users") @PreAuthorize("hasRole('ADMIN')") public Object users() {
        return s.readUsers();
    }
    @PostMapping("/admin/users") @PreAuthorize("hasRole('ADMIN')") public Object user(@Valid @RequestBody NewUser b,Authentication a) {
        return s.createUser(b.username,b.password,b.fullName,b.role,a);
    }
    @GetMapping("/admin/audit") @PreAuthorize("hasRole('ADMIN')") public Object audit() {
        return s.readAudit();
    }
    @PostMapping("/account/password") public Object password(@jakarta.validation.Valid @RequestBody tz.faraja.dto.AccountPassword b,Authentication a) {
        s.changePassword(b.currentPassword(),b.newPassword(),a);
        return Map.of("message","Password updated");
    }
    @PostMapping("/medicines/{id}/stock") @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')") public Object stock(@PathVariable Long id,@jakarta.validation.Valid @RequestBody tz.faraja.dto.StockReceipt b,Authentication a) {
        return s.receiveStock(id,b.quantity(),b.reference(),a);
    }
    @PutMapping("/appointments/{id}/cancel") @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')") public Object cancel(@PathVariable Long id,Authentication a) {
        return s.cancelAppointment(id,a);
    }
    @PutMapping("/visits/{id}/close") @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')") public Object close(@PathVariable Long id,Authentication a) {
        return s.closeVisit(id,a);
    }
}
