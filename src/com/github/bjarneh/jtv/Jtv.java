// Copyright 2014 bjarneh@ifi.uio.no. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.github.bjarneh.jtv;

// std
import java.net.URL;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.InputEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
/// import javax.swing.tree.ExpandVetoException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.text.DefaultEditorKit;

// libb
import com.github.bjarneh.utilz.res;


/**
 * The tree-view and listeners are placed in this file.
 * 
 * @author bjarneh@ifi.uio.no
 */

public class Jtv extends JPanel {

    static final long serialVersionUID = 0;

    private JTree tree;
    private JFrame topFrame;

    public static int dw = 25; // in[de]crease in width on ctrl+[-]
    public static int dh = 30; // in[de]crease in height on ctrl+[-]

    public static int minWidth   = 170; // decrease cannot go past this
    public static int minHeight  = 400; // decrease cannot go past this

    public static int initWidth  = 280; 
    public static int initHeight = 700; 

    public static JtvFileCmp cmp = new JtvFileCmp();
    public static JtvFileFilter filter = new JtvFileFilter();


    public static final String regularStyle = 
        "com.github.bjarneh.jtv.JtvLookAndFeel";

    private int currentMark = 0;
    private ArrayList<JtvTreeNode> marks = new ArrayList<JtvTreeNode>();

    private DefaultMutableTreeNode current;

    private static final Logger log = Logger.getLogger(Jtv.class.getName());


    public Jtv() {
        super(new GridLayout(1,0));
    }


