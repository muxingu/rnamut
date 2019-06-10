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
import prog.core.aln.hot.VafCorrector;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationSpotInsertion extends MutationSpot implements Serializable{
    
    
    public MutationSpotInsertion(String chr, int low, int high, String mut, String gene){
        super(chr,low,high,"Ins",mut,gene);
    }
    public MutationSpotInsertion(){}
    

@Override
public String toString() {
    return chr+":"+low+"-"+high+"_"+type+":"+mut;
}

@Override
public String mutatedCDS(Index index) {
    IsoformStrand isostd = isoformStrandSense(index);
    int l = isostd.codingInds().a(), h = isostd.codingInds().b()+1;
    String seq = isostd.sequence();
    int pos = mutposlow(isostd, this);
    return seq.substring(l,pos) +mutString()+ seq.substring(pos,h);
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
    return aaMutDiffLen(mutseq,wtseq,muta,wta,"Ins",0);
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
        int pos = mutposlow(iso, this), left = left(pos,rlen,iso), right = right(pos,rlen,iso);
        String nseql = seq.substring(left,pos), nseqr= seq.substring(pos,right), 
                mstr=iso.isForward()?mutString():Nucleotide.reverseComplement(mutString()), nseq=nseql+mstr+nseqr;
        String wtseq = nseql+nseqr;
        int sfl=0; String nseql_=nseql+mstr;
        while(true){
            int p = nseql.length()-(sfl+1); if( p<0 ){break;}
            if( nseql.charAt(p) == nseql_.charAt(p+mstr.length()) ){ sfl++; }else{ break; }
        }
        int sfr=0; String nseqr_=mstr+nseqr;
        while(true){
            int p = sfr; if( p>=nseqr.length() ){break;}
            if( nseqr.charAt(p) == nseqr_.charAt(p) ){ sfr++;  }else{ break; }
        }
        int pos_ = pos-left;
        addtoset(nseq,wtseq,new Region(pos_-sfl-1,pos_+mutString().length()-1+sfr+1),sps,nseqset);
    }
    return sps;
}

public static void main(String[] args) throws Exception { //debug 
   VafCorrector.main(args);
}
    
}
