/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet.test;

import snippet.test.Summarise.Parser;
import static snippet.test.Summarise.selectAA;

/**
 *
 * @author mg31
 */
public class Sums {


public static Parser NPM1 = new Summarise.Parser("NPM1"){
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){
        if(aa.contains("W288")){ 
            return true; 
        } 
        return false;
    }
    @Override
    protected boolean ishotspot(String aa, String vs){
        if(aa.contains("W288")){
            return true;
        }
        return false;
    }
};    

public static Parser IDH1 = new Summarise.Parser("IDH1"){
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){
        if(aa.contains("R132") || aa.equals("F32V") || aa.equals("Y183C")){
            return true;
        }
        return false;
    }
    @Override
    protected boolean ishotspot(String aa, String vs){
        if(aa.contains("R132")){
            return true;
        }
        return false;
    }
};
public static Parser IDH2 = new Summarise.Parser("IDH2"){
    @Override
    protected boolean ishotspot(String aa, String vs){
        if(aa.contains("R140") || aa.contains("R172")){
            return true;
        }
        return false;
    }
};

public static Parser FLT3 = new Parser("FLT3"){
    @Override
    protected void initRefcol(){
        refcol=selectAA(refcoln,"in_frame_ins",false);
    }
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){
        if(aa.contains("Ins")){
            return false;
        }
        if(aa.contains("D835") || aa.equals("S451F") || aa.equals("D839G")){
            return true;
        }
        if(aa.equals("D7G")||aa.equals("V16L")||aa.equals("V194M")||aa.equals("T227M")||aa.equals("D324N")||
                aa.equals("I417L")||aa.equals("V557I")||aa.equals("K623I")||aa.equals("R973Q")){
            return false;
        }
        return false;
        //return super.ifmutgood(loc, aa, gene);
    }
    @Override
    protected boolean ishotspot(String aa, String vs){
        if(aa.contains("D835")){
            return true;
        }
        return false;
    }
};

public static Parser CEBPA = new Summarise.Parser("CEBPA"){
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){
        if(aa.contains("fs") || aa.contains("*")){
            return true;
        }
        if(aa.contains("196") ){
            return false;
        }
        return super.ifmutgood(loc, aa, gene);
    }
};

public static Parser DNMT3A = new Summarise.Parser("DNMT3A"){
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){
        if(aa.contains("fs") || aa.contains("*") || aa.contains("R882")
                ||aa.equals("S129L")||aa.equals("N501S")||aa.equals("G543C")||aa.equals("S714C")||
                aa.equals("K468R") || aa.equals("K829R")){
            return true;
        }
        if(aa.equals("E30A")){
            return false;
        }
        return super.ifmutgood(loc, aa, gene);
    }
    @Override
    protected boolean ishotspot(String aa, String vs){
        if(aa.contains("R882")){
            return true;
        }
        return false;
    }
};

public static Parser RUNX1 = new Summarise.Parser("RUNX1"){
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){
        if(aa.contains("fs") || aa.contains("*") || aa.contains("R162") ||
                aa.equals("L56S")||aa.equals("A60V")||aa.equals("D198G")||aa.equals("R207W") ){
            return true;
        }
        if(aa.contains("K110Q") || aa.contains("S141A")){
            return false;
        }
        return super.ifmutgood(loc, aa, gene);
    }
};

public static Parser TET2 = new Summarise.Parser("TET2"){
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){
        if(aa.contains("fs") || aa.contains("*") || aa.contains("R162") ||
                aa.equals("P174H")||aa.equals("C1289S")||aa.equals("G1869W") ){
            return true;
        }
        if( aa.equals("P29R")||aa.equals("L34F")||aa.equals("V218M")||aa.equals("G355D")||aa.equals("P363L")||
                aa.equals("M533I")||aa.equals("Q810R")||aa.equals("Y867H")||aa.equals("Q1084P")||
                aa.equals("M1701I")||aa.equals("V1718L")||aa.equals("L1721W")||aa.equals("P1723S")||
                aa.equals("I1762V")||aa.equals("H1778R") || aa.equals("V1949fs") ){
            return false;
        }
        return super.ifmutgood(loc, aa, gene);
    }
};

public static Parser TP53 = new Summarise.Parser("TP53"){
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){
        if(aa.contains("fs") || aa.contains("*") || aa.contains("R162") ||
                aa.equals("H193Y")||aa.equals("I195S")||aa.equals("Y205C")||aa.equals("Y220C")||
                aa.equals("R248Q")||aa.equals("R248W")||aa.equals("G266R")||aa.equals("V272M")||
                aa.equals("R273H")||aa.equals("R273C")||aa.equals("R282W") ){
            return true;
        }
        if( aa.equals("P72R") ){
            return false;
        }
        return super.ifmutgood(loc, aa, gene);
    }
};

public static Parser FLT3ITD = new Parser(new String[]{"FLT3"},"FLT3.ITD","FLT3","FLT.ITD_Ref"){
    @Override
    protected void initRefcol(){
        refcol=selectAA(refcoln,"in_frame_ins",true);
    }
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){return aa.contains("Ins"); }
    @Override
    protected boolean ishotspot(String aa, String vs){
        if(aa.contains("Ins")){
            int v = Integer.parseInt(aa.split("_Ins")[0]);
            if(v>580 && v<620){
                double vaf = Double.parseDouble(vs);
                //if(vaf>0.1){
                    return true;
                //}
            }
        }
        return false;
    }
};

public static Parser MLLPTD = new Parser(new String[]{"KMT2A"},"MLL.PTD","MLL.PTD","MLL.PTD_Ref"){
    @Override
    protected boolean ifmutgood(String loc, String aa, String gene){return aa.indexOf("exon")==0; }
    @Override
    protected boolean ifvaf(String mut, String vafstr){
        return Double.parseDouble(vafstr)>0.05;
    }
};

public static Parser MLL_FUSION = new Summarise.Parserf(
        new String[]{"KMT2A-AFDN","AFDN-KMT2A",
                "KMT2A-MLLT3","MLLT3-KMT2A",
                "KMT2A-ELL","ELL-KMT2A",
                "KMT2A-MLLT10","MLLT10-KMT2A",
                "KMT2A-AFF1","AFF1-KMT2A",
                "KMT2A-MLLT1","MLLT1-KMT2A",
                "KMT2A-EPS15","EPS15-KMT2A",
                "KMT2A-MLLT11","MLLT11-KMT2A",
            },
            "MLLs","MLL.partner","MLL.partner_Ref"
);



public static void main(String[] args) throws Exception { //debug 
    Summarise.main(args);
}

}



