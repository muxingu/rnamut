/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import prog.core.Param;
import prog.core.aln.mut.MutationResult;
import prog.core.aln.read.ReadPool;
import prog.core.aln.res.AlignmentResult;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class Detector {

protected ReadPool pool;
protected Index index;
protected Param par;
protected AlignmentResult alnres;

protected MutationResult res = new MutationResult();


    public Detector(AlignmentResult alnres, Index index, ReadPool pool, Param par) {
        this.pool = pool;
        this.index=index;
        this.alnres = alnres;
        this.par=par; 
    }


public MutationResult result(){
    return res;
}


    
}
