package com.troblecodings.launcher.javafx;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.troblecodings.launcher.Launcher;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CreditsScene extends Scene {

    private static StackPane stackpane = new StackPane();

    public CreditsScene() {
        super(stackpane);
        Launcher.setupScene(this, stackpane);

        ScrollPane sp = new ScrollPane();

        sp.setHbarPolicy(ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        sp.setMaxHeight(500);
        sp.setMaxWidth(650);

        VBox vbox = new VBox();

        sp.setContent(vbox);
        vbox.setPrefSize(sp.getMaxWidth(), sp.getMaxHeight());

        vbox.setStyle("-fx-padding: 40px 60px;");
        stackpane.getChildren().add(sp);

        Label graficslabel = new Label("Graphics");
        graficslabel.setStyle("-fx-padding: 0px 0px 10px 0px;");
        Button mcjeronimo = new Button("Mc_Jeronimo [Twitter]");
        mcjeronimo.setMnemonicParsing(false);
        mcjeronimo.setOnAction(event -> openWebsiteInBrowser("https://twitter.com/Jer0nimo_97"));
        Label programminglabel = new Label("Programming");
        Button mrtroblebutton = new Button("MrTroble [Twitter]");
        mrtroblebutton.setOnAction(event -> openWebsiteInBrowser("https://twitter.com/TherealMrTroble"));
        Button derzaubererbutton = new Button("Der_Zauberer [Twitter]");
        derzaubererbutton.setMnemonicParsing(false);
        derzaubererbutton.setOnAction(event -> openWebsiteInBrowser("https://twitter.com/Der_Zauberer_DA"));
        Button shirosakaButton = new Button("Shirosaka");
        Button codingbytimoButton = new Button("Codingbytimo");

        Label libarieslabel = new Label("Libraries");
        Button mcauthenticatorbutton = new Button("Minecraft-Authenticator by HyCraftHD [GitHub]");
        mcauthenticatorbutton.setOnAction(event -> openWebsiteInBrowser("https://github.com/HyCraftHD/Minecraft-Authenticator/blob/main/LICENSE"));
        Button jsonbutton = new Button("JSON-java by stleary [GitHub]");
        jsonbutton.setOnAction(event -> openWebsiteInBrowser("https://github.com/stleary/JSON-java/blob/master/LICENSE"));
        Button minecrafterbutton = new Button("MineCrafter Font by MadPixel [Dafont]");
        minecrafterbutton.setOnAction(event -> openWebsiteInBrowser("https://www.dafont.com/de/minecrafter.font"));
        Button gsonbutton = new Button("Gson by Google [GitHub]");
        gsonbutton.setOnAction(event -> openWebsiteInBrowser("https://github.com/google/gson/blob/master/LICENSE"));

        vbox.getChildren().addAll(graficslabel, mcjeronimo, programminglabel, mrtroblebutton, derzaubererbutton, shirosakaButton, codingbytimoButton, libarieslabel, mcauthenticatorbutton, jsonbutton, minecrafterbutton, gsonbutton);
    }

    public static void openWebsiteInBrowser(String url) {
        Launcher.getInstance().getHostServices().showDocument(url);
    }
}
