package com.example.agriweb;

import com.example.agriweb.models.Role;
import com.example.agriweb.repositories.RoleRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RolesRepositoryTest {
    @Autowired
    private RoleRepo roleRepo;

    @Test
    public void TestCreateRole(){
        Role admin = new Role((long) 1,"Admin", "manage all");
        Role savedRole =  roleRepo.save(admin);

    }
}
