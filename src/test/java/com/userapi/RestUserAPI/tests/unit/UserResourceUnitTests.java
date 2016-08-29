package com.userapi.RestUserAPI.tests.unit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpURI;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.userapi.RestUserAPI.model.User;
import com.userapi.RestUserAPI.resource.UserResource;
import com.userapi.RestUserAPI.service.UserService;

import junit.framework.TestCase;
import spark.Spark;
import spark.utils.IOUtils;

public class UserResourceUnitTests {

	  @BeforeClass	
	  public static void beforeClass() {
		  UserResource.main(null);
	  }
	 
	  @AfterClass
	  public static void afterClass() {
		  Spark.stop();
	  }
	  
	  public JSONObject getUserData(){
		  String id = "1630215c-2608-44b9-aad4-9d56d8aafd4c";
		  String firstName = "Dorris";
		  String lastName = "Keeling";
		  String email = "Darby_Leffler68@gmail.com";
		  String profilePic = "http://lorempixel.com/640/480/people";
		  
		  HashMap<String, String> addressMap = new HashMap<String, String>();
		  addressMap.put("street", "193 Talon Valley");
		  addressMap.put("city", "South Tate furt");
		  addressMap.put("zip", "47069");
		  addressMap.put("state", "IA");
		  addressMap.put("country", "US");
			
		  HashMap<String, String> companyMap = new HashMap<String, String>();
		  companyMap.put("name", "Myntra");
		  companyMap.put("website", "http://jodie/myntra.org");
		  
		  JSONObject jsonObject = new JSONObject();
		  jsonObject.put("id", id);
		  jsonObject.put("firstName", firstName);
		  jsonObject.put("lastName", lastName);
		  jsonObject.put("email", email);
		  jsonObject.put("address", addressMap);
		  jsonObject.put("company", companyMap);
		  
		  return jsonObject;
	  }
	  
	  @Test
	  public void userModelShouldPopulate() throws ParseException{
		  JSONObject body = getUserData();
		  User user = UserService.getUserModel(body.toJSONString());
		  
		  assertTrue(user.getFirstName().equals(body.get("firstName")));
	  }
	  
	  @Test
	  public void usersShouldbeRetrieved() throws IOException{
		  TestResponse res = request("GET", "http://localhost:4567/users", null);
		  
		  List<String> json = res.jsonList();
		  
		  assertTrue(json.size() > 0);
		  assertTrue(res.status == 200);	  
	  }
	  
	  @Test
	  public void userShouldbeUpdated() throws IOException{
		  String id = "57c37809893c0433ccba586c";
		  JSONObject body = getUserData();
		  TestResponse res = request("PUT", "http://localhost:4567/users/" + id, body);
		  
		  HashMap<String, String> result = res.jsonMap();
		  
		  assertTrue(result.get("success").equals("true"));
		  assertTrue(res.status == 200);	  
	  }
	  
	  @Test
	  public void userUpdateShouldFailDueToNonExistingUser() throws IOException{
		  String id = "37c37809893c0433ccba586c";
		  JSONObject body = getUserData();
		  TestResponse res = request("PUT", "http://localhost:4567/users/" + id, body);
		  
		  HashMap<String, String> result = res.jsonMap();
		  
		  assertTrue(result.get("error").equals("User does not exist."));
		  assertTrue(res.status == 404);	  
	  }
	  
	  @Test
	  public void userShouldBeCreated() throws IOException{
		  JSONObject body = getUserData();
		  body.put("email", "junitCreate2@gmail.com");
		  body.put("firstName", "junitCreate2");
		  TestResponse res = request("POST", "http://localhost:4567/users", body);
		  
		  HashMap<String, String> result = res.jsonMap();
		  
		  assertTrue(result.get("success").equals("true"));
		  assertTrue(res.status == 201);	  
	  }
	  
	  @Test
	  public void userCreateShouldFailAsUserExists() throws IOException{
		  JSONObject body = getUserData();
		  body.put("email", "junitCreate2@gmail.com");
		  body.put("firstName", "junitCreate3");
		  TestResponse res = request("POST", "http://localhost:4567/users", body);
		  
		  HashMap<String, String> result = res.jsonMap();

		  assertTrue(result.get("error").equals("User with same email Id already exists"));
		  assertTrue(res.status == 409);	  
	  }
	  
	  private TestResponse request(String method, String path, JSONObject jsonBodyObject) throws IOException {
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod(method);
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		
		OutputStream os = null;
		if(method.equals("PUT") || method.equals("POST")){
			os = connection.getOutputStream();
			OutputStreamWriter wr= new OutputStreamWriter(os);
			wr.write(jsonBodyObject.toJSONString());
			wr.flush();
		}
		
		connection.connect();
		String body = null;
		
		try{
			body = IOUtils.toString(connection.getInputStream());
		}
		catch(IOException e){
			body = IOUtils.toString(connection.getErrorStream());
		}
		
		return new TestResponse(connection.getResponseCode(), body);
	}
	
	private static class TestResponse {

		public final String body;
		public final int status;

		public TestResponse(int status, String body) {
			this.status = status;
			this.body = body;
		}

		public HashMap<String,String> jsonMap() {
			return new Gson().fromJson(body, HashMap.class);
		}
		
		public List<String> jsonList() {
			return new Gson().fromJson(body, List.class);
		}
}
	  
}
