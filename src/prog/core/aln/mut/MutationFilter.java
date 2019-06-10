/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import fork.lib.base.Print;
import fork.lib.bio.seq.Nucleotide;
import fork.lib.math.applied.stat.FrequencyCount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import prog.core.aln.ele.Gene;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.hot.DetectorHotspot;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationFilter {
    
public HashSet<String> exclude = new HashSet<>();


    public MutationFilter(){
        exclude.addAll( Arrays.asList(new String[]{"Syn","UTR","Error"}) );
    }

    
public boolean isGood(MutationSpot mut, Index index){
    boolean ret = true;
    String aty = mut.aminoAcidMutation(index);
    if(exclude.contains(aty)){
        //System.out.println(aty+"  "+ subExclude.contains(aty));
        return false;
    }
    if(mut instanceof MutationSpotSubstitution){
    }else if(mut instanceof MutationSpotInsertion){
        IsoformStrand iso = mut.isoformStrandSense(index); String seq = iso.sequence();
        char nt = iso.isForward() ? mut.mutString().charAt(0) : Nucleotide.complementaryNucleotide(mut.mutString().charAt(0));
        HashMap<Integer,Integer> pl = iso.pos2loc();
        ArrayList<Integer> ps = new ArrayList<>(); ps.addAll(pl.keySet()); Collections.sort(ps);
        //for( Integer p:ps ){System.out.println(p+" "+pl.get(p)+" "+iso.sequence().charAt(p));} System.out.println(loc);
        int panc = MutationSpot.mutposlow(iso, mut);
        int pancl = panc, panch=panc;
        while(true){
            if(pancl<=0){break;}
            if(seq.charAt(pancl)!=nt){ pancl++; break;}
            pancl--;
        }
        while(true){
            if(panch>=seq.length()-1){break;}
            if(seq.charAt(panch)!=nt){ panch--; break;}
            panch++;
        }
        if((panch-pancl+1)>=5){
             ret = false;
        }
        
    }else if(mut instanceof MutationSpotITD){
        String aa = mut.aminoAcidMutation(index);
        if(mut instanceof MutationSpotPTD){
            if(aa.contains("exon")){
                ret= true;
            }else{ret = false;}
        }else{
            if(aa.contains("Error") || aa.contains("fs") || aa.contains("UTR")){
                ret= false;
            }
        }
    }
    return ret;
}



public static void main(String[] args) throws Exception { //debug 
    DetectorHotspot.main(args);
}

}
