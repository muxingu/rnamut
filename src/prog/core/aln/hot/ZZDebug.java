/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import prog.core.aln.mut.MutationResult;
import fork.lib.base.collection.Pair;
import fork.lib.base.file.FileName;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import prog.core.Param;
import prog.core.aln.Aligner;
import prog.core.aln.res.AlignmentResult;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;
import prog.core.index.IndexBuilderFasta;

/**
 *
 * @author mg31
 */
public class ZZDebug {
    
public static void main(String[] args) throws Exception { //debug 
    File dir= new File("C:\\muxingu/data/own/SangerSoftware2/file");
    
    String tag = "test_TCGA-AB-2984";
    String[] infns = {dir+"/"+tag+"_1.fq.gz", dir+"/"+tag+"_2.fq.gz"};
    
    String pp = dir+"/pool/pool_"+tag+"-test.rds";
    
    //IndexBuilderFasta.main(args);
    Index index= IndexBuilder.read(dir+"/index.ind"); System.out.println("Index loaded. ");Param par = new Param();
    Writer wr = new PrintWriter(System.out);
    
    Aligner alner = infns.length==2 ? Aligner.AlignFastqPE(infns,pp,index,par,wr) : Aligner.AlignFastqSE(infns,pp,index,par,wr);
    
    ReadPool pool = ReadPool.read(pp);
    AlignmentResult res = alner.result(); res.write(dir+"/align_"+tag+"-test.aln");
    
    DetectorHotspot hs = new DetectorHotspot( res,index,pool,par );
    hs.start();
    DetectorITD dd = new DetectorITD(res,index,pool,par);
    dd.addAlignmentResult(res);
    dd.start(); 
    
    DetectorFusion ff = new DetectorFusion(res,index,pool,par);
    ff.start();

    MutationResult mres = new MutationResult();
    mres.addAll(hs.result());
    mres.addAll(dd.result());
    mres.addAll(ff.result());
    mres.write( dir+"/hotspot_"+tag+"-test.mut" );
    MutationResult.writeToTxtFile(mres, index, pool, dir+"/hotspot_"+tag+"-test.txt", false );
    
    wr.close();
}
    
}
