package com.troblecodings.launcher.assets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.troblecodings.launcher.ErrorDialog;

public class Assets {

	public static InputStream getResourceAsStream(String name) {
		return Assets.class.getResourceAsStream(name);
	}
	
	public static BufferedImage getImage(String name) {
		try {
			return ImageIO.read(getResourceAsStream(name));
		} catch (IOException e) {
			ErrorDialog.createDialog(e);
			return null;
		}
	}
	
}
