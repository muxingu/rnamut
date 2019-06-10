/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui;

import fork.lib.gui.soft.gen.comp.FMainFrame;
import fork.lib.gui.soft.gen.comp.Positioner;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import prog.Sys;
import prog.core.Param;
import prog.core.ProgramMain;
import prog.core.aln.Aligner;
import prog.gui.anal.Anal;
import prog.gui.anal.Main;
import prog.gui.comp.PanelNew;
import prog.gui.comp.PanelRunning;
import prog.gui.util.FWorker;
import prog.gui.util.TaWriter;

/**
 *
 * @author mg31
 */
public class MainGui extends FMainFrame{
    
private Positioner pner;
private Main main;
    
    
    public MainGui(Positioner pner){
        this.pner=pner;
        this.main = new Main(this);
        pner.setWindowSizeRelativeToScreen(this, 0.8, 0.8);
        pner.positionAtScreenCentre(this);
        setTitle(Sys.NAME+" Version "+Sys.VERSION);
        initDividerLocations(0.2, 0.4);
        setIconImage( new ImageIcon(Sys.IMG_LOGO_16).getImage() );
        setVisible(true);
    }
    
    

@Override
protected JComponent defaultRight(){
    JPanel p = new JPanel();
    p.add(new JLabel(""));
    return p;
}
@Override
protected JComponent defaultTopLeft(){
    JPanel p = new JPanel();
    p.add(new JLabel(""));
    return p;
}
@Override
protected JComponent defaultBtmLeft(){
    JPanel p = new JPanel();
    p.add(new JLabel(""));
    return p;
}

    
    
    
    

@Override
protected JMenuBar initJMenuBar() {
    JMenuBar mb= new JMenuBar();
    JMenu file= new JMenu("File");
    final JMenuItem newAnal= new JMenuItem("New Analysis"),
            openProj= new JMenuItem("Open..."),
            save= new JMenuItem("Save"),
            saveAll= new JMenuItem("Save All"),
            quit= new JMenuItem("Quit");
    newAnal.setIcon(new ImageIcon(Sys.IMG_NEW_16));
    newAnal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(newAnal)){ newAnal(); }
            } });
    file.add(newAnal);
    /*
    openProj.setIcon(new ImageIcon(Sys.IMG_OPEN_16));
    openProj.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(openProj)){ openAnal(); }
            }});
    file.add(openProj);
    */
    file.addSeparator();
    
    save.setIcon(new ImageIcon(Sys.IMG_SAVE_16));
    save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(save)){ saveAnal(); }
            }});
    file.add(save);
    saveAll.setIcon(new ImageIcon(Sys.IMG_SAVE_ALL_16));
    saveAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(saveAll)){saveAll(); }
            }});
    file.add(saveAll);
    file.addSeparator();
    
    quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(quit)){ quit(); }
            } });
    file.add(quit);
    mb.add(file);
    
    JMenu helpM= new JMenu("Help");
    final JMenuItem help= new JMenuItem("Help"),
            about = new JMenuItem("About");
    help.setIcon(new ImageIcon(Sys.IMG_HELP_16));
    help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(help)){ help(); }
            } });
    helpM.add(help);
    helpM.addSeparator();
    about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(about)){ about(); }
            } });
    helpM.add(about);
    mb.add(helpM);
    return mb;
}

@Override
protected JToolBar initJToolBar() {
    JToolBar tb= new JToolBar();
    tb.setFloatable(false);
    tb.setRollover(true);
    tb.addSeparator();
    tb.addSeparator();
    
    JButton newAnal= new JButton();
    newAnal.setIcon(new ImageIcon(Sys.IMG_NEW_32));
    newAnal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { newAnal(); }
        });
    newAnal.setToolTipText("New Analysis");
    tb.add(newAnal);
    /*
    JButton openAnal= new JButton();
    openAnal.setIcon(new ImageIcon(Sys.IMG_OPEN_32));
    openAnal.setToolTipText("Open Analysis");
    openAnal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { openAnal(); }
        });
    tb.add(openAnal);
    */
    tb.addSeparator();
    
    JButton save= new JButton();
    save.setIcon(new ImageIcon(Sys.IMG_SAVE_32));
    save.setToolTipText("Save Analysis");
    save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { saveAnal(); }
        });
    tb.add(save);
    
    JButton saveAll= new JButton();
    saveAll.setIcon(new ImageIcon(Sys.IMG_SAVE_ALL_32));
    saveAll.setToolTipText("Save Output");
    saveAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { saveAll(); }
        });
    tb.add(saveAll);
    tb.addSeparator();
    
    JButton help= new JButton();
    help.setIcon(new ImageIcon(Sys.IMG_HELP_32));
    help.setToolTipText("Help");
    help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { help(); }
        });
    tb.add(help);
    return tb;
}

public void newAnal(){
    JDialog d= new JDialog(this, "New Analysis", true);
    PanelNew pan = new PanelNew( d );
    d.setContentPane( pan );
    d.setSize( new Dimension(500,400) ); 
    d.setIconImage(new ImageIcon(Sys.IMG_LOGO_16).getImage() ); 
    pner.positionAtCentreWithFactor(d, this, 0.3, 0.2);
    d.setVisible(true);
    if(pan.ifStart()){
        d.dispose();
        String analpath = Sys.DIR_TEMP +"/"+pan.analName();
        new File(analpath).mkdirs();
        
        JDialog dr = new JDialog(this, "Running", ModalityType.APPLICATION_MODAL);
        PanelRunning prun = new PanelRunning(dr);
        dr.setContentPane(prun);
        dr.setSize(650,350);
        pner.positionAtCentreWithFactor(dr, this, 0.3, 0.2);
        ProgramMain prog = new ProgramMain(analpath,new String[]{pan.file_1().getAbsolutePath(), pan.file_2().getAbsolutePath()},analpath,pan.analName(),pan.index(),new Param());
        FWorker worker = new FWorker(dr,this,new TaWriter(prun.textArea())) {
            @Override
            public Void doInBackground() {
                try{
                    prog.setWriter(wr);
                    prog.start();
                }catch(Exception e){  return null; }
                return null;
            }
            @Override
            protected void done() { dr.dispose(); }
        };
        prun.setWorker(worker);
        worker.execute2();
        main.addAnal(pan.analName(), analpath, prog.pathPool(), prog.pathAln(), prog.pathCorr(), pan.index());
    }
}

public void openAnal(){
    
}

public void saveAnal(){ 
    try{
        main.saveAnal();
    }catch(Exception e){}
}

public void saveAll(){
    
}

public void help(){
    
}

public void about(){
    
}

public void quit(){
    this.dispose();
}




    
public static void main(String[] args) throws Exception { //debug 
    try {
       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {}
    
    Positioner pner = new Positioner();
    MainGui main = new MainGui(pner);
    
    
    
    
}
    
    
}
