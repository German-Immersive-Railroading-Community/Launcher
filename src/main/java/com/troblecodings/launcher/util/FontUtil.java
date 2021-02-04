package com.troblecodings.launcher.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import com.troblecodings.launcher.assets.Assets;

public class FontUtil {

	private static Font defaultFont = init();
	
	private static Font init() {
		try {
			return Font.createFont(Font.TRUETYPE_FONT, Assets.getResourceAsStream("font.otf"));
		} catch (Throwable e) {
			// Fallback font
			return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()[0];
		}
	}

	public static Font getFont(float size) {
		return defaultFont.deriveFont(size);
	}
	
}
