package com.troblecodings.launcher;

import java.awt.Color;

import com.troblecodings.launcher.node.ImageView;
import com.troblecodings.launcher.node.Label;
import com.troblecodings.launcher.node.MiddlePart;
import com.troblecodings.launcher.util.FontUtil;

public class ErrorPart extends MiddlePart{
	
	public ErrorPart(final MiddlePart last, final String errorHeader, final String error) {
		this.add(new ImageView(0, 0, Launcher.WIDTH, Launcher.HEIGHT, "error.png"));
		
		Label lab1 = new Label((Launcher.WIDTH - 412) / 2, 310, (Launcher.WIDTH + 412) / 2, 330, Color.RED, errorHeader);
		lab1.setFont(FontUtil.getFont(30f));
		this.add(lab1);

		Label lab2 = new Label((Launcher.WIDTH - 412) / 2, 360, (Launcher.WIDTH + 412) / 2, 380, Color.RED, error);
		this.add(lab2);
		
		Label lab3 = new Label((Launcher.WIDTH - 412) / 2, 510, (Launcher.WIDTH + 412) / 2, 530, Color.GRAY, "Back",
				() -> Launcher.INSTANCEL.setPart(last == null ? new LoginPage():last));
		lab3.setFont(FontUtil.getFont(20f));
		this.add(lab3);
	}
	
}
