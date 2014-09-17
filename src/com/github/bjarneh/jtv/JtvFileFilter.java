// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Filters out files directories which should not
 * be displayed in the tree-view.
 *
 * @author bjarneh@ifi.uio.no
 */

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
