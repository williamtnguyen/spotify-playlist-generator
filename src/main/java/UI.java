import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class UI extends Application {
    private Button submitBtn;
    private Label urlLabel;
    private TextField textField;
    private ProgramManager programManager;
    private Image banner;

    /**
     * NOTE: Reformat later. Just want to get a basic UI working
     */
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        // Initialize instance variables
        primaryStage.setTitle("Authentication");
        submitBtn = new Button("Submit");

        banner = new Image(new FileInputStream(System.getProperty("user.dir") + "/logo/Spotify_Logo_RGB_Green.png"), 400,120,false,false);
        urlLabel = new Label("After agreeing to authorize this app from your main browser, copy and paste the redirect URL into the textbox");

        textField = new TextField();
        textField.setPromptText("Enter redirect URL here");
        programManager = new ProgramManager();


        // Opens default browser for spotify login
        try {
            programManager.openBrowserForAuthentication();
        }
        catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("An error has occurred. Please try again later");

            alert.showAndWait();
        }

        // Begins to authenticate user and brings user to the next scene if authentication passes
        submitBtn.setOnAction(e -> {
            if (textField.getText().length() != 0) {
                try {
                    programManager.authenticateUser(textField.getText());

                    // Create and initialize elements
                    ImageView imageView = new ImageView(banner);

                    Text title = new Text("What's your mood? (0: sad, 1: happy)");
                    title.setId("moodTitle");

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(20);
                    grid.setPadding(new Insets(25, 25, 25, 25));

                    Button generatePlaylistBtn = new Button("Generate Mood Playlist");

                    Button generateTopListensBtn = new Button("Generate Top Listens by Play Count");

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
                    grid.add(imageView, 0, 0);
                    grid.add(title, 0, 1, 2, 1);
                    grid.add(moodSlider,0,2);
                    grid.add(generatePlaylistBtn,0,3);
                    grid.add(generateTopListensBtn,0,4);

                    StackPane root = new StackPane();

                    // Set center all elements and padding
                    GridPane.setHgrow(moodSlider, Priority.ALWAYS);
                    grid.setAlignment(Pos.CENTER);
                    GridPane.setHalignment(imageView,HPos.CENTER);
                    GridPane.setHalignment(title, HPos.CENTER);
                    GridPane.setHalignment(moodSlider, HPos.CENTER);
                    GridPane.setHalignment(generatePlaylistBtn, HPos.CENTER);
                    GridPane.setHalignment(generateTopListensBtn, HPos.CENTER);

                    root.getChildren().add(grid);

                    // Create a scene for user to input mood value
                    Scene mainScene = new Scene(root, 750, 400);
                    mainScene.getStylesheets().add("style.css");
                    primaryStage.setTitle("Set mood");
                    primaryStage.setScene(mainScene);
                    primaryStage.show();

                    // Start creating a playlist when generate playlist button is clicked
                    generatePlaylistBtn.setOnAction(a -> {
                        try {
                            ProgressIndicator pi = new ProgressIndicator();
                            VBox box = new VBox(pi);
                            box.setAlignment(Pos.CENTER);

                            // Grey Background
                            title.setOpacity(0.4);
                            grid.setDisable(true);
                            root.getChildren().add(box);

                            Task<PlaylistTrack[]> task = new Task<>() {
                                @Override
                                protected PlaylistTrack[] call() throws Exception {
                                    return programManager.generatePlayList(moodSlider.getValue());
                                }
                            };

                            // Usually happens when mood = 0
                            task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                                @Override
                                public void handle(WorkerStateEvent t) {
                                    // Display error window with text
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error Dialog");
                                    alert.setHeaderText(null);
                                    alert.setContentText("An error has occurred while creating your playlist. " +
                                            "Try to set a different mood");

                                    // Revert the previous scene to how it was previously
                                    alert.showAndWait();
                                    title.setOpacity(1.0);
                                    grid.setDisable(false);
                                    root.getChildren().remove(box);

                                    primaryStage.setScene(mainScene);
                                    primaryStage.show();
                                }
                            });

                            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                                @Override
                                public void handle(WorkerStateEvent t) {
                                    PlaylistTrack[] tracks = new PlaylistTrack[0];
                                    try {
                                        tracks = task.get();
                                    }
                                    catch (InterruptedException | ExecutionException ex) {
                                        ex.printStackTrace();
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error Dialog");
                                        alert.setHeaderText(null);
                                        alert.setContentText("An error has occurred while creating your playlist. " +
                                                "Please try again later");

                                        alert.showAndWait();
                                    }

                                    ListView<String> songAndArtistList = new ListView<String>();
                                    ObservableList<String> songAndArtists = FXCollections.observableArrayList();

                                    for (int i = 0; i < tracks.length; i++) {
                                        StringBuilder artistNames = new StringBuilder();
                                        artistNames.append(tracks[i].getTrack().getName() + " - ");
                                        ArtistSimplified[] artistArray = tracks[i].getTrack().getArtists();
                                        // Get artist name and append it to the song name separated by commas
                                        for (int index = 0; index < artistArray.length; index++) {
                                            artistNames.append(artistArray[index].getName());

                                            if (index != artistArray.length - 1) {
                                                artistNames.append(", ");
                                            }
                                        }

                                        // Adds songs and artists into ObservableList if song and artist isn't already added
                                        String songAndArtistString = artistNames.toString();
                                        if (!songAndArtists.contains(songAndArtistString))
                                        {
                                            songAndArtists.add(artistNames.toString());
                                        }
                                    }

                                    songAndArtistList.setItems(songAndArtists);

                                    Text header = new Text("Enjoy your personalized playlist!");
                                    header.setId("enjoyPlayListTitle");
                                    Button goBackBtn = new Button("Create another playlist");

                                    goBackBtn.setOnAction(goBack -> {
                                        title.setOpacity(1.0);
                                        grid.setDisable(false);
                                        root.getChildren().remove(box);

                                        primaryStage.setScene(mainScene);
                                        primaryStage.show();
                                    });

                                    // Create BorderPane layout and add the all elements into it
                                    BorderPane borderPane = new BorderPane();
                                    borderPane.getStylesheets().add("style.css");
                                    borderPane.setTop(header);
                                    borderPane.setAlignment(header, Pos.CENTER);
                                    BorderPane.setMargin(header, new Insets(10));
                                    borderPane.setRight(goBackBtn);
                                    borderPane.setAlignment(goBackBtn, Pos.CENTER);
                                    BorderPane.setMargin(goBackBtn, new Insets(10));
                                    borderPane.setCenter(songAndArtistList);
                                    BorderPane.setMargin(songAndArtistList, new Insets(10));

                                    // Create a scene for displaying the track names and display it in primaryStage
                                    Scene playlistScene = new Scene(borderPane, 750, 400);
                                    primaryStage.setTitle("Generated Playlist");
                                    primaryStage.setScene(playlistScene);
                                    primaryStage.show();

                                }
                            });

                            new Thread(task).start();
                        }
                        // Happens when there is no songs in the generated playlist
                        catch (NullPointerException n)
                        {
                            n.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("No Songs generated");
                            alert.setHeaderText(null);
                            alert.setContentText("There were no songs generated in playlist. Try generating a playlist and" +
                                    "add songs in it and come back");

                            alert.showAndWait();
                        }
                    });

                    generateTopListensBtn.setOnAction(a -> {
                            try {
                                ProgressIndicator pi = new ProgressIndicator();
                                VBox box = new VBox(pi);
                                box.setAlignment(Pos.CENTER);

                                // Grey Background
                                title.setOpacity(0.4);
                                grid.setDisable(true);
                                root.getChildren().add(box);

                                Task<PlaylistTrack[]> task = new Task<>() {
                                    @Override
                                    protected PlaylistTrack[] call() throws Exception {
                                        return programManager.generatePlaylistTopListens();
                                    }
                                };

                                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                                    @Override
                                    public void handle(WorkerStateEvent t) {
                                        PlaylistTrack[] tracks = new PlaylistTrack[0];
                                        try {
                                            tracks = task.get();
                                        }
                                        catch (InterruptedException | ExecutionException ex) {
                                            ex.printStackTrace();
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("Error Dialog");
                                            alert.setHeaderText(null);
                                            alert.setContentText("An error has occurred while creating your playlist. " +
                                                    "Please try again later");

                                            alert.showAndWait();
                                        }

                                        ListView<String> songAndArtistList = new ListView<String>();
                                        ObservableList<String> songAndArtists = FXCollections.observableArrayList();

                                        for (int i = 0; i < tracks.length; i++) {
                                            StringBuilder artistNames = new StringBuilder();
                                            artistNames.append(tracks[i].getTrack().getName() + " - ");
                                            ArtistSimplified[] artistArray = tracks[i].getTrack().getArtists();
                                            // Get artist name and append it to the song name separated by commas
                                            for (int index = 0; index < artistArray.length; index++) {
                                                artistNames.append(artistArray[index].getName());

                                                if (index != artistArray.length - 1) {
                                                    artistNames.append(", ");
                                                }
                                            }

                                            // Adds songs and artists into ObservableList if song and artist isn't already added
                                            String songAndArtistString = artistNames.toString();
                                            if (!songAndArtists.contains(songAndArtistString))
                                            {
                                                songAndArtists.add(artistNames.toString());
                                            }
                                        }

                                        songAndArtistList.setItems(songAndArtists);

                                        Text header = new Text("Enjoy your personalized playlist!");
                                        header.setId("enjoyPlayListTitle");

                                        Button goBackBtn = new Button("Create another playlist");

                                        goBackBtn.setOnAction(goBack -> {
                                            title.setOpacity(1.0);
                                            grid.setDisable(false);
                                            root.getChildren().remove(box);

                                            primaryStage.setScene(mainScene);
                                            primaryStage.show();
                                        });

                                        // Create BorderPane layout and add the all elements into it
                                        BorderPane borderPane = new BorderPane();
                                        borderPane.getStylesheets().add("style.css");
                                        borderPane.setTop(header);
                                        borderPane.setAlignment(header, Pos.CENTER);
                                        BorderPane.setMargin(header, new Insets(10));
                                        borderPane.setRight(goBackBtn);
                                        borderPane.setAlignment(goBackBtn, Pos.CENTER);
                                        BorderPane.setMargin(goBackBtn, new Insets(10));
                                        borderPane.setCenter(songAndArtistList);
                                        BorderPane.setMargin(songAndArtistList, new Insets(10));

                                        // Create a scene for displaying the track names and display it in primaryStage
                                        Scene playlistScene = new Scene(borderPane, 750, 400);
                                        primaryStage.setTitle("Generated Playlist");
                                        primaryStage.setScene(playlistScene);
                                        primaryStage.show();
                                    }
                                });

                                new Thread(task).start();
                            }
                            // Happens when there is no songs in the generated palylist
                            catch (NullPointerException n)
                            {
                                n.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("No Songs generated");
                                alert.setHeaderText(null);
                                alert.setContentText("There were no songs generated in playlist. Try generating a playlist and" +
                                        "add songs in it and come back");

                                alert.showAndWait();
                            }
                    });

                } catch (IOException | SpotifyWebApiException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Incorrect code. Please copy/paste again. If problem persists, reopen the app");

                    alert.showAndWait();
                }
            }
        });

        // Set layout and elements that will be added to the pane
        GridPane grid = new GridPane();
        Text scenetitle = new Text("Welcome to Spotify Playlist Generator");

        // Set css for scene title
        scenetitle.setId("welcomeTitle");
        urlLabel.setId("welcomeDirections");

        // Set spacing between components
        grid.setHgap(10);
        grid.setVgap(20);

        ImageView imView = new ImageView(banner);
        GridPane.setHalignment(imView, HPos.CENTER);
        grid.add(imView,0,0);

        GridPane.setHalignment(scenetitle, HPos.CENTER);
        grid.add(scenetitle, 0, 1, 2, 1);

        grid.setAlignment(Pos.CENTER);

        GridPane.setHalignment(urlLabel,HPos.CENTER);
        GridPane.setHalignment(textField,HPos.CENTER);
        GridPane.setHalignment(submitBtn,HPos.CENTER);

        grid.add(urlLabel,0,2);
        grid.add(textField,0,3);
        grid.add(submitBtn,0,4);

        // Set pane to scene, set scene into stage, and show stage
        // Note: stage = window and scene = panel
        Scene scene = new Scene(grid, 750, 400);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("spotify-icon.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
