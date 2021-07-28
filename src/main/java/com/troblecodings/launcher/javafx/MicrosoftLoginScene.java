package com.troblecodings.launcher.javafx;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.util.AuthUtil;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;

public class MicrosoftLoginScene extends Scene {
	
	private static StackPane stackpane = new StackPane();
	
	private static WebEngine engine; 
	
	public MicrosoftLoginScene() {
		super(stackpane);
		Launcher.setupScene(this, stackpane);
		
		VBox vbox = new VBox();
		vbox.setMaxHeight(500);
		vbox.setMaxWidth(625);
		vbox.setAlignment(Pos.CENTER);
		stackpane.getChildren().add(vbox);
		
		// Setup cookie manager
		final CookieManager manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		
		final WebView webView = new WebView();
		webView.setMinWidth(600);
		engine = webView.getEngine();
		
		engine.setJavaScriptEnabled(true);
		engine.load(Authenticator.microsoftLogin().toString());
		
		webView.getEngine().getHistory().getEntries().addListener(this::loginCheck);
		
		vbox.getChildren().addAll(webView);
		
		final ImageView trainImageView = new ImageView(Assets.getImage("train2.png"));
		trainImageView.setTranslateX(760 - trainImageView.getImage().getWidth());
		trainImageView.setTranslateY(325 - trainImageView.getImage().getHeight());
		stackpane.getChildren().add(trainImageView);
	}
	
	private void loginCheck(ListChangeListener.Change<? extends WebHistory.Entry> event) {		
		if (event.next() && event.wasAdded()) {
			for (WebHistory.Entry entry : event.getAddedSubList()) {
				if (entry.getUrl().startsWith(Authenticator.microsoftLoginRedirect())) {
					final String authCode = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
					
					try {
						AuthUtil.microsoftLogin(authCode);
						Platform.runLater(() -> {
							Launcher.setScene(Launcher.HOMESCENE);
						});
					} catch (AuthenticationException ex) {
						// Platform.runLater(() -> error.setText("Wrong credentials!"));
						engine.reload();
						Launcher.onError(ex);
					}
				}
			}
		}
	}
	
}
