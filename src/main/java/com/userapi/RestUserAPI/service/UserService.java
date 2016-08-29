package com.userapi.RestUserAPI.service;

import java.util.ArrayList;
import java.util.Date;
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
import java.util.Map;

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
		System.out.println("Inside getUserModel");
		user.setId((String) jsonRequestBody.get("id"));
		System.out.println("Inside after Id");
		user.setFirstName((String) jsonRequestBody.get("firstName"));
		System.out.println("Inside after firstName");
		user.setLastName((String) jsonRequestBody.get("lastName"));
		user.setEmail((String) jsonRequestBody.get("email"));
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		user.setDateCreated(sdf.format(new Date()));
		System.out.println("Inside after dateCreated");
		user.setProfilePic((String) jsonRequestBody.get("profilePic"));
		System.out.println("Inside after profilePic");
		
		JSONObject jsonUserAddress = (JSONObject)jsonRequestBody.get("address");
		System.out.println("Inside after address");
		
		HashMap<String, String> addressMap = new HashMap<String, String>();
		if(jsonUserAddress != null){
			addressMap.put("street", (String)jsonUserAddress.get("street"));
			System.out.println("Inside after street");
			addressMap.put("city", (String)jsonUserAddress.get("city"));
			addressMap.put("zip", (String)jsonUserAddress.get("zip"));
			addressMap.put("state", (String)jsonUserAddress.get("state"));
			addressMap.put("country", (String)jsonUserAddress.get("country"));
		}

		
		JSONObject jsonUserCompany = (JSONObject)jsonRequestBody.get("company");
		
		HashMap<String, String> companyMap = new HashMap<String, String>();
		if(jsonUserCompany != null){
			companyMap.put("name", (String)jsonUserCompany.get("name"));
			companyMap.put("website", (String)jsonUserCompany.get("website"));
		}
		
		user.setAddress(addressMap);
		user.setCompany(companyMap);
	    System.out.println("Exiting User model creation");
		return user;
	}
	
	public HashMap<String, String> getFieldsToUpdate(User user){
		
		HashMap<String, String> fieldsToUpdate = new HashMap<String, String>();
		
		String field = user.getFirstName();
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("firstName", field);
		
		field = user.getLastName();
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("lastName", field);
		
		field = user.getEmail();
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("email", field);
		
		field = user.getDateCreated();
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("dateCreated", field);
		
		field = user.getProfilePic();
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("profilePic", field);
		
		HashMap<String, String> addressField = user.getAddress();
		field = addressField.get("street");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("address.street", field);
		
		field = addressField.get("city");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("address.city", field);
		
		field = addressField.get("zip");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("address.zip", field);
		
		field = addressField.get("state");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("address.state", field);
		
		field = addressField.get("country");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("address.country", field);
		
		HashMap<String, String> companyField = user.getAddress();
		field = companyField.get("name");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("company.name", field);
		
		field = companyField.get("website");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("company.website", field);
		
		return fieldsToUpdate;
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
	
	public List<String> getAllUsers() throws ParseException{
		
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase("UserDB");
		
		List<String> resultList = new ArrayList<String>();
	
		FindIterable<Document> iterable = db.getCollection("UserCollection").find();
		iterable.forEach(new Block<Document>(){
			@Override
		    public void apply(final Document document) {
				JSONParser parser = new JSONParser();
				JSONObject jsonDocument;
				try {
					jsonDocument = (JSONObject) parser.parse(document.toJson());
					JSONObject jsonId = (JSONObject) jsonDocument.get("_id");
					String id = (String) jsonId.remove("$oid");
					jsonDocument.remove("_id");
					jsonDocument.put("id", id);
				    resultList.add(jsonDocument.toJSONString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
		
		return resultList;
	}
	
	public String updateUser(String requestBody) throws ParseException, IllegalArgumentException{
	    System.out.println("Inside updateUser");

		User user = UserService.getUserModel(requestBody);
		boolean userFound = true;
		HashMap<String, String> fieldsToUpdate = getFieldsToUpdate(user);
		
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase("UserDB");
	    System.out.println("The user request id is");
	    System.out.println(user.getId());
		String id = user.getId();
		
		UpdateResult updateResult = null;
		
		for(Map.Entry<String, String> entry : fieldsToUpdate.entrySet()){
			String fieldKey = entry.getKey();
			String fieldValue = entry.getValue();
			
			updateResult = db.getCollection("UserCollection").updateOne(new Document("_id", new ObjectId(id)),
			        new Document("$set", new Document(fieldKey, fieldValue))
			           		);
			if(updateResult.getMatchedCount() == 0){
				userFound = false;
				break;
			}
		}
		
		System.out.println(updateResult.getMatchedCount());
		if(userFound == false) throw new IllegalArgumentException();
		
		return "Success";
	}
}
