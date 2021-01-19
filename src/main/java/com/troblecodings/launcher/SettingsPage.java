package com.troblecodings.launcher;

import java.awt.Color;

import com.troblecodings.launcher.node.ImageView;
import com.troblecodings.launcher.node.InputField;
import com.troblecodings.launcher.node.Label;
import com.troblecodings.launcher.node.MiddlePart;
import com.troblecodings.launcher.node.SliderButton;
import com.troblecodings.launcher.util.StartupUtil;

public class SettingsPage extends MiddlePart{

	public static String NEWBASEDIR;
	
	private Label label;
	public static final int MIN = 526, MAX = 15258;
	
	public SettingsPage() {
		this.add(new ImageView(0, 0, Launcher.WIDTH, Launcher.HEIGHT, "settings.png"));
		InputField xinput = new InputField(505, 372, 618, 390, false, this, true);
		xinput.setText(StartupUtil.LWIDTH);
		xinput.setRun(() -> StartupUtil.LWIDTH = xinput.getText());
		this.add(xinput);
		InputField yinput = new InputField(670, 372, 772, 390, false, this, true);
		yinput.setText(StartupUtil.LHEIGHT);
		yinput.setRun(() -> StartupUtil.LHEIGHT = yinput.getText());
		this.add(yinput);
		
		this.add(new Label(368, 220, 524, 306, Color.WHITE, String.valueOf(MIN) + "MB"));
		this.add(new Label(710, 220, 960, 306, Color.WHITE, String.valueOf(MAX) + "MB"));
		label = new Label(0, 0, Launcher.WIDTH, 310, Color.decode("0xb16beb"), String.valueOf(StartupUtil.RAM) + "MB");
		this.add(label);
		
		SliderButton slider = new SliderButton(MIN, MAX, 422, 857 - 24, 176 + 68, "sliderisolation.png");
		slider.setValue(StartupUtil.RAM);
		slider.setOnAction(() -> label.setText(String.valueOf(StartupUtil.RAM = slider.getValue()) + "MB"));
		this.add(slider);
		
		InputField directory = new InputField((Launcher.WIDTH - 412) / 2, 460, (Launcher.WIDTH + 412) / 2, 490, false, this);
		directory.setText(SettingsPage.NEWBASEDIR);
		directory.setRun(() -> NEWBASEDIR = directory.getText());
		this.add(directory);
	}
	
}
