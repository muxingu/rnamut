/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln;

import fork.lib.base.collection.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import prog.core.Param;
import prog.core.aln.Aligner;
import prog.core.aln.Alignment;
import prog.core.aln.GapAligner;
import prog.core.aln.hot.ZZDebug;
import prog.core.aln.hot.ZZTest;
import prog.core.aln.read.Read;
import prog.core.aln.read.Seed;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class ReadAligner {
    
private static Comparator<Alignment> comp = new Comparator<Alignment>(){
    public int compare(Alignment o1, Alignment o2) {
        return Double.compare(o1.mismatchScore(), o2.mismatchScore());
    }
};
private static Comparator<Pair<Seed,Integer>> seedcomp = new Comparator<Pair<Seed,Integer>>(){
    public int compare(Pair<Seed, Integer> o1, Pair<Seed, Integer> o2) {
        return Integer.compare(o1.a().index(), o2.a().index());
    }
};
    
private Read read;
private Index index;
private Param par;

private ArrayList<Pair<Seed,HashMap<String,ArrayList<Integer>>>> seedmaps = new ArrayList<>();
private HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> iss, isg;
private HashMap<String, ArrayList<ArrayList<Pair<Seed,Integer>>>> isord;
private ArrayList<Alignment> alns = new ArrayList<>();
private Alignment bestaln = null;
private boolean ifstart = false, ifbestaln = false;

        
    public ReadAligner(Read read, Index index, Param par){
        this.read=read;
        this.index=index;
        this.par=par;
    }
    
    
public HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> iss(){return iss;}
public ArrayList<Pair<Seed,HashMap<String,ArrayList<Integer>>>> seedmaps() {return seedmaps;}

public Read read(){return read;}
    
public boolean startAlign() throws Exception { 
    ifstart = true;
    if(read.seeds()==null){
        read.initSeedSet(index.kmer());
    }
    // map each seed
    seedmaps = seedmaps(index,par);
    // sum seeds to isoform
    iss = mapSeedsToIsoform(index, par);
    if(iss.isEmpty()){
        return false;
    }
    // find all isoforms with over half seeds mapped (order unchecked)
    isg = selectGoodSeedMap(iss, par);
    if(isg.isEmpty()){
        return false;
    }
    // select seed sets with right order
    isord = matchOrder(isg, par,index.kmer());
    
    if(isord==null || isord.isEmpty()){
        return false;
    }
    // for( String s:isord.keySet() ){System.out.print(s+"  "); for( ArrayList al:isord.get(s) ){System.out.println(al);}}
    // fill gap
    for( String iso:isord.keySet() ){
        for( ArrayList<Pair<Seed,Integer>> locs:isord.get(iso) ){
            GapAligner ga = new GapAligner(index, index.getIsoformStrand(iso), read, locs, par );
            Alignment aln = ga.align();
            if(aln!=null){
                alns.add(aln);
            }
        }
    }
    return true;
}

public ArrayList<Pair<Seed,HashMap<String,ArrayList<Integer>>>> seedmaps(Index index, Param par)throws Exception { 
    ArrayList<Pair<Seed,HashMap<String,ArrayList<Integer>>>> ret = new ArrayList<>();
    for( int i=0; i<read.seeds().size(); i++ ){
        Seed sd= read.seeds().get(i);
        HashMap<String,ArrayList<Integer>> ilocs = sd.align(index, par);
        ret.add(new Pair<>(sd,ilocs));
    }
    return ret;
}

public HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> mapSeedsToIsoform(Index index, Param par)throws Exception { 
    HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> iseeds = new HashMap<>(); // isoform to ind map
    for( int i=0; i<seedmaps.size(); i++ ){
        Seed sd = seedmaps.get(i).a();
        HashMap<String,ArrayList<Integer>> ilocs = seedmaps.get(i).b();
        if(ilocs!=null){
            for( String iso:ilocs.keySet() ){
                if(!iseeds.containsKey(iso)){
                    iseeds.put(iso, new ArrayList<>());
                }
                iseeds.get(iso).add( new Pair<>(sd,ilocs.get(iso)) );
            }
        }
    }
    return iseeds;
}

public HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> selectGoodSeedMap(
        HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> iseeds, Param par)throws Exception { 
    HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> iseedsg = new HashMap<>();
    int thr = (int)Math.max(par.minMappedSeeds, (int)Math.ceil(par.minMappedSeedsFraction*read.seeds().size()));
    for( String iso:iseeds.keySet() ){
        ArrayList<Pair<Seed,ArrayList<Integer>>> seeds = iseeds.get(iso);
        if( seeds.size() >= thr ){
            iseedsg.put(iso, seeds);
        }
    }
    return iseedsg;
}

public HashMap<String, ArrayList<ArrayList<Pair<Seed,Integer>>>> matchOrder(
        HashMap<String, ArrayList<Pair<Seed,ArrayList<Integer>>>> isg, Param par, int kmer)throws Exception { 
    HashMap<String, ArrayList<ArrayList<Pair<Seed,Integer>>>> ret= new HashMap<>();
    //System.out.println(isg);
    for( String iso:isg.keySet() ){
        ArrayList<Pair<Seed,ArrayList<Integer>>> glis = isg.get(iso);
        HashMap<ArrayList<Integer>,ArrayList<Seed>> valismap = new HashMap<>();
        int thr = (int)Math.max(par.minOrderedSeeds, (int) Math.floor(glis.size()*par.minOrderedSeedsFraction));
        ArrayList<Pair<Seed,Integer>> uniseeds = new ArrayList<>();
        ArrayList<Pair<Seed,ArrayList<Integer>>> mulseeds = new ArrayList<>();
        for( Pair<Seed,ArrayList<Integer>> p:glis ){
            if(p.b().size()==1){ uniseeds.add(new Pair<>(p.a(),p.b().get(0)));}
            else{ mulseeds.add(p); }
        }
        
        if(uniseeds.size()<par.minUniqueMappedSeeds){ continue; }
        if(!uniqueSeedsOrder(uniseeds,par)){ continue; }
        
        ArrayList<Pair<Seed,Integer>> locseeds = new ArrayList<>(); locseeds.addAll(uniseeds);
        for( Pair<Seed,ArrayList<Integer>> p:mulseeds ){
            Pair<Seed,Integer> sind= bestMatchingOrder( p,uniseeds, par );
            if(sind!=null){
                locseeds.add(sind);
            }
        }
        locseeds.sort(seedcomp);
        for( int i=0; i<locseeds.size()-1; i++ ){
            Pair<Seed,Integer> p = locseeds.get(i), pn = locseeds.get(i+1);
            //System.out.println(p+"  "+pn+"  "+ (pn.a().index()-p.a().index())+"   "+ (pn.b()-p.b())+"  "+(pn.a().index()-p.a().index()) );
            if( pn.a().index()-p.a().index()<=kmer ){
                if( (pn.b()-p.b())-(pn.a().index()-p.a().index()) <0 ){
                    locseeds.remove(i+1); i--;
                }
            }else{
                if( seedsDistanceBad(p,pn,par) ){
                    locseeds.remove(i+1); i--;
                }
            }
        }
        if( locseeds.size() >= thr ){
            ArrayList<Seed> rs= new ArrayList<>(); ArrayList<Integer> ls = new ArrayList<>();
            for( Pair<Seed,Integer> p:locseeds ){
                rs.add(p.a()); ls.add(p.b());
            }
            if( !valismap.containsKey(ls)){
                valismap.put( ls,rs );
            }
        }else{
            continue;
        }
        
        //for( ArrayList<Integer> as:valismap.keySet() ){ArrayList<ReadSeed> rs = valismap.get(as);for( int i=0; i<as.size(); i++ ){System.out.println(as.get(i)+"  "+ rs.get(i).index());}System.out.println();}
        
        ArrayList<ArrayList<Pair<Seed,Integer>>> valis = new ArrayList<>();
        for( ArrayList<Integer> keys:valismap.keySet() ){
            ArrayList<Seed> vals = valismap.get(keys);
            ArrayList<Pair<Seed,Integer>> ps= new ArrayList<>();
            for( int i=0; i<keys.size(); i++ ){
                ps.add( new Pair<>(vals.get(i),keys.get(i)) );
            }
            valis.add(ps);
        }
        ret.put(iso, valis);
    }
    return ret;
}

private boolean uniqueSeedsOrder(ArrayList<Pair<Seed,Integer>> us, Param par){
    for( int i=0; i<us.size()-1; i++ ){
        Pair<Seed,Integer> u = us.get(i), un = us.get(i+1);
        if( seedsDistanceBad(u,un,par) ){
            return false;
        }
    }
    return true;
}

private boolean seedsDistanceBad(Pair<Seed,Integer> u, Pair<Seed,Integer> un, Param par){
    int diff = (un.b()-u.b()) - (un.a().index()-u.a().index());
    return diff>par.maxDeletion || diff<-par.maxInsertion;
}

private Pair<Seed,Integer> bestMatchingOrder(Pair<Seed,ArrayList<Integer>> tar, ArrayList<Pair<Seed,Integer>> us, Param par)throws Exception { 
    Pair<Seed,Integer> ret = null; int mindis = Integer.MAX_VALUE;
    loop:
    for( Integer loc:tar.b() ){
        int dis = 0; int ind = tar.a().index();
        for( Pair<Seed,Integer> up:us ){
            int d = (up.b()-loc) - (up.a().index()-ind);
            if( d>par.maxDeletion || d<-par.maxInsertion ){
                continue loop;
            }
            dis+= Math.abs(d);
        }
        if(dis<mindis){
            mindis=dis; ret = new Pair<>(tar.a(),loc);
        }
    }
    return ret;
}
    
public Alignment bestAlignment()throws Exception { 
    if(ifbestaln){
        return bestaln;
    }
    ArrayList<Alignment> alns_= alignments();
    if(alns_==null || alns_.isEmpty()){
        return null;
    }
    Collections.sort(alns_, comp);
    //for( Alignment a:alns ){ System.out.println(a.iso.uniqueID()); a.print();System.out.println(); }
    bestaln = alns_.get(0);
    return bestaln;
}

public ArrayList<Alignment> alignments()throws Exception { 
    if(ifstart){
        return alns;
    }
    startAlign();
    return alns;
}

    
    
    
    
public static void main(String[] args) throws Exception { //debug 
    //ZZDebug.main(args);
    ZZTest.main(args);
}
    
}
