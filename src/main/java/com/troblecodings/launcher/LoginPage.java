package com.troblecodings.launcher;

import java.awt.Color;

import com.troblecodings.launcher.node.Button;
import com.troblecodings.launcher.node.ImageView;
import com.troblecodings.launcher.node.InputField;
import com.troblecodings.launcher.node.Label;
import com.troblecodings.launcher.node.MiddlePart;
import com.troblecodings.launcher.util.AuthUtil;

public class LoginPage extends MiddlePart{

	private InputField email, pass;
	protected static Label label = new Label((Launcher.WIDTH - 412) / 2, 0, (Launcher.WIDTH + 412) / 2, 500, Color.RED, "");
	
	public LoginPage() {
		this.add(new ImageView(0, 0, Launcher.WIDTH, Launcher.HEIGHT, "login.png"));
		ImageView view = new ImageView(0, 0, Launcher.WIDTH, Launcher.HEIGHT, "loginbackground.png");
		this.add(view);
		Button loginbtn = new Button((Launcher.WIDTH - 164) / 2, 421, (Launcher.WIDTH - 164) / 2 + 164, 463, "loginbutton.png", this::login);
		this.add(loginbtn);
		view.setVisible(false);
		loginbtn.setVisible(false);
		loginbtn.setEnabled(false);
		email = new InputField((Launcher.WIDTH - 412) / 2, 260, (Launcher.WIDTH + 412) / 2, 280, false, this);
		pass = new InputField((Launcher.WIDTH - 412) / 2, 360, (Launcher.WIDTH + 412) / 2, 380, true, this);
		email.setRun(() -> {
			if(!email.getText().isEmpty() && !pass.getText().isEmpty()) {
				view.setVisible(true);
				loginbtn.setVisible(true);
				loginbtn.setEnabled(true);
			} else {
				view.setVisible(false);
				loginbtn.setVisible(false);
				loginbtn.setEnabled(false);
			}
		});
		pass.setRun(email.getRun());
		this.add(email);
		this.add(pass);
		this.add(label);
		this.add(new Label((Launcher.WIDTH - 412) / 2, 510, (Launcher.WIDTH + 412) / 2, 530, Color.GRAY, "Lizenzen & Kredits", () -> Launcher.INSTANCEL.setPart(new CreditPage())));
	}
	
	private void login() {
		try {
			if(AuthUtil.auth(email.getText(), pass.getPassword()) != null) {
				Launcher.INSTANCEL.setPart(new HomePage());
				Launcher.INSTANCEL.home.setActivated(true);
				Launcher.INSTANCEL.settings.setVisible(true);
				Launcher.INSTANCEL.home.setVisible(true);
				Launcher.INSTANCEL.settings.setEnabled(true);
				Launcher.INSTANCEL.home.setEnabled(true);
			} else {
				label.setText("Authentication failed!");
			}
		} catch (Throwable e) {
			label.setText(e.getMessage());
		}
	}
	
}
