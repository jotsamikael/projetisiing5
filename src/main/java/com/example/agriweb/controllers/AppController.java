package com.example.agriweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
    @GetMapping(" ")
    public String goHome(){
        return "index";
    }


  @GetMapping("/login")
  public String viewLoginPage(){
    return "login";
}
}