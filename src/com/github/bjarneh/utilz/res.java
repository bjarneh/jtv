// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.utilz;

import java.net.URL;
import java.io.IOException;
import javax.swing.ImageIcon;

/**
 * Get resources from your zip/jar file.
 *
 * @author bjarneh@ifi.uio.no
 */

public class res {

    private static res single;

    public static res get(){
        if( single == null ){
            single = new res();
        }
        return single;
    }


    public URL getUrl(String ref){
        return this.getClass().getClassLoader().getResource(ref);
    }

    public ImageIcon getIcon(String ref){
        return new ImageIcon(getUrl(ref));
    }
}
