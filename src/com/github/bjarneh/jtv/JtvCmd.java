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

/**
 * Execute external commands.
 * 
 * @author bjarneh@ifi.uio.no
 */
//TODO FIXME ProcessBuilder
public class JtvCmd {

    static Runtime rt  = Runtime.getRuntime();
    static File curdir = new File( System.getProperty("user.dir") );

    static String[] args = {
        "xterm",
        "-geometry","80x35",
        "-bw","0",
        "-e","vim",
        null        // file name
    };


    public static void setOpener(String program){
        args[args.length - 2] = program;
    }

    public static void noXterm(){
        if( args.length == 8 ){
            args = new String[]{ args[6], args[7] };
        }
    }

    // env == null will inherit environment from our own process
    public static synchronized Process open(File file) throws IOException {
        args[args.length - 1] = file.toString(); // relative path
        return rt.exec( args, null, curdir );
    }

    // env == null will inherit environment from our own process
    public static Process run(String[] cmd) throws IOException {
        return rt.exec( cmd, null, curdir );
    }

}
