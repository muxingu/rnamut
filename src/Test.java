
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mg31
 */
public class Test {


public static void main(String[] args) throws Exception { //debug 
    File dir= new File("C:/muxingu/data/own/SangerSoftware2/file");
    
    //IndexBuilderFasta.main(args);
    long a = Runtime.getRuntime().freeMemory();
    Index index= IndexBuilder.read(dir+"/index.ind");
    long b = Runtime.getRuntime().freeMemory();
    
    System.out.println( (a-b)/1024/1024);
    
    
    
}

    
}
