/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.hot;

import fork.lib.base.collection.Pair;
import fork.lib.base.format.collection.ArrayOp1D;
import fork.lib.bio.seq.Nucleotide;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import prog.core.Param;
import prog.core.aln.ele.IsoformStrand;
import prog.core.aln.read.Read;
import prog.core.aln.read.Seed;
import prog.core.aln.read.ReadShrink;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class Anchor implements Serializable{

public int lind = -1, rind = -1, firstSize =0, lastSize=0;
public String ins = "";
    
    public Anchor(){}
    public Anchor(ArrayList<Pair<Seed,ArrayList<Integer>>> locs, Index index, String isostr, Read read, Param par){
        this(locs,locs,index,isostr,isostr,read,par);
    }
    public Anchor(ArrayList<Pair<Seed,ArrayList<Integer>>> locsa, ArrayList<Pair<Seed,ArrayList<Integer>>> locsb,
            Index index, String isoastr, String isobstr, Read read, Param par){
        IsoformStrand isoa = index.getIsoformStrand(isoastr);
        IsoformStrand isob = index.getIsoformStrand(isobstr);
        if(locsa.size()<2 || locsb.size()<2 ){ return; }
        if( hasFirst(locsa,read) && hasLast(locsb,read) ){
            ArrayList<Pair<Seed,Integer>> clusf = cluster(locsa,0,index); 
            if(clusf==null){ return; }
            ArrayList<Pair<Seed,Integer>> clusl = cluster(locsb,locsb.size()-1,index); 
            if(clusl==null){ return; }
            //if( (clusf.size()+clusl.size())*index.kmer() >= read.length() ){ return; }
            firstSize = clusf.size();
            lastSize = clusl.size();
            
            ReadShrink rsh = new ReadShrink(isoa.sequence().substring(clusf.get(clusf.size()-1).b()),
                    read.sequence().substring(clusf.get(clusf.size()-1).a().index()));
            rsh.computeLeft();
            int posa = clusf.get(clusf.size()-1).a().index() + rsh.left().length();
            rind = clusf.get(clusf.size()-1).b() + rsh.left().length();
            
            ReadShrink rsl = new ReadShrink(isob.sequence().substring(0,clusl.get(0).b()),
                    read.sequence().substring(0,clusl.get(0).a().index()));
            rsl.computeRight();
            int posb = clusl.get(0).a().index()-1 - rsl.right().length();
            lind = clusl.get(0).b()-1 - rsl.right().length();
            
            if( posa<=(posb+1) ){
                ins = read.sequence().substring(posa, posb+1);
            }else{
                int shift = posa-posb-1;
                if(isoa.isForward()){ 
                    rind-=shift; 
                }else{ 
                    lind+=shift; 
                }
                if(!isoa.uniqueID().equals(isob.uniqueID())){
                    if(isoa.sas()=='a'){
                        lind+=shift;
                        rind+=shift;
                    }
                }
            }
            /*
            System.out.println(isoa.uniqueID()+"_"+isoa.strand()+"  "+isob.uniqueID()+"_"+isob.strand());
            System.out.println("lind:"+(lind+1)+" rind:"+(rind-1)+"  "+"a-rind:"+
                    isoa.location(rind-1) +" b-lind:"+isob.location(lind+1) );
            System.out.println(read);System.out.println();
            System.exit(1);
            */
        }
    }
    

private boolean hasFirst(ArrayList<Pair<Seed,ArrayList<Integer>>> locs, Read read){
    return locs.get(0).a().index()==0;
}
private boolean hasLast(ArrayList<Pair<Seed,ArrayList<Integer>>> locs, Read read){
    return locs.get(locs.size()-1).a().index() == read.seeds().get(read.seeds().size()-1).index();
}
private ArrayList<Pair<Seed,Integer>> cluster(ArrayList<Pair<Seed,ArrayList<Integer>>> locs, int ind, Index index){
    ArrayList<ArrayList<Pair<Seed,Integer>>> pss = new ArrayList<>();
    for( Integer loc:locs.get(ind).b() ){
        ArrayList<Pair<Seed,Integer>> ps = new ArrayList<>();
        ps.add( new Pair<>( locs.get(ind).a(), loc ) ); pss.add(ps);
    }
    ExtendCluster ea = new ExtendCluster(locs, pss, ind+1, index, 0, locs.size()-2){
        protected int nextInd(int i) {return i+1;}
        protected int target(ArrayList<Pair<Seed, Integer>> ps) { return ps.size()-1; }
        protected boolean isAdjacent(int loc, int loc_) { return loc_==loc+this.index.kmer();}
        protected void add(ArrayList<Pair<Seed, Integer>> ps, Pair<Seed, Integer> p) { ps.add(p); }
    };
    ExtendCluster eb = new ExtendCluster(locs, pss, ind-1, index, 1, locs.size()-1){
        protected int nextInd(int i) {return i-1;}
        protected int target(ArrayList<Pair<Seed, Integer>> ps) { return 0; }
        protected boolean isAdjacent(int loc, int loc_) { return loc_==loc-this.index.kmer();}
        protected void add(ArrayList<Pair<Seed, Integer>> ps, Pair<Seed, Integer> p) { ps.add(0, p); }
    };
    ArrayList<Pair<ArrayList<Pair<Seed,Integer>>,Integer>> val = new ArrayList<>();
    for( ArrayList<Pair<Seed,Integer>> ps:pss ){
        if(ps.size()>=1){ val.add( new Pair<>(ps,ps.size()) ); }
    }
    if(val.isEmpty()){
        return null;
    }
    Collections.sort(val, new Comparator<Pair<ArrayList<Pair<Seed,Integer>>,Integer>>() {
        public int compare(Pair<ArrayList<Pair<Seed, Integer>>, Integer> o1, Pair<ArrayList<Pair<Seed, Integer>>, Integer> o2) {
            return Integer.compare(o2.b(), o1.b());
        }
    } );
    return val.get(0).a();
}

    
    
    

abstract public class ExtendCluster{
    private ArrayList<Pair<Seed,ArrayList<Integer>>> locs;
    protected Index index;
    public ArrayList<ArrayList<Pair<Seed,Integer>>> pss;
    public ExtendCluster( ArrayList<Pair<Seed,ArrayList<Integer>>> locs, ArrayList<ArrayList<Pair<Seed,Integer>>> pss, 
            int ind, Index index, int indmin, int indmax ){
        this.locs = locs;
        this.pss = pss;
        this.index = index;
        int ind_ = ind; 
        ArrayList<Boolean> ifcont = ArrayOp1D.repeat(true, pss.size());
        while( ind_>=indmin && ind_<=indmax ){
            Pair<Seed,ArrayList<Integer>> pair = locs.get(ind_);
            for( int i=0; i<pss.size(); i++ ){
                if(ifcont.get(i)){
                    boolean hasadj = false;
                    for( Integer loc_:pair.b() ){

                        ArrayList<Pair<Seed,Integer>> ps = pss.get(i);
                        int loc = ps.get( target(ps) ).b();
                        if( isAdjacent(loc,loc_) ){
                            add( ps, new Pair<>(pair.a(),loc_) ); hasadj=true;
                        }
                    }
                    if(!hasadj){ ifcont.set(i, false); }
                }
            }
            boolean ifbreak = true;
            for( Boolean cont:ifcont ){
                if(cont){ ifbreak=false; break; }
            }
            if(ifbreak){break;}
            ind_ = nextInd(ind_);
        }
    }
    abstract protected int nextInd(int i);
    abstract protected int target(ArrayList<Pair<Seed,Integer>> ps);
    abstract protected boolean isAdjacent(int loc, int loc_);
    abstract protected void add(ArrayList<Pair<Seed,Integer>> ps, Pair<Seed,Integer> p);
    }

    
public static void main(String[] args) throws Exception { //debug 
    //ZZDebug.main(args);
    DetectorHotspot.main(args);
}
    
}

