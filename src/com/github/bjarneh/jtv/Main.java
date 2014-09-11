package com.github.bjarneh.jtv;

import java.io.File;
import javax.swing.SwingUtilities;
import java.util.ArrayList;


public class Main {


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
                // log a warning
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

        final JtvTreeNode root = buildTree(dirs); //getTree(new File(dir));

        // This adds new icons for the tree view
        Jtv.setLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Jtv jtv = new Jtv();
                jtv.addTree(root);
                jtv.createAndShowGUI();
            }
        });
    }

///         DefaultMutableTreeNode top = 
///             ///  getTree(new File("/home/bjarne/mercurial/mine/cop/src"));
///             ///  getTree(new File("/home/bjarne/local/yacy/source"));
///             getTree(new File("src"));

}
