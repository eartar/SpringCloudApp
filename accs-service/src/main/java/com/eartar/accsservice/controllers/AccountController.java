package com.eartar.accsservice.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @RequestMapping("/status/check")
    public String status() {
        return "Working...";
    }

}
