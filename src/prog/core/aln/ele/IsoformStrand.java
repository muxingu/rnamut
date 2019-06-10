/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.ele;

import fork.lib.base.collection.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author mg31
 */
public class IsoformStrand implements Serializable{
    
protected Isoform parent;
protected char sas;
protected String seq, uid;
protected HashMap<Integer,Integer> pos2loc= new HashMap<>();
//protected HashMap<Integer,Integer> loc2pos= new HashMap<>();
private Pair<Integer,Integer> cinds;


    public IsoformStrand(Isoform parent, char sas, String seq){
        this.sas = sas;
        this.seq = seq;
        this.parent=parent;
        init();
    }
    
protected void init(){ 
    ArrayList<Pair<Integer,Integer>> lhs = parent.lhs();
    Pair<Integer,Integer> cods = parent.codons();
    ArrayList<Integer> ps = new ArrayList<>();
    if( isForward() ){
        int ind =0; 
        for( Pair<Integer,Integer> p:lhs ){
            int l = p.a(), h = p.b();
            for( int i=l; i<=h; i++ ){
                pos2loc.put(ind, i);
                //loc2pos.put(i, ind);
                if(i==cods.a()||i==cods.b()){ps.add(ind);}
                ind++;
            }
        }
    }else{
        int ind =0;
        for( int j=lhs.size()-1; j>=0; j-- ){
            Pair<Integer,Integer> p = lhs.get(j);
            int l = p.a(), h = p.b();
            for( int i=h; i>=l; i-- ){
                pos2loc.put(ind, i);
                if(i==cods.a()||i==cods.b()){ps.add(ind);}
                ind++;
            }
        }
    }
    Collections.sort(ps);
    cinds = new Pair<>(ps.get(0), ps.get(1));
    uid = geneID()+"_"+parent.ID()+"_"+sas;
}


public void setParent(Isoform par){this.parent=par;}
public Isoform parent(){return parent;}
public String geneID(){return parent.geneID();}
public String transcriptID(){return parent.ID();}
public String uniqueID(){return uid;}
public String chr(){return parent.chr();}
public String sequence(){return seq;}
public char strand(){return parent.strand();}
public char sas(){return sas;}
public int location(int pos){ return pos2loc.get(pos); }
public String toString(){return uniqueID()+"_"+strand();}
//public int position(int loc){ return loc2pos.get(loc); }
public HashMap<Integer,Integer> pos2loc(){return pos2loc;}
//public HashMap<Integer,Integer> loc2pos(){return loc2pos;}
public Pair<Integer,Integer> codingInds(){return cinds;}


public boolean isForward(){
    if(strand()=='+'){
        return sas=='s';
    }
    return sas=='a';
}

public static boolean isPair(IsoformStrand ia, IsoformStrand ib){
    if(ia.geneID().equals(ib.geneID())){
        if(ia.transcriptID().equals(ib.transcriptID())){
            if(ia.sas()!=ib.sas()){
                return true;
            }
        }
    }
    return false;
}


}



