// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved. 
// Use of this source code is governed by a BSD-style 
// license that can be found in the LICENSE file. 

package com.github.bjarneh.jtv;

// std
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.ImageIcon;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;


// local
import com.github.bjarneh.utilz.res;


/**
 * Trying out some Java GUI (tree-view).
 * 
 * @author bjarneh@ifi.uio.no
 */

public class Jtv extends JPanel {


    private static JTree tree;
    private static JFrame topFrame;

    private static int dw = 20; // in[de]crease in width on ctrl+[-]
    private static int dh = 30; // in[de]crease in height on ctrl+[-]

    private static int minWidth   = 170; // decrease cannot go past this
    private static int minHeight  = 400; // decrease cannot go past this

    private static int initWidth  = 280; 
    private static int initHeight = 700; 


    private DefaultMutableTreeNode current;


    private static final Logger log =
        Logger.getLogger( Jtv.class.getName() );


    // Perhaps that getModifiers stuff should be used instead?
    boolean CTRL_IS_DOWN = false;


    public Jtv() {
        super(new GridLayout(1,0));
    }


    public void addTree(DefaultMutableTreeNode root){

        tree = new JTree(root);
        //tree.setRootVisible(false); set this if more than one dir is given
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        // Display some better looking icons
        tree.setCellRenderer(new JtvTreeCellRenderer());

        // Add listeners, perhaps the markListener can be dropped
        tree.addTreeSelectionListener(markListener);
        tree.addMouseListener(mouseListener);
        tree.addKeyListener(keyListener);


        // Create the scroll pane and add the tree to it. 
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(initWidth, initHeight));

        add(scrollPane);
    }


    private void expandAll(DefaultMutableTreeNode path){

        if( path == null ){
            return;
        }

        int minLevel = path.getLevel();
        DefaultMutableTreeNode node = path.getNextNode();

        while( node != null && node.getLevel() > minLevel ) {
            if( !node.isLeaf() ){
                tree.expandPath(new TreePath(node.getPath()));
            }
            node = node.getNextNode();
        }

        tree.expandPath(new TreePath(path.getPath()));
    }


    private void collapseAll(DefaultMutableTreeNode path){

        if( path == null ){
            return;
        }

        int minLevel = path.getLevel();
        DefaultMutableTreeNode node = path.getNextNode();

        while( node != null && node.getLevel() > minLevel ) {
            if( !node.isLeaf() ){
                tree.collapsePath(new TreePath(node.getPath()));
            }
            node = node.getNextNode();
        }

        tree.collapsePath(new TreePath(path.getPath()));
    }


    private void toggleMaximize(){
        int extendedState = topFrame.getExtendedState();
        if( (extendedState & JFrame.MAXIMIZED_BOTH) > 0 ){
            topFrame.setExtendedState( JFrame.NORMAL );
        }else{
            topFrame.setExtendedState( JFrame.MAXIMIZED_BOTH );
        }
    }


    private void getBigger(){
        int width  = topFrame.getWidth();
        int height = topFrame.getHeight();
        topFrame.setSize( width + dw, height + dh );
    }
    

    private void getSmaller(){
        int width  = topFrame.getWidth();
        int height = topFrame.getHeight();
        if( height > minHeight ) { height -= dh; }
        if( width > minWidth ){ width -= dw; }
        topFrame.setSize( width, height );
    }


    private void resetSize(){
        topFrame.setSize( initWidth, initHeight );
    }


    private void resetPosition(){
        topFrame.setLocation( 0, 0 );
    }

    
    private void openFile( DefaultMutableTreeNode node ){

        String cmd = 
            String.format("xterm -geometry 80x35 -bw 0 -e  vim %s",
                node.getUserObject());
        try{

            JtvCmd.run( cmd );

        }catch(Exception e){
            log.log(Level.SEVERE, e.getMessage(), e); 
        }

    }


    public static void setLookAndFeel(){

        try {

            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
///                     UIManager.getSystemLookAndFeelClassName());
///             MetalLookAndFeel.setCurrentTheme(new OceanTheme());
///             UIManager.setLookAndFeel(new MetalLookAndFeel());
            UIManager.put("Tree.collapsedIcon",
                    res.get().getIcon("img/collapsed.png"));
            UIManager.put("Tree.expandedIcon",
                    res.get().getIcon("img/expanded.png"));

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e); 
        }

    }


    public void createAndShowGUI() {

        // Create and set up the window.
        JFrame frame = new JFrame("jtv");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(this);

        frame.pack();
        frame.setVisible(true);

        topFrame = frame;

    }


    // Listen to mouse events
    private final MouseListener mouseListener = new MouseAdapter() {

        public void mousePressed(MouseEvent e) {

            int selRow       = tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

            if( selRow != -1 && e.getClickCount() == 2 ) {
                DefaultMutableTreeNode node = 
                    (DefaultMutableTreeNode) selPath.getLastPathComponent();
                if( node.isLeaf() ){
                    e.consume();
                    openFile( node );
                }
            }
        }
    };


    // Listen to events to track selected tree view element
    final TreeSelectionListener markListener = new TreeSelectionListener() {

        public void valueChanged(TreeSelectionEvent e) {

            current = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
        }
    };


    // Listen to events to track selected tree view element
    final KeyListener keyListener = new KeyAdapter() {


        void holdsCtrl(boolean value){
            CTRL_IS_DOWN = value;
        }


        void enterPressed(KeyEvent e){
            if( current != null ) {
                if( current.isLeaf() ) {
                    openFile( current );
                }else{
                    if( tree.isCollapsed( tree.getSelectionPath() ) ){
                        expandAll( current );
                    }else{
                        collapseAll( current );
                    }
                }
                e.consume();
            }
        }


        void perhapsMaximize(){
            if( CTRL_IS_DOWN ){
                toggleMaximize();
            }
        }


        void perhapsNormalize(){
            if( CTRL_IS_DOWN ){
                resetSize();
            }
        }


        void perhapsResetYX(){
            if( CTRL_IS_DOWN ){
                resetPosition();
            }
        }


        void quit(boolean sure){

            if( CTRL_IS_DOWN ){

                if( !sure ){
                    int reply = JOptionPane.showConfirmDialog(topFrame,
                            "Are you sure you want to quit?", "Quit?",
                            JOptionPane.YES_NO_OPTION);
                    if( reply != JOptionPane.YES_OPTION ){
                        return;
                    }
                }

                System.exit(0);
            }
        }


        public void keyPressed(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
                case KeyEvent.VK_CONTROL: holdsCtrl(true); break;
                case KeyEvent.VK_ENTER  : enterPressed(e); break;
                case KeyEvent.VK_Q      :
                case KeyEvent.VK_W      : quit(true); break;
                case KeyEvent.VK_C      : quit(false); break;
                case KeyEvent.VK_PLUS   : getBigger(); break;
                case KeyEvent.VK_MINUS  : getSmaller(); break;
                case KeyEvent.VK_M      : perhapsMaximize(); break;
                case KeyEvent.VK_N      : perhapsNormalize(); break;
                case KeyEvent.VK_0      : perhapsResetYX(); break;
            }

        }


        public void keyReleased(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
                case KeyEvent.VK_CONTROL: holdsCtrl(false); break;
            }
        }

    };

}
