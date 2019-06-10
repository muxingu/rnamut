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
import prog.core.aln.Aligner;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.read.Read;

/**
 *
 * @author mg31
 */
public class Substitution extends Mutation{
    
private String seqchars, refchars;
    
    public Substitution(int seqpos, int refloc, String seqchars, String refchars)throws Exception { 
        try{
            refreg = new Region(refloc, refloc+refchars.length()-1);
            seqreg = new Region(seqpos, seqpos+seqchars.length()-1);
            this.seqchars = seqchars;
            this.refchars = refchars;
        }catch(Exception e){
            e.printStackTrace(); System.exit(1);
        }
        if(seqchars.length()!=refchars.length()){
            System.err.println("seqchar:"+this.seqchars+" != refchar:"+this.refchars);
            throw new Exception();
        }
    }
    public Substitution(int seqpos, int refloc, char seqchar, char refchar)throws Exception { 
        this(seqpos,refloc, Character.toString(seqchar), Character.toString(refchar) );
    }
    public Substitution(){}

public String originalChars(){return refchars;}
public String substitutionChars(){return seqchars;}
public String toString(){return (int)seqreg.low()+"-"+(int)seqreg.high()+": Sub:"+ refchars+">"+seqchars; }

public MutationSpot mutationSpot(IsoformStrand iso){
    int l, h;
    String mutstr = iso.sequence().substring((int)refreg.low(), (int)refreg.high()+1);
    if(iso.isForward()){
        mutstr = mutstr+">"+seqchars;
        l = iso.location((int)refreg.low()); h = iso.location((int)refreg.high());
    }else{
        mutstr = Nucleotide.reverseComplement( mutstr ) + ">"+ Nucleotide.reverseComplement( seqchars );
        h = iso.location((int)refreg.low()); l = iso.location((int)refreg.high());
    }
    return new MutationSpotSubstitution(iso.chr(),l,h,mutstr,iso.geneID());
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
