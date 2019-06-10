/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.comp;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import prog.gui.MainGui;

/**
 *
 * @author mg31
 */
public class DynamicMenuBar extends JMenuBar{
    
public MainGui main;
public JMenu menuFile;
public JMenu menuView;
public JMenu menuHelp;

    
    public DynamicMenuBar(MainGui main){
        this.main=main;
    }
    
    




    
    
}
