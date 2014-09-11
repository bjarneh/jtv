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

    // env == null will inherit environment from our own process
    public static Process run(String cmd) throws IOException {
        return rt.exec( cmd.split("\\s+"), null, curdir );
    }

    // env == null will inherit environment from our own process
    public static Process run(String[] cmd) throws IOException {
        return rt.exec( cmd, null, curdir );
    }

}
