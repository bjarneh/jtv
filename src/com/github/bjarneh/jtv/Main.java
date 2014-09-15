// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.io.File;
import javax.swing.SwingUtilities;
import java.util.HashMap;
import java.util.ArrayList;
import javax.swing.UIManager;

// libb
import com.github.bjarneh.parse.options.Getopt;

/**
 * Entry point for the application.
 *
 * @author bjarneh@ifi.uio.no
 */

public class Main {


    static String help =

        " jtv - java tree view                    \n"+
        "                                         \n"+
        " usage: jtv [OPTIONS] [DIR]              \n"+
        "                                         \n"+
        " options:                                \n"+
        "                                         \n"+
        "  -h --help  : print this menu and exit  \n"+
        "  -l --list  : list available themes     \n"+
        "  -t --theme : set alternative theme     \n"+
        "  -b --bruce : use a bruce lee logo      \n";


    static String[] dirs = {"src"};
    static String theme  = Jtv.regularStyle;
    static boolean bruce = false;


    public static void main(String[] args) {


        Getopt getopt = new Getopt();
        getopt.addBoolOption("-h -help --help");
        getopt.addBoolOption("-l -list --list");
        getopt.addBoolOption("-b -bruce --bruce");
        getopt.addFancyStrOption("-t --theme");


        String[] rest = getopt.parse(args);


        if( getopt.isSet("-help") ){
            System.out.printf("\n%s\n", help);
            System.exit(0);
        }

        if( getopt.isSet("-list") ){
            listLookAndFeel();
            System.exit(0);
        }

        bruce = getopt.isSet("-bruce");

        if( getopt.isSet("-theme") ){
            updateTheme( getopt.get("-theme") );
        }

        //System.out.printf(" %s\n", getopt);

        if( rest.length > 0 ){
            dirs = rest;
        }


        final JtvTreeNode root = Jtv.buildTree(dirs);


        if( root != null ){

            // We can do some basic styling
            Jtv.setLookAndFeel( theme );

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Jtv jtv = new Jtv();
                    jtv.addTree(root);
                    jtv.createAndShowGUI( bruce );
                }
            });
        }
    }


    private static void listLookAndFeel(){

        UIManager.LookAndFeelInfo[] looks =
            UIManager.getInstalledLookAndFeels();

        for(UIManager.LookAndFeelInfo look : looks) {
            System.out.printf("%10s : %s\n",
                    look.getName(),look.getClassName());
        }

    }


    private static void updateTheme(String val){

        HashMap<String,String> themes = new HashMap<String,String>();

        UIManager.LookAndFeelInfo[] looks =
            UIManager.getInstalledLookAndFeels();

        for(UIManager.LookAndFeelInfo look : looks) {
            themes.put( look.getName(), look.getClassName() );
            themes.put( look.getClassName(), look.getClassName() );
        }

        if( !themes.containsKey( val ) ){
            System.err.printf("[ERROR] unknown theme: %s\n", val);
            System.exit(1);
        }

        theme = themes.get( val );
    }

    

}
