// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.github.bjarneh.jtv;

import java.io.File;
import java.util.Comparator;

/**
 * Order files in tree-view alphabetically.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvFileCmp implements Comparator<File> {

    @Override
    public int compare(File f1, File f2){
        String fname1 = f1.getName();
        String fname2 = f2.getName();
        return f1.compareTo( f2 );
    }


    @Override
    public boolean equals(Object o){
        if( o instanceof JtvFileCmp ){
            return true;
        }
        return false;
    }
}
