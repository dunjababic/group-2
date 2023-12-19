package rest_client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import model.User;
import utils.StringNames;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RestClient {

    /**
     * --------------------------------------------------------------------
     * ---------------- DO NOT CHANGE THE FOLLOWING CODE! -----------------
     * --------------------------------------------------------------------
     */

    private static RestClient instance;

    private User user;

    /**
     * Private constructor, so that no objects can be created from the outside.
     */
    private RestClient() {
        // set base url
        String SERVER_URL = "http://localhost:4568";
        Unirest.config().defaultBaseUrl(SERVER_URL);
    }

    public static RestClient getRestClient() {
        return getRestClient(false);
    }

    public static RestClient getRestClient(boolean skipServerTest) {
        if (instance == null) {
            instance = new RestClient();
        }
        if (!skipServerTest){
            // Test if RestServer is reachable and show helpful error message if not
            try {
                Unirest.get("/test").asEmpty();
            } catch (UnirestException e) {
                Logger logger = Logger.getLogger("RestServer test");
                logger.warning("RestServer is unreachable - did you start it?");
                throw new RuntimeException("RestServer is unreachable - did you start it?");
            }
        }
        return instance;
    }

    // GET and SET user

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Converts the string in JSON format to a <code>JsonObject</code>.
     *
     * @param jsonString the string in JSON format
     * @return the <code>JsonObject</code>
     */
    private JsonObject mapStringToJsonObject(String jsonString) {
        return new Gson().fromJson(jsonString, JsonArray.class).get(0).getAsJsonObject();
    }

    /**
     * Converts the string representation of an array of JSON objects into a list of
     * <code>JsonObject</code>.
     *
     * @param jsonString the string in JSON format
     * @return the list of <code>JsonObject</code>
     */
    private List<JsonObject> mapStringToJsonObjectList(String jsonString) {
        JsonArray jsonArray = new Gson().fromJson(jsonString, JsonArray.class);

        List<JsonObject> jsonList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonList.add(jsonArray.get(i).getAsJsonObject());
        }
        return jsonList;
    }

    /*
     * ------------------------- CUSTOMER REQUESTS ------------------------
     */

    /**
     * Makes a REST request to the server. Retrieves the user id from the
     * corresponding database table by providing only the email address of the user
     * (is unique).
     *
     * @param email the email of the user
     * @return the result as a <code>JsonObject</code>
     */
    public JsonObject getUserInfoByMail(String email) {
        HttpResponse<JsonNode> jsonResponse = Unirest.get("/customers")
                .queryString(StringNames.email, email)
                .header(StringNames.authorization, user.getAuthorization())
                .asJson();
        if (jsonResponse.getStatus() != 200) {
            return null;
        }
        return mapStringToJsonObject(jsonResponse.getBody().toString());
    }

    /**
     * Makes a REST request to the server. Checks in the corresponding table if the
     * provided user credentials (email and password) are a valid entry, this means
     * if the password belongs to the email address.
     *
     * @param email    the email of the user
     * @param password the corresponding password of the same user
     * @return <code>true</code> if credentials are; <code>false</code> otherwise
     */
    public boolean validClientCredentials(String email, String password) {
        HttpResponse<JsonNode> jsonResponse = Unirest.get("/customers")
                .queryString(StringNames.email, email)
                .queryString(StringNames.password, password)
                .asJson();
        return jsonResponse.getStatus() == 200;
    }

    /**
     * Makes a REST request to the server. Tries to create a new user entry with the
     * provided information in the customer table.
     *
     * @param firstname the first name of the user
     * @param lastname  the last name of the user
     * @param email     the email of the user
     * @return <code>true</code> if user was created successfully;
     * <code>false</code> otherwise
     */
    public boolean createNewUser(String firstname, String lastname, String email, String password) {
        HttpResponse<JsonNode> jsonResponse = Unirest
                .post("/customer/create")
                .queryString(StringNames.firstname, firstname)
                .queryString(StringNames.lastname, lastname)
                .queryString(StringNames.email, email)
                .queryString(StringNames.password, password)
                .asJson();
        return jsonResponse.getStatus() == 201;
    }

    /*
     * --------------------------------------------------------
     * -------------------------- END -------------------------
     * --------------------------------------------------------
     */

    /*
     * TODO: For each endpoint of the RestServer, create a method in this class that
     * makes a request to this endpoint. After the response of the server has been
     * received, check the status of it and depending on the outcome, return a
     * respective value. You can use the method "mapStringToJsonObject" and
     * "mapStringToJsonObjectList" to map the response to a JsonObject or a list of
     * JsonObjects respectively.
     *
     * Look at the implementation of the request methods in the Cinema Case if you are
     * unclear how you should write the function.
     *
     * Sensitive data like the reservations of one specific customer should not be
     * available for all people but only for that specific customer who must provide
     * his/her correct credentials in order to retrieve this information. For
     * realizing this concept, authorization can be used and must be included for
     * several request to the REST server.
     * For requests to endpoints that query the "reservation" table (might be a different
     * name for your application), make sure you also include the
     * authorization string (you can simply use .header("Authorization",
     * user.getAuthorization())) in the request), as shown in the Cinema Case.
     *
     * Remember to use the query parameter keys from the StringNames class and don't
     * hardcode them.
     *
     * You can use the following template as a starting point for each request
     * method. Keep in mind that you might need to change the REST method (get,
     * post, put, delete).
     */

    public List<JsonObject> requestEndpoint(int queryParam1, String queryParam2) {
        HttpResponse<JsonNode> jsonResponse = Unirest
                .get("/endpoint")
                .queryString(StringNames.query1, queryParam1)
                .queryString(StringNames.query2, queryParam2)
                .asJson();
        if (jsonResponse.getStatus() != 200) {
            return null;
        }
        return mapStringToJsonObjectList(jsonResponse.getBody().toString());
    }

}
