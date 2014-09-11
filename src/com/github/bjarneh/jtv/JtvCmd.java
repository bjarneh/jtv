package com.github.bjarneh.jtv;

import java.io.File;
import java.io.IOException;

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
