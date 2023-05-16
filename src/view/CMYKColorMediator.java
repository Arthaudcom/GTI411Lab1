package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

public class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider magentaCS;
	ColorSlider cyanCS;
	ColorSlider yellowCS;
    ColorSlider blackCS;
	int cyan;
	int yellow;
	int magenta;
    int black;
    BufferedImage cyanImage;
    BufferedImage yellowImage;
	BufferedImage magentaImage;
    BufferedImage blackImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.cyan = result.getPixel().getCyan();
		this.yellow = result.getPixel().getYellow();
        this.magenta = result.getPixel().getMagenta();
        this.black = result.getPixel().getAlpha();
		this.result = result;
		result.addObserver(this);

		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeMagentaImage(cyan, yellow, magenta, black);
		computeYellowImage(cyan, yellow, magenta, black);
		computeCyanImage(cyan, yellow, magenta, black);	
        computeBlackImage(cyan, yellow, magenta, black);
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateCyan = false;
		boolean updateYellow = false;
		boolean updateMagenta = false;
        boolean updateBlack = false;
		if (s == cyanCS && v != cyan) {
			cyan = v;
			updateYellow = true;
			updateMagenta = true;
		}
		if (s == yellowCS && v != yellow) {
			yellow = v;
			updateCyan = true;
			updateMagenta = true;
		}
		if (s == magentaCS && v != magenta) {
			magenta = v;
			updateCyan = true;
			updateYellow = true;
		}
        if (s == blackCS && v != black) {
            black = v;
            updateCyan = true;
            updateYellow = true;
            updateMagenta = true;
        }
		if (updateCyan) {
			computeCyanImage(cyan, yellow, magenta, black);
		}
		if (updateYellow) {
			computeYellowImage(cyan, yellow, magenta, black);
		}
		if (updateMagenta) {
			computeMagentaImage(cyan, yellow, magenta, black);
		}
        if (updateBlack) {
            computeBlackImage(cyan, yellow, magenta, black);
        }
		Pixel pixel = new Pixel(cyan, yellow, magenta, 255);
		result.setPixel(pixel);
	}
	
	public void computeMagentaImage(int cyan, int yellow, int magenta, int black) { 
		Pixel p = new Pixel(cyan, yellow, magenta, black); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setMagenta((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}
	
	public void computeCyanImage(int cyan, int yellow, int magenta, int black) { 
		Pixel p = new Pixel(cyan, yellow, magenta, black); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setCyan((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
	}

    public void computeYellowImage(int cyan, int yellow, int magenta, int black) { 
		Pixel p = new Pixel(cyan, yellow, magenta, black); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setYellow((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}

    public void computeBlackImage(int cyan, int yellow, int magenta, int black) {
        Pixel p = new Pixel(cyan, yellow, magenta, black);
        for (int i = 0; i<imagesWidth; ++i) {
            p.setAlpha((int)(((double)i / (double)imagesWidth)*255.0));
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                blackImage.setRGB(i, j, rgb);
            }
        }
        if (blackCS != null) {
            blackCS.update(blackImage);
        }
    }
	

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}

    /**
     * @param slider
     */
    public BufferedImage getBlackImage() {
        return blackImage;
    }

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

    /**
     * @param slider
     */
    public void setBlackCS(ColorSlider slider) {
        blackCS = slider;
        slider.addObserver(this);
    }

	/**
	 * @return
	 */
	public double getMagenta() {
		return magenta;
	}

	/**
	 * @return
	 */
	public double getYellow() {
		return yellow;
	}

	/**
	 * @return
	 */
	public double getCyan() {
		return cyan;
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel(cyan, yellow, magenta, 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		cyan = result.getPixel().getCyan();
		yellow = result.getPixel().getYellow();
		magenta = result.getPixel().getMagenta();
        black = result.getPixel().getAlpha();
		
		cyanCS.setValue(cyan);
		yellowCS.setValue(yellow);
		magentaCS.setValue(magenta);
        blackCS.setValue(black);
		computeCyanImage(cyan, yellow, magenta, black);
		computeYellowImage(cyan, yellow, magenta, black);
		computeMagentaImage(cyan, yellow, magenta, black);
        computeBlackImage(cyan, yellow, magenta, black);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}
