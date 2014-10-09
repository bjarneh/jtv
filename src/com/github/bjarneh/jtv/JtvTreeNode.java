// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Extend DefaultMutableTreeNode in order to override the toString method.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTreeNode extends DefaultMutableTreeNode {

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

}
