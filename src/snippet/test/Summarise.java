/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet.test;

import fork.lib.base.collection.NamedTable;
import fork.lib.base.collection.Pair;
import fork.lib.base.collection.Table;
import fork.lib.base.file.FileName;
import fork.lib.base.file.io.txt.ReadTable;
import fork.lib.base.file.io.txt.ReadTableParam;
import fork.lib.base.format.collection.ArrayOp1D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author mg31
 */
public class Summarise {
 
protected String dir; 
protected String outn;
protected String d;
static boolean ifAppendRef = true;

protected static NamedTable<String,String,String> metab;
protected static HashMap<String,HashMap<String,Pair<String,String>>> patho = new HashMap<>();
protected ArrayList<Parser> pars = new ArrayList<>();
protected File[] fs;

public NamedTable<String,String,String> otab = new NamedTable<>();

static{
    try{
        metab = new ReadTable("C:\\muxingu/data/own/SangerSoftware2/file/mut_meta/fab_class_.txt").getTable().toNamedTable();
        Table<String> tt = new ReadTable("C:\\muxingu/data/own/SangerSoftware2/file/mut_meta/cosmic_dbsnp.txt").getTable();
        for( int i=0; i<tt.rowNumber(); i++ ){
            ArrayList<String> r = tt.getRow(i);
            String g=r.get(0), m = r.get(1);
            if(m.contains("*")){
                m = m.substring(0,m.indexOf("*"));
            }
            if(!patho.containsKey(g)){
                patho.put(g, new HashMap<>());
            }
            patho.get(g).put(m, new Pair<>(r.get(2),r.get(3)));
        }
    }catch(Exception e){
        e.printStackTrace();
        System.exit(1);
    }
}

    
    public Summarise(String dir)throws Exception {        
        this.dir=dir;
        this.outn = FileName.baseName(dir);
        this.d = dir+"/mut_correct";
        fs = new File(d).listFiles();
        otab.DEFAULT = "-";
    }
    
    
private String tag(File fn){return fn.getName().replace("hotspot-corr_", "").replace(".txt", "");}
public void addParser(Parser par){pars.add(par);}

public void start()throws Exception { 
    for( File f:fs ){
        otab.appendEmptyRow(tag(f));
    }
    for( Parser p:pars ){
        otab.appendEmptyColumn(p.colnout);
        if(ifAppendRef){
            otab.appendColumn(p.refcolnout, p.refcol );
        }
    }
    for( File f:fs ){
        //if(!f.getName().contains("2934")){ continue; }
        
        ReadTable rt = new ReadTable(f); rt.setSkipRows(0);
        Table<String> tab = rt.getTable(); String tag = tag(f);
        for( int i=0; i<tab.rowNumber(); i++ ){
            ArrayList<String> r = tab.getRow(i);
            String gene=r.get(0), loc = r.get(1), aa = r.get(2), 
                mut = r.get(3), wt=r.get(4), vaf = r.get(5);
            for( Parser par:pars ){
                if(par.ifadd(gene, loc, aa, wt, mut, vaf)){
                    String nv = par.value(gene,loc,aa,wt,mut,vaf);
                    String ov = otab.getValueAt(tag, par.colnout);
                    if(ov==null){
                        otab.setValueAt(nv, tag, par.colnout);
                    }else{
                        otab.setValueAt(ov+",,"+nv, tag, par.colnout);
                    }
                }
            }
        }
    }
}
    

public static class Parser{
    protected HashSet<String> cols = new HashSet<>();
    public String colnout, refcoln, refcolnout;
    public ArrayList<String> refcol = new ArrayList<>();
    Parser(String[] scols, String col, String refcoln, String refcolout){
        this.colnout=col;
        this.refcoln=refcoln;
        this.refcolnout=refcolout;
        this.cols.addAll(Arrays.asList(scols));
        initRefcol();
        for( String v:refcol ){
            for( String g:v.split(",") ){
                String g_ = g.replace("p.", "");
                String xx = "in_frame_ins";
                if(g_.contains(xx)){ g_=g_.substring(0, g_.indexOf(xx)+xx.length()); }
            }
        }
    }
    Parser(String s){ this(new String[]{s},s,s,s+"_Ref"); }
    protected void initRefcol(){
        refcol = metab.getColumn(refcoln);
    }
    protected boolean ishotspot(String aa, String vs){
        return false;
    }
    protected boolean ifvaf(String aa, String vs){
        double v = Double.parseDouble(vs);
        if(aa.contains("fs") || aa.contains("Ins")){
            return v>0.096;
        }else if(aa.contains("*")){
            return v>0.1;
        }
        return v>0.15;
    }
    protected boolean ifmutgood(String loc, String aa, String gene){
        boolean ret = true;
        if(aa.contains("UTR")){
            return false;
        }
        if(aa.contains("Ins") || aa.contains("Del")){
            return true;
        }
        HashMap<String,Pair<String,String>> map = patho.get(gene);
        if(map==null){return false;}
        Pair<String,String> vs = map.get(aa);
        //if(aa.contains("R142fs")){
            //System.out.println(vs);System.exit(1);
        //}
        if(vs!=null){
            String sa = vs.a(), sb = vs.b();
            ret = sa.contains("athogenic") || sb.contains("athogenic");
        }
        return ret;
    }
    public boolean ifadd(String gene, String loc, String aa, String wt, String mut, String vaf){
        if(ishotspot(aa,vaf)){
            return true;
        }
        return cols.contains(gene) && ifmutgood(loc, aa,gene) && ifvaf(aa,vaf);
    }
    public String value(String gene, String loc, String aa, String wt, String mut, String vaf){
        return aa+"_"+vaf;
    }
}

public static class Parserf extends Parser{
    public Parserf(String[] scols, String col, String refcol, String refcolout){
        super(scols,col,refcol,refcolout);
    }
    @Override
    protected boolean ifvaf(String mut, String vafstr){return true;}
    @Override
    protected boolean ifmutgood(String loc,String aa, String gene){return true; }
    @Override
    public String value(String gene, String loc, String aa, String wt, String mut, String vaf){
        return aa+"_"+mut+"_"+wt;
    }
}

public static ArrayList<String> selectAA(String refcoln, String tar, boolean contain){
    ArrayList<String> vs = metab.getColumn(refcoln);
    ArrayList<String> ret = new ArrayList<>();
    for( String v:vs ){
        String addv = "";
        for( String s:v.split(",") ){
            if( contain ? s.contains(tar) : !s.contains(tar)){
                addv += s;
            }
        }
        ret.add( addv.equals("") ? "-" : addv);
    }
    return ret;
}



public static void main(String[] args) throws Exception { //debug 
    String tag = "tcga";
    //String tag = "leu"; Summarise.ifAppendRef = false;
    
    String dir = "C:\\muxingu/data/own/SangerSoftware2/file/out_"+tag;
    Summarise ss = new Summarise(dir);
    
    ss.addParser( new Parserf(new String[]{"PML-RARA","RARA-PML"},"PML-RARA","PML.RARA","PML-RARA_Ref"));
    ss.addParser( new Parserf(new String[]{"CBFB-MYH11","MYH11-CBFB"},"MYH11-CBFB","MYH11.CBFB","MYH11-CBFB_Ref"));
    ss.addParser( new Parserf(new String[]{"RUNX1-RUNX1T1","RUNX1T1-RUNX1"},"RUNX1-RUNX1T1","RUNX1.RUNX1T1","RUNX1-RUNX1T1_Ref"));
    ss.addParser( Sums.MLL_FUSION );
    ss.addParser( new Parserf(new String[]{"BCR-ABL1","ABL1-BCR"},"BCR-ABL1","BCR.ABL","BCR-ABL1_Ref"));
    ss.addParser( new Parserf(new String[]{"NSD1-NUP98","NUP98-NSD1"},"NUP98-NSD1","NUP98.NSD1","NUP98-NSD1_Ref"));
    
    ss.addParser(Sums.NPM1);
    ss.addParser(Sums.FLT3ITD);
    ss.addParser(Sums.MLLPTD);
    ss.addParser(Sums.FLT3);
    ss.addParser(Sums.CEBPA);
    ss.addParser(Sums.DNMT3A);
    ss.addParser(Sums.IDH1);
    ss.addParser(Sums.IDH2);
    ss.addParser(Sums.RUNX1);
    ss.addParser(Sums.TP53);
    ss.addParser(Sums.TET2);
    
    ss.start();
    ss.otab.writeToFile(dir+"/summary_"+tag+".txt");

}
    
}
