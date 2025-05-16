package com.example.localloop;

public class Admin extends User{

    public Admin(String firstName,String lastName,String userName,String email,String password){
        super(firstName,lastName,userName,email,password,Role.ADMIN);
    }
}
