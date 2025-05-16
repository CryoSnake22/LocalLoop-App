package com.example.localloop.auth;
import com.example.localloop.*;
import com.example.localloop.database.DatabaseOperator;


import java.lang.reflect.Array;


public class Authenticator {
    public Authenticator(){
    }
   public boolean authenticateUser(String username, String password){
       // User foundUser = DatabaseOperator.getUser(user);
       // if (foundUser.equals(user)){
       //   return true;
       // else{
       // return false;
       // }
       // Eventually there should be an if statement and if the user is not found it returns false
       // otherwise it proceeds with auth
       String empty = "";
       User foundUser = new Admin(empty,empty, "admin",empty, "XPI76SZUqyCjVxgnUjm0");

       if (foundUser.getPassword().equals(password) && foundUser.getUsername().equals(username)){
            return true;
       }
       else{
           return false;
       }

   }
}
