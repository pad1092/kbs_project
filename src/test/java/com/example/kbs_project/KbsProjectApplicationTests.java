package com.example.kbs_project;

import com.example.kbs_project.repository.LawRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class KbsProjectApplicationTests {
    @Autowired
    LawRepository lawRepository;

    @Test
    void contextLoads() {
    }
    @Test
    @Transactional
    void testLawRepo(){
//        lawRepository.findAll().forEach(law -> {
//            System.out.println(law.toString());
//        });
        System.out.println(lawRepository.getById(1));
    }
}
