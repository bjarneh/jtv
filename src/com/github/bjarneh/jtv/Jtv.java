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

    private static int initWidth  = 280; 
    private static int initHeight = 700; 


    private DefaultMutableTreeNode current;


    private static final Logger log =
        Logger.getLogger( Jtv.class.getName() );


    // Perhaps that getModifiers stuff should be used instead?
    boolean CTRL_IS_DOWN = false;


    public Jtv() {
        super(new GridLayout(1,0));
        addTree();
    }


    //TODO FIXME
    private void addTree(){

        DefaultMutableTreeNode top = 
///             getTree(new File("/home/bjarne/mercurial/mine/cop/src"));
            getTree(new File("src"));

        // Create a tree with single selection.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        // Display some better looking icons
        tree.setCellRenderer(new JtvTreeCellRenderer());

        // Add listeners
        tree.addTreeSelectionListener(markListener);
        tree.addMouseListener(mouseListener);
        tree.addKeyListener(keyListener);


        // Create the scroll pane and add the tree to it. 
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(initWidth, initHeight));

        add(scrollPane);

    }


    private JtvTreeNode getTree(File file){
        JtvTreeNode node = new JtvTreeNode(file);
        if( file.isDirectory() ){
            for(File f: file.listFiles()){
                node.add( getTree( f ));
            }
        }
        return node;
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


    private static void setLookAndFeel(){

        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
///             ///   UIManager.getSystemLookAndFeelClassName());

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


        
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {

        setLookAndFeel();

        // Create and set up the window.
        JFrame frame = new JFrame("jtv");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add content to the window.
        frame.add(new Jtv());

        // Display the window.
        frame.pack();
        frame.setVisible(true);

        topFrame = frame;

    }


    private void getBigger(){
        int width  = topFrame.getWidth();
        int height = topFrame.getHeight();
        topFrame.setSize( width + dw, height + dh );
    }
    

    private void getSmaller(){
        int width  = topFrame.getWidth();
        int height = topFrame.getHeight();
        if( height > 100 ) { height -= dh; }
        if( width > 100 ){ width -= dw; }
        topFrame.setSize( width, height );
    }


    public static void main(String[] args) {
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }


    // Listen to mouse events
    private final MouseListener mouseListener = new MouseAdapter() {

        public void mousePressed(MouseEvent e) {

            int selRow       = tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

            //System.out.printf(" selPath: %s\n", selPath);
            if( selRow != -1 && e.getClickCount() == 2 ) {
                System.out.printf(" open[m]: %s\n",
                    (DefaultMutableTreeNode) selPath.getLastPathComponent());
            }
        }
    };


    // Listen to events to track selected tree view element
    final TreeSelectionListener markListener = new TreeSelectionListener() {

        public void valueChanged(TreeSelectionEvent e) {

            current = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();

            /*
            if (current != null){
                Object nodeObj = current.getUserObject();
                System.out.printf(" what: %s (%s)\n", current, nodeObj);
            }
            */
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
                    System.out.printf(" open[k]: %s\n", current);
                }else{
                    //TreePath path = tree.getSelectionPath();
                    if( tree.isCollapsed( tree.getSelectionPath() ) ){
                        expandAll( current );
                    }else{
                        collapseAll( current );
                        //tree.collapsePath( path );
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
