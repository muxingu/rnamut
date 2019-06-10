/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.index;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import prog.core.Statics;


/**
 *
 * @author mg31
 */
abstract public class IndexBuilder {
    
protected Index index;
    
    public IndexBuilder()throws Exception {     
        
    }
    

public void write(String path)throws Exception { 
    Output out= new Output( new GZIPOutputStream( new FileOutputStream(path) ) );
    Statics.kryo.writeClassAndObject(out, index);
    out.close();
}

public static Index read(String path)throws Exception { 
    Input in = new Input( new GZIPInputStream( new FileInputStream( path ) ) );
    Index ret= (Index) Statics.kryo.readClassAndObject(in);
    in.close();
    return ret;
}

abstract public void build()throws Exception;

public static void main(String[] args) throws Exception { //debug 
    IndexBuilderFasta.main(args);
}
    
}
