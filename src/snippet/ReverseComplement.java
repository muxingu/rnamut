/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.bio.seq.Nucleotide;

/**
 *
 * @author mg31
 */
public class ReverseComplement {

public static void main(String[] args) throws Exception { //debug 
    String s= "ATTCATCAATTATGTGAAGAATTGCTTCCGGATGACTGACCAAGAGGCTATTCAAGATCTCTGCATGGCAGTGGAGGAAGTCTCTTTAAGAAAATAGTTTAAACAATTTGTTAAAAAATTTTCC";
    System.out.println(Nucleotide.reverseComplement(s));
}

    
}
