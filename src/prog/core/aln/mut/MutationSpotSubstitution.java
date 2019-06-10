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
import prog.core.aln.hot.DetectorHotspot;
import prog.core.aln.hot.VafCorrector;
import static prog.core.aln.mut.MutationSpot.mutposlow;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationSpotSubstitution extends MutationSpot implements Serializable{
    
    
    public MutationSpotSubstitution(String chr, int low, int high, String mut, String gene){
        super(chr,low,high,"Sub",mut,gene);
    }
    public MutationSpotSubstitution(){}
    

@Override
public String toString() {
    return chr+":"+low+"-"+high+"_"+type+":"+mut;
}

@Override
public String mutatedCDS(Index index) {
    IsoformStrand isostd = isoformStrandSense(index);
    int l = isostd.codingInds().a(), h = isostd.codingInds().b()+1;
    String seq = isostd.sequence();
    int pos = mutposlow(isostd,this);
    return ( l<pos ? seq.substring(l,pos) : "" ) +
            subchar(isostd,this)+ 
            ( pos+1<h ? seq.substring(pos+1,h) : "" );
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
    for( int i=0; i<muta.length(); i++ ){
        if(muta.charAt(i)!=wta.charAt(i)){
            return wta.charAt(i)+""+(i+1)+""+muta.charAt(i);
        }
    }
    return "Syn";
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
        int pos = mutposlow(iso,this);
        int left = left(pos,rlen,iso), right = right(pos,rlen,iso);
        String nseq = (left<=pos?seq.substring(left,pos):"") +subchar(iso,this)+ (pos<right?seq.substring(pos+1,right):"");
        String wtseq = seq.substring(left,right);
        addtoset(nseq,wtseq,new Region(pos-left,pos-left),sps,nseqset);
    }
    return sps;
}

public static void main(String[] args) throws Exception { //debug 
    VafCorrector.main(args);
}
    
}
