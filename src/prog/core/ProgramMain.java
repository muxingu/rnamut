/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core;

import fork.lib.base.collection.Pair;
import fork.lib.base.file.FileName;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import prog.core.aln.Aligner;
import prog.core.aln.res.AlignmentResult;
import prog.core.aln.hot.Anchor;
import prog.core.aln.hot.DetectorFusion;
import prog.core.aln.hot.DetectorFusionPair;
import prog.core.aln.hot.DetectorHotspot;
import prog.core.aln.hot.DetectorITD;
import prog.core.aln.mut.MutationResult;
import prog.core.aln.mut.MutationSpot;
import prog.core.aln.hot.VafCorrector;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class ProgramMain {
    
private Writer wr;
private String dir, outdir, tag;
private String[] infs;
private Index index;
private Param par;

protected String outalndir, outmutdir, outcordir, outtestdir, 
        poolpath, alnpath, mutpath, corpath, cortxtpath;


    public ProgramMain(Writer wr, String dir, String[]infs, String outdir, String tag, Index index, Param par){
        this.wr=wr;
        this.dir=dir;
        this.outdir=outdir;
        this.tag=tag;
        this.infs=infs;
        this.index=index;
        this.par=par;
        if(this.par==null){ this.par = new Param();}
        // fs
        mkdirs(outdir);
        outalndir = outdir+"/align"; mkdirs(outalndir);
        outcordir = outdir+"/mut_correct"; mkdirs(outcordir);
        outmutdir = outdir+"/mut_init"; mkdirs(outmutdir);
        outtestdir = outdir+"/test"; mkdirs(outtestdir);
        
        poolpath = outalndir+"/pool_"+FileName.baseName(tag)+".rds";
        alnpath = outalndir+"/align_"+FileName.baseName(tag)+".aln";
        mutpath = outmutdir+"/hotspot_"+tag+".mut";
        corpath = outmutdir+"/hotspot-corr_"+tag+".mut";
        cortxtpath = outcordir+"/hotspot-corr_"+tag+".txt";
    }
    public ProgramMain(String dir, String[]infs, String outdir, String tag, Index index, Param par){
        this(null,dir,infs,outdir,tag,index,par);
    }
    
public String dirOutaln(){return outalndir;}
public String dirOutmut(){return outmutdir;}
public String dirOutcorr(){return outcordir;}
public String pathPool(){return poolpath;}
public String pathAln(){return alnpath;}
public String pathMut(){return mutpath;}
public String pathCorr(){return corpath;}
public void setWriter(Writer wr){this.wr=wr;}

private static void mkdirs(String dir){
    File dirf = new File(dir);
    if(!dirf.exists()){
        dirf.mkdirs();
    }
}

public void writeln(String l)throws Exception { 
    wr.write(l+"\n"); wr.flush();
}
public void close()throws Exception { 
    wr.close();
}
    
public void align(String[] infs, String poolpath, String alnpath, Index index, Param par )throws Exception { 
    Aligner alner = Aligner.AlignFastqPE(infs,poolpath,index,par, wr);
    //Aligner alner = Aligner.AlignPool(poolpath,index,par);
    AlignmentResult res = alner.result(); res.write(alnpath);
    writeln("Align done!");
}

public void callMutation(String poolpath, String alnpath, String mutpath, Index index, Param par )throws Exception { 
    ReadPool pool = ReadPool.read(poolpath);
    AlignmentResult res = AlignmentResult.read(alnpath,index,pool);
    
    DetectorHotspot hs = new DetectorHotspot( res,index,pool,par );
    hs.start();
    DetectorITD dd = new DetectorITD(res,index,pool,par);
    dd.start(); 
    DetectorFusion ff = new DetectorFusion(res,index,pool,par);
    ff.start();
    DetectorFusionPair fp= new DetectorFusionPair(res,index,pool,par);
    fp.start();
    fp.writeWigFiles(outtestdir, tag);

    MutationResult mres = new MutationResult();
    mres.addAll(hs.result());
    mres.addAll(dd.result());
    mres.addAll(ff.result());
    mres.addAll(fp.result());
    mres.write( mutpath );
    writeln("Call mutation done!");
}
    
public void correct(String poolpath, String aln, String muts, String outmut, String outmutxt, Index index)throws Exception { 
    ReadPool pool = ReadPool.read(poolpath);
    AlignmentResult res = AlignmentResult.read(aln, index, pool);
    MutationResult allmuts = MutationResult.read(muts);

    MutationResult mutout= new MutationResult();
    for( MutationSpot mut:allmuts.allMutations() ){
        VafCorrector cor = new VafCorrector( mut, mut.reconstructedTranscripts(index, par.mostFrequencReadLength()), par );
        cor.build();
        cor.correct(res, index, pool);
        mutout.addAll(cor.mutationResult());
    }
    mutout.write(outmut);
    MutationResult.writeToTxtFile(mutout,index,pool,outmutxt,false);
    writeln("Correction done!");
}

public void start()throws Exception { 
    align(infs,poolpath,alnpath,index,par);
    callMutation(poolpath,alnpath,mutpath,index,par);
    correct(poolpath,alnpath,mutpath,corpath,cortxtpath,index);
}

    
    
    
public static void main(String[] args) throws Exception { //debug 
    String dir=args[0], infstr=args[1], outdir=args[2], tag=args[3];
    /*
    String dir="C:\\muxingu\\data\\own\\SangerSoftware2",
            infstr=dir+"/file/TCGA-AB-2948_1.fq.gz,"+dir+"/file/TCGA-AB-2948_2.fq.gz",
            outdir=dir+"/temp/TCGA-AB-2948", tag="TCGA-AB-2948";
    */
    String[] infs = infstr.split(",");
    for( String inf:infs ){
        if(! new File(inf).exists() ){
            System.out.println("File not exist: "+ inf);
            System.exit(1);
        }
    }
    Index index= IndexBuilder.read(dir+"/index.ind"); System.out.println("Index loaded. ");
    Param par = new Param();
    
    ProgramMain main = new ProgramMain( new PrintWriter(System.out),dir,infs,outdir,tag,index,par);
    main.start();
    
}
    
}
