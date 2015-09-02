package net.tools;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.app.labeli.Survey;
import com.app.labeli.SurveyItem;
import com.app.labeli.member.Member;
import com.app.labeli.project.Message;
import com.app.labeli.project.Project;
import com.app.labeli.project.ProjectUser;
import com.app.labeli.Vote;
import com.tools.DateTools;

import net.tools.RequestSender;

/**
 * > @APIConnection
 *
 * Connector to Label[i] API
 * Reference : https://github.com/asso-labeli/labeli-api
 *
 * @author Florian "Aamu Lumi" Kauder
 * for the project @Label[i]
 */
public abstract class APIConnection {

	/**
	 * Reference : https://github.com/eolhing/labeli-api
	 */

	// URLs
	public static String apiUrl = "http://dev.aamulumi.info:9010/";
	private static String urlUsers = apiUrl + "users";
	private static String urlAuth = apiUrl + "auth";
	private static String urlProjects = apiUrl + "projects";
	private static String urlMessages = apiUrl + "messages";
	private static String urlMessage = apiUrl + "message";
	private static String urlVotes = apiUrl + "votes";

	// HTTP
	private static String GET = "GET";
	private static String POST = "POST";
	private static String DELETE = "DELETE";
	private static String PUT = "PUT";

	// API Parameters Name
	private static String paramAuthUsername = "username";
	private static String paramAuthPassword = "password";
	private static String paramProjectsName = "name";
	private static String paramProjectsType = "type";
	private static String paramUserFirstName = "firstName";
	private static String paramUserLastName = "lastName";
	private static String paramUserEmail = "email";
	private static String paramUserPassword = "password";
	private static String paramMessagesContent = "content";

	// Users JSON Tags
	private static String tagUserFirstName = "firstName";
	private static String tagUserLastName = "lastName";
	private static String tagUserUsername = "username";
	private static String tagUserEmail = "email";
	private static String tagUserRole = "role";
	private static String tagUserUniversityGroup = "universityGroup";
	private static String tagUserDescription = "description";
	private static String tagUserPicture = "picture";
	private static String tagUserCreated = "created";
	private static String tagUserBirthday = "birthday";
	private static String tagUserId = "_id";
	private static String tagUserLevel = "level";
	private static String tagUserWebsite = "website";

	// Projects JSON Tags
	private static String tagProjectAuthor = "author";
	private static String tagProjectName = "name";
	private static String tagProjectDescription = "description";
	private static String tagProjectPicture = "picture";
	private static String tagProjectCreated = "created";
	private static String tagProjectLastEdited = "lastEdited";
	private static String tagProjectStatus = "status";
	private static String tagProjectType = "type";
	private static String tagProjectId = "_id";

	// SurveyItem JSON Tags
	private static String tagSurveyItemName = "name";
	private static String tagSurveyItemCreated = "created";
	private static String tagSurveyItemLastEdited = "lastEdited";
	private static String tagSurveyItemId = "_id";

	// Surveys JSON Tags
	private static String tagSurveyDescription = "description";
	private static String tagSurveyName = "name";
	private static String tagSurveyState = "state";
	private static String tagSurveyNumberChoices = "numberChoices";
	private static String tagSurveyCreated = "created";
	private static String tagSurveyLastEdited = "lastEdited";
	private static String tagSurveyAuthor = "author";
	private static String tagSurveyId = "_id";

	// Vote JSON Tags
	private static String tagVoteNegative = "negative";
	private static String tagVoteNeutral = "neutral";
	private static String tagVotePositive = "positive";
	private static String tagVoteTotal = "total";

	// Messages JSON Tags

	private static String tagMessageContent = "content";
	private static String tagMessageProject = "project";
	private static String tagMessageAuthor = "author";
	private static String tagMessageLastEdited = "lastEdited";
	private static String tagMessageCreated = "created";
	private static String tagMessageId = "_id";

	// Votes JSON Tags

	public static final int BOOLEAN_TRUE = 0;
	public static final int BOOLEAN_FALSE = 1;
	public static final int ERROR_VALUE = -1;

	private static Member loggedUser = null;

	private static RequestSender jParser = new RequestSender();

	private static JSONObject makeHttpRequest(String url, 
			String method, HashMap<String, String> urlParameters, HashMap<String, String> bodyParameters){
		return jParser.makeHttpRequest(url, method, urlParameters, bodyParameters);
	}
	
	public static boolean makeFileRequest(File f, String fileName){
		return jParser.postPicture(f, fileName);
	}

	public static Member getLoggedUser(){
		return loggedUser;
	}

	public static boolean isLogged(){
		return loggedUser != null;
	}

