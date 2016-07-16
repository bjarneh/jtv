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
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

// libb
import com.github.bjarneh.utilz.res;


/**
 * Extend DefaultTreeCellRenderer to use better looking icons.
 *
 * The icons used here come from the J editor.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTreeCellRenderer extends DefaultTreeCellRenderer {

    static final long serialVersionUID = 0;

    private static final Logger log = 
        Logger.getLogger(JtvTreeCellRenderer.class.getName());

    static public int fontSize    = 12;
    static public int fontStyle   = Font.PLAIN;
    static public String fontName = Font.MONOSPACED;

    static int fontIndex = 0;
    static Font[] fonts  = null;
    static String[] fontNames = null;
    static int currFontSize = 12;

    static boolean alternativeColor = false;
    static Color highlighted = Color.GREEN;

    static final Pattern rgb = 
        Pattern.compile("^rgb\\((\\d+),(\\d+),(\\d+)\\)$");


    public JtvTreeCellRenderer() {

        super();

        setOpenIcon(res.get().icon("img/dir_open.png"));
        setClosedIcon(res.get().icon("img/dir_close.png"));
        setLeafIcon(null); // to remove leaf icons from this look and feel

        setFont( new Font( fontName, fontStyle, fontSize) );

    }


    // Add some truetype fonts
    protected static void registerFonts(){

        try{

            InputStream is;
            Font addTtf, addSized;

            String[] fontNames = {
                "font/Monaco.ttf",
                "font/SpecialElite.ttf"
            };

            GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();

            for(String n: fontNames){
                is = res.get().url(n).openStream();
                addTtf   = Font.createFont(Font.TRUETYPE_FONT, is);
                addSized = addTtf.deriveFont(Font.PLAIN, (float) fontSize );
                ge.registerFont( addSized );
            }

        // Multi-catch not supported in 1.5 compliant source
        //java.io.IOException || java.awt.FontFormatException
        }catch( Exception e ){
            // not much we can do here..
            log.log(Level.SEVERE, e.getMessage(), e);
        }

    }


    protected Font nextFont(){
        if( fonts == null ){
            allFonts(); // fonts gets filled
            fontIndex = fontPosition();
        }
        return fonts[++fontIndex % fonts.length];
    }


    protected Font prevFont(){
        if( fonts == null ){
            allFonts(); // fonts gets filled
            fontIndex = fontPosition();
        }
        fontIndex--;
        if( fontIndex < 0 ){
            fontIndex += fonts.length;
        }
        return fonts[ fontIndex % fonts.length ];
    }


    // Look up the position of the current Font
    private int fontPosition(){
        if( fonts != null ){
            String currFont = getFont().toString();
            int tmp = Arrays.binarySearch(fontNames, currFont);
            if( tmp > 0 ){// Should always be true
                return tmp;
            }
        }
        return 0;
    }


    public static Font[] allFonts(){
        if( fonts == null ){
            long t1, t0 = System.currentTimeMillis();
            registerFonts();
            fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                       .getAllFonts();

            TreeMap<String, Font> sortedFonts = new TreeMap<String, Font>();
            for(int i = 0; i < fonts.length; i++){
                fonts[i] = new Font(fonts[i].getName(),fontStyle,fontSize);
                sortedFonts.put( fonts[i].getName(), fonts[i] );
            }
            int cnt = 0;
            fontNames = new String[ fonts.length ];
            for(String k: sortedFonts.keySet()){
                fonts[cnt] = sortedFonts.get(k);
                fontNames[cnt++] = k;
            }
            t1 = System.currentTimeMillis();
            log.log(Level.INFO,"Loaded "+ fonts.length + " fonts in "+
                    (t1-t0) + " milliseconds");
        }
        return fonts;
    }


    public static Font getFussyFont(String name, boolean isTab){
        Font tmp = null;
        if( fonts == null ){ allFonts(); }
        int location = Arrays.binarySearch(fontNames, name);
        if( location > 0 ){
            if( isTab && fonts[location].getName().equals(name) ){
                tmp = fonts[ (location+1) % fonts.length ];
            }else{
                tmp = fonts[location];
            }
        } else if( location < -1 ){
            int placement = (location + 1) * -1;
            tmp = fonts[placement];
        }
        if( tmp != null ){
            return new Font( tmp.getName(), tmp.getStyle(), currFontSize );
        }
        return null;
    }


    public static void setColor(String asRGB){

        if( asRGB != null ) {

            Matcher m = rgb.matcher( asRGB );
            if( m.matches() ){
                int r = Integer.parseInt(m.group(1));
                int g = Integer.parseInt(m.group(2));
                int b = Integer.parseInt(m.group(3));

                highlighted = new Color(r,g,b);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Color getBackgroundNonSelectionColor(){
        if( alternativeColor ){
            return highlighted;
        }
        return super.getBackgroundNonSelectionColor();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Color getTextSelectionColor(){
        if( alternativeColor ){
            return highlighted;
        }
        return super.getTextSelectionColor();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
    {
        if( value instanceof JtvTreeNode ){
            JtvTreeNode n = (JtvTreeNode) value;
            alternativeColor = n.isMarked();
        }
        super.getTreeCellRendererComponent(
                tree, value, sel, expanded, leaf, row, hasFocus);
        return this;
    }

}
