package com.github.bjarneh.jtv;

// std
import javax.swing.tree.DefaultTreeCellRenderer;

// local
import com.github.bjarneh.utilz.res;


/**
 * Extend DefaultTreeCellRenderer to use better looking icons.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTreeCellRenderer extends DefaultTreeCellRenderer {

    public JtvTreeCellRenderer() {

        super();

        setOpenIcon(res.get().getIcon("img/dir_open.png"));
        setClosedIcon(res.get().getIcon("img/dir_close.png"));
        setLeafIcon(null);

    }
}

