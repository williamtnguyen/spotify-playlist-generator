import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;

public class UI extends Application {
    private Button submitBtn;
    private Label urlLabel;
    private TextField textField;
    private ProgramManager programManager;

    /**
     * NOTE: Reformat later. Just want to get a basic UI working
     */
    @Override
    public void start(Stage primaryStage) {

        // Initialize instance variables
        primaryStage.setTitle("Authentication");
        submitBtn = new Button("Submit");
        urlLabel = new Label("After agreeing to authorize this app from your main browser, copy and paste the redirect URL into the textbox");
        textField = new TextField();
        programManager = new ProgramManager();


        // Opens default browser for spotify login
        try {
            programManager.openBrowserForAuthentication();
        } catch (IOException e) {
            //TODO: create warning box
        }

        // Begins to authenticate user and brings user to the next scene if authentication passes
        submitBtn.setOnAction(e -> {
            if (textField.getText().length() != 0) {
                try {
                    programManager.authenticateUser(textField.getText());

                    // Create and initialize elements
                    Text title = new Text("What's your mood? (0: sad, 1: happy)");
                    GridPane grid = new GridPane();
                    Button generatePlaylistBtn = new Button("Generate Playlist");
                    title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

                    // Create the slider from 0 - 1
                    Slider moodSlider = new Slider();
                    moodSlider.setMin(0);
                    moodSlider.setMax(1);
                    moodSlider.setValue(0.5);
                    moodSlider.setShowTickLabels(true);
                    moodSlider.setShowTickMarks(true);
                    moodSlider.setMajorTickUnit(0.5);
                    moodSlider.setBlockIncrement(0.1);

                    // Add elements into the GridPane layout
                    grid.add(title, 0, 0, 2, 1);
                    grid.add(moodSlider,0,1);
                    grid.add(generatePlaylistBtn,0,2);

                    // Set center all elements and padding
                    grid.setAlignment(Pos.CENTER);
                    grid.setHgap(10);
                    grid.setVgap(10);
                    GridPane.setHalignment(title, HPos.CENTER);
                    GridPane.setHalignment(moodSlider, HPos.CENTER);
                    GridPane.setHalignment(generatePlaylistBtn, HPos.CENTER);
                    grid.setPadding(new Insets(25, 25, 25, 25));

                    // Create a scene for user to input mood value
                    Scene mainScene = new Scene(grid, 720, 350);
                    primaryStage.setTitle("Set mood");
                    primaryStage.setScene(mainScene);
                    primaryStage.show();

                    // Start creating a playlist when generate playlist button is clicked
                    generatePlaylistBtn.setOnAction(a -> {
                        try {
                            PlaylistTrack[] tracks = programManager.generatePlayList(moodSlider.getValue());

                            // Initialize data structures for listing track names
                            ListView<String> list = new ListView<String>();
                            ObservableList<String> items = FXCollections.observableArrayList();

                            // Get track names and add them into items
                            for (int i = 0; i < tracks.length; i++) {
                                items.add(tracks[i].getTrack().getName());
                            }

                            list.setItems(items);

                            // Create StackPane layout and add the list of track names into it
                            StackPane root = new StackPane();
                            root.getChildren().add(list);

                            // Create a scene for displaying the track names and display it in primaryStage
                            Scene playlistScene = new Scene(root, 720, 350);
                            primaryStage.setTitle("Generated Playlist");
                            primaryStage.setScene(playlistScene);
                            primaryStage.show();
                        } catch (IOException | SpotifyWebApiException ex) {
                            ex.printStackTrace();
                        }

                    });

                } catch (IOException | SpotifyWebApiException ex) {
                    // TODO: create warning box
                    ex.printStackTrace();
                }
            }
        });

        // Set layout and add elements to pane
        GridPane grid = new GridPane();
        Text scenetitle = new Text("Welcome to Spotify Playlist");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.add(urlLabel,0,1);
        grid.add(textField,0,2);
        grid.add(submitBtn,0,3);

        // Set pane to scene, set scene into stage, and show stage
        // Note: stage = window and scene = panel
        Scene scene = new Scene(grid, 720, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
