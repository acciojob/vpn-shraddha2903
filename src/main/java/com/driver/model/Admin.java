package com.driver.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admin")
public class Admin
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    private String password;

    @OneToMany(mappedBy = "admin",cascade = CascadeType.ALL)
    private List<ServiceProvider> serviceProviders = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ServiceProvider> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(List<ServiceProvider> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }
}
