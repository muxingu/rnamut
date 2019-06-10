/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.core.aln.read;

/**
 *
 * @author mg31
 */
public class ReadShrink {
    
private String ref, seq;
private int matleft=0, matright=0;

    
    public ReadShrink(String ref, String seq){
        this.seq= seq;
        this.ref=ref;
    }


public void computeLeft(){
    while(true){
        if( matleft==ref.length() || matleft==seq.length() ){ break; }
        if( matleft==(ref.length()-matright) || matleft==(seq.length()-matright) ){break;}
        if( ref.charAt(matleft)==seq.charAt(matleft) ){
            matleft++;
        }else{break;}
    }
}

public void computeRight(){
    while(true){
        if( matright==ref.length() || matright==seq.length() ){ break; }
        if( matright==(ref.length()-matleft) || matright==(seq.length()-matleft) ){break;}
        if( ref.charAt(ref.length()-1-matright)==seq.charAt(seq.length()-1-matright) ){
            matright++;
        }else{break;}
    }
}

public String left(){ return seq.substring(0, matleft); }
public String right(){ return seq.substring(seq.length()-matright); }
public String referenceRemain(){ return ref.substring(matleft, ref.length()-matright); }
public String sequenceRemain(){ return seq.substring(matleft, seq.length()-matright); }
    
public static void main(String[] args) throws Exception { //debug 
    String a = "AATTCCGG";
    String b = "AATTCCCGG";
    ReadShrink ss= new ReadShrink(a,b);
    ss.computeLeft();
    ss.computeRight();
    System.out.println(ss.left());
    System.out.println(ss.right());
}

}
