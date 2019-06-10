/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.base.Counter;
import fork.lib.base.collection.Pair;
import fork.lib.base.file.FileName;
import fork.lib.bio.seq.Nucleotide;
import fork.lib.bio.seq.parser.fastq.FastqEntry;
import fork.lib.bio.seq.parser.fastq.FastqReader;
import fork.lib.bio.seq.parser.fastq.FastqWriter;
import java.util.ArrayList;
import java.util.HashMap;
import prog.core.Param;
import prog.core.aln.Alignment;
import prog.core.aln.ReadAligner;
import prog.core.aln.read.Read;
import prog.core.aln.read.Seed;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class CheckSeqManualFusion {
    
    
private static Pair<Boolean,ArrayList<Integer>> exist(HashMap<String,ArrayList<Integer>> m, String tar){
    if(m==null){
        return new Pair<>(false,null);
    }
    for( String iso:m.keySet() ){
        if(iso.substring(0,tar.length()).equals(tar)){
            return new Pair<>(true,m.get(iso));
        }
    }
    return new Pair<>(false,null);
}
private static boolean cont(ArrayList<Integer> ins, HashMap<String,ArrayList<Integer>> m, String tar){
    Pair<Boolean,ArrayList<Integer>> ee = exist(m,tar);
    if(!ee.a()){ 
        return false; 
    }
    ArrayList<Integer> ins2 = ee.b();
    for( Integer ia:ins ){
        for( Integer ib:ins2 ){
            if(Math.abs(ia-ib)==10){
                return true;
            }
        }
    }
    return false;
}

private static boolean span(ArrayList<Seed> ss, Read rd, Index index, Param par, String tar1, String tar2)throws Exception { 
    try{
        HashMap<String,ArrayList<Integer>> m0 = ss.get(0).align(index, par);
        HashMap<String,ArrayList<Integer>> m1 = ss.get(1).align(index, par);
        HashMap<String,ArrayList<Integer>> m3 = ss.get(rd.seeds().size()-2).align(index, par);
        HashMap<String,ArrayList<Integer>> m4 = ss.get(rd.seeds().size()-1).align(index, par);
        Pair<Boolean,ArrayList<Integer>> exa= exist(m0,tar1), exb=exist(m4,tar2);
        if( exa.a() && exb.a()){
            if(cont(exa.b(),m1,tar1) && cont(exb.b(),m3,tar2)){ 
                return true;
            }
        }else{ 
            exa= exist(m4,tar1); exb=exist(m0,tar2);
            if(exa.a() && exb.a()){
                if(cont(exa.b(),m3,tar1) && cont(exb.b(),m1,tar2)){ 
                    return true;
                }
            }
        }
    }catch(Exception e){}
    return false;
}

    
    
    
public static void main(String[] args) throws Exception { //debug 
    String dir = "C:/muxingu/data/own/SangerSoftware2/file";
    Index index= IndexBuilder.read(dir+"/index.ind"); System.out.println("Index loaded. ");Param par = new Param();
    
    String samp = "TCGA-AB-2999";
    String tar1 = "PML", tar2="RARA";
    //String tar1 = "MYH11", tar2="CBFB";
    //String tar1 = "BCR", tar2="ABL1";
    //String tar1 = "NUP98", tar2="NSD1";
    //String tar1 = "KMT2A", tar2="AFDN";
    //String tar1 = "KMT2A", tar2="ELL";
    //String tar1 = "KMT2A", tar2="MLLT3";
    String[] infns = {dir+"/"+samp+"_1.fq.gz", dir+"/"+samp+"_2.fq.gz"};
    Counter cc = new Counter(1000000); cc.setIfTime(true);
    String tag = samp+"_"+tar1+"-"+tar2;
    FastqWriter fw = new FastqWriter(dir+"/manual-f_"+tag+".fq");
    FastqWriter fw1 = new FastqWriter(dir+"/manual-f_"+tag+"_1.fq");
    FastqWriter fw2 = new FastqWriter(dir+"/manual-f_"+tag+"_2.fq");
    FastqWriter fwa= new FastqWriter(dir+"/manual-fa_"+tag+"_1.fq.gz", true);
    FastqWriter fwb= new FastqWriter(dir+"/manual-fa_"+tag+"_2.fq.gz", true);
    par.ifAlignEnds=true;
    
    int i = 0;
    FastqReader fr1= FileName.extension(infns[0]).equals("gz") ? new FastqReader(infns[0],true) : new FastqReader(infns[0]);
    FastqReader fr2= FileName.extension(infns[1]).equals("gz") ? new FastqReader(infns[1],true) : new FastqReader(infns[1]);
    FastqEntry en1, en2;
    while((en1=fr1.nextEntry())!=null){
        en2 = fr2.nextEntry();
        cc.count();
        Read rd1 = new Read(i,en1,par); i++; Read rd2= new Read(i,en2,par); i++;
        ReadAligner rdln1 = new ReadAligner(rd1, index, par), rdln2 = new ReadAligner(rd2, index, par);
        ArrayList<Alignment> aln1 = rdln1.alignments(), aln2 = rdln2.alignments();
        /////////
        boolean sp1 = false, sp2 = false;
        if(aln1.isEmpty()){
            sp1 = span(rd1.seeds(),rd1,index,par,tar1,tar2);
            if(sp1){
                fw.write(en1);
            }
        }
        if(aln2.isEmpty()){
            sp2 = span(rd2.seeds(),rd2,index,par,tar1,tar2);
            if(sp2){
                fw.write(en2);
            }
        }
        if(sp1 || sp2){
            fwa.write(en1);
            fwb.write(en2);
        }
        /////////
        loop:
        for( Alignment a1:aln1 ){
            if(a1.isoformStrand().geneID().equals(tar1)){
                for( Alignment a2:aln2 ){
                    if(a2.isoformStrand().geneID().equals(tar2)){
                        fw1.write(en1); fw2.write(en2); break loop;
                    }
                }
            }else if(a1.isoformStrand().geneID().equals(tar2)){
                for( Alignment a2:aln2 ){
                    if(a2.isoformStrand().geneID().equals(tar1)){
                        fw1.write(en1); fw2.write(en2); break loop;
                    }
                }
            }
        }
    }
    fw.close(); fw1.close(); fw2.close(); fwa.close(); fwb.close();
}

}


