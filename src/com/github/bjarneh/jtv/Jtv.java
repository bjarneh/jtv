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
import java.net.URL;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellEditor;
/// import javax.swing.tree.ExpandVetoException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.text.DefaultEditorKit;

// libb
import com.github.bjarneh.utilz.res;
import com.github.bjarneh.utilz.io;
import com.github.bjarneh.utilz.handy;
import com.github.bjarneh.utilz.Tuple;


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

    public static final String STATE_FILE = ".jtv_state";

    public static final String regularStyle = 
        "com.github.bjarneh.jtv.JtvLookAndFeel";

    private int currentMark = 0;
    private ArrayList<JtvTreeNode> marks = new ArrayList<JtvTreeNode>();
    private boolean isHidden = false;

    private JDialog helpMenu;
    private JtvTextField cmdInput;
    private DefaultMutableTreeNode current;

    private static final Logger log = Logger.getLogger(Jtv.class.getName());

    public static final String megaHelp =
        "<html>                                                         "+
        " <head>                                                        "+
        " <style type='text/css'>                                       "+
        " th { text-align:right; }                                      "+
        " td { font-weight:normal; }                                    "+
        " table { font-family: monospace; padding:20px; color:#ffffff;} "+
        " div { border: 1px solid white;}                               "+
        " html { background-color: rgb(64,64,64);}                      "+
        " </style>                                                      "+
        " </head>                                                       "+
        " <div'>                                                        "+
        " <table>                                                       "+
        "  <tr><th>Ctrl+K</th> <td>Toggle help </td></tr>               "+
        "  <tr><th>Ctrl+M</th> Toggle maximize </td></tr>               "+
        "  <tr><th>Ctrl+[+]</th> Make jtv bigger </td></tr>             "+
        "  <tr><th>Ctrl+[-]</th> Make jtv smaller </td></tr>            "+
        "  <tr><th>Ctrl+N</th> Back to default size </td></tr>          "+
        "  <tr><th>Alt+[+]</th> Make jtv font bigger </td></tr>         "+
        "  <tr><th>Alt+[-]</th> Make jtv font smaller </td></tr>        "+
        "  <tr><th>Alt+N</th> Back to default font size </td></tr>      "+
        "  <tr><th>Ctrl+0</th> Move jtv to (0,0) </td></tr>             "+
        "  <tr><th>Return</th> Expand dir or open file </td></tr>       "+
        "  <tr><th>F5</th> Refresh file tree </td></tr>                 "+
        "  <tr><th>Space</th> Toggle mark </td></tr>                    "+
        "  <tr><th>Ctrl+F</th> Goto next mark  </td></tr>               "+
        "  <tr><th>Ctrl+L</th> Remove marks  </td></tr>                 "+
        "  <tr><th>Ctrl+H</th><td>Hide toggle marked files </td></tr>   "+
        "  <tr><th>Ctrl+E</th><td>Cycle font forwards</td></tr>         "+
        "  <tr><th>Ctrl+S</th><td>Cycle font backwards</td></tr>        "+
        "  <tr><th>Ctrl+Y</th><td>Open command line</td></tr>           "+
        "  <tr><th>Ctrl+V</th><td>Store marked file state</td></tr>     "+
        " </table>                                                      "+
        " </div>                                                        "+
        "<html>                                                         ";


    public Jtv() {
        //super(new GridLayout(2,1));
        super(new BorderLayout());
    }


    public void addTree(DefaultMutableTreeNode root){

        tree = new JtvTree(root);

        // More than one dir is given
        if( root.getUserObject() == null ){
            tree.setRootVisible(false);
        }else{
            tree.collapsePath(new TreePath(root.getPath()));
        }

        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        DefaultTreeCellRenderer cellRenderer;

        // Display some better looking icons for Metal
        if( lookAndFeel != null &&
            lookAndFeel.getClass().getName().equals( regularStyle ) )
        {
            cellRenderer = new JtvTreeCellRenderer();
        }else{
            cellRenderer = new DefaultTreeCellRenderer();
        }

        // Add cellRenderer + cellListener to draw correct icons and
        // listen to change events indicating file renaming
        tree.setCellRenderer(cellRenderer);
        DefaultTreeCellEditor editor =
            new DefaultTreeCellEditor(tree,cellRenderer);
        editor.addCellEditorListener( cellListener );
        tree.setCellEditor(editor);

        // Turn on tooltips
        ToolTipManager.sharedInstance().registerComponent(tree);

        // Add listeners, perhaps the markListener can be dropped
        tree.addTreeSelectionListener(markListener);
        tree.addMouseListener(mouseListener);
        tree.addKeyListener(keyListener);
        tree.addKeyListener(comboListener);

        // Create the scroll pane and add the tree to it. 
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setPreferredSize(new Dimension(initWidth, initHeight));

        // Add the actual treeview inside a scroller
        add(scrollPane);

        // Help menu with html JLabel
        helpMenu = new JDialog(topFrame, "help", true);
        helpMenu.setUndecorated(true);

        JLabel p = new JLabel( megaHelp );
        helpMenu.addKeyListener( helpListener );
        helpMenu.add( p );
        helpMenu.setLocationRelativeTo( topFrame );
        //helpMenu.setLocationRelativeTo( null );
        helpMenu.pack();
        helpMenu.setVisible(false);
    }


    public static void setLookAndFeel(String style){

        try {

            UIManager.setLookAndFeel( style );

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e); 
        }

    }


    public void createAndShowGUI(boolean useBruce, boolean isRestored) {

        // Create and set up the window.
        JFrame frame = new JFrame("jtv");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if( useBruce ){
            frame.setIconImage(res.get().icon("img/bruce.png").getImage());
        }else{
            frame.setIconImage(res.get().icon("img/ninja.png").getImage());
        }

        Container pane = frame.getContentPane();
        pane.add(this, BorderLayout.CENTER);

        // Add an invisible font selector SOUTH
        cmdInput = new JtvTextField();
        cmdInput.setBackground(new Color(64,64,64));
        cmdInput.setForeground(Color.WHITE);
        cmdInput.setCaretColor(Color.WHITE);
        cmdInput.setFocusTraversalKeysEnabled(false);// !TAB
        cmdInput.setVisible(false);
        cmdInput.addKeyListener(cmdListener);
        cmdInput.addKeyListener(comboListener);
        pane.add(cmdInput, BorderLayout.SOUTH);

        // Let the window manager decide placement
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);

        topFrame = frame;

        if( isRestored ){
            expandRestored();
        }
    }


    private void expandRestored(){
        if( marks != null && marks.size() > 0 ){
            for(JtvTreeNode n: marks){
                expandTo( n );
            }
        }
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
        String abs;
        ArrayList<File> dirs = new ArrayList<File>();

        for(String a: args){

            tmp = new File(a);
            while( tmp.getAbsolutePath().endsWith(".") ){
                abs = tmp.getAbsolutePath();
                tmp = new File( abs.substring(0, abs.length() -1));
            }

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


    @SuppressWarnings("unchecked")
    private void expandTo(DefaultMutableTreeNode path){
        if( path == null ){
            return;
        }
        tree.expandPath(new TreePath(path.getPath()));
        expandTo( (DefaultMutableTreeNode) path.getParent() );
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


    public void loadState(){
        File f = new File(STATE_FILE);
        if( f.isFile() ){
            try{
                byte[] raw = io.raw(f);
                String[] fnames =
                    new String(io.raw(f), getCharset()).split("\n");
                HashSet<String> shouldBeMarked = new HashSet<String>();
                Collections.addAll( shouldBeMarked, fnames );

                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                JtvTreeNode child, n = (JtvTreeNode) model.getRoot();

                Enumeration<?> en = n.preorderEnumeration();
                while(en.hasMoreElements()){
                    child = (JtvTreeNode) en.nextElement();
                    if( child != null && child.getUserObject() != null ){
                        String fileName = child.getUserObject().toString();
                        if( shouldBeMarked.contains(fileName) ){
                            child.toggleMark();
                            marks.add( child );
                        }
                    }
                }

            }catch(Exception e){
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }


    // TODO: use StandardCharsets.UTF_8 when Java 1.7 can be assumed
    private Charset getCharset(){
        return Charset.defaultCharset();
    }


    // Utility
    private JtvTreeNode nodeFromTreePath(TreePath tp){
        if( tp == null ){
            return null;
        }
        JtvTreeNode selected = (JtvTreeNode) tp.getLastPathComponent();
        return selected;
    }


    private JtvTreeNode currentNode(){
        return nodeFromTreePath(tree.getSelectionPath());
    }


    private boolean gotoPath(String pattern){

        if( pattern == null ){
            return false;
        }

        boolean foundMatch = false;
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        JtvTreeNode child, n   = (JtvTreeNode) model.getRoot();

        File file;
        TreePath p, curr = tree.getSelectionPath();
        JtvTreeNode currNode = null;
        boolean waitForCurrNode = false;
        if( curr != null ){
            currNode = nodeFromTreePath(curr);
            if(currNode != null && currNode.matches(pattern)){
                waitForCurrNode = true;
            }
        }
        Enumeration<?> en = n.preorderEnumeration();
        while(en.hasMoreElements()){
            child = (JtvTreeNode) en.nextElement();
            if( waitForCurrNode ){
                if( currNode.equals(child) ){
                    waitForCurrNode = false;
                }else{
                    continue;
                }
            }
            if( child.matches( pattern ) ) {
                p = new TreePath(child.getPath());
                if( p.equals(curr) ){
                    continue;
                }
                tree.setSelectionPath( p );
                tree.scrollPathToVisible( p );
                foundMatch = true;
                break;
            }
        }

        return foundMatch;
    }


    // Remove highligted files / directiories
    void removeMarks(){
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        for(JtvTreeNode n: marks){
            n.toggleMark();
            model.nodeChanged( n );
        }
        marks.clear();
    }


    // NOTE: this changes/removes earlier highlighted paths
    private boolean grepFiles(String pattern){
        try{
            // Put this first in case of compilation error
            Pattern regx = Pattern.compile(pattern);

            removeMarks();

            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            JtvTreeNode child, n = (JtvTreeNode) model.getRoot();

            File file;
            TreePath tp;
            Enumeration<?> en = n.preorderEnumeration();
            while(en.hasMoreElements()){
                child = (JtvTreeNode) en.nextElement();
                if( child.containsPattern( regx ) ){
                    tp = new TreePath(child.getPath());
                    tree.scrollPathToVisible( tp );
                    marks.add( child );
                    child.toggleMark();
                }
            }
            SwingUtilities.updateComponentTreeUI(tree);
            return true;
        }catch(Exception e){
            log.log(Level.WARNING, e.getMessage(), e);
        }
        return false;
    }


    // Listen to cell edit event
    private final CellEditorListener cellListener = new CellEditorListener() {

        public void editingCanceled(ChangeEvent e){
            tree.setEditable(false);
        }

        public void editingStopped(ChangeEvent e){
            tree.setEditable(false);
        }

    };



    // Hide or show the command input
    private void hideToggleCommand(){
        Container pane = topFrame.getContentPane();
        if( cmdInput.isVisible() ){
            cmdInput.storeCaretPos();
            cmdInput.setVisible(false);
            pane.remove(cmdInput);
        }else{
            cmdInput.setVisible(true);
            pane.add(cmdInput, BorderLayout.SOUTH);
            cmdInput.requestFocus();
        }
        SwingUtilities.updateComponentTreeUI(topFrame);
        cmdInput.restoreCaretPos();
    }



    // Listen to mouse events
    private final MouseListener mouseListener = new MouseAdapter() {

        public void mousePressed(MouseEvent e) {

            int selRow       = tree.getRowForLocation(e.getX(), e.getY());
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

            if( selRow != -1 && e.getClickCount() == 2 ) {
                JtvTreeNode node =
                    (JtvTreeNode) selPath.getLastPathComponent();
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


    // Listen to a few key events [JDialog]
    final KeyListener helpListener = new KeyAdapter() {

        void handleHoverHelp(KeyEvent e){
            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                helpMenu.setVisible( false );
            }
        }

        public void keyPressed(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
                case KeyEvent.VK_K  : handleHoverHelp(e); break;
            }

        }

        // not much here :-)
        @Override
        public void keyReleased(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
            }
        }

    };


    // Listen to a few key events [JTextField]
    final KeyListener cmdListener = new KeyAdapter() {


        void handleExecute(KeyEvent e){

            e.consume();

            Font f;
            Tuple<Integer,String> tup = cmdInput.getCommand();

            switch( tup.getLeft() ){
            case JtvTextField.FONT_SELECT:
                f = JtvTreeCellRenderer.getFussyFont(tup.getRight(),false);
                if( f != null ){
                    cmdInput.storeCaretPos();
                    cmdInput.setText("f:"+f.getName());
                    TreeCellRenderer cellRenderer = tree.getCellRenderer();
                    JtvTreeCellRenderer jtvCellRenderer =
                        (JtvTreeCellRenderer) cellRenderer;
                    jtvCellRenderer.setFont( f );
                    cmdInput.setFont( f );
                    SwingUtilities.updateComponentTreeUI(topFrame);
                    cmdInput.restoreCaretPos();
                }
                break;
            case JtvTextField.PATH_GREP:
                if( ! gotoPath( tup.getRight() ) ){
                    cmdInput.flashField(Color.RED, Color.WHITE, 20, 40);
                }
                break;
            case JtvTextField.FILE_GREP:
                if( ! grepFiles( tup.getRight() ) ){
                    cmdInput.flashField(Color.RED, Color.WHITE, 20, 40);
                }
                break;
            default:
                cmdInput.flashField(Color.RED, Color.WHITE, 20, 40);
                break;
            }
        }


        void handleComplete(KeyEvent e){

            e.consume();

            Font f;
            Tuple<Integer,String> tup = cmdInput.getCommand();

            switch( tup.getLeft() ){
            case JtvTextField.FONT_SELECT:
                f = JtvTreeCellRenderer.getFussyFont(tup.getRight(),true);
                if( f != null ){
                    cmdInput.setText("f:"+f.getName());
                }
                break;
            // No auto-complete for grep or path
            default:
                cmdInput.flashField(Color.BLACK, Color.WHITE, 20, 40);
                break;
            }
        }


        public void keyPressed(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
                case KeyEvent.VK_TAB    : handleComplete(e); break;
                case KeyEvent.VK_ENTER  : handleExecute(e); break;
            }

        }

        // not much here :-)
        @Override
        public void keyReleased(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
            }
        }

    };



    // Listen to a few key events [JTree]
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


        void handleRename(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                if( current != null ){
                    e.consume();
                    tree.setEditable(true);
                    File file = (File) current.getUserObject();
                    tree.startEditingAtPath(new TreePath(current.getPath()));
                }
            }
        }


        void handleMark(KeyEvent e, boolean needCtrl){

            if( !needCtrl || (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){

                e.consume();

                JtvTreeNode n = (JtvTreeNode)
                    tree.getLastSelectedPathComponent();

                if( n == null ){
                    return;
                }

                n.toggleMark();

                if( n.isMarked() ){
                    marks.add( n );
                    Collections.sort( marks );
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
                removeMarks();
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
                case KeyEvent.VK_A     : handleNewFile(e); break;
                case KeyEvent.VK_D     : handleDeleteFile(e); break;
                case KeyEvent.VK_R     : handleRename(e); break;
                case KeyEvent.VK_L     : handleRemoveMarks(e); break;
                case KeyEvent.VK_SPACE : handleMark(e, false); break;
                case KeyEvent.VK_F     : handleGoto(e); break;
            }

        }


        // not much here :-)
        @Override
        public void keyReleased(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
            }
        }

    };


    // Listen to key events for multiple components
    final KeyListener comboListener = new KeyAdapter() {


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
            }else if( (e.getModifiers() & KeyEvent.ALT_MASK) != 0 ){

                TreeCellRenderer cellRenderer = tree.getCellRenderer();

                if( cellRenderer instanceof DefaultTreeCellRenderer ){

                    e.consume();

                    DefaultTreeCellRenderer dCellRenderer =
                        (DefaultTreeCellRenderer) cellRenderer;

                    Font font = dCellRenderer.getFont();
                    Font next = new Font(font.getName(), font.getStyle(), 12);
                    dCellRenderer.setFont( next );
                    cmdInput.storeCaretPos();
                    cmdInput.setFont( next );
                    SwingUtilities.updateComponentTreeUI(topFrame);
                    cmdInput.restoreCaretPos();
                }
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

                if( cellRenderer instanceof JtvTreeCellRenderer ){

                    e.consume();

                    JtvTreeCellRenderer jtvCellRenderer =
                        (JtvTreeCellRenderer) cellRenderer;

                    Font font = jtvCellRenderer.getFont();
                    Font next = new Font(font.getName(),
                            font.getStyle(), font.getSize() + 1);
                    jtvCellRenderer.setFont( next );
                    JtvTreeCellRenderer.currFontSize = font.getSize() + 1;
                    cmdInput.storeCaretPos();
                    cmdInput.setFont( next );
                    SwingUtilities.updateComponentTreeUI(topFrame);
                    cmdInput.restoreCaretPos();
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

                if( cellRenderer instanceof JtvTreeCellRenderer ){

                    e.consume();

                    JtvTreeCellRenderer jtvCellRenderer =
                        (JtvTreeCellRenderer) cellRenderer;

                    Font font = jtvCellRenderer.getFont();

                    if( font.getSize() > 1 ){
                        Font next = new Font(font.getName(),
                                font.getStyle(), font.getSize() - 1);
                        jtvCellRenderer.setFont( next );
                        JtvTreeCellRenderer.currFontSize = font.getSize() -1;
                        cmdInput.storeCaretPos();
                        cmdInput.setFont( next );
                        SwingUtilities.updateComponentTreeUI(topFrame);
                        cmdInput.restoreCaretPos();
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
            TreePath treePath, selection, scrollTo = null;
            ArrayList<File> files = new ArrayList<File>();

            // HACK: The missing equals-method in TreePath makes
            // this pretty strange, although the horrid Java tree-view
            // classes and interfaces will always leave this messy,
            // this is particularly funky where we have to compare
            // with toString on the objects to see if they are equal.
            //
            // Store current location of cursor
            selection = tree.getSelectionPath();

            // Store list of expanded directories to re-expand
            HashSet<File> expanded = new HashSet<File>();
            // Store list of marked nodes to refill
            HashSet<File> wasMarked = new HashSet<File>();

            // Don't forget marked files
            if( marks.size() > 0 ){
                for(JtvTreeNode jtvNode: marks){
                    wasMarked.add( (File) jtvNode.getUserObject() );
                }
            }

            // Remember all expanded paths
            Enumeration<?> en = n.preorderEnumeration();
            while(en.hasMoreElements()){
                child = (JtvTreeNode) en.nextElement();
                treePath = new TreePath(child.getPath());
                if( !child.isLeaf() && 
                    tree.isExpanded( treePath ) )
                {
                    expanded.add( (File) child.getUserObject() );
                }
            }


            // multiple roots
            if( n.getUserObject() == null ){

                en = n.children();
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
                    /// roots[i++] = f.getAbsolutePath();
                    roots[i++] = f.getPath();
                }

                r2 = buildTree( roots );

            } else {

                File orig = (File) n.getUserObject();
                /// r2 = buildTree( orig.getAbsolutePath() );
                r2 = buildTree( orig.getPath() );
            }

            model.setRoot( r2 );
            marks.clear();

            // Update expanded paths
            en = r2.preorderEnumeration();
            while(en.hasMoreElements()){
                child = (JtvTreeNode) en.nextElement();
                file  = (File) child.getUserObject();
                if( wasMarked.contains( file )){
                    marks.add( child );
                    child.toggleMark();
                }
                if( expanded.contains( file )){
                    treePath = new TreePath(child.getPath());
                    tree.expandPath(treePath);
                }
                if( selection != null ){
                    // HACK: no equals method for TreePath of course see
                    // comment above selection variable.
                    TreePath tmp = new TreePath(child.getPath());
                    if( tmp.toString().equals(selection.toString()) ){
                        scrollTo = tmp;
                    }
                }
            }

            // We found a path to scroll/expand to
            if( scrollTo != null ){
                tree.setSelectionPath( scrollTo );
                tree.scrollPathToVisible( scrollTo );
            }
        }


        void handleFontCycle(KeyEvent e, boolean forward){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){

                TreeCellRenderer cellRenderer = tree.getCellRenderer();

                if( cellRenderer instanceof JtvTreeCellRenderer ){

                    e.consume();

                    JtvTreeCellRenderer jtvCellRenderer =
                        (JtvTreeCellRenderer) cellRenderer;

                    Font font = jtvCellRenderer.getFont();
                    Font next;
                    if( forward ){
                        next = jtvCellRenderer.nextFont();
                    }else{
                        next = jtvCellRenderer.prevFont();
                    }
                    next = new Font(next.getName(),
                            next.getStyle(), font.getSize());
                    jtvCellRenderer.setFont( next );
                    cmdInput.storeCaretPos();
                    cmdInput.setFont( next );
                    log.info(""+ next);
                    SwingUtilities.updateComponentTreeUI(topFrame);
                    cmdInput.restoreCaretPos();
                }
            }

        }


        void handleHideToggle(KeyEvent e){

            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){

                DefaultTreeModel model  = (DefaultTreeModel) tree.getModel();
                JtvTreeNode child, p, r = (JtvTreeNode) model.getRoot();

                ArrayList<JtvTreeNode> removed = new ArrayList<JtvTreeNode>();

                // Hide
                if( !isHidden ){

                    e.consume();

                    if( marks.size() == 0 ){
                        log.info("No marks found");
                        return;
                    }

                    Enumeration<?> en = r.preorderEnumeration();
                    while(en.hasMoreElements()){
                        child = (JtvTreeNode) en.nextElement();
                        if( child.isLeaf() && !child.isMarked() ){
                            removed.add( child );
                        }
                    }

                    // Tuple<father,son>
                    for(JtvTreeNode rm: removed){
                        model.removeNodeFromParent( rm );
                    }

                }else{ // Show off
                    handleRefresh(e);
                }

                // Toggle state
                isHidden = !isHidden;
            }

        }


        void handleHoverHelp(KeyEvent e){
            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                helpMenu.setVisible( !helpMenu.isVisible() );
            }
        }


        void handleCommandLine(KeyEvent e){
            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                hideToggleCommand();
            }
        }


        void handleSaveState(KeyEvent e){
            if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 ){
                e.consume();
                if( marks != null && marks.size() > 0 ){
                    File f;
                    Object obj;
                    ArrayList<String> fnames = new ArrayList<String>();
                    for(JtvTreeNode n: marks){
                        obj = n.getUserObject();
                        if( obj instanceof File ){
                            f = (File) obj;
                            fnames.add( f.getPath() );
                        }
                    }
                    if( fnames.size() > 0 ){
                        byte[] content =
                            handy.join("\n", fnames)
                                 .getBytes(getCharset());
                        try{
                            f = new File(STATE_FILE);
                            io.pipe(content, new FileOutputStream(f));
                        }catch(Exception ex){
                            log.log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }
        }


        public void keyPressed(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
                case KeyEvent.VK_Q     :
                case KeyEvent.VK_W     : handleQuit(e, true); break;
                case KeyEvent.VK_C     : handleQuit(e, false); break;
                case KeyEvent.VK_PLUS  : handleBigger(e); break;
                case KeyEvent.VK_MINUS : handleSmaller(e); break;
                case KeyEvent.VK_E     : handleFontCycle(e, true); break;
                case KeyEvent.VK_S     : handleFontCycle(e, false); break;
                case KeyEvent.VK_F11   : handleMaximize(e, false); break;
                case KeyEvent.VK_M     : handleMaximize(e, true); break;
                case KeyEvent.VK_N     : handleNormalize(e); break;
                case KeyEvent.VK_0     : handleResetYX(e); break;
                case KeyEvent.VK_X     : handleTerm(e); break;
                case KeyEvent.VK_F5    : handleRefresh(e); break;
                case KeyEvent.VK_H     : handleHideToggle(e); break;
                case KeyEvent.VK_K     : handleHoverHelp(e); break;
                case KeyEvent.VK_Y     : handleCommandLine(e); break;
                case KeyEvent.VK_V     : handleSaveState(e); break;
            }

        }


        // not much here :-)
        @Override
        public void keyReleased(KeyEvent e) {

            switch(e.getKeyCode()){
                default: break;
            }
        }

    };

}
