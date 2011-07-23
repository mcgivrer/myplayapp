package shared;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;

import shared.exceptions.ThumbnailException;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Thumbnail generator.
 * give it input image and sizes target, thunmbs images will be 
 * gerenated in the destination fodler.
 * 
 * Size are Resolution object.
 * 
 * @author Frédéric Delorme<frederic.delorme@gmail.com>
 * 
 */
public class ThumbnailGenerator {
	
	private static ThumbnailGenerator _instance;

	
	/**
	 * Resolution entity to modelize thumbnail size and name.
	 * @author frederic
	 *
	 */
	protected class Resolution {
		public int width;
		public int height;
		public String name;

	}

	/**
	 * Reads an image in a file and creates a thumbnail in another file.
	 * largestDimension is the largest dimension of the thumbnail, the other
	 * dimension is scaled accordingly. Utilises weighted stepping method to
	 * gradually reduce the image size for better results, i.e. larger steps to
	 * start with then smaller steps to finish with. Note: always writes a JPEG
	 * because GIF is protected or something - so always make your outFilename
	 * end in 'jpg'. PNG's with transparency are given white backgrounds
	 * 
	 */
	public void create(File inFilename, File outFilename, String size)
			throws Exception {
		double scale;
		int sizeDifference, originalImageLargestDim;
		int largestDimension;

		Resolution res = this.extractSize(size);
		largestDimension = Math.max(res.width,res.height);

		if (!inFilename.getName().endsWith(".jpg")
				&& !inFilename.getName().endsWith(".jpeg")
				&& !inFilename.getName().endsWith(".gif")
				&& !inFilename.getName().endsWith(".png")) {
			throw new ThumbnailException(
					"Error: Unsupported image type, please only either JPG, GIF or PNG");
		}

		Image inImage = Toolkit.getDefaultToolkit().getImage(
				inFilename.getAbsolutePath());
		if (inImage.getWidth(null) == -1 || inImage.getHeight(null) == -1) {
			throw new ThumbnailException("Error loading file: \"" + inFilename + "\"");
		}
		// find biggest dimension
		if (inImage.getWidth(null) > inImage.getHeight(null)) {
			scale = (double) largestDimension / (double) inImage.getWidth(null);
			sizeDifference = inImage.getWidth(null) - largestDimension;
			originalImageLargestDim = inImage.getWidth(null);
		} else {
			scale = (double) largestDimension
					/ (double) inImage.getHeight(null);
			sizeDifference = inImage.getHeight(null) - largestDimension;
			originalImageLargestDim = inImage.getHeight(null);
		}
		// create an image buffer to draw to
		BufferedImage outImage = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_RGB); // arbitrary init so code compiles
		Graphics2D g2d;
		AffineTransform tx;
		if (scale < 1.0d) // only scale if desired size is smaller than original
		{
			int numSteps = sizeDifference / 100;
			int stepSize = sizeDifference / numSteps;
			int stepWeight = stepSize / 2;
			int heavierStepSize = stepSize + stepWeight;
			int lighterStepSize = stepSize - stepWeight;
			int currentStepSize, centerStep;
			double scaledW = inImage.getWidth(null);
			double scaledH = inImage.getHeight(null);
			if (numSteps % 2 == 1) // if there's an odd number of steps
				centerStep = (int) Math.ceil((double) numSteps / 2d); // find
																		// the
																		// center
																		// step
			else
				centerStep = -1; // set it to -1 so it's ignored later
			Integer intermediateSize = originalImageLargestDim, previousIntermediateSize = originalImageLargestDim;

			for (Integer i = 0; i < numSteps; i++) {
				if (i + 1 != centerStep) // if this isn't the center step
				{
					if (i == numSteps - 1) // if this is the last step
					{
						// fix the stepsize to account for decimal place errors
						// previously
						currentStepSize = previousIntermediateSize
								- largestDimension;
					} else {
						if (numSteps - i > numSteps / 2) // if we're in the
															// first half of the
															// reductions
							currentStepSize = heavierStepSize;
						else
							currentStepSize = lighterStepSize;
					}
				} else // center step, use natural step size
				{
					currentStepSize = stepSize;
				}
				intermediateSize = previousIntermediateSize - currentStepSize;
				scale = (double) intermediateSize
						/ (double) previousIntermediateSize;
				scaledW = (int) scaledW * scale;
				scaledH = (int) scaledH * scale;
				outImage = new BufferedImage((int) scaledW, (int) scaledH,
						BufferedImage.TYPE_INT_RGB);
				g2d = outImage.createGraphics();
				g2d.setBackground(Color.WHITE);
				g2d.setColor(new Color(0.0f, 0.0f, 0.0f, 1.0f));
				g2d.clearRect(0, 0, outImage.getWidth(), outImage.getHeight());
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

				tx = new AffineTransform();
				tx.scale(scale, scale);
				g2d.drawImage(inImage, tx, null);
				g2d.dispose();
				inImage = new ImageIcon(outImage).getImage();
				previousIntermediateSize = intermediateSize;
			}
		} else {
			// just copy the original
			outImage = new BufferedImage(inImage.getWidth(null),
					inImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
			g2d = outImage.createGraphics();
			g2d.setBackground(Color.BLACK);
			g2d.clearRect(0, 0, outImage.getWidth(), outImage.getHeight());
			tx = new AffineTransform();
			tx.setToIdentity(); // use identity matrix so image is copied
								// exactly
			g2d.drawImage(inImage, tx, null);
			g2d.dispose();
		}
		// JPEG-encode the image and write to file.
		OutputStream os = new FileOutputStream(outFilename);
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
		encoder.encode(outImage);
		os.close();
	}

	/**
	 * Convert String formated to a Resolution object.
	 * 
	 * @param size
	 *            the size parameter must obey to "[aaaaaaaa]-[999]x[999]"
	 *            format.
	 * @return Resolution object
	 */
	private Resolution extractSize(String size) {

		Resolution res = new Resolution();
		if (size.equals("")) {
			res.name = "";
			res.width = -1;
			res.height = -1;
		} else {
			if(size.contains("-")){
				String[] params = size.split("-");
				res.name = params[0];
				String[] dimension = params[1].split("x");
				res.width = Integer.parseInt(dimension[0]);
				res.height = Integer.parseInt(dimension[1]);
			}else{
				res.name = size;
				String[] dimension = size.split("x");
				res.width = Integer.parseInt(dimension[0]);
				res.height = Integer.parseInt(dimension[1]);				
			}
		}
		return res;
	}
	
	/**
	 * Singleton <code>getInstance()</code> implementation. if <code>_instance</code>
	 * is not instantiate, instantiate it and return this.
	 * @return
	 */
	public static ThumbnailGenerator getInstance(){
		if(_instance==null){
			_instance = new ThumbnailGenerator();
		}
		return _instance;
	}
}