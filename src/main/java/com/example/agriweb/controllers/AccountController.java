package com.example.agriweb.controllers;

import com.example.agriweb.models.User;
import com.example.agriweb.security.AgriwebUserDetails;
import com.example.agriweb.services.FileUploadUtil;
import com.example.agriweb.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String viewDetails(@AuthenticationPrincipal AgriwebUserDetails loggedUser, Model model) {

        String email = loggedUser.getUsername();
        User user = userService.getByEmail(email);

        model.addAttribute("user", user);
        return "account_form";
    }


    @PostMapping("/account/update")
    public String saveUser(User user, @AuthenticationPrincipal AgriwebUserDetails loggedUser,
                           RedirectAttributes redirectAttributes, @RequestParam("image")
                                   MultipartFile multipartFile) throws IOException {
        System.out.println(user);
        System.out.println(multipartFile.getOriginalFilename());

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.updateAccount(user);
            String uploadDir = "user-photos/" + savedUser.getIdUser();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhoto().isEmpty()) user.setPhoto(null);
            userService.updateAccount(user);
        }
        //userService.saveUser(user);
         loggedUser.setUsername(user.getUsername());
        redirectAttributes.addFlashAttribute("message", "Your account details have been updated successfully");

        return "redirect:/account";

    }
}
