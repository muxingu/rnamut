/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import fork.lib.bio.seq.Nucleotide;
import fork.lib.bio.seq.parser.fastq.Phred;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Serializable;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.read.Read;

/**
 *
 * @author mg31
 */
public class Insertion extends Mutation{
    
private int loc;
private String ins;
    
    public Insertion(int pos, int loc, String ins){
        try{
            seqreg= new Region(pos, pos+ins.length()-1);
            this.loc=loc;
            this.ins=ins;
        }catch(Exception e){ 
            System.err.println("Error - insertion: "+ins); 
        }
    }
    public Insertion(){}

public String insertion(){return ins;}
public int location(){return loc;}

public MutationSpot mutationSpot(IsoformStrand iso){
    int loc_; String ins_=null;
    if(iso.isForward()){
        loc_ = iso.location(loc);
        ins_ = ins;
    }else{
        loc_ = iso.location(loc) + 1;
        ins_ = Nucleotide.reverseComplement( ins );
    }
    return new MutationSpotInsertion(iso.chr(),loc_,loc_, ins_,iso.geneID());
}

@Override
public String toString() {
    return (int)seqreg.low()+"-"+(int)seqreg.high()+": Ins:"+ins;
}

@Override
public double quality(Read read){
    double ret = 0;
    for( int i=(int)seqreg.low(); i<=(int)seqreg.high(); i++ ){
        ret = Math.max(ret, Phred.PHRED33.get(read.quality().charAt(i)) );
    }
    return ret;
}
    
}
