// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

// libb
import com.github.bjarneh.parse.options.Getopt;

/**
 * Entry point for the application.
 *
 * @author bjarneh@ifi.uio.no
 */

public class Main {


    static String helpMenu =

        " jtv - java tree view                    \n"+
        "                                         \n"+
        " usage: jtv [OPTIONS] [DIR]              \n"+
        "                                         \n"+
        " options:                                \n"+
        "                                         \n"+
        "  -h --help  : print this menu and exit  \n"+
        "  -l --list  : list themes and fonts     \n"+
        "  -s --stil  : set alternative theme     \n"+
        "  -f --font  : set alternative font      \n"+
        "  -z --size  : set alternative font size \n"+
        "  -d --drop  : exclude file/dirs [regex] \n"+
        "  -n --noxt  : don't open via xterm      \n"+
        "  -o --open  : open with [default: vim]  \n";


    static String[] dirs = {"src"};
    static String theme  = Jtv.regularStyle;
    static boolean bruce = false;
    static boolean help  = false;
    static boolean list  = false;
    static boolean noxt  = false;
    static Getopt getopt = initParser();


    static Getopt initParser(){

        Getopt getopt = new Getopt();

        getopt.addBoolOption("-h -help --help");
        getopt.addBoolOption("-l -list --list");
        getopt.addBoolOption("-n -noxt --noxt");
        getopt.addBoolOption("-b -bruce --bruce"); // hidden :-)
        getopt.addFancyStrOption("-s --stil");
        getopt.addFancyStrOption("-d --drop");
        getopt.addFancyStrOption("-f --font");
        getopt.addFancyStrOption("-z --size");
        getopt.addFancyStrOption("-o --open");

        return getopt;
    }


    static String[] parseArgs(String[] args){

        String[] rest = getopt.parse( args );

        if( getopt.isSet("-help") ){
            help = true;
        }

        if( getopt.isSet("-list") ){
            list = true;
        }

        if( getopt.isSet("-noxt") ){
            JtvCmd.noXterm();
        }

        if( getopt.isSet("-size") ){
            JtvTreeCellRenderer.fontSize = getopt.getInt("-size");
        }

        if( getopt.isSet("-font") ){
            JtvTreeCellRenderer.fontName = getopt.get("-font");
        }

        if( getopt.isSet("-open") ){
            JtvCmd.setOpener( getopt.get("-open") );
        }

        if( getopt.isSet("-drop") ){
            JtvFileFilter.setFilter( getopt.get("-drop") );
        }

        bruce = getopt.isSet("-bruce");

        if( getopt.isSet("-stil") ){
            theme = updateTheme( getopt.get("-stil") );
        }

        return rest;
    }


    public static void main(String[] args) {

        String[] rest = parseArgs( args );

        if ( help ) {
            System.out.printf("\n%s\n", helpMenu);
            System.exit(0);
        }

        if( list ){
            listAvailableFonts();
            listLookAndFeel();
            System.exit(0);
        }

        //System.out.printf(" %s\n", getopt);

        if( rest.length > 0 ){
            dirs = rest;
        }


        final JtvTreeNode root = Jtv.buildTree(dirs);


        if( root != null ){

            // We have our own style, but others can be given
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

        System.out.println("\n[ Look and feel ]\n");

        UIManager.LookAndFeelInfo[] looks =
            UIManager.getInstalledLookAndFeels();

        for(UIManager.LookAndFeelInfo look : looks) {
            System.out.printf("%10s : %s\n",
                    look.getName(),look.getClassName());
        }

        System.out.println();

    }


    private static void listAvailableFonts(){

        System.out.println("\n[ Font names ]\n");

        GraphicsEnvironment g = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        for( Font f: g.getAllFonts() ){
            System.out.printf(" %s\n", f.getFontName());
        }

    }


    private static String updateTheme(String val){

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

        return themes.get( val );
    }

}
