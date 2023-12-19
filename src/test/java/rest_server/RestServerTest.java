package rest_server;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import model.DatabaseConnector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.StringNames;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestServerTest {

    // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
    private static RestServer restServer;
    private static final int restServerTestPort = 4569;

    @BeforeAll
    public static void setUpConnectionAndVariables() {
        Unirest.config().defaultBaseUrl("http://localhost:" + restServerTestPort);

        DatabaseConnector mockDbConnector = mock(DatabaseConnector.class);
        restServer = new RestServer(mockDbConnector, new DataValidation(mockDbConnector), restServerTestPort);
    }

    @AfterAll
    public static void tearDown() {
        restServer.stopServer();
    }

    /**
     * Creates a new mock object for the class
     * <code>{@link DatabaseConnector}</code> and defines which output should be
     * returned for the given parameters when executing a SELECT query on the
     * database.
     *
     * @param projection    the array with column names; cannot be
     *                      <code>null</code>; can contain only "*" for selecting
     *                      all columns
     * @param tables        the array with database table names; cannot be
     *                      <code>null</code>
     * @param tableAlias    the array with table name aliases; can be
     *                      <code>null</code> to omit aliases
     * @param selection     the array with conditions; can be <code>null</code> to
     *                      omit conditions
     * @param selectionArgs the array with the corresponding values for the
     *                      selection; can be <code>null</code> if no parameter
     *                      values are needed
     * @param addListItem   <code>true</code> if an item should be added to the list
     *                      which is returned by the SELECT query;
     *                      <code>false</code> otherwise
     * @param key           the string defining the key of the map which is added to
     *                      the output list
     * @param value         the corresponding value to the key
     * @return the create mock object for the class
     * <code>{@link DatabaseConnector}</code>
     */
    public DatabaseConnector createAndAssignMockObjectSelectQuery(String[] projection, String[] tables,
                                                                  String[] tableAlias, String selection, String[] selectionArgs, boolean addListItem, String key,
                                                                  Object value) {
        // define mock database connection object
        DatabaseConnector mockDbConn = mock(DatabaseConnector.class);

        // define mock list to return
        List<Map<String, Object>> outList = new ArrayList<>();
        if (addListItem) {
            Map<String, Object> map = new HashMap<>();
            map.put(key, value);
            outList.add(map);
        }

        // specify when-then-return structure
        when(mockDbConn.executeSelectQuery(projection, tables, tableAlias, selection, selectionArgs))
                .thenReturn(outList);

        // assign mock database connection to server
        restServer.setDbConnectorAndDataValidator(mockDbConn);

        return mockDbConn;
    }

    /**
     * Adds a SELECT query to the given mock object of
     * <code>{@link DatabaseConnector}</code>. The output that is returned by
     * executing the SELECT query with the given parameters is defined as a list
     * that contains maps with the given keys and values (similar to
     * <code>{@link #createAndAssignMockObjectSelectQuery(String[], String[], String[], String, String[], boolean, String, Object)}</code>).
     *
     * @param mockDbConn    the mock object to which a SELECT query is added
     * @param projection    the array with column names; cannot be
     *                      <code>null</code>; can contain only "*" for selecting
     *                      all columns
     * @param tables        the array with database table names; cannot be
     *                      <code>null</code>
     * @param tableAlias    the array with table name aliases; can be
     *                      <code>null</code> to omit aliases
     * @param selection     the array with conditions; can be <code>null</code> to
     *                      omit conditions
     * @param selectionArgs the array with the corresponding values for the
     *                      selection; can be <code>null</code> if no parameter
     *                      values are needed
     * @param keys          the list defining the keys of the maps which are added
     *                      to the output list
     * @param values        the corresponding values to each of the keys
     */
    public void addMockSelectQuery(DatabaseConnector mockDbConn, String[] projection, String[] tables,
                                   String[] tableAlias, String selection, String[] selectionArgs, String[] keys, Object[] values) {
        // define mock list to return
        List<Map<String, Object>> outList = new ArrayList<>();

        if (keys != null && values != null) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], values[i]);
            }
            outList.add(map);
        }

        // specify when-then-return structure
        when(mockDbConn.executeSelectQuery(projection, tables, tableAlias, selection, selectionArgs))
                .thenReturn(outList);
    }

    /**
     * Creates a new mock object for the class
     * <code>{@link DatabaseConnector}</code> and defines which output should be
     * returned for the given parameters when executing an INSERT query on the
     * database.
     *
     * @param table       the name of the table; cannot be <code>null</code>
     * @param columns     the array containing the columns to insert values; cannot
     *                    be <code>null</code>
     * @param values      the array with the values to insert; cannot be
     *                    <code>null</code>; must have the same length as columns
     * @param returnValue <code>true</code> if the insertion should be mocked as
     *                    successful; <code>false</code> otherwise
     * @return the create mock object for the class
     * <code>{@link DatabaseConnector}</code>
     */
    public DatabaseConnector createAndAssignMockObjectInsertQuery(String table, String[] columns, String[] values,
                                                                  boolean returnValue) {
        // define mock database connection object
        DatabaseConnector mockDbConn = mock(DatabaseConnector.class);

        // specify when-then-return structure
        when(mockDbConn.executeInsertQuery(table, columns, values)).thenReturn(returnValue);

        // assign mock database connection to server
        restServer.setDbConnectorAndDataValidator(mockDbConn);

        return mockDbConn;
    }

    /**
     * Adds an UPDATE query to the given mock object of
     * <code>{@link DatabaseConnector}</code>. Defines the output that is returned
     * by executing the UPDATE query with the given parameters as the given
     * returnValue.
     *
     * @param mockDbConn       the mock object to which an UPDATE query is added
     * @param table            the name of the table; cannot be <code>null</code>
     * @param modification     the array with columns that are changed; cannot be
     *                         <code>null</code>;
     * @param modificationArgs the array with the corresponding values for the
     *                         modification; can be <code>null</code> if no
     *                         modification parameter are needed
     * @param selection        the array with conditions; can be <code>null</code>
     *                         to omit conditions
     * @param selectionArgs    the array with the corresponding values for the
     *                         selection; can be <code>null</code> if no parameter
     *                         values are needed
     * @param returnValue      <code>true</code> if the update should be mocked as
     *                         successful; <code>false</code> otherwise
     */
    public void addMockUpdateQuery(DatabaseConnector mockDbConn, String table, String[] modification,
                                   String[] modificationArgs, String selection, String[] selectionArgs, boolean returnValue) {
        // specify when-then-return structure
        when(mockDbConn.executeUpdateQuery(table, modification, modificationArgs, selection, selectionArgs))
                .thenReturn(returnValue);
    }

    /**
     * Adds a DELETE query to the given mock object of
     * <code>{@link DatabaseConnector}</code>. Defines the output that is returned
     * by executing the DELETE query with the given parameters as the given
     * returnValue.
     *
     * @param mockDbConn    the mock object to which a DELETE query is added
     * @param table         the name of the table to delete from; cannot be
     *                      <code>null</code>
     * @param selection     the array with conditions; can be <code>null</code> to
     *                      delete all records in table
     * @param selectionArgs the array with the corresponding values for the
     *                      selection; can be <code>null</code> if no parameter
     *                      values are needed
     * @param returnValue   <code>true</code> if the deletion should be mocked as
     *                      successful; <code>false</code> otherwise
     */
    public void addMockDeleteQuery(DatabaseConnector mockDbConn, String table, String selection, String[] selectionArgs,
                                   boolean returnValue) {
        // specify when-then-return structure
        when(mockDbConn.executeDeleteQuery(table, selection, selectionArgs)).thenReturn(returnValue);
    }

    // ---------------------------------- END ----------------------------------

    /**
     * The implemented endpoints of the class RestServer which should be tested in
     * this class are all highly dependent on the class DatabaseConnector. Therefore,
     * it is again necessary to use mock objects in order to be sure to only test
     * the functionality of the RestServer. This is the same approach as explained
     * in the class RestClientTest however in this test class the creation of the
     * mock objects is slightly different.
     * <p>
     * For example if we want to test the endpoint /cinemas (without query
     * parameters) we only need to simulate one SELECT query on the database which
     * is done by using the method createAndAssignMockObjectSelectQuery. Here, we
     * need to make sure that the correct parameters for the SELECT query, that will
     * be later used in the RestServer, are passed to the method.
     * <p>
     * Afterwards, we can normally proceed by making the actual request to the
     * RestServer endpoint and asserting that the response contains the correct
     * data.
     * <p>
     * Again, in principle the structure of all test cases is the same for every
     * endpoint of the REST server:
     * <p>
     * 1. Setup mock object using predefined methods (possibly calling more than one
     * if multiple queries on the database are made).
     * 2. Make actual request to REST server.
     * 3. Assert that returned response has the correct status and the data is equal
     * to expected result.
     * <p>
     * Because of this, comments that describe which steps are performed, will only
     * be provided within the first method.
     */

    /* TODO: Add test methods for the implemented endpoints in the RestServer. Make
     * sure that you are writing a separate test method for every possible way of
     * requesting an endpoint. This means, if you for example have an endpoint that
     * can either be accessed without query parameters or with one query parameter,
     * you need to write a separate test method for each possibility. Also, test for
     * wrong or invalid inputs for the query parameters i.e. if you expect a String
     * you can provide an int and check that the response is still what you would
     * expect.
     *
     * A good example that captures all the mentioned criteria (no query parameters
     * vs. 1 query parameter, correct query parameter vs invalid/wrong data type)
     * are the test methods for the CINEMA REQUESTS in the Cinema Case
     * implementation.
     *
     * If authorization is required, make sure you are also passing this correctly
     * to the mock object and the actual request to the server. For an example usage
     * of this, refer to the method
     * testCreateReservationsWithCustomerIdWrongDatatype() in the Cinema Case.
     *
     * You can use the following template as a starting point for testing each
     * endpoint.
     */
    @Test
    public void testGetUserInformationWithValidAuthorizationAndCredentials() {
        // create mock object and assign SELECT query (make sure you use the correct
        // parameters which are needed in the RestServer, you can copy and paste it from
        // the RestServer)
        DatabaseConnector mockDbConn = createAndAssignMockObjectSelectQuery(new String[]{"*"},
                new String[]{DatabaseConnector.CUSTOMERS}, null, "email = ?", new String[]{"email@test.de"}, true,
                "id", 1);

        // possibly add a second SELECT/INSERT/UPDATE/DELETE query to the same mock
        // object (again make sure you are using the correct parameters which are needed
        // in the RestServer)
        addMockSelectQuery(mockDbConn, new String[]{"*"}, new String[]{DatabaseConnector.CUSTOMERS}, null,
                "id = ? and email = ? and password = ?", new String[]{"1", "email@test.de", "testPassword"},
                new String[]{"key"}, new Object[]{"value"});

        String authorization = "Basic " + Base64.getEncoder().encodeToString("email@test.de:testPassword".getBytes());
        // Make the actual request to the REST server (use the correct REST method
        // (get/post/put/delete)) and pass the necessary query parameters
        HttpResponse<JsonNode> response = Unirest.get("/customers")
                .queryString(StringNames.email, "email@test.de")
                .header(StringNames.authorization, authorization).asJson();

        // assert that the status is as expected
        assertEquals(200, response.getStatus());
        // OR assert that the returned JSON contains the correct information
        assertEquals(1, response.getBody().getArray().getJSONObject(0).getInt("id"));
    }
}
