/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import prog.core.aln.mut.MutationResult;
import prog.core.aln.mut.MutationSpot;
import fork.lib.base.collection.Pair;
import fork.lib.base.file.FileName;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import prog.core.Param;
import prog.core.aln.res.AlignmentResult;
import prog.core.aln.Aligner;
import prog.core.aln.Alignment;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.read.Read;
import prog.core.aln.mut.AlignedElement;
import prog.core.aln.mut.Mutation;
import prog.core.aln.mut.MutationFilter;
import prog.core.aln.read.ReadPool;
import prog.core.aln.read.Seed;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class DetectorHotspot extends Detector{

private HashMap<MutationSpot,ArrayList<Integer>> mutrs = new HashMap<>();
private MutationFilter filt = new MutationFilter();



    public DetectorHotspot(AlignmentResult alnres, Index index, ReadPool pool, Param par) throws Exception { 
        super(alnres,index,pool,par);
        for( String gid:alnres.alignmentResult().keySet() ){
            for( Alignment aln:alnres.alignmentResult().getAlignmentsForGene(gid) ){
                addAlignment(aln);
            }
        }
    }
    
    
public void addAlignment(Alignment aln)throws Exception { 
    if(aln==null){ return; }
    for( AlignedElement e:aln.elements() ){
        if(e instanceof Mutation){
            Mutation m = (Mutation)e;
            if( m.quality(aln.getRead(pool))>par.quality ){ continue; }
            MutationSpot ms = m.mutationSpot(aln.isoformStrand());
            if(!mutrs.containsKey(ms)){ 
                mutrs.put(ms, new ArrayList<>());
            }
            mutrs.get(ms).add(aln.getReadID());
        }
    }
}

public void addMutationSpot(MutationSpot m, ArrayList<Integer> rs){
    if(!mutrs.containsKey(m)){ 
        mutrs.put(m, new ArrayList<>()); 
    }
    mutrs.get(m).addAll(rs);
}

public void start()throws Exception { 
    for( MutationSpot mut:mutrs.keySet() ){
        if(!par.targetGenes.contains(mut.gene())){
            continue;
        }
        if(!filt.isGood(mut,index)){
            continue;
        }
        ArrayList<Integer> mutReads = mutrs.get(mut); 
        ArrayList<Integer> wtReads = alnres.alignmentResult().getWTReadsForLocation(mut.gene(),mut.low(),mut.high(),pool,par);
        int mutn = mutReads.size(), wtn = wtReads.size();
        double vaf = (double) mutn/(mutn+wtn);
        if( mutn>=par.hsFirstRunMinCount && vaf>par.hsFirstRunMinVAF ){
            mut.setMutatedReads(mutReads);
            mut.setWTReads(wtReads);
            res.add(mut);
        }
    }
}






public static void main(String[] args) throws Exception { //debug 
    //String infa = args[0], infb = args[1], outf = args[2];
    //File dir = new File("/nfs/users/nfs_m/mg31/data/own/soft/20181022_test/dist");
    //String[] infns = {infa, infb};
    
    File dir= new File("C:/muxingu/data/own/SangerSoftware2/file");
    String tag = "TCGA-AB-2948";
    String[] infns = {dir+"/"+tag+"_1.fq.gz", dir+"/"+tag+"_2.fq.gz"};
    //String[] infns = {dir+"/sim_npm1.fq.gz"};
    //String[] infns = {dir+"/sim_flt3.fq.gz"};
    
    //IndexBuilderFasta.main(args);
    Index index= IndexBuilder.read(dir+"/index.ind"); System.out.println("Index loaded. ");
    Writer wr = new PrintWriter(System.out);
    
    Param par = new Param(); 
    String alnpath = dir+"/pool/align_"+tag+".aln";
    String poolpath = dir+"/pool/pool_"+tag+".rds";
    
    //Aligner alner = Aligner.AlignPool(poolpath,index,par); System.out.println("Aligned.");
    //Aligner alner = infns.length==2 ? Aligner.AlignFastqPE(infns,poolpath,index,par,wr) : Aligner.AlignFastqSE(infns,poolpath,index,par,wr);
    //AlignmentResult res = alner.result(); res.write(alnpath);
    
    ReadPool pool = ReadPool.read(poolpath); System.out.println("Pool Read.");
    AlignmentResult res2 = AlignmentResult.read(alnpath,index,pool);
    for( Alignment aa: res2.alignmentResult().getAlignmentsForGene("IDH1") ){
        Read r= aa.getRead(pool);
        if(r.id().contains("SOLEXA3_1:7:113:19834:11431")){
            aa.print();
            System.out.println();
        }
    }
    
    DetectorHotspot hs = new DetectorHotspot( res2, index, pool, par );
    hs.start();
    DetectorITD dd = new DetectorITD(res2,index,pool,par);
    dd.start(); 
    DetectorFusion ff = new DetectorFusion(res2,index,pool,par);
    ff.start(); 
    DetectorFusionPair fp= new DetectorFusionPair(res2,index,pool,par);
    fp.start();
    
    MutationResult mres = new MutationResult();
    mres.addAll(hs.result());
    mres.addAll(dd.result());
    mres.addAll(ff.result());
    mres.addAll(fp.result());
    MutationResult.writeToTxtFile(mres, index, pool, dir+"/hotspot_"+tag+".txt", false );
    mres.write( dir+"/hotspot_"+tag+".mut" );

    VafCorrector.main(new String[]{tag});

}


}

