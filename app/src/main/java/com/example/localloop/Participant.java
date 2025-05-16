package com.example.localloop;

public class Participant extends User{
    public Participant(String firstName,String lastName,String userName,String email,String password){
        super(firstName,lastName,userName,email,password, Role.PARTICIPANT);
    }
}
