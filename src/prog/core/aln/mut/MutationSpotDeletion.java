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
import java.util.HashSet;
import prog.core.aln.ele.Gene;
import prog.core.aln.ele.Isoform;
import prog.core.aln.ele.IsoformStrand;
import static prog.core.aln.mut.MutationSpot.mutposlow;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationSpotDeletion extends MutationSpot implements Serializable{
    
    
    public MutationSpotDeletion(String chr, int low, int high, String mut, String gene){
        super(chr,low,high,"Del",mut,gene);
    }
    public MutationSpotDeletion(){}
    

@Override
public String toString() {
    return chr+":"+low+"-"+high+"_"+type+":"+mut;
}

@Override
public String mutatedCDS(Index index) {
    IsoformStrand isostd = isoformStrandSense(index);
    int l = isostd.codingInds().a(), h = isostd.codingInds().b()+1;
    String seq = isostd.sequence();
    int posl = mutposlow(isostd,this), posh = mutposhigh(isostd,this);
    return (l<=posl ? seq.substring(l,posl) : "") + 
            (posh+1<=h ? seq.substring(posh+1,h) : "");
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
    return aaMutDiffLen(mutseq,wtseq,muta,wta,"Del",0);
}

@Override
public ArrayList<Triplet<String, Region, String>> reconstructedTranscripts(Index index, int rlen)throws Exception { 
    HashSet<String> nseqset = new HashSet<>();
    ArrayList<Triplet<String,Region,String>> sps = new ArrayList<>();
    Gene geneid = isoformStrandSense(index).parent().parent();
    ArrayList<IsoformStrand> isos = new ArrayList<>();
    for( Isoform is:geneid.isoforms() ){
        isos.add(is.strandSense());
    }
    for( IsoformStrand iso:isos ){
        String seq = iso.sequence();
        int posl = mutposlow(iso,this), posh = mutposhigh(iso,this);
        int left = left(posl,rlen,iso), right = right(posh,rlen,iso);
        String nseq = left<=posl ? seq.substring(left,posl) : "";
        nseq += posh+1<=right ? seq.substring(posh+1,right) : "";
        String wtseq = seq.substring(left,right);
        addtoset(nseq,wtseq,new Region(posl-left,posh-left),sps,nseqset);
    }
    return sps;
}


}

