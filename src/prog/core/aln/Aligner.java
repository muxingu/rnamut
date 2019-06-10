/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln;

import prog.core.aln.res.AlignmentResult;
import fork.lib.base.Counter;
import fork.lib.base.collection.Pair;
import prog.core.aln.read.Read;
import prog.core.Param;
import fork.lib.base.file.FileName;
import fork.lib.bio.seq.parser.fastq.FastqEntry;
import fork.lib.bio.seq.parser.fastq.FastqReader;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.hot.Anchor;
import prog.core.aln.hot.DetectorHotspot;
import prog.core.aln.hot.ZZDebug;
import prog.core.aln.hot.ZZTest;
import prog.core.aln.read.ReadPool;
import prog.core.aln.read.ReadPoolWriter;
import prog.core.aln.read.Seed;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class Aligner {
    
    
protected Index index;
protected Param par;
private AlignmentResult res = new AlignmentResult();
    
    
    public Aligner(Index index, Param par){
        this.index=index;
        this.par=par;
        if(this.par==null){
            this.par = new Param();
        }
    }
    public Aligner(Index index){
        this(index, null);
    }


public AlignmentResult result(){return res;}
    

public static Aligner AlignFastqSE(String[] infns, String poolpath, Index index, Param par, Writer tawriter)throws Exception { 
    ReadPoolWriter fw = new ReadPoolWriter(poolpath);
    Aligner alner = new Aligner(index,par);
    Counter cc = new Counter(1000000, "", " single-end reads mapped... ", tawriter);
    int intid = 0;
    for(String infn:infns){
        FastqReader fr= FileName.extension(infn).equals("gz") ? new FastqReader(infn,true) : new FastqReader(infn);
        FastqEntry en;
        while((en=fr.nextEntry())!=null){
            intid++;
            Read rd= new Read( intid, en, par ); 
            par.readlens.add(rd.length());
            if(par.ifVerbose){ cc.count(); }
            if(rd.length()<par.minReadLength){ 
                continue; 
            }
            ReadAligner rdalner = new ReadAligner(rd,index,par);
            Alignment aln = rdalner.bestAlignment();
            addToResult(alner, new Alignment[]{aln}, new ReadAligner[]{rdalner}, index, par, fw);
        }
        fr.close();
    }
    fw.close(); 
    return alner;
}



public static Aligner AlignFastqPE(String[] infns, String poolpath, Index index, Param par, Writer tawriter)throws Exception { 
    ReadPoolWriter fw = new ReadPoolWriter(poolpath);
    Aligner alner = new Aligner(index,par);
    FastqReader fr1= FileName.extension(infns[0]).equals("gz") ? new FastqReader(infns[0],true) : new FastqReader(infns[0]);
    FastqReader fr2= FileName.extension(infns[1]).equals("gz") ? new FastqReader(infns[1],true) : new FastqReader(infns[1]);
    FastqEntry en1, en2;
    int id=-1;
    Counter cc = new Counter(1000000, "", " paired-end reads mapped... ", tawriter, true);
    while((en1=fr1.nextEntry())!=null){
        id+=2;
        if(par.ifVerbose){ cc.count(); }
        en2 = fr2.nextEntry();
        Read rd1= new Read( id,en1,par); if(rd1.length()<par.minReadLength){  continue;  } 
        Read rd2= new Read( id+1,en2,par ); if(rd2.length()<par.minReadLength){  continue;  }
        par.readlens.add(rd1.length()); par.readlens.add(rd2.length());
        ReadAligner rdalner1 = new ReadAligner(rd1,index,par), rdalner2 = new ReadAligner(rd2,index,par);
        ArrayList<Alignment> alns1= rdalner1.alignments(), alns2 = rdalner2.alignments();
        Pair<Alignment,Alignment> bpair = bestPair( alns1, alns2, par);
        if( bpair==null){
            if(addToPairedTranslocation(alner,alns1,alns2,rd1,rd2,par,fw)){ continue; }
            if(rdalner1.bestAlignment()==null){ 
                if(!addToResultITD(alner,rd1,rdalner1,index,par,fw)){
                    addToReadTranslocation(alner,rd1,rdalner1,index,par,fw,true);
                }
            }
            if(rdalner2.bestAlignment()==null){ 
                if(!addToResultITD(alner,rd2,rdalner2,index,par,fw)){
                    addToReadTranslocation(alner,rd2,rdalner2,index,par,fw,true);
                }
            }
            continue; 
        }
        
        // pair
        Alignment aln1 = bpair.a(), aln2 = bpair.b();
        if(aln1!=null && aln2!=null){
            addToResult(alner, new Alignment[]{aln1,aln2}, new ReadAligner[]{rdalner1, rdalner2}, index, par, fw);
        }
        
    }
    fw.close(); fr1.close(); fr2.close();
    return alner;
}


public static Aligner AlignPool(String poolpath, Index index, Param par)throws Exception { 
    ReadPool pool = ReadPool.read(poolpath);
    Aligner alner = new Aligner(index,par);
    for( Integer intid:pool.keySet() ){
        Read rd= pool.get(intid); 
        ReadAligner rdalner = new ReadAligner(rd,index,par);
        Alignment aln = rdalner.bestAlignment();
        addToResult(alner, new Alignment[]{aln}, new ReadAligner[]{rdalner}, index, par, null, false);
    }
    return alner;
}

private static void addToResult(Aligner alner, Alignment[] alns, ReadAligner[] rdalners, Index index, Param par, ReadPoolWriter fw)throws Exception { 
    addToResult(alner,alns,rdalners,index,par,fw,true);
}
private static void addToResult(Aligner alner, Alignment[] alns, ReadAligner[] rdalners, Index index, Param par, ReadPoolWriter fw, boolean ifw)throws Exception { 
    for( int i=0; i<alns.length; i++ ){
        Alignment aln = alns[i]; ReadAligner rdalner= rdalners[i]; Read rd = rdalner.read();
        if(aln!=null){
            alner.result().alignmentResult().addAlignment( aln );
            if(ifw){ fw.write(rd); }
        }else if(!addToResultITD(alner,rd,rdalner,index,par,fw, ifw)){
            addToReadTranslocation(alner,rd,rdalner,index,par,fw,ifw);
        }
    }
}
private static boolean addToResultITD(Aligner alner, Read rd, ReadAligner rdalner, Index index, Param par, ReadPoolWriter fw)throws Exception { 
    return addToResultITD(alner,rd,rdalner,index,par,fw,true);
}
private static boolean addToResultITD(Aligner alner, Read rd, ReadAligner rdalner, Index index, Param par, ReadPoolWriter fw, boolean ifw)throws Exception { 
    HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> ilocs = rdalner.iss();
    int n = -1; IsoformStrand iso=null; ArrayList<Pair<Seed,ArrayList<Integer>>> islocs=null;
    for( String is:ilocs.keySet() ){
        int s = ilocs.get(is).size();
        if(s>n){
            n=s; islocs = ilocs.get(is); 
            iso=index.getIsoformStrand(is);
        }
    }
    ///
    if(islocs!=null && islocs.size()>=2 ){
        String isostr = iso.uniqueID();
        Anchor an = new Anchor(islocs, index, isostr, rd,par);
        if( an.firstSize>=par.itdMapMinConsecutiveSeeds || an.lastSize>=par.itdMapMinConsecutiveSeeds ){
            if(par.itdGenes.contains(iso.geneID())){
                alner.result().alignmentResultITD().addReadITD(iso.geneID(),isostr, rd, an);
                if(ifw){ 
                    fw.write(rd); 
                }
                return true;
            }
        }
    }
    return false;
}

private static boolean addToPairedTranslocation(Aligner alner, ArrayList<Alignment> alns1,
        ArrayList<Alignment> alns2, Read rd1, Read rd2, Param par, ReadPoolWriter fw)throws Exception { 
    loop:
    for( Alignment a1:alns1 ){
        for( Alignment a2:alns2 ){
            Pair<String,String> gpair= new Pair<>(a1.isoformStrand().geneID(),a2.isoformStrand().geneID());
            if(par.allPairPermutations.contains(gpair)){
                alner.result().alignmentResultFusionPair().addAlignmentsTranslocation(a1, a2);
                fw.write(rd1);fw.write(rd2);
                return true;
            }
        }
    }
    return false;
}

private static boolean addToReadTranslocation(Aligner alner, Read rd, ReadAligner rdalner, 
        Index index, Param par, ReadPoolWriter fw, boolean ifw)throws Exception { 
    ArrayList<Pair<Seed,HashMap<String,ArrayList<Integer>>>> smap = rdalner.seedmaps();
    if( smap.get(0).b()==null || smap.get(smap.size()-1).b()==null ){
        return false;
    }
    /// find all gene pairs at ends
    ArrayList<Pair<String,String>> gpairs = new ArrayList<>();
    ArrayList<Pair<String,String>> isopairs = new ArrayList<>();
    for( String isostr:smap.get(0).b().keySet() ){
        String gid = index.getIsoformStrand(isostr).geneID();
        if(par.transPairs.containsKey(gid)){
            for( String isostr2:smap.get(smap.size()-1).b().keySet() ){
                String gid2 = index.getIsoformStrand(isostr2).geneID();
                if(par.transPairs.get(gid).contains(gid2)){
                    gpairs.add( new Pair<>(gid,gid2) );
                    isopairs.add( new Pair<>(isostr,isostr2) );
                }
            }
        }
    }
    /// extend each
    ArrayList< Pair<ArrayList<Pair<Seed,ArrayList<Integer>>>,ArrayList<Pair<Seed,ArrayList<Integer>>>> > islocss= new ArrayList<>();
    for( Pair<String,String> gp:gpairs ){
        ArrayList<Pair<Seed,ArrayList<Integer>>> islocsa = new ArrayList<>(), islocsb = new ArrayList<>();
        addtotail(smap,islocsa,gp.a(),index);
        addtotail(smap,islocsb,gp.b(),index);
        islocss.add( new Pair<>(islocsa,islocsb) );
    }
    /// anchor
    for( int i=0; i<gpairs.size(); i++ ){
        Pair<String,String> ip = isopairs.get(i);
        Pair<ArrayList<Pair<Seed,ArrayList<Integer>>>,ArrayList<Pair<Seed,ArrayList<Integer>>>> islocs2 = islocss.get(i);
        Anchor an = new Anchor(islocs2.a(),islocs2.b(), index, ip.a(), ip.b(), rd,par);
        if( an.firstSize>=par.itdMapMinConsecutiveSeeds || an.lastSize>=par.itdMapMinConsecutiveSeeds ){
            alner.result().alignmentResultFusionRead().addReadTranslocation(index.getIsoformStrand(ip.a()), index.getIsoformStrand(ip.b()), rd, an);
            //System.out.println(ip.a()+":"+an.lind+"  "+ip.b()+":"+an.rind+"  "+an.firstSize+" "+an.lastSize+" "+an.ins);
            //System.out.println(rd.sequence()); System.out.println();
            if(ifw){ 
                fw.write(rd); 
            }
            return true;
        }
    }
    return false;
}

private static void addtotail(ArrayList<Pair<Seed,HashMap<String,ArrayList<Integer>>>> smap, 
        ArrayList<Pair<Seed,ArrayList<Integer>>> islocs, String tar, Index index){
    for( Pair<Seed,HashMap<String,ArrayList<Integer>>> slocs:smap ){
        ArrayList<Integer> locs = new ArrayList<>();
        if(slocs.b()!=null){
            for( String isostr:slocs.b().keySet() ){
                String gid = index.getIsoformStrand(isostr).geneID();
                if(gid.equals(tar)){
                    locs.addAll( slocs.b().get(isostr) );
                }
            }
        }
        islocs.add( new Pair<>(slocs.a(),locs) );
    }
}

private static Pair<Alignment,Alignment> bestPair(ArrayList<Alignment>alnsa, ArrayList<Alignment>alnsb, Param par)throws Exception { 
    if(alnsa==null || alnsb==null){
        return null;
    }
    ArrayList<Pair<Alignment,Alignment>> ps = new ArrayList<>();
    for( Alignment alna:alnsa ){
        for( Alignment alnb:alnsb ){
            if(IsoformStrand.isPair(alna.isoformStrand(), alnb.isoformStrand())){
                Region ra = alna.span(), rb = alnb.span();
                int l = alnb.isoformStrand().sequence().length();
                Region rbr = new Region(l-rb.high(),l-rb.low());
                double frag = ra.distanceFrom(rbr)+ra.getRange()+rb.getRange();
                if(frag< par.peMaxFragmentSize){
                    ps.add(new Pair<>(alna,alnb));
                }
            }
        }
    }
    Collections.sort(ps, new Comparator<Pair<Alignment,Alignment>>(){
        public int compare(Pair<Alignment, Alignment> o1, Pair<Alignment, Alignment> o2) {
            return Double.compare(o1.a().mismatchScore()+o1.b().mismatchScore(), 
                    o2.a().mismatchScore()+o2.b().mismatchScore());
        }
    });
    return ps.isEmpty() ? null : ps.get(0);
}







public static void main(String[] args) throws Exception { //debug 
    //DetectorHotspot.main(args);
    ZZTest.main(args);
}


}
