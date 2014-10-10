// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

// libb
import com.github.bjarneh.utilz.res;


/**
 * Extend DefaultTreeCellRenderer to use better looking icons.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTreeCellRenderer extends DefaultTreeCellRenderer {

    static final long serialVersionUID = 0;

    //  LOOKS PRETTY GOOD
    //
    //  Gothic Uralic
    //  L M Mono Lt10 Bold
    //  L M Mono10 Regular

    static public int fontSize    = 12;
    static public int fontStyle   = Font.PLAIN;
    static public String fontName = Font.MONOSPACED;

    static Font[] fonts  = null;
    static int fontIndex = 0;

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


    protected Font nextFont(){
        if( fonts == null ){
            allFonts(); // fonts gets filled
        }
        return fonts[fontIndex++ % fonts.length];
    }


    public static Font[] allFonts(){
        if( fonts == null ){
            fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                       .getAllFonts();
            for(int i = 0; i < fonts.length; i++){
                fonts[i] = new Font(fonts[i].getName(),fontStyle,fontSize);
            }
        }
        return fonts;
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
///             int r = Integer.parseInt(htmlColor.substring(1,3), 16);
///             int g = Integer.parseInt(htmlColor.substring(3,5), 16);
///             int b = Integer.parseInt(htmlColor.substring(5), 16);
/// 
///             highlighted = new Color(r, g, b);
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
