/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import prog.core.aln.mut.MutationResult;
import fork.lib.base.Print;
import fork.lib.base.collection.Pair;
import fork.lib.base.collection.Triplet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import prog.core.Param;
import prog.core.aln.res.AlignmentResult;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.mut.MutationFilter;
import prog.core.aln.mut.MutationSpot;
import prog.core.aln.mut.MutationSpotFusionRead;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class DetectorFusion extends Detector{

private HashMap<Pair<String,String>, HashMap<Pair<Integer,Integer>,HashSet<Read>>> ginds = new HashMap<>();
private MutationFilter filt = new MutationFilter();

    
    public DetectorFusion(AlignmentResult alnres, Index index, ReadPool pool, Param par){
        super(alnres,index,pool,par);
        for( Pair<String,String> gid:alnres.alignmentResultFusionRead().keySet() ){
            for( Pair<String,String> iso:alnres.alignmentResultFusionRead().get(gid).keySet() ){
                for( Pair<Integer,Anchor> p:alnres.alignmentResultFusionRead().get(gid).get(iso) ){
                    addRead(iso, pool.get(p.a()), p.b());
                }
            }
        }
    }

public void addRead(Pair<String,String> isos, Read read, Anchor an){
    //if(!read.sequence().equals("ATTTGAGTCATTTCCTTCGTACCCACAGTGCTTCATGAGAGATGCCAGCA")){return;}
    if(!an.ins.equals("")){ 
        //System.err.println(read.sequence()+"  "+an.ins);
        return;
    }
    IsoformStrand isoa = index.getIsoformStrand(isos.a());
    IsoformStrand isob = index.getIsoformStrand(isos.b());
    //if( isoa.sas()!=isob.sas() ){ return; }
    
    String ga = isoa.geneID(), gb= isob.geneID();
    int lind=an.rind-1, rind=an.lind+1;
    int lloc = isoa.location(lind), rloc = isob.location(rind);
    
    Pair<String,String> corp; 
    Pair<Integer,Integer> inds; 
    if(isoa.sas()=='s'){
        corp = new Pair<>(ga,gb);
        inds = new Pair<>(lloc,rloc); 
    }else{
        corp = new Pair<>(gb,ga);
        inds = new Pair<>(rloc,lloc); 
    }
    if(!ginds.containsKey(corp)){
        ginds.put(corp, new HashMap<>());
    }
    HashMap<Pair<Integer,Integer>,HashSet<Read>> map = ginds.get(corp);
    if(!map.containsKey(inds)){
        map.put(inds, new HashSet<>());
    }
    map.get(inds).add(read);
    /*
    System.out.println(read.sequence());
    System.out.println( lind+"-"+rind+"  "+isoa.geneID()+":"+isoa.chr()+":"+lloc+"-"+isob.geneID()+":"+isob.chr()+":"+rloc+"  "+ isoa.uniqueID()+" "+isob.uniqueID()  );
    System.out.println(isoa.sequence().subSequence(lind-14, lind+1)); 
    System.out.println(isob.sequence().subSequence(rind, rind+15));
    System.out.println();
    //System.exit(1);
    */
}

public void start()throws Exception { 
    ArrayList<Triplet<MutationSpotFusionRead,Integer,HashSet<Integer>>> mcs = new ArrayList<>();
    for( Pair<String,String> genes:ginds.keySet() ){
        for( Pair<Integer,Integer> inds:ginds.get(genes).keySet() ){
            MutationSpotFusionRead mut = new MutationSpotFusionRead( index.getGene(genes.a()).chr(),inds.a(),
                    index.getGene(genes.b()).chr(), inds.b(),genes.a(),genes.b());
            HashSet<String> uset = new HashSet<>();
            HashSet<Integer> rids = new HashSet<>();
            for( Read r:ginds.get(genes).get(inds) ){
                uset.add(r.sequence());
                rids.add(r.intID());
            }
            mcs.add( new Triplet<>( mut,uset.size(),rids ) );
        }
    }
    for( Triplet<MutationSpotFusionRead,Integer,HashSet<Integer>> trs:mcs ){
        MutationSpotFusionRead mut = trs.a();       
        if(!filt.isGood(mut,index)){
            continue;
        }
        if(trs.b()>=par.itdFirstRunMinCount){
            mut.setMutatedReads(trs.c());
            ArrayList<Integer> prsa = alnres.resread.getWTReadsForLocation(mut.gene1(),mut.low(),mut.low(),pool,par),
                    prsb = alnres.resread.getWTReadsForLocation(mut.gene2(),mut.high(),mut.high(),pool,par);
            mut.setWTReads(prsa);
            mut.setWTReads2(prsb);
            res.add(trs.a());
        }
    }
}

    
    
    

public static void main(String[] args) throws Exception { //debug 
    //ZZDebug.main(args);
    DetectorHotspot.main(args);
}
    
    
}
