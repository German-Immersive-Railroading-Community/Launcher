package com.troblecodings.launcher.node;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.util.FontUtil;

public class Label extends Node{

	protected String label;
	protected int width;
	protected Color color;
	protected Runnable run = () -> {};
	private Font font;
	
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Label(int x1, int y1, int x2, int y2, Color color, String label) {
		this(x1, y1, x2, y2, color, label, null);
	}
	
	public Label(int x1, int y1, int x2, int y2, Color color, String label, Runnable run) {
		super(x1, y1, x2, y2);
		this.label = label;
		this.color = color;
		this.run = run;
		this.font = FontUtil.getFont(20f);
	}
	
	@Override
	public void update(int mousex, int mousey, int mousebtn) {
		super.update(mousex, mousey, mousebtn);
		if(clicked && run != null) {
			this.run.run();
		}
	}
	
	@Override
	public void draw(Graphics gr) {
		((Graphics2D)gr).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		gr.setColor(color);
		gr.setFont(this.font);
		width = gr.getFontMetrics().stringWidth(label);
		gr.drawString(this.label, x1 + ((x2 - x1) / 2) - width / 2, y2);
	}
	
	public void setText(String label) {
		this.label = label;
		if(Launcher.INSTANCE != null) Launcher.INSTANCE.repaint(x1, y1, x2 - x1, y2 - y1);
	}

}
