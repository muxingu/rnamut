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
import java.util.HashSet;
import prog.core.aln.ele.Gene;
import prog.core.aln.ele.Isoform;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.hot.DetectorHotspot;
import prog.core.aln.hot.VafCorrector;
import static prog.core.aln.mut.MutationSpot.mutposlow;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationSpotITD extends MutationSpot implements Serializable{
    
    
    public MutationSpotITD(String chr, int low, int high, String mut, String gene){
        super(chr,low,high,"ITD",mut,gene);
    }
    public MutationSpotITD(){}
    

@Override
public String toString() {
    return chr+":"+low+"-"+high+"_"+type;
}

public int junction(Index index){
    IsoformStrand iso = isoformStrandSense(index);
    if(iso.isForward()){
        return high;
    }else{
        return low;
    }
}

@Override
public String mutatedCDS(Index index) {
    IsoformStrand isostd = isoformStrandSense(index);
    int l = isostd.codingInds().a(), h = isostd.codingInds().b()+1;
    String seq = isostd.sequence();
    String mstr = isostd.isForward() ? mutString() : Nucleotide.reverseComplement(mutString());
    int posh = mutposhigh(isostd,this);
    return (l<=posh+1?seq.substring(l,posh+1):"") + mstr + (posh+1<=h?seq.substring(posh+1,h):"");
}

@Override
public String aminoAcidMutation(Index index) {
    IsoformStrand iso = isoformStrandSense(index); 
    int l = iso.codingInds().a(), h = iso.codingInds().b()+1;
    int posl = mutposlow(iso, this), posh = mutposhigh(iso,this);
    if(posh<=l || posl>h){
        return "UTR";
    }
    String wtseq = wtCDS(index), mutseq = mutatedCDS(index);
    String wta = new CodonTranslator(wtseq).frame(0);
    String muta = new CodonTranslator(mutseq).frame(0);
    return aaMutDiffLen(mutseq,wtseq,muta,wta,"Ins",1);
}

@Override
public ArrayList<Triplet<String, Region, String>> reconstructedTranscripts(Index index, int rlen) throws Exception {
    HashSet<String> nseqset = new HashSet<>();
    ArrayList<Triplet<String,Region,String>> sps = new ArrayList<>();
    Gene geneid = isoformStrandSense(index).parent().parent();
    ArrayList<IsoformStrand> isos = new ArrayList<>();
    for( Isoform is:geneid.isoforms() ){
        isos.add(is.strandSense());
    }
    for( IsoformStrand iso:isos ){
        String seq = iso.sequence(); 
        String mstr = iso.isForward() ? mutString() : Nucleotide.reverseComplement(mutString());
        int posl = mutposlow(iso,this), posh = mutposhigh(iso,this);
        int  hleft = left(posh,rlen,iso), hright = right(posh,rlen,iso);
        String nseq = seq.substring(hleft,posh+1) +  (mstr.length() >= rlen ? 
                mstr.substring(0,rlen) : mstr+seq.substring(posl,posl+rlen-mstr.length()-1));
        String wtseq = seq.substring(hleft,hright);
        addtoset(nseq,wtseq,new Region(posl-hleft,posh-hleft),sps,nseqset);
        
        //System.out.println(nseq);System.out.println(wtseq);System.out.println();
    }
    return sps;
}

public static void main(String[] args) throws Exception { //debug 
    VafCorrector.main(args);
}

}
