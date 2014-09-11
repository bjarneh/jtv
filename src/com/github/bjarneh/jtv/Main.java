// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.io.File;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {


    private static final Logger log =
        Logger.getLogger( Main.class.getName() );


    private static JtvTreeNode getTree(File file){
        JtvTreeNode node = new JtvTreeNode(file);
        if( file.isDirectory() ){
            for(File f: file.listFiles()){
                node.add( getTree( f ));
            }
        }
        return node;
    }


    private static JtvTreeNode buildTree(String ... args){

        File tmp;
        ArrayList<File> dirs = new ArrayList<File>();

        for(String a: args){
            tmp = new File(a);
            if( tmp.isDirectory() ){
                dirs.add( tmp );
            }else{
                log.info("This is not a directory '"+ a +"'");
            }
        }

        if( dirs.size() == 1 ){

            return getTree( dirs.get(0) );

        } else if ( dirs.size() > 1 ) {

            JtvTreeNode dummy = new JtvTreeNode();

            for(File f: dirs){
                dummy.add( getTree( f ) );
            }

            return dummy;

        } else {

            return null;

        }

    }


    public static void main(String[] args) {

        String[] dirs = {"src"};

        if( args.length > 0 ){
            dirs = args;
        }

        final JtvTreeNode root = buildTree(dirs);

        if( root != null ){

            // Add new icons for the tree view
            Jtv.setLookAndFeel();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Jtv jtv = new Jtv();
                    jtv.addTree(root);
                    jtv.createAndShowGUI();
                }
            });
        }

    }

}