	public static boolean loggedUserIsMember(){
		if (isLogged())
			return loggedUser.getLevel() >= Member.LEVEL_MEMBER;

			return false;
	}

	public static boolean loggedUserIsAdmin(){
		if (isLogged())
			return loggedUser.getLevel() >= Member.LEVEL_ADMIN;

			return false;
	}

	/**
	 * V�rifie si la cha�ne est un bool�en. Elle supprime tout les espaces et les \n de la chaine avant la v�rification.
	 * @param bool
	 * @return
	 */
	private static boolean convertToBoolean(String bool){
		return Boolean.valueOf(bool.replace(" ", "").replace("\n", ""));
	}

	private static <T> ArrayList<T> getItems(String url, String parseMethod, 
			HashMap<String, String> urlParameters, HashMap<String, String> bodyParameters){
		Class<?>[] cArg = new Class[1];
		cArg[0] = JSONObject.class;

		Method parse = null;
		try {
			parse = APIConnection.class.getMethod(parseMethod, cArg);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			return null;
		}

		ArrayList<T> list = new ArrayList<T>();

		JSONObject json = makeHttpRequest(url, GET, urlParameters, bodyParameters);

		if (json == null)
			return null;
		try {
			int success = json.getInt("success");
			// Parse if successfull
			if (success == 1){
				JSONArray data = json.getJSONArray("data");
				for (int i = 0; i < data.length(); i++){
					@SuppressWarnings("unchecked")
					T tmp = (T) parse.invoke(APIConnection.class, data.getJSONObject(i));
					list.add(tmp);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	private static <T> T getItem(String url, String parseMethod, 
			HashMap<String, String> urlParameters, HashMap<String, String> bodyParameters){
		Class<?>[] cArg = new Class[1];
		cArg[0] = JSONObject.class;

		Method parse = null;
		try {
			parse = APIConnection.class.getMethod(parseMethod, cArg);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			return null;
		}

		// Make request
		JSONObject json = makeHttpRequest(url, GET, urlParameters, bodyParameters);

		if (json == null)
			return null;
		try {
			int success = json.getInt("success");
			// Parse if successfull
			if (success == 1){
				return (T) parse.invoke(APIConnection.class, json.getJSONObject("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T createItem(String url, String parseMethod, 
			HashMap<String, String> urlParameters, HashMap<String, String> bodyParameters){
		Class<?>[] cArg = new Class[1];
		cArg[0] = JSONObject.class;

		Method parse = null;
		try {
			parse = APIConnection.class.getMethod(parseMethod, cArg);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			return null;
		}

		JSONObject json = makeHttpRequest(url, POST, urlParameters, bodyParameters);

		if (json == null)
			return null;
		try {
			int success = json.getInt("success");
			Log.i("Coucou", json.toString());
			// Parse if successfull
			if (success == 1){
				return (T) parse.invoke(APIConnection.class, json.getJSONObject("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T editItem(String url, String parseMethod, 
			HashMap<String, String> urlParameters, HashMap<String, String> bodyParameters){
		Class<?>[] cArg = new Class[1];
		cArg[0] = JSONObject.class;

		Method parse = null;
		try {
			parse = APIConnection.class.getMethod(parseMethod, cArg);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			return null;
		}

		JSONObject json = makeHttpRequest(url, PUT, urlParameters, bodyParameters);

		if (json == null)
			return null;
		try {
			int success = json.getInt("success");
			// Parse if successfull
			if (success == 1){
				return (T) parse.invoke(APIConnection.class, json.getJSONObject("data"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public static boolean deleteItem(String url,
			HashMap<String, String> urlParameters, HashMap<String, String> bodyParameters){
		HashMap<String, String> params = new HashMap<String, String>();
		JSONObject json = makeHttpRequest(url, DELETE, urlParameters, bodyParameters);

		if (json == null)
			return false;
		try {
			int success = json.getInt("success");
			// Parse if successfull
			if (success == 1){
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		} 

		return false;
	}

	/*
	 * AUTHENTIFICATION
	 */

	public static Member getCurrentUser(){
		// TODO getCurrentUser
		return null;
	}

	public static boolean login(String username, String password){
		HashMap<String, String> nameValuePairs = new HashMap<String, String>(2);
		nameValuePairs.put("username", username);
		nameValuePairs.put("password", password);

		JSONObject json = makeHttpRequest(urlAuth, POST, null, nameValuePairs);

		try {
			if (json.getInt("success") == 1){
				loggedUser = parseMember(json.getJSONObject("data"));
				return true;
			}
		} catch (JSONException e) {
			Log.w("APIConnection", "Error during parsing JSON");
		} catch (NullPointerException e){
			Log.w("APIConnection", "Error during getting response");
		}

		return false;
	}

	public static boolean logout(){
		JSONObject json = makeHttpRequest(urlAuth, DELETE, null, null);

		try {
			if (json.getInt("success") == 1){
				loggedUser = null;
				return true;
			}
		} catch (JSONException e) {
			Log.w("APIConnection", "Error during parsing JSON");
		} catch (NullPointerException e){
			Log.w("APIConnection", "Error during getting response");
		}

		return false;
	}

	/*
	 * USERS
	 */

	public static ArrayList<Member> getUsers(){
		return APIConnection.<Member>getItems(urlUsers, "parseMember", null, null);
	}

	public static Member createUser(String firstName, String lastName, 
			String email){
		HashMap<String, String> params = new HashMap<String, String>(3);
		params.put("firstName", firstName);
		params.put("lastName", lastName);
		params.put("email", email);

		return APIConnection.<Member>createItem(urlUsers, "parseMember", null, params);
	}

	public static Member getUser(String userID){
		return APIConnection.<Member>getItem(urlUsers + "/" + userID, "parseMember", null, null);
	}

	public static boolean editUser(String userID, String firstName, 
			String lastName, String email, String website, 
			String universityGroup, String role, Date birthday,
			String description, String picture){
		// TODO editUser
		return false;
	}

	public static boolean deleteUser(String userID){
		// TODO deleteUser
		return false;
	}

	/*
	 * PROJECTS
	 */

	public static Project createProject(String name, String type, 
			String authorUsername){
		HashMap<String, String> params = new HashMap<String, String>(3);
		params.put("name", name);
		params.put("type", type);
		params.put("authorUsername", authorUsername);

		return APIConnection.<Project>createItem(urlProjects, "parseProject", null, params);
	}

	public static boolean deleteProject(String projectID){
		// TODO deleteProject
		return false;
	}

	public static Project editProject(Project p){
		HashMap<String, String> params = new HashMap<String, String>(6);
		params.put("name", p.getName());
		params.put("status", String.valueOf(p.getStatus()));
		params.put("description", p.getDescription());
		params.put("type", String.valueOf(p.getType()));
		params.put("authorUsername", p.getAuthor().getUsername());
		if (p.getPictureURL() != null) params.put("picture", p.getPictureURL());

		return APIConnection.<Project>editItem(urlProjects + "/" + p.getId(), "parseProject", null, params);
	}

	public static ArrayList<Project> getProjects(){
		return APIConnection.<Project>getItems(urlProjects, "parseProject", null, null);
	}

	public static Project getProject(String projectID){
		return APIConnection.<Project>getItem(urlProjects + "/" + projectID, "parseProject", null, null);
	}

	/*
	 * PROJECTUSERS
	 */

	public static boolean createOrEditProjectUser(String projectID, 
			String level, String username){
		// TODO createOrEditProjectUser
		return false;
	}

	public static boolean deleteProjectUser(String projectUserID){
		// TODO deleteProjectUser
		return false;
	}

	public static ProjectUser getProjectUser(String projectUserID){
		// TODO getProjectUser
		return null;
	}

	public static ArrayList<ProjectUser> getProjectUsers(String projectID){
		// TODO getProjectUsers
		return null;
	}

	/*
	 * MESSAGES
	 */

	public static Message createMessage(String projectID, String content){
		if (!isLogged()) return null;

		HashMap<String, String> params = new HashMap<String, String>(1);
		params.put("content", content);
		return APIConnection.<Message>createItem(urlMessages + "/" + projectID, "parseMessage", null, params);
	}

	public static boolean deleteMessage(String messageID){
		if (!isLogged()) return false;

		return APIConnection.deleteItem(urlMessage + "/" + messageID, null, null);
	}

	public static Message editMessage(String messageID, String content){
		if (!isLogged()) return null;

		HashMap<String, String> params = new HashMap<String, String>(1);
		params.put("content", content);
		return APIConnection.<Message>editItem(urlMessage + "/" + messageID, "parseMessage", null, params);
	}

	public static Message getMessage(String messageID){
		return APIConnection.<Message>getItem(urlMessage + "/" + messageID , "parseMessage", null, null);
	}

	public static ArrayList<Message> getMessages(String projectID) {
		return APIConnection.<Message>getItems(urlMessages + "/" + projectID, "parseMessage", null, null);
	}

	/*
	 * VOTES
	 */

	public static Vote getVote(String id){
		return null;
	}

	public static boolean createVote(String id){
		return false;
	}

	public static ArrayList<SurveyItem> getSurveyItems(String id){
		return null;
	}

	public static Member parseMember(JSONObject o) throws JSONException{
		String lastName = o.getString(tagUserLastName);
		String firstName = o.getString(tagUserFirstName);
		String username = o.getString(tagUserUsername);
		String email = o.getString(tagUserEmail);
		String picture = o.getString(tagUserPicture);
		String role = o.getString(tagUserRole);
		int level = o.getInt(tagUserLevel);
		String description = o.getString(tagUserDescription);
		Date created = null;
		Date birthday = null;
		try {
			created = DateTools.parse(o.getString(tagUserCreated));
			if (!o.getString(tagUserBirthday).equals("null"))
				birthday = DateTools.parse(o.getString(tagUserBirthday));
			else
				birthday = new Date(0);
		} catch (ParseException e) {
			e.printStackTrace();
			created = new Date(0);
			birthday = new Date(0);
		}
		String universityGroup = o.getString(tagUserUniversityGroup);
		String id = o.getString(tagUserId);
		String website = o.getString(tagUserWebsite);

		return new Member(firstName, lastName, username, email, role, website, universityGroup,
				description, picture, created, birthday, id, level);
	}

	public static Project parseProject(JSONObject o) throws JSONException{
		Member author = getUser(o.getString(tagProjectAuthor));
		String name = o.getString(tagProjectName);
		int type = o.getInt(tagProjectType);
		String description = o.getString(tagProjectDescription);
		Date created = null;
		Date lastEdited = null;
		try {
			created = DateTools.parse(o.getString(tagProjectCreated));
			lastEdited = DateTools.parse(o.getString(tagProjectLastEdited));
		} catch (ParseException e) {
			created = new Date(0);
			lastEdited = new Date(0);
			e.printStackTrace();
		}
		String picture = o.getString(tagProjectPicture);
		int status = o.getInt(tagProjectStatus);
		String id = o.getString(tagProjectId);

		return new Project(author, name, description, picture, created, lastEdited, status, type, id);
	}

	public static SurveyItem parseSurveyItem(JSONObject o) throws JSONException{
		String name = o.getString(tagSurveyItemName);
		Date created = null;
		Date lastEdited = null;
		try {
			created = DateTools.parse(o.getString(tagSurveyItemCreated));
			lastEdited = DateTools.parse(o.getString(tagSurveyItemLastEdited));
		} catch (ParseException e) {
			e.printStackTrace();
			created = new Date(0);
			lastEdited = new Date(0);
		}
		String id = o.getString(tagSurveyItemId);

		return new SurveyItem(name, created, lastEdited, id);
	}

	public static Vote parseVote(JSONObject o) throws JSONException {
		int negative = o.getInt(tagVoteNegative);
		int neutral = o.getInt(tagVoteNeutral);
		int positive = o.getInt(tagVotePositive);
		int total = o.getInt(tagVoteTotal);

		return new Vote(negative, neutral, positive, total);
	}

	public static Survey parseSurvey(JSONObject oSurvey) throws JSONException {
		String description = oSurvey.getString(tagSurveyDescription);
		String name = oSurvey.getString(tagSurveyName);
		int state = oSurvey.getInt(tagSurveyState);
		int numberChoices = oSurvey.getInt(tagSurveyNumberChoices);
		Date created = null;
		Date lastEdited = null;
		try {
			created = DateTools.parse(oSurvey.getString(tagSurveyItemCreated));
			lastEdited = DateTools.parse(oSurvey.getString(tagSurveyItemLastEdited));
		} catch (ParseException e) {
			e.printStackTrace();
			created = new Date(0);
			lastEdited = new Date(0);
		}
		Member author = getUser(oSurvey.getString(tagSurveyAuthor));
		String id = oSurvey.getString(tagSurveyId);

		Vote v = getVote(id);
		ArrayList<SurveyItem> items = getSurveyItems(id);

		return new Survey(description, name, state, numberChoices, created, lastEdited, author, id, v, items);
	}

	public static Message parseMessage(JSONObject o) throws JSONException{
		String content = o.getString(tagMessageContent);
		Project project = getProject(o.getString(tagMessageProject));
		Member author = getUser(o.getString(tagMessageAuthor));
		Date created = null;
		Date lastEdited = null;
		try {
			created = DateTools.parse(o.getString(tagMessageCreated));
			lastEdited = DateTools.parse(o.getString(tagMessageLastEdited));
		} catch (ParseException e) {
			e.printStackTrace();
			created = new Date(0);
			lastEdited = new Date(0);
		}
		String id = o.getString(tagMessageId);

		return new Message(project, author, created, lastEdited, id, content);
	}
}
