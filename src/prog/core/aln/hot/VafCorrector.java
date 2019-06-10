/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import fork.lib.base.Print;
import prog.core.aln.mut.MutationSpot;
import fork.lib.base.collection.Pair;
import fork.lib.base.collection.Triplet;
import fork.lib.base.file.FileName;
import fork.lib.bio.seq.Nucleotide;
import fork.lib.bio.seq.parser.fastq.FastqEntry;
import fork.lib.bio.seq.parser.fastq.FastqReader;
import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import prog.core.Param;
import prog.core.aln.Aligner;
import prog.core.aln.Alignment;
import prog.core.aln.res.AlignmentResult;
import prog.core.aln.ReadAligner;
import prog.core.aln.ele.Gene;
import prog.core.aln.ele.Isoform;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.mut.MutationResult;
import prog.core.aln.mut.AlignedElement;
import prog.core.aln.mut.Deletion;
import prog.core.aln.mut.Insertion;
import prog.core.aln.mut.Mutation;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;
import prog.core.aln.ele.TranscriptFragmentSet;
import prog.core.aln.mut.MutationSpotDeletion;
import prog.core.aln.mut.MutationSpotFusionPair;
import prog.core.aln.mut.MutationSpotFusionRead;
import prog.core.aln.mut.MutationSpotITD;
import prog.core.aln.mut.MutationSpotInsertion;
import prog.core.aln.mut.MutationSpotSubstitution;

/**
 *
 * @author mg31
 */
public class VafCorrector {

private static String GENE_WT = "wt";
private static String GENE_MUT = "mut";
private MutationSpot mut;
private ArrayList<Triplet<String,Region,String>> spss;
private ArrayList<Triplet<Index,Region,Index>> inds = new ArrayList<>();
private Param par;

private MutationResult mutres = new MutationResult();
    

