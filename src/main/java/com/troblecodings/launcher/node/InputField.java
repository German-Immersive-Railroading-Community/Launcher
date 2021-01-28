package com.troblecodings.launcher.node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

import com.troblecodings.launcher.ErrorDialog;
import com.troblecodings.launcher.Launcher;

public class InputField extends Label {

	private boolean pass, focused = false, num = false;
	private String password = "";
	private MiddlePart parent;
	private Runnable run = () -> {
	};
	private int cursorPos;

	public InputField(int x1, int y1, int x2, int y2, boolean pass, MiddlePart parent) {
		this(x1, y1, x2, y2, pass, parent, false);
	}

	public InputField(int x1, int y1, int x2, int y2, boolean pass, MiddlePart parent, boolean num) {
		super(x1, y1, x2, y2, Color.WHITE, "");
		this.pass = pass;
		this.parent = parent;
		this.num = num;
	}

	@Override
	public void draw(Graphics gr) {
		super.draw(gr);
		if (focused) {
			int tmp = x1 + ((x2 - x1) / 2) + width / 2 + (label.isEmpty() ? width
					: gr.getFontMetrics().stringWidth(label.substring(0, cursorPos)) - width - 2);
			gr.drawString("|", tmp, y2);
		}
	}

	@Override
	public boolean update(int mousex, int mousey, int mousebtn) {
		super.update(mousex, mousey, mousebtn);
		if (clicked) {
			if (!focused)
				cursorPos = this.label.length();
			parent.unfocuse();
			this.setFocused(true);
			Launcher.INSTANCE.repaint(1);
			return true;
		}
		return false;
	}

	@Override
	public void keyTyped(char key, int keycode, boolean ctrl) {
		if (!focused || (this.label.length() > 80
				&& (keycode != KeyEvent.VK_BACK_SPACE || keycode != KeyEvent.VK_LEFT || keycode != KeyEvent.VK_RIGHT)))
			return;

		if (keycode == KeyEvent.VK_LEFT && cursorPos > 0) {
			cursorPos--;
			Launcher.INSTANCE.repaint(1);
		} else if (keycode == KeyEvent.VK_RIGHT && cursorPos < this.label.length()) {
			cursorPos++;
			Launcher.INSTANCE.repaint(1);
		}

		if (ctrl && keycode == KeyEvent.VK_V) {
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			try {
				String insert = (String) c.getData(DataFlavor.stringFlavor);
				if (pass) {
					this.password = insert(insert, this.password);
					for (int i = this.label.length(); i < password.length(); i++) {
						this.label += "*";
					}
				} else {
					this.label = insert(insert, this.label);
				}
				cursorPos += insert.length();
				this.run.run();
				Launcher.INSTANCE.repaint(1);
			} catch (UnsupportedFlavorException | IOException e) {
				ErrorDialog.createDialog(e);
			}
			return;
		}

		if (tryBackspace(keycode))
			return;

		String str = regex(key);
		if (str.isEmpty())
			return;

		if (pass) {
			this.password = insert(str, this.password);
			this.label += "*";
		} else {
			this.label = insert(str, this.label);
		}
		cursorPos++;
		this.run.run();
		Launcher.INSTANCE.repaint(1);
	}

	private String regex(char c) {
		String str = String.valueOf(c);
		if (pass) {
			return str.replaceAll("[^\\w !\"#$%&'()*+,\\-.\\/:;<=>?@\\[\\]^_`{|}~]", "");
		} else if (num) {
			return str.replaceAll("[^0-9]", "");
		} else {
			return str.replaceAll("[^\\w\\-@._]", "");
		}

	}

	private boolean tryBackspace(int keycode) {
		if (keycode == KeyEvent.VK_BACK_SPACE) {
			if (this.label.isEmpty() || (pass && this.password.isEmpty()))
				return true;
			if (pass) {
				int tmpcur = cursorPos;
				this.password = del(this.password);
				if (!this.label.isEmpty() && tmpcur != 0)
					this.label = this.label.substring(0, this.label.length() - 1);
			} else
				this.label = del(this.label);
			this.run.run();
			return true;
		}
		return false;
	}

	private String insert(String text, String lab) {
		if (lab.isEmpty())
			return text;
		return lab.substring(0, cursorPos) + text + lab.substring(cursorPos);
	}

	private String del(String lab) {
		if (cursorPos <= 0)
			return lab;
		String rt = lab.substring(0, cursorPos - 1) + lab.substring(cursorPos);
		Launcher.INSTANCE.repaint(1);
		cursorPos--;
		return rt;
	}

	public String getPassword() {
		return password;
	}

	public String getText() {
		return label;
	}

	public void setFocused(boolean b) {
		focused = b;
	}

	public Runnable getRun() {
		return run;
	}

	public void setRun(Runnable run) {
		this.run = run;
	}
	
}
