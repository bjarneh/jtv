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

    // Matches nothing: \b\B
    static Pattern pattern = Pattern.compile("^\\..*$");
            //"^(.+\\.[86o]$|.+\\.class|.+\\.pyc|\\..*)$"

    public static void setFilter(String s){
        pattern = Pattern.compile( s );
    }

    @Override
    public boolean accept(File file){
        Matcher m = pattern.matcher( file.getName() );
        //System.out.printf(" p: '%s', m: %s, f: %s\n", pattern, m.matches(), file.getName());
        return !m.matches();
    }

}
