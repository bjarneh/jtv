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
import java.io.IOException;
import java.util.ArrayList;


/**
 * Execute external commands.
 * 
 * @author bjarneh@ifi.uio.no
 */

public class JtvCmd {

    static final long serialVersionUID = 0;

    //// Xterm (or alternative term) + arguments for it
    // private static String[] targs = null;
    //// Executable + arguments for it
    // private static String[] xargs = null;

    // Defaults
    private static String[] args = {
        "xterm",
        "-geometry","80x35",
        "-bw","0",
        "-e",
        "vim",
        null        // file name
    };

    // Windows arguments [Opens Vim in CMD terminal]
    private static String[] winArgs = {
        "CMD",
        "/C",
        "START",
        "CMD",
        "/C",
        "vim",
        null        // file name
    };


    //TODO FIXME add ability to edit modify command line arguments
    //for both terminal and executable.
    static {
        if( System.getProperty("os.name").matches("^[Ww]indows.*$") ){
            args = winArgs;
        }
    }


    public static void setOpener(String program){
        args[args.length - 2] = program;
    }


    public static void noXterm(){
        if( args.length == 8 ){
            args = new String[]{ args[6], args[7] };
        }
    }


    public static synchronized Process open(File file) throws IOException {
        args[args.length - 1] = file.toString(); // relative path
        return new ProcessBuilder(args).start();
    }


    public static Process run(String[] cmd) throws IOException {
        return new ProcessBuilder(cmd).start();
    }

}
