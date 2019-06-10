/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.index;

import prog.core.aln.ele.TranscriptFragmentSet;
import fork.lib.base.collection.Pair;
import fork.lib.bio.seq.CodonTranslator;
import fork.lib.bio.seq.Nucleotide;
import fork.lib.bio.seq.parser.fasta.FastaEntry;
import fork.lib.bio.seq.parser.fasta.FastaReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import prog.core.aln.ele.Gene;
import prog.core.aln.ele.IsoformStrand;

/**
 *
 * @author mg31
 */
public class IndexBuilderFasta extends IndexBuilder{
    
protected File file;
public int kmer= 10;
    
    
    public IndexBuilderFasta(File file)throws Exception { 
        this.file=file;
        index= new Index(kmer);
    }
    

@Override
public void build() throws Exception {
    index = new Index(kmer);
    FastaReader fr= new FastaReader(file); FastaEntry en;
    // seggregate genes
    HashMap<String,Gene> idgs= new HashMap<>();
    while((en=fr.nextEntry())!=null){
        FastaTitleParser ps = new FastaTitleParser(en.title());
        String geneName = ps.geneName;
        if(!idgs.containsKey(geneName)){
            idgs.put(geneName, new Gene(geneName, ps.chr(), ps.strand()));
        }
        idgs.get(geneName).addTranscript( new TranscriptFragmentSet(ps.transcriptID(), ps.lowHighs(), ps.codons(), en.sequence()) );
    }
    for( String gn:idgs.keySet() ){
        index.addGene( idgs.get(gn) );
    }
}


public static void main(String[] args) throws Exception { //debug 
    File dir = new File("file");
    File fa= new File(dir+"/gene.fa"); String outf= dir+"/"+"index.ind";
    
    IndexBuilderFasta in= new IndexBuilderFasta(fa);
    in.build();
    in.write(outf);
    
    
}

}
