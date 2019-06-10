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
import prog.core.aln.hot.Anchor;
import prog.core.aln.read.Read;

/**
 *
 * @author mg31
 */
public class AlignmentResultITD extends HashMap<String,HashMap<String, ArrayList<Pair<Integer,Anchor>>>>implements Serializable{
    

public void addReadITD(String gid, String iso, Read read, Anchor an){
    if(!containsKey(gid)){
        put(gid,new HashMap<>());
    }
    HashMap<String, ArrayList<Pair<Integer,Anchor>>> map = get(gid);
    if(!map.containsKey(iso)){
        map.put(iso, new ArrayList<>());
    }
    map.get(iso).add( new Pair<>(read.intID(),an) );
}
public ArrayList<Pair<Integer,Anchor>> getAlignmentsForGeneITD(String gid){
    ArrayList<Pair<Integer,Anchor>> ret = new ArrayList<>();
    if(containsKey(gid)){
        for( String iso:get(gid).keySet() ){
            ret.addAll(get(gid).get(iso));
        }
    }
    return ret;
}
    
    
    
}
