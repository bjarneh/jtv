package com.github.bjarneh.jtv;

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Extend DefaultMutableTreeNode in order to override the toString method.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTreeNode extends DefaultMutableTreeNode {

    public JtvTreeNode(){
        super();
    }

    public JtvTreeNode(Object obj){
        super(obj);
    }

    public JtvTreeNode(Object obj, boolean allowsChildren){
        super(obj, allowsChildren);
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
}
