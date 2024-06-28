package com.jean.productauthentifier.model;

public class UserRegistration {

    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String address;
    private String email;
    private String phone;
    private String country;
    private boolean notifiedbymail;

    public UserRegistration(String firstname,
                            String lastname,
                            String username,
                            String password,
                            String address,
                            String email,
                            String phone,
                            String country,
                            boolean notifiedbymail) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.notifiedbymail = notifiedbymail;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isNotifiedbymail() {
        return notifiedbymail;
    }

    public void setNotifiedbymail(boolean notifiedbymail) {
        this.notifiedbymail = notifiedbymail;
    }
}
