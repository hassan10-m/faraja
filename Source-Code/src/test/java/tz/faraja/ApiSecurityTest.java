package tz.faraja;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tz.faraja.controller.ApiController;
import tz.faraja.service.ClinicService;
import tz.faraja.security.SecurityConfig;
import tz.faraja.repository.UserRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(ApiController.class) @Import(SecurityConfig.class) class ApiSecurityTest  {
    @Autowired MockMvc mvc;
    @MockBean ClinicService service;
    @MockBean UserRepository users;
    @Test void anonymousCannotReadPatients()throws Exception {
        mvc.perform(get("/api/patients")).andExpect(status().is3xxRedirection());
    }
    @Test @WithMockUser(roles="RECEPTIONIST") void receptionistCannotReadStaffAccounts()throws Exception {
        mvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(roles="ADMIN") void mutationRequiresCsrf()throws Exception {
        mvc.perform(post("/api/patients").contentType("application/json").content("{\"fullName\":\"Test\"}")).andExpect(status().isForbidden());
    }
}
