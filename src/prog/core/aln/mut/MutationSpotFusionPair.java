/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import fork.lib.base.collection.Pair;
import fork.lib.base.collection.Triplet;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationSpotFusionPair extends MutationSpotFusion implements Serializable{

protected static DecimalFormat df3p = new DecimalFormat("#.##");
protected ArrayList<Pair<Integer,Integer>> reads = new ArrayList<>();
protected double cova, covb, covwta, covwtb;

    
    public MutationSpotFusionPair(String chr, String gene, double cova, double covwta,
            String chr2, String gene2, double covb, double covwtb){
        super(chr,0,1,"Fusion","",gene);
        this.chr2=chr2;
        this.gene2=gene2;
        this.cova = cova;
        this.covb = covb;
        this.covwta = covwta;
        this.covwtb = covwtb;
    }
    public MutationSpotFusionPair(){}


@Override
public String toString() {
    return gene+"-"+gene2+"_"+chr+"-"+chr2+"_PE";
}

public String mutatedCDS(Index index) {return null; }
public String aminoAcidMutation(Index index) { return "PE"; }
public ArrayList<Triplet<String, Region, String>> reconstructedTranscripts(Index index, int rlen) throws Exception {return null;}

public void addReads(int ida, int idb){ reads.add( new Pair<>(ida,idb) ); }
public double covA(){return cova;}
public double covB(){return covb;}
public double covwtA(){return covwta;}
public double covwtB(){return covwtb;}
public double vafA(){ return covwta+cova==0? 0: cova/(covwta+cova); }
public double vafB(){ return covwtb+covb==0? 0: covb/(covwtb+covb); }
public double maxVaf(){ return Math.max(vafA(), vafB()); }
public String covString(){return Integer.toString( (int)Math.ceil((cova+covb)/2)); }
public String covWTString(){return Integer.toString((int)covwta)+"_"+Integer.toString((int)covwtb);}

}

