/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.util;

import java.io.IOException;
import java.io.Writer;
import javax.swing.JTextArea;

/**
 *
 * @author mg31
 */
public class TaWriter extends Writer{
    
private JTextArea ta;
    
    public TaWriter(JTextArea ta){
        this.ta=ta;
    }

    
    
@Override
public void write(char[] cbuf, int off, int len) throws IOException {
    ta.append( new String(cbuf) );
    ta.setCaretPosition(ta.getDocument().getLength());
}

public void flush() throws IOException {}
public void close() throws IOException {}

}
