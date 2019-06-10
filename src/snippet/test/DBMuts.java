/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet.test;

import fork.lib.base.collection.Pair;
import fork.lib.base.collection.Table;
import fork.lib.base.collection.Triplet;
import fork.lib.base.file.io.txt.ReadTable;
import fork.lib.bio.seq.CodonTranslator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import prog.core.aln.ele.IsoformStrand;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class DBMuts extends HashMap<String,HashMap<String,HashMap<String,Pair<String,String>>>> {
    
protected Index index;


    public DBMuts(Index index){
        this.index=index;
    }
    
    
private Pair<String,String> breakMut(String mut){
    if(mut.contains("fs")){
        return brs(mut,"fs");
    }else if(mut.contains("ins")){
        return brs(mut,"ins");
    }else if(mut.contains("del")){
        return brs(mut,"del");
    }else{
        try{
            String mid = mut.substring(1,mut.length()-1);
            int loc = Integer.parseInt(mid);
            return new Pair<>(mut.substring(0,mut.length()-1),mut.substring(mut.length()-1));
        }catch(Exception e){return null;}
    }
}
private Pair<String,String> brs(String mut, String s){
    int i = mut.indexOf(s);
    return new Pair<>(mut.substring(0,i), mut.substring(i));
}
    
public void addCosmic(File f, String gene)throws Exception { 
    ReadTable rt = new ReadTable(f);
    rt.param().setSkipRows(0); rt.param().setSep(",");
    Table<String> tab= rt.getTable();
    for( int i=0; i<tab.size(); i++ ){
        ArrayList<String> r = tab.getRow(i);
        String mut = r.get(18).replace("p.", "").replace(" ", "_");
        String _clin = r.get(27).replace(" ", "_"); String clin=_clin.charAt(0)+_clin.substring(1).toLowerCase();
        clin = clin.equals("null") ? "-" : clin;
        if( !mut.contains("?") ){
            Pair<String,String> br = breakMut(mut);
            if(br==null){continue;}
            addMut(gene,br.a(),br.b(),0,clin);
        }
    }
}

public void addSNP(File f, String gene)throws Exception { 
    IsoformStrand iso = index.getGene(gene).isoforms().get(0).strandSense();
    String nuc = iso.sequence(); 
    String seq = new CodonTranslator(nuc.substring(iso.codingInds().a(),iso.codingInds().b()+1)).frame(0);
    ReadTable rt = new ReadTable(f);
    Table<String> tab= rt.getTable();
    for( int i=0; i<tab.size(); i++ ){
        ArrayList<String> r = tab.getRow(i);
        String _mut = r.get(12);
        int li = _mut.indexOf("["), hi = _mut.indexOf("]");
        if(li!=-1 && hi!=-1){
            String mut = _mut.substring(li+1,hi);
            int loc = Integer.parseInt(r.get(14));
            char ori = seq.charAt(loc-1);
            String clin = r.get(9).replace(" ", "_");
            addMut(gene, ori+""+loc, mut, 1, clin);
        }
    }
}

private void addMut(String gene, String aa, String mut, int ab, String clin){
    if(!containsKey(gene)){
        put(gene, new HashMap<>());
    }
    HashMap<String,HashMap<String,Pair<String,String>>> map = get(gene);
    if(!map.containsKey(aa)){
        map.put(aa, new HashMap<>());
    }
    HashMap<String,Pair<String,String>> map2 = map.get(aa);
    if(!map2.containsKey(mut)){
        map2.put(mut, new Pair<>());
    }
    Pair<String,String> p = map2.get(mut);
    if(ab==0){
        p.setA(clin);
    }else{
        p.setB(clin);
    }
}

public void writeToFile(File out)throws Exception { 
    out.getAbsoluteFile().getParentFile().mkdirs();
    BufferedWriter bw= new BufferedWriter(new FileWriter(out));
    bw.write("Gene\tMutation\tCosmic\tdbSNP\n");
    for( String gene:keySet() ){
        ArrayList<String> locs = new ArrayList<>(); locs.addAll(get(gene).keySet());
        Collections.sort(locs, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return Integer.compare(gl(o1), gl(o2));
            }
            protected int gl(String s){
                if(s.contains("_")){
                    try{
                        return Integer.parseInt(s.substring(1,s.indexOf("_")));
                    }catch(Exception e){ return 0; }
                }else{
                    try{
                        return Integer.parseInt(s.substring(1));
                    }catch(Exception e){ return 0; }
                }
            }
        });
        for( String loc:locs ){
            HashMap<String,Pair<String,String>> map = get(gene).get(loc);
            for( String mut:map.keySet() ){
                Pair<String,String> p = map.get(mut);
                String va = p.a()==null ? "-" : p.a();
                String vb = p.b()==null ? "-" : p.b();
                //if(va.equals("-") && vb.equals("-")){continue;}
                bw.write(gene+"\t"+loc+mut+"\t"+va+"\t"+vb+"\n");
            }
        }
    }
    bw.close();
}
    

    
public static void main(String[] args) throws Exception { //debug 
    String dir = "C:\\muxingu\\data\\own\\SangerSoftware2\\file\\mut_meta";
    String indir = dir+"/db";
    
    Index index= IndexBuilder.read("C:/muxingu/data/own/SangerSoftware2/file/index.ind"); 
    
    String[] gns = new String[]{"CEBPA","DNMT3A","FLT3","IDH1","IDH2","NPM1","RUNX1","TET2","TP53"};
    
    DBMuts dd = new DBMuts(index);
    
    for( String gn:gns ){
        System.out.println("g:"+gn);
        File cf = new File(indir+"/cosmic_"+gn.toLowerCase()+".csv");
        File sf = new File(indir+"/snp_"+gn.toLowerCase()+".txt");
        dd.addCosmic(cf, gn);
        dd.addSNP(sf, gn);

    }
    
    dd.writeToFile( new File(dir+"/cosmic_dbsnp.txt") );
    
}
    
    
}
