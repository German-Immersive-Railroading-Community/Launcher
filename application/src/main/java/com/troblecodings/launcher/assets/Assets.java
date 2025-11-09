package com.troblecodings.launcher.assets;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Objects;

public class Assets {

	public static InputStream getResourceAsStream(String name) {
		return Assets.class.getResourceAsStream(name);
	}
	
	public static String getStyleSheet(String name) {
		return Objects.requireNonNull(Assets.class.getResource(name)).toExternalForm();
	}

	public static Image getImage(String name) {
		return new Image(getResourceAsStream(name));
	}

}
