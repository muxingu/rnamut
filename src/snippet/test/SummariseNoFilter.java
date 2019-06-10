/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet.test;

import static snippet.test.Summarise.selectAA;

/**
 *
 * @author mg31
 */
public class SummariseNoFilter{
    
    
public static void main(String[] args) throws Exception { //debug 
    
    String tag = "tcga";
    //String tag = "leu"; Summarise.ifAppendRef = false;
    
    String dir = "C:\\muxingu/data/own/SangerSoftware2/file/out_"+tag;
    Summarise ss = new Summarise(dir);
    
    ss.addParser( new Summarise.Parserf(new String[]{"PML-RARA","RARA-PML"},"PML-RARA","PML.RARA","PML-RARA_Ref"));
    ss.addParser( new Summarise.Parserf(new String[]{"CBFB-MYH11","MYH11-CBFB"},"MYH11-CBFB","MYH11.CBFB","MYH11-CBFB_Ref"));
    ss.addParser( new Summarise.Parserf(new String[]{"RUNX1-RUNX1T1","RUNX1T1-RUNX1"},"RUNX1-RUNX1T1","RUNX1.RUNX1T1","RUNX1-RUNX1T1_Ref"));
    ss.addParser( Sums.MLL_FUSION );
    ss.addParser( new Summarise.Parserf(new String[]{"BCR-ABL1","ABL1-BCR"},"BCR-ABL1","BCR.ABL","BCR-ABL1_Ref"));
    ss.addParser( new Summarise.Parserf(new String[]{"NSD1-NUP98","NUP98-NSD1"},"NUP98-NSD1","NUP98.NSD1","NUP98-NSD1_Ref"));
    
    ss.addParser(Sums.NPM1);
    ss.addParser(Sums.FLT3ITD);
    ss.addParser(Sums.MLLPTD);
    ss.addParser(Sums.FLT3);
    ss.addParser( new Summarise.Parser("CEBPA"));
    ss.addParser( new Summarise.Parser("DNMT3A") );
    ss.addParser( new Summarise.Parser("IDH1") );
    ss.addParser( new Summarise.Parser("IDH2") );
    ss.addParser( new Summarise.Parser("RUNX1") );
    ss.addParser( new Summarise.Parser("TP53") );
    ss.addParser( new Summarise.Parser("TET2"));
    
    ss.start();
    ss.otab.writeToFile(dir+"/summary_"+tag+".txt");
    
}
    
}
