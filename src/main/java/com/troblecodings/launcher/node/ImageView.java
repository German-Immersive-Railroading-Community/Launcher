package com.troblecodings.launcher.node;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;

public class ImageView extends Node {

	protected final BufferedImage image;
	protected final Runnable onButton;
	
	public ImageView(int x1, int y1, int x2, int y2, String name) {
		this(x1, y1, x2, y2, name, null);
	}
	
	public ImageView(int x1, int y1, int x2, int y2, String name, Runnable btn) {
		super(x1, y1, x2, y2);
		this.image = Assets.getImage(name);
		this.onButton = btn;
	}
	
	@Override
	public boolean update(int mousex, int mousey, int mousebtn) {
		boolean b = super.update(mousex, mousey, mousebtn);
		if(clicked && onButton != null)
			onButton.run();
		return b;
	}
	
	@Override
	public void draw(Graphics gr) {
		if(!visible)
			return;
		
		float dx = (x2 - x1) * 0.5f, dy = (y2 - y1) * 0.5f;
		int xl1 = Math.round((x1 + dx) - (dx * scaleX)), yl1 = Math.round((y1 + dy) - (dy * scaleY)), 
				xl2 = Math.round((x1 + dx) + (dx * scaleX)), yl2 = Math.round((y1 + dy) + (dy * scaleY));
		gr.drawImage(image, xl1, yl1, xl2, yl2, 0, 0, image.getWidth(), image.getHeight(), Launcher.INSTANCEL);
	}

}
