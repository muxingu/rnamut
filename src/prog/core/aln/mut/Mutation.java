/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;


import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.read.Read;

/**
 *
 * @author mg31
 */
abstract public class Mutation extends AlignedElement{

    
public String genomicLocation(IsoformStrand iso) throws Exception{
    return mutationSpot(iso).toString();
};

abstract public MutationSpot mutationSpot(IsoformStrand iso)throws Exception;
abstract public double  quality(Read read);
    
}
