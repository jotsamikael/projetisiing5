package com.example.agriweb.services;

import com.example.agriweb.exception.UserNotFoundException;
import com.example.agriweb.models.Role;
import com.example.agriweb.models.User;
import com.example.agriweb.repositories.RoleRepo;
import com.example.agriweb.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    public static final int USERS_PER_PAGE = 4;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getByEmail(String email){
       return userRepo.getUserByEmail(email);
    }

    public List<User> getUserList(){
        return (List<User>) userRepo.findAll(Sort.by("username").ascending());
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending(): sort.descending();
        Pageable pageable = PageRequest.of(pageNum-1, USERS_PER_PAGE, sort);
      if(keyword != null){
          return userRepo.findAll(keyword, pageable);
      }
        return userRepo.findAll(pageable);
    }

    public List<Role> getRoleList(){
        return (List<Role>) roleRepo.findAll();
    }


    public User saveUser(User user){
        boolean isUpdatingUser =  (user.getIdUser() != null);
        if(isUpdatingUser){
          User existingUser = userRepo.findById(user.getIdUser()).get();
          if(user.getPassword().isEmpty()){
              user.setPassword(existingUser.getPassword());
          } else{
              encodePassword(user);
          }
        } else {
        encodePassword(user);
        }
      return   userRepo.save(user);
    }

    //update account details

    public User updateAccount(User userInForm){
        User userInDB = userRepo.findById(userInForm.getIdUser()).get();
        if(!userInForm.getPassword().isEmpty()){
            userInDB.setPassword(userInForm.getPassword());
             encodePassword(userInDB);
        }
        if(userInForm.getPhoto() !=null){
            userInDB.setPhoto(userInForm.getPhoto());
        }
        userInDB.setUsername(userInForm.getUsername());
        return userRepo.save(userInDB);
    }

    public void encodePassword(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }


    public boolean isEmailUnique(Long idUser, String email){
        User userByEmail = userRepo.getUserByEmail(email);
        if(userByEmail == null) return true;
        boolean isCreatingNew = (idUser == null);

        if(isCreatingNew){
            if(userByEmail != null) return false;
        } else {
            if(userByEmail.getIdUser() != idUser ){
                return  false;
            }
        }

        return  true;
    }

    public Optional<User> getUserByid(Long idUser) throws UserNotFoundException {
        try{
        return userRepo.findById(idUser);
        }
        catch (NoSuchElementException ex){
            throw  new UserNotFoundException("Could not find user with id:"+ idUser);
        }
    }

    public void deleteUser(Long idUser) throws UserNotFoundException {
        Integer countById = userRepo.countByIdUser(idUser);
        if(countById == null || countById == 0){
            throw  new UserNotFoundException("Could not find user with id:"+ idUser);
        }
        userRepo.deleteById(idUser);
    }

    public void updateUserEnabledStatus(Long idUser, boolean enabled){
        userRepo.updateEnabledStatus(idUser,enabled);

    }
}
