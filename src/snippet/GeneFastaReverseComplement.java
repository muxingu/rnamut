/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.base.file.FileName;
import fork.lib.bio.seq.Nucleotide;
import fork.lib.bio.seq.parser.fasta.FastaEntry;
import fork.lib.bio.seq.parser.fasta.FastaReader;
import fork.lib.bio.seq.parser.fasta.FastaWriter;
import java.io.File;

/**
 *
 * @author mg31
 */
public class GeneFastaReverseComplement {
    
    
public static void main(String[] args) throws Exception { //debug 
    File dir= new File("C:\\muxingu/data/own/SangerSoftware2\\file");
    File f = new File(dir+"/gene.fa");
    File of = new File(dir+"/"+FileName.baseName(f)+"_all.fa");
    
    FastaReader fr = new FastaReader(f);
    FastaWriter fw = new FastaWriter(of);
    fw.param().charPerLine=999999;
    FastaEntry en;
    while((en=fr.nextEntry())!=null){
        fw.write(en);
        fw.write(new FastaEntry("REV_"+en.title(), Nucleotide.reverseComplement(en.sequence())));
    }
    fr.close();
    fw.close();
}
    
}
