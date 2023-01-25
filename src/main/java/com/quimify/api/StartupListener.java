package com.quimify.api;

import com.quimify.api.inorganic.InorganicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// This class contains the code that Spring Boot will run after the server has fully started.

@Component
@Transactional
class StartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    InorganicService inorganicService; // Inorganic compounds logic

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        inorganicService.refreshAutocompletion();
    }

}