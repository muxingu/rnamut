/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.anal;

import fork.lib.base.file.FileName;
import fork.lib.gui.soft.gen.util.FAnalysis;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import prog.Sys;
import prog.core.index.Index;
import prog.gui.MainGui;

/**
 *
 * @author mg31
 */
public class Main extends FAnalysis{

private MainGui gui;
private JTree tree;
    
    
    public Main(MainGui gui){
        super(null);
        this.gui=gui;
    }
    
public MainGui gui(){return gui;}
    
    
@Override
protected void destroy() {
    
}

@Override
public void reloadComponents() {
    int ind;
    try{ 
        ind= tree==null ? 3 : tree.getSelectionRows()[0];
    }catch(Exception e){ ind=0; }
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
    DefaultTreeModel treemod = new DefaultTreeModel(root);
    tree = new JTree(treemod);
    for( FAnalysis ch:children ){
        Anal anal = (Anal) ch;
        treemod.insertNodeInto(anal.node, root, root.getChildCount());
    }
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    for( int i=0; i<tree.getRowCount(); i++ ){
        tree.expandRow(i);
    }
    tree.setSelectionRow(ind);
    tree.addTreeSelectionListener( new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
            try{
                FTreeNode n = (FTreeNode)tree.getLastSelectedPathComponent();
                n.anal().reRenderChain();
            }catch(Exception ex){
            }
        }
    } );
}

@Override
public void render() {
    JTabbedPane ttl= new JTabbedPane();
    ttl.addTab("Project",tree);
    gui.mainPan.setJSPTopLeft(ttl);
    if(curChild==null){return;}
    System.out.println(curChild);
    curChild.render();
}

public void addAnal(String tag, String dir, String poolpath, String alnpath, String corrpath, Index index){
    Anal anal = new Anal(this,tag,dir,poolpath,alnpath,corrpath,index);
    anal.reRenderChain();
    tree.setSelectionRow(2); anal.analmut.list.setSelectedIndex(0);
}

public void saveAnal()throws Exception { 
    if(! (curChild instanceof AnalMutSub) ){
        JOptionPane.showMessageDialog(gui, "Nothing to save!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    File f = null;
    JFileChooser jfc= new JFileChooser();
    jfc.setDialogType(JFileChooser.SAVE_DIALOG);
    jfc.setCurrentDirectory( new File(Sys.DIR) );
    jfc.setFileFilter(new FileNameExtensionFilter("Text", "txt"));
    int returnVal = jfc.showSaveDialog(gui);
    if ( returnVal == JFileChooser.APPROVE_OPTION ) {
        f=jfc.getSelectedFile();
        if(f.exists()){
            int o = JOptionPane.showConfirmDialog(gui, "File "+FileName.baseName(f)+" already exists.\nOverwrite?");
            if(o==JOptionPane.YES_OPTION){
                ((AnalMutSub)curChild).writeToFile(f);
                JOptionPane.showMessageDialog(gui, "Output saved: "+FileName.baseName(f));
            }
        }
    }
}



public static void main(String[] args) throws Exception { //debug 
    MainGui.main(args);
}   
    
}
