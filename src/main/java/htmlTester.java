import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;

public class htmlTester extends Application
{
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("WebView test");

        WebView browser = new WebView();

        // Retrieve the html file
        String tempHTMLFilePath = new File("src/main/java/website.html").toURI().toURL().toString().substring(6);
        tempHTMLFilePath = tempHTMLFilePath.replace("/", "\\\\");
        File HTMLfilePath = new File(tempHTMLFilePath);
        browser.getEngine().load(HTMLfilePath.toURI().toURL().toString());


        StackPane sp = new StackPane();
        sp.getChildren().add(browser);

        Scene root = new Scene(sp);

        primaryStage.setScene(root);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
