package com.userapi.RestUserAPI.resource;

import static spark.Spark.*;

import java.util.HashMap;

import com.google.gson.Gson;
import com.userapi.RestUserAPI.service.UserService;

/**
 * This class is the controller in JavaSpark and listens for requests and sets responses.
 * @author Rathan
 *
 */
public class UserResource {
	
    public static void main(String[] args) {  	
    	Gson gson = new Gson();
    	UserService userService = new UserService();
    	
    	// POST method for user creation.
        post("/users", (req, res) ->{
        	String result = userService.createUser(req.body());
        	
        	HashMap<String, String> responseMap = new HashMap<String, String>();
        	
        	// If response is error, then set the appropriate status and message codes. 
        	if(result.equals("error")){
        		res.status(409);
        		responseMap.put("error", "User with same email Id already exists.");
        		res.body(responseMap.toString());
        	}
        	else{
        		res.status(201);
        		responseMap.put("success", "true");
        	}
        
        	return responseMap;
        	
        } , gson::toJson);
        
        // GET method for fetching all users.
        get("/users", (req, res) -> userService.getAllUsers());
        
        
        //PUT method for updating the user info.
        put("/users/:id", (req, res) -> {
        	String id = req.params(":id");
        	String result = userService.updateUser(id, req.body());
        	HashMap<String, String> responseMap = new HashMap<String, String>();
        	
        	// If response is error, then set the appropriate status and message codes. 
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
        
        // USing After for setting the response type.
        after((req, res) -> {
        	res.type("application/json");
        	});
    }
}
