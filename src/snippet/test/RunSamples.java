/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet.test;

import fork.lib.base.file.FileName;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import prog.core.Param;
import prog.core.ProgramMain;
import prog.core.aln.hot.DetectorFusion;
import prog.core.aln.hot.DetectorFusionPair;
import prog.core.aln.hot.DetectorHotspot;
import prog.core.aln.hot.DetectorITD;
import prog.core.aln.hot.VafCorrector;
import prog.core.aln.mut.MutationResult;
import prog.core.aln.mut.MutationSpot;
import prog.core.aln.read.ReadPool;
import prog.core.aln.res.AlignmentResult;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class RunSamples {
    
public static void main(String[] args) throws Exception { //debug 
    File dir= new File("C:/muxingu/data/own/SangerSoftware2/file");
    Param par = new Param(); 
    Index index= IndexBuilder.read(dir+"/index.ind"); System.out.println("Index loaded. ");
    File[] fs = new File(dir+"/pool").listFiles();
    
    
    for( File f:fs ){
        if(!FileName.extension(f).equals("rds") || f.getName().contains("test")){
            continue;
        }
        String tag = FileName.baseName(f).replace("pool_", "");
        
        String alnpath = dir+"/pool/align_"+tag+".aln";
        String poolpath = dir+"/pool/pool_"+tag+".rds";

        ReadPool pool = ReadPool.read(poolpath); 
        System.out.println(tag);
        AlignmentResult res2 = AlignmentResult.read(alnpath,index,pool);

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
        
        MutationResult mutout= new MutationResult();
        for( MutationSpot mut:mres.allMutations() ){
            //if(!mut.aminoAcidMutation(index).equals("KMT2A.K1406-ELL.D46")){continue;}
            VafCorrector cor = new VafCorrector( mut, mut.reconstructedTranscripts( index, par.mostFrequencReadLength()), par );
            cor.build();
            cor.correct(res2,index,pool);
            mutout.addAll(cor.mutationResult());
        }
        MutationResult.writeToTxtFile(mutout,index,pool,dir+"/out_tcga/mut_correct/hotspot-corr_"+tag+".txt", false);
    }
    Summarise.main(args);
}
    
}
