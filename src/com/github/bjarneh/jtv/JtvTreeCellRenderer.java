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
// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

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


    // Only Monaco currently
    protected static void registerFonts(){

        try{

            InputStream is;
            Font monacoTtf, monacoSized;

            is = res.get().url("font/Monaco.ttf").openStream();
            
            monacoTtf   = Font.createFont(Font.TRUETYPE_FONT, is);
            monacoSized = monacoTtf.deriveFont(Font.PLAIN, (float) fontSize );

            GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();

            ge.registerFont( monacoSized );

        //java.io.IOException || java.awt.FontFormatException
        }catch(Exception e){ 
            // not much we can do here..
            log.log(Level.INFO, e.getMessage(), e);
        }

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
