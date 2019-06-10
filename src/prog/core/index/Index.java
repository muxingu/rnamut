/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.index;

import prog.core.aln.ele.Gene;
import fork.lib.base.collection.random.RandomString;
import fork.lib.math.applied.stat.FrequencyCount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import prog.core.aln.ele.Isoform;
import prog.core.aln.ele.IsoformStrand;

/**
 *
 * @author mg31
 */
public class Index implements Serializable{
    

private int kmer;
private HashMap<String,Gene> idgene = new HashMap<>();
private HashMap<String,IsoformStrand> idiso = new HashMap<>();
private HashMap<String,HashMap<String,ArrayList<Integer>>> smap = new HashMap<>();


    public Index(int kmer){
        this.kmer=kmer;
    }


public void addSeed(String seed, IsoformStrand ist, Integer pos){
    String id = ist.uniqueID();
    if(!idiso.containsKey(id)){
        idiso.put(id, ist);
    }
    if(!smap.containsKey(seed)){
        smap.put(seed, new HashMap<>());
    }
    if(!smap.get(seed).containsKey(id)){
        smap.get(seed).put(id, new ArrayList<>());
    }
    smap.get(seed).get(id).add(pos);
}

public void addIsoformStrand(IsoformStrand ist){
    String s = ist.sequence();
    for( int i=0; i<s.length()-kmer; i++ ){
        String seed = s.substring(i, i+kmer);
        if( checkSeed(seed) ){
            addSeed(seed, ist, i);
        }
    }
}

private static boolean checkSeed(String seed){
    FrequencyCount<Character> fc= new FrequencyCount();
    for( int j=0; j<seed.length(); j++ ){
        fc.add(seed.charAt(j));
    }
    double maxf = (double)fc.getCount(fc.mostFrequentKey())/seed.length();
    if(maxf>0.8){
        return false;
    }
    for( int i=2; i<=3; i++ ){
        ArrayList<HashSet<Character>> sets = new ArrayList<>();
        for( int j=0; j<i; j++ ){ sets.add(new HashSet<>()); }
        int ind=0;
        for( int j=0; j<seed.length(); j++ ){
            sets.get(ind).add( seed.charAt(j) );
            ind++; if(ind>=sets.size()){ ind=0; }
        }
        boolean ifallone = true;
        for( int j=0; j<sets.size(); j++ ){
            if(sets.get(j).size()>1){
                ifallone = false; break;
            }
        }
        if(ifallone){
            return false;
        }
    }
    return true;
}

public void addGene(Gene gene){
    if(!idgene.containsKey(gene.ID())){
        idgene.put(gene.ID(), gene);
    }
    for( Isoform iso:gene.isoforms() ){
        addIsoformStrand(iso.strandSense());
        addIsoformStrand(iso.strandAntisense());
    }
}
    
public int kmer(){ return kmer; }
public Gene getGene(String id){return idgene.get(id);}
public IsoformStrand getIsoformStrand(String id){ return idiso.get(id); }
public HashMap<String,IsoformStrand> idToIsoformStrand(){return idiso;}
public HashMap<String,HashMap<String,ArrayList<Integer>>> hashMap(){return smap;}
public HashMap<String,Gene> idToGene(){return idgene;}






}

