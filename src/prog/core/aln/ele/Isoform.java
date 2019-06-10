/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.ele;

import fork.lib.base.collection.Pair;
import fork.lib.bio.seq.Nucleotide;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author mg31
 */
public class Isoform implements Serializable{
    
private Gene parent;
private String id;
private IsoformStrand ise, ias;
private ArrayList<Pair<Integer,Integer>> lhs;
private Pair<Integer,Integer> codons;

    
    public Isoform(Gene parent, String id, ArrayList<Pair<Integer,Integer>> lhs, Pair<Integer,Integer> codons){
        this.parent=parent;
        this.id=id;
        this.lhs = lhs;
        this.codons = codons;
    }
 

public void initChildren(String seq)throws Exception { 
    ise = new IsoformStrand(this, 's', seq );
    ias = new IsoformStrand(this, 'a', Nucleotide.reverseComplement(seq) );
}

public String ID(){return id;}
public String geneID(){return parent.ID();}
public String chr(){return parent.chr();}
public char strand(){return parent.strand();}
public IsoformStrand strandSense(){return ise;}
public IsoformStrand strandAntisense(){return ias;}
public Gene parent(){return parent;}
public ArrayList<Pair<Integer,Integer>> lhs(){return lhs;}
public Pair<Integer,Integer> codons(){return codons;}


@Override
public boolean equals(Object obj) {
    if (this == obj) {return true;}
    if (obj == null) {return false;}
    if (getClass() != obj.getClass()) {return false;}
    final Isoform other = (Isoform) obj;
    if (!Objects.equals(this.id, other.id)) {return false;}
    if (!Objects.equals(this.parent, other.parent)) {return false;}
    return true;
}

@Override
public int hashCode() {
    int hash = 5;
    hash = 29 * hash + Objects.hashCode(this.parent);
    hash = 29 * hash + Objects.hashCode(this.id);
    return hash;
}


}
