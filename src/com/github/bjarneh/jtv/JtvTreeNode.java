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

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Extend DefaultMutableTreeNode in order to override the toString method.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTreeNode extends DefaultMutableTreeNode
    implements Comparable<JtvTreeNode> {

    static final long serialVersionUID = 0;

    boolean marked = false;

    public JtvTreeNode(){
        super();
    }

    public JtvTreeNode(Object obj){
        super(obj);
    }

    public JtvTreeNode(Object obj, boolean allowsChildren){
        super(obj, allowsChildren);
    }

    public boolean isMarked(){
        return marked;
    }

    public void toggleMark(){
        marked = !marked;
    }

    @Override
    public String toString(){

        Object userObj = getUserObject();

        if( userObj == null ){
            return "";
        }
        if( userObj instanceof File ){
            File f = (File) userObj;
            return f.getName(); // looks better with less path :-)
        }
        return super.toString();
    }

    @Override
    public int compareTo(JtvTreeNode other){

        File otherFile = (File) other.getUserObject();
        File myFile    = (File) this.getUserObject();

        return myFile.compareTo(otherFile);
    }

}
