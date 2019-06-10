/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.anal;

import fork.lib.gui.soft.gen.util.FAnalysis;
import java.awt.Cursor;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author mg31
 */
abstract public class ViewPoint extends FAnalysis{

protected JComponent comp;
    
    
    public ViewPoint(FAnalysis anal){
        super(anal);
    }
    
abstract protected JComponent createComponent();
    
public JComponent compoment(){return comp;}

@Override
public void reloadComponents() {
    Main main= (Main)this.getTopParent();
    main.gui().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    comp= createComponent();
    main.gui().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}
@Override
protected void destroy() {
    comp=null;
}




    
    
    
}
