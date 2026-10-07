package tz.faraja.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import tz.faraja.model.*;
import tz.faraja.repository.*;
import java.util.*;
import java.math.*;
import java.time.*;
@Service @Transactional public class ClinicService  {
    private final UserRepository users;
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final VisitRepository visits;
    private final LabOrderRepository labs;
    private final MedicineRepository medicines;
    private final PrescriptionRepository prescriptions;
    private final InvoiceRepository invoices;
    private final PaymentRepository payments;
    private final AuditLogRepository audits;
    private final PasswordEncoder encoder;
    public ClinicService(UserRepository users,PatientRepository patients,AppointmentRepository appointments,VisitRepository visits,LabOrderRepository labs,MedicineRepository medicines,PrescriptionRepository prescriptions,InvoiceRepository invoices,PaymentRepository payments,AuditLogRepository audits,PasswordEncoder encoder) {
        this.users=users;
        this.patients=patients;
        this.appointments=appointments;
        this.visits=visits;
        this.labs=labs;
        this.medicines=medicines;
        this.prescriptions=prescriptions;
        this.invoices=invoices;
        this.payments=payments;
        this.audits=audits;
        this.encoder=encoder;
    }
    public void log(Authentication a,String action,String target) {
        AuditLog l=new AuditLog();
        l.actor=a.getName();
        l.action=action;
        l.target=target;
        audits.save(l);
    }
    public Patient patient(Patient p,Authentication a) {
        if(p.fullName==null||p.fullName.isBlank())throw new IllegalArgumentException("Patient name required");
        p.id=null;
        p.number="FAR-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
        Patient saved=patients.save(p);
        log(a,"REGISTER_PATIENT",saved.number);
        return saved;
    }
    public Appointment appointment(Long patientId,Long doctorId,LocalDateTime at,String reason,Authentication a) {
        Appointment x=new Appointment();
        x.patient=patients.findById(patientId).orElseThrow(()->new IllegalArgumentException("Patient not found"));
        if(doctorId!=null)x.doctor=users.findById(doctorId).orElseThrow(()->new IllegalArgumentException("Doctor not found"));
        if(at==null||at.isBefore(LocalDateTime.now()))throw new IllegalArgumentException("Future appointment time required");
        x.scheduledAt=at;
        x.reason=reason;
        x.status="BOOKED";
        Appointment saved=appointments.save(x);
        log(a,"BOOK_APPOINTMENT",saved.id.toString());
        return saved;
    }
    public Visit visit(Long patientId,Long appointmentId,Authentication a) {
        Visit v=new Visit();
        v.patient=patients.findById(patientId).orElseThrow(()->new IllegalArgumentException("Patient not found"));
        if(appointmentId!=null) {
            Appointment ap=appointments.findById(appointmentId).orElseThrow(()->new IllegalArgumentException("Appointment not found"));
            if(!ap.patient.id.equals(patientId)||!ap.status.equals("BOOKED"))throw new IllegalArgumentException("Invalid appointment");
            ap.status="ARRIVED";
            v.appointment=ap;
        }
        v.status="WAITING";
        Visit saved=visits.save(v);
        log(a,"START_VISIT",saved.id.toString());
        return saved;
    }
    public Visit consult(Long id,Visit data,Authentication a) {
        if(data.complaint==null||data.complaint.isBlank()||data.diagnosis==null||data.diagnosis.isBlank())throw new IllegalArgumentException("Complaint and diagnosis required");
        Visit v=visits.findById(id).orElseThrow(()->new IllegalArgumentException("Visit not found"));
        if("CLOSED".equals(v.status))throw new IllegalArgumentException("Visit is closed");
        v.complaint=data.complaint;
        v.history=data.history;
        v.examination=data.examination;
        v.diagnosis=data.diagnosis;
        v.treatment=data.treatment;
        v.clinician=users.findByUsername(a.getName()).orElseThrow();
        v.status="CONSULTED";
        log(a,"CONSULT",id.toString());
        return v;
    }
    public Visit vitals(Long id,Visit data,Authentication a) {
        if(data.systolic!=null&&(data.systolic<30||data.systolic>300)||data.diastolic!=null&&(data.diastolic<10||data.diastolic>200)||data.pulse!=null&&(data.pulse<10||data.pulse>300)||data.temperature!=null&&(data.temperature.compareTo(new BigDecimal("25"))<0||data.temperature.compareTo(new BigDecimal("45"))>0)||data.weightKg!=null&&data.weightKg.signum()<=0)throw new IllegalArgumentException("Check vital sign values and units");
        Visit v=visits.findById(id).orElseThrow(()->new IllegalArgumentException("Visit not found"));
        v.systolic=data.systolic;
        v.diastolic=data.diastolic;
        v.pulse=data.pulse;
        v.temperature=data.temperature;
        v.weightKg=data.weightKg;
        log(a,"VITALS",id.toString());
        return v;
    }
    public LabOrder orderLab(Long visitId,String test,Authentication a) {
        if(test==null||test.isBlank())throw new IllegalArgumentException("Test required");
        LabOrder l=new LabOrder();
        l.visit=visits.findById(visitId).orElseThrow(()->new IllegalArgumentException("Visit not found"));
        l.testName=test;
        l.status="ORDERED";
        LabOrder saved=labs.save(l);
        log(a,"ORDER_LAB",saved.id.toString());
        return saved;
    }
    public LabOrder result(Long id,String result,Authentication a) {
        LabOrder l=labs.findById(id).orElseThrow(()->new IllegalArgumentException("Lab order not found"));
        if(result==null||result.isBlank())throw new IllegalArgumentException("Result required");
        l.resultText=result;
        l.status="COMPLETE";
        log(a,"LAB_RESULT",id.toString());
        return l;
    }
    public Medicine medicine(Medicine m,Authentication a) {
        if(m.name==null||m.name.isBlank()||m.unit==null||m.unit.isBlank()||m.stock<0||m.reorderLevel<0||m.unitPrice==null||m.unitPrice.signum()<0)throw new IllegalArgumentException("Check medicine fields");
        m.id=null;
        Medicine saved=medicines.save(m);
        log(a,"ADD_MEDICINE",saved.id.toString());
        return saved;
    }
    public Prescription prescribe(Long visitId,Long medicineId,int qty,String instructions,Authentication a) {
        if(qty<=0)throw new IllegalArgumentException("Quantity must be positive");
        Prescription p=new Prescription();
        p.visit=visits.findById(visitId).orElseThrow(()->new IllegalArgumentException("Visit not found"));
        p.medicine=medicines.findById(medicineId).orElseThrow(()->new IllegalArgumentException("Medicine not found"));
        p.quantity=qty;
        p.instructions=instructions;
        Prescription saved=prescriptions.save(p);
        log(a,"PRESCRIBE",saved.id.toString());
        return saved;
    }
    public Prescription dispense(Long id,Authentication a) {
        Prescription p=prescriptions.lockById(id).orElseThrow(()->new IllegalArgumentException("Prescription not found"));
        if(p.dispensed)throw new IllegalArgumentException("Already dispensed");
        // Conditional UPDATE checks and deducts stock atomically, including concurrent dispensing.
        if(medicines.deductStock(p.medicine.id,p.quantity,LocalDate.now())!=1)throw new IllegalArgumentException("Insufficient stock or medicine expired");
        p.dispensed=true;
        log(a,"DISPENSE",id.toString());
        return p;
    }
    public Invoice invoice(Long visitId,BigDecimal amount,String description,Authentication a) {
        if(amount==null||amount.signum()<0||description==null||description.isBlank())throw new IllegalArgumentException("Check invoice fields");
        if(invoices.findAll().stream().anyMatch(i->i.visit.id.equals(visitId)))throw new IllegalArgumentException("Visit already invoiced");
        Invoice i=new Invoice();
        i.visit=visits.findById(visitId).orElseThrow(()->new IllegalArgumentException("Visit not found"));
        i.amount=amount;
        i.description=description;
        Invoice saved=invoices.save(i);
        log(a,"INVOICE",saved.id.toString());
        return saved;
    }
    public Payment pay(Long invoiceId,BigDecimal amount,String method,String reference,Authentication a) {
        Invoice i=invoices.lockById(invoiceId).orElseThrow(()->new IllegalArgumentException("Invoice not found"));
        if(amount==null||amount.signum()<=0||method==null||method.isBlank())throw new IllegalArgumentException("Check payment fields");
        BigDecimal paid=payments.findAll().stream().filter(p->p.invoice.id.equals(invoiceId)).map(p->p.amount).reduce(BigDecimal.ZERO,BigDecimal::add);
        if(paid.add(amount).compareTo(i.amount)>0)throw new IllegalArgumentException("Payment exceeds balance");
        Payment p=new Payment();
        p.invoice=i;
        p.amount=amount;
        p.method=method;
        p.reference=reference;
        Payment saved=payments.save(p);
        log(a,"PAYMENT",saved.id.toString());
        return saved;
    }
    public User createUser(String username,String password,String fullName,String role,Authentication a) {
        if(!Set.of("ADMIN","RECEPTIONIST","DOCTOR","NURSE","LAB","PHARMACIST","CASHIER").contains(role)||username==null||username.isBlank()||password==null||password.length()<12||fullName==null||fullName.isBlank())throw new IllegalArgumentException("Check user fields; password minimum 12 characters");
        if(users.findByUsername(username).isPresent())throw new IllegalArgumentException("Username already exists");
        User u=new User();
        u.username=username;
        u.password=encoder.encode(password);
        u.fullName=fullName;
        u.role=role;
        u.active=true;
        User saved=users.save(u);
        log(a,"CREATE_USER",saved.id.toString());
        return saved;
    }
    @Transactional(readOnly=true) public Object readDashboard() {
        return Map.of("patients",patients.count(),"appointments",appointments.count(),"queue",visits.findByStatusOrderByCreatedAtAsc("WAITING").size(),"labOrders",labs.count(),"lowStock",medicines.findAll().stream().filter(m->m.stock<=m.reorderLevel).count());
    }
    @Transactional(readOnly=true) public Object readPatients() {
        return patients.findAll();
    }
    @Transactional(readOnly=true) public Object readHistory(Long id) {
        return visits.findByPatientIdOrderByCreatedAtDesc(id);
    }
    @Transactional(readOnly=true) public Object readAppointments() {
        return appointments.findAll();
    }
    @Transactional(readOnly=true) public Object readVisits(Authentication a) {
        boolean clinical=a.getAuthorities().stream().anyMatch(r->Set.of("ROLE_ADMIN","ROLE_DOCTOR","ROLE_NURSE").contains(r.getAuthority()));
        if(clinical)return visits.findAll();
        return visits.findAll().stream().map(v->Map.of("id",v.id,"patient",Map.of("id",v.patient.id,"fullName",v.patient.fullName),"status",v.status,"createdAt",v.createdAt)).toList();
    }
    @Transactional(readOnly=true) public Object readQueue() {
        return visits.findByStatusOrderByCreatedAtAsc("WAITING");
    }
    @Transactional(readOnly=true) public Object readLabs() {
        return labs.findAll();
    }
    @Transactional(readOnly=true) public Object readMedicines() {
        return medicines.findAll();
    }
    @Transactional(readOnly=true) public Object readPrescriptions() {
        return prescriptions.findAll();
    }
    @Transactional(readOnly=true) public Object readInvoices() {
        return invoices.findAll();
    }
    @Transactional(readOnly=true) public Object readPayments() {
        return payments.findAll();
    }
    @Transactional(readOnly=true) public Object readReport() {
        return Map.of("patients",patients.count(),"visits",visits.count(),"invoices",invoices.count(),"billed",invoices.findAll().stream().map(i->i.amount).reduce(BigDecimal.ZERO,BigDecimal::add),"collected",payments.findAll().stream().map(p->p.amount).reduce(BigDecimal.ZERO,BigDecimal::add));
    }
    @Transactional(readOnly=true) public Object readUsers() {
        return users.findAll();
    }
    @Transactional(readOnly=true) public Object readAudit() {
        return audits.findAll();
    }
    public Medicine receiveStock(Long id,int quantity,String reference,Authentication a) {
        if(quantity<=0)throw new IllegalArgumentException("Positive quantity required");
        Medicine m=medicines.lockById(id).orElseThrow(()->new IllegalArgumentException("Medicine not found"));
        m.stock=Math.addExact(m.stock,quantity);
        log(a,"RECEIVE_STOCK",id+" qty="+quantity+" ref="+reference);
        return m;
    }
    public void changePassword(String current,String replacement,Authentication a) {
        User u=users.findByUsername(a.getName()).orElseThrow();
        if(!encoder.matches(current,u.password))throw new IllegalArgumentException("Current password is incorrect");
        u.password=encoder.encode(replacement);
        log(a,"CHANGE_PASSWORD",u.username);
    }
    public Appointment cancelAppointment(Long id,Authentication a) {
        Appointment x=appointments.findById(id).orElseThrow(()->new IllegalArgumentException("Appointment not found"));
        if(!"BOOKED".equals(x.status))throw new IllegalArgumentException("Only booked appointments can be cancelled");
        x.status="CANCELLED";
        log(a,"CANCEL_APPOINTMENT",id.toString());
        return x;
    }
    public Visit closeVisit(Long id,Authentication a) {
        Visit v=visits.findById(id).orElseThrow(()->new IllegalArgumentException("Visit not found"));
        if(!"CONSULTED".equals(v.status))throw new IllegalArgumentException("Consultation must be recorded first");
        v.status="CLOSED";
        log(a,"CLOSE_VISIT",id.toString());
        return v;
    }
}
