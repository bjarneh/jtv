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


    // Silent 1.8 compiler warning
    @Override
    public int hashCode(){
        return 1;
    }
}
