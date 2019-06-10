/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.ele;

import fork.lib.base.collection.Pair;
import fork.lib.bio.seq.region.GenomicRegion;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author mg31
 */
public class Gene implements Serializable{
    
private String id, chr;
private char std;
private int low=Integer.MAX_VALUE, high=Integer.MIN_VALUE;
private ArrayList<Isoform> isos = new ArrayList<>();


    public Gene(String id, String chr, char std){
        this.id=id;
        this.chr=chr;
        this.std=std;
    }
    

public String ID(){return id;}
public String chr(){return chr;}
public char strand(){return std;}
public ArrayList<Isoform> isoforms(){return isos;}
public boolean isOnForwardStrnd(){return std=='+';}

@Override
public boolean equals(Object obj) {
    if (this == obj) {return true;}
    if (obj == null) {return false;}
    if (getClass() != obj.getClass()) {return false;}
    final Gene other = (Gene) obj;
    if (this.std != other.std) {return false;}
    if (!Objects.equals(this.id, other.id)) {return false;}
    if (!Objects.equals(this.chr, other.chr)) {return false;}
    return true;
}

@Override
public int hashCode() {
    int hash = 3;
    hash = 43 * hash + Objects.hashCode(this.id);
    hash = 43 * hash + Objects.hashCode(this.chr);
    hash = 43 * hash + this.std;
    return hash;
}


public void addTranscript(TranscriptFragmentSet frag)throws Exception { 
    Isoform is = new Isoform(this, frag.transcriptID(),frag.lowHighs(),frag.codons());
    is.initChildren(frag.sequence());
    isos.add( is );
    Pair<Integer,Integer> lh = frag.getLowHigh();
    low = Math.min(low, lh.a());
    high = Math.max(high, lh.b());
}

public GenomicRegion toGenomicRegion(){
    try{
        return new GenomicRegion(chr,low,high);
    }catch(Exception e){return null;}
}


    
}


