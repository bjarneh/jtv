//  Copyright © 2014 bjarneh
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.github.bjarneh.jtv;

// std
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
/// import javax.swing.plaf.synth.SynthLookAndFeel;

// libb
import com.github.bjarneh.utilz.res;


/**
 * The base for our own look and feel.
 *
 * NOTE: Currently this is basically the Metal look and feel,
 * apart from a few new icons stuffed into the tree-view;
 * but it will hopefully change to something better.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvLookAndFeel extends MetalLookAndFeel {

    static final int  hexBase = 16;
    static final long serialVersionUID = 0;


    /**
     * Public constructor.
     */
    public JtvLookAndFeel(){
        super();
        UIManager.put("Tree.collapsedIcon",
                res.get().icon("img/collapsed.png"));
        UIManager.put("Tree.expandedIcon",
                res.get().icon("img/expanded.png"));
        UIManager.put("Tree.closedIcon",
                res.get().icon("img/dir_close.png"));
        UIManager.put("Tree.line", Color.GRAY);
        UIManager.put("Tree.hash", Color.GRAY);
        UIManager.put("Tree.selectionForeground", Color.WHITE);
        UIManager.put("Tree.selectionBackground", Color.DARK_GRAY);
        UIManager.put("Tree.selectionBorderColor",Color.DARK_GRAY);

        // Styling of the scrollbar
        // Alternative colors: 
        //   #308850 SEAGREEN, #80D0E0 SKYBLUE, #C0C0C0 SILVER
        //   #708890 LIGHTSLATEGRAY, #008880 DARK CYAN, #A0A8A0 DARK GRAY
        UIManager.put("ScrollBar.thumb", hexColor("#E0D8E0")); // GAINSBORO
        UIManager.put("ScrollBar.thumbHighlight", hexColor("#F8F8FF"));//GHOST
        UIManager.put("ScrollBar.thumbDarkShadow", Color.DARK_GRAY);
        UIManager.put("ScrollBarUI", JtvScrollBarUI.class.getName());
    }


    /**
     * Returns the name of this look and feel. This returns
     * {@code "Jtv"}.
     *
     * @return the name of this look and feel
     */
    public String getName() {
        return "jtv";
    }

    /**
     * Returns an identifier for this look and feel. This returns
     * {@code "jtv"}.
     *
     * @return the identifier of this look and feel
     */
    public String getID() {
        return "jtv";
    }

    /**
     * Returns a short description of this look and feel. This returns
     * {@code "jtv look and feel"}.

     * @return a short description for the look and feel
     */
    public String getDescription() {
        return "jtv look and feel";
    }

    // Utility function which should be moved somewhere more fittting
    public static Color hexColor(String s){
        if(s != null && s.matches("^#[0-9A-Fa-f]{6}$") ){
            int r = Integer.parseInt(s.substring(1,3), hexBase);
            int g = Integer.parseInt(s.substring(3,5), hexBase);
            int b = Integer.parseInt(s.substring(5,7), hexBase);
            return new Color(r,g,b);
        }
        return null;
    }
}
