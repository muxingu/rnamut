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
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class Deletion extends Mutation{


    public Deletion(int l, int h){
        try{
            refreg = new Region(l,h);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public Deletion(double l, double h){
        this((int)l,(int)h);
    }
    public Deletion(){}

public MutationSpot mutationSpot(IsoformStrand iso){
    String dseq = iso.sequence().substring((int)refreg.low(), (int)refreg.high()+1);
    int l, h;
    if(iso.isForward()){
        l = iso.location((int)refreg.low());
        h = iso.location((int)refreg.high());
    }else{
        h = iso.location((int)refreg.low());
        l = iso.location((int)refreg.high());
        dseq = Nucleotide.reverseComplement( dseq );
    }
    return new MutationSpotDeletion(iso.chr(),l,h,dseq,iso.geneID());
}

@Override
public double quality(Read read){
    return 0;
}


    
}
