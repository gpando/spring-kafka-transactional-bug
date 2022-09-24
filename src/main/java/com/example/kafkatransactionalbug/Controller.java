package com.example.kafkatransactionalbug;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {

    private final AccountService service;


    public Controller(AccountService service) {

        this.service = service;
    }

    @GetMapping("/test-ko")
    public void testKO() {

        service.doTaskKO();
    }

    @GetMapping("/test-ok")
    public void testOK() {
        
        service.doTaskOK();
    }
}
