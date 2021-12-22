package com.example.agriweb;


import com.example.agriweb.models.User;
import com.example.agriweb.repositories.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    private UserRepo userRepo;

    @Test
    //Test la recherche d'un user selon l'email
    public void  testgetUserByEmail(){
        String email = "admin@gmail.com";
        User user = userRepo.getUserByEmail(email);
        assertThat(user).isNotNull();

    }

    @Test
    public void testCountById(){
        Long idUser = Long.valueOf(1);
        Integer countById = userRepo.countByIdUser(idUser);
        assertThat(countById).isNotNull().isGreaterThan(0);

    }

    @Test
    public void testDisableUser(){
        Long idUser = Long.valueOf(1);
        userRepo.updateEnabledStatus(idUser, false);
    }

    @Test
    public void testEnableUser(){
        Long idUser = Long.valueOf(3);
        userRepo.updateEnabledStatus(idUser, true);
    }

    @Test
    public void testListFirstPage(){
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepo.findAll(pageable);

        List<User> listUsers = page.getContent();
        listUsers.forEach(user-> System.out.println(user));
       assertThat(listUsers.size()).isEqualTo(pageSize);
    }
}
