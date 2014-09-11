package com.github.bjarneh.jtv;

import java.io.File;
import javax.swing.SwingUtilities;


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


    public static void main(String[] args) {

        String dir = "src";

        if( args.length > 0 ){
            dir = args[0];
        }

        final JtvTreeNode root = getTree(new File(dir));

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
