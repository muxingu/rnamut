/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln;

import fork.lib.math.algebra.elementary.set.continuous.Region;
import java.io.Serializable;
import prog.core.aln.read.Read;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import prog.core.aln.mut.MutationSpot;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.mut.AlignedElement;
import prog.core.aln.mut.AlignedMatch;
import prog.core.aln.mut.Deletion;
import prog.core.aln.mut.Insertion;
import prog.core.aln.mut.Mutation;
import prog.core.aln.mut.Substitution;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class Alignment implements Serializable{
    
private String isostr;
private int readID;
private ArrayList<AlignedElement> eles = new ArrayList<>();

private transient IsoformStrand iso;
private transient double mmscore = Double.NEGATIVE_INFINITY;


    public Alignment(String isostr, Read read){
        this.isostr=isostr;
        this.readID = read.intID();
    }
    public Alignment(IsoformStrand iso, Read read){
        this(iso.uniqueID(), read);
        this.iso = iso;
    }
    public Alignment(){}

    
public void initTransient(Index index, ReadPool pool){
    this.iso=index.getIsoformStrand(isostr);
}
    
public void add(AlignedElement e) throws Exception { 
    if(eles.isEmpty()){
        eles.add(e); return;
    }
    AlignedElement pr = eles.get(eles.size()-1);
    if(pr instanceof Deletion){
        if(e instanceof Deletion){
            if( pr.referenceRegion().high()!=e.referenceRegion().low()-1 ){ 
                System.err.println("del-del: "+ pr+"  "+ e);
                throw new Exception();
            }
        }else if (e instanceof Insertion){
            System.err.println("del-ins: "+ pr+"  "+ e);
                throw new Exception();
        }else{
        }
    }else if(pr instanceof Insertion){
        if(e instanceof Deletion){
        }else if (e instanceof Insertion){
        }else{
        }
    }else{
        if(e instanceof Deletion){
        }else if (e instanceof Insertion){
        }else{
            if( pr.referenceRegion().high()!=e.referenceRegion().low()-1 || 
                    pr.sequenceRegion().high()!=e.sequenceRegion().low()-1){
                System.err.println("mat-mat: "+ pr+"  "+ e);
                throw new Exception(); 
            }
        }
    }
    eles.add(e);
}

public Region span()throws Exception { 
    Region rb = eles.get(0).referenceRegion(), re = eles.get(eles.size()-1).referenceRegion();
    return new Region( Math.min(rb.low(), re.low()), Math.max(rb.high(), re.high()) );
    /*
    double low = Double.POSITIVE_INFINITY, high = Double.NEGATIVE_INFINITY;
    for( AlignedElement e:eles ){
        Region r = e.referenceRegion();
        if(r!=null){
            if(r.low()<low){ low=r.low(); }
            if(r.high()>high){ high=r.high(); }
        }
    }
    return new Region(low,high);
    */
}
    
public void print(){
    for( AlignedElement e:eles ){
        System.out.println(e);
    }
}
public void printMutation()throws Exception { printMutation("\n"); }
public void printMutation(String sep)throws Exception { 
    for( AlignedElement e:eles ){
        if(e instanceof Mutation){
            MutationSpot ms = ((Mutation) e).mutationSpot(iso);
            System.out.print( ms.toString() + sep );
        }
    }
}

public IsoformStrand isoformStrand(){return iso;}
public String geneID(){return isoformStrand().geneID(); }
public void addMerge(AlignedElement e)throws Exception { add(e); merge(); }
public ArrayList<AlignedElement> elements(){return eles;}
public int getReadID(){return readID;}
public Read getRead(ReadPool p){return p.get(readID);}

public double mismatchScore(){ 
    if(mmscore==Double.NEGATIVE_INFINITY){
        calculateMismatchScore();
    }
    return mmscore;
}

private void calculateMismatchScore(){
    mmscore = 0;
    for( AlignedElement e:eles ){
        if(e instanceof Insertion || e instanceof Deletion){
            mmscore += 10;
            if(e instanceof Insertion){
                mmscore += ((Insertion)e).insertion().length();
            }else if (e instanceof Deletion){
                mmscore += ((Deletion)e).referenceRegion().getRange()+1;
            }
        }else if(e instanceof Substitution){
            mmscore += 1.5;
        }
    } 
}

public int mutationNumber(){
    int n=0;
    for( AlignedElement e:eles ){ if(e instanceof Mutation){n++;} }
    return n;
}

public int mutationBases(){
    int n=0;
    for( AlignedElement e:eles ){ 
        if(e instanceof Substitution){
            n+= (int)e.referenceRegion().getRange()+1;
        }else if(e instanceof Insertion){
            n+= ((Insertion)e).insertion().length();
        }else if(e instanceof Deletion){
            n+= (int)e.referenceRegion().getRange()+1;
        }
    }
    return n;
}

public void merge()throws Exception { 
    ArrayList<AlignedElement> eles_ = new ArrayList<>();
    for( int i=0; i<eles.size(); i++ ){
        AlignedElement e= eles.get(i);
        if(eles_.isEmpty()){ eles_.add(e); continue; }
        AlignedElement prev= eles_.get(eles_.size()-1); 
        boolean ifadd = false;
        if(prev instanceof AlignedMatch && e instanceof AlignedMatch){
            if( prev.referenceRegion().high() == (e.referenceRegion().low()-1) &&
                    prev.sequenceRegion().high() == (e.sequenceRegion().low()-1) ){
                AlignedMatch nm = new AlignedMatch( 
                        new int[]{ (int)prev.sequenceRegion().low(), (int) e.sequenceRegion().high() },
                        new int[]{ (int)prev.referenceRegion().low(), (int) e.referenceRegion().high() } );
                eles_.set(eles_.size()-1, nm);
            }else{ ifadd=true; }
        }else if(prev instanceof Insertion && e instanceof Insertion){
            if( ((Insertion)prev).location() == ((Insertion)e).location() &&
                    prev.sequenceRegion().high() == e.sequenceRegion().low()-1 ){
                Insertion ni = new Insertion( (int)prev.sequenceRegion().low(), ((Insertion)e).location(), 
                        ((Insertion)prev).insertion()+((Insertion)e).insertion() );
                eles_.set(eles_.size()-1, ni);
            }else{ ifadd=true; }
        }else if(prev instanceof Deletion && e instanceof Deletion){
            if( prev.referenceRegion().high()==e.referenceRegion().low()-1 ){
                Deletion nd = new Deletion( (int)prev.referenceRegion().low(), (int)e.referenceRegion().high() );
                eles_.set(eles_.size()-1, nd);
            }else{ ifadd=true; }
        }else if(prev instanceof Substitution && e instanceof Substitution){
            if( prev.referenceRegion().high()==e.referenceRegion().low()-1 ){
                Substitution nd = new Substitution( (int)prev.sequenceRegion().low(), (int)prev.referenceRegion().low(),
                        ((Substitution)prev).substitutionChars()+((Substitution)e).substitutionChars(),
                        ((Substitution)prev).originalChars()+((Substitution)e).originalChars() );
                eles_.set(eles_.size()-1, nd);
            }else{ ifadd=true; }
        }else{ ifadd=true; }
        if(ifadd){ 
            eles_.add(e); 
        }
    }
    eles=eles_;
}

public void reorder(Read rd)throws Exception { 
    if(iso.isForward()){
        for( int i=1; i<eles.size()-1; i++ ){
            AlignedElement pr = eles.get(i-1);
            AlignedElement pn = eles.get(i+1);
            if( pr instanceof AlignedMatch && pn instanceof AlignedMatch ){
                AlignedElement ele = eles.get(i);
                if(ele instanceof Deletion){
                    reorderDeletionForward(i,pr,ele,pn,rd);
                }else if(ele instanceof Insertion){
                    reorderInsertionForward(i,pr,ele,pn,rd);
                }
            }
        }
    }else{
        for( int i=1; i<eles.size()-1; i++ ){
            AlignedElement pr = eles.get(i-1);
            AlignedElement pn = eles.get(i+1);
            if( pr instanceof AlignedMatch && pn instanceof AlignedMatch ){
                AlignedElement ele = eles.get(i);
                if(ele instanceof Deletion){
                    reorderDeletionReverse(i,pr,ele,pn,rd);
                }else if(ele instanceof Insertion){
                    reorderInsertionReverse(i,pr,ele,pn,rd);
                }
            }
        }
    }
}

private void reorderDeletionForward(int i, AlignedElement pr, AlignedElement ele, AlignedElement pn, Read read)throws Exception { 
    int shift = 0;
    while(true){
        int sind = (int)pr.sequenceRegion().high()-shift, rind=(int)ele.referenceRegion().high()-shift;
        if( sind<0 || sind<=pr.sequenceRegion().low() ){break;}
        if( rind<0 || rind<=pr.referenceRegion().low() ){break;}
        if( read.sequence().charAt(sind)!=iso.sequence().charAt(rind) ){break;}
        shift++;
    }
    if(shift>0){
        eles.set(i-1, new AlignedMatch( pr.sequenceRegion().low(), pr.sequenceRegion().high()-shift, 
                pr.referenceRegion().low(), pr.referenceRegion().high()-shift));
        eles.set(i, new Deletion( ele.referenceRegion().low()-shift, ele.referenceRegion().high()-shift));
        eles.set(i+1, new AlignedMatch( pn.sequenceRegion().low()-shift, pn.sequenceRegion().high(), 
                pn.referenceRegion().low()-shift, pn.referenceRegion().high() ));
    }
}

private void reorderInsertionForward(int i, AlignedElement pr, AlignedElement ele, AlignedElement pn, Read read)throws Exception { 
    int shift = 0;
    while(true){
        int sind = (int)ele.sequenceRegion().high()-shift, rind=(int)pr.referenceRegion().high()-shift;
        if( sind<0 || sind<=pr.sequenceRegion().low() ){ break; }
        if( rind<0 || rind<=pr.referenceRegion().low() ){ break; }
        if( read.sequence().charAt(sind)!=iso.sequence().charAt(rind) ){break;}
        shift++;
    }
    if(shift>0){
        Insertion ins = (Insertion)ele; int low =(int)ins.sequenceRegion().low()-shift;
        eles.set(i-1, new AlignedMatch( pr.sequenceRegion().low(), pr.sequenceRegion().high()-shift, 
                pr.referenceRegion().low(), pr.referenceRegion().high()-shift));
        eles.set(i, new Insertion( (int)ele.sequenceRegion().low()-shift, ins.location()-shift, read.sequence().substring(low,low+ins.insertion().length())));
        eles.set(i+1, new AlignedMatch( pn.sequenceRegion().low()-shift, pn.sequenceRegion().high(), 
                pn.referenceRegion().low()-shift, pn.referenceRegion().high() ));
    }
}

private void reorderDeletionReverse(int i, AlignedElement pr, AlignedElement ele, AlignedElement pn, Read read)throws Exception { 
    int shift = 0;
    while(true){
        int sind = (int)pn.sequenceRegion().low()+shift, rind=(int)ele.referenceRegion().low()+shift;
        if( sind>=read.sequence().length() || sind>=pn.sequenceRegion().high() ){ break; }
        if( rind>=iso.sequence().length() || rind>=pn.sequenceRegion().high() ){ break; }
        if( read.sequence().charAt(sind)!=iso.sequence().charAt(rind) ){break;}
        shift++;
    }
    if(shift>0){
        eles.set(i-1, new AlignedMatch( pr.sequenceRegion().low(), pr.sequenceRegion().high()+shift, 
                pr.referenceRegion().low(), pr.referenceRegion().high()+shift));
        eles.set(i, new Deletion( ele.referenceRegion().low()+shift, ele.referenceRegion().high()+shift));
        eles.set(i+1, new AlignedMatch( pn.sequenceRegion().low()+shift, pn.sequenceRegion().high(), 
                pn.referenceRegion().low()+shift, pn.referenceRegion().high() ));
        
    }
}

private void reorderInsertionReverse(int i, AlignedElement pr, AlignedElement ele, AlignedElement pn, Read read)throws Exception { 
    int shift = 0;
    while(true){
        int sind = (int)ele.sequenceRegion().low()+shift, rind=(int)pn.referenceRegion().low()+shift;
        if( sind>=read.sequence().length() || sind>=pn.sequenceRegion().high() ){ break; }
        if( rind>=iso.sequence().length() || rind>=pn.referenceRegion().high() ){ break; }
        if( read.sequence().charAt(sind)!=iso.sequence().charAt(rind) ){break;}
        shift++;
    }
    if(shift>0){
        Insertion ins = (Insertion)ele; int low =(int)ins.sequenceRegion().low()+shift;
        eles.set(i-1, new AlignedMatch( pr.sequenceRegion().low(), pr.sequenceRegion().high()+shift, 
                pr.referenceRegion().low(), pr.referenceRegion().high()+shift));
        eles.set(i, new Insertion( (int)ele.sequenceRegion().low()+shift, ins.location()+shift, read.sequence().substring(low,low+ins.insertion().length())));
        eles.set(i+1, new AlignedMatch( pn.sequenceRegion().low()+shift, pn.sequenceRegion().high(), 
                pn.referenceRegion().low()+shift, pn.referenceRegion().high() ));
    }
}

public void printAlignmentLocation(){
    int low = (int) eles.get(0).referenceRegion().low();
    int high = (int) eles.get(0).referenceRegion().high();
    System.out.println( iso.chr()+":"+ iso.location(low) +"-" + iso.location(high) );
}

public String reconstruct(MutationSpot mut)throws Exception { 
    StringBuilder sb= new StringBuilder();
    for( AlignedElement e:eles ){
        boolean ifadd = false;
        if(e instanceof Mutation){
            if( ((Mutation) e).genomicLocation(iso).equals( mut.toString() )){
                if(e instanceof Insertion){
                    sb.append(((Insertion)e).insertion());
                }else if(e instanceof Substitution){
                    sb.append(((Substitution)e).substitutionChars());
                }
            }else{
                if(!(e instanceof Insertion)){ 
                    ifadd = true; 
                }
            }
        }else{ ifadd = true; }
        
        if(ifadd){ 
            sb.append( iso.sequence().subSequence((int)e.referenceRegion().low(), (int)e.referenceRegion().high()+1) ); 
        }
    }
    return sb.toString();
}






}

