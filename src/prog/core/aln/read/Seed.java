/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.read;

import prog.core.Param;
import java.util.ArrayList;
import java.util.HashMap;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class Seed {
    
    
private String seq;
private int ind;
    
    
    public Seed(String seq, int ind){
        this.seq=seq;
        this.ind=ind;
    }
    public Seed(){}
    
    
public String sequence(){return seq;}
public int index(){return ind;}


public HashMap<String,ArrayList<Integer>> align(Index index, Param par)throws Exception { 
    return index.hashMap().get(seq);
}

public String toString(){
    return Integer.toString(ind);
}
    
    
}
