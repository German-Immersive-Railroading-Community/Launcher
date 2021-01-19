package com.troblecodings.launcher;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.troblecodings.launcher.assets.Assets;
import com.troblecodings.launcher.node.Button;
import com.troblecodings.launcher.node.ImageView;
import com.troblecodings.launcher.node.MiddlePart;
import com.troblecodings.launcher.node.Node;
import com.troblecodings.launcher.util.AuthUtil;
import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.FontUtil;
import com.troblecodings.launcher.util.StartupUtil;

import net.cydhra.nidhogg.exception.TooManyRequestsException;

public class Launcher extends Canvas implements MouseListener, MouseMotionListener, KeyListener, WindowListener {
	public Launcher() {
	}

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;

	public static JFrame INSTANCE;
	public static Launcher INSTANCEL;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		FontUtil.init();
		try {
			FileUtil.init();
			AuthUtil.auth(null, null);
		} catch (TooManyRequestsException e) {
			try {
				Thread.sleep(10000);
				main(args);
				return;
			} catch (InterruptedException e1) {
				ErrorDialog.createDialog(e1);
			}
		} catch (RuntimeException e2) {
			LoginPage.label.setText("There was an error with your saved session!");
		} catch (Throwable e1) {
			ErrorDialog.createDialog(e1);
		}
		EventQueue.invokeLater(() -> {
			try {
				INSTANCE = new JFrame();
				INSTANCE.setTitle("Launcher");
				INSTANCE.setResizable(false);
				INSTANCE.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
				INSTANCE.setBounds((screensize.width - WIDTH) / 2, (screensize.height - HEIGHT) / 2, WIDTH, HEIGHT);
				INSTANCE.setUndecorated(true);
				INSTANCE.setLayout(null);
				INSTANCE.setIconImage(Assets.getImage("icon.png"));
				INSTANCE.setBackground(new Color(0, 0, 0, 0));

				INSTANCEL = new Launcher();
				INSTANCEL.setSize(WIDTH, HEIGHT);
				INSTANCE.add(INSTANCEL);
				INSTANCEL.init();
				INSTANCE.setVisible(true);
			} catch (Exception e) {
				ErrorDialog.createDialog(e);
			}
		});
	}

	private static ArrayList<Node> nodes = new ArrayList<Node>();
	private static final int DRAGBAR_Y = 87;
	private int dx, dy;
	private MiddlePart part;

	protected Button settings, home, launch;

	public void init() {
		part = new HomePage();

		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		nodes.add(new ImageView(0, 0, WIDTH, HEIGHT, "background.png"));
		Button close = new Button(WIDTH - 79, 20, WIDTH - 27, 87, "closebutton.png", this::exit);
		close.scaleChange = true;
		close.scaleFactor = 1.1f;
		nodes.add(close);

		home = new Button((WIDTH - 120) / 2, 20, (WIDTH - 120) / 2 + 120, 42, "homebuttonoff.png", null, null,
				"homebuttonon.png", this::home);
		settings = new Button((WIDTH - 167) / 2, 58, (WIDTH - 167) / 2 + 167, 80, "settingbuttonoff.png", null, null,
				"settingbuttonon.png", this::settings);
		nodes.add(home);
		nodes.add(settings);

		if (FileUtil.DEFAULT == null) {
			home.setActivated(false);
			settings.setActivated(false);
			home.setVisible(false);
			settings.setVisible(false);
			this.part = new LoginPage();
		} else {
			home.setActivated(true);
			settings.setActivated(false);
		}
		setIgnoreRepaint(false);
	}
	
	public static BufferedImage buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	public static Graphics2D graph = buffer.createGraphics();
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		nodes.forEach(node -> node.draw(graph));
		part.paint(graph);
		g.drawImage(buffer, 0, 0, this);
	}
	
	private void settings() {
		home.setActivated(false);
		this.setPart(new SettingsPage());
	}
	
	private void home() {
		settings.setActivated(false);
		this.setPart(new HomePage());
	}

	public void setPart(MiddlePart part) {
		this.part.onExit();
		System.gc();
		this.part = part;
		INSTANCE.repaint();
	}
		
	private void exit() {
		Path settingpath = Paths.get(System.getProperty("user.home") + "/.launcher/Settings.txt");
		try {
			if (Files.notExists(settingpath))
				Files.createFile(settingpath);
			Files.write(settingpath, (StartupUtil.LWIDTH + System.lineSeparator() +  
					StartupUtil.LHEIGHT + System.lineSeparator() + 
					StartupUtil.RAM  + System.lineSeparator() + 
					SettingsPage.NEWBASEDIR).getBytes());
		} catch (IOException e) {
			ErrorDialog.createDialog(e);
		}
		System.exit(0);
	}
	
	/*
	 * I want to take a minute to express my hate against the swing / AWT API Just
	 * horrible
	 */

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dx = e.getX();
		dy = e.getY();
		nodes.forEach(node -> node.update(e.getX(), e.getY(), e.getButton()));
		part.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		nodes.forEach(node -> node.update(e.getX(), e.getY(), MouseEvent.NOBUTTON));
		part.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (dy <= DRAGBAR_Y)
			INSTANCE.setLocation(e.getXOnScreen() - dx, e.getYOnScreen() - dy);
		part.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		nodes.forEach(node -> node.update(e.getX(), e.getY(), e.getButton()));
		part.mouseMoved(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		nodes.forEach(node -> node.keyTyped(e.getKeyChar(), e.getKeyCode(), e.isControlDown()));
		this.part.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		exit();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
