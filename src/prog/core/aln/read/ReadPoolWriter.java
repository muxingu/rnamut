/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.read;

import fork.lib.bio.seq.parser.fastq.FastqEntry;
import fork.lib.bio.seq.parser.fastq.FastqWriter;

/**
 *
 * @author mg31
 */
public class ReadPoolWriter {
    
private FastqWriter fw;
    

    public ReadPoolWriter(String path)throws Exception { 
        fw = new FastqWriter(path,true);
    }
    
    
public void write(Read read)throws Exception { 
    fw.write( new FastqEntry(read.intID()+"\t"+read.id(),read.sequence(),"+",read.quality()) );
}

public void close()throws Exception { 
    fw.close();
}
    
    
}
