package com.troblecodings.launcher.javafx;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import com.troblecodings.launcher.Launcher;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CreditsScene extends Scene {
	
	private static StackPane stackpane = new StackPane();

	public CreditsScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);
		
		VBox vbox = new VBox();
		vbox.setMaxHeight(400);
		vbox.setMaxWidth(650);
		stackpane.getChildren().add(vbox);
		
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
		Button nidhoggbutton = new Button("Nidhogg by Cydhra [GitHub]");
		nidhoggbutton.setOnAction(event -> openWebsiteInBrowser("https://github.com/Cydhra/Nidhogg/blob/master/LICENSE"));
		Button jsonbutton = new Button("JSON-java by stleary [GitHub]");
		jsonbutton.setOnAction(event -> openWebsiteInBrowser("https://github.com/stleary/JSON-java/blob/master/LICENSE"));
		Button minecrafterbutton = new Button("MineCrafter Font by MadPixel [Dafont]");
		minecrafterbutton.setOnAction(event -> openWebsiteInBrowser("https://www.dafont.com/de/minecrafter.font"));
		
		vbox.getChildren().addAll(graficslabel, mcjeronimo, programminglabel, mrtroblebutton, derzaubererbutton, shirosakaButton, codingbytimoButton, libarieslabel, nidhoggbutton, jsonbutton, minecrafterbutton);
	}
	
	public static void openWebsiteInBrowser(String url) {
		try {
			Desktop.getDesktop().browse(new URL(url).toURI());
		} catch (IOException | URISyntaxException exception) {
			exception.printStackTrace();
		}
	}

}
