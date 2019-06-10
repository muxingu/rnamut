/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.anal;

import fork.lib.gui.soft.gen.util.FAnalysis;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import prog.Sys;
import prog.core.aln.mut.MutationResult;

/**
 *
 * @author mg31
 */
public class AnalMut extends ViewPoint{
    
public JList list;


    
    public AnalMut(Anal anal, MutationResult mutres){
        super(anal);
        DefaultListModel mod = new DefaultListModel();
        mod.addElement("All Mutations");
        addChild( new AnalMutSub(this, "All Mutations", mutres.allMutations() ) );
        list = new JList(mod);
        list.addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int i = ((JList)e.getSource()).getSelectedIndex();
                children.get(i+1).reRenderChain();
            }
        });
        //list.setSelectedIndex(0);
    }



@Override
protected JComponent createComponent() {
    JTabbedPane tab = new JTabbedPane();
    tab.addTab("Genes", new ImageIcon(Sys.IMG_FOLDER_16), list);
    return tab;
}
public void render(){
    ((Main)getTopParent()).gui().mainPan.setJSPBtmLeft(comp);
}
    
    
}
