/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Serializable;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
abstract public class AlignedElement{
    
protected Region refreg;
protected Region seqreg;


public Region referenceRegion(){return refreg;}
public Region sequenceRegion(){return seqreg;}


    
}
