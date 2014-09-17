
package com.github.bjarneh.jtv;

import java.io.File;
import java.io.FileFilter;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class JtvFileFilter implements FileFilter {

    // Matches nothing..
    static Pattern pattern = Pattern.compile("\\b\\B");
            //"^(.+\\.[86o]$|.+\\.class|.+\\.pyc|\\..*)$"


    public static void setFilter(String s){
        pattern = Pattern.compile( s );
    }


    @Override
    public boolean accept(File file){
        Matcher m = pattern.matcher( file.getName() );
        return !m.matches();
    }

}
