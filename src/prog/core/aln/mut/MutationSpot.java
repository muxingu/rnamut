/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import fork.lib.base.collection.Triplet;
import fork.lib.bio.seq.CodonTranslator;
import fork.lib.bio.seq.Nucleotide;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.aln.read.ReadShrink;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
abstract public class MutationSpot implements Serializable{
    
    
protected String chr, type, mut, gene;
protected int low, high;
 
protected transient String mutseq = null;
public HashSet<Integer> mutReads = new HashSet<>();
public HashSet<Integer> wtReads = new HashSet<>();

    
    public MutationSpot(String chr, int low, int high, String type, String mut, String gene){
        this.chr=chr;
        this.type=type;
        this.mut=mut;
        this.gene=gene;
        this.low=low;
        this.high=high;
    }
    public MutationSpot(){}


@Override
abstract public String toString();
abstract public String mutatedCDS(Index index);
abstract public String aminoAcidMutation(Index index);
abstract public ArrayList<Triplet<String,Region,String>> reconstructedTranscripts(Index index, int rlen)throws Exception;


  

public String gene(){return gene;}
public String chr(){return chr;}
public int low(){return low;}
public int high(){return high;}
public String type(){return type;}
public String mutString(){return mut;}
public IsoformStrand isoformStrandSense(Index index){ return index.getGene(gene).isoforms().get(0).strandSense(); }

public void setGene(String gene){this.gene=gene;}
public void setWTReads(Collection<Integer> bg){ wtReads = new HashSet<>(); wtReads.addAll(bg); }
public void setMutatedReads(Collection<Integer> rs){ mutReads=new HashSet<>(); mutReads.addAll(rs); } 

public double vaf(){
    int mn = mutReads.size(), bg = wtReads.size();
    double tot=(bg+mn);
    return  tot==0 ? 0 : (double) mn/tot;
}
public HashSet<Read> mutatedReads(ReadPool p){
    HashSet<Read> ret = new HashSet<>();
    for( Integer id:mutReads ){
        ret.add(p.get(id));
    }
    return ret;
}
public HashSet<Read> wtReads(ReadPool p){
    HashSet<Read> ret = new HashSet<>();
    for( Integer id:wtReads ){
        ret.add(p.get(id));
    }
    return ret;
}


public String wtCDS(Index index){ 
    IsoformStrand iso = isoformStrandSense(index);
    return iso.sequence().substring(iso.codingInds().a(),iso.codingInds().b()+1);
}
protected static int mutposlow(IsoformStrand iso, MutationSpot mut){
    return iso.isForward() ? mutpos(iso, mut.low()) : mutpos(iso,mut.high());
}
protected static int mutposhigh(IsoformStrand iso, MutationSpot mut){
    return iso.isForward() ? mutpos(iso, mut.high()) : mutpos(iso,mut.low());
}
protected static int mutpos(IsoformStrand iso, int ref){
    int pos=0; String seq = iso.sequence();
    if(iso.isForward()){
        while(pos<seq.length()){
            if(iso.pos2loc().get(pos)>=ref){ break; }
            pos++;
        }
    }else{
        while(pos<seq.length()){
            if(iso.pos2loc().get(pos)<=ref){ break; }
            pos++;
        }
    }
    return pos;
}
protected static char subchar(IsoformStrand iso, MutationSpot m){
    return iso.isForward() ? m.mutString().split(">")[1].charAt(0) :
            Nucleotide.complementaryNucleotide(m.mutString().split(">")[1].charAt(0));
}
protected static String aaMutDiffLen(String mutseq, String wtseq, String muta, String wta, String tag, int off){ 
    if(isInFrame(mutseq,wtseq)){
        ReadShrink rs = new ReadShrink(wta,muta); rs.computeLeft(); rs.computeRight();
        return (rs.left().length()+off)+"_"+tag+"_"+rs.sequenceRemain();
    }else{ return stringFrameShift(wta,muta); }
}
protected static boolean isInFrame(String wt, String mut){
    int dif = Math.abs(mut.length()-wt.length());
    return (double)dif/3 == dif/3;
}
protected static String stringFrameShift(String wt, String mut){
    for( int i=0; i<Math.min(wt.length(),mut.length()); i++ ){
        if(mut.charAt(i)!=wt.charAt(i)){
            return wt.charAt(i)+""+(i+1)+"fs";
        }
    }
    return "??fs";
}

protected static int left(int pos, int rlen, IsoformStrand iso){ return pos>=rlen ? pos-rlen : 0; }
protected static int right(int pos, int rlen, IsoformStrand iso){
    return pos+rlen<iso.sequence().length() ?  pos+rlen : iso.sequence().length();
}
protected static void addtoset(String nseq, String wtseq, Region reg, ArrayList<Triplet<String,Region,String>> sps, HashSet<String> nseqset){
    if( nseq.equals(wtseq) ){return;}
    if(!nseqset.contains(nseq)){
        sps.add( new Triplet(nseq, reg,wtseq) );
        nseqset.add(nseq);
    }
    //System.out.println(nseq); System.out.println(wtseq); System.out.println();
}

  
@Override
public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) {return false; }
    if (getClass() != obj.getClass()) {return false;}
    final MutationSpot other = (MutationSpot) obj;
    if (!Objects.equals(this.chr, other.chr)) {return false;}
    if (!Objects.equals(this.type, other.type)) {return false;}
    if (!Objects.equals(this.mut, other.mut)) {return false;}
    if (!Objects.equals(this.low, other.low)) {return false;}
    if (!Objects.equals(this.high, other.high)) {return false;}
    return true;
}

@Override
public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.chr);
    hash = 97 * hash + Objects.hashCode(this.low);
    hash = 97 * hash + Objects.hashCode(this.high);
    hash = 97 * hash + Objects.hashCode(this.type);
    hash = 97 * hash + Objects.hashCode(this.mut);
    return hash;
}


}


