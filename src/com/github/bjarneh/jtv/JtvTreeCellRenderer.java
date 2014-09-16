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

    public JtvTreeCellRenderer() {

        super();

        setOpenIcon(res.get().icon("img/dir_open.png"));
        setClosedIcon(res.get().icon("img/dir_close.png"));
        setLeafIcon(null); // to remove leaf icons from this look and feel

        // FONT EXPERIMENT, GO WITH DEFAULT?
///         setFont( new Font( Font.MONOSPACED, Font.PLAIN, 12) );
///         setFont( new Font( Font.SERIF, Font.PLAIN, 14) );
///         setFont( new Font( null, Font.PLAIN, 14) );
///         setFont( new Font( Font.DIALOG, Font.PLAIN, 12) );
///         setFont( new Font( Font.SANS_SERIF, Font.PLAIN, 12) );
///         setFont( new Font("Monaco", Font.PLAIN, 12) );
///         setFont( new Font( Font.DIALOG_INPUT, Font.PLAIN, 12) );

///         GraphicsEnvironment g = 
///             GraphicsEnvironment.getLocalGraphicsEnvironment();
///         for( Font f: g.getAllFonts() ){
///             System.out.printf(" font: %s\n", f);
///         }
    }

}
