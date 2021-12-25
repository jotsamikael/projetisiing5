package com.example.agriweb.security;

import com.example.agriweb.models.User;
import com.example.agriweb.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AgriwebUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user =  userRepo.getUserByEmail(email);
        if(user != null){
            return new AgriwebUserDetails(user);
        }
        throw new UsernameNotFoundException("Could not find user with email:" + email);
    }
}
