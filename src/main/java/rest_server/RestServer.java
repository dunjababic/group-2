package rest_server;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import model.DatabaseConnector;
import org.jetbrains.annotations.NotNull;
import utils.StringNames;
import utils.Utils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public class RestServer {

    // reach server under: http://localhost:4568/ (simply type it in your web browser)

    private static DataValidation dataVal;
    private final Javalin javalinApp;
    private DatabaseConnector dbConnector;


    public RestServer(DatabaseConnector dbConnector, DataValidation dataValidation) {
        this(dbConnector, dataValidation, 4568);
    }

    public RestServer(DatabaseConnector dbConnector, DataValidation dataValidation, int port) {
        this.dbConnector = dbConnector;
        dataVal = dataValidation;

        Gson gson = new Gson();
        JsonMapper gsonMapper = new JsonMapper() {
            @Override
            public @NotNull String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj, type);
            }

            @Override
            public <T> @NotNull T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                return gson.fromJson(json, targetType);
            }
        };
        this.javalinApp = Javalin.create(config -> config.jsonMapper(gsonMapper)).start(port);
        defineRoutes();
    }

    public static void main(String[] args) {
        DatabaseConnector dbConnector = new DatabaseConnector("reservation_system");
        new RestServer(dbConnector, new DataValidation(dbConnector));

    }

    public void setDbConnectorAndDataValidator(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
        dataVal = new DataValidation(dbConnector);
    }

    public void stopServer() {
        javalinApp.stop();
    }

    public void defineRoutes() {
        javalinApp.get("test", context -> context.result("Test successfull, server is reachable!"));
        /*
         * TODO: Implement all REST server endpoints you need here (in the method
         * routes()). Do not forget to include data validation and set the status
         * correctly. For every endpoint either return the requested data or a message
         * telling the error if the data could not be retrieved.
         *
         * Orient yourself on the implementation of the endpoints in the Cinema Case.
         *
         * Sensitive data like the reservations of one specific customer should not be
         * available for all people but only for that specific customer who must provide
         * his/her correct credentials in order to retrieve this information. For
         * realizing this concept, authorization can be used and must be applied for
         * several endpoints of this REST server. <br>
         * For all endpoints that access the "reservation" table (might be a different
         * name for your application), make sure you check for valid authorization of
         * the user (you can use the method dataVal.isUserAuthorized for this), as shown
         * in the Cinema Case. It can also be the case that you must include
         * authorization for other endpoints which you define on your own.
         *
         * You can use the following template as a starting point for each endpoint.
         * Keep in mind that you might need to change the REST method (get, post, put,
         * delete).
         */

        javalinApp.get("/endpoint", context -> {
            // obtain query parameters
            String queryParam1 = context.queryParam(StringNames.query1);
            String queryParam2 = context.queryParam(StringNames.query2);
            List<Map<String, Object>> queryResult = null;

            // data validation
            if (queryParam1 != null && !dataVal.isValidId(queryParam1)) {
                context.status(400);
                context.json(new String[] { "Format of query parameter 1 not correct."});
                return;
            }
            if (queryParam2 != null && !dataVal.isValidId(queryParam2)) {
                context.status(400);
                context.json(new String[] { "Format of query parameter 2 not correct."});
                return;
            }

            /**
             * Check which query parameters are given.
             *
             * You can leave the conditions away, if you do not need to check for a
             * combination of query parameters and want to perform the same operations for
             * every request to this endpoint.
             */

            if (queryParam1 == null && queryParam2 == null) {
                // for each case either return error with message
                context.status(400);
                context.json(new String[] { "At least one parameter is required."});
                return;
            } else if (queryParam1 != null && queryParam2 == null) {
                // or make request to the database retrieving the requested information
                queryResult = dbConnector.executeSelectQuery(new String[] { "*" },
                        new String[] { DatabaseConnector.CUSTOMERS }, null, "email = ?", new String[] { queryParam1 });
            } else if (queryParam1 == null && queryParam2 != null) {
                context.status(400);
                context.json(new String[] { "Other combination of query parameters needed."});
                return;
            } else {
                context.status(400);
                context.json(new String[] { "Other combination of query parameters needed."});
                return;
            }

            // check for empty result set
            if (queryResult.isEmpty()) {
                context.status(404);
                context.json(new String[] { "Result was empty."});
                return;
            }
            context.json(queryResult);
        });

        /*
         * --------------------------------------------------------------------
         * -------------- DO NOT CHANGE THE FOLLOWING ENDPOINTS! --------------
         * --------------------------------------------------------------------
         */

        // ------------------------------------------------------------------------------------------------------------------------
        // CUSTOMER REQUESTS

        /*
         * returns customer information by email (with query parameter email) or checks
         * if correct client credentials are provided (with query parameters email and
         * password)
         *
         * path: /customers?email=value&password=value
         */
        javalinApp.get("/customers", context -> {
            String email = context.queryParam(StringNames.email);
            String password = context.queryParam(StringNames.password);
            String authString = context.header(StringNames.authorization);
            List<Map<String, Object>> queryResult;

            // data validation
            if (email != null && !Utils.isValidEmailAddress(email)) {
                context.status(400);
                context.json(new String[]{"Email address format not correct."});
                return;
            }
            if (password != null && !Utils.isValidPassword(password)) {
                context.status(400);
                context.json(new String[]{"Password format not correct."});
                return;
            }

            if (email == null && password == null) {
                context.status(400);
                context.json(new String[]{"At least e-mail is required as parameter."});
                return;
            } else if (email != null && password == null) { // get customer information by email
                /*
                 * SELECT *
                 * FROM customers
                 * WHERE email = email
                 */
                queryResult = dbConnector.executeSelectQuery(new String[]{"*"},
                        new String[]{DatabaseConnector.CUSTOMERS}, null, "email = ?", new String[]{email});
                if (queryResult.isEmpty()) { // email not found
                    context.status(404);
                    context.json(new String[]{"E-mail not found."});
                    return;
                } else {
                    // check if user is authorized
                    String customerId = String.valueOf(queryResult.get(0).get("id"));
                    if (authString == null || !dataVal.isUserAuthorized(authString, customerId)) {
                        context.status(401);
                        context.json(new String[]{"User is not authorized to perform this action."});
                        return;
                    }
                }
            } else if (email == null && password != null) {
                context.status(400);
                context.json(new String[]{"E-mail required when password is given."});
                return;
            } else { // check client credentials
                /*
                 * SELECT *
                 * FROM customers
                 * WHERE email = email and password = password
                 */
                queryResult = dbConnector.executeSelectQuery(new String[]{"*"},
                        new String[]{DatabaseConnector.CUSTOMERS}, null, "email = ? and password = ?",
                        new String[]{email, password});
            }
            // check for empty result set
            if (queryResult.isEmpty()) {
                context.status(404);
                context.json(new String[]{"E-mail not found or no valid credentials given."});
                return;
            }
            context.json(queryResult);
        });
        /*
         * creates a new customer in the database table CUSTOMERS with given attributes
         * (specified in query parameters)
         *
         * path:
         * /createCustomer?firstname=value&lastname=value&email=value&password=value
         */
        javalinApp.post("/customer/create", context -> {
            String firstname = context.queryParam(StringNames.firstname);
            String lastname = context.queryParam(StringNames.lastname);
            String email = context.queryParam(StringNames.email);
            String password = context.queryParam(StringNames.password);

            if (firstname != null && lastname != null && email != null && password != null) {
                // data validation
                if (!Utils.isAlpha(firstname) || !Utils.isAlpha(lastname)) {
                    context.status(400);
                    context.json(new String[]{"Firstname and lastname can only contain letters."});
                    return;
                }
                if (!Utils.isValidEmailAddress(email)) {
                    context.status(400);
                    context.json(new String[]{"Email address format not correct."});
                    return;
                }
                if (!Utils.isValidPassword(password)) {
                    context.status(400);
                    context.json(new String[]{"Password format not correct."});
                    return;
                }

                /*
                 * INSERT INTO customers (firstname, lastname, email, password)
                 * VALUES (firstname, lastname, email, password)
                 */
                if (!dbConnector.executeInsertQuery(DatabaseConnector.CUSTOMERS,
                        new String[]{StringNames.firstname, StringNames.lastname, StringNames.email,
                                StringNames.password},
                        new String[]{firstname, lastname, email, password})) {
                    context.status(400);
                    context.json(new String[]{"E-mail address does already exist."});
                    return;
                }

                context.status(201);
                context.json(new String[]{"Customer created successfully!"});
            } else {
                context.status(400);
                context.json(new String[]{"For creating a customer, firstname, lastname, " +
                        "e-mail and password is required."});
            }
        });

        /*
         * --------------------------------------------------------
         * -------------------------- END -------------------------
         * --------------------------------------------------------
         */
    }

}
