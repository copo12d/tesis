package com.tesisUrbe.backend.common.init;

import com.tesisUrbe.backend.entities.enums.RoleList;
import com.tesisUrbe.backend.entities.account.Role;
import com.tesisUrbe.backend.entities.account.User;
import com.tesisUrbe.backend.entities.setting.ReportSetting;
import com.tesisUrbe.backend.entities.setting.UbicationSetting;
import com.tesisUrbe.backend.entities.setting.UniversitySetting;
import com.tesisUrbe.backend.settingsManagement.repository.ReportSettingRepository;
import com.tesisUrbe.backend.settingsManagement.repository.UbicationSettingRepository;
import com.tesisUrbe.backend.settingsManagement.repository.UniversitySettingRepository;
import com.tesisUrbe.backend.usersManagement.repository.UserRepository;
import com.tesisUrbe.backend.usersManagement.services.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SuperAdminInitializer implements CommandLineRunner {

    @Value("${superuser.name}")
    private String superuserName;

    @Value("${superuser.email}")
    private String superuserEmail;

    @Value("${superuser.password}")
    private String superuserPassword;

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ReportSettingRepository reportRepo;
    private final UniversitySettingRepository universityRepo;
    private final UbicationSettingRepository ubicationRepo;

    public SuperAdminInitializer(UserRepository userRepository,
                                 RoleService roleService,
                                 PasswordEncoder passwordEncoder,
                                 ReportSettingRepository reportRepo,
                                 UniversitySettingRepository universityRepo,
                                 UbicationSettingRepository ubicationRepo) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.reportRepo = reportRepo;
        this.universityRepo = universityRepo;
        this.ubicationRepo = ubicationRepo;
    }

    @Override
    public void run(String... args) {
        if (roleService.count() == 0) {
            for (RoleList roleName : RoleList.values()) {
                Role role = new Role();
                role.setName(roleName);
                roleService.save(role);
            }
            log.info("✅ Roles base creados");
        } else {
            log.info("ℹ Roles ya existen, no se crean nuevamente");
        }

        if (userRepository.count() == 0) {
            Role superRole = roleService.findByName(RoleList.ROLE_SUPERUSER)
                    .orElseThrow(() -> new IllegalStateException("Rol SUPERUSER no encontrado"));

            User superAdmin = new User();
            superAdmin.setFullName("Super Admin");
            superAdmin.setUserName(superuserName);
            superAdmin.setEmail(superuserEmail);
            superAdmin.setPassword(passwordEncoder.encode(superuserPassword));
            superAdmin.setRole(superRole);
            superAdmin.setVerified(false);
            superAdmin.setAccountLocked(false);
            superAdmin.setUserLocked(false);
            superAdmin.setDeleted(false);
            userRepository.save(superAdmin);

            log.info("✅ Superusuario inicial creado: {} / {}", superuserName, superuserPassword);
        } else {
            log.info("ℹ Ya existen usuarios, no se crea superusuario por defecto");
        }

        if (reportRepo.count() == 0) {
            ReportSetting report = new ReportSetting();
            report.setId(1L);
            report.setTableHeaderColor("#0000FF");
            report.setHeaderTextColor("#FFFFFF");
            report.setRecordColor("#000000");
            reportRepo.save(report);
            log.info("✅ Estilos de reporte inicializados");
        } else {
            log.info("ℹ Estilos de reporte ya existen, no se inicializan nuevamente");
        }

        if (universityRepo.count() == 0) {
            UniversitySetting university = new UniversitySetting();
            university.setId(1L);
            university.setLegalName("Universidad Venezuela");

            UniversitySetting.TaxId taxId = new UniversitySetting.TaxId();
            taxId.setType("J");
            taxId.setNumber("0123456789-1");
            university.setTaxId(taxId);

            university.setAddress1("Dirección: Av. 16 GUAJIRA, AL LADO DE PLAZA DE TOROS.");
            university.setAddress2("Maracaibo, Edo. Zulia, Municipio Juana de Avila.");
            university.setAddress3("Venezuela. Código Postal: 4005");
            university.setPhone("Teléfonos: +582610000000 / +582610000000");
            university.setEmail("example@universidad.edu.ve");
            university.setLogoPath("/images/logo.png");

            universityRepo.save(university);
            log.info("✅ Datos institucionales inicializados");
        } else {
            log.info("ℹ Datos institucionales ya existen, no se inicializan nuevamente");
        }

        if (ubicationRepo.count() == 0) {
            UbicationSetting ubication = new UbicationSetting();
            ubication.setId(1L);
            ubication.setLatitude(10.6941532);
            ubication.setLongitude(-71.6343502);
            ubication.setMapZoom(20);
            ubicationRepo.save(ubication);
            log.info("✅ Ubicación institucional inicializada");
        } else {
            log.info("ℹ Ubicación institucional ya existe, no se inicializa nuevamente");
        }
    }
}