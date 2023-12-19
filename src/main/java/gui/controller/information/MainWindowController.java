package gui.controller.information;

import com.google.gson.JsonObject;
import gui.controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController extends Controller implements Initializable {

    // ------------------ class attributes ------------------
    // TODO: rename both attributes
    private List<Integer> cinemaIds = new ArrayList<>();
    private int lastClickedCinemaIndex = -1;
    // ------------------------------------------------------

    // ------------------ FXML components ------------------
    /**
     * TODO: rename the attribute and also rename the fx:id of the corresponding
     * component in the MainWindow.fxml file to the same name
     */
    @FXML
    private ListView<String> cinemaListView;
    // ------------- DO NOT CHANGE THE FOLLOWING PART OF THE CODE -------------
    @FXML
    private AnchorPane informationPanel;
    @FXML
    private AnchorPane toolBar;
    // ---------------------------------- END ----------------------------------

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        sceneNavigator.setMainWindowController(this);

        /*
         * TODO: retrieve all items from database (via REST client and server)
         * CinemaCase: List<JsonObject> cinemaList = restClient.requestCinemas();
         */
        List<JsonObject> itemsList = new ArrayList<>();
        initializeCinemaListView(itemsList);
    }

    /**
     * Requests all available cinemas from REST client. If currently no cinemas are
     * available, a message is shown. Otherwise the list view is initialized, this
     * means each list view item corresponds to the name of one cinema. Also, the
     * ids of the cinemas are kept in a separate list.
     */
    public void initializeCinemaListView(List<JsonObject> cinemaList) {
        // reset variables when filter is applied
        cinemaIds = new ArrayList<>();
        cinemaListView.getSelectionModel().clearSelection();
        lastClickedCinemaIndex = -1;

        // initializes cinema list view
        if (cinemaList.isEmpty()) { // no cinemas available
            List<String> data = new ArrayList<>();
            data.add("No Data");
            data.add("Currently there is no data available. Pleasy try again later.");
            sceneNavigator.loadSceneToMainWindow(sceneNavigator.SUCCESS_PANEL, data);

            // reset variables when filter is applied
            cinemaListView.setItems(FXCollections.observableArrayList());
        } else { // otherwise load cinemas to list view
            ObservableList<String> obsList = FXCollections.observableArrayList();
            for (JsonObject o : cinemaList) {
                obsList.add(o.get("name").getAsString());
                cinemaIds.add(o.get("id").getAsInt());
            }
            cinemaListView.setItems(obsList);
        }
    }

    /*
     * --------------------------------------------------------------------
     * --------------- DO NOT CHANGE THE FOLLOWING METHODS! ---------------
     * --------------------------------------------------------------------
     */

	/**
     * Is called when the user clicks on an item in the list view.<br>
     * <br>
     * Only if the user clicked a different item than before and no blank space, the
     * information of the clicked cinema are shown. The next scene (Information
     * scene) is loaded into the MainWindow while passing the cinema id as the data.
     */
    @FXML
    public void onCinemaListItemClicked() {
        int clickedIndex = cinemaListView.getSelectionModel().getSelectedIndex();

        // only if no blank space was selected and different list item than
        // before is clicked
        if ((clickedIndex != -1 && clickedIndex != lastClickedCinemaIndex)) {
            lastClickedCinemaIndex = clickedIndex;
            int cinemaId = cinemaIds.get(clickedIndex);
            sceneNavigator.loadSceneToMainWindow(sceneNavigator.INFO, Collections.singletonList(cinemaId));
        }
    }

    /**
     * Sets the given node to the information panel (on the right).
     *
     * @param node the loaded fxml layout
     */
    public void setPanelScene(Node node) {
        informationPanel.getChildren().setAll(node);
        // set all anchors to 0, so that content scales when window size is changed
        setAllAnchors(node, 0);
    }

    /**
     * Sets the given node to the tool bar panel (on the top).
     *
     * @param node the loaded fxml layout
     */
    public void setToolBar(Node node) {
        toolBar.getChildren().setAll(node);
        // set all anchors to 0, so that content scales when window size is changed
        setAllAnchors(node, 0);
    }

    /**
     * Sets all anchors of the given node to distance, so that the content scales
     * when the window size is changed.
     *
     * @param node  the node to change
     * @param value the offset from the bottom/top/left/right of the node
     */
    private void setAllAnchors(Node node, double value) {
        AnchorPane.setBottomAnchor(node, value);
        AnchorPane.setTopAnchor(node, value);
        AnchorPane.setLeftAnchor(node, value);
        AnchorPane.setRightAnchor(node, value);
    }

    /*
     * --------------------------------------------------------
     * -------------------------- END -------------------------
     * --------------------------------------------------------
     */

}
