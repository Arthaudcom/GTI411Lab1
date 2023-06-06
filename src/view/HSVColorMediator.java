package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
    ColorSlider hueCS;
    ColorSlider saturationCS;
    ColorSlider valueCS;
    float hsvTable[] = new float[3];
    float hue;
    float saturation;
    float value;
    BufferedImage hueImage;
    BufferedImage saturationImage;
    BufferedImage valueImage;
    int imagesWidth;
    int imagesHeight;
    ColorDialogResult result;

    HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
        this.imagesWidth = imagesWidth;
        this.imagesHeight = imagesHeight;
        this.hsvTable = generateHsvTable(result);
        this.hue = hsvTable[0];
        this.saturation = hsvTable[1];
        this.value = hsvTable[2];
        this.result = result;
        result.addObserver(this);

        hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        saturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
        valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);

        computeHueImage(saturation, value);
        computeSaturationImage(hue, value);
        computeValueImage(hue, saturation);
    }

    public float getHue() {
        return hue*255/360;
    }

    public float getSaturation() {
        return saturation*255;
    }

    public float getValue() {
        return value*255;
    }

    public BufferedImage getHueImage() {
        return hueImage;
    }

    public BufferedImage getSaturationImage() {
        return saturationImage;
    }

    public BufferedImage getValueImage() {
        return valueImage;
    }

    public void setHueCS(ColorSlider hueCS) {
        this.hueCS = hueCS;
        hueCS.addObserver(this);
    }

    public void setSaturationCS(ColorSlider saturationCS) {
        this.saturationCS = saturationCS;
        saturationCS.addObserver(this);
    }

    public void setValueCS(ColorSlider valueCS) {
        this.valueCS = valueCS;
        valueCS.addObserver(this);
    }

    private void computeValueImage(float hue, float saturation) {
        for (int i = 0; i < imagesWidth; i++) {
            float v = ((float)i / (float)imagesWidth);
            Pixel p = getPixelFromHsvToRgb(hue, saturation, v);
            int rgb = p.getARGB();

            for (int j = 0; j < imagesHeight; j++) {
                valueImage.setRGB(i, j, rgb);
            }
        }
        if (hueCS != null) {
            hueCS.update(hueImage);
        }
    }

    private void computeSaturationImage(float hue, float value) {
        for (int i = 0; i < imagesWidth; i++) {
            float s = ((float)i/ (float)imagesWidth);
            Pixel p = getPixelFromHsvToRgb(hue, s, value);
            int rgb = p.getARGB();

            for (int j = 0; j < imagesHeight; j++) {
                saturationImage.setRGB(i, j, rgb);
            }
        }
        if (saturationCS != null) {
            saturationCS.update(saturationImage);
        }
    }

    private void computeHueImage(float saturation, float value) {
        for (int i = 0; i < imagesWidth; i++) {
            float h = ((float)i / (float)imagesWidth)*360;
            Pixel p = getPixelFromHsvToRgb(h, saturation, value);
            int rgb = p.getARGB();

            for (int j = 0; j<imagesHeight; j++) {
                hueImage.setRGB(i, j, rgb);
            }
        }
        if (hueCS != null) {
            hueCS.update(hueImage);
        }
    }

    @Override
    public void update() {
        Pixel currentColor = getPixelFromHsvToRgb(hue, saturation, value);

        if (currentColor.getARGB() == result.getPixel().getARGB()) 
            return;

        this.hue = hsvTable[0];
        this.saturation = hsvTable[1];
        this.value = hsvTable[2];

        hueCS.setValue((int) getHue());
        saturationCS.setValue((int) getSaturation());
        valueCS.setValue((int) getValue());

        computeHueImage(saturation, value);
        computeSaturationImage(hue, value);
        computeValueImage(hue, saturation);
    }

    @Override
    public void update(ColorSlider cs, int v) {
        boolean updateHue = false;
        boolean updateSaturation = false;
        boolean updateValue = false;

        if (cs == hueCS && v != (int)getHue()) {
            hue = (float) v*360/255;
            updateSaturation = true;
            updateValue = true;
        }

        if (cs == saturationCS && v != (int)getSaturation()) {
            saturation = (float) v/255;
            updateHue = true;
            updateValue = true;
        }

        if (cs == valueCS && v != (int)getValue()) {
            value = (float) v/255;
            updateHue = true;
            updateSaturation = true;
        }

        if (updateHue) {
            computeHueImage(saturation, value);
        }

        if (updateSaturation) {
            computeSaturationImage(hue, value);
        }

        if (updateValue) {
            computeValueImage(hue, saturation);
        }

        result.setPixel(getPixelFromHsvToRgb(hue, saturation, value));
    }

    private Pixel getPixelFromHsvToRgb(float hue, float saturation, float value) {
        float chroma = value * saturation;
        float secondLargestColor = chroma * (1 - Math.abs(((hue/60)%2) - 1));
        float min = value - chroma;

        float red = -1.0f;
        float green = -1.0f;
        float blue = -1.0f;

        if (0.0f <= hue && hue < 60.0f) {
            red = chroma;
            green = secondLargestColor;
            blue = 0.0f;
        }
        else if (60.0f <= hue && hue < 120.0f) {
            red = secondLargestColor;
            green = chroma;
            blue = 0.0f;
        }
        else if (120.0f <= hue && hue < 180.0f) {
            red = 0.0f;
            green = chroma;
            blue = secondLargestColor;
        }
        else if (180.0f <= hue && hue < 240.0f) {
            red = 0.0f;
            green = secondLargestColor;
            blue = chroma;
        }
        else if (240.0f <= hue && hue < 300.0f) {
            red = secondLargestColor;
            green = 0.0f;
            blue = chroma;
        }
        else if (300.0f <= hue && hue < 360.0f) {
            red = chroma;
            green = 0.0f;
            blue = secondLargestColor;
        }
        
        int intRed = Math.round((red + min)*255.0f);
        int intGreen = Math.round((green + min)*255.0f);
        int intBlue = Math.round((blue + min)*255.0f);
        
        Pixel p = new Pixel(intRed, intGreen, intBlue);

        return p;
    }

    private float[] generateHsvTable(ColorDialogResult result) {
        float hsvTable[] = new float[3];

        float red = (float) result.getPixel().getRed()/255;
		float green = (float) result.getPixel().getGreen()/255;
		float blue = (float) result.getPixel().getBlue()/255;

		float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));

        // hsvTable[1] = saturation
        hsvTable[1] = (max - min)/max;
        // hsvTable[2] = value
        hsvTable[2] = max;

        // hsvTable[0] = hue
        if (red == max && green == min) {
			hsvTable[0] = 5 + (red - blue)/(red - green);
		} 
		else if (red == max && blue == min) {
			hsvTable[0] = 1 - (red - green)/(red - blue);
		}
		else if (green == max && blue == min) {
			hsvTable[0] = 1 + (green - red)/(green - blue);
		}
		else if (green == max && red == min) {
			hsvTable[0] = 3 - (green - blue)/(green - red);
		}
		else if (blue == max && red == min) {
			hsvTable[0] = 3 + (blue - green)/(blue - red);
		}
		else if (blue == max && green == min) {
			hsvTable[0] = 5 - (blue - red)/(blue - green);
		}

        hsvTable[0] = hsvTable[0] * 60;

        if (hsvTable[1] == 0) {
            hsvTable[0] = 0;
        }
        
        if (hsvTable[0] == 360) {
            hsvTable[0] = 0;
        }

        return hsvTable;
    }
}