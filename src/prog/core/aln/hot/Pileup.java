/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import fork.lib.bio.seq.parser.bedgraph.BedGraphExporter;
import fork.lib.bio.seq.region.GenomicRegion;
import fork.lib.bio.seq.region.landscape.LandscapeBuilder;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import prog.core.aln.Aligner;
import prog.core.aln.Alignment;
import prog.core.aln.ele.Gene;
import prog.core.aln.res.AlignmentResult;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.mut.AlignedElement;

/**
 *
 * @author mg31
 */
public class Pileup extends HashMap<String,HashMap<Integer,Integer>>{

    

    public Pileup(){}
    

public void addAlignmentResult(AlignmentResult res){
    for( String gid:res.alignmentResult().keySet() ){
        for( Alignment aln:res.resread.getAlignmentsForGene(gid) ){
            addAlignment(aln);
        }
    }
}

public void addAlignment(Alignment aln){
    for( AlignedElement ele:aln.elements() ){
        addAlignedElement(ele,aln.isoformStrand());
    }
}
    
public void addAlignedElement(AlignedElement ele, IsoformStrand iso){
    if(ele==null){return;}
    Region reg = ele.referenceRegion();
    if(reg==null){return;}
    String chr = iso.chr();
    if(!containsKey(chr)){ put(chr,new HashMap<>()); }
    HashMap<Integer,Integer> pv = get(chr);
    for( int i=(int)reg.low(); i<=(int)reg.high(); i++ ){
        if(i==-1){continue;}
        int loc = iso.location(i);
        if( !pv.containsKey(loc) ){ 
            pv.put(loc, 0); 
        }
        pv.put(loc, pv.get(loc)+1);
    }
}


public int height(String chr, int loc){ 
    if(!containsKey(chr)){ return 0; }
    HashMap<Integer,Integer> pv = get(chr);
    if(!pv.containsKey(loc)){ 
        return Math.max( hh(pv,loc-1), hh(pv,loc+1) );
    }
    return pv.get(loc);
}
private static int hh(HashMap<Integer,Integer>pv, int loc){
    return pv.containsKey(loc) ? pv.get(loc) : 0;
}    

public void writeToWig(String out)throws Exception { 
    LandscapeBuilder lb = new LandscapeBuilder();
    for( String chr:keySet() ){
        HashMap<Integer,Integer> pv = get(chr);
        ArrayList<Integer> ps = new ArrayList<>(); ps.addAll(pv.keySet());
        Collections.sort(ps);
        for( Integer p:ps ){
            GenomicRegion gr = new GenomicRegion(chr,p,p); gr.setValue(pv.get(p));
            lb.add( gr );
        }
    }
    new BedGraphExporter(lb).writeToFile(out);
}

public double averageHeight(String chr, int low, int high){
    double ret = 0; int len=0;
    int[] poss = new int[]{low, high};
    for( int i=0; i<poss.length; i++ ){
        int v =  height(chr, poss[i]);
        if(v>0){
            ret+=v; len++;
        }
    }
    return len==0 ? 0 : ret/len;
}

public int baseCount(GenomicRegion... grs){
    int ret =0;
    for( GenomicRegion gr:grs ){
        String chr = gr.chr();
        if(!containsKey(chr)){ continue; }
        int low = (int)gr.low(), high = (int) gr.high();
        HashMap<Integer,Integer> hm = get(chr);
        for( int k:get(chr).keySet() ){
            if(k>=low&&k<=high){
                ret += hm.get(k);
            }
        }
    }
    return ret;
}

public int baseCount(ArrayList<GenomicRegion> grs){
    int ret = 0;
    for( GenomicRegion gr:grs ){
        ret += baseCount(gr);
    }
    return ret;
}

public ArrayList<GenomicRegion> coveredRegionsForGene(GenomicRegion greg){
    ArrayList<GenomicRegion> ret = new ArrayList<>();
    if(greg==null){
        return ret;
    }
    String chr = greg.chr();
    int low = (int)greg.low(), high = (int) greg.high();
    if(!containsKey(chr)){return ret;}
    HashMap<Integer,Integer> hm = get(chr);
    
    int maxv = 0;
    for( int k:hm.keySet() ){ 
        if(!(k>=low && k<=high)){ continue; }
        int v = hm.get(k);
        if(v>maxv){
            maxv=v;
        }
    }
    
    double thr = (double) maxv/6;
    ArrayList<Integer> ks = new ArrayList<>(); ks.addAll(hm.keySet()); Collections.sort(ks);
    int l=-1, h=-1;
    for( int k:ks ){
        if(!(k>=low && k<=high)){ continue; }
        if(hm.get(k)<thr){ continue; }
        if(l==-1){
            l=k; h=k;
        }else if(k==h+1){
            h=k;
        }else{
            try {
                ret.add(new GenomicRegion(chr,l,h));
            } catch (Exception ex) {}
            l=k; h=k;
        }
    }
    if(l!=-1){
        try {
            ret.add(new GenomicRegion(chr,l,h));
        } catch (Exception ex) {}
    }
    return ret;
}


    
}
