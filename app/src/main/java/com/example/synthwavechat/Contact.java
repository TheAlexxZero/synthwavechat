package com.example.synthwavechat;

public class Contact {

    private String name;
    private String email;

    // Constructor
    public Contact(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
