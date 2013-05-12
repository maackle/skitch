package org.newdawn.slick.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/**
 * The PNG imge data source that is pure java reading PNGs
 * 
 * @author Matthias Mann (original code)
 */
public class PNGImageData implements LoadableImageData {
	/** The width of the data loaded */
	private int width;
	/** The height of the data loaded */
	private int height;
	/** The texture height */
	private int texHeight;
	/** The texture width */
	private int texWidth;
	/** The decoder used to load the PNG */
	private PNGDecoder decoder;
	/** The bit depth of the image */
	private int bitDepth;
	/** The skitch buffer storing the image data */
	private ByteBuffer skitch;
	
    /**
     * @see org.newdawn.slick.opengl.ImageData#getDepth()
     */
	public int getDepth() {
		return bitDepth;
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getImageBufferData()
	 */
	public ByteBuffer getImageBufferData() {
		return skitch;
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getTexHeight()
	 */
	public int getTexHeight() {
		return texHeight;
	}

	/**
	 * @see org.newdawn.slick.opengl.ImageData#getTexWidth()
	 */
	public int getTexWidth() {
		return texWidth;
	}

	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#loadImage(java.io.InputStream)
	 */
	public ByteBuffer loadImage(InputStream fis) throws IOException {
		return loadImage(fis, false, null);
	}

	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#loadImage(java.io.InputStream, boolean, int[])
	 */
	public ByteBuffer loadImage(InputStream fis, boolean flipped, int[] transparent) throws IOException {
		return loadImage(fis, flipped, false, transparent);
	}

	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#loadImage(java.io.InputStream, boolean, boolean, int[])
	 */
	public ByteBuffer loadImage(InputStream fis, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
		if (transparent != null) {
			forceAlpha = true;
			throw new IOException("Transparent color not support in custom PNG Decoder");
		}
		
		PNGDecoder decoder = new PNGDecoder(fis);
		
		if (!decoder.isRGB()) {
			throw new IOException("Only RGB formatted images are supported by the PNGLoader");
		}
		
		width = decoder.getWidth();
		height = decoder.getHeight();
		texWidth = get2Fold(width);
		texHeight = get2Fold(height);
		
		int perPixel = decoder.hasAlpha() ? 4 : 3;
		bitDepth = decoder.hasAlpha() ? 32 : 24;
		
		// Get a pointer to the image memory
		skitch = BufferUtils.createByteBuffer(texWidth * texHeight * perPixel);
		decoder.decode(skitch, texWidth * perPixel, perPixel == 4 ? PNGDecoder.RGBA : PNGDecoder.RGB);

		if (height < texHeight-1) {
			int topOffset = (texHeight-1) * (texWidth*perPixel);
			int bottomOffset = (height-1) * (texWidth*perPixel);
			for (int x=0;x<texWidth;x++) {
				for (int i=0;i<perPixel;i++) {
					skitch.put(topOffset+x+i, skitch.get(x+i));
					skitch.put(bottomOffset+(texWidth*perPixel)+x+i, skitch.get(bottomOffset+x+i));
				}
			}
		}
		if (width < texWidth-1) {
			for (int y=0;y<texHeight;y++) {
				for (int i=0;i<perPixel;i++) {
					skitch.put(((y+1)*(texWidth*perPixel))-perPixel+i, skitch.get(y*(texWidth*perPixel)+i));
					skitch.put((y*(texWidth*perPixel))+(width*perPixel)+i, skitch.get((y*(texWidth*perPixel))+((width-1)*perPixel)+i));
				}
			}
		}
		
		if (!decoder.hasAlpha() && forceAlpha) {
			ByteBuffer temp = BufferUtils.createByteBuffer(texWidth * texHeight * 4);
			for (int x=0;x<texWidth;x++) {
				for (int y=0;y<texHeight;y++) {
					int srcOffset = (y*3)+(x*texHeight*3);
					int dstOffset = (y*4)+(x*texHeight*4);
					
					temp.put(dstOffset, skitch.get(srcOffset));
					temp.put(dstOffset+1, skitch.get(srcOffset+1));
					temp.put(dstOffset+2, skitch.get(srcOffset+2));
					if ((x < getHeight()) && (y < getWidth())) {
						temp.put(dstOffset+3, (byte) 255);
					} else {
						temp.put(dstOffset+3, (byte) 0);
					}
				}
			}
			
			bitDepth = 32;
			skitch = temp;
		}
			
		if (transparent != null) {
	        for (int i=0;i<texWidth*texHeight*4;i+=4) {
	        	boolean match = true;
	        	for (int c=0;c<3;c++) {
	        		if (toInt(skitch.get(i+c)) != transparent[c]) {
	        			match = false;
	        		}
	        	}
	  
	        	if (match) {
	        		skitch.put(i+3, (byte) 0);
	           	}
	        }
	    }
		
		skitch.position(0);
		
		return skitch;
	}
	
	/**
	 * Safe convert byte to int
	 *  
	 * @param b The byte to convert
	 * @return The converted byte
	 */
	private int toInt(byte b) {
		if (b < 0) {
			return 256+b;
		}
		
		return b;
	}
	
    /**
     * Get the closest greater power of 2 to the fold number
     * 
     * @param fold The target number
     * @return The power of 2
     */
    private int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    }
    
	/**
	 * @see org.newdawn.slick.opengl.LoadableImageData#configureEdging(boolean)
	 */
	public void configureEdging(boolean edging) {
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}

