/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import prog.core.index.Index;

/**
 *
 * @author mg31
 */
public class Statics {
    
public static Kryo kryo= new Kryo();
static{
    kryo.addDefaultSerializer(Index.class,  new JavaSerializer() );
}
    
    
}
