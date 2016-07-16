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
import java.awt.Font;
import java.awt.Container;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.event.ChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// libb
import com.github.bjarneh.utilz.Tuple;


/**
 * JTextField that listens to a few commands.
 * 
 * @author bjarneh@ifi.uio.no
 */
public class JtvTextField extends JTextField {

    static final long serialVersionUID = 0;

    private static final Logger log =
            Logger.getLogger(JtvTextField.class.getName());

    // Commands
    private static Pattern fontCmd =
        Pattern.compile("^f(ont)?(:|\\s*)(.+)$");
    private static Pattern grepCmd =
        Pattern.compile("^g(rep)?(:|\\s*)(.+)$");
    private static Pattern pathCmd =
        Pattern.compile("^p(ath)?(:|\\s*)(.+)$");

    private static Timer flashTimer = null;

    // Store caret position which is erased during repaint etc.
    private static int caretPos = -1;

    // OP code for different commands
    public static final int NOOP = 0;
    public static final int FONT_SELECT = 1;
    public static final int FILE_GREP = 2;
    public static final int PATH_GREP = 3;


    public JtvTextField(){
        super();
    }

    public JtvTextField(String s){
        super(s);
    }


    public void storeCaretPos(){
        caretPos = getCaretPosition();
    }

    public void restoreCaretPos(){
        String tmp = getText();
        if( tmp != null && tmp.length() <= caretPos ){
            setCaretPosition( caretPos );
        }
    }


    public Tuple<Integer, String> getCommand(){
        String cmd = getText();
        if( cmd != null ){
            Matcher m = fontCmd.matcher( cmd );
            if( m.matches() ){
                return new Tuple<Integer,String>(FONT_SELECT, m.group(3));
            }
            m = grepCmd.matcher( cmd );
            if( m.matches() ){
                return new Tuple<Integer,String>(FILE_GREP, m.group(3));
            }
            m = pathCmd.matcher( cmd );
            if( m.matches() ){
                return new Tuple<Integer,String>(PATH_GREP, m.group(3));
            }
        }
        return new Tuple<Integer,String>(0, "");
    }



    // Somewhat modified:
    // http://stackoverflow.com/a/11130842
    public void flashField(final Color flashFgColor, 
            final Color flashBgColor, int timerDelay, int totalTime)
    {
        final int totalCount = totalTime / timerDelay;

        final Color origBg = getBackground();
        final Color origFg = getForeground();

        if(flashTimer != null && flashTimer.isRunning()){
            return;
        }

        flashTimer = new Timer(timerDelay, new ActionListener(){
            int count = 0;
            public void actionPerformed(ActionEvent evt) {
                if (count % 2 == 0) {
                    setBackground(flashBgColor);
                    setForeground(flashFgColor);
                } else {
                    setBackground(origBg);
                    setForeground(origFg);
                    if (count >= totalCount) { 
                        ((Timer) evt.getSource()).stop();
                    }
                }
                count++;
            }
        });

        flashTimer.start();
    }


}
