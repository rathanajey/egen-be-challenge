package com.userapi.RestUserAPI.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.userapi.RestUserAPI.model.User;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * This class acts as the service for User service interaction and contains the functionality logic.
 * @author Rathan
 *
 */
public class UserService {
	
	public static final String DB_NAME = "UserDB";
	public static final String COLLECTION_NAME = "UserCollection";
	private static final MongoClient MONGO_CLIENT = new MongoClient();
	
	/**
	 * This method converts a given string to a JSONObject. 
	 * @param body
	 * @return
	 * @throws ParseException
	 */
	private static JSONObject parseRequestBody(String body) throws ParseException{
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(body);
		
		return jsonObject;
	}
	
	public MongoDatabase getMongoDBObject(){		
		return  MONGO_CLIENT.getDatabase(DB_NAME);
	}
	
	/**
	 * This method populates the User model with the request body.
	 * @param requestBody
	 * @return
	 * @throws ParseException
	 */
	public static User getUserModel(String requestBody) throws ParseException{
		JSONObject jsonRequestBody = UserService.parseRequestBody(requestBody);
		
		User user = new User();
		user.setId((String) jsonRequestBody.get("id"));
		user.setFirstName((String) jsonRequestBody.get("firstName"));
		user.setLastName((String) jsonRequestBody.get("lastName"));
		user.setEmail((String) jsonRequestBody.get("email"));
		
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		user.setDateCreated(sdf.format(new Date()));
		user.setProfilePic((String) jsonRequestBody.get("profilePic"));
		
		JSONObject jsonUserAddress = (JSONObject)jsonRequestBody.get("address");
		
		HashMap<String, String> addressMap = new HashMap<String, String>();
		if(jsonUserAddress != null){
			addressMap.put("street", (String)jsonUserAddress.get("street"));
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
		
		return user;
	}
	
	/**
	 * This method takes the User model and returns a list of the fields that must be updated for this user id.
	 * @param user
	 * @return
	 */
	private HashMap<String, String> getFieldsToUpdate(User user){
		
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
		
		HashMap<String, String> companyField = user.getCompany();
		field = companyField.get("name");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("company.name", field);
		
		field = companyField.get("website");
		if(field != null && field.trim().length() > 0) fieldsToUpdate.put("company.website", field);
		
		return fieldsToUpdate;
	}
	
	/**
	 * This method inserts a user record in MongoDB if the user isn't already present.
	 * The emailId field is used to check whether a user has already been entered. If so then the error
	 * response is returned.
	 * @param requestBody
	 * @return
	 * @throws ParseException
	 */
	public String createUser(String requestBody) throws ParseException{
		
		User user = UserService.getUserModel(requestBody);
			
		MongoDatabase db = getMongoDBObject();
		
		String result = "success";
		try{
			FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME).find(
			        new Document("email", user.getEmail())).limit(1);		
			
			// If there is a record with the email id then consider that the user already exists and return error response.
			if(iterable.iterator().hasNext()){
				result = "error";
			}
			else{
				db.getCollection("COLLECTION_NAME").insertOne(
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
			}
		}catch(Exception e){
			e.getMessage();
			e.printStackTrace();
		}
		
		MONGO_CLIENT.close();
			
		return result;
	}
	
	/**
	 * This method returns the list of user records in the MongoDB collection.
	 * @return
	 * @throws ParseException
	 */
	public List<String> getAllUsers() throws ParseException{
		
		MongoDatabase db = getMongoDBObject();
		
		List<String> resultList = new ArrayList<String>();
	
		FindIterable<Document> iterable = db.getCollection(COLLECTION_NAME).find();
		
		// Iterate over each document to stringify and clean the id field.
		iterable.forEach(new Block<Document>(){
			@Override
		    public void apply(final Document document) {
				JSONParser parser = new JSONParser();
				JSONObject jsonDocument;
				
				// Change the "_id" field from MongoDB to a general purpose "id" field.
				try {
					jsonDocument = (JSONObject) parser.parse(document.toJson());
					JSONObject jsonId = (JSONObject) jsonDocument.get("_id");
					String id = (String) jsonId.remove("$oid");
					jsonDocument.remove("_id");
					jsonDocument.put("id", id);
				    resultList.add(jsonDocument.toJSONString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    }
		});
		
		MONGO_CLIENT.close();
		
		return resultList;
	}
	
	/**
	 * This method updates the user record if the id from the PUT request exists.
	 * If not then it returns an error response.
	 * @param id
	 * @param requestBody
	 * @return
	 * @throws ParseException
	 * @throws MongoException
	 */
	public String updateUser(String id, String requestBody) throws ParseException, MongoException{

		// If id from url is null or empty then return error response.
		if(id == null || id.trim().equals("")) return "error";
		
		User user = UserService.getUserModel(requestBody);
		boolean userFound = true;
		
		// Get the fields that need to be updated, from the request body.
		HashMap<String, String> fieldsToUpdate = getFieldsToUpdate(user);
		
		// Does not make sense to update the dateCreated field.
		fieldsToUpdate.remove("dateCreated");
		
		MongoDatabase db = getMongoDBObject();
		
		UpdateResult updateResult = null;
		// For each field that needs to be update, call the update method.
		for(Map.Entry<String, String> entry : fieldsToUpdate.entrySet()){
			String fieldKey = entry.getKey();
			String fieldValue = entry.getValue();

			try{
				updateResult = db.getCollection(COLLECTION_NAME).updateOne(new Document("_id", new ObjectId(id)),
				        new Document("$set", new Document(fieldKey, fieldValue))
				           		);
			}
			catch(IllegalArgumentException e){
				userFound = false;
				break;
			}
			
			// If the given id is not found in MongoDB then flag the error response and exit the loop.
			if(updateResult.getMatchedCount() == 0){
				userFound = false;
				break;
			}
		}
		
		MONGO_CLIENT.close();
		
		String result = "success";
	
		if(userFound == false) result = "error";
		
		return result;
	}
}
