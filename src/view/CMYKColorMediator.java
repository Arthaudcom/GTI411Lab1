package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

public class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {
    ColorSlider cyanCS;
	ColorSlider yellowCS;
	ColorSlider magentaCS;
	ColorSlider keyCS;
    float cmykTable[] = new float[4];
    float cyan;
    float magenta;
    float yellow;
    float key;
	BufferedImage cyanImage;
	BufferedImage yellowImage;
	BufferedImage magentaImage;
	BufferedImage keyImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;

    public CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
        this.imagesWidth = imagesWidth;
        this.imagesHeight = imagesHeight;
        this.cmykTable = generateCmykTable(result);
        this.cyan = cmykTable[0];
        this.magenta = cmykTable[1];
        this.yellow = cmykTable[2];
        this.key = cmykTable[3];
        this.result = result;
        result.addObserver(this);

        cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		keyImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		computeCyanImage(magenta, yellow, key);
		computeMagentaImage(cyan, yellow, key); 	
		computeYellowImage(cyan, magenta, key);
		computeKeyImage(cyan, magenta, yellow);
    }

    public void setCyanCS(ColorSlider cyanCS) {
        this.cyanCS = cyanCS;
        cyanCS.addObserver(this);
    }

    public void setMagentaCS(ColorSlider magentaCS) {
        this.magentaCS = magentaCS;
        magentaCS.addObserver(this);
    }

    public void setYellowCS(ColorSlider yellowCS) {
        this.yellowCS = yellowCS;
        yellowCS.addObserver(this);
    }

    public void setKeyCS(ColorSlider keyCS) {
        this.keyCS = keyCS;
        keyCS.addObserver(this);
    }

    public BufferedImage getCyanImage() {
        return cyanImage;
    }

    public BufferedImage getMagentaImage() {
        return magentaImage;
    }

    public BufferedImage getYellowImage() {
        return yellowImage;
    }

    public BufferedImage getKeyImage() {
        return keyImage;
    }
    
    public float getCyan() {
        return cyan*255;
    }

    public float getMagenta() {
        return magenta*255;
    }

    public float getYellow() {
        return yellow*255;
    }

    public float getKey() {
        return key*255;
    }

    private void computeKeyImage(float cyan, float magenta, float yellow) {
        for (int i = 0; i < imagesWidth; i++) {
            float k = ((float) i/(float) imagesWidth);
            Pixel p = getPixelFromCmykToRgb(cyan, magenta, yellow, k);
            int rgb = p.getARGB();

            for (int j = 0; j < imagesHeight; j++) {
                keyImage.setRGB(i, j, rgb);
            }
        }
        if (keyCS != null) {
            keyCS.update(keyImage);
        }
    }

    private void computeYellowImage(float cyan, float magenta, float key) {
        for (int i = 0; i < imagesWidth; i++) {
            float y = ((float) i/(float) imagesWidth);
            Pixel p = getPixelFromCmykToRgb(cyan, magenta, y, key);
            int rgb = p.getARGB();

            for (int j = 0; j < imagesHeight; j++) {
                yellowImage.setRGB(i, j, rgb);
            }
        }
        if (yellowCS != null) {
            yellowCS.update(yellowImage);
        }
    }

    private void computeMagentaImage(float cyan, float yellow, float key) {
        for (int i = 0; i < imagesWidth; i++) {
            float m = ((float) i/(float) imagesWidth);
            Pixel p = getPixelFromCmykToRgb(cyan, m, yellow, key);
            int rgb = p.getARGB();

            for (int j = 0; j < imagesHeight; j++) {
                magentaImage.setRGB(i, j, rgb);
            }
        }
        if (magentaCS != null) {
            magentaCS.update(magentaImage);
        }
    }

    private void computeCyanImage(float magenta, float yellow, float key) {
        for (int i = 0; i < imagesWidth; i++) {
            float c = ((float) i/(float) imagesWidth);
            Pixel p = getPixelFromCmykToRgb(c, magenta, yellow, key);
            int rgb = p.getARGB();

            for (int j = 0; j < imagesHeight; j++) {
                cyanImage.setRGB(i, j, rgb);
            }
        }
        if (cyanCS != null) {
            cyanCS.update(cyanImage);
        }
    }

    private Pixel getPixelFromCmykToRgb(float cyan, float magenta, float yellow, float key) {
        float red = 255.0f * (1 - cyan) * (1 - key);
        float green = 255.0f * (1 - magenta) * (1 - key);
        float blue = 255.0f * (1 - yellow) * (1 - key);

        Pixel p = new Pixel(Math.round(red), Math.round(green), Math.round(blue));
        
        return p;
    }

    private float[] generateCmykTable(ColorDialogResult result) {
        float cmykTable[] = new float[4];

        float red = (float) result.getPixel().getRed()/255;
		float green = (float) result.getPixel().getGreen()/255;
		float blue = (float) result.getPixel().getBlue()/255;

        float k = (1 - Math.max(red, Math.max(green, blue)));
        float c = ((1 - red - k)/(1 - k));
        float m = ((1 - green - k)/(1 - k));
        float y = ((1 - blue - k)/(1 - k));

        if (k < 1.0f) {
            cmykTable[0] = c;
			cmykTable[1] = m;
			cmykTable[2] = y;
			cmykTable[3] = k;
        } else {
            cmykTable[0] = 0;
			cmykTable[1] = 0;
			cmykTable[2] = 0;
			cmykTable[3] = 1;
        }

        return cmykTable;
    }

    @Override
    public void update() {
        Pixel currentColor = getPixelFromCmykToRgb(cyan, magenta, yellow, key);

        if (currentColor.getARGB() == result.getPixel().getARGB())
            return;
        
        float[] cmykTable = generateCmykTable(result);

        cyan = cmykTable[0];
        magenta = cmykTable[1];
        yellow = cmykTable[2];
        key = cmykTable[3];

        cyanCS.setValue((int)getCyan());
        magentaCS.setValue((int)getMagenta());
        yellowCS.setValue((int)getYellow());
        keyCS.setValue((int)getKey());

        computeCyanImage(magenta, yellow, key);
        computeMagentaImage(cyan, yellow, key);
        computeYellowImage(cyan, magenta, key);
        computeKeyImage(cyan, magenta, yellow);
    }

    @Override
    public void update(ColorSlider cs, int v) {
        boolean updateCyan = false;
        boolean updateMagenta = false;
        boolean updateYellow = false;
        boolean updateKey = false;

        if (cs == cyanCS && v != (int)getCyan()) {
            cyan = (float)v/255;
            updateMagenta = true;
            updateYellow = true;
            updateKey = true;
        }

        if (cs == magentaCS && v != (int)getMagenta()) {
            magenta = (float)v/255;
            updateCyan = true;
            updateYellow = true;
            updateKey = true;
        }

        if (cs == yellowCS && v != (int)getYellow()) {
            yellow = (float)v/255;
            updateCyan = true;
            updateMagenta = true;
            updateKey = true;
        }

        if (cs == keyCS && v != (int)getKey()) {
            key = (float)v/255;
            updateCyan = true;
            updateMagenta = true;
            updateYellow = true;
        }

        if (updateCyan) {
            computeCyanImage(magenta, yellow, key);
        }

        if (updateMagenta) {
            computeMagentaImage(cyan, yellow, key);
        }

        if (updateYellow) {
            computeYellowImage(cyan, magenta, key);
        }

        if (updateKey) {
            computeKeyImage(cyan, magenta, yellow);
        }

        result.setPixel(getPixelFromCmykToRgb(cyan, magenta, yellow, key));
    }
}
