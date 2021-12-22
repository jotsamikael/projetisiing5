package com.example.agriweb.models;

import javax.persistence.*;

@Entity
@Table(name = "Roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRole;

    @Column(length = 35, nullable= false, unique = true)
    private String nameRole;

    @Column(length = 150, nullable= false, unique = true)
    private String descRole;

    public Role() {
    }

    public Role(Long idRole, String nameRole, String descRole) {
        this.idRole = idRole;
        this.nameRole = nameRole;
        this.descRole = descRole;
    }

    public Long getIdRole() {
        return idRole;
    }

    public void setIdRole(Long idRole) {
        this.idRole = idRole;
    }

    public String getNameRole() {
        return nameRole;
    }

    public void setNameRole(String nameRole) {
        this.nameRole = nameRole;
    }

    public String getDescRole() {
        return descRole;
    }

    public void setDescRole(String descRole) {
        this.descRole = descRole;
    }

    @Override
    public String toString() {
        return this.nameRole;
    }
}
