package com.example.agriweb.controllers;

import com.example.agriweb.exception.UserNotFoundException;
import com.example.agriweb.models.Role;
import com.example.agriweb.models.User;
import com.example.agriweb.services.FileUploadUtil;
import com.example.agriweb.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listFirstPage(Model model){

       return listByPage(1,model );
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model){
        Page<User> page = userService.listByPage(pageNum);
        List<User> listUsers = page.getContent();
        long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
        long endCount = startCount +  UserService.USERS_PER_PAGE - 1;
        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);

       /* System.out.println("pageNum="+pageNum);
        System.out.println("Total elements="+page.getTotalElements());
        System.out.println("Total pages="+page.getTotalPages());*/

        model.addAttribute("userList", listUsers);
        return "users";

    }

    @GetMapping("/users/new")
    public String createUser(Model model){
        List<Role> roleList = userService.getRoleList();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("roleList", roleList);
        model.addAttribute("pageTitle", "Create New User");

        return "userform";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {
        System.out.println(user);
        System.out.println(multipartFile.getOriginalFilename());

       if(!multipartFile.isEmpty()){
           String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
           user.setPhoto(fileName);
           User savedUser  = userService.saveUser(user);
           String uploadDir = "user-photos/" + savedUser.getIdUser();

           FileUploadUtil.cleanDir(uploadDir);
           FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
       } else {
           if(user.getPhoto().isEmpty()) user.setPhoto(null); userService.saveUser(user);
       }
        //userService.saveUser(user);

         redirectAttributes.addFlashAttribute("message", "user saved successfully");
        return "redirect:/users";

    }

    @GetMapping("/users/edit/{idUser}")
    public String updateUser(@PathVariable(name = "idUser") Long idUser,Model model, RedirectAttributes redirectAttributes){
        try{
            Optional<User> user = userService.getUserByid(idUser);
            List<Role> roleList = userService.getRoleList();
            model.addAttribute("roleList", roleList);

            if (user.isPresent()){
            model.addAttribute("user", user.get());
            }
            model.addAttribute("pageTitle", "Edit User with (ID: "+ idUser +")");


            return "userform";

        } catch (UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }

    }

  @GetMapping("/users/delete/{idUser}")
    public String deleteUser(@PathVariable(name = "idUser") Long idUser,Model model, RedirectAttributes redirectAttributes){
      try{
          userService.deleteUser(idUser);
          redirectAttributes.addFlashAttribute("message", "The User ID "+idUser+ "has been deleted sucessfully");
      } catch (UserNotFoundException ex){
          redirectAttributes.addFlashAttribute("message", ex.getMessage());
      }
      return "redirect:/users";
  }

    @GetMapping("/users/{idUser}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("idUser") Long idUser, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes){
        userService.updateUserEnabledStatus(idUser, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID "+idUser+ " has been"+ status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";


    }

}
