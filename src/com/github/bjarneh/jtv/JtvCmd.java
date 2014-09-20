// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

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
        args = new String[]{ args[6], args[7] };
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
