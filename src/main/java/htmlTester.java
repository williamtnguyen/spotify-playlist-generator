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

        String html = getClass().getResource("website.html").toExternalForm();
        browser.getEngine().load(html);

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
