/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.res;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import prog.core.Statics;
import prog.core.aln.hot.ZZDebug;
import prog.core.aln.read.ReadPool;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class AlignmentResult implements Serializable{

public AlignmentResultRead resread = new AlignmentResultRead();
public AlignmentResultITD resitd = new AlignmentResultITD();
public AlignmentResultFusionPair resfpair = new AlignmentResultFusionPair();
public AlignmentResultFusionRead resfread = new AlignmentResultFusionRead();
    
    
    public AlignmentResult(){}

    
public AlignmentResultRead alignmentResult(){return resread;}
public AlignmentResultITD alignmentResultITD(){return resitd;}
public AlignmentResultFusionPair alignmentResultFusionPair(){return resfpair;}
public AlignmentResultFusionRead alignmentResultFusionRead(){return resfread;}


public void write(String path)throws Exception { 
    Output out= new Output( new GZIPOutputStream( new FileOutputStream(path) ) );
    Statics.kryo.writeClassAndObject(out, this);
    out.close();
}
    
public static AlignmentResult read(String path, Index index, ReadPool pool)throws Exception { 
    Input in = new Input( new GZIPInputStream( new FileInputStream( path ) ) );
    AlignmentResult ret= (AlignmentResult) Statics.kryo.readClassAndObject(in);
    in.close();
    ret.resread.initAlignments(index, pool);
    ret.resfpair.initAlignments(index, pool);
    return ret;
}


public static void main(String[] args) throws Exception { //debug 
    ZZDebug.main(args);
}

    
}


