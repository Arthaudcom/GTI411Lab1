/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package model;

import java.awt.Color;

/**
 * <p>Title: Pixel</p>
 * <p>Description: Class that handles Pixel data in various formats</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barr�-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author Colin Barr�-Brisebois
 * @version $Revision: 1.11 $
 */
public class Pixel {
    /** ARGB Pixel value */
    private int valueARGB;
    
    /**
     * Pixel default constructor
     */
    public Pixel() {
		valueARGB = 0;
    }
    
	/**
	 * Pixel constructor with a specified ARGB value
	 * @param valueARGB the pixel's ARGB value
	 */
    public Pixel(int valueARGB) {
        this.valueARGB = valueARGB;
    }
    
    public Pixel(int rValue, int gValue, int bValue) {
    	setRed(rValue);
    	setGreen(gValue);
    	setBlue(bValue);
    	setAlpha(255);
    }
    
	public Pixel(int rValue, int gValue, int bValue, int alpha) {
		setRed(rValue);
		setGreen(gValue);
		setBlue(bValue);
		setAlpha(alpha);
	}    
    
    public Pixel(PixelDouble pixel) {
		setRed((int)pixel.getRed());
		setGreen((int)pixel.getGreen());
		setBlue((int)pixel.getBlue());
		setAlpha((int)pixel.getAlpha());
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's ARGB value
	 */    
    public int getARGB() { 
    	return (valueARGB); 
    }

	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's ALPHA value
	 */        
    public int getAlpha() { 
    	return ((valueARGB >> 24) & 0xff); 
    }

	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's RED value
	 */            
    public int getRed() { 
    	return ((valueARGB >> 16) & 0xff); 
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's GREEN value
	 */            
    public int getGreen() { 
    	return ((valueARGB >> 8) & 0xff); 
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's BLUE value
	 */            
    public int getBlue() { 
    	return ((valueARGB) & 0xff); 
    }
    
	/**
	 * Sets an attribute of the pixel
	 * @param valueARGB the pixel's ARGB value
	 */            
    public void setARGB(int valueARGB) { 
		this.valueARGB = valueARGB; 
    }

	/** Sets the color, ignores null pixel. */
    public void setColor(Pixel p) {
	    if (p == null) return;
	    setARGB(p.valueARGB);
	}
	
	/**
	 * Sets an attribute of the pixel
	 * @param valueAlpha the pixel's ALPHA value
	 */               	
    public void setAlpha(int valueAlpha) { 
    	valueARGB = (valueARGB & 0x00ffffff) | ((valueAlpha & 0xff) << 24);
    }

	/**
	 * Sets an attribute of the pixel
	 * @param valueRed the pixel's RED value
	 */               
	public void setRed(int valueRed) { 
		valueARGB = (valueARGB & 0xff00ffff) | ((valueRed & 0xff) << 16);
	}

	/**
	 * Sets an attribute of the pixel
	 * @param valueGreen the pixel's GREEN value
	 */               
	public void setGreen(int valueGreen) { 
		valueARGB = (valueARGB & 0xffff00ff) | ((valueGreen & 0xff) << 8);
	}

	/**
	 * Sets an attribute of the pixel
	 * @param valueBlue the pixel's BLUE value
	 */               
	public void setBlue(int valueBlue) { 
		valueARGB = (valueARGB & 0xffffff00) | ((valueBlue & 0xff));
	}

	/**
	 * Object's toString() method redefinition
	 */               
    public String toString() {
        return new String("(R-" + getRed() + 
                          " G-" + getGreen() + 
                          " B-" + getBlue() + 
                          " A-" + getAlpha() + 
                          ")");
    }

	//Temp/Will see if keeping    
    /**
     * Convert pixel to Color
     * @return color value
     */
    public Color toColor() {
		return new Color((float)getRed() / 255.0F, 
		             	 (float)getGreen() / 255.0F,
		             	 (float)getBlue() / 255.0F);    	
    }
    
    /* 
     * Compute if two colors are the same, based on their ARGB values.
     */ 
    public boolean equals(Object o) {
    	if (o instanceof Pixel) {
    		return (((Pixel)o).getARGB() == getARGB());
    	}
    	return false;
    }

	public void setHSV(int hue, int saturation, int value) {
		if (saturation == 0) {
			setRed(value * 255);
			setGreen(value * 255);
			setBlue(value * 255);
		}
		else
		{
			int h = hue * 6;

			if (h == 6)
				h = 0;
			
			int i = h;
			int o1 = value * (1 - saturation);
			int o2 = value * (1 - saturation * (h - i));
			int o3 = value * (1 - saturation * (1 - (h - i)));
			int r = 0;
			int g = 0;
			int b = 0;

			if (i == 0) {
				r = value;
				g = o3;
				b = o1;
			}
			else if (i == 1) {
				r = o2;
				g = value;
				b = o1;
			}
			else if (i == 2) {
				r = o1;
				g = value;
				b = o3;
			}
			else if (i == 3) {
				r = o1;
				g = o2;
				b = value;
			}
			else if (i == 4) {
				r = o3;
				g = o1;
				b = value;
			}
			else {
				r = value;
				g = o1;
				b = o2;
			}

			setRed(r * 255);
			setGreen(g * 255);
			setBlue(b * 255);
		}
	}

	public int getHue() {
		int red = getRed();
		int green = getGreen();
		int blue = getBlue();
		int max = Math.max(red, Math.max(green, blue));
		int min = Math.min(red, Math.min(green, blue));
		int hue = 0;

		if (red == max && green == min) {
			hue = 5 + (red - blue)/(red - green);
		} 
		else if (red == max && blue == min) {
			hue = 1 - (red - green)/(red - blue);
		}
		else if (green == max && blue == min) {
			hue = 1 + (green - red)/(green - blue);
		}
		else if (green == max && red == min) {
			hue = 3 - (green - blue)/(green - red);
		}
		else if (blue == max && red == min) {
			hue = 3 + (blue - green)/(blue - red);
		}
		else if (blue == max && green == min) {
			hue = 5 - (blue - red)/(blue - green);
		}

		hue = hue * 60;

		if (hue < 0) {
			hue += 360;
		}

		return hue;
	}

    public int getSaturation() {
		int red = getRed();
		int green = getGreen();
		int blue = getBlue();

		int value = getValue();

		int saturation = (value-Math.min(red,Math.min(green, blue)))/value;

        return saturation;
    }

    public int getValue() {
		int red = getRed();
		int green = getGreen();
		int blue = getBlue();

		int value = Math.max(red, Math.max(green, blue));

        return value;
    }
}