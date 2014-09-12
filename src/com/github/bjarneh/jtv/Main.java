// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.io.File;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import javax.swing.UIManager;


/**
 * Entry point for the application.
 *
 * @author bjarneh@ifi.uio.no
 */

public class Main {


    static String help =

        " jtv - java tree view                   \n"+
        "                                        \n"+
        " usage: jtv [OPTIONS] [DIR]             \n"+
        "                                        \n"+
        " options:                               \n"+
        "                                        \n"+
        "  -h --help : print this menu and exit  \n"+
        "  -l --list : list available themes     \n"+
        "                                        \n"+
        "                                        \n";


    static String[] dirs = {"src"};




    public static void main(String[] args) {

        if( args.length > 0 ){
            dirs = args;
        }

        final JtvTreeNode root = Jtv.buildTree(dirs);

        if( root != null ){

            // Add new icons for the tree view
            Jtv.setLookAndFeel(Jtv.regularStyle);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Jtv jtv = new Jtv();
                    jtv.addTree(root);
                    jtv.createAndShowGUI();
                }
            });
        }
    }


    public static void listLookAndFeel(){

        UIManager.LookAndFeelInfo[] looks =
            UIManager.getInstalledLookAndFeels();

        for(UIManager.LookAndFeelInfo look : looks) {
            System.out.printf("%10s : %s\n",
                    look.getName(),look.getClassName());
        }

    }

}