    public VafCorrector( MutationSpot mut, ArrayList<Triplet<String,Region,String>> spss, Param par)throws Exception { 
        this.mut=mut;
        this.spss = spss;
        this.par=par;
        init();
    }

 
public MutationResult mutationResult(){ return mutres; }
    
protected void init() throws Exception { 
    par.ifAlignEnds=true;
}

public void correct(AlignmentResult res, Index index, ReadPool pool)throws Exception { 
    IsoformStrand iso = mut.isoformStrandSense(index);
    String gid = iso.geneID();
    if( mut instanceof MutationSpotSubstitution || mut instanceof MutationSpotInsertion ||
            mut instanceof MutationSpotDeletion ){
        for( Read read:mut.wtReads(pool) ){
            //if(!read.sequence().equals("CCCTGAGTCCCATCATCACTGCTGTTGAGTTTTTTTCATTCGGTACTGGC")){continue;}
            int rid = read.intID();
            RealignmentClassifier re = new RealignmentClassifier(read,inds);
            if( re.ifwt ){
                if(re.ifmut){
                    mut.mutReads.remove(rid);
                }else{
                    mut.wtReads.add(rid);
                    mut.mutReads.remove(rid);
                }
            }else{
                if(re.ifmut){
                    mut.mutReads.add(rid);
                    mut.wtReads.remove(rid);
                }else{
                    mut.wtReads.remove(rid);
                }
            }
            //System.out.println( Nucleotide.reverseComplement(read.sequence())); System.out.println(re.ifmut+" "+re.ifwt); System.exit(1);
        }
        int minc = par.hsSecondRunMinCountGene.containsKey(mut.gene()) ? par.hsSecondRunMinCountGene.get(mut.gene()) : par.hsSecondRunMinCount;
        int mhs = par.hsUniqueSeqMinCountGene.containsKey(mut.gene()) ? par.hsUniqueSeqMinCountGene.get(mut.gene()) : par.hsUniqueSeqMinCount;
        if(mut.mutReads.size()>=minc && mut.vaf()>par.hsSecondRunMinVAF && uniqueSeqs(mut.mutatedReads(pool))>=mhs ){
            mutres.add(mut);
        }
    }else if(mut instanceof MutationSpotITD){
        HashSet<Read> rers = new HashSet<>();
        HashSet<Read> wtrs = mut.wtReads(pool);
        rers.addAll(wtrs);
        for( Pair<Integer,Anchor> pair:res.alignmentResultITD().getAlignmentsForGeneITD(gid) ){
            rers.add(pool.get(pair.a()));
        }
        for( Read read:rers ){
            int rid = read.intID();
            RealignmentClassifier re = new RealignmentClassifier(read,inds);
            if(wtrs.contains(read)){
                if(re.ifmut && !re.ifwt){
                    mut.mutReads.add(rid);
                    mut.wtReads.remove(rid);
                }
            }else{
                if(re.ifmut && !re.ifwt){
                    mut.mutReads.add(rid);
                }
            }
        }
        int minc = par.itdSecondRunMinCountGene.containsKey(mut.gene())? par.itdSecondRunMinCountGene.get(mut.gene()):par.itdSecondRunMinCount;
        if(mut.mutReads.size()>=minc && mut.vaf()>par.itdSecondRunMinVAF && uniqueSeqs(mut.mutatedReads(pool))>=par.itdUniqueSeqMinCount){
            mutres.add(mut);
        }
    }else if(mut instanceof MutationSpotFusionRead){
        MutationSpotFusionRead mutf = (MutationSpotFusionRead) mut;
        HashSet<Read> rers = new HashSet<>();
        for( Pair<Integer,Anchor> pair: res.alignmentResultFusionRead().getReadForGeneTranslocation(mutf.gene1(), mutf.gene2())  ){
            rers.add(pool.get(pair.a()));
        }
        for( Read read:rers ){
            if(mut.mutReads.contains(read.intID())){ continue; }
            int rid = read.intID();
            RealignmentClassifier re = new RealignmentClassifier(read,inds);
            //System.out.println(read.sequence()+"  "+re.ifmut+" "+re.ifwt);
            if(re.ifmut && !re.ifwt){
                mut.mutReads.add(rid);
            }
        }
        if(mut.mutReads.size()>=par.fusionSecondRunMinCount){
            mutres.add(mut);
        }
    }else if(mut instanceof MutationSpotFusionPair){
        mutres.add(mut);
    }
}

private int uniqueSeqs(HashSet<Read> rs){
    HashSet<String> set = new HashSet<>();
    for( Read r:rs ){
        String s = r.sequence(), sr = Nucleotide.reverseComplement(s);
        if(!set.contains(s) && !set.contains(sr)){
            set.add(s);
        }
    }
    return set.size();
}


public void build()throws Exception { 
    if(mut instanceof MutationSpotFusionPair){
        return;
    }
    for( Triplet<String,Region,String> sps:spss ){
        inds.add(new Triplet<>( buildIndex(sps.a(),GENE_MUT), sps.b(), buildIndex(sps.c(),GENE_WT) ));
    }
}

private Index buildIndex(String seq, String tag)throws Exception { 
    Index ret = new Index(par.kmer);
    Gene gene = new Gene(GENE_WT,"c",'+');
    ArrayList<Pair<Integer,Integer>> lhs = new ArrayList<>(); lhs.add( new Pair<>(0,seq.length()-1) );
    gene.addTranscript( new TranscriptFragmentSet(tag,lhs,new Pair<>(0,seq.length()-1), seq) );
    ret.addGene(gene);
    return ret;
}



class Realigner{
    public int mismatch = -1;
    public boolean ifalign = false, ifmutcover=false, ifspan;
    public Alignment aln;
    public Realigner(Index index, Region reg, Read read, Param par) throws Exception { 
        Alignment aln_ = new ReadAligner(read,index,par).bestAlignment();
        if(aln_==null){ 
            return;
        }
        
        mismatch = aln_.mutationBases();
        if(mismatch>par.realignMaxMismatch){
            return;
        }
        IsoformStrand iso = aln_.isoformStrand();
        Region taregstd = targetRegionStranded(iso,reg);
        Region span = aln_.span();
        
        //aln_.print(); System.out.println();
        //System.out.println("tar: "+taregstd);
        
        if(span.overlapsWith(taregstd)){
            ifspan = true;
        }
        for( AlignedElement e:aln_.elements() ){
            if( e instanceof Mutation ){
                if(coversMutLoc(e,taregstd)){
                    ifmutcover = true;
                }
            }
        }
        ifalign = true;
        aln = aln_;
    }
}

class RealignmentClassifier{
    public boolean ifwt = false, ifmut = false;
    public Realigner wtaln, mutaln;
    public RealignmentClassifier(Read read, ArrayList<Triplet<Index,Region,Index>> inds)throws Exception { 
        for( Triplet<Index,Region,Index> iri:inds ){
            wtaln = new Realigner(iri.c(),iri.b(),read,par); 
            mutaln = new Realigner(iri.a(),iri.b(),read,par); 
            ifwt = wtaln.ifalign && wtaln.ifspan && !wtaln.ifmutcover;
            ifmut = mutaln.ifalign && mutaln.ifspan && !mutaln.ifmutcover;
            //System.out.println(read.id()+"  "+read.sequence());
            //System.out.println("m-mm:"+mutaln.mismatch+"  w-mm:"+wtaln.mismatch+"  maln:"+ifmut+"  waln:"+ifwt+"  mspan:"+mutaln.ifspan+"  wspan:"+wtaln.ifspan+"  mcov:"+mutaln.ifmutcover+"  wcov:"+wtaln.ifmutcover);
        }
    }
}



private static boolean coversMutLoc(AlignedElement e, Region taregstr)throws Exception { 
    Region mutreg;
    if( e instanceof Insertion ){
        Insertion ins = (Insertion) e;
        mutreg = new Region(ins.location(),ins.location()+ins.insertion().length()-1);
        //System.out.println("# "+ ins.location());
    }else{
        mutreg = e.referenceRegion();
    }
    //System.out.println("mut:"+ mutreg+"  tar:"+ taregstr);
    if(mutreg.overlapsWith(taregstr)){
        return true;
    }
    return false;
}

private static Region targetRegionStranded(IsoformStrand iso, Region tareg)throws Exception { 
    Region ret;
    if(iso.isForward()){
        ret = tareg;
    }else{
        ret = new Region( iso.sequence().length()-1-tareg.high(), iso.sequence().length()-1-tareg.low() );
    }
    return ret;
}




public static void main(String[] args) throws Exception { //debug 
    String dir = "C:\\muxingu\\data\\own\\SangerSoftware2\\file";
    
    String tt = "TCGA-AB-2948";
    
    args = args.length==0 ? new String[]{tt} : args;
    
    
    String tag = args[0];
    Param par = new Param();
    String aln = dir+"/pool/align_"+tag+".aln";
    String poolpath = dir+"/pool/pool_"+tag+".rds";

    //IndexBuilderFasta.main(args);
    Index index= IndexBuilder.read(dir+"/index.ind"); System.out.println("Index loaded. ");
    ReadPool pool = ReadPool.read(poolpath);
    AlignmentResult res = AlignmentResult.read(aln, index, pool);

    MutationResult allmuts = MutationResult.read(dir+"/hotspot_"+tag+".mut");
    MutationResult mutout= new MutationResult();
    for( MutationSpot mut:allmuts.allMutations() ){
        //if(!mut.aminoAcidMutation(index).equals("KMT2A.K1406-ELL.D46")){continue;}
        VafCorrector cor = new VafCorrector( mut, mut.reconstructedTranscripts( index, par.mostFrequencReadLength()), par );
        cor.build();
        cor.correct(res,index,pool);
        mutout.addAll(cor.mutationResult());
    }
    
    MutationResult.writeToTxtFile(mutout,index,pool,dir+"/hotspot_"+tag+"_corrected.txt", false);

}

}


