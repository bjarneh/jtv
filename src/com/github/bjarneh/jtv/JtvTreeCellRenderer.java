// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
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


    /**
     * {@inheritDoc}
     */
    @Override
    public Color getBackgroundNonSelectionColor(){
        if( alternativeColor ){
            return Color.GREEN;
        }
        return super.getBackgroundNonSelectionColor();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Color getTextSelectionColor(){
        if( alternativeColor ){
            return Color.GREEN;
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
