package com.troblecodings.launcher;

import com.troblecodings.launcher.node.Button;
import com.troblecodings.launcher.node.ImageView;
import com.troblecodings.launcher.node.MiddlePart;
import com.troblecodings.launcher.util.AuthUtil;
import com.troblecodings.launcher.util.StartupUtil;

public class HomePage extends MiddlePart {

	private static Button launch = null;
	private static ImageView connect = null;
	private static boolean isLaunching = false;

	public static final ImageView logo = new ImageView(Launcher.WIDTH / 2 - 396 / 2, Launcher.HEIGHT / 2 - 30 - 528 / 2,
			Launcher.WIDTH / 2 + 396 / 2, Launcher.HEIGHT / 2 - 30 + 528 / 2, "logo.png");

	public HomePage() {
		this.add(new ImageView(0, 0, Launcher.WIDTH, Launcher.HEIGHT, "launchback.png"));
		logo.scaleChange = true;
		this.add(logo);
		
		if (launch == null)
			launch = new Button(465, 577, 465 + 350, 677, "launchbutton.png", this::launch);
		launch.scaleChange = true;
		this.add(launch);
		if (connect == null)
			connect = new ImageView(0, 0, Launcher.WIDTH, Launcher.HEIGHT, "connecting.png");
		connect.setVisible(false);
		if (isLaunching) {
			launch.setActivated(false);
			connect.setVisible(true);
		}
		this.add(connect);
	}

	@Override
	public void onExit() {}
	
	private void launch() {
		isLaunching = true;
		launch.setActivated(false);
		connect.setVisible(true);
		new Thread(() -> {
			try {
				StartupUtil.prestart();
				if (AuthUtil.START_PARAMS != null) {
					Process pro = StartupUtil.start(AuthUtil.START_PARAMS);
					Launcher.INSTANCE.setVisible(false);
					pro.waitFor();
					Launcher.INSTANCE.setVisible(true);
					connect.setVisible(false);
					launch.setActivated(true);
					isLaunching = false;
					return;
				}
				LoginPage page = new LoginPage();
				LoginPage.label.setText("There was an error with your credentials!");
				Launcher.INSTANCEL.setPart(page);
				connect.setVisible(false);
				launch.setActivated(true);
				isLaunching = false;
			} catch (Throwable e) {
				ErrorDialog.createDialog(e);
				connect.setVisible(false);
				launch.setActivated(true);
				isLaunching = false;
			}
		}).start();
	}

}
