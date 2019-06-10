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
import prog.core.aln.hot.DetectorFusionPair;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class AlignmentResultFusionPair extends HashMap<Pair<String,String>,HashMap<Pair<String,String>,ArrayList<Pair<Alignment,Alignment>>>> implements Serializable{

    
public void addAlignmentsTranslocation(Alignment aln1, Alignment aln2){
    Pair<String,String> gs = new Pair<>(aln1.isoformStrand().geneID(), aln2.isoformStrand().geneID());
    Pair<String,String> isos = new Pair<>(aln1.isoformStrand().uniqueID(), aln2.isoformStrand().uniqueID());
    if(!containsKey(gs)){
        put(gs, new HashMap<>());
    }
    HashMap<Pair<String,String>,ArrayList<Pair<Alignment,Alignment>>> map = get(gs);
    if(!map.containsKey(isos)){
        map.put(isos, new ArrayList<>());
    }
    map.get(isos).add( new Pair<>(aln1,aln2) );
}

public ArrayList<Pair<Alignment,Alignment>> getAlignmentsForGeneTranslocation(String gid1, String gid2){
    ArrayList<Pair<Alignment,Alignment>> ret = new ArrayList<>();
    Pair<String,String> gs = new Pair<>(gid1,gid2);
    if(containsKey(gs)){
        HashMap<Pair<String,String>,ArrayList<Pair<Alignment,Alignment>>> map = get(gs);        
        for( Pair<String,String> p:map.keySet() ){
            ret.addAll(map.get(p));
        }
    }
    Pair<String,String> gsr = new Pair<>(gid2,gid1);
    if(containsKey(gsr)){
        HashMap<Pair<String,String>,ArrayList<Pair<Alignment,Alignment>>> map = get(gsr);
        for( Pair<String,String> p:map.keySet() ){
            for( Pair<Alignment,Alignment> as:map.get(p) ){
                ret.add( new Pair<>(as.b(),as.a()) );
            }
        }
    }
    return ret;
}

public void initAlignments(Index index, ReadPool pool){
    for( Pair<String,String> gs:keySet() ){
        HashMap<Pair<String,String>,ArrayList<Pair<Alignment,Alignment>>> map = get(gs);
        for( Pair<String,String> is:map.keySet() ){
             for( Pair<Alignment,Alignment> p:map.get(is) ){
                 p.a().initTransient(index, pool);
                 p.b().initTransient(index, pool);
             }
        }
    }
}


}
