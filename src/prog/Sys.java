/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import prog.gui.MainGui;

/**
 *
 * @author mg31
 */
public class Sys {
    

public final static String NAME = "RNAmut";
public final static String VERSION = "0.5.1_beta";

public static String DIR;
static{
    try {
        DIR = new File(MainGui.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getAbsoluteFile().getParentFile().toString();
        DIR = "C:/muxingu/data/own/SangerSoftware2";
    } catch (URISyntaxException ex) {}
}

public static final String DIR_TEMP = DIR+"/temp";
	
public static final String DIR_LIB = DIR+"/lib";
public static final String LIB_BASE = DIR_LIB+"ForksBaseLib.jar";
public static final String LIB_MATH = DIR_LIB+"ForksMathLib.jar";
public static final String LIB_SEQ = DIR_LIB+"ForkSeqLib.jar";
public static final String LIB_GUI = DIR_LIB+"ForksGUILib.jar";

public static final String DIR_IMG = DIR+"/img";
public static final String IMG_NEW_32 = DIR_IMG+"/new_32.png";
public static final String IMG_OPEN_32 = DIR_IMG+"/open_32.png";
public static final String IMG_SAVE_32 = DIR_IMG+"/save_32.png";
public static final String IMG_SAVE2_32 = DIR_IMG+"/save2_32.png";
public static final String IMG_SAVE_ALL_32 = DIR_IMG+"/saveall_32.png";
public static final String IMG_HELP_32 = DIR_IMG+"/help_32.png";

public static final String IMG_LOGO_16 = DIR_IMG+"/logo_16.png";
public static final String IMG_NEW_16 = DIR_IMG+"/new_16.png";
public static final String IMG_OPEN_16 = DIR_IMG+"/open_16.png";
public static final String IMG_SAVE_16 = DIR_IMG+"/save_16.png";
public static final String IMG_SAVE2_16 = DIR_IMG+"/save2_16.png";
public static final String IMG_SAVE_ALL_16 = DIR_IMG+"/saveall_16.png";
public static final String IMG_HELP_16 = DIR_IMG+"/help_16.png";
public static final String IMG_FOLDER_16 = DIR_IMG+"/folder_16.png";

static{
    try{
        File dirtemp = new File(DIR_TEMP);
        if( !dirtemp.exists() ){
            dirtemp.mkdirs();
        }
    }catch(Exception e){}
}



public static void main(String[] args) throws Exception { //debug 
    MainGui.main(args);
    System.out.println(DIR);
}

    
}
