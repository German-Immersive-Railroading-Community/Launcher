package eu.girc.launcher;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void init() throws Exception {
        
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(true);
        stage.setTitle("GIRC Launcher v.....");

        // find more themes in 'atlantafx.base.theme' package
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.show();
    }

    @Override
    public void stop() throws Exception {

    }
}
