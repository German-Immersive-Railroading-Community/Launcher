package com.troblecodings.launcher.node;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.assets.Assets;

public class Button extends Node {
	
	private final BufferedImage standard, hover, pressed, activated;
	protected Runnable onAction;
	private boolean active = true;
	public Runnable onHover = null;
	
	public Button(int x1, int y1, int x2, int y2, String standard, Runnable run) {
		this(x1, y1, x2, y2, standard, null, null, run);
	}

	public Button(int x1, int y1, int x2, int y2, String standard, String hover, String pressed, Runnable run) {
		this(x1, y1, x2, y2, standard, hover, pressed, null, run);
	}

	public Button(int x1, int y1, int x2, int y2, String standard, String hover, String pressed, String activat,
			Runnable run) {
		super(x1, y1, x2, y2);
		this.standard = Assets.getImage(standard);
		this.hover = hover == null ? this.standard : Assets.getImage(hover);
		this.pressed = pressed == null ? this.standard : Assets.getImage(pressed);
		this.activated = activat == null ? this.standard : Assets.getImage(activat);
		this.onAction = run;
	}

	@Override
	public boolean update(int mousex, int mousey, int mousebtn) {
		super.update(mousex, mousey, mousebtn);
		if (!enabled)
			return false;
		if (clicked) {
			active = true;
			onAction.run();
			Launcher.INSTANCE.repaint(x1, y1, x2 - x1, y2 - y1);
			return true;
		}
		if(hovered && onHover != null)
			onHover.run();
		return false;
	}
	
	public void setActivated(boolean b) {
		active = b;
	}

	@Override
	public void draw(Graphics gr) {
		if(!visible || !enabled)
			return;
		
		float dx = (x2 - x1) * 0.5f, dy = (y2 - y1) * 0.5f;
		int xl1 = Math.round((x1 + dx) - (dx * scaleX)), yl1 = Math.round((y1 + dy) - (dy * scaleY)), 
				xl2 = Math.round((x1 + dx) + (dx * scaleX)), yl2 = Math.round((y1 + dy) + (dy * scaleY));
		if (active) {
			gr.drawImage(activated, xl1, yl1, xl2, yl2, 0, 0, activated.getWidth(), activated.getHeight(), Launcher.INSTANCEL);
			return;
		}
		if (clicked) {
			gr.drawImage(pressed, xl1, yl1, xl2, yl2, 0, 0, pressed.getWidth(), pressed.getHeight(), Launcher.INSTANCEL);
			return;
	    }
		if (hovered) {
			gr.drawImage(hover, xl1, yl1, xl2, yl2, 0, 0, hover.getWidth(), hover.getHeight(), Launcher.INSTANCEL);
		} else
			gr.drawImage(standard, xl1, yl1, xl2, yl2, 0, 0, standard.getWidth(), standard.getHeight(), Launcher.INSTANCEL);
	}

	public Runnable getOnAction() {
		return onAction;
	}

	public void setOnAction(Runnable onAction) {
		this.onAction = onAction;
	}

}
