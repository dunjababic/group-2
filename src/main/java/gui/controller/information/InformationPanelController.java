package gui.controller.information;

import com.google.gson.JsonObject;
import gui.controller.Controller;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import utils.Utils;

import java.util.*;

public class InformationPanelController extends Controller {

    // ------------------ class attributes ------------------
    /**
     * TODO: adjust the names of all attributes
     */
    // variables for storing current ids
    private int cinemaId;
    private int movieId;
    private final List<Integer> movieIds = new ArrayList<>();
    private List<List<String>> datePlaytimeIds = new ArrayList<>();

    // last clicked movie
    private int lastClickedMovieIndex = -1;
    // store last clicked date and playtime index (corresponds to column and row
    // index)
    private int lastClickedDateIndex = 0;
    private int lastClickedPlaytimeIndex = 0;
    // for storing all list views
    private List<ListView<String>> startTimeListViews = new ArrayList<>();
    // ------------------------------------------------------

    // ------------------ FXML components ------------------
    /**
     * The following attributes all describe features of the selected item (cinema).
     * TODO: adjust the names of the following attributes and add more labels or
     * other components (of course also adapt the corresponding fxml-layout) if you
     * need them
     * Make sure you also rename the fx:id of the corresponding components in the
     * InformationPanel.fxml file to the same names.
     */
    @FXML
    private Label cinemaNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private ListView<String> movieListView;
    /**
     * The following attributes all describe features of the selected subitem
     * (movie) that is available for the selected item (cinema).
     * TODO: adjust the names of the following attributes and add more labels or
     * other components (of course then also in the fxml-layout) if you need them
     * Make sure you also rename the corresponding components in the
     * InformationPanel.fxml file to the same names.
     */
    @FXML
    private VBox movieInformationPanel;
    @FXML
    private Label movieNameLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label filmLengthLabel;

    // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
    @FXML
    private GridPane playtimeGridPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ScrollPane playtimeWrapScrollPane;

    @FXML
    private Button reserveButton;
    @FXML
    private Label messageLabel;
    // ------------------------------------------------------

    /**
     * Hides the <code>movieInformationPanel</code> because at the beginning no
     * movie is selected. Is shown later, when user selects a movie.<br>
     * Retrieves delivered data from previous scene and sets the text of all
     * labels.<br>
     * If currently the cinema has no movies to show, create a list with one dummy
     * JSON object containing a message which states that no movies are
     * available.<br>
     * At the end initializes the list view in which all movies are displayed.
     */
    @Override
    public <T> void initializeSceneData(List<T> data) {
        // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
        // at first hide movie information panel
        movieInformationPanel.setVisible(false);
        movieInformationPanel.setMinHeight(0);
        movieInformationPanel.setPrefHeight(0);
        // disable reservation as long as no time is selected
        setReservable(false);
        // get respective elements from data list
        cinemaId = (int) data.get(0);
        // ---------------------------------- END ----------------------------------

        /*
         * TODO: retrieve information of the item on which the user clicked previously
         * and get all subitems (information and id) of this item from the database (via
         * REST client and server)
         * Cinema Case:
         * JsonObject cinemaJson = restClient.requestCinemaInformation(cinemaId);
         * List<JsonObject> movieJson = restClient.requestMoviesOfCinema(cinemaId);
         */
        JsonObject itemJson = null;
        List<JsonObject> subitemsJson = null;

        /*
         * TODO: set the text of all FXML labels for the selected item accordingly. If
         * you previously added more components, make sure you all set them respectively
         * with the data you retrieved via the REST client.
         */
        cinemaNameLabel.setText(itemJson.get("name").getAsString());
        addressLabel.setText(itemJson.get("address").getAsString());
        descriptionLabel.setText(itemJson.get("description").getAsString());

        // if currently no movies are available, show message instead of movie list
        if (subitemsJson == null) {
            /*
             * TODO: adjust text that is shown when no items are available
             */
            ObservableList<String> obsList = createNoItemsAvailableList(movieListView, "Currently no movies available.");
            movieListView.setItems(obsList);
        } else {

            initializeMovieListView(subitemsJson);
        }
    }

