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
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;


/**
 * Extend JTree to override a single method, great design again Java.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTree extends JTree {

    static final long serialVersionUID = 0;

    public JtvTree(TreeModel treeModel){
        super(treeModel);
    }

    public JtvTree(TreeNode treeNode){
        super(treeNode);
    }

    @Override
    public String getToolTipText(MouseEvent e){
        if (getRowForLocation(e.getX(), e.getY()) == -1) {
            return "<html><b>Ctrl+K</b> &nbsp; Toggle help";
        }
        return null;
    }
}
