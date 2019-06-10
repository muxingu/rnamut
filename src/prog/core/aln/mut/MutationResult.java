/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.mut;

import prog.core.aln.mut.MutationSpot;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import prog.core.Statics;
import prog.core.aln.hot.DetectorHotspot;
import prog.core.aln.read.Read;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class MutationResult extends HashMap<String,HashMap<String,MutationSpot>> implements Serializable{
    
private static DecimalFormat df= new DecimalFormat("0.000");
private static DecimalFormat df1p= new DecimalFormat("#.#");
    
    
    public MutationResult(){}
    
    
    
public void add(MutationSpot mut){
    String gene = mut.gene();
    if(!containsKey(gene)){
        put(gene,new HashMap<>());
    }
    String mutstr = mut.toString();
    HashMap<String,MutationSpot> hm = get(gene);
    if(!hm.containsKey(mutstr)){
        hm.put(mutstr, mut);
    }
    hm.get(mutstr).mutReads.addAll(mut.mutReads );
}

public void addAll(MutationResult res){
    for( MutationSpot m:res.allMutations() ){
        add(m);
    }
}

public ArrayList<MutationSpot> allMutations(){
    ArrayList<MutationSpot> ret = new ArrayList<>();
    for( String gene:keySet() ){
        ret.addAll(allMutationsForGene(gene));
    }
    return ret;
}

public ArrayList<MutationSpot> allMutationsForGene(String gene){
    ArrayList<MutationSpot> ms = new ArrayList<>(); 
    ms.addAll( get(gene).values() );
    Collections.sort(ms, new Comparator<MutationSpot>() {
        public int compare(MutationSpot o1, MutationSpot o2) {
            if(o1.low()==o2.low()){
                return Integer.compare(o1.high(), o2.high());
            }
            return Integer.compare(o1.low(), o2.low());
        }
    });
    return ms;
}

public MutationResult subsetType(String... tys){
    MutationResult ret = new MutationResult();
    HashSet<String> set = new HashSet<>();
    set.addAll(Arrays.asList(tys));
    for( MutationSpot mut:allMutations() ){
        if(set.contains(mut.type())){
            ret.add(mut);
        }
    }
    return ret;
}

public static void writeToTxtFile(MutationResult res, Index index, ReadPool pool, String path, boolean ifseq)throws Exception { 
    new File(path).getAbsoluteFile().getParentFile().mkdirs();
    BufferedWriter bw = new BufferedWriter(new FileWriter(path));
    bw.write("Gene\tMutation\tProtMut\tMutReads\tWTReads\tVAF\n");
    ArrayList<String> genes = new ArrayList<>();
    genes.addAll(res.keySet());
    Collections.sort(genes);
    for( String gene:genes ){
        ArrayList<MutationSpot> ms = new ArrayList<>(); ms.addAll(res.get(gene).values());
        Collections.sort(ms, new Comparator<MutationSpot>() {
            public int compare(MutationSpot o1, MutationSpot o2) {
                if(o1.low()==o2.low()){
                    return Integer.compare(o1.high(), o2.high());
                }
                return Integer.compare(o1.low(), o2.low());
            }
        });
        for( MutationSpot m:ms ){
            String prot = m.aminoAcidMutation(index);
            String mutn, wtn, vaf;
            if(m instanceof MutationSpotFusionRead){
                MutationSpotFusionRead mf = (MutationSpotFusionRead)m;
                mutn = Integer.toString(m.mutReads.size());
                wtn = mf.wtReads.size()+"-"+mf.wtReads2.size(); vaf = "NA";
            }else if(m instanceof MutationSpotFusionPair){
                MutationSpotFusionPair mf = (MutationSpotFusionPair)m;
                mutn = mf.covString(); wtn = mf.covWTString(); vaf = "NA";
            }else{
                mutn = Integer.toString(m.mutReads.size());
                wtn = Integer.toString(m.wtReads.size());
                vaf = df.format(m.vaf());
            }
            bw.write(gene+"\t"+m.toString()+"\t"+ prot+"\t"+
                    mutn+"\t"+
                    wtn+"\t"+ vaf+"\n" );
            if(ifseq){
                if(!m.type().equals("Sub")){
                    for( Read rd:m.mutatedReads(pool) ){
                        bw.write("\t"+rd.id()+"\t"+rd.sequence()+"\n"); 
                    }
                }
            }
            if(prot.equals("W288fsxx")){
                for( Read rd:m.mutatedReads(pool) ){
                    bw.write("\tmut\t"+rd.id()+"\t"+rd.sequence()+"\t"+rd.quality()+"\n"); 
                }
                for( Read rd:m.wtReads(pool) ){
                    bw.write("\twt\t"+rd.id()+"\t"+rd.sequence()+"\t"+rd.quality()+"\n"); 
                }
            }
        }
    }
    bw.close();
}

public void write(String path)throws Exception { 
    Output out= new Output( new GZIPOutputStream( new FileOutputStream(path) ) );
    Statics.kryo.writeClassAndObject(out, this);
    out.close();
}

public static MutationResult read(String path)throws Exception { 
    Input in = new Input( new GZIPInputStream( new FileInputStream( path ) ) );
    MutationResult ret= (MutationResult) Statics.kryo.readClassAndObject(in);
    in.close();
    return ret;
}


public static void main(String[] args) throws Exception { //debug 
    DetectorHotspot.main(args);
}

}
