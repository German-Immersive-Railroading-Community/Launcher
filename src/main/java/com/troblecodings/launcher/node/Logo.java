package com.troblecodings.launcher.node;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.troblecodings.launcher.Launcher;

public class Logo extends ImageView {

	public Logo(int x1, int y1, int width, int height) {
		super(x1, y1, width, height, "logo.png");
	}
	
	@Override
	public void draw(Graphics gr) {
		if(!visible)
			return;
		float dx = x2, dy = y2;
		int xl1 = Math.round(x1 - (dx * scaleX)), yl1 = Math.round(y1 - (dy * scaleY)), 
				xl2 = Math.round(x1 + (dx * scaleX)), yl2 = Math.round(y1 + (dy * scaleY));
		Graphics2D g2 = (Graphics2D)gr;
		g2.drawImage(image, xl1, yl1, xl2, yl2, 0, 0, image.getWidth(), image.getHeight(), Launcher.INSTANCEL);
	}
	
}
