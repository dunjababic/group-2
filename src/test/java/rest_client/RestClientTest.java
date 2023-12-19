package rest_client;

import kong.unirest.*;
import model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.StringNames;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestClientTest {

    // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
    private static final RestClient client = RestClient.getRestClient(true);
    @Mock
    JsonNode value;
    @Mock
    private GetRequest getRequest;
    @Mock
    private HttpRequestWithBody requestWithBody;
    @Mock
    private HttpResponse<JsonNode> httpResponse;
    private static MockedStatic<Unirest> mockedUnirest;

    @BeforeAll
    public static void initMock() {
        mockedUnirest = mockStatic(Unirest.class);
    }

    @AfterAll
    public static void deregisterMock() { mockedUnirest.close();}
    // ---------------------------------- END ----------------------------------

    /**
     * As you know from the lecture, unit testing aims at only testing one component
     * at a time. The methods of the RestClient class are highly dependent on the
     * implementation of the RestServer. This means, if we are testing one method of
     * RestClient, we are actually also testing the correct behavior of the
     * RestServer. In order to only test for the correct functionality of a
     * RestClient method, we can simulate the RestServer, so that we know what it
     * will always return as a response.
     * <p>
     * This simulation can be achieved by the usage of so-called mock objects. A
     * mock object simulates a specific method that is used in the method which we
     * want to test. The behavior of this mock object is set before it is actually
     * used, and therefore we can always be sure what the result of the execution of
     * this simulated method will be. This is just a very brief explanation of the
     * concept of mock objects. For further information you can refer for example to
     * https://www.vogella.com/tutorials/Mockito/article.html
     * <p>
     * In our case this means, we are creating a mock object, to simulate the
     * response of the server for a specific endpoint. For example if we want to
     * test the method restClient.requestCinemas() we only need to simulate the
     * endpoint /cinemas of the RestServer because this is the only endpoint that is
     * used (this is performed by using the when(...).thenReturn(...) structure as
     * shown in all test cases below).
     * <p>
     * After creating the mock object, we can then perform the actual test by
     * calling the method restClient.requestCinemas() and asserting afterwards that
     * the result returned by the mock object is equal to the result we expect.
     * <p>
     * In principle the structure of all test cases is the same for every REST
     * client method:
     * <p>
     * 1. Setup mock object using when and thenReturn. <br>
     * 2. Make actual request to REST client. <br>
     * 3. Assert that returned result is equal to expected result.
     * <p>
     * Because of this, comments that describe which steps are performed, will only
     * be provided within the first method.
     */

    /* TODO: Add test methods for the implemented methods of the class RestClient.
     * Make sure that you are writing a separate test method for every possible way
     * of requesting a method. This normally means, for one method you have to write
     * two test cases: the first case tests for a successful response of the
     * RestServer i.e. the server returns a status of 200/201 and the second case
     * tests for an unsuccessful response of the RestServer i.e. the server returns
     * a status of 400/401/404.
     *
     * A good example that shows the necessary test cases for one RestClient method
     * are the test methods in the section CINEMA REQUESTS of the Cinema Case
     * implementation.
     *
     * If authorization is required, make sure you are also passing this correctly
     * to the mock object and the actual request to the RestClient. For an example
     * usage of this, refer to the method testCreateNewReservationStatus201() in the
     * Cinema Case implementation.
     *
     * The creation of the mock object is different if you are testing a method that
     * uses the GET method than if you use the PUT, POST or DELETE method.
     * Therefore, there are two different templates which show you how to create the
     * mock object in each case. You can use the following templates as a starting
     * point for testing your RestClient methods.
     */

    @Test
    public void testCaseTemplateWithGet() {
        // testGetUserInfoByMailStatus401 in CinemaCase
        String email = "noah.const@web.de";
        client.setUser(new User(email, "9ikwelf%"));
        String auth = "Basic " + Base64.getEncoder().encodeToString("noah.const@web.de:9ikwelf%".getBytes());

        when(Unirest.get("/customers")).thenReturn(getRequest);
        when(getRequest.queryString(StringNames.email, email)).thenReturn(getRequest);
        when(getRequest.header(StringNames.authorization, auth)).thenReturn(getRequest);
        when(getRequest.asJson()).thenReturn(httpResponse);
        when(httpResponse.getStatus()).thenReturn(401);

        assertNull(client.getUserInfoByMail(email));
    }

    @Test
    public void testCaseTemplateWithPost() {
        /*
         * If you need to use another REST method than GET you need to use another
         * procedure to create the mock object. This is shown in the following.
         */
        // testCreateNewUserStatus201 in CinemaCase
        String firstname = "Max";
        String lastname = "Mustermann";
        String email = "max.mt@web.de";
        String password = "dieSonne";

        when(Unirest.post("/customer/create")).thenReturn(requestWithBody);
        when(requestWithBody.queryString(StringNames.firstname, firstname)).thenReturn(requestWithBody);
        when(requestWithBody.queryString(StringNames.lastname, lastname)).thenReturn(requestWithBody);
        when(requestWithBody.queryString(StringNames.email, email)).thenReturn(requestWithBody);
        when(requestWithBody.queryString(StringNames.password, password)).thenReturn(requestWithBody);
        when(requestWithBody.asJson()).thenReturn(httpResponse);
        when(httpResponse.getStatus()).thenReturn(201);

        assertTrue(client.createNewUser(firstname, lastname, email, password));
    }
}
