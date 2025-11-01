//package com.tesisUrbe.backend.common.init;
//
//import com.tesisUrbe.backend.entities.account.User;
//import com.tesisUrbe.backend.entities.solidWaste.BatchEnc;
//import com.tesisUrbe.backend.entities.solidWaste.BatchReg;
//import com.tesisUrbe.backend.entities.solidWaste.Container;
//import com.tesisUrbe.backend.entities.solidWaste.ContainerType;
//import com.tesisUrbe.backend.solidWasteManagement.enums.BatchStatus;
//import com.tesisUrbe.backend.solidWasteManagement.enums.ContainerStatus;
//import com.tesisUrbe.backend.solidWasteManagement.repository.BatchEncRepository;
//import com.tesisUrbe.backend.solidWasteManagement.repository.BatchRegRepository;
//import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerRepository;
//import com.tesisUrbe.backend.solidWasteManagement.repository.ContainerTypeRepository;
//import com.tesisUrbe.backend.usersManagement.repository.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.DayOfWeek;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Component
//@Slf4j
//public class BatchRegDataSeeder implements CommandLineRunner {
//
//    @Autowired
//    private ContainerTypeRepository containerTypeRepo;
//
//    @Autowired
//    private ContainerRepository containerRepo;
//
//    @Autowired
//    private BatchEncRepository batchEncRepo;
//
//    @Autowired
//    private BatchRegRepository batchRegRepo;
//
//    @Autowired
//    private UserRepository userRepo;
//
//    @Override
//    public void run(String... args) {
//        if (batchRegRepo.count() > 0) {
//            log.info("ℹ Datos de prueba ya existen, no se generan nuevamente");
//            return;
//        }
//
//        User creator = userRepo.findByUserName("superuser")
//                .orElseThrow(() -> new IllegalStateException("Superusuario no encontrado"));
//
//        String[] tipos = {"papel", "vidrio", "plastico", "organico", "metal", "carton"};
//        Map<String, ContainerType> tipoMap = new HashMap<>();
//
//        for (String tipo : tipos) {
//            ContainerType ct = new ContainerType();
//            ct.setName(tipo);
//            ct.setDescription("Contenedor de " + tipo);
//            ct.setDeleted(false);
//            containerTypeRepo.save(ct);
//            tipoMap.put(tipo, ct);
//        }
//
//        List<Container> contenedores = new ArrayList<>();
//        for (int i = 0; i < 12; i++) {
//            String tipo = tipos[i % tipos.length];
//            Container c = Container.builder()
//                    .serial("C-" + (100 + i))
//                    .latitude(BigDecimal.valueOf(10.69 + i * 0.001))
//                    .longitude(BigDecimal.valueOf(-71.63 + i * 0.001))
//                    .capacity(BigDecimal.valueOf(100))
//                    .status(ContainerStatus.AVAILABLE)
//                    .containerType(tipoMap.get(tipo))
//                    .createdBy(creator)
//                    .deleted(false)
//                    .build();
//            containerRepo.save(c);
//            contenedores.add(c);
//        }
//
//        BatchEnc lote = new BatchEnc();
//        lote.setCreationDate(LocalDateTime.now().minusDays(7));
//        lote.setTotalWeight(BigDecimal.ZERO);
//        lote.setStatus(BatchStatus.IN_PROGRESS);
//        lote.setCreatedBy(creator);
//        lote.setDeleted(false);
//        batchEncRepo.save(lote);
//
//        LocalDateTime baseDate = LocalDateTime.now().withHour(10);
//        Random rand = new Random();
//
//        for (int i = 0; i < 50; i++) {
//            LocalDateTime fecha = baseDate.minusDays(rand.nextInt(7));
//            DayOfWeek dia = fecha.getDayOfWeek();
//
//            if (dia == DayOfWeek.WEDNESDAY) {
//                continue;
//            }
//
//            BatchReg reg = new BatchReg();
//            reg.setCollectionDate(fecha);
//            reg.setWeight(BigDecimal.valueOf(10 + rand.nextInt(90)));
//            reg.setContainer(contenedores.get(rand.nextInt(contenedores.size())));
//            reg.setBatchEnc(lote);
//            reg.setCreatedBy(creator);
//            reg.setDeleted(false);
//            batchRegRepo.save(reg);
//        }
//
//        log.info("✅ Datos ficticios generados para probar el resumen diario (miércoles excluido)");
//    }
//}
