package com.example.kbs_project;

import com.example.kbs_project.repository.LawRepository;
import com.example.kbs_project.service.TeleBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class KbsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(KbsProjectApplication.class, args);
    }

}
