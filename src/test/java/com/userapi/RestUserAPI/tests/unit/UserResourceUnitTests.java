package com.userapi.RestUserAPI.tests.unit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpURI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.userapi.RestUserAPI.resource.UserResource;

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
	  
	  @Test
	  public void usersShouldbeRetrieved(){
		  TestResponse res = request("GET", "http://localhost:4567/users");
		  List<String> json = res.jsonList();
		  assert(json.size() > 0);
		  assert(res.status == 200);	  
	  }
	  
	private TestResponse request(String method, String path) {
			try {
				URL url = new URL(path);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod(method);
				connection.setDoOutput(true);
				connection.connect();
				String body = IOUtils.toString(connection.getInputStream());
				return new TestResponse(connection.getResponseCode(), body);
			} catch (IOException e) {
				e.printStackTrace();
				fail("Sending request failed: " + e.getMessage());
				return null;
			}
	}
	
	private static class TestResponse {

		public final String body;
		public final int status;

		public TestResponse(int status, String body) {
			this.status = status;
			this.body = body;
		}

		public Map<String,String> json() {
			return new Gson().fromJson(body, HashMap.class);
		}
		
		public List<String> jsonList() {
			return new Gson().fromJson(body, List.class);
		}
}
	  
}
