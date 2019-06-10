/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.index;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author mg31
 */
public class FastaIndexChecker implements Serializable{
    
protected File file;

    public FastaIndexChecker( File file )throws Exception { 
        this.file=file;
        init();
    }
    
protected void init() throws Exception { 
    
}
    
    
protected void initCheck(){ 
    ArrayList<Integer> ls=null, hs=null;
    String seq ="";
    if(ls.isEmpty() || hs.isEmpty()){
        System.err.println("Empty exons!");
        System.exit(1);
    }
    if(ls.size()!=hs.size()){
        System.err.println("lows size ("+ls.size()+") != highs size ("+hs.size()+")");
        System.exit(1);
    }
    for( int i=0; i<ls.size(); i++ ){
        if(ls.get(i)>hs.get(i)){
            System.err.println("low ("+ls.get(i)+") > high ("+hs.get(i)+")");
            System.exit(1);
        }
    }
    int len = 0;
    for( int i=0; i<ls.size(); i++ ){
        len+= hs.get(i)-ls.get(i)+1;
    }
    if(len!=seq.toString().length()){
        System.err.println("Sum of exon lengths("+len+") != Sequence length("+seq.toString().length()+")");
        System.exit(1);
    }
}



}


