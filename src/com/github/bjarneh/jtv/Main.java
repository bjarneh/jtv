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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

// libb
import com.github.bjarneh.parse.options.Getopt;
import com.github.bjarneh.utilz.io;

/**
 * Entry point for the application.
 *
 * @author bjarneh@ifi.uio.no
 */

public class Main {


    private static final Logger log =
        Logger.getLogger(Main.class.getName());


    static String helpMenu =

        " jtv - java tree view                     \n"+
        "                                          \n"+
        " usage: jtv [OPTIONS] [DIR]               \n"+
        "                                          \n"+
        " options:                                 \n"+
        "                                          \n"+
        "  -h --help  :  print this menu and exit  \n"+
        "  -l --list  :  list themes and fonts     \n"+
        "  -s --stil  :  set alternative theme     \n"+
        "  -f --font  :  set alternative font      \n"+
        "  -z --size  :  set alternative font size \n"+
        "  -x --save  :  ignore saved state        \n"+
        "  -d --drop  :  exclude file/dirs [regex] \n"+
        "  -m --mark  :  mark color: rgb(0,255,0)  \n"+
        "  -n --noxt  :  don't open via xterm      \n"+
        "  -g --geom  :  geometry wxh+x+y          \n"+
        "  -o --open  :  open with [default: vim]  \n";

    static Pattern whxyPattern =
        Pattern.compile("^(\\d+)x(\\d+)\\+(\\d+)\\+(\\d+)$");

    static String[] dirs = {"src"};
    static String theme  = Jtv.regularStyle;
    static boolean bruce = false;
    static boolean help  = false;
    static boolean list  = false;
    static boolean noxt  = false;
    static boolean font  = false; // mark true if user want's a new font
    static boolean save  = true;
    static int launchX   = -1;
    static int launchY   = -1;
    static Getopt getopt = initParser();


    static Getopt initParser(){

        Getopt getopt = new Getopt();

        getopt.addBoolOption("-h -help --help");
        getopt.addBoolOption("-l -list --list");
        getopt.addBoolOption("-n -noxt --noxt");
        getopt.addBoolOption("-x -save --save");
        getopt.addBoolOption("-b -bruce --bruce"); // hidden :-)
        getopt.addFancyStrOption("-s --stil");
        getopt.addFancyStrOption("-d --drop");
        getopt.addFancyStrOption("-f --font");
        getopt.addFancyStrOption("-z --size");
        getopt.addFancyStrOption("-o --open");
        getopt.addFancyStrOption("-m --mark");
        getopt.addFancyStrOption("-g --geom");

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

        if( getopt.isSet("-save") ){
            save = false;
        }

        if( getopt.isSet("-size") ){
            JtvTreeCellRenderer.fontSize = getopt.getInt("-size");
        }

        if( getopt.isSet("-mark") ){
            JtvTreeCellRenderer.setColor( getopt.get("-mark") );
        }

        if( getopt.isSet("-font") ){
            JtvTreeCellRenderer.fontName = getopt.get("-font");
            font = true;
        }

        if( getopt.isSet("-open") ){
            JtvCmd.setOpener( getopt.get("-open") );
        }

        if( getopt.isSet("-drop") ){
            JtvFileFilter.setFilter( getopt.get("-drop") );
        }

        if( getopt.isSet("-bruce") ){
            bruce = true;
        }

        if( getopt.isSet("-stil") ){
            theme = updateTheme( getopt.get("-stil") );
        }

        if( getopt.isSet("-geom") ){
            String whxy = getopt.get("-geom");
            Matcher m = whxyPattern.matcher(whxy);
            if(! m.matches() ){
                System.err.printf("--geom wrong format: %s\n", whxy);
                System.exit(1);
            }else{
                Jtv.initWidth = Integer.parseInt(m.group(1));
                Jtv.initHeight = Integer.parseInt(m.group(2));
                launchX = Integer.parseInt(m.group(3));
                launchY = Integer.parseInt(m.group(4));
            }
        }

        getopt.reset();

        return rest;
    }


    public static void main(String[] args) {

        // Parse configuration first
        parseConfig();
        // Command line overrides it..
        String[] rest = parseArgs( args );

        if ( help ) {
            System.out.printf("\n%s\n", helpMenu);
            System.exit(0);
        }

        if( font || list ){
            // Register some alternative fonts
            JtvTreeCellRenderer.registerFonts();
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
                    if( save ){
                        jtv.loadState();
                    }
                    jtv.createAndShowGUI( bruce, save, launchX, launchY );
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

        for( Font f: JtvTreeCellRenderer.allFonts() ){
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

    
    private static void parseConfig(){

        File stub;
        String[] args;

        File pwd  = new File( System.getProperty("user.dir") );
        File home = new File( System.getProperty("user.home") );

        ArrayList<File> conf = new ArrayList<File>();
        // $HOME/.jtvrc
        conf.add( new File( home, ".jtvrc" ) );
        // XDG_CONFIG_HOME/jtv/jtvrc || $HOME/.config/jtv/jtvrc
        String xdg = System.getenv("XDG_CONFIG_HOME");
        if( xdg != null ){
            stub = new File(xdg);
        }else{
            stub = new File(home, ".config");
        }
        conf.add( new File(new File(stub,"jtv"), "jtvrc") );
        // $PWD/.jtvrc
        conf.add( new File( pwd, ".jtvrc" ) );
        for(File c: conf){
            if( c.isFile() ){
                System.out.printf(" reading: %s\n", c);
                args = configToArgs(c);
                if( args != null && args.length > 0 ){
                    parseArgs( args );
                }
            }
        }
    }


    // Utility function which should be moved
    private static String[] configToArgs(File f){
        try{
            String content = new String(io.raw(f));
            content = content.replaceAll("(^#[^\\n]*|\n#[^\\n]*)"," ");
            return content.trim().split("\\s+");
        }catch(IOException e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

}
