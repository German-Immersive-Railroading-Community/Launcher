package com.troblecodings.launcher.node;

import java.awt.Color;
import java.awt.Graphics;

import com.troblecodings.launcher.Launcher;

public class ProgressBar extends Node {

	private final int max;
	private final Color color;
	
	public ProgressBar(int x1, int y1, int width, int height, Color color) {
		super(x1, y1, 0, height);
		max = width;
		this.color = color;
	}

	public void update(float percentage) {
		this.x2 = (int) (percentage * max);
		Launcher.INSTANCE.repaint();
	}
	
	@Override
	public void draw(Graphics gr) {
		gr.setColor(this.color);
		gr.fillRect(x1, y1, x2, y2);
	}
}
