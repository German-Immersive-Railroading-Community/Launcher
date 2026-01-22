package com.troblecodings.launcher.javafx;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CreditsScene extends Scene {
    private static final StackPane stackpane = new StackPane();

    public CreditsScene() {
        super(stackpane);
        Launcher.setupScene(this, stackpane);
        this.getStylesheets().add(Assets.getStyleSheet("credits.css"));

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

        Label graphicsLabel = new Label("Graphics");
        graphicsLabel.setStyle("-fx-padding: 0px 0px 10px 0px;");
        vbox.getChildren().add(graphicsLabel);

        Button mcjeronimo = new Button("Mc_Jeronimo [Twitter]");
        mcjeronimo.setMnemonicParsing(false);
        mcjeronimo.setOnAction(e -> openWebsiteInBrowser("https://twitter.com/Jer0nimo_97"));
        mcjeronimo.getStyleClass().add("link");
        vbox.getChildren().add(mcjeronimo);

        Label programminglabel = new Label("Programming");
        vbox.getChildren().add(programminglabel);

        Button mrtroblebutton = new Button("MrTroble [Twitter]");
        mrtroblebutton.setOnAction(e -> openWebsiteInBrowser("https://twitter.com/TherealMrTroble"));
        mrtroblebutton.getStyleClass().add("link");
        vbox.getChildren().add(mrtroblebutton);

        Button derZaubererButton = new Button("Der_Zauberer [Twitter]");
        derZaubererButton.setMnemonicParsing(false);
        derZaubererButton.setOnAction(e -> openWebsiteInBrowser("https://twitter.com/Der_Zauberer_DA"));
        derZaubererButton.getStyleClass().add("link");
        vbox.getChildren().add(derZaubererButton);

        Button shirosakaButton = new Button("Shirosaka");
        Button codingByTimoButton = new Button("Codingbytimo");
        vbox.getChildren().addAll(shirosakaButton, codingByTimoButton);

        Label librariesLabel = new Label("Libraries");
        vbox.getChildren().add(librariesLabel);

        Button minecraftAuthButton = new Button("Minecraft-Auth: GPLv3 [GitHub]");
        minecraftAuthButton.setOnAction(e -> openWebsiteInBrowser("https://github.com/RaphiMC/MinecraftAuth/blob/main/LICENSE"));
        minecraftAuthButton.getStyleClass().add("link");
        vbox.getChildren().add(minecraftAuthButton);

        Button orgJsonButton = new Button("JSON-java: Public domain [GitHub]");
        orgJsonButton.setOnAction(e -> openWebsiteInBrowser("https://github.com/stleary/JSON-java/blob/master/LICENSE"));
        orgJsonButton.getStyleClass().add("link");
        vbox.getChildren().add(orgJsonButton);

        Button minecrafterFontButton = new Button("MineCrafter Font: CC BY-ND [Dafont]");
        minecrafterFontButton.setOnAction(e -> openWebsiteInBrowser("https://www.dafont.com/de/minecrafter.font"));
        minecrafterFontButton.getStyleClass().add("link");
        vbox.getChildren().add(minecrafterFontButton);

        Button gsonButton = new Button("Gson: Apache License 2.0 [GitHub]");
        gsonButton.setOnAction(e -> openWebsiteInBrowser("https://github.com/google/gson/blob/master/LICENSE"));
        gsonButton.getStyleClass().add("link");
        vbox.getChildren().add(gsonButton);

        Button log4jButton = new Button("Log4j2: Apache License 2.0 [GitHub]");
        log4jButton.setOnAction(e -> openWebsiteInBrowser("https://github.com/apache/logging-log4j2/blob/2.x/LICENSE.txt"));
        log4jButton.getStyleClass().add("link");
        vbox.getChildren().add(log4jButton);

        Button commonsLangButton = new Button("commons-lang: Apache License 2.0 [GitHub]");
        commonsLangButton.setOnAction(e -> openWebsiteInBrowser("https://github.com/apache/commons-lang/blob/master/LICENSE.txt"));
        commonsLangButton.getStyleClass().add("link");
        vbox.getChildren().add(commonsLangButton);

        Button devDirsButton = new Button("directories-jvm: MPL Version 2.0 [GitHub]");
        devDirsButton.setOnAction(e -> openWebsiteInBrowser("https://codeberg.org/dirs/directories-jvm/src/branch/main/LICENSE"));
        devDirsButton.getStyleClass().add("link");
        vbox.getChildren().add(devDirsButton);
    }

    public static void openWebsiteInBrowser(String url) {
        Launcher.getInstance().getHostServices().showDocument(url);
    }
}