/* Copyright (C) 2025  Avishek Gorai <avishekgorai@myyahoo.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


/**
 * 
 */
package avishek.gorai.jchip8.jchip8_app;

import javax.swing.JPanel;

/**
 * 
 */
class Display 
extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 5022455962999905026L;
    private static final int disiplayHeight = 32, displayWidth = 64;
    private boolean[][] displayRam;
    
    Display() {
        setDisplayRam(new boolean[Display.getDisplayHeight()][Display.getDisplayWidth()]);
    }
    
    private static int getDisplayHeight() {
        return disiplayHeight;
    }
    
    private static int getDisplayWidth() {
        return displayWidth;
    }

	Display clear() {
	    for (var i = 0; i < Display.getDisplayHeight(); ++i) {
	        for (var j = 0; j < Display.getDisplayWidth(); ++j) {
	            clearPixel(i, j);
	        }
	    }
		return this;
	}

	private Display clearPixel(int x, int y) {
        getDisplayRam()[x][y] = false;
        return this;
    }

    private Display putPixel(int i, int j) {
	    getDisplayRam()[i][j] = true;
        return this;
    }

    boolean draw(int i, int j, int index, int k) {
		// TODO Auto-generated method stub
		return true;
	}

    /**
     * @return the displayRam
     */
    public boolean[][] getDisplayRam() {
        return displayRam;
    }

    /**
     * @param displayRam the displayRam to set
     */
    public Display setDisplayRam(boolean[][] displayRam) {
        this.displayRam = displayRam;
        return this;
    }

}