    public void addTree(DefaultMutableTreeNode root){

        tree = new JTree(root);

        // More than one dir is given
        if( root.getUserObject() == null ){
            tree.setRootVisible(false);
        }else{
            tree.collapsePath(new TreePath(root.getPath()));
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
            File[] files = file.listFiles( filter );
            Arrays.sort( files, cmp );
            for(File f: files ){
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
                log.info("Directory '"+ a +"' not found");
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


    //TODO FIXME stop tree from collapsing after update
    private void nodeChanged( DefaultMutableTreeNode node ){
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.reload( node );
    }


    private void expandAll(DefaultMutableTreeNode path){

        TreePath tp;

        if( path == null ){
            return;
        }

        int minLevel = path.getLevel();
        DefaultMutableTreeNode node = path.getNextNode();

        while( node != null && node.getLevel() > minLevel ) {
            if( !node.isLeaf() ){
                tp = new TreePath(node.getPath());
                tree.expandPath(tp);
            }
            node = node.getNextNode();
        }

        tp = new TreePath(path.getPath());
        //tree.fireTreeWillExpand(tp);
        tree.expandPath(tp);
        //tree.fireTreeExpanded(tp);
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

        // TODO check if user is actually using vim
        File swp = new File( file.getParentFile(),"."+file.getName()+".swp");
        if( swp.isFile() ){
            JOptionPane.showMessageDialog(topFrame,
                    "File is already open", "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try{

            JtvCmd.open( file );

        }catch(Exception e){
            log.log(Level.SEVERE, e.getMessage(), e); 
        }

    }


    private boolean deleteFile( DefaultMutableTreeNode node ){

        try{
            File file = (File) node.getUserObject();
            file.delete();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.removeNodeFromParent( node );
            return true;
        }catch(Exception e){
            log.log(Level.SEVERE, e.getMessage(), e); 
        }

        return false;
    }


    public static void setLookAndFeel(String style){

        try {

            UIManager.setLookAndFeel( style );

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e); 
        }

    }


    public void createAndShowGUI(boolean useBruce) {

        // Create and set up the window.
        JFrame frame = new JFrame("jtv");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if( useBruce ){
            frame.setIconImage(res.get().icon("img/bruce.png").getImage());
        }else{
            frame.setIconImage(res.get().icon("img/ninja.png").getImage());
        }

        frame.add(this);
        // Let the window manager decide placement
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);

        topFrame = frame;

    }


    public boolean touch( File file ){
        try{
            if( ! file.exists() ){
                new FileOutputStream(file).close();
            }
            file.setLastModified(System.currentTimeMillis());
            return true;
        }catch(Exception e){
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
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

        void handleEnter(KeyEvent e){

            if( current != null ) {
                e.consume();
                if( current.isLeaf() ) {
                    openFile( current );
                }else{
                    if( tree.isCollapsed( tree.getSelectionPath() ) ){
                        expandAll( current );
                    }else{
                        collapseAll( current );
                    }
                }
            }

        }


        void handleMaximize(KeyEvent e, boolean needsCtrl){
            if( !needsCtrl || 
                (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 )
            {
                e.consume();
                toggleMaximize();
            }
        }


        void handleNormalize(KeyEvent e){
            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                resetSize();
            }
        }


        void handleResetYX(KeyEvent e){
            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                resetPosition();
            }
        }


        void handleBigger(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                getBigger();
            }

            if( (e.getModifiers() & KeyEvent.ALT_MASK) != 0 ){

                TreeCellRenderer cellRenderer = tree.getCellRenderer();

                if( cellRenderer instanceof DefaultTreeCellRenderer ){

                    e.consume();

                    DefaultTreeCellRenderer dCellRenderer =
                        (DefaultTreeCellRenderer) cellRenderer;

                    Font font = dCellRenderer.getFont();
                    Font next = new Font(font.getName(),
                            font.getStyle(), font.getSize() + 1);
                    dCellRenderer.setFont( next );

                    //System.out.printf(" font: %s\n", next);

                    // repaint tree with new font
                    ((DefaultTreeModel)tree.getModel()).reload();

                }
            }
        }


        void handleSmaller(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                getSmaller();
            }

            if( (e.getModifiers() & KeyEvent.ALT_MASK) != 0 ){

                TreeCellRenderer cellRenderer = tree.getCellRenderer();

                if( cellRenderer instanceof DefaultTreeCellRenderer ){

                    e.consume();

                    DefaultTreeCellRenderer dCellRenderer =
                        (DefaultTreeCellRenderer) cellRenderer;

                    Font font = dCellRenderer.getFont();

                    if( font.getSize() > 1 ){
                        Font next = new Font(font.getName(),
                                font.getStyle(), font.getSize() - 1);
                        dCellRenderer.setFont( next );
                        // repaint tree with new font
                        ((DefaultTreeModel)tree.getModel()).reload();
                    }
                }
            }
        }


        void handleTerm(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                try{
                    JtvCmd.run(new String[]{"xterm"});
                }catch(Exception ex){
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                }
                e.consume();
            }
        }


        void handleQuit(KeyEvent e, boolean sure){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){

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


        void handleRefresh(KeyEvent e){

            e.consume();

            DefaultTreeModel model   = (DefaultTreeModel) tree.getModel();
            JtvTreeNode child, r2, n = (JtvTreeNode) model.getRoot();

            File file;
            ArrayList<File> files = new ArrayList<File>();

            // multiple roots
            if( n.getUserObject() == null ){

                Enumeration en = n.children();
                while(en.hasMoreElements()){
                    child = (JtvTreeNode) en.nextElement();
                    file  = (File) child.getUserObject();
                    if( file.isDirectory() ){
                        files.add( file );
                    }

                }
                
                String[] roots = new String[ files.size() ];
                int i = 0;
                for(File f: files){
                    roots[i++] = f.getAbsolutePath();
                }

                r2 = buildTree( roots );

            } else {

                File orig = (File) n.getUserObject();
                r2 = buildTree( orig.getAbsolutePath() );
            }

            model.setRoot( r2 );
            marks.clear();

        }


        void handleNewFile(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0  ){
                e.consume();
                if( current != null ){
                    File parent = (File) current.getUserObject();
                    if( parent.isDirectory() ){
                        String fname = 
                            JOptionPane.showInputDialog(
                                    topFrame, 
                                    "Name:",
                                    "Add file",
                                    JOptionPane.INFORMATION_MESSAGE);
                        if( fname != null && !fname.matches("^\\s*$") ){
                            File son = new File( parent, fname );
                            if( touch( son ) ){
                                JtvTreeNode node = new JtvTreeNode( son );
                                current.add( node );
                                nodeChanged( current );
                            }
                        }
                    }
                }
            }
        }


