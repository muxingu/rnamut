/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;


/**
 *
 * @author mg31
 */
public abstract class MutationSpotFusion extends MutationSpot{

protected String gene2, chr2;

    public MutationSpotFusion(String chr, int low, int high, String type, String mut, String gene){
        super(chr,low,high,type,mut,gene);
    }
    public MutationSpotFusion(){}
    

@Override
public String gene(){
    return gene+"-"+gene2;
}
public String gene1(){return gene;}
public String gene2(){return gene2;}

    
}
