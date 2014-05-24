package util;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JComponent;

public class ImageUtil {

	public static byte[] convertToPNG(BufferedImage buffi){     
		try{                   
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
			ImageWriter writer = writers.next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			//          param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			//          param.setCompressionQuality(.75f);
			writer.setOutput(ios);
			writer.write(null, new IIOImage(buffi, null, null), param);
			byte[] data = baos.toByteArray();
			writer.dispose(); 
			return data;          
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage drawingToGrayscaleBufferedImage(Image colorImage){  
		BufferedImage image = new BufferedImage(colorImage.getWidth(null), colorImage.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);  
		Graphics g = image.getGraphics();  
		g.drawImage(colorImage, 0, 0, null);  
		g.dispose();
		return image;
	}

	public static Image getImageFromBytes(byte[] is) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(is);
		return ImageIO.read(bais);
	}

	public static byte[] getImageBytes(BufferedImage bufImage) {
		try {
			java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
			javax.imageio.ImageIO.write(bufImage, "png", baos);
			return baos.toByteArray();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static BufferedImage copy(BufferedImage im) {
		int w = im.getWidth();
		int h = im.getHeight();
		int type = im.getType();
		if (type == BufferedImage.TYPE_CUSTOM) {
			System.out.println("note: not preserving type");
			type = im.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB
					: BufferedImage.TYPE_INT_RGB;
		}
		BufferedImage result = new BufferedImage(w, h, type);
		Graphics2D g = result.createGraphics();
		g.drawRenderedImage(im, null);
		g.dispose();
		return result;
	}


	public static byte[] compress(byte[] b) {
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);
		compressor.setInput(b);
		compressor.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(b.length);
		// Compress the data
		byte[] buf = new byte[1024];
		while (!compressor.finished()) {
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}
		try {
			bos.close();
		} catch (IOException e) {}
		// Get the compressed data
		return bos.toByteArray();
	}

	public static byte[] deCompress(byte[] b) {
		Inflater decompressor = new Inflater();
		byte[] buf = new byte[1024];
		decompressor.setInput(b);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(b.length);
		// Decompress the data
		buf = new byte[1024];
		while (!decompressor.finished()) {
			try {
				int count = decompressor.inflate(buf);
				bos.write(buf, 0, count);
			} catch (DataFormatException e) {}
		}
		try {
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Get the decompressed data
		return bos.toByteArray();
	}
	
	public static Image scaleImage(Image img, JComponent comp) {
		return img.getScaledInstance(comp.getWidth(), comp.getHeight(),
				Image.SCALE_SMOOTH);
	}

	public static BufferedImage getBufferedImage(Image img){
		BufferedImage bufferedImage;
		if (img instanceof BufferedImage)
			return (BufferedImage)img;
		bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(img, 0, 0, img.getWidth(null),img.getHeight(null), null);
		return bufferedImage;
	}

}
