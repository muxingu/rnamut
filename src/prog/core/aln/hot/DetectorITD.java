/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import prog.core.aln.mut.MutationResult;
import prog.core.aln.mut.MutationSpot;
import fork.lib.base.collection.Pair;
import fork.lib.base.collection.Triplet;
import fork.lib.bio.seq.Nucleotide;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import prog.core.Param;
import prog.core.aln.res.AlignmentResult;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.mut.MutationSpotITD;
import prog.core.aln.mut.MutationSpotPTD;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class DetectorITD {

private Param par;
private Index index;
private ReadPool pool;
private HashMap<String,HashMap<Triplet<Integer,Integer,String>,HashSet<Integer>>> ifc = new HashMap<>();
private AlignmentResult alnres;

private MutationResult res = new MutationResult();
    
    
    public DetectorITD(AlignmentResult alnres, Index index, ReadPool pool, Param par){
        this.alnres=alnres;
        this.index = index;
        this.pool = pool;
        this.par=par;
        if(this.par==null){ this.par=new Param(); }
        for( String gid:alnres.alignmentResultITD().keySet() ){
            for( String iso:alnres.alignmentResultITD().get(gid).keySet() ){
                for( Pair<Integer,Anchor> p:alnres.alignmentResultITD().get(gid).get(iso) ){
                    addRead(iso, pool.get(p.a()), p.b());
                }
            }
        }
    }
    public DetectorITD(AlignmentResult alnres, Index index, ReadPool pool){this(alnres,index,pool,null);}


public void addRead(String isostr, Read read, Anchor an){
    IsoformStrand iso = index.getIsoformStrand(isostr);
    //System.out.println(an.leftind+"  "+an.rightind+"  "+ an.firstSize+" "+an.lastSize); 
    if( (an.firstSize>=2 && an.lastSize>0) || (an.lastSize>=2 && an.firstSize>0)){
        if(an.lind!=-1 && an.rind!=-1 ){
            int len = an.rind - an.lind +1;
            if(len>2 && len<par.maxITDLength){
                //System.out.println("itd");
                if(!ifc.containsKey(isostr)){ 
                    ifc.put(isostr, new HashMap<>()); 
                }
                Triplet<Integer,Integer,String> p =new Triplet<>(an.lind, an.rind,an.ins);
                if(!ifc.get(isostr).containsKey(p)){ 
                    ifc.get(isostr).put(p, new HashSet<>()); 
                }
                ifc.get(isostr).get(p).add(read.intID());
            }
        }
    }
}

public void addAlignmentResult(AlignmentResult res){
    for( String gid:res.alignmentResultITD().keySet() ){
        for( String iso:res.alignmentResultITD().get(gid).keySet() ){
            for( Pair<Integer,Anchor> p:res.alignmentResultITD().get(gid).get(iso) ){
                addRead(iso, pool.get(p.a()), p.b());
            }
        }
    }
}

public void start()throws Exception { 
    ArrayList<Pair<MutationSpotITD,HashSet<Integer>>> mcs = new ArrayList<>();
    HashMap<MutationSpot,Integer> mind = new HashMap<>();
    for( String iso:ifc.keySet() ){
        HashMap<Triplet<Integer,Integer,String>,HashSet<Integer>> fc= ifc.get(iso);
        for( Triplet<Integer,Integer,String> k:fc.keySet() ){
            HashSet<Integer> rs = fc.get(k);
            MutationSpotITD itd = toitd(iso,k.a(),k.b(),k.c());
            itd.setMutatedReads(rs);
            if(itd!=null){ 
                if(!mind.containsKey(itd)){
                    mcs.add( new Pair<>(itd,rs) );
                    mind.put(itd,mcs.size()-1);
                }else{
                    Pair<MutationSpotITD,HashSet<Integer>> pair=mcs.get(mind.get(itd));
                    pair.a().mutReads.addAll(itd.mutReads );
                }
            }
        }
    }
    for( Pair<MutationSpotITD,HashSet<Integer>> pair:mcs ){
        MutationSpotITD itd = pair.a();
        if(!par.filt.isGood(itd, index)){
            continue;
        }
        if(pair.b().size()>=par.itdFirstRunMinCount){
            int loc = itd.junction(index);
            ArrayList<Integer> prs = alnres.alignmentResult().getWTReadsForLocation(itd.gene(), loc, loc,pool,par);
            itd.setWTReads( prs ); 
            res.add(itd);
        }
    }
}

private MutationSpotITD toitd(String istr, int low, int high, String ins){
    IsoformStrand iso = index.getIsoformStrand(istr);
    int l = low+1;
    String seq = iso.sequence().substring(l, high);
    String seqins = iso.sas()=='s' ? ins+iso.sequence().substring(l, high) :
            iso.sequence().substring(l, high) + Nucleotide.reverseComplement(ins);
    //System.out.println( iso.uniqueID()+"  "+ iso.isForward());
    //System.out.println( iso.isForward()? seq : Nucleotide.reverseComplement(seq));
    //System.out.println(iso.location(l)+"  "+ iso.location(l+seq.length()-1));System.out.println();
    if(seq.isEmpty()){return null;}
    if(iso.isForward()){
        return iso.geneID().equals("KMT2A") ? 
                new MutationSpotPTD(iso.chr(), iso.location(l), iso.location(l+seq.length()-1), seqins, iso.geneID() ) :
                new MutationSpotITD(iso.chr(), iso.location(l), iso.location(l+seq.length()-1), seqins, iso.geneID() );
    }else{
        return iso.geneID().equals("KMT2A") ? 
                new MutationSpotPTD(iso.chr(),iso.location(l+seq.length()-1),iso.location(l),Nucleotide.reverseComplement(seqins), iso.geneID() ) :
                new MutationSpotITD(iso.chr(),iso.location(l+seq.length()-1),iso.location(l),Nucleotide.reverseComplement(seqins), iso.geneID() );
    }
}

public MutationResult result(){return res;}





public static void main(String[] args) throws Exception { //debug 
    DetectorHotspot.main(args);
    //ZZDebug.main(args);
}

}

