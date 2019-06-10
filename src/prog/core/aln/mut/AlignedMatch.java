/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Serializable;
import prog.core.aln.read.Read;

/**
 *
 * @author mg31
 */
public class AlignedMatch extends AlignedElement{

    public AlignedMatch(){}
    public AlignedMatch(int[] seqlh, int[] reflh)throws Exception { 
        this(seqlh[0],seqlh[1],reflh[0],reflh[1]);
    }
    public AlignedMatch(int seql, int seqh, int refl, int refh)throws Exception { 
        try{
            seqreg = new Region(seql,seqh);
            refreg = new Region(refl,refh);
        }catch(Exception e){
            System.err.println(seql+" "+seqh+"   "+refl+" "+refh);
            e.printStackTrace();
        }
        if(seqreg.getRange()!=refreg.getRange()){
            System.err.println("seq:"+seqreg+" != ref:"+refreg);
            throw new Exception();
        }
    }
    public AlignedMatch(double seql, double seqh, double refl, double refh)throws Exception { 
        this((int)seql,(int)seqh,(int)refl,(int)refh);
    }
    public AlignedMatch(int seq, int ref)throws Exception { 
        this(seq,seq,ref,ref);
    }
    
    
@Override
public String toString() {
    return (int)seqreg.low()+"-"+(int)seqreg.high()+": Mat:"+(int)refreg.low()+"-"+(int)refreg.high();
}


}
