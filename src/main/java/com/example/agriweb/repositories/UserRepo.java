package com.example.agriweb.repositories;

import com.example.agriweb.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends PagingAndSortingRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email);

    public Integer countByIdUser(Long idUser);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.idUser = ?1")
    @Modifying
    public void updateEnabledStatus(Long idUser, boolean enabled);
}
