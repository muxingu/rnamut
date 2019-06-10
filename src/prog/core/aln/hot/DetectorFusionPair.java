/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import fork.lib.base.collection.Pair;
import fork.lib.base.collection.Triplet;
import fork.lib.base.file.FileName;
import fork.lib.bio.seq.region.GenomicRegion;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import prog.core.Param;
import prog.core.aln.Alignment;
import prog.core.aln.mut.MutationSpotFusionPair;
import prog.core.aln.read.ReadPool;
import prog.core.aln.res.AlignmentResult;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class DetectorFusionPair extends Detector{
    
protected ArrayList<Triplet<String,String,Pileup>> pus = new ArrayList<>();
protected HashSet<String> wtgenes = new HashSet<>();
protected Pileup puwt = new Pileup();

    
    public DetectorFusionPair(AlignmentResult alnres, Index index, ReadPool pool, Param par){
        super(alnres,index,pool,par);
    }
    
    
protected void addAlignments(String ga, String gb, Pileup pu){ 
    for( Pair<Alignment,Alignment> as:alnres.resfpair.getAlignmentsForGeneTranslocation(ga, gb)){
        pu.addAlignment(as.a());
        pu.addAlignment(as.b());
    }
    if(!wtgenes.contains(ga)){
        for( Alignment aln:alnres.resread.getAlignmentsForGene(ga)){
            puwt.addAlignment(aln);
        }
        wtgenes.add(ga);
    }
    if(!wtgenes.contains(gb)){
        for( Alignment aln:alnres.resread.getAlignmentsForGene(gb)){
            puwt.addAlignment(aln);
        }
        wtgenes.add(gb);
    }
}

public void start(){
    for( HashSet<String> k:par.correctPairs.keySet() ){
        Pileup pu = new Pileup();
        Pair<String,String> pair = par.correctPairs.get(k);
        String ga = pair.a(), gb=pair.b();
        addAlignments(ga,gb,pu);
        startPair(ga,gb,pu);
        pus.add( new Triplet<>(ga,gb,pu) ); 
    }
}

private void startPair(String ga, String gb, Pileup pu){
    Pair<Double,Double> csa = covs(ga,pu);
    Pair<Double,Double> csb = covs(gb,pu);
    if(csa.a()>=par.fusionPEMinCount || csb.a()>=par.fusionPEMinCount){
        MutationSpotFusionPair mut = new MutationSpotFusionPair(
                index.getGene(ga).chr(), ga, csa.a(), csa.b(),
                index.getGene(gb).chr(), gb, csb.a(), csb.b()
        );
        double v = mut.maxVaf();
        res.add(mut);
    }
}

private Pair<Double,Double> covs(String gid, Pileup pm){
    GenomicRegion gene = index.getGene(gid).toGenomicRegion();
    ArrayList<GenomicRegion> grs = pm.coveredRegionsForGene(gene);
    double covm = Math.ceil( pm.baseCount(grs)/par.mostFrequencReadLength() );
    double covwt = Math.ceil( puwt.baseCount(grs)/par.mostFrequencReadLength() );
    return new Pair<>(covm, covwt);
}

public void writeWigFiles(String odir, String tag)throws Exception { 
    for( Triplet<String,String,Pileup> pdat:pus ){
        String ga = pdat.a(), gb = pdat.b();
        Pileup pu = pdat.c();
        String tag_ = tag+"_"+ga+"-"+gb;
        pu.writeToWig(odir+"/fus_"+tag_+".wig");
    }
    puwt.writeToWig(odir+"/wt_"+tag+".wig");
}


public static void main(String[] args) throws Exception { //debug 
    DetectorHotspot.main(args);
}
    
    
}

