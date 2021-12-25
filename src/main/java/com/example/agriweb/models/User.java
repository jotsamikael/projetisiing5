package com.example.agriweb.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(length = 128, nullable= false, unique = true)
    private String email;

    @Column(length = 250, nullable= false)
    private String password;

    @Column(length = 25, nullable= false)
    private String username;

    private String photo;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns =  @JoinColumn(name = "idUser")
            ,inverseJoinColumns = @JoinColumn(name = "idRole"))

    private Set<Role> roles = new HashSet<>();


    public User() {
    }

    public User(Long idUser, String email, String password, String username, String photo, boolean enabled, Set<Role> roles) {
        this.idUser = idUser;
        this.email = email;
        this.password = password;
        this.username = username;
        this.photo = photo;
        this.enabled = enabled;
        this.roles = roles;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", photo='" + photo + '\'' +
                ", enabled=" + enabled +
                ", roles=" + roles +
                '}';
    }

    @Transient
    public String getPhotoImagePath(){
        if(idUser == null || photo == null) return "/img/thumbnail.png";
        return "/user-photos/" + this.idUser + "/" +this.photo;
    }
}
