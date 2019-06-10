/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.res;

import fork.lib.bio.seq.parser.fastq.Phred;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import prog.core.Param;
import prog.core.aln.Alignment;
import prog.core.aln.hot.DetectorHotspot;
import prog.core.aln.mut.AlignedElement;
import prog.core.aln.mut.AlignedMatch;
import prog.core.aln.mut.MutationSpot;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class AlignmentResultRead extends HashMap<String,HashMap<String,ArrayList<Alignment>>> implements Serializable{
    

public void addAlignment(Alignment aln){
    if(aln==null){return;}
    String gene = aln.isoformStrand().geneID();
    if(!containsKey(gene)){
        put(gene, new HashMap<>());
    }
    HashMap<String,ArrayList<Alignment>> map = get(gene);
    String uid = aln.isoformStrand().uniqueID();
    if(!map.containsKey(uid)){
        map.put(uid, new ArrayList<>());
    }
    map.get(uid).add(aln);
}
public ArrayList<Alignment> getAlignmentsForGene(String gid){
    ArrayList<Alignment> ret = new ArrayList<>();
    if( containsKey(gid) ){
        for( String iso:get(gid).keySet() ){
            ret.addAll(get(gid).get(iso));
        }
    }
    return ret;
}

public void initAlignments(Index index, ReadPool pool){
    for( String gid:keySet() ){
        for( Alignment aln:getAlignmentsForGene(gid) ){
            aln.initTransient(index,pool);
        }
    }
}

public ArrayList<Integer> getWTReadsForLocation(String gid, int low, int high, ReadPool pool, Param par){
    ArrayList<Integer> ret = new ArrayList<>();
    for( Alignment aln:this.getAlignmentsForGene(gid) ){
        for( AlignedElement ele:aln.elements() ){
            if(ele instanceof AlignedMatch){
                int ma = aln.isoformStrand().location((int)ele.referenceRegion().low());
                int mb = aln.isoformStrand().location((int)ele.referenceRegion().high());
                int a, b;
                if(ma<mb){
                    a=ma; b=mb;
                }else{
                    a=mb; b=ma;
                }
                if(a<=low && b>=high){
                    int ldis = low-a, rdis = b-high;
                    int inda = (int)ele.sequenceRegion().low()+ldis;
                    int indb = (int)ele.sequenceRegion().high()-rdis;
                    Read rd = aln.getRead(pool);
                    double score = 0;
                    try{
                        try{
                            score = Phred.PHRED33.get(rd.sequence().charAt(inda));
                        }catch(Exception e){
                            score = Phred.PHRED33.get(rd.sequence().charAt(indb));
                        }
                    }catch(Exception e){}
                    if(score<par.quality){
                        ret.add(aln.getReadID());
                    }
                }
                
            }
        }
    }
    return ret;
}

public static void main(String[] args) throws Exception { //debug 
    DetectorHotspot.main(args);
}
    
}
