/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import fork.lib.base.collection.Triplet;
import fork.lib.bio.seq.CodonTranslator;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.hot.VafCorrector;
import prog.core.aln.read.ReadShrink;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationSpotFusionRead extends MutationSpotFusion implements Serializable{
    
public HashSet<Integer> wtReads2 = new HashSet<>();

    
    public MutationSpotFusionRead(String chr, int low, String chr2, int high, String gene, String gene2){
        super(chr,low,high,"Fusion","",gene);
        this.chr2=chr2;
        this.gene2=gene2;
    }
    public MutationSpotFusionRead(){}
 

public HashSet<Integer> getWTReads2(){return wtReads2;}
public void setWTReads2(Collection<Integer> bg){ wtReads2 = new HashSet<>(); wtReads2.addAll(bg); }

@Override
public String toString() {
    return gene+":"+chr+":"+low+"-"+gene2+":"+chr2+":"+high;
}

@Override
public String mutatedCDS(Index index) {
    IsoformStrand isoa = index.getGene(gene).isoforms().get(0).strandSense();
    IsoformStrand isob = index.getGene(gene2).isoforms().get(0).strandSense();
    String seqa = isoa.sequence(), seqb = isob.sequence();
    int posa = mutpos(isoa,low), posb = mutpos(isob,high);
    
    //System.out.println(this); System.out.println(posa+" "+posb);
    /*
    System.out.println(this);
    System.out.println(posa+" "+posb);
    System.out.println(isoa.codingInds()+"  "+isob.codingInds());
    System.out.println(seqa.length()+"  "+seqb.length());
    */
    if( isoa.codingInds().a()>posa+1 || isob.codingInds().b()+1<posb ){
        return "UTR";
    }
    String nseq = seqa.substring(isoa.codingInds().a(),posa+1)+seqb.substring(posb,isob.codingInds().b()+1);
    return nseq;
}


@Override
public String aminoAcidMutation(Index index) {
    IsoformStrand isoa = index.getGene(gene).isoforms().get(0).strandSense();
    IsoformStrand isob = index.getGene(gene2).isoforms().get(0).strandSense();
    String mutseq = mutatedCDS(index);
    //Print.string(mutseq, 100); System.out.println(mutseq.length()); System.out.println();
    
    String wtseqa = isoa.sequence().substring(isoa.codingInds().a(),isoa.codingInds().b()+1);
    String wtseqb = isob.sequence().substring(isob.codingInds().a(),isob.codingInds().b()+1);
    String muta = new CodonTranslator(mutseq).frame(0);
    String wtaa =  new CodonTranslator(wtseqa).frame(0);
    String wtab =  new CodonTranslator(wtseqb).frame(0);
    String astra = fusionString(muta,wtaa,wtab,isoa,isob);
    
    //System.out.println(mutseq); System.out.println(wtseqa); System.out.println(wtseqb); System.exit(1);
    return astra;
}

private String fusionString(String mut, String wta, String wtb, IsoformStrand isoa, IsoformStrand isob){
    ReadShrink sl = new ReadShrink(mut,wta); sl.computeLeft();
    ReadShrink sr = new ReadShrink(mut,wtb); sr.computeRight();
    //System.out.println(mut);System.out.println(wta);System.out.println(wtb);
    //System.exit(1);
    try{
    return isoa.geneID()+"."+mut.charAt(sl.left().length()-1)+sl.left().length()+"-"+isob.geneID()+"."+
            mut.charAt(mut.length()-sr.right().length())+(wtb.length()-sr.right().length()+1);
    }catch(Exception e){
        return "Error";
    }
}

@Override
public ArrayList<Triplet<String, Region, String>> reconstructedTranscripts(Index index, int rlen) throws Exception {
    HashSet<String> nseqset = new HashSet<>();
    ArrayList<Triplet<String,Region,String>> sps = new ArrayList<>();
    IsoformStrand isoa = index.getGene(gene).isoforms().get(0).strandSense();
    IsoformStrand isob = index.getGene(gene2).isoforms().get(0).strandSense();
    String seqa = isoa.sequence(), seqb = isob.sequence();
    int posa = mutpos(isoa,low), posb = mutpos(isob,high);
    String wtseq = seqa.substring(left(posa,rlen,isoa),right(posa,rlen,isoa))+"XXXXXXXXXX"+
            seqb.substring(left(posb,rlen,isob),right(posb,rlen,isob));
    
    int mutind;
    int left = left(posa,rlen,isoa), right = right(posb+1,rlen,isob);
    String nseq = seqa.substring(left,posa+1)+seqb.substring(posb,right);
    mutind = posa-left;

    //System.out.println(this);System.out.println(isoa.uniqueID()+"  "+isob.uniqueID());
    //System.out.println(nseq);System.out.println(wtseq);
    addtoset(nseq,wtseq,new Region(mutind,mutind),sps,nseqset);
    
    return sps;
}
    
public static void main(String[] args) throws Exception { //debug 
    //ZZDebug.main(args);
    //DetectorHotspot.main(args);
    VafCorrector.main(args);
}

}
