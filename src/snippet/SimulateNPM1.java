/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.bio.seq.parser.fasta.FastaReader;
import fork.lib.bio.seq.parser.fastq.FastqEntry;
import fork.lib.bio.seq.parser.fastq.FastqReader;
import fork.lib.bio.seq.parser.fastq.FastqWriter;
import java.util.ArrayList;

/**
 *
 * @author mg31
 */
public class SimulateNPM1 {
    
public static void main(String[] args) throws Exception { //debug 
    String dir = "C:\\muxingu\\data\\own\\SangerSoftware2\\file/sim";
    String fan = dir+"/npm1.fa";
    String outn = dir+"/sim_npm1.fq.gz";
    int pos = 1160;
    
    String seq = new FastaReader(fan).nextEntry().sequence();
    
    ArrayList<String> wts = new ArrayList<>(),
            muts = new ArrayList<>();
    while(true){
        if(wts.size()>=1000 && muts.size()>=1000){break;}
        int p = (int) (Math.random() * seq.length());
        int ph = p+50;
        try{
            if(p>pos || ph<pos-1){
            }else{
                if(pos-p<44){
                    String s = seq.substring(p,pos) + "TCTG";
                    s+= seq.substring(pos,pos+50-s.length());
                    if(muts.size()<1000){
                        muts.add(s);
                    }
                    if(wts.size()<1000){
                        wts.add(seq.substring(p,ph));
                    }
                }
            }
        }catch(Exception e){}
    }
    
    FastqWriter fw = new FastqWriter(outn,true);
    for( int i=0; i<muts.size(); i++ ){
        String s = muts.get(i);
        fw.write( new FastqEntry("Mut_"+(i+1),s) );
    }
    for( int i=0; i<muts.size(); i++ ){
        String s = wts.get(i);
        fw.write( new FastqEntry("WT_"+(i+1),s) );
    }
    fw.close();
}    
    
}
