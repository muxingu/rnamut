/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.base.Print;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import prog.core.aln.hot.Pileup;
import prog.core.aln.read.ReadPool;
import prog.core.aln.res.AlignmentResult;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class ViewPileup {
    
    
    
public static void main(String[] args) throws Exception { //debug 
    String dir = "C:\\muxingu\\data\\own\\SangerSoftware2\\file";
    String pdir = dir+"/pool";
    Index index= IndexBuilder.read(dir+"/index.ind");
    
    String tar = "TCGA-AB-2918";
    
    ReadPool pool = ReadPool.read(pdir+"/pool_"+tar+".rds"); 
    AlignmentResult res = AlignmentResult.read(pdir+"/align_"+tar+".aln", index, pool);
    
    Pileup pu = new Pileup();
    pu.addAlignmentResult(res);
    
    
    HashMap<Integer,Integer> mp = pu.get("chr5");
    ArrayList<Integer> ls = new ArrayList<>(); ls.addAll(mp.keySet()); Collections.sort(ls);
    for( Integer loc:ls ){
        System.out.println(loc+" "+mp.get(loc));
    }
    
    
}
    
}