    /**
     * Extracts all unique movie names from the list of movies. Determines the
     * corresponding ids for the movies and sets the items of
     * <code>movieListView</code> to the unique movies.
     *
     * @param movieJson the list containing all movies as <code>JsonObject</code>
     */
    private void initializeMovieListView(List<JsonObject> movieJson) {
        // initializes movie list
        /*
         * TODO: adjust "name" to the correct attribute name from the database for
         * describing the title of the subitem
         *
         * Cinema Case: "name" corresponds to a column from the table "movies" and
         * describes the title of the movie
         */
        List<String> uniqueMovies = Utils.getUniqueItems(movieJson, "name");

        // get ids for movies
        for (String movie : uniqueMovies) {
            // iterate over every entry in movie json
            for (JsonObject json : movieJson) {
                // TODO: adjust "name" to the same string as above
                if (json.get("name").getAsString().equals(movie)) {
                    // TODO: adjust "movieId" to the correct column name from the database (possibly
                    // the column name was also renamed which is the case for "movieId"), containing
                    // the id of the subitem
                    movieIds.add(json.get("movieId").getAsInt());
                    break;
                }
            }
        }
        movieListView.setItems(FXCollections.observableArrayList(uniqueMovies));
    }

    /**
     * Is called when the user clicks on an item in the
     * <code>movieListView</code>.<br>
     * <br>
     * Only if the user clicked not on an empty list item and the selected item is
     * different to the previously selected item, retrieves movie information and
     * initializes the <code>movieInformationPanel</code>, including the view for
     * showing the playtimes.
     */
    public void onSubitemListItemClicked() {
        // get clicked movie id
        int clickedIndex = movieListView.getSelectionModel().getSelectedIndex();
        String subitem = movieListView.getSelectionModel().getSelectedItem();

        // if no empty item and different movie is selected than before
        if (clickedIndex != -1 && clickedIndex != lastClickedMovieIndex) {
            lastClickedMovieIndex = clickedIndex;
            movieId = movieIds.get(clickedIndex);
            // disable reserve button and show message
            setReservable(false);

            // --------------- ONLY MAKE CHANGES TO THE CODE IN THIS AREA ---------------
            /*
             * TODO 1: for the selected item (cinema) and subitem (movie), retrieve further
             * information, this means the subsubitems (date) with according details (time),
             * from the database (via REST client and server). Be careful that you include
             * all data and ids that are later necessary to set up the table of subsubitems
             * (dates) with details (times).
             * Cinema Case:<br>
             * List<JsonObject> movieJson =
             * restClient.requestMovieInformationOfCinema(cinemaId, movieId);
             *
             * TODO 2: set the text of all FXML labels for the selected subitem accordingly.
             * If you previously added more components for describing the subitem, make sure
             * you all set them respectively with the data you retrieved via the REST
             * client.
             *
             */
            List<JsonObject> movieJson = null;

            // set retrieved text and set visible
            movieNameLabel.setText(subitem);
            genreLabel.setText(movieJson.get(0).get("genre").getAsString());
            filmLengthLabel.setText(movieJson.get(0).get("length").getAsString() + " min");
            // ---------------------------------- END ----------------------------------

            // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
            initializeViewForSubsubitems(movieJson);

            // set height of panel and show it
            movieInformationPanel.setMinHeight(Control.USE_COMPUTED_SIZE);
            movieInformationPanel.setPrefHeight(Control.USE_COMPUTED_SIZE);
            movieInformationPanel.setVisible(true);

            // automatically scroll to bottom of page
            Animation animation = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(scrollPane.vvalueProperty(), 1)));
            animation.play();
            // ---------------------------------- END ----------------------------------
        }
    }

    /**
     * Initializes the GridPane which contains the playtime information. Retrieves
     * unique dates and sorts them. Adds a column for each date to the GridPane and
     * a ListView containing the times when the movie is shown for this date.
     *
     * @param movieJson the list of <code>JsonObject</code>, each containing
     *                  information of one movie
     */
    public void initializeViewForSubsubitems(List<JsonObject> movieJson) {
        // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
        // reset variables so that previous information will not be regarded
        playtimeGridPane.getChildren().clear();
        playtimeGridPane.getColumnConstraints().clear();
        startTimeListViews = new ArrayList<>();
        datePlaytimeIds = new ArrayList<>();
        // ---------------------------------- END ----------------------------------

        // get unique dates, reformat them and remove dates before today's date
        /*
         * TODO: change "date" to attribute name of the subsubitem which you want to
         * show as the column headings in the "table". Depending on if this is also a
         * date in your case or not, you need to leave out
         * Utils.parseListOfStringsToDates.
         */
        List<Date> dates = Utils.parseListOfStringsToDates(Utils.getUniqueItems(movieJson, "date"),
                Utils.monthDayYearDateFormat);
        Collections.sort(dates);

        // iterate over each date
        for (int i = 0; i < dates.size(); i++) {
            // TODO: adjust datatype and variable name, if you are not using dates
            Date currDate = dates.get(i);
            /*
             * TODO: leave out Utils.dayMonthDateFormat.format if you are not using dates If
             * your data is cut off in the column, specify a larger width (currently 80) for
             * the column.
             */
            initializeTableColumnsAndConstraints(Utils.dayMonthDateFormat.format(currDate), i, 80);

            /*
             * Create list view with all times when movie is played. This corresponds to one
             * column in the table.
             *
             * TODO: Change "date" to the same string as above and possibly leave out
             * Utils.monthDayYearDateFormat.format if you are not using dates. This ensures
             * you only consider the items that belong to the current column.
             */
            List<JsonObject> columnData = Utils.getItemsWithValue(movieJson, "date",
                    Utils.monthDayYearDateFormat.format(currDate));
            /*
             * TODO: change "startTime" and "datePlaytimeId" to according strings for your
             * application. "startTime" corresponds to the details (time) of the subsubitem
             * (date) and "datePlaytimeId" corresponds to the database table column which
             * unambiguously identifies "startTime". Use true if the first string contains
             * time data and false if it contains anything else like names etc.
             */
            initializeDetailListViewAsColumn(columnData, i, "startTime", "datePlaytimeId", true);
        }
    }

    /**
     * Is called when the reserve button is clicked.<br>
     * <br>
     * Retrieves all necessary information for the reservation scene: cinema, movie,
     * date, start time and cinema id, movie id, playtime id. Gathers information in
     * list and shows reservation scene with this information.
     */
    @FXML
    public void onReserveButtonClicked() {
        // retrieve information from GUI
        String movie = movieListView.getSelectionModel().getSelectedItem();
        // * 2 because GridPane also contains the list view for start times
        String date = ((Label) playtimeGridPane.getChildren().get(lastClickedDateIndex * 2)).getText();
        String startTime = startTimeListViews.get(lastClickedDateIndex).getSelectionModel().getSelectedItem();
        String playtimeId = datePlaytimeIds.get(lastClickedDateIndex).get(lastClickedPlaytimeIndex);

        /*
         * TODO: build up the array data with the needed information which is then
         * passed to the reservation scene. Extend the array with extra variables, if
         * you have additional information that must be displayed or are needed in the
         * reservation scene. <br>
         * Keep the order of the data in mind! This must be the same as the order in
         * which you retrieve the data in the initializeSceneData method in the
         * ReservationPanelController class!!
         * Cinema Case: 7 items are passed to the reservation scene and 7 items are then
         * extracted in the ReservationPanelController.
         */
        // load reservation view and initialize with current data
        String[] data = {cinemaNameLabel.getText(), movie, startTime, date, cinemaId + "", movieId + "",
                playtimeId + ""};
        sceneNavigator.loadSceneToMainWindow(sceneNavigator.RESERVATION, Arrays.asList(data));
    }

    /*
     * --------------------------------------------------------------------
     * --------------- DO NOT CHANGE THE FOLLOWING METHODS! ---------------
     * --------------------------------------------------------------------
     */

    /**
     * Is called if there are no items to be displayed in the given ListView. Sets a
     * cell factory to the ListView to change it appearance and creates a
     * <code>ObservableList</code> containing the message to display instead of
     * elements.
     *
     * @param <T>      the type of items in the ListView
     * @param listView the ListView on which to set properties
     * @param text     the text to show in the ListView
     * @return the observable list
     */
    private <T> ObservableList<String> createNoItemsAvailableList(ListView<T> listView, String text) {
        // disable that items are clickable
        listView.setMouseTransparent(true);
        listView.setFocusTraversable(false);

        // change appearance of cell: text is centered and height of item is equal to
        // height of list view
        listView.setCellFactory(lst -> new ListCell<>() {
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				// Create the HBox
				HBox hBox = new HBox();
				hBox.setAlignment(Pos.CENTER);
				hBox.setPrefHeight(listView.getPrefHeight() - 10);

				// Create centered Label
				Label label = new Label((String) item);
				label.setStyle("-fx-font-style: italic;");
				label.setAlignment(Pos.CENTER);

				hBox.getChildren().add(label);
				setGraphic(hBox);
			}
		});
        ObservableList<String> message = FXCollections.observableArrayList();
        message.add(text);
        return message;
    }
    private void initializeTableColumnsAndConstraints(String colName, int i, int width) {
        // add column name label
        Label label = new Label(colName);
        playtimeGridPane.add(label, i, 0);
        label.setPadding(new Insets(5, 0, 5, 5));
        label.getStyleClass().add("boldLabel");

        // set column constraints: every column should take equal width
        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setPrefWidth(width);
        playtimeGridPane.getColumnConstraints().add(colConstraints);
    }

    /**
     * Creates a list view for all playtimes of one date and adds it to the
     * GridPane. Adds all start times as items to the list view and sets the
     * corresponding playtime ids. Adds a click listener for the list view.
     *
     * @param playtimesOfDate the list of all playtimes of one date
     * @param colIndex        the column index for the GridPane
     * @param itemName        the name of the key containing the detail information
     *                        to show in the table
     * @param idName          the name of the key containing the id that identifies
     *                        itemName
     * @param itemIsTime      <code>true</code> if itemName contains time values (in
     *                        12 hour format); <code>false</code> otherwise
     */
    private void initializeDetailListViewAsColumn(List<JsonObject> playtimesOfDate, int colIndex, String itemName,
                                                  String idName, boolean itemIsTime) {
        Map<String, String> columToId = new HashMap<>();
        if (itemIsTime) {
            // map start time to corresponding date playtime id
            Map<String, String> tmp = Utils.mapKeyToAnotherKeyFromJsonList(playtimesOfDate, itemName, idName);
            // copy map and convert format of keys
            for (String key : tmp.keySet()) {
                columToId.put(Utils.convertTime12To24(key), tmp.get(key));
            }
            // sort map by key
            columToId = new TreeMap<>(columToId);
        } else {
            // map start time to corresponding date playtime id
            columToId = Utils.mapKeyToAnotherKeyFromJsonList(playtimesOfDate, itemName, idName);
        }

        // create list view and add time information
        ListView<String> columnListView = new ListView<>();
        ObservableList<String> obsList = FXCollections.observableArrayList(new ArrayList<>(columToId.keySet()));
        columnListView.setItems(obsList);
        playtimeGridPane.add(columnListView, colIndex, 1);
        // add list view to global list for later reference
        startTimeListViews.add(columnListView);

        // add playtime ids to global list
        List<String> ids = new ArrayList<>(columToId.values());
        datePlaytimeIds.add(ids);

        // add click listener to time list view
        addListViewClickListener(columnListView, colIndex);
    }

    /**
     * Adds a click listener to the given list view. The <code>handle</code> method
     * gets called when the user clicks on an item in the list view, this means
     * clicks on a specific start time.<br>
     * Sets different indices and unselects all selections from other list views
     * showing playtime information. Checks if user selected a not empty list item
     * and either enables or disables the reserve button.
     *
     * @param timesListView the list view to add the listener
     * @param dateIndex     the index of the column from the GridaPane
     */
    private void addListViewClickListener(ListView<String> timesListView, int dateIndex) {
        timesListView.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event arg0) {
                // update global variables saving which date and time was selected
                lastClickedDateIndex = dateIndex;
                lastClickedPlaytimeIndex = timesListView.getSelectionModel().getSelectedIndex();
                // unselect items in all other listview except for the current one
                for (int k = 0; k < startTimeListViews.size(); k++) {
                    if (k != lastClickedDateIndex) {
                        startTimeListViews.get(k).getSelectionModel().clearSelection();
                    }
                }
                // only if clicked list item is not empty, enable reserve button and hide
                // message label
                setReservable(lastClickedPlaytimeIndex != -1);
            }
        });
    }

    /**
     * If <code>ifReservable</code> is <code>true</code>, enables the reserve button
     * and hides the message. Otherwise, disables the reserve button and shows the
     * message. If the user is currently logged in, the user can make a reservation,
     * this means the method should be called with <code>isReservable</code> equals
     * to <code>true</code>.
     *
     * @param isReservable the flag specifying if user can reserve
     */
    private void setReservable(boolean isReservable) {
        reserveButton.setDisable(!isReservable);
        messageLabel.setVisible(!isReservable);
    }

    /*
     * --------------------------------------------------------
     * -------------------------- END -------------------------
     * --------------------------------------------------------
     */

}
