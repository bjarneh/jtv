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
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// libb
import com.github.bjarneh.utilz.io;


/**
 * Extend DefaultMutableTreeNode in order to override the toString method.
 *
 * @author bjarneh@ifi.uio.no
 */

public class JtvTreeNode extends DefaultMutableTreeNode
    implements Comparable<JtvTreeNode> {

    static final long serialVersionUID = 0;

    private static final Logger log =
            Logger.getLogger(JtvTreeNode.class.getName());

    boolean marked = false;

    public JtvTreeNode(){
        super();
    }

    public JtvTreeNode(Object obj){
        super(obj);
    }

    public JtvTreeNode(Object obj, boolean allowsChildren){
        super(obj, allowsChildren);
    }

    public boolean isMarked(){
        return marked;
    }

    public void toggleMark(){
        marked = !marked;
    }

    // lowerCase startsWith || regex match
    public boolean matches(String pattern){
        File file  = (File) getUserObject();
        String lowerPattern = pattern.toLowerCase();
        try{
            // Illegal regexp can throw Exceptions
            if( file != null && file.getName() != null ){
                if(file.getName().toLowerCase().startsWith(lowerPattern)
                   || file.getName().matches(pattern))
                {
                    return true;
                }
            }
        }catch(Exception e){
            log.log(Level.WARNING, e.getMessage(), e);
        }
        return false;
    }


    public boolean containsPattern(Pattern p){
        try{
            File file = (File) getUserObject();
            if( file != null && file.isFile() ){
                String content = new String(io.raw(file));
                Matcher m = p.matcher( content );
                return m.find();
            }
        }catch(Exception e){
            log.log(Level.WARNING, e.getMessage(), e);
        }
        return false;
    }


    @Override
    public String toString(){

        Object userObj = getUserObject();

        if( userObj == null ){
            return "";
        }
        if( userObj instanceof File ){
            File f = (File) userObj;
            return f.getName(); // looks better with less path :-)
        }
        return super.toString();
    }


    @Override
    public int compareTo(JtvTreeNode other){

        File otherFile = (File) other.getUserObject();
        File myFile    = (File) this.getUserObject();
        if(otherFile == null){ return 1; }
        if(myFile == null){ return -1; }

        return myFile.compareTo(otherFile);
    }


    @Override
    public void setUserObject(Object userObj){
        if( userObj instanceof String ){
            File current = (File) getUserObject();
            File parent  = current.getParentFile();
            try{
                File present = new File(parent, userObj.toString());
                if( current.renameTo( present ) ){
                    userObj = present;
                }else{
                    throw new Exception("Could not rename:'" +
                            current + "', to: '"+ present + "'");
                }
            // SecurityException + the one above
            }catch(Exception e){
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        super.setUserObject(userObj);
    }


    @Override
    public int hashCode(){
        if(this.getUserObject() != null){
            this.getUserObject().hashCode();
        }
        return 0;
    }


    @Override
    public boolean equals(Object other){
        if(other == null){ return false; }
        if(other == this){ return true; }
        if(other instanceof JtvTreeNode){
            JtvTreeNode jtvNode = (JtvTreeNode) other;
            if(this.getUserObject() != null){
                if(jtvNode.getUserObject() != null){
                    return jtvNode.getUserObject().equals(this.getUserObject());
                }
            }
        }
        return false;
    }

}
