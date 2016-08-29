package com.userapi.RestUserAPI.resource;

import static spark.Spark.*;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import com.userapi.RestUserAPI.HelloWorld;
import com.userapi.RestUserAPI.model.User;
import com.userapi.RestUserAPI.service.UserService;

public class UserResource {
	
	
		
	
    public static void main(String[] args) {
    	
    	Gson gson = new Gson();
    	UserService userService = new UserService();
    	
        post("/users", (req, res) -> userService.createUser(req.body()), gson::toJson);
        
        get("/users", (req, res) -> userService.getAllUsers());
        
        put("/users", (req, res) -> userService.updateUser(req.body()), gson::toJson);
        
        after((req, res) -> {
        	res.type("application/json");
        	});
        
        exception(IllegalArgumentException.class, (e, req, res) -> {
        	res.status(404);
        	res.body("User Not Found");
        	});
        
        System.out.println("Updated");
        //stop();
    }
}
