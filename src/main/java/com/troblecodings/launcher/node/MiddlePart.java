package com.troblecodings.launcher.node;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class MiddlePart implements MouseListener, MouseMotionListener, KeyListener{
		
	private ArrayList<Node> nodes = new ArrayList<Node>();

	/*
	 * I want to take a minute to express my hate against the swing / AWT API Just
	 * horrible
	 */
	
	@Override
	public void mousePressed(MouseEvent e) {
		nodes.forEach(node -> node.update(e.getX(), e.getY(), e.getButton()));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		nodes.forEach(node -> node.update(e.getX(), e.getY(), MouseEvent.NOBUTTON));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		nodes.forEach(node -> node.update(e.getX(), e.getY(), e.getButton()));
	}
	
	public void paint(Graphics gr) {
		nodes.forEach(node -> node.draw(gr));
	}
		
	public void add(Node n) {
		nodes.add(n);
	}
	
	public void unfocuse() {
		nodes.forEach(node -> {
			if(node instanceof InputField) {
				((InputField) node).setFocused(false);
			}
		});
	}

	public void onExit() {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		nodes.forEach(node -> node.keyTyped(e.getKeyChar(), e.getKeyCode(), e.isControlDown()));
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		nodes.forEach(node -> node.drag(e.getX(), e.getY()));
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}
}
