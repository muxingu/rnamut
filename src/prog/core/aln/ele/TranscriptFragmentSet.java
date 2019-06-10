/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.ele;

import fork.lib.base.collection.Pair;
import java.util.ArrayList;

/**
 *
 * @author mg31
 */
public class TranscriptFragmentSet {


private ArrayList<Pair<Integer,Integer>> lhs; 
private String seq, trid;
private Pair<Integer,Integer> codons;
    
    
    public TranscriptFragmentSet(String trid, ArrayList<Pair<Integer,Integer>> lhs, Pair<Integer,Integer> codons, String seq) throws Exception { 
        this.trid = trid;
        this.lhs=lhs;
        this.codons=codons;
        this.seq=seq;
        init();
    }
    
    
protected void init() throws Exception { 
    int len = 0; 
    for( Pair<Integer,Integer> p:lhs ){
        len += p.b() - p.a() + 1;
    }
    if(len!=seq.length()){
        System.err.println("len not match: len="+len+" seq="+seq.length());
        throw new Exception();
    }
}

public Pair<Integer,Integer> getLowHigh(){
    int l=Integer.MAX_VALUE, h=Integer.MIN_VALUE;
    for( Pair<Integer,Integer> vs:lhs ){
        l = Math.min(l, vs.a());
        l = Math.min(l, vs.b());
        h = Math.max(h, vs.a());
        h = Math.max(h, vs.b());
    }
    return new Pair<>(l,h);
}
    
public ArrayList<Pair<Integer,Integer>> lowHighs(){return lhs;}
public Pair<Integer,Integer> codons(){return codons;}
public String sequence(){ return seq; }    
public String transcriptID(){return trid;}
    
}
