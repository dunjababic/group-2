package gui.controller.information;

import com.google.gson.JsonObject;
import gui.controller.Controller;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.Reservation;
import utils.Utils;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ProfilePanelController extends Controller implements Initializable {

    // ------------------ class attributes ------------------
    // properties for height of table view
    private final int tableRowHeight = 30; // define height of each table row

    // columns used for sorting
    // TODO: change the attribute names to the columns by which you want to sort by
    // and adjust the data type accordingly
    private TableColumn<Reservation, Date> dateColumn;
    private TableColumn<Reservation, String> timeColumn;
    // ------------------------------------------------------

    // ------------------ FXML components ------------------
    // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
    @FXML
    private Label firstnameLabel;
    @FXML
    private Label lastnameLabel;
    @FXML
    private Label emailLabel;

    @FXML
    private TableView<Reservation> reservationsTableView;
    @FXML
    private GridPane buttonGridPane;
    // ------------------------------------------------------
    // ---------------------------------- END ----------------------------------

    /**
     * Initializes the table view that shows all reservations of the user who is
     * currently logged in. Initializes the properties of the table columns at
     * first. Next, request all reservations of the user via the REST client. If
     * client has currently no reservations, shows a message. Otherwise, adds every
     * <code>Reservation</code> as one row to the table, sorts the reservation by
     * date and time and sets the height and style of each row.
     */
    public void initializeReservationTableView() {
        initializeTableColumns();
        // --------------- ONLY MAKE CHANGES TO THE CODE IN THIS AREA ---------------
        /*
         * TODO: retrieve all reservations of the currently logged in user (via REST
         * client and server). The currently logged in user is stored in the variable
         * user of the class RestClient and can be accessed from there.
         *
         * Cinema Case:<br>
         * List<JsonObject> reservations = restClient.getReservationsOfClient();
         */
        List<JsonObject> reservations = null;
        // ---------------------------------- END ----------------------------------

        // if client currently has no reservations, show message
        if (reservations == null) {
            Label label = new Label("No reservations available until now.");
            label.setStyle("-fx-font-style: italic;");
            reservationsTableView.setPlaceholder(label);
            reservationsTableView.setPrefHeight(100);
        } else { // initialize table with reservation information
            for (JsonObject reservationJson : reservations) {
                addReservationToTable(reservationJson);
            }
            // sort table view by defined column
            reservationsTableView.getSortOrder().add(dateColumn);
            reservationsTableView.getSortOrder().add(timeColumn);
            reservationsTableView.sort();

            // set properties of table and rows
            setTableRowAndHeaderHeight();
            styleRows();
        }
    }

    /**
     * Iterates over every table column, which were previously already created in
     * the fxml layout. For every column
     * {@link javafx.scene.control.TableColumn#setCellValueFactory(javafx.util.Callback)}
     * is called which connects the table column with an attribute from the
     * <code>Reservation</code> class.<br>
     * Adds further properties for specific columns:
     * <ul>
     * <li><b>date:</b> register column for default sorting; add
     * {@link javafx.scene.control.TableColumn#setCellFactory(javafx.util.Callback)}
     * to show date in correct format</li>
     * <li><b>time:</b> register column for default sorting</li>
     * <li><b>movie/cinema:</b> add
     * {@link javafx.scene.control.TableColumn#setCellFactory(javafx.util.Callback)}
     * to add tooltip showing the complete movie/cinema name</li>
     * </ul>
     */
    public void initializeTableColumns() {
        /*
         * TODO: This is just for clarification purposes: The class "Reservation"
         * represents a reservation of the currently logged in user. Each such
         * reservation is represented as one row in the "reservationTableView". The
         * class attributes (not necessary all, some of them might also be for other
         * purposes) of the "Reservation" class correspond to columns in the table view.
         *
         * Be careful to correctly define the class "Reservation" in order to make the
         * functionality of this class work properly.
         */
        // get all table columns from fxml layout
        ObservableList<TableColumn<Reservation, ?>> columns = reservationsTableView.getColumns();
        /*
         * get corresponding attributes from Reservation class
         *
         * this will then define the mapping between class attributes and
         * FXML-Table-Columns
         */
        String[] variableNames = Reservation.getVariableNames();

        // iterate over each column and set factory to variable in POJO
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i).setCellValueFactory(new PropertyValueFactory<>(variableNames[i]));
            TableColumn<Reservation, String> col = (TableColumn<Reservation, String>) columns.get(i);

            /*
             * TODO: check if currently a specific column is processed. For comparing the
             * column names, use the same names that are contained in the array
             * "variableNames" (these names are defined in the method getVariableNames of
             * the Reservation class).
             *
             * For the checked column, perform further processing, like assigning the column
             * as a sorting column, format the date to display it in the correct format or
             * add a tooltip to the column which shows the complete name (helpful for long
             * names). Of course, you could also add other functionality.
             *
             * You could also completely omit the following switch-construct if you do not
             * want to sort the columns, do not want to format the date column or do not
             * want to add tooltips.
             */
            // TODO: possibly change the column by which to sort and format (in this case it
            // is the date column)
			switch (variableNames[i]) {
				case "date":
					dateColumn = (TableColumn<Reservation, Date>) columns.get(i);
					dateColumn.setSortType(TableColumn.SortType.ASCENDING);

					// set another cell factory, so that date is shown in correct format
					dateColumn.setCellFactory(column -> {
						return new TableCell<>() {
							@Override
							protected void updateItem(Date item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setText(null);
								} else {
									setText(Utils.dayMonthYearDateFormat.format(item));
								}
							}
						};
					});
					break;
                // TODO: possibly change the column by which to sort
				case "time":
					timeColumn = col;
					timeColumn.setSortType(TableColumn.SortType.ASCENDING);
					break;
                // TODO: add/remove columns to which to add a tooltip
				case "movie":
				case "cinema":
					// if columns is movie or cinema, show tooltip, as the text might not fit into
					// the column width
					col.setCellFactory(column -> {
						return new TableCell<>() {
							@Override
							protected void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);
								setText(item);
								setTooltip(new Tooltip(item));
							}
						};
					});
					break;
			}
        }
    }

    /**
     * Makes request to REST client to extract the playtime information of the date
     * playtime id contained in the reservation. This way the ids of the cinema and
     * movie, time and date of the reservation are extracted. Creates new
     * <code>Reservation</code> object which is add to the table view. The mapping
     * of the attributes of <code>Reservation</code> to the columns was previously
     * defined.
     *
     * @param json the reservation in JSON format
     */
    private void addReservationToTable(JsonObject json) {
        /*
         * TODO: retrieve all information that should be displayed in the reservations
         * table for the current reservation, which is stored in the json parameter.
         * (via REST client and server)
         *
         * Cinema Case:<br>
         * JsonObject playtimeInfo =
         * restClient.getPlaytimeInfo(json.get("datePlaytimeId").getAsInt());
         */
        // get playtime info for current reservation from server
        JsonObject playtimeInfo = null;

        /*
         * TODO: from the retrieved JsonObject extract and transform several attributes.
         * In this case all ids are extracted and the time and date are transformed into
         * the correct format. This process depends highly on the data you have and want
         * to show, thus you can use the following as an orientation.
         */
        // extract various ids
        int cinemaId = playtimeInfo.get("cinemaId").getAsInt();
        int movieId = playtimeInfo.get("movieId").getAsInt();
        int datePlaytimeId = playtimeInfo.get("datePlaytimeId").getAsInt();

        String time = Utils.convertTime12To24(playtimeInfo.get("startTime").getAsString());
        Date date = Utils.parseStringToDate(playtimeInfo.get("date").getAsString() + " " + time,
                Utils.monthDayYearDateTimeFormat);

        /*
         * TODO: create a new reservation according to the constructor you defined in
         * the Reservation class. For this object, use the attributes you extracted
         * previously. This means you need to get all information from the REST client
         * which you want to assign to the object here.
         */
        Reservation res = new Reservation(json.get("id").getAsInt(), cinemaId, movieId, datePlaytimeId,
                playtimeInfo.get("cinemaName").getAsString(), playtimeInfo.get("movieName").getAsString(), time,
                json.get("reservedSeats").getAsInt(), date);

        reservationsTableView.getItems().add(res);
    }

    /**
     * Defines two <code>PseudoClass</code> objects that define the appearance of
     * the tables rows which contain a reservation from the past. The style is
     * defined in <code>application.css</code> with the tag
     * <code>.table-view .table-row-cell:pastReservation/pastReservationSelected .text</code>.<br>
     * Adds a
     * {@link javafx.scene.control.TableView#setRowFactory(javafx.util.Callback)} to
     * the table view to handle the event of changing the row color of past
     * reservations. In the <code>updateItem</code> method it checks if the
     * reservation is <code>null</code> in which case the table view is only
     * refreshed. If the reservation is from the past, changes the pseudo class to
     * one of the previously defined pseudo classes, depending on whether the
     * reservation is selected or not (if the reservation is in the past and
     * selected, the row gets a lighter grey color).
     */
    private void styleRows() {
        // define pseudo class for past reservations
        final PseudoClass pastReservationClass = PseudoClass.getPseudoClass("pastReservation");
        final PseudoClass pastReservationSelectedClass = PseudoClass.getPseudoClass("pastReservationSelected");
        reservationsTableView.setRowFactory(tv -> new TableRow<>() {
			@Override
			protected void updateItem(Reservation item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					// refresh view so that after resorting the rows, the coloring is still correct
					reservationsTableView.refresh();
				}
                /*
                 * IN THIS METHOD ONLY MAKE A MODIFICATION AT THIS PLACE!
                 *
                 * These operations are used to create different highlighting of the
                 * reservations between two different groups of reservations. In the Cinema Case
                 * all reservations that are in the past are highlighted in grey whilst the
                 * reservations for the future are shown normally.
                 *
                 * TODO: adapt the if-condition to check if you want to highlight the
                 * reservation. If you also have a date, you may be able to leave it like this.
                 * If you have a criterion based on another variable, then replace the given
                 * condition by your new one.
                 *
                 * Cinema Case: Check if the date of the reservation is in the past, if yes,
                 * highlight the item.
                 */else if (Utils.isPastDate(item.getDate())) {
					// if current item is from the past, set pseudo class to true, check if this
					// item is the selected one; if yes, set other pseudo class to true
					Reservation selectedReservation = reservationsTableView.getSelectionModel().getSelectedItem();
					if (item.equals(selectedReservation)) {
						pseudoClassStateChanged(pastReservationSelectedClass, true);
					} else {
						pseudoClassStateChanged(pastReservationClass, true);
					}
					reservationsTableView.refresh();
				}
			}
		});
    }

    /**
     * Adds a click listener to the delete button. This <code>handle</code> method
     * is called when the user clicks on the delete button. Inside the method the
     * selected reservation gets deleted with a REST client request and afterwards
     * reloads the profile page, so that the delete reservation is no longer
     * visible.
     *
     * @param button      the delete button
     * @param reservation the <code>Reservation</code> object on which was clicked
     */
    public void addDeleteButtonListener(Button button, Reservation reservation) {
        button.setOnAction(new EventHandler<>() {
			@Override
			public void handle(ActionEvent arg0) {
                // delete reservation by id and reload profile page
                /*
                 * TODO: delete the current reservation from the corresponding database table by
                 * specifying the id (via REST client and server)
                 *
                 * Cinema Case: restClient.deleteReservation(reservation.getReservationId());
                 */
				sceneNavigator.loadSceneToMainWindow(sceneNavigator.PROFILE, null);
			}
		});
    }

    /**
     * Adds a click listener to the edit button. This <code>handle</code> method is
     * called when the user clicks on the edit button. Inside the method the
     * necessary information from the <code>Reservation</code> object is passed to a
     * data list. Next, loads the reservation scene is loaded in which the user can
     * then edit the selected reservation.
     *
     * @param button      the edit button
     * @param reservation the <code>Reservation</code> object on which was clicked
     */
    public void addEditButtonListener(Button button, Reservation reservation) {
        button.setOnAction(new EventHandler<>() {
			@Override
			public void handle(ActionEvent arg0) {
				// IMPORTANT: set PROFILE scene as previous scene so that correct navigation
				// behavior is performed
				sceneNavigator.setPreviousScene(sceneNavigator.PROFILE);

                /*
                 * TODO: build up the array data with the needed information which is then
                 * passed to the reservation scene. Extend the array with extra variables, if
                 * you have additional information that must be displayed or are needed in the
                 * reservation scene.
                 *
                 * Keep the order of the data in mind! This must be the same as the order in
                 * which you retrieve the data in the initializeSceneData method in the
                 * ReservationPanelController class!!
                 *
                 * This is basically the same array as in the method onReserveButtonClicked of
                 * the class InformationPanelController. However, you might want to pass
                 * additional variables, that are only present, if the user wants to edit a
                 * reservation.
                 *
                 * Cinema Case: 9 items are passed to the reservation scene and 9 items are then
                 * extracted in the ReservationPanelController. This means 2 additional items,
                 * the reservation id and how many seats were reserved, are passed to the
                 * ReservationPanelController.
                 */
				String[] data = {reservation.getCinema(), reservation.getMovie(), reservation.getTime(),
						Utils.dayMonthYearDateFormat.format(reservation.getDate()), reservation.getCinemaId() + "",
						reservation.getMovieId() + "", reservation.getDatePlaytimeId() + "",
						reservation.getReservationId() + "", reservation.getReservedSeats() + ""};
				sceneNavigator.loadSceneToMainWindow(sceneNavigator.RESERVATION, Arrays.asList(data));
			}
		});
    }

    /**
     * Is called when the user clicks on a table row. <br>
     * <br>
     * Determines which row was clicked (index and item). Computes the position
     * where the delete and modify button should be added for the selected
     * reservation and adds both buttons. If the selected reservation is in the
     * past, both buttons get disabled.
     */
    @FXML
    public void onReservationItemClicked() {
        // clear all elements in button grid pane
        buttonGridPane.getChildren().clear();

        // get index and content of clicked row
        int index = reservationsTableView.getSelectionModel().getSelectedIndex();
        Reservation res = reservationsTableView.getSelectionModel().getSelectedItem();

        // only if not clicked on table header
        if (res != null) {
            // add empty pane in button grid pane so that buttons will be added right
            // underneath the pane
            Pane pane = new Pane();
            int height = (index + 1) * tableRowHeight - 3; // -3 because of button padding
            pane.setMinHeight(height);
            pane.setMaxHeight(height);
            buttonGridPane.add(pane, 0, 0);

            // add buttons for edit and delete with picture and respective click listener
            Button editButton = addButtonWithIcon("/images/edit_icon.png", 0, 1, false, res);
            Button deleteButton = addButtonWithIcon("/images/delete_icon.png", 1, 1, true, res);// --------------- ONLY MAKE CHANGES TO THE CODE IN THIS AREA ---------------
            // if reservation is in the past, disable both buttons
            /*
             * Again, you have to make the check between the two different groups of
             * reservations, as in the styleRows() method. In this case for the one group
             * the buttons should not be visible but for the other group they should be
             * shown.
             *
             * TODO: adapt the condition to the same condition as in the styleRows() method
             * using the currently selected reservation that is stored in the variable
             * "res". This condition should make the distinction between the two groups.
             *
             * Cinema Case: reservations from the past are not deletable or editable, thus
             * no buttons should be shown. The conditions checks therefore if the currently
             * selected reservation lies in the past.
             */

            // if reservation is in the past, disable both buttons
            if (Utils.isPastDate(res.getDate())) {
                editButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        }
    }

    /*
     * --------------------------------------------------------------------
     * --------------- DO NOT CHANGE THE FOLLOWING METHODS! ---------------
     * --------------------------------------------------------------------
     */

	/**
     * Overrides the method from the <code>Initializable</code> interface which is
     * called when the fxml layout is loaded.<br>
     * Initializes the text of all labels and the table view showing the
     * reservations.
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        firstnameLabel.setText(restClient.getUser().getFirstName());
        lastnameLabel.setText(restClient.getUser().getLastName());
        emailLabel.setText(restClient.getUser().getEmail());

        initializeReservationTableView();
    }

    /**
     * Sets the height of all table rows and of the header to the same size. Binds
     * the height of the complete table view to the overall height of all rows plus
     * header height. This way no scrollbar is added to the table view when to many
     * rows are added. The scrollbar is however contained in the PROFILE layout so
     * that nevertheless all table entries are shown.
     */
    private void setTableRowAndHeaderHeight() {
        // set item count (+1 for table header) and set row height
        int tableItemCount = Bindings.size(reservationsTableView.getItems()).get() + 1;
        reservationsTableView.setFixedCellSize(tableRowHeight);

        // set total height of table view so that no empty rows are shown/ no scroll bar
        // is inserted
        reservationsTableView.prefHeightProperty()
                .bind(reservationsTableView.fixedCellSizeProperty().multiply(tableItemCount));
        reservationsTableView.minHeightProperty().bind(reservationsTableView.prefHeightProperty());
        reservationsTableView.maxHeightProperty().bind(reservationsTableView.prefHeightProperty());

        // set height of table header to same size as table rows
        reservationsTableView.skinProperty().addListener((obs, ol, ne) -> {
            Pane header = (Pane) reservationsTableView.lookup("TableHeaderRow");
            header.prefHeightProperty().bind(reservationsTableView.prefHeightProperty().divide(tableItemCount));
            header.minHeightProperty().bind(header.prefHeightProperty());
            header.maxHeightProperty().bind(header.prefHeightProperty());
        });
    }

    /**
     * Creates a new button with the specified image on it. Adds a listener to the
     * button which is dependent on the type of button that is created (either
     * delete or edit button). Then adds the button to the button grid (right next
     * to the table view and right beneath the previously added empty pane in
     * {@link #onReservationItemClicked()})
     *
     * @param path           the path to the image
     * @param col            the number for the column
     * @param row            the number for the row
     * @param isDeleteButton the flag specifying if a delete button is added
     * @param reservation    the <code>Reservation</code> object on which was
     *                       clicked
     */
    public Button addButtonWithIcon(String path, int col, int row, boolean isDeleteButton, Reservation reservation) {
        ImageView imageview = new ImageView(new Image(path));
        imageview.setFitWidth(15);
        imageview.setPreserveRatio(true);
        Button button = new Button();
        button.setGraphic(imageview);
        button.setPadding(new Insets(5, 5, 5, 5));

        // add button click listener depending on type of button
        if (isDeleteButton) {
            addDeleteButtonListener(button, reservation);
        } else {
            addEditButtonListener(button, reservation);
        }
        buttonGridPane.add(button, col, row);
        return button;
    }

    /**
     * If the user selects a table view row with key arrows up or down, the same
     * functionality is performed as when the user clicked. This means
     * {@link #onReservationItemClicked()} is called.
     *
     * @param event the action which caused the method call
     */
    @FXML
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
            onReservationItemClicked();
        }
    }

    /*
     * --------------------------------------------------------
     * -------------------------- END -------------------------
     * --------------------------------------------------------
     */
}
