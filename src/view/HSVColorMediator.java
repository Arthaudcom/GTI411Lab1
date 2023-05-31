package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

public class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
    ColorSlider  hueCS;
    ColorSlider saturationCS;
    ColorSlider valueCS;
    int hue;
    int saturation;
    int value;
    BufferedImage hueImage;
    BufferedImage saturationImage;
    BufferedImage valueImage;
    int imagesWidth;
    int imagesHeight;
    ColorDialogResult result;

    HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
        this.imagesWidth = imagesWidth;
        this.imagesHeight = imagesHeight;
        this.hue = result.getPixel().getHue();
        this.saturation = result.getPixel().getSaturation();
        this.value = result.getPixel().getValue();
        this.result = result;
        result.addObserver(this);

        hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        saturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeRedImage(red, green, blue);
		computeGreenImage(red, green, blue);
		computeBlueImage(red, green, blue); 
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
        int red = (int) (255 * (1 - cyan) * (1 - black));
        int green = (int) (255 * (1 - magenta) * (1 - black));
        int blue = (int) (255 * (1 - yellow) * (1 - black));
        Pixel pixel = new Pixel(red, green, blue);
        result.setPixel(pixel);
    }

    public void computeHueImage(int hue, int saturation, int value) {
        Pixel p = new Pixel(cyan, yellow, magenta, black);
        for (int i = 0; i < imagesWidth; i++) {
            p.setMagenta((int)(((double)i / (double)imagesWidth) * 255.0));
            int rgb = p.getARGB();
            for (int j = 0; j < imagesHeight; ++j) {
                magentaImage.setRGB(i, j, rgb);
            }
        }
        if (magentaCS != null) {
            magentaCS.update(magentaImage);
        }
    }

    public void computeCyanImage(int cyan, int yellow, int magenta, int black) {
        Pixel p = new Pixel(cyan, yellow, magenta, black);
        for (int i = 0; i < imagesWidth; i++) {
            p.setCyan((int)(((double)i / (double)imagesWidth) * 255.0));
            int rgb = p.getARGB();
            for (int j = 0; j < imagesHeight; ++j) {
                cyanImage.setRGB(i, j, rgb);
            }
        }
        if (cyanCS != null) {
            cyanCS.update(cyanImage);
        }
    }

    public void computeYellowImage(int cyan, int yellow, int magenta, int black) {
        Pixel p = new Pixel(cyan, yellow, magenta, black);
        for (int i = 0; i < imagesWidth; i++) {
            p.setYellow((int)(((double)i / (double)imagesWidth) * 255.0));
            int rgb = p.getARGB();
            for (int j = 0; j < imagesHeight; ++j) {
                yellowImage.setRGB(i, j, rgb);
            }
        }
        if (yellowCS != null) {
            yellowCS.update(yellowImage);
        }
    }

    

    /**
     * @return
     */
    public BufferedImage getHueImage() {
        return hueImage;
    }

    /**
     * @return
     */
    public BufferedImage getSaturationImage() {
        return saturationImage;
    }

    /**
     * @return
     */
    public BufferedImage getValueImage() {
        return valueImage;
    }


    /**
     * @param slider
     */
    public void setHueCS(ColorSlider slider) {
        hueCS = slider;
        slider.addObserver(this);
    }

    /**
     * @param slider
     */
    public void setSaturationCS(ColorSlider slider) {
        saturationCS = slider;
        slider.addObserver(this);
    }

    /**
     * @param slider
     */
    public void setValueCS(ColorSlider slider) {
        valueCS = slider;
        slider.addObserver(this);
    }


    /**
     * @return
     */
    public double getHue() {
        return hue;
    }

    /**
     * @return
     */
    public double getSaturation() {
        return saturation;
    }

    /**
     * @return
     */
    public double getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see model.ObserverIF#update()
     */
    public void update() {
        // When updated with the new "result" color, if the "currentColor"
        // is aready properly set, there is no need to recompute the images.
        Pixel currentColor = new Pixel(cyan, yellow, magenta, 255);
        if (currentColor.getARGB() == result.getPixel().getARGB())
            return;

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
