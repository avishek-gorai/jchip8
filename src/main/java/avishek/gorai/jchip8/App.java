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
package avishek.gorai.jchip8;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App
extends JFrame {
    private static final long serialVersionUID = -5465098962286923212L;

    App() {
        super("jCHIP8");
        setSize(300, 300);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}
