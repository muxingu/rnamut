/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.read;

import fork.lib.bio.seq.parser.fastq.FastqEntry;
import fork.lib.bio.seq.parser.fastq.FastqReader;
import java.util.HashMap;
import prog.core.Param;

/**
 *
 * @author mg31
 */
public class ReadPool extends HashMap<Integer,Read>{
    
    
    public ReadPool(){}
    
    
public static ReadPool read(String path)throws Exception { 
    Param par = new Param();
    ReadPool ret = new ReadPool();
    FastqReader fr = new FastqReader(path,true);
    FastqEntry en;
    while( (en=fr.nextEntry())!=null ){
        String[] ss= en.ID().split("\t");
        int intid = Integer.parseInt(ss[0]);
        ret.put( intid, new Read( intid, ss[1], en.sequence(), en.qualiy(), par ));
    }
    return ret;
}
    
}
