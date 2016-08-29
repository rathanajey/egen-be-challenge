package com.userapi.RestUserAPI.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.userapi.RestUserAPI.model.User;
import com.userapi.RestUserAPI.resource.UserResource;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import static java.util.Arrays.asList;


public class UserService {
	
	public static JSONObject parseRequestBody(String body) throws ParseException{
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
		
		return jsonObject;
	}
	
	public static User getUserModel(String requestBody) throws ParseException{
		JSONObject jsonRequestBody = UserService.parseRequestBody(requestBody);
		
		User user = new User();
		
		user.setId((String) jsonRequestBody.get("id"));
		user.setFirstName((String) jsonRequestBody.get("firstName"));
		user.setLastName((String) jsonRequestBody.get("lastName"));
		user.setEmail((String) jsonRequestBody.get("email"));
		user.setDateCreated((String) jsonRequestBody.get("dateCreated"));
		user.setProfilePic((String) jsonRequestBody.get("profilePic"));
		
		JSONObject jsonUserAddress = (JSONObject)jsonRequestBody.get("address");
		HashMap<String, String> addressMap = new HashMap<String, String>();
		addressMap.put("street", (String)jsonUserAddress.get("street"));
		addressMap.put("city", (String)jsonUserAddress.get("city"));
		addressMap.put("zip", (String)jsonUserAddress.get("zip"));
		addressMap.put("state", (String)jsonUserAddress.get("state"));
		addressMap.put("country", (String)jsonUserAddress.get("country"));
		
		JSONObject jsonUserCompany = (JSONObject)jsonRequestBody.get("company");
		HashMap<String, String> companyMap = new HashMap<String, String>();
		companyMap.put("name", (String)jsonUserCompany.get("name"));
		companyMap.put("website", (String)jsonUserCompany.get("website"));
		
		user.setAddress(addressMap);
		user.setCompany(companyMap);
	    System.out.println("Exiting User model creation");
		return user;
	}
	
	public String createUser(String requestBody) throws ParseException{
		
		User user = UserService.getUserModel(requestBody);
		MongoClient mongoClient = new MongoClient();		
		MongoDatabase db = mongoClient.getDatabase("UserDB");
		
		db.getCollection("UserCollection").insertOne(
				new Document("firstName", user.getFirstName())
				.append("lastName", user.getLastName())
				.append("email", user.getEmail())
				.append("profilePic", user.getProfilePic())
				.append("dateCreated", user.getDateCreated())
				.append("address", new Document()
						.append("street", user.getAddress().get("street"))
						.append("city", user.getAddress().get("city"))
						.append("zip", user.getAddress().get("zip"))
						.append("state", user.getAddress().get("state"))
						.append("country", user.getAddress().get("country")))
				.append("company", new Document()
						.append("name", user.getCompany().get("name"))
						.append("website", user.getCompany().get("website")))				
				);
			
		return "Done";
	}
	
	public List<String> getAllUsers(){
		
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase("UserDB");
		List<String> resultList = new ArrayList<String>();
	
		FindIterable<Document> iterable = db.getCollection("UserCollection").find();
		iterable.forEach(new Block<Document>(){
			@Override
		    public void apply(final Document document) {
		       resultList.add(document.toJson());
		    }
		});
		
		return resultList;
	}
	
	public String updateUser(String requestBody) throws ParseException{
	    System.out.println("Inside updateUser");

		User user = UserService.getUserModel(requestBody);
		
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase("UserDB");
	    System.out.println("The user request id is");
	    System.out.println(user.getId());
		String id = user.getId();
	    System.out.println("The user request firstName is");
		System.out.println(user.getFirstName());
//		DBCollection coll = (DBCollection) db.getCollection("UserCollection"); 
////		DBObject searchById = new BasicDBObject("_id", new ObjectId(id));
////		DBObject found = coll.findOne(searchById);
//	    System.out.println("Just before dbobject query");
//	    
//	    BasicDBObject query = new BasicDBObject();
//	    query.put("_id", new ObjectId(id));
//	    DBObject dbObj = coll.findOne(query);
		
		UpdateResult result = db.getCollection("UserCollection").updateOne(new Document("_id", new ObjectId(id)),
		        new Document("$set", new Document("firstName", user.getFirstName()))
		           		);
		
		System.out.println(result.getUpsertedId());
		return "Updated!";
	}
}