        void handleDeleteFile(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                if( current != null ){
                    File file = (File) current.getUserObject();
                    if( file.isFile() ){
                        e.consume();
                        int reply = JOptionPane.showConfirmDialog(topFrame,
                                "Are you sure?", "Delete: "+file.getName(),
                                JOptionPane.YES_NO_OPTION);
                        if( reply == JOptionPane.YES_OPTION ){
                            deleteFile( current );
                        }
                    }
                }
            }
        }


        void handleFontCycle(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){

                TreeCellRenderer cellRenderer = tree.getCellRenderer();

                if( cellRenderer instanceof JtvTreeCellRenderer ){

                    e.consume();

                    JtvTreeCellRenderer jtvCellRenderer =
                        (JtvTreeCellRenderer) cellRenderer;

                    Font next = jtvCellRenderer.nextFont();
                    jtvCellRenderer.setFont( next );

                    //System.out.printf(" font: %s\n", next);

                    // repaint tree with new font
                    ((DefaultTreeModel)tree.getModel()).reload();

                }
            }

        }


        void handleMark(KeyEvent e, boolean needCtrl){

            if( !needCtrl || (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){

                e.consume();

                JtvTreeNode n = (JtvTreeNode)
                    tree.getLastSelectedPathComponent();

                n.toggleMark();

                if( n.isMarked() ){
                    marks.add( n );
                }else{
                    marks.remove( n );
                }

                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                model.nodeChanged( n );
            }

        }


        void handleRemoveMarks(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                for(JtvTreeNode n: marks){
                    n.toggleMark();
                }
                marks.clear();
            }
        }


        void handleGoto(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){

                e.consume();

                TreePath p;
                JtvTreeNode n;

                if( marks.size() > 0 ){
                    currentMark = (++currentMark) % marks.size();
                    n = marks.get(currentMark);
                    p = new TreePath(n.getPath());
                    tree.setSelectionPath( p );
                    tree.scrollPathToVisible( p );
                }
            }

        }


        public void keyPressed(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
                case KeyEvent.VK_ENTER : handleEnter(e); break;
                case KeyEvent.VK_Q     :
                case KeyEvent.VK_W     : handleQuit(e, true); break;
                case KeyEvent.VK_C     : handleQuit(e, false); break;
                case KeyEvent.VK_PLUS  : handleBigger(e); break;
                case KeyEvent.VK_MINUS : handleSmaller(e); break;
                case KeyEvent.VK_E     : handleFontCycle(e); break;
                case KeyEvent.VK_F11   : handleMaximize(e, false); break;
                case KeyEvent.VK_M     : handleMaximize(e, true); break;
                case KeyEvent.VK_N     : handleNormalize(e); break;
                case KeyEvent.VK_0     : handleResetYX(e); break;
                case KeyEvent.VK_X     : handleTerm(e); break;
                case KeyEvent.VK_A     : handleNewFile(e); break;
                case KeyEvent.VK_D     : handleDeleteFile(e); break;
                case KeyEvent.VK_R     : handleRemoveMarks(e); break;
                case KeyEvent.VK_S     :
                case KeyEvent.VK_K     : handleMark(e, true); break;
                case KeyEvent.VK_SPACE : handleMark(e, false); break;
                case KeyEvent.VK_F     : handleGoto(e); break;
                case KeyEvent.VK_F5    : handleRefresh(e); break;
            }

        }


        // not much here :-)
        public void keyReleased(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
            }
        }

    };

}
