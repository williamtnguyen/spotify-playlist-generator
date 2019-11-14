import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
        }
        catch (IOException e) {
            //TODO: create warning box
        }

        try {
            programManager.authenticateUser("https://www.google.com/");
        }

//        // Authenticates user once submit button is clicked
//        submitBtn.setOnAction(e ->{
//            if (textField.getText().length() != 0)
//            {
//                try {
//                    programManager.authenticateUser(textField.getText());
//                    try
//                    {
//                        programManager.userTopArtistAndTrack();
//                    } catch (SpotifyWebApiException | IOException ea) {
//                        ea.printStackTrace();
//                    }
//                } catch (IOException | SpotifyWebApiException ex) {
//                    // TODO: create warning box
//                    ex.printStackTrace();
//                }
//            }
//        });

        // Set layout and add elements to pane
        GridPane grid = new GridPane();
        Text scenetitle = new Text("Welcome");
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
        Scene scene = new Scene(grid, 350, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
