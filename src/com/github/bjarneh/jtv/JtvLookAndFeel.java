// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

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

}
