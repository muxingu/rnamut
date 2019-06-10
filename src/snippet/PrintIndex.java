/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.bio.seq.CodonTranslator;
import fork.lib.bio.seq.region.GenomicRegion;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import prog.core.aln.ele.Gene;
import prog.core.aln.ele.IsoformStrand;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class PrintIndex {
    
    
public static void main(String[] args) throws Exception { //debug 
    File dir= new File("C:\\muxingu/data/own/SangerSoftware2\\file");
    Index index= IndexBuilder.read(dir+"/index.ind"); System.out.println("Index loaded. ");
    
    BufferedWriter bw= new BufferedWriter(new FileWriter(dir+"/gene_loc.txt"));
    for( String id : index.idToGene().keySet() ){
        Gene g = index.getGene(id);
        IsoformStrand iso = g.isoforms().get(0).strandSense();
        String cd = iso.sequence().substring(iso.codingInds().a(), iso.codingInds().b()+1);
        CodonTranslator tr = new CodonTranslator(cd);
        String aa = tr.frame(0);
        System.out.println(id+" "+aa.length());
        GenomicRegion gr =g.toGenomicRegion();
        bw.write(g.ID()+"\t"+g.chr()+":"+((int)gr.low())+"-"+((int)gr.high())+"\n");
    }
    bw.close();
    
}
    
}
