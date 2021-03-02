package com.troblecodings.launcher.assets;

import java.io.InputStream;
import javafx.scene.image.Image;

public class Assets {

	public static InputStream getResourceAsStream(String name) {
		return Assets.class.getResourceAsStream(name);
	}
	
	public static String getStyleSheet(String name) {
		return Assets.class.getResource(name).toExternalForm();
	}

	public static Image getImage(String name) {
		return new Image(getResourceAsStream(name));
	}

}
