package com.server.serverapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/server")
public class ServerController {
    @GetMapping("/data")
    public String getData() {
        System.out.println("Returning data from server");
        return "Hello from server";
    }
}
