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
public class CheckSeqID {
    
    
public static void main(String[] args) throws Exception { //debug 
    String dir = "C:/muxingu/data/own/SangerSoftware2/file";
    String[] tars = new String[]{
        "SOLEXA10_1:7:76:8226:2503",
        "SOLEXA3_1:7:113:19834:11431",
        "SOLEXA3_1:7:69:1475:18778",
        "SOLEXA10_1:7:83:14680:20243",
        "SOLEXA10_1:7:88:4199:10015",
        "SOLEXA3_1:7:116:14306:11956",
    };
    
    String samp = "TCGA-AB-2984";
    String[] infns = {dir+"/"+samp+"_1.fq.gz", dir+"/"+samp+"_2.fq.gz"};
    Counter cc = new Counter(1000000);
    FastqWriter fwout = new FastqWriter(dir+"/ID_manual_"+samp+".fq");
    FastqWriter fwa= new FastqWriter(dir+"/test_"+samp+"_1.fq.gz", true);
    FastqWriter fwb= new FastqWriter(dir+"/test_"+samp+"_2.fq.gz", true);
    
    FastqReader fr1= FileName.extension(infns[0]).equals("gz") ? new FastqReader(infns[0],true) : new FastqReader(infns[0]);
    FastqReader fr2= FileName.extension(infns[1]).equals("gz") ? new FastqReader(infns[1],true) : new FastqReader(infns[1]);
    FastqEntry en1, en2;
    while((en1=fr1.nextEntry())!=null){
        en2=fr2.nextEntry();
        cc.count();
        boolean ifw = false;
        for( String tar:tars ){
            if( en1.ID().contains(tar) ){
                fwout.write(en1); ifw=true;
            }
            if( en2.ID().contains(tar) ){
                fwout.write(en2); ifw=true;
            }
            if(ifw){
                fwa.write(en1);fwb.write(en2);
                break;
            }
        }
        
    }
    fwout.close();
    fwa.close(); fwb.close();
    
}

    
}
