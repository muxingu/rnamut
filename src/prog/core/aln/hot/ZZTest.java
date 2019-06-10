
package prog.core.aln.hot;

import fork.lib.base.collection.Pair;
import fork.lib.base.file.FileName;
import fork.lib.bio.seq.CodonTranslator;
import fork.lib.bio.seq.Nucleotide;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashMap;
import prog.core.Param;
import prog.core.aln.*;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.aln.res.AlignmentResult;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

public class ZZTest {

public static void main(String[] args) throws Exception { //debug 
    File dir= new File("C:/muxingu/data/own/SangerSoftware2/file");
    String tag = "test_TCGA-AB-2984";
    String[] infns = {dir+"/"+tag+"_1.fq.gz", dir+"/"+tag+"_2.fq.gz"};
    //String[] infns = {dir+"/test.fq",dir+"/test.fq"};
    
    //IndexBuilderFasta.main(args);
    Index index= IndexBuilder.read(dir+"/index.ind"); System.out.println("Index loaded. ");
    Writer wr = new PrintWriter(System.out);
    
    Param par = new Param(); 
    String alnpath = dir+"/pool/align_"+tag+".aln";
    String poolpath = dir+"/pool/pool_"+tag+".rds";
    
    Aligner alner = Aligner.AlignFastqPE(infns, poolpath, index, par, wr);
    AlignmentResult res = alner.result(); res.write(alnpath);
    
    
    
    
}
    
}
