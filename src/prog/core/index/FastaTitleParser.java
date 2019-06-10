/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.index;

import fork.lib.base.collection.Pair;
import fork.lib.bio.seq.parser.fasta.FastaEntry;
import fork.lib.bio.seq.parser.fasta.FastaReader;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author mg31
 */
public class FastaTitleParser {


public static String SEP = " ";
public static String SEP_IDNAME= "_";
public static String SEP_LOCATION= ",";
public static String SEP_LOWHIGH= "-";

protected String head;
protected String geneName, trID, chr;
protected char std;
protected ArrayList<Pair<Integer,Integer>> lhs= new ArrayList<>();
private Pair<Integer,Integer> codons;


    public FastaTitleParser(String head){ 
        this.head=head;
        init();
    }
    
    
protected void init(){
    String[] ss = head.split(SEP);
    String[] gchs = ss[0].split(SEP_IDNAME);
    trID = gchs[0]; 
    geneName = gchs[1];
    chr = ss[1];
    std =  ss[2].charAt(0);
    String[] pss = ss[3].split(",");
    codons = new Pair<>(Integer.parseInt(pss[0]),Integer.parseInt(pss[1]));
    String[] locs = ss[4].split(SEP_LOCATION);
    for( int i=0; i<locs.length; i++ ){
        String[] locss = locs[i].split(SEP_LOWHIGH);
        lhs.add( new Pair<>(Integer.parseInt(locss[0]), Integer.parseInt(locss[1])) );
    }
}

public String transcriptID(){return trID;}
public String geneName(){return geneName;}
public String chr(){return chr;}
public char strand(){return std;}
public boolean isForwardStrand(){return std=='+';}
public ArrayList<Pair<Integer,Integer>> lowHighs(){return lhs;}
public Pair<Integer,Integer> codons(){return codons;}



public static void main(String[] args) throws Exception { //debug 
    File f= new File("C:\\muxingu\\progs\\java\\SangerSoftware2\\file/gene.fa");
    
    FastaEntry en = new FastaReader(f).nextEntry();
    FastaTitleParser pp = new FastaTitleParser(en.title());
    
    
    
}
    
}



