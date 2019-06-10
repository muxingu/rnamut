/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core;

import fork.lib.base.collection.Pair;
import fork.lib.math.applied.stat.FrequencyCount;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import prog.core.aln.mut.MutationFilter;

/**
 *
 * @author mg31
 */
public class Param implements Serializable{
    
// system
public boolean ifVerbose = true;
    
// index
public int kmer = 10;

// read
public FrequencyCount<Integer> readlens = new FrequencyCount();
public int mostFrequencReadLength(){
    Integer ret = readlens.mostFrequentKey();
    return ret==null ? 100 : ret;
}
public boolean ifClipEndN = false;
public int minReadLength = 40;
public int peMaxFragmentSize = 500;
    
// seed mapping
public int minMappedSeeds = 2;
public double minMappedSeedsFraction = 0.5;
public int minUniqueMappedSeeds = 2;
public int minOrderedSeeds = 2;
public double minOrderedSeedsFraction = 0.5;

// mutation filter
public double quality = 0.01;
public int maxInsertion = 30;
public int maxDeletion = 6;
public int maxMutationsPerTranscript = 3;
public int maxMutatedBases = 12;

public boolean ifAlignEnds = false;

// hotspot
public int hsFirstRunMinCount = 2;
public int hsUniqueSeqMinCount = 3;
public HashMap<String,Integer> hsUniqueSeqMinCountGene = new HashMap<>();
public double hsFirstRunMinVAF = 0.01;
public int hsSecondRunMinCount = 10;
public double hsSecondRunMinVAF = 0.05;
public HashMap<String,Integer> hsSecondRunMinCountGene = new HashMap<>();
// itd
public int itdMapMinConsecutiveSeeds = 2;
public int itdFirstRunMinCount = 2;
public int itdUniqueSeqMinCount = 3;
public double itdFirstRunMinVAF = 0.01;
public int itdSecondRunMinCount = 10;
public double itdSecondRunMinVAF = 0.05;
public int maxITDLength = 50000;
public HashMap<String,Integer> itdSecondRunMinCountGene = new HashMap<>();
// fusion
public int fusionFirstRunMinCount = 2;
public int fusionSecondRunMinCount = 5;
public int fusionPEMinCount = 5;
public double transPEMinVaf = 0.1;

// realign
public final int realignMaxMismatch = 2;

// indel
public HashSet<String> targetGenes = new HashSet<>();
public void addTargetGenes(String...gs){targetGenes.addAll(Arrays.asList(gs));}
public void setTargetGenes(String...gs){targetGenes = new HashSet<>(); targetGenes.addAll(Arrays.asList(gs)); }
// itd
public HashSet<String> itdGenes = new HashSet<>();
public void addItdGenes(String... gs){ itdGenes.addAll(Arrays.asList(gs)); }
public void setItdGenes(String... gs){ itdGenes = new HashSet<>(); itdGenes.addAll(Arrays.asList(gs)); }
// translocation
public HashMap<String,HashSet<String>> transPairs = new HashMap<>();
public HashMap<HashSet<String>,Pair<String,String>> correctPairs = new HashMap<>();
public HashSet<Pair<String,String>> allPairPermutations = new HashSet<>();


public MutationFilter filt = new MutationFilter();


    public Param(){
        addTargetGenes("NPM1","DNMT3A","TET2","FLT3","IDH2","IDH1","TP53","CEBPA","RUNX1");
        addItdGenes("FLT3","KMT2A");
        addTranslocationPair("PML","RARA");
        addTranslocationPair("CBFB","MYH11");
        addTranslocationPair("RUNX1","RUNX1T1");
        addTranslocationPair("BCR","ABL1");
        addTranslocationPair("NUP98","NSD1");
        addTranslocationPair("KMT2A","AFDN");
        addTranslocationPair("KMT2A","MLLT3");
        addTranslocationPair("KMT2A","ELL");
        addTranslocationPair("KMT2A","MLLT10");
        addTranslocationPair("KMT2A","AFF1");
        addTranslocationPair("KMT2A","MLLT1");
        addTranslocationPair("KMT2A","EPS15");
        addTranslocationPair("KMT2A","MLLT11");
        
        hsSecondRunMinCountGene.put("DNMT3A", 3);
        hsSecondRunMinCountGene.put("TP53", 2);
        hsSecondRunMinCountGene.put("TET2", 6);
        itdSecondRunMinCountGene.put("KMT2A", 5);
        hsUniqueSeqMinCountGene.put("TP53", 2);
        init(); 
    }
    
    
protected void init(){
    for( String a:transPairs.keySet() ){
        for( String b:transPairs.get(a) ){
            allPairPermutations.add(new Pair<>(a,b));
        }
    }
}
    
    

public Pair<String,String> getCorrectPair(String a, String b){
    HashSet<String> set = new HashSet<>();
    set.addAll(Arrays.asList(new String[]{a,b}));
    return correctPairs.get(set);
}
public void addTranslocationPair(String ga, String gb){
    addTranslocationPairOrder(ga,gb,true);
    addTranslocationPairOrder(gb,ga,false);
}
private void addTranslocationPairOrder(String ga, String gb, boolean ifcorrect){
    if(!transPairs.containsKey(ga)){
        transPairs.put(ga, new HashSet<>());
    }
    transPairs.get(ga).add(gb);
    HashSet<String> set = new HashSet<>();
    set.addAll(Arrays.asList(new String[]{ga,gb}));
    if(ifcorrect){
        correctPairs.put(set, new Pair<>(ga,gb));
    }else{
        correctPairs.put(set, new Pair<>(gb,ga));
    }
}




public static void main(String[] args) throws Exception { //debug 
    String a = "123";
}

    
    
}


