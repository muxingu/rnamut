/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.bio.seq.parser.fastq.FastqEntry;
import fork.lib.bio.seq.parser.fastq.FastqWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import prog.core.Param;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.aln.res.AlignmentResult;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class WriteReadPool {
    
public static void main(String[] args) throws Exception { //debug 
    
    File dir= new File("C:/muxingu/data/own/SangerSoftware2/file");
    String tag = "TCGA-AB-2844";
    
    //IndexBuilderFasta.main(args);
    String poolpath = dir+"/pool/pool_"+tag+".rds";
    ReadPool pool = ReadPool.read(poolpath); System.out.println("Pool Read.");
    
    
    FastqWriter fw = new FastqWriter(dir+"/pool_"+tag+".fq");
    for( Integer i:pool.keySet() ){
        Read r= pool.get(i);
        fw.write(new FastqEntry(r.id(),r.sequence(),"",r.quality()));
    }
    fw.close();
    
}

    
}
