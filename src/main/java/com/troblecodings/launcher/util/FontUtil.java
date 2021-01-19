package com.troblecodings.launcher.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import com.troblecodings.launcher.assets.Assets;

public class FontUtil {

	private static Font defaultFont = null;
	
	public static void init() {
		try {
			defaultFont = Font.createFont(Font.TRUETYPE_FONT, Assets.getResourceAsStream("font.otf"));
		} catch (FontFormatException | IOException e) {
			// Fallback font
			defaultFont = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()[0];
		}
	}

	public static Font getFont(float size) {
		return defaultFont.deriveFont(size);
	}
	
}
