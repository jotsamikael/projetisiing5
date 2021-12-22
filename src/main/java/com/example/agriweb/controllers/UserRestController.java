package com.example.agriweb.controllers;

import com.example.agriweb.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/check_email")
    public String checkDuplicatedEmail(@Param("idUser") Long idUser ,@Param("email") String email){
        return  userService.isEmailUnique(idUser, email) ? "OK" : "DUPLICATED";
    }

}
