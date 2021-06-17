package com.troblecodings.launcher.assets;

import java.util.ArrayList;

import javafx.scene.image.Image;

public class ImageController {
	
	ArrayList<Image> images = new ArrayList<Image>();
	
	public ArrayList<Image> getImages() {
		images.add(Assets.getImage("background.png"));
		images.add(Assets.getImage("background_2.png"));
		images.add(Assets.getImage("background_3.png"));
		return images;
	}

}
