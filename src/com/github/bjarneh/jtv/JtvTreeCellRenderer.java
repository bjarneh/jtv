// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.tree.DefaultTreeCellRenderer;

// libb
import com.github.bjarneh.utilz.res;


/**
 * Extend DefaultTreeCellRenderer to use better looking icons.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTreeCellRenderer extends DefaultTreeCellRenderer {

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

}
