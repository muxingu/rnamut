/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.read;

import prog.core.Param;
import fork.lib.base.collection.Pair;
import fork.lib.bio.seq.parser.fastq.FastqEntry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import prog.core.aln.Aligner;
import prog.core.aln.Alignment;
import prog.core.aln.GapAligner;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class Read implements Serializable{
    
private int intid;
private String id, seq, qual;
private Param par;

private ArrayList<Seed> seeds;
private String overhang = "";
private int seedlen = -1;

    
    public Read(int intid, String id, String seq, String qual, Param par){
        this.intid=intid;
        this.id=id;
        this.par= par;
        init(seq, qual);
        
    }
    public Read(int intid, FastqEntry en, Param par){
        this(intid,en.ID(),en.sequence(),en.qualiy(),par);
    }
    public Read(){}
    
    
private void init(String seq, String qual){
    if(par.ifClipEndN){
        int lind=0, rind=seq.length()-1;
        while( lind<seq.length() && seq.charAt(lind)=='N' ){ lind++; }
        while( rind>=0 && seq.charAt(rind)=='N' ){ rind--; }
        try{
            this.seq = seq.substring(lind, rind+1);
            this.qual = qual.substring(lind,rind+1);
        }catch(Exception e){ this.seq=""; this.qual=""; }
    }else{
        this.seq = seq;
        this.qual = qual;
    }
    
}
    
    
public int intID(){return intid;}
public String id(){ return id; }
public String sequence(){return seq;}
public String quality(){return qual;}
public String overhang(){return overhang;}
public ArrayList<Seed> seeds(){return seeds;}
public String toString(){return seq;}


public int length(){    
    return seq.length();
}

public void initSeedSet(int k){
    seeds= new ArrayList<>();
    int ind = 0;
    while(true){
        if(ind+k > length() ){
            break;
        }
        seeds.add(new Seed( seq.substring(ind,ind+k), ind) );
        ind+=k;
    }
    if(ind<length()){
        if(length()>=k){
            overhang = seq.substring(length()-k,length());
        }
    }
}

public void destroySeedSet(){
    seeds = null;
    overhang = null;
}

public void calculateLength(){
    seedlen =0;
    for( Seed s:seeds ){
        int n = s.index() + s.sequence().length();
        if(n>seedlen){
            seedlen = n;
        }
    }
}

public int seedLength(){
    if(seedlen==-1){
        calculateLength();
    }
    return seedlen;
}

@Override
public boolean equals(Object obj) {
    if (this == obj) {return true;}
    if (obj == null) {return false; }
    if (getClass() != obj.getClass()) {return false;}
    final Read other = (Read) obj;
    if (!Objects.equals(this.id, other.id)) {return false;}
    return true;
}

@Override
public int hashCode() {
    int hash = 5;
    hash = 79 * hash + Objects.hashCode(this.id);
    return hash;
}



    
}


