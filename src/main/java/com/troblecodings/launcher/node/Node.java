package com.troblecodings.launcher.node;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import com.troblecodings.launcher.Launcher;

public class Node {

	protected int x1;
	protected int x2;
	protected int y1;
	protected int y2;
	protected boolean hovered, clicked, enabled = true, visible = true;
	public float scaleX = 1;
	public float scaleY = 1;
	private boolean lastHover = false;
	public float scaleFactor = 1.01f;
	public boolean scaleChange = false;

	public Node(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public void setEnabled(boolean b) {
		enabled = b;
	}

	public boolean isMouseOver(int mousex, int mousey) {
		return mousex > x1 && mousex < x2 && mousey > y1 && mousey < y2;
	}

	public void update(int mousex, int mousey, int mousebtn) {
		if (enabled) {
			hovered = isMouseOver(mousex, mousey);
			clicked = mousebtn == MouseEvent.BUTTON1 && hovered;
			
			if(lastHover != hovered && scaleChange) {
				lastHover = hovered;
				if(hovered)
					this.scaleY = this.scaleX = scaleFactor;
				else
					this.scaleY = this.scaleX = 1f;
				Launcher.INSTANCE.repaint();
			}
		}
	}

	public void keyTyped(char key, int keycode, boolean ctrl) {
		
	}
	
	public void drag(int mousex, int mousey) {
		
	}
	
	public void draw(Graphics gr) {
	}
	
	public void setVisible(boolean bool) {
		visible = bool;
		Launcher.INSTANCE.repaint();
	}
}
