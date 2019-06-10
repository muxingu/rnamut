/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.anal;

import fork.lib.gui.soft.gen.util.FAnalysis;
import javax.swing.JList;
import prog.core.aln.res.AlignmentResult;
import prog.core.aln.mut.MutationResult;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class Anal extends FAnalysis{

    
private String dir, poolpath, alnpath, corrpath;
public Index index;
public ReadPool pool;
public AlignmentResult alnres;
public MutationResult mutres;

public AnalMut analmut;

public FTreeNode node;

    
    public Anal(Main main, String tag, String dir, String poolpath, String alnpath, String corrpath, Index index){
        super(main);
        this.tit = tag;
        this.dir = dir;
        this.poolpath = poolpath;
        this.alnpath = alnpath;
        this.corrpath = corrpath;
        this.index = index;
        try{
            pool = ReadPool.read(poolpath);
            alnres = AlignmentResult.read(alnpath, index, pool);
            mutres = MutationResult.read(corrpath);
            analmut = new AnalMut(this,mutres);
        }catch(Exception e){ 
            e.printStackTrace();
        } 
        reloadComponents();
    }

@Override
public void reloadComponents() {
    node = new FTreeNode(this,this.tit);
}
@Override
protected void destroy() {
    node = null;
}
public void render(){}


}


