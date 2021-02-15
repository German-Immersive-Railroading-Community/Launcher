package com.troblecodings.launcher;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.troblecodings.launcher.node.ImageView;
import com.troblecodings.launcher.node.Label;
import com.troblecodings.launcher.node.MiddlePart;
import com.troblecodings.launcher.util.FontUtil;

public class CreditPage extends MiddlePart {

	public CreditPage(MiddlePart last) {
		this.add(new ImageView(240, 176, 1040, 545, "credits.png"));
		this.add(new Label((Launcher.WIDTH - 412) / 2, 240, (Launcher.WIDTH + 412) / 2, 260, Color.GRAY, "Nidhogg by Cydhra [Github]", () -> {
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/Cydhra/Nidhogg/blob/master/license"));
			} catch (IOException | URISyntaxException e) {
				ErrorDialog.createDialog(e);
			}
		}));
		this.add(new Label((Launcher.WIDTH - 412) / 2, 280, (Launcher.WIDTH + 412) / 2, 300, Color.GRAY, "JSON-java by stleary [Github]", () -> {
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/stleary/JSON-java/blob/master/LICENSE"));
			} catch (IOException | URISyntaxException e) {
				ErrorDialog.createDialog(e);
			}
		}));
		this.add(new Label((Launcher.WIDTH - 412) / 2, 320, (Launcher.WIDTH + 412) / 2, 340, Color.GRAY, "MineCrafter Font by MadPixel [Dafont]", () -> {
			try {
				Desktop.getDesktop().browse(new URI("https://www.dafont.com/de/minecrafter.font"));
			} catch (IOException | URISyntaxException e) {
				ErrorDialog.createDialog(e);
			}
		}));
		this.add(new Label((Launcher.WIDTH - 412) / 2, 400, (Launcher.WIDTH + 412) / 2, 420, Color.GRAY, "Graphics by Mc_Jeronimo [Twitter]", () -> {
			try {
				Desktop.getDesktop().browse(new URI("https://twitter.com/Jer0nimo_97"));
			} catch (IOException | URISyntaxException e) {
				ErrorDialog.createDialog(e);
			}
		}));
		this.add(new Label((Launcher.WIDTH - 412) / 2, 440, (Launcher.WIDTH + 412) / 2, 460, Color.GRAY, "Programming by MrTroble [Twitter]", () -> {
			try {
				Desktop.getDesktop().browse(new URI("https://twitter.com/TherealMrTroble"));
			} catch (IOException | URISyntaxException e) {
				ErrorDialog.createDialog(e);
			}
		}));
		this.add(new Label((Launcher.WIDTH - 412) / 2, 480, (Launcher.WIDTH + 412) / 2, 500, Color.GRAY, "and Der_Zauberer [Twitter]", () -> {
			try {
				Desktop.getDesktop().browse(new URI("https://twitter.com/Der_Zauberer_DA"));
			} catch (IOException | URISyntaxException e) {
				ErrorDialog.createDialog(e);
			}
		}));
		Label lab = new Label((Launcher.WIDTH - 412) / 2, 510, (Launcher.WIDTH + 412) / 2, 530, Color.GRAY, "Back",
				() -> Launcher.INSTANCEL.setPart(last));
		lab.setFont(FontUtil.getFont(20f));
		this.add(lab);
	}

}
