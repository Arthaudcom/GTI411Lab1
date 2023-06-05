package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
    ColorSlider hueCS;
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

        computeHueImage(hue, saturation, value);
        computeSaturationImage(hue, saturation, value);
        computeValueImage(hue, saturation, value);
    }

    /**
     * Work-in-progress -> need to verify how to generate hue, saturation and value bufferedimage.
     * @param hue
     * @param saturation
     * @param value
     */
    private void computeValueImage(int hue, int saturation, int value) {
        Pixel p = new Pixel();
        for (int i = 0; i < imagesWidth; i++) {
            p.setHSV(hue, saturation, value);
            int v = p.getValue();

            for (int j = 0; j < imagesHeight; j++) {
                valueImage.setRGB(i, j, v);
            }
        }
        if (hueCS != null) {
            hueCS.update(hueImage);
        }
    }

    private void computeSaturationImage(int hue, int saturation, int value) {
    }

    private void computeHueImage(int hue, int saturation, int value) {
    }

    @Override
    public void update() {
        Pixel currentColor = new Pixel(hue, saturation, value);

        if (currentColor.getARGB() == result.getPixel().getARGB()) 
            return;

        hue = result.getPixel().getHue();
        saturation = result.getPixel().getSaturation();
        value = result.getPixel().getValue();

        hueCS.setValue(hue);
        saturationCS.setValue(saturation);
        valueCS.setValue(value);

        computeHueImage(hue, saturation, value);
        computeSaturationImage(hue, saturation, value);
        computeValueImage(hue, saturation, value);
    }

    @Override
    public void update(ColorSlider cs, int v) {
        boolean updateHue = false;
        boolean updateSaturation = false;
        boolean updateValue = false;

        if (cs == hueCS && v != hue) {
            hue = v;
            updateSaturation = true;
            updateValue = true;
        }

        if (cs == saturationCS && v != saturation) {
            saturation = v;
            updateHue = true;
            updateValue = true;
        }

        if (cs == valueCS && v != value) {
            value = v;
            updateHue = true;
            updateSaturation = true;
        }

        if (updateHue) {
            computeHueImage(hue, saturation, value);
        }

        if (updateSaturation) {
            computeSaturationImage(hue, saturation, value);
        }

        if (updateValue) {
            computeValueImage(hue, saturation, value);
        }

        Pixel pixel = new Pixel(hue, saturation, value); //Create a new constructor
        result.setPixel(pixel);
    }
    
}