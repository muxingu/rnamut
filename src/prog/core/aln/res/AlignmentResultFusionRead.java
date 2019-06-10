/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.res;

import fork.lib.base.collection.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import prog.core.aln.Alignment;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.hot.Anchor;
import prog.core.aln.read.Read;

/**
 *
 * @author mg31
 */
public class AlignmentResultFusionRead extends HashMap<Pair<String,String>,HashMap<Pair<String,String>,ArrayList<Pair<Integer,Anchor>>>> implements Serializable{



public void addReadTranslocation(IsoformStrand iso1, IsoformStrand iso2, Read read, Anchor an){
    Pair<String,String> gs = new Pair<>(iso1.geneID(), iso2.geneID());
    Pair<String,String> isos = new Pair<>(iso1.uniqueID(), iso2.uniqueID());
    if(!containsKey(gs)){
        put(gs, new HashMap<>());
    }
    HashMap<Pair<String,String>,ArrayList<Pair<Integer,Anchor>>> map = get(gs);
    if(!map.containsKey(isos)){
        map.put(isos, new ArrayList<>());
    }
    map.get(isos).add( new Pair<>(read.intID(),an) );
}
public ArrayList<Pair<Integer,Anchor>> getReadForGeneTranslocation(String gid1, String gid2){
    ArrayList<Pair<Integer,Anchor>> ret = new ArrayList<>();
    ret.addAll( getReadForGeneTranslocationPair(gid1,gid2) );
    ret.addAll( getReadForGeneTranslocationPair(gid2,gid1) );
    return ret;
}
private ArrayList<Pair<Integer,Anchor>> getReadForGeneTranslocationPair(String gid1, String gid2){
    ArrayList<Pair<Integer,Anchor>> ret= new ArrayList<>();
    Pair<String,String> gs = new Pair<>(gid1,gid2);
    HashMap<Pair<String,String>,ArrayList<Pair<Integer,Anchor>>> map = containsKey(gs) ?
            get(gs) : get(new Pair<>(gid2,gid1));
    if(map==null){
        return ret;
    }
    for( Pair<String,String> p:map.keySet() ){
        ret.addAll(map.get(p));
    }
    return ret;
}


    
}

