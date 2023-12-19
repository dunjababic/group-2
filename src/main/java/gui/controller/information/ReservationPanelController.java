package gui.controller.information;

import gui.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class ReservationPanelController extends Controller {

    // ------------------ class attributes ------------------
    // only used, if previous scene was profile scene
    // TODO: possibly adjust the attribute names or add/substitute additional
    // attributes
    private int reservationId;
    private int oldReservedSeatAmount;

    // variables for storing current ids
    private int cinemaId;
    private int movieId;
    private int datePlaytimeId;
    // ------------------------------------------------------

    // ------------------ FXML components ------------------
    /*
     * TODO: adjust the names of the following attributes and add more labels or
     * other components (of course also adapt the corresponding fxml-layout) if you
     * need them
     *
     * Make sure you also rename the fx:id of the corresponding components in the
     * ReservationPanel.fxml file to the same names.
     */
    @FXML
    private Label ticketReservationLabel;
    @FXML
    private Label cinemaLabel;
    @FXML
    private Label movieLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label playtimeLabel;
    @FXML
    private ChoiceBox<Integer> seatAmountChoiceBox;

    // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
    @FXML
    private Button reserveButton;
    @FXML
    private Label noModificationLabel;

    @FXML
    private HBox noAccountBox;
    @FXML
    private Button createAccountButton;
    @FXML
    private Button logInButton;
    // ---------------------------------- END ----------------------------------

    /**
     * Initializes the text of all labels and corresponding ids.<br>
     * Determines if components for log in and sign up need to be shown. If user is
     * not logged in these components are shown, otherwise not.<br>
     * Loads more information (for example old seat amount or previously selected
     * seat amount) if the previous scene was either the profile scene or the
     * reservation scene.
     */
    @Override
    public <T> void initializeSceneData(List<T> data) {
        // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
        // if current session is null, ask user to create new account, otherwise
        // reservations is made in logged in account directly
        if (restClient.getUser() == null) {
            noAccountBox.setVisible(true);
            reserveButton.setDisable(true);
        } else {
            noAccountBox.setVisible(false);
        }
        // ---------------------------------- END ----------------------------------
        /*
         * TODO: for each fxml-component you defined above, set the text according to
         * the provided data (this was passed from the InformationPanelController to
         * this method)
         *
         * Cinema Case: cinemaLabel.setText((String) data.get(0)); ...
         * dateLabel.setText((String) data.get(3));
         */
        // initialize text of labels
        cinemaLabel.setText((String) data.get(0));
        movieLabel.setText((String) data.get(1));
        playtimeLabel.setText((String) data.get(2));
        dateLabel.setText((String) data.get(3));

        /*
         * TODO: similar to the FXML-components from above, set the value of the global
         * ids to its according values that were provided in "data" (this was passed
         * from the InformationPanelController to this method)
         *
         * Cinema Case: cinemaId = Integer.valueOf((String) data.get(4)); ...
         * datePlaytimeId = Integer.valueOf((String) data.get(6));
         *
         */
        // initialize ids
        cinemaId = Integer.parseInt((String) data.get(4));
        movieId = Integer.parseInt((String) data.get(5));
        datePlaytimeId = Integer.parseInt((String) data.get(6));

        int selectedSeats = 1;
        // if previous page was profile scene, this means the user modifies a
        // reservation, load more data and change several things
        if (sceneNavigator.getPreviousScene() != null
                && sceneNavigator.getPreviousScene().equals(sceneNavigator.PROFILE)) {
            // TODO: adjust which data gets read additionally, depending on which
            // information you need to modify a reservation
            reservationId = Integer.parseInt((String) data.get(7));
            oldReservedSeatAmount = Integer.parseInt((String) data.get(8));
            ticketReservationLabel.setText("Change ticket reservation");
            reserveButton.setText("Change reservation");
            selectedSeats = oldReservedSeatAmount;
        }

        /*
         * TODO: This method gets called to set up the ChoiceBox with the amount of seats the
         * user can reserve. Depending on your case, you might want to adjust this
         * method to your needs, you may leave it out completely or freshly define your
         * own method.
         */
        setUpSeatSelection(selectedSeats);
    }

    /**
     * Sets up drop down menu where the amount of seats are selected. If more than
     * 10 seats are available, the range for the menu is 1 to 10. Otherwise, checks
     * if user modifies a reservation in which case the end number of the range must
     * be checked. If user creates a new reservations, checks if amount of free
     * seats is smaller than 10 and sets the end number of the range to this amount.
     */
    private void setUpSeatSelection(int selectedSeats) {
        /*
         * Cinema Case: int freeSeats =
         * restClient.getDatePlaytimeInfo(datePlaytimeId).get("freeSeats").getAsInt();
         *
         * Retrieves from the database how many free seats are available for the
         * selected playtime of the movie.
         */
        int freeSeats = 10;

        // check if user is modifying a reservation (oldReservedSeatAmount != 0) and
        // only less than 10 seats are available
        int seatMax = 10;
        if (oldReservedSeatAmount != 0 && freeSeats < 10) {
            if (oldReservedSeatAmount + freeSeats < 10) {
                seatMax = oldReservedSeatAmount + freeSeats;
            }
        } else if (freeSeats < 10) { // user makes new reservation -> check if only less than 10 seats are available
            seatMax = freeSeats;
        }

        // initializes choice box with previously computed values
        int[] seatOptions = IntStream.rangeClosed(1, seatMax).toArray();
        for (int i : seatOptions) {
            seatAmountChoiceBox.getItems().add(i);
        }
        seatAmountChoiceBox.setValue(selectedSeats);
    }

    /**
     * Is called when the user clicks the reserve button. <br>
     * <br>
     * Retrieves the selected amount of seats and sets up a list of data to pass to
     * the next scene. <br>
     * If the user came from the profile scene (this means the user wanted to modify
     * a reservation), checks if the amount of seats change and if yes, requests the
     * REST client to modify the reservation entry in the database. <br>
     * If the user came from the cinema information scene, requests the REST client
     * to create a new reservation in the database. <br>
     * In both cases the next scene is loaded which shows a message showing the
     * success of the creation/modification of the reservation.
     */
    @FXML
    public void onReserveButtonClicked() {
        /*
         * Get new amount of seats to reserve.
         *
         * TODO: In your application you may have another type of data which specifies a
         * reservation. In this case you need to make an adjustment here to retrieve the
         * respective value.
         */
        // get new amount of seats to reserve
        int seatAmount = this.seatAmountChoiceBox.getValue();

        List<String> controllerData = new ArrayList<>();
        // if previous scene was profile scene, load corresponding successful panel
        if (sceneNavigator.getPreviousScene() != null
                && sceneNavigator.getPreviousScene().equals(sceneNavigator.PROFILE)) {
            if (seatAmount == oldReservedSeatAmount) {
                noModificationLabel.setText("No changes were performed.");
            } else {
                /*
                 * TODO: make the modification for the reservation in the database by calling a
                 * respective method in the RestClient which performs this operation
                 *
                 * Cinema Case: restClient.modifyReservation(reservationId, seatAmount);
                 */

                controllerData.add("Change of reservation successful");
                // TODO: adjust the text message for the success panel
                controllerData.add("The amount of reserved tickets was successfully changed.");
                controllerData.add("Continue");
                sceneNavigator.loadSceneToMainWindow(sceneNavigator.SUCCESS_PANEL, controllerData);
            }
        } else { // otherwise load different successful panel
            /*
             * TODO: make the reservation in the database by calling a respective method in
             * the RestClient which performs this operation
             *
             * Cinema Case: restClient.createNewReservation(datePlaytimeId,
             * Integer.valueOf(seatAmount));
             */

            controllerData.add("Reservation successful");
            // TODO: adjust the text message for the success panel
            controllerData.add("Tickets were successfully reserved for the chosen film and time.");
            controllerData.add("Continue");
            controllerData.add(sceneNavigator.PROFILE);
            sceneNavigator.loadSceneToMainWindow(sceneNavigator.SUCCESS_PANEL, controllerData);
        }
    }

    /**
     * Sets the reservation scene (the current scene) as the previous scene in the
     * SceneNavigator and also sets the currently selected data (ciname, movie,
     * playtime, date, seats) as the date of the previous scene. This way, the user
     * will return to the reservation scene (after log in/sign up) and see the same
     * data as before.
     */
    public void setPreviousSceneInformation() {
        sceneNavigator.setPreviousScene(sceneNavigator.RESERVATION);
        /*
         * TODO: define an array "data" according to the below one that contains all
         * information in the correct order which are retrieved in the method
         * initializeSceneData of this class (ReservationPanelController). Make sure
         * that the length of the array and the amount of time you retrieve data from it
         * in initializeSceneData is the same.
         *
         * This should be equal to the array which you pass in the method
         * onReserveButtonClicked from the InformationPanelController to the
         * ReservationPanelController.
         */
        String[] data = {cinemaLabel.getText(), movieLabel.getText(), playtimeLabel.getText(), dateLabel.getText(),
                cinemaId + "", movieId + "", datePlaytimeId + ""};
        sceneNavigator.setDataPreviousScene(Arrays.asList(data));
    }

    /*
     * --------------------------------------------------------------------
     * --------------- DO NOT CHANGE THE FOLLOWING METHODS! ---------------
     * --------------------------------------------------------------------
     */

    /**
     * Is called when the user clicks the cancel button. <br>
     * <br>
     * Loads the profile scene, if the user previously came from the profile scene
     * (this means the user wanted to modify a reservation).<br>
     * Loads the cinema information scene with correctly set data (only cinema id),
     * if the user previously came from the cinema information scene (this means the
     * user wanted to make a new reservation).
     */
    @FXML
    public void onCancelButtonClicked() {
        // if previous scene was profile scene, return to it
        if (sceneNavigator.getPreviousScene() != null
                && sceneNavigator.getPreviousScene().equals(sceneNavigator.PROFILE)) {
            sceneNavigator.setPreviousScene(null);
            sceneNavigator.setDataPreviousScene(null);
            sceneNavigator.loadSceneToMainWindow(sceneNavigator.PROFILE, null);
        } else { // otherwise return to cinema information scene
            sceneNavigator.loadSceneToMainWindow(sceneNavigator.INFO, Collections.singletonList(cinemaId));
        }
    }

    /**
     * Is called when the user clicks the create account button. <br>
     * <br>
     * Sets the previous scene information in the SceneNavigator to the reservation
     * scene, so that after the account is created, the user is redirected to the
     * reservation scene again. Then loads the sign up dialog scene to the GUI.
     */
    @FXML
    public void onCreateAccountButtonClicked() {
        // set up information about previous scene and call sign up scene
        setPreviousSceneInformation();
        sceneNavigator.loadCompleteWindow(sceneNavigator.SIGN_UP_DIALOG,
                (Stage) createAccountButton.getScene().getWindow());
    }

    /**
     * Is called when the user clicks the log in button. <br>
     * <br>
     * Sets the previous scene information in the SceneNavigator to the reservation
     * scene, so that user logged in, the user is redirected to the reservation
     * scene again. Then loads the log in dialog scene to the GUI.
     */
    @FXML
    public void onLogInButtonClicked() {
        // set up information about previous scene and call log in scene
        setPreviousSceneInformation();
        sceneNavigator.loadCompleteWindow(sceneNavigator.LOG_IN_DIALOG, (Stage) logInButton.getScene().getWindow());
    }

    /*
     * --------------------------------------------------------
     * -------------------------- END -------------------------
     * --------------------------------------------------------
     */

}
