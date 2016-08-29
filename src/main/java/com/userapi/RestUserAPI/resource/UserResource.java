package com.userapi.RestUserAPI.resource;

import static spark.Spark.*;

import java.util.HashMap;

import com.google.gson.Gson;
import com.userapi.RestUserAPI.service.UserService;

public class UserResource {
	
    public static void main(String[] args) {  	
    	Gson gson = new Gson();
    	UserService userService = new UserService();
    	
        post("/users", (req, res) ->{
        	String result = userService.createUser(req.body());
        	
        	HashMap<String, String> responseMap = new HashMap<String, String>();
        	
        	if(result.equals("error")){
        		res.status(409);
        		responseMap.put("error", "User with same email Id already exists");
        		res.body(responseMap.toString());
        	}
        	else{
        		res.status(201);
        		responseMap.put("success", "true");
        	}
        
        	return responseMap;
        	
        } , gson::toJson);
        
        get("/users", (req, res) -> userService.getAllUsers());
        
        put("/users/:id", (req, res) -> {
        	String id = req.params(":id");
        	String result = userService.updateUser(id, req.body());
        	HashMap<String, String> responseMap = new HashMap<String, String>();
        	
        	if(result.equals("error")){
        		res.status(404);
        		responseMap.put("error", "User does not exist.");
        		res.body(responseMap.toString());
        	}
        	else{
        		res.status(200);
        		responseMap.put("success", "true");
        	}
        
        	return responseMap;
        }, gson::toJson);
        
        after((req, res) -> {
        	res.type("application/json");
        	});
    }
}
