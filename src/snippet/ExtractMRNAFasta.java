/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snippet;

import fork.lib.base.file.io.txt.ReadTable;
import fork.lib.bio.seq.Nucleotide;
import fork.lib.bio.seq.parser.fasta.FastaEntry;
import fork.lib.bio.seq.parser.fasta.FastaReader;
import fork.lib.bio.seq.parser.fasta.FastaWriter;
import fork.lib.bio.seq.parser.gtfgff.GtfLine;
import fork.lib.bio.seq.parser.gtfgff.GtfReader;
import fork.lib.bio.seq.region.DirectionalGenomicRegion;
import fork.lib.bio.seq.region.Gene;
import fork.lib.bio.seq.region.GenomicRegion;
import fork.lib.bio.seq.region.Transcript;
import fork.lib.bio.seq.region.builder.GenomicRegionsBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import prog.core.index.IndexBuilderFasta;

/**
 *
 * @author mg31
 */
public class ExtractMRNAFasta {
    
    
    
public String chr, id, name;
public char std;
public ArrayList<Integer> lows, highs;

    public ExtractMRNAFasta(String chr, String id, String name, char std, ArrayList<Integer> lows, ArrayList<Integer> highs){
        this.id=id;
        this.name=name;
        this.chr=chr;
        this.std=std;
        this.lows=lows;
        this.highs=highs;
    }
    
    
    
public static ArrayList<Integer> toint(String[] ss){
    ArrayList<Integer> ret= new ArrayList<>();
    for( int i=0; i<ss.length; i++ ){
        String s= ss[i];
        if(!s.equals("")){
            ret.add(Integer.parseInt(s));
        }
    }
    return ret;
}
    
    
    
public static void main(String[] args) throws Exception { //debug 
    File f= new File("C:\\muxingu\\genome\\human\\grch38/ass/Homo_sapiens.GRCh38.93_pc.gtfmin");
    File spldir= new File("C:\\muxingu\\genome\\human\\grch38\\fasta/chr");
    
    String[] tars= {"NPM1","DNMT3A","TET2","FLT3","IDH2","IDH1","TP53","CEBPA","RUNX1","KMT2A"
            ,"PML","RARA","MYH11","RUNX1T1","CBFB",
            "AFDN","MLLT3","ELL","MLLT10","AFF1","MLLT1","EPS15","MLLT11",
            "BCR","ABL1"
            ,"NUP98","NSD1"}; 
    String afn = "gene.fa";
    
    HashSet<String> ccds = new HashSet<>();
    ccds.addAll( new ReadTable("C:\\muxingu\\genome\\human\\grch38\\ass/mart_export_with-ccds.txt").getColumnArray(1) );
    
    File out = new File("C:\\muxingu/data/own/SangerSoftware2/file/"+afn);
    HashSet<String> tarset= new HashSet<>();
    tarset.addAll(Arrays.asList(tars));
    HashSet<String> tarsin = new HashSet<>();
    
    HashMap<String,ArrayList<Gene>> chrgs = new HashMap<>();
    GtfReader grd= new GtfReader(f);
    GenomicRegionsBuilder gb= grd.genomicRegionsBuilder();
    for( GenomicRegion gr:gb ){
        Gene gene  = (Gene) gr;
        ArrayList<Transcript> trs= new ArrayList<>();
        for( Transcript tr:gene.children() ){
            GtfLine gl = (GtfLine) tr.attr();
            if( ccds.contains(gl.getField(GtfLine.FIELD_TRANSCRIPT_ID)) ){
                trs.add(tr);
            }
        }
        if(trs.isEmpty()){ continue; }
        Collections.sort(trs, new Comparator<Transcript>(){
            public int compare(Transcript o1, Transcript o2) {
                return Integer.compare(o2.cdsLength(), o1.cdsLength());
            }
        });
        ArrayList<Transcript> ntrs = new ArrayList<>(); ntrs.add(trs.get(0));
        
        Gene ngene = new Gene(gene.chr(), gene.strand(), ntrs);
        ngene.setAttribute(gene.attr());
        if(tarset.contains(ngene.name())){
            if(!chrgs.containsKey(ngene.chr())){
                chrgs.put(ngene.chr(), new ArrayList<>());
            }
            chrgs.get(ngene.chr()).add(ngene);
            tarsin.add(ngene.name());
        }
    }
    boolean iferr = false;
    for( String g:tarset ){
        if(!tarsin.contains(g)){ System.out.println(g); iferr=true; }
    }
    if(iferr){ System.exit(1); }
    
    FastaWriter fw= new FastaWriter(out); fw.param().charPerLine=100;
    for( String chr:chrgs.keySet() ){
        FastaReader fr= new FastaReader(spldir+"/chr"+chr+".txt");
        String chrseq = fr.nextEntry().sequence();
        String chrseqr = Nucleotide.pairedSequence(chrseq);
        for( Gene gene:chrgs.get(chr) ){
            for( Transcript tr:gene.children() ){
                String cds;
                if(gene.isOnForwardStrand()){
                    cds = tr.lowPoint()+","+(tr.highPoint()+3);
                }else{
                    cds = (tr.lowPoint()-3)+","+tr.highPoint();
                }
                String tit = tr.getID()+"_"+gene.name()+" chr"+tr.chr()+" "+tr.strand()+" "+cds+" ";
                for( DirectionalGenomicRegion ex:tr.children() ){
                    tit+=(int)ex.low()+"-"+(int)ex.high()+",";
                }
                String seq = tr.sequence(chrseq, chrseqr);
                fw.write(new FastaEntry(tit, seq));
            }
        }
    }
    fw.close();
    GeneFastaReverseComplement.main(args);
    IndexBuilderFasta.main(args);
}


}

