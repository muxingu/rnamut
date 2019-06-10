/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.anal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import prog.core.aln.mut.MutationSpot;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class AnalMutSub extends ViewPoint{
    
private static DecimalFormat df= new DecimalFormat("#.###");
private static DecimalFormat df1p= new DecimalFormat("#.#");

public String gene;
public ArrayList<MutationSpot> muts;
public DefaultTableModel tm;
    
    public AnalMutSub(AnalMut mut, String gene, ArrayList<MutationSpot> muts){
        super(mut);
        this.gene=gene;
        this.muts=muts;
        try{
            Index index = ((Anal)parent.getParent()).index;
            String[][] ss= new String[muts.size()][6];
            for(int i=0;i<muts.size();i++){
                MutationSpot m = muts.get(i);
                ss[i][0] = m.gene();
                ss[i][1] = m.toString();
                ss[i][2] = m.aminoAcidMutation(index);
                ss[i][3] = Integer.toString(m.mutReads.size());
                ss[i][4] = df1p.format(m.wtReads.size());
                ss[i][5] = df.format(m.vaf());
            }
            tm = new DefaultTableModel(ss,
                    new String[]{"Gene","Mutation","ProtMut","MutReads","WTReads","VAF"}){
                public boolean isCellEditable(int row, int col){return false; }
            };
        }catch(Exception e){}
    }
    
@Override
protected JComponent createComponent() {
    JTable t= new JTable(tm);
    JScrollPane jsp = new JScrollPane(t);
    return jsp;
}
public void render(){
    this.reloadComponents();
    ((Main)getTopParent()).gui().mainPan.setJSPRight(comp);
}

public void writeToFile(File out)throws Exception { 
    out.getAbsoluteFile().getParentFile().mkdirs();
    BufferedWriter bw= new BufferedWriter(new FileWriter(out));
    bw.write("Gene\tMutation\tProtMut\tMutReads\tWTReads\tVAF\n");
    for( int i=0; i<tm.getRowCount(); i++ ){
        for( int j=0; j<tm.getColumnCount(); j++ ){
            bw.write((String)tm.getValueAt(i, j)+"\t");
        }
        bw.write("\n");
    }
    bw.close();
}

    
}
