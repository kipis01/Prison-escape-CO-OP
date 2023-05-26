package Utils;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FlipImage {
	public static BufferedImage flipImageHorizontally(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
		Graphics2D g2d = flippedImage.createGraphics();

		AffineTransform transform = new AffineTransform();
		transform.scale(-1, 1);
		transform.translate(-width, 0);

		g2d.setTransform(transform);
		g2d.drawImage(image, 0, 0, null);

		g2d.dispose();

		return flippedImage;
	}
}
