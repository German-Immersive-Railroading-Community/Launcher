package com.troblecodings.launcher.node;

import java.awt.event.MouseEvent;

import com.troblecodings.launcher.Launcher;

public class SliderButton extends Button {

	private int x;
	private int min, max, startx, endx, value;
	private boolean wasover;

	public SliderButton(int min, int max, int startx, int endx, int starty, String standard) {
		super(startx, starty, startx + 24, starty + 42, standard, () -> {
		});
		this.min = min;
		this.max = max;
		this.startx = startx;
		this.endx = endx;
	}

	@Override
	public boolean update(int mousex, int mousey, int mousebtn) {
		super.update(mousex, mousey, mousebtn);
		if (clicked) {
			x = mousex - x1;
			wasover = true;
		}
		if (mousebtn != MouseEvent.BUTTON1) {
			wasover = false;
		}
		return wasover;
	}

	@Override
	public void drag(int mousex, int mousey) {
		if (wasover) {
			int tmpx = mousex - x;
			x1 = Math.min(Math.max(tmpx, startx), endx);
			x2 = x1 + 24;
			this.value = Math.round((max - min) * ((x1 - startx) / (float) (endx - startx))) + min;
			this.onAction.run();
			Launcher.INSTANCE.repaint(x1, y1, x2 - x1, y2 - y1);
		}
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = Math.max(Math.min(value, max), min);
		x1 = Math.round((endx - startx) * (float) ((this.value - min) / (float) (max - min))) + startx;
		x2 = x1 + 24;
	}
}
