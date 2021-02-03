package com.troblecodings.launcher;

import java.awt.Color;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.troblecodings.launcher.node.Button;
import com.troblecodings.launcher.node.ImageView;
import com.troblecodings.launcher.node.InputField;
import com.troblecodings.launcher.node.Label;
import com.troblecodings.launcher.node.MiddlePart;
import com.troblecodings.launcher.node.SliderButton;
import com.troblecodings.launcher.util.FileUtil;
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
		
		this.add(new Button(540, 577, 730, 600, "settingbuttonoff.png", () -> resetFiles()));
		
		this.add(new Label((Launcher.WIDTH - 412) / 2, 685, (Launcher.WIDTH + 412) / 2, 705, Color.GRAY, "Lizenzen & Kredits", () -> Launcher.INSTANCEL.setPart(new CreditPage(this))));
	}
	
	private void resetFiles() {
		ArrayList<File> files = new ArrayList<>();
		files.add(Paths.get(FileUtil.BASE_DIR + "/options.txt").toFile());
		files.add(Paths.get(FileUtil.BASE_DIR + "/optionsof.txt").toFile());
		files.add(Paths.get(FileUtil.BASE_DIR + "/GIR.json").toFile());
		File[] modfiles = Paths.get(FileUtil.BASE_DIR + "/mods").toFile().listFiles();
		for(int i = 0; i < modfiles.length; i++) {
			files.add(modfiles[i]);
		}
		files.add(Paths.get(FileUtil.BASE_DIR + "/mods").toFile());
		File[] assets = Paths.get(FileUtil.BASE_DIR + "/assets").toFile().listFiles();
		for(int i = 0; i < assets.length; i++) {
			files.add(assets[i]);
		}
		files.add(Paths.get(FileUtil.BASE_DIR + "/assets").toFile());
		File[] libraries = Paths.get(FileUtil.BASE_DIR + "/libraries").toFile().listFiles();
		for(int i = 0; i < libraries.length; i++) {
			files.add(libraries[i]);
		}
		files.add(Paths.get(FileUtil.BASE_DIR + "/libraries").toFile());
		File[] config = Paths.get(FileUtil.BASE_DIR + "/config").toFile().listFiles();
		for(int i = 0; i < config.length; i++) {
			files.add(config[i]);
		}
		files.add(Paths.get(FileUtil.BASE_DIR + "/config").toFile());
		for (File file : files) {
			if(file != null && file.exists()) {
				file.delete();
			}
		}
		try {
			FileUtil.init();
		} catch (Throwable e) {
		}
		Launcher.INSTANCEL.setPart(new HomePage());
	}
	
	
}
