package tz.faraja;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import tz.faraja.service.ClinicService;
import tz.faraja.repository.*;
import tz.faraja.model.*;
import java.math.BigDecimal;
import java.util.*;
class ClinicServiceTest  {
    PatientRepository patients=mock(PatientRepository.class);
    MedicineRepository medicines=mock(MedicineRepository.class);
    PrescriptionRepository prescriptions=mock(PrescriptionRepository.class);
    InvoiceRepository invoices=mock(InvoiceRepository.class);
    PaymentRepository payments=mock(PaymentRepository.class);
    AuditLogRepository audits=mock(AuditLogRepository.class);
    ClinicService service=new ClinicService(mock(UserRepository.class),patients,mock(AppointmentRepository.class),mock(VisitRepository.class),mock(LabOrderRepository.class),medicines,prescriptions,invoices,payments,audits,mock(PasswordEncoder.class));
    org.springframework.security.core.Authentication actor=new UsernamePasswordAuthenticationToken("tester","unused");
    @Test void readPatientsWorksThroughClassProxy() {
        Patient p=new Patient();
        p.fullName="Test Patient";
        when(patients.findAll()).thenReturn(List.of(p));
        ProxyFactory factory=new ProxyFactory(service);
        factory.setProxyTargetClass(true);
        ClinicService proxy=(ClinicService)factory.getProxy();
        assertEquals(List.of(p),proxy.readPatients());
    }
    @Test void repeatedDispenseIsRejected() {
        Prescription p=new Prescription();
        p.dispensed=true;
        when(prescriptions.lockById(1L)).thenReturn(Optional.of(p));
        assertThrows(IllegalArgumentException.class,()->service.dispense(1L,actor));
        verifyNoInteractions(medicines);
    }
    @Test void dispensingReducesStockExactlyOnce() {
        Medicine m=new Medicine();
        m.id=2L;
        m.stock=10;
        Prescription p=new Prescription();
        p.medicine=m;
        p.quantity=3;
        when(prescriptions.lockById(1L)).thenReturn(Optional.of(p));
        when(medicines.deductStock(eq(2L),eq(3),any())).thenReturn(1);
        service.dispense(1L,actor);
        verify(medicines,times(1)).deductStock(eq(2L),eq(3),any());
        assertTrue(p.dispensed);
        assertThrows(IllegalArgumentException.class,()->service.dispense(1L,actor));
        verify(medicines,times(1)).deductStock(eq(2L),eq(3),any());
    }
    @Test void insufficientStockIsRejected() {
        Medicine m=new Medicine();
        m.id=2L;
        m.stock=1;
        Prescription p=new Prescription();
        p.medicine=m;
        p.quantity=3;
        when(prescriptions.lockById(1L)).thenReturn(Optional.of(p));
        when(medicines.lockById(2L)).thenReturn(Optional.of(m));
        assertThrows(IllegalArgumentException.class,()->service.dispense(1L,actor));
        assertEquals(1,m.stock);
        assertFalse(p.dispensed);
    }
    @Test void overpaymentIsRejected() {
        Invoice i=new Invoice();
        i.id=1L;
        i.amount=new BigDecimal("100");
        Payment paid=new Payment();
        paid.invoice=i;
        paid.amount=new BigDecimal("90");
        when(invoices.lockById(1L)).thenReturn(Optional.of(i));
        when(payments.findAll()).thenReturn(List.of(paid));
        assertThrows(IllegalArgumentException.class,()->service.pay(1L,new BigDecimal("11"),"CASH",null,actor));
        verify(payments,never()).save(any());
    }
}
