/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import fork.lib.base.collection.Pair;
import fork.lib.base.format.collection.ArrayOp1D;
import fork.lib.bio.seq.CodonTranslator;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Serializable;
import java.util.ArrayList;
import prog.core.aln.ele.IsoformStrand;
import static prog.core.aln.mut.MutationSpot.mutposlow;
import prog.core.aln.read.ReadShrink;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationSpotPTD extends MutationSpotITD implements Serializable{

    
    public MutationSpotPTD(String chr, int low, int high, String mut, String gene){
        super(chr,low,high,mut,gene);
        this.type="PTD";
    }
    public MutationSpotPTD(){}
    
    
    
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
    ReadShrink rs = new ReadShrink(wtseq,mutseq); rs.computeLeft(); rs.computeRight();
    String rem = rs.sequenceRemain();
    int ind=-iso.codingInds().a(), low=wtseq.indexOf(rem);
    try{
        Region reg= new Region(low, low+rem.length()-1); 
        ArrayList<Pair<Integer,Integer>> lhs = iso.parent().lhs();
        if(iso.strand()=='-'){ ArrayOp1D.invert(lhs); }
        ArrayList<Integer> pdexs= new ArrayList<>(); 
        Region[] sides = new Region[2];
        for( int i=0; i<lhs.size(); i++ ){
            Pair<Integer,Integer> lh = lhs.get(i); int len = lh.b()-lh.a()+1;
             Region exon = new Region(ind,ind+len-1);
             if(reg.contains(exon)){
                 pdexs.add((i+1));
             }else if(reg.overlapsWith(exon)){
                 if( exon.low()<reg.low() && exon.high()>=reg.low() ){
                     sides[0] = reg.overlap(exon);
                 }else if( exon.low()<=reg.high() && exon.high()>reg.high() ){
                     sides[1] = reg.overlap(exon);
                 }
             }
             //System.out.println((i+1)+" "+len+ "  "+exon+" "+reg);
             ind+=len;
        }
        if(!pdexs.isEmpty()){
            if(sides[0]==null && sides[1]==null){
                return "exons"+pdexs.get(0) +"-"+ pdexs.get(pdexs.size()-1);
            }
            return "Error";
        }
        return aaMutDiffLen(mutseq,wtseq,muta,wta,"Ins",1);
    }catch(Exception e){return "Error";}
}

    
}
