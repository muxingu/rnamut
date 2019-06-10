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
public class CheckSeqManual {
    
public static void main(String[] args) throws Exception { //debug 
    String dir = "C:/muxingu/data/own/SangerSoftware2/file";
    String tar = "CAGATGAAGAAGTCAGAGTGCGAAGTCCCACAAGGTCTCCTTCAGGAAAAACCACCTCCGGTCAATAAGCAGGAGAATGCAGGCACTTT";
    
    String tarr = Nucleotide.reverseComplement(tar);
    String samp = "TCGA-AB-2948";
    String[] infns = {dir+"/"+samp+"_1.fq.gz", dir+"/"+samp+"_2.fq.gz"};
    Counter cc = new Counter(1000000);
    FastqWriter fwout = new FastqWriter(dir+"/PTD_manual_"+samp+".fq");
    FastqWriter fwa= new FastqWriter(dir+"/test1_"+samp+".fq.gz", true);
    FastqWriter fwb= new FastqWriter(dir+"/test2_"+samp+".fq.gz", true);
    
    FastqReader fr1= FileName.extension(infns[0]).equals("gz") ? new FastqReader(infns[0],true) : new FastqReader(infns[0]);
    FastqReader fr2= FileName.extension(infns[1]).equals("gz") ? new FastqReader(infns[1],true) : new FastqReader(infns[1]);
    FastqEntry en1, en2;
    while((en1=fr1.nextEntry())!=null){
        en2=fr2.nextEntry();
        cc.count();
        boolean ifw = false;
        if(tar.contains(en1.sequence()) || tarr.contains(en1.sequence()) ){
            fwout.write(en1); ifw=true;
        }
        if(tar.contains(en2.sequence()) || tarr.contains(en2.sequence()) ){
            fwout.write(en2); ifw=true;
        }
        if(ifw){
            fwa.write(en1);fwb.write(en2);
        }
    }
    fwout.close();
    fwa.close(); fwb.close();
    
}
    
}

