/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.anal;

import fork.lib.gui.soft.gen.util.FAnalysis;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author mg31
 */
public class FTreeNode extends DefaultMutableTreeNode{
    
private FAnalysis anal;
    
    public FTreeNode(FAnalysis anal, String tag){
        super(tag);
        this.anal = anal;
    }
    
public FAnalysis anal(){return anal;}
    
    
    
}
