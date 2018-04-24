package Tetris;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {

	public static Image[] loadImage(String path, int width) throws IOException{
		BufferedImage load = ImageIO.read(ImageLoader.class.getResource(path));
		Image[] images = new Image[load.getWidth() / width];
		for(int i = 0; i < images.length; i++){
			images[i] = load.getSubimage(i * width, 0, width, width);
		}
		return images;
	}
}
