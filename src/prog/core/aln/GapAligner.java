/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln;

import prog.core.aln.read.Read;
import prog.core.aln.read.Seed;
import prog.core.aln.read.ReadShrink;
import prog.core.Param;
import fork.lib.base.collection.Pair;
import fork.lib.bio.seq.align.NeedlemanWunsch;
import java.util.ArrayList;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.hot.ZZDebug;
import prog.core.aln.mut.AlignedElement;
import prog.core.aln.mut.AlignedMatch;
import prog.core.aln.mut.Insertion;
import prog.core.aln.mut.Deletion;
import prog.core.aln.mut.Substitution;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class GapAligner {
    
private Index index;
private IsoformStrand iso;
private Read read;
private ArrayList<Pair<Seed,Integer>> mseeds;
private Param par;


    public GapAligner(Index index, IsoformStrand iso, Read read, ArrayList<Pair<Seed,Integer>> mseeds, Param par){ 
        this.index = index;
        this.iso=iso;
        this.read=read;
        this.mseeds=mseeds;
        this.par=par;
    }

public Alignment align() throws Exception { 
    Alignment aln = new Alignment(iso,read); boolean ifc = false;
    for( int i=0; i<mseeds.size(); i++ ){
        //System.out.println("xxxxx i:"+i+"  p:"+mseeds.get(i).a().index()+"  l:" +mseeds.get(i).b());
        Pair<Seed,Integer> ms = mseeds.get(i); Seed sd = ms.a(); int loc = ms.b();
        if(i==0){
            if( sd.index()>0 ){
                // left gap
                boolean ifgood = leftGap(aln, read.sequence(), sd.index(), iso.sequence(), loc);
                if(!ifgood){ return null; }
            }
        }
        if(i<mseeds.size()-1){
            Pair<Seed,Integer> msn = mseeds.get(i+1); Seed sdn = msn.a(); 
            int loch = loc+index.kmer(), locn = msn.b();
            int posh = sd.index()+index.kmer(), posn = sdn.index();
            //  System.out.println("  "+loch+"  "+locn);
            if(posh==posn){
                if( loch < locn ){
                    match(aln, sd.index(), loc);
                    boolean ifc_ = gap(aln, read.sequence(), new int[]{sd.index()+index.kmer(),sdn.index()}, 
                            iso.sequence(), new int[]{loch,locn}, iso);
                    ifc = ifc || ifc_;
                }else if( loch > locn ){
                    //System.out.println(sd.index()+"  "+ sdn.index()+"  "+ loc+"  "+ loch+"  "+ locn);
                    int off = loch - locn, inspos = posh-off;
                    aln.add( new AlignedMatch(sd.index(),inspos-1, loc, locn-1) );
                    aln.add( new Insertion(inspos, locn, read.sequence().substring(inspos,sdn.index()) ) );
                    ifc = true;
                }else{ 
                    match(aln, sd.index(), loc);
                }
            }else{
                //System.out.println(posh+"  "+ posn+"  "+ loc+"  "+ loch+"  "+ locn);
                if( loch<locn ){
                    match(aln, sd.index(), loc);
                    boolean ifc_ = gap(aln, read.sequence(), new int[]{sd.index()+index.kmer(),sdn.index()}, iso.sequence(), new int[]{loch,locn}, iso);
                    ifc = ifc || ifc_;
                }else{ return null; }
            }
        }else{
            match(aln, sd.index(), loc);
            if( sd.index()+index.kmer()<read.length() ){
                // right gap
                boolean ifgood = rightGap(aln, read.sequence(), sd.index()+index.kmer(), iso.sequence(), loc+index.kmer());
                if(!ifgood){ return null; }
            }
        }
        //System.out.println("#######  "+ i); aln.print(); System.out.println("muts:"+aln.mutationNumber()); System.out.println();
    }
    aln.merge(); 
    if(ifc){
        if(!checkAlign(aln)){return null;}
    } if(!checkAlign(aln)){return null;}
    
    aln.reorder(read);
    return aln;
}

private boolean checkAlign(Alignment aln){
    if(aln.mutationNumber()>par.maxMutationsPerTranscript || 
            aln.mutationBases()> par.maxMutatedBases ){
        return false;
    }
    return true;
}

private boolean leftGap( Alignment aln, String seqstr, int pos, String refstr, int loc )throws Exception { 
    int len = (int)Math.min( pos, loc );
    return end(aln,seqstr,pos-len,refstr,loc-len,len,10);
}
private boolean rightGap( Alignment aln, String seqstr, int pos, String refstr, int loc )throws Exception { 
    int len = (int)Math.min(seqstr.length()-pos, refstr.length()-loc);
    return end(aln,seqstr,pos,refstr,loc,len,10);
}

private boolean end( Alignment aln, String seqstr, int pos, String refstr, int loc, int len, int thr)throws Exception { 
    String seq = seqstr.substring(pos,pos+len), ref = refstr.substring(loc,loc+len);
    if(len==0){ return true; }
    if(par.ifAlignEnds){
        gap(aln, seqstr, new int[]{pos,pos+len}, refstr, new int[]{loc,loc+len}, iso);
        return true;
    }else{
        int mm = 0;
        for( int i=0; i<seq.length(); i++ ){
            if(seq.charAt(i)!=ref.charAt(i)){ mm++; }
        }
        if(mm>thr){
            return false;
        }else{       
            aln.add( new AlignedMatch( new int[]{pos,pos+len-1}, new int[]{loc,loc+len-1} ) );
            return true;
        }
    }
}
private void match(Alignment aln, int pos, int loc)throws Exception { 
    aln.add( new AlignedMatch( new int[]{pos,pos+index.kmer()-1}, new int[]{loc,loc+index.kmer()-1} ) );
}

public static boolean gap(Alignment aln, String seqstr, int[] poslh, String refstr, int[] loclh, IsoformStrand iso )throws Exception { 
    String seq = seqstr.substring(poslh[0],poslh[1]), ref = refstr.substring(loclh[0],loclh[1]);
    boolean ifmut = false;
    //System.out.println(poslh[0]+" "+poslh[1]); System.out.println(loclh[0]+" "+loclh[1]);
    //System.out.println("seq: "+seq);System.out.println("ref: "+ref);
    
    ReadShrink rs= new ReadShrink(ref,seq);
    if( iso.isForward() ){
        rs.computeRight(); rs.computeLeft();
    }else{
        rs.computeLeft(); rs.computeRight();
    }
    String seqr = rs.sequenceRemain();
    String refr = rs.referenceRemain();
    
    //System.out.println("seq: "+rs.left()+" "+seqr+" "+rs.right()); System.out.println("ref: "+rs.left()+" "+refr+" "+rs.right());
    
    if(!rs.left().isEmpty()){
        aln.add( new AlignedMatch(new int[]{poslh[0],poslh[0]+rs.left().length()-1}, 
                new int[]{loclh[0],loclh[0]+rs.left().length()-1}) );
    }
    if(seqr.isEmpty()){
        if(refr.isEmpty()){
        }else{
            aln.add( new Deletion(loclh[0]+rs.left().length(), loclh[0]+rs.left().length()+refr.length()-1) );
        }
    }else{
        if(refr.isEmpty()){
            aln.add( new Insertion( poslh[0]+rs.left().length(), loclh[0]+rs.left().length() ,seqr) );
        }else{
            int pl = poslh[0]+rs.left().length(), ll= loclh[0]+rs.left().length();
            if(seqr.length()==refr.length()){
                for( int i=0; i<seqr.length(); i++ ){
                    if(seqr.charAt(i)==refr.charAt(i)){
                        aln.add( new AlignedMatch(pl+i,ll+i) );
                    }else{
                        aln.add( new Substitution(pl+i,ll+i,seqr.charAt(i),refr.charAt(i)) );
                        ifmut = true;
                    }
                }
            }else{
                // needleman wunsch
                ifmut = true;
                NeedlemanWunsch nw = new NeedlemanWunsch(seqr, refr); nw.compute();
                Pair<String,String> bestnw = bestNeedlemanWunsch( nw.alignments(), iso );
                String nwseq = bestnw.a(), nwref = bestnw.b();
                
                //System.out.println(nwseq);System.out.println(nwref);System.out.println();
                int lind= 0, hind=nwseq.length()-1;
                
                //System.out.println(lind+"  "+ hind);
                int seqind = pl, refind = ll;
                for( int i=0; i<nwseq.length(); i++ ){
                    boolean ifadd = i>=lind && i<=hind;
                    char s = nwseq.charAt(i), r = nwref.charAt(i);
                    if(r=='-'){
                        if(ifadd){aln.add( new Insertion(seqind, refind, Character.toString(s)) );}
                        seqind++;
                    }else if(s=='-'){
                        if(ifadd){aln.add( new Deletion(refind,refind) );}
                        refind++;
                    }else{
                        if(ifadd){
                            if(r==s){ aln.add( new AlignedMatch(seqind, refind) );
                            }else{aln.add( new Substitution(seqind, refind , s, r) );}
                        }
                        refind++; seqind++;
                    }
                }
            }
        }
    }
    if(!rs.right().isEmpty()){
        addRightMatch(aln, rs.right(), loclh[1], poslh[1]);
    }
    return ifmut;
}

private static void addRightMatch(Alignment aln, String right, int loc, int pos) throws Exception { 
    aln.add( new AlignedMatch(new int[]{pos-right.length(),pos-1}, new int[]{loc-right.length(),loc-1}) );
}

private static Pair<String,String> bestNeedlemanWunsch(ArrayList<Pair<String,String>> nws, IsoformStrand iso){
    double minv=Double.POSITIVE_INFINITY, maxv = Double.NEGATIVE_INFINITY;
    int minind = -1, maxind = -1;
    for( int i=0; i<nws.size(); i++ ){
        Pair<String,String> p = nws.get(i);
        double score = 0;
        for( int j=0; j<p.a().length(); j++ ){
            if(p.a().charAt(j)=='-'){
                score += (double)1/(1+j);
            }
        }
        for( int j=0; j<p.b().length(); j++ ){
            if(p.b().charAt(j)=='-'){
                score += (double)1/(1+j);
            }
        }
        //System.out.println(p.b()); System.out.println(p.a());System.out.println(score); System.out.println();
        if(score<minv){
            minv = score; minind = i;
        }
        if(score>maxv){
            maxv = score; maxind = i;
        }
    }
    if(iso.isForward()){
        return nws.get(minind);
    }else{
        return nws.get(maxind);
    }
}

public static void main(String[] args) throws Exception { //debug 
    ZZDebug.main(args);
}
    
}
