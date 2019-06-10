/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.util;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingWorker;


abstract public class FWorker extends SwingWorker<Void, String>{

public boolean ifsucceed = false;
protected JDialog dialog;
protected JFrame parent;
protected Writer wr;
    

    public FWorker(JDialog dialog, JFrame parent, Writer wr){
        this.dialog = dialog;
        this.parent = parent;
        this.wr = wr;
    }


abstract public Void doInBackground();
abstract protected void done();

public void execute2(){ 
    try {
        wr.write("Start mapping...\n");
    } catch (IOException ex) {}
    execute();
    dialog.setVisible(true);
}


public void interrupt(){
    this.cancel(true);
    try {
        wr.write("Candelling...");
        Thread.sleep(1000);
    } catch (Exception ex) {}
    dialog.dispose();
}

}

