/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.base.Counter;
import fork.lib.base.file.FileName;
import fork.lib.bio.seq.Nucleotide;
import fork.lib.bio.seq.parser.fastq.FastqEntry;
import fork.lib.bio.seq.parser.fastq.FastqReader;
import fork.lib.bio.seq.parser.fastq.FastqWriter;

/**
 *
 * @author mg31
 */
public class CheckSeqManualPTD {
    
public static int sidemin = 12, sidemax = 15;
    
    
public static void main(String[] args) throws Exception { //debug 
    String dir = "C:/muxingu/data/own/SangerSoftware2/file";
    String tar1 = "AAAATTCCAGCAGATGGAGTCCACAGGATCAGAGTGGACTTTAAG";
    String tar2 = "GATGAGCAATTCTTAGGTTTTGGCTCAGATGAAGAAGTCAGAGTG";
    String tarr1 = Nucleotide.reverseComplement(tar1);
    String tarr2 = Nucleotide.reverseComplement(tar2);
    String samp = "TCGA-AB-2948";
    String[] infns = {dir+"/"+samp+"_1.fq.gz", dir+"/"+samp+"_2.fq.gz"};
    Counter cc = new Counter(1000000); cc.setIfTime(true);
    FastqWriter fw= new FastqWriter(dir+"/manual-pg_"+samp+".fq", false);
    
    for( String inf:infns ){
        FastqReader fr= FileName.extension(inf).equals("gz") ? new FastqReader(inf,true) : new FastqReader(inf);
        FastqEntry en;
        while((en=fr.nextEntry())!=null){
            cc.count();
            String seq = en.sequence();
            if(tar1.contains(seq.substring(0,sidemax))){
                if(tar2.contains(seq.substring(seq.length()-sidemin))){
                    fw.write(en);
                }
            }else if(tarr1.contains(seq.substring(0,sidemax))){
                if(tarr2.contains(seq.substring(seq.length()-sidemin))){
                    fw.write(en);
                }
            }else if(tar2.contains(seq.substring(0,sidemax))){
                if(tar1.contains(seq.substring(seq.length()-sidemin))){
                    fw.write(en);
                }
            }else if(tarr2.contains(seq.substring(0,sidemax))){
                if(tarr1.contains(seq.substring(seq.length()-sidemin))){
                    fw.write(en);
                }
            }
        }
    }
    fw.close(); 
    
}
    
}
