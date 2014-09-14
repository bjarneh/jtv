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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

// libb
import com.github.bjarneh.utilz.res;


/**
 * The tree-view and listeners are placed in this file.
 * 
 * @author bjarneh@ifi.uio.no
 */

public class Jtv extends JPanel {

    private JTree tree;
    private JFrame topFrame;

    public static int dw = 25; // in[de]crease in width on ctrl+[-]
    public static int dh = 30; // in[de]crease in height on ctrl+[-]

    public static int minWidth   = 170; // decrease cannot go past this
    public static int minHeight  = 400; // decrease cannot go past this

    public static int initWidth  = 280; 
    public static int initHeight = 700; 


    public static final String regularStyle = 
        UIManager.getCrossPlatformLookAndFeelClassName();

    private DefaultMutableTreeNode current;


    private static final Logger log = Logger.getLogger(Jtv.class.getName());


    // Perhaps that getModifiers stuff should be used instead?
    boolean CTRL_IS_DOWN = false;


    public Jtv() {
        super(new GridLayout(1,0));
    }


    public void addTree(DefaultMutableTreeNode root){

        tree = new JTree(root);

        // More than one dir is given
        if( root.getUserObject() == null ){
           tree.setRootVisible(false);
        }
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();

        // Display some better looking icons for Metal
        if( lookAndFeel != null &&
            lookAndFeel.getClass().getName().equals( regularStyle ) )
        {
            tree.setCellRenderer(new JtvTreeCellRenderer());
        }

        // Add listeners, perhaps the markListener can be dropped
        tree.addTreeSelectionListener(markListener);
        tree.addMouseListener(mouseListener);
        tree.addKeyListener(keyListener);

        // Create the scroll pane and add the tree to it. 
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(initWidth, initHeight));

        add(scrollPane);
    }


    private static JtvTreeNode getTree(File file){
        JtvTreeNode node = new JtvTreeNode(file);
        if( file.isDirectory() ){
            for(File f: file.listFiles()){
                node.add( getTree( f ));
            }
        }
        return node;
    }


    public static JtvTreeNode buildTree(String ... args){

        File tmp;
        ArrayList<File> dirs = new ArrayList<File>();

        for(String a: args){

            tmp = new File(a);

            if( tmp.isDirectory() ){
                dirs.add( tmp );
            }else{
                log.info("This is not a directory '"+ a +"'");
            }
        }

        if( dirs.size() == 1 ){

            return getTree( dirs.get(0) );

        } else if ( dirs.size() > 1 ) {

            JtvTreeNode dummy = new JtvTreeNode();

            for(File f: dirs){
                dummy.add( getTree( f ) );
            }

            return dummy;

        } else {

            return null;

        }
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
        if( (topFrame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0 ){
            int width  = topFrame.getWidth();
            int height = topFrame.getHeight();
            topFrame.setSize( width + dw, height + dh );
        }
    }
    

    private void getSmaller(){
        int width  = topFrame.getWidth();
        int height = topFrame.getHeight();
        if( height > minHeight ) { height -= dh; }
        if( width > minWidth ){ width -= dw; }
        topFrame.setSize( width, height );
    }


    private void resetSize(){
        topFrame.setExtendedState( JFrame.NORMAL );
        topFrame.setSize( initWidth, initHeight );
    }


    private void resetPosition(){
        topFrame.setLocation( 0, 0 );
    }

    
    private void openFile( DefaultMutableTreeNode node ){

        File file = (File) node.getUserObject();

        if( file.isDirectory() ){
            return;
        }

        // Path's have changed, notify user.
        if( !file.isFile() ){
            JOptionPane.showMessageDialog(topFrame,
                    "Not a valid path, refresh [F5]", "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        String cmd = 
            String.format("xterm -geometry 80x35 -bw 0 -e  vim %s",
                node.getUserObject());
        try{

            JtvCmd.run( cmd );

        }catch(Exception e){
            log.log(Level.SEVERE, e.getMessage(), e); 
        }

    }


    public static void setLookAndFeel(String style){

        try {

            UIManager.setLookAndFeel( style );

            if( style.equals( regularStyle ) ){

                UIManager.put("Tree.collapsedIcon",
                        res.get().icon("img/collapsed.png"));
                UIManager.put("Tree.expandedIcon",
                        res.get().icon("img/expanded.png"));
                UIManager.put("Tree.closedIcon",
                        res.get().icon("img/dir_close.png"));

            }

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e); 
        }

    }


    public void createAndShowGUI() {

        // Create and set up the window.
        JFrame frame = new JFrame("jtv");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(res.get().icon("img/ninja.png").getImage());

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


    // Listen to a few key events
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


        void perhapsMaximize(KeyEvent e){
            if( CTRL_IS_DOWN ){
                toggleMaximize();
                e.consume();
            }
        }


        void perhapsNormalize(KeyEvent e){
            if( CTRL_IS_DOWN ){
                resetSize();
                e.consume();
            }
        }


        void perhapsResetYX(KeyEvent e){
            if( CTRL_IS_DOWN ){
                resetPosition();
                e.consume();
            }
        }


        void perhapsBigger(KeyEvent e){
            if( CTRL_IS_DOWN ){
                getBigger();
                e.consume();
            }
        }


        void perhapsSmaller(KeyEvent e){
            if( CTRL_IS_DOWN ){
                getSmaller();
                e.consume();
            }
        }


        void perhapsTerm(KeyEvent e){
            if( CTRL_IS_DOWN ){
                try{
                    JtvCmd.run("xterm");
                }catch(Exception ex){
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                }
                e.consume();
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


        void refreshTree(KeyEvent e){
            e.consume();
            System.out.printf(" refresh\n");
        }




        public void keyPressed(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
                case KeyEvent.VK_CONTROL: holdsCtrl(true); break;
                case KeyEvent.VK_ENTER  : enterPressed(e); break;
                case KeyEvent.VK_Q      :
                case KeyEvent.VK_W      : quit(true); break;
                case KeyEvent.VK_C      : quit(false); break;
                case KeyEvent.VK_PLUS   : perhapsBigger(e); break;
                case KeyEvent.VK_MINUS  : perhapsSmaller(e); break;
                case KeyEvent.VK_M      : perhapsMaximize(e); break;
                case KeyEvent.VK_N      : perhapsNormalize(e); break;
                case KeyEvent.VK_0      : perhapsResetYX(e); break;
                case KeyEvent.VK_X      : perhapsTerm(e); break;
                case KeyEvent.VK_F5     : refreshTree(e); break;
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
