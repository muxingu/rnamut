/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prog.gui.comp;

import fork.lib.base.file.FileName;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import prog.Sys;
import prog.core.index.Index;
import prog.core.index.IndexBuilder;

/**
 *
 * @author mg31
 */
public class PanelNew extends javax.swing.JPanel {

private JDialog dialog;
private boolean ifStart = false, ifpe;
private File file1, file2;
private Index index;
private FileNameExtensionFilter filter = new FileNameExtensionFilter("FASTQ", "gz", "fq","fastq");
    
    
    public PanelNew(JDialog dialog) {
        this.dialog = dialog;
        initComponents();
        tfName1.setText("C:/muxingu/data/own/SangerSoftware2/file/TCGA-AB-2948_1.fq.gz");
        tfName2.setText("C:/muxingu/data/own/SangerSoftware2/file/TCGA-AB-2948_2.fq.gz");
        tfIndex.setText(Sys.DIR+"/index.ind");
        //tfanal.setText("New_analysis");
        tfanal.setText("TCGA-AB-2948");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgroupSEPE = new javax.swing.ButtonGroup();
        tfName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jrbSE = new javax.swing.JRadioButton();
        jrbPE = new javax.swing.JRadioButton();
        tfName1 = new javax.swing.JTextField();
        butSelect1 = new javax.swing.JButton();
        tfName2 = new javax.swing.JTextField();
        butSelect2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tfIndex = new javax.swing.JTextField();
        butSelect3 = new javax.swing.JButton();
        butCancel = new javax.swing.JButton();
        butStart = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tfanal = new javax.swing.JTextField();

        tfName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfNameActionPerformed(evt);
            }
        });

        setPreferredSize(new java.awt.Dimension(400, 500));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("Input Files:");

        bgroupSEPE.add(jrbSE);
        jrbSE.setText("Single-end");
        jrbSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbSEActionPerformed(evt);
            }
        });

        bgroupSEPE.add(jrbPE);
        jrbPE.setSelected(true);
        jrbPE.setText("Paired-end");
        jrbPE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbPEActionPerformed(evt);
            }
        });

        tfName1.setEditable(false);
        tfName1.setBackground(new java.awt.Color(255, 255, 255));
        tfName1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfName1ActionPerformed(evt);
            }
        });

        butSelect1.setText("Select");
        butSelect1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSelect1ActionPerformed(evt);
            }
        });

        tfName2.setEditable(false);
        tfName2.setBackground(new java.awt.Color(255, 255, 255));
        tfName2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfName2ActionPerformed(evt);
            }
        });

        butSelect2.setText("Select");
        butSelect2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSelect2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("Gene Index:");

        tfIndex.setEditable(false);
        tfIndex.setBackground(new java.awt.Color(255, 255, 255));
        tfIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfIndexActionPerformed(evt);
            }
        });

        butSelect3.setText("Select");
        butSelect3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSelect3ActionPerformed(evt);
            }
        });

        butCancel.setText("Cancel");
        butCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCancelActionPerformed(evt);
            }
        });

        butStart.setText("Start");
        butStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStartActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel3.setText("Analysis Name");

        tfanal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfanalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfName1, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSelect1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jrbSE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jrbPE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfName2, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSelect2, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(butStart, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(butCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tfIndex, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSelect3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3)
                    .addComponent(tfanal, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jrbSE)
                    .addComponent(jrbPE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfName1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSelect1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfName2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSelect2))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfIndex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSelect3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tfanal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 232, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butStart)
                    .addComponent(butCancel))
                .addContainerGap())
        );

        jrbSE.getAccessibleContext().setAccessibleName("jrbSE");
    }// </editor-fold>//GEN-END:initComponents
    private void jrbSEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbSEActionPerformed
        groupButt();
    }//GEN-LAST:event_jrbSEActionPerformed
    private void tfNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfNameActionPerformed

    }//GEN-LAST:event_tfNameActionPerformed
    private void tfName1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfName1ActionPerformed
    }//GEN-LAST:event_tfName1ActionPerformed
    private void butSelect1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelect1ActionPerformed
        File f = null;
        JFileChooser jfc= new JFileChooser();
        jfc.setDialogTitle( "Select File" );
        jfc.setCurrentDirectory( new File(Sys.DIR) );
        jfc.setFileFilter(filter);
        int returnVal = jfc.showOpenDialog(dialog);
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            f=jfc.getSelectedFile();
        }
        if( f != null ){
            tfName1.setText(f.getAbsolutePath());
        }
    }//GEN-LAST:event_butSelect1ActionPerformed
    private void tfName2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfName2ActionPerformed
    }//GEN-LAST:event_tfName2ActionPerformed
    private void butSelect2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelect2ActionPerformed
        File f = null;
        JFileChooser jfc= new JFileChooser();
        jfc.setDialogTitle( "Select File" );
        jfc.setCurrentDirectory( new File(Sys.DIR) );
        jfc.setFileFilter(filter);
        int returnVal = jfc.showOpenDialog(dialog);
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            f=jfc.getSelectedFile();
        }
        if( f != null ){
            tfName2.setText(f.getAbsolutePath());
        }
    }//GEN-LAST:event_butSelect2ActionPerformed
    private void tfIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfIndexActionPerformed
    }//GEN-LAST:event_tfIndexActionPerformed
    private void butSelect3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSelect3ActionPerformed
        File f = null;
        JFileChooser jfc= new JFileChooser();
        jfc.setDialogTitle( "Select Index" );
        jfc.setCurrentDirectory( new File(System.getProperty("user.dir")+"/file") );
        jfc.setFileFilter(new FileNameExtensionFilter("Index", "ind") );
        int returnVal = jfc.showOpenDialog(dialog);
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            f=jfc.getSelectedFile();
        }
        if( f != null ){
            tfIndex.setText(f.getAbsolutePath());
        }
    }//GEN-LAST:event_butSelect3ActionPerformed

    private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
        dialog.dispose();
    }//GEN-LAST:event_butCancelActionPerformed

    private void butStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butStartActionPerformed
        String err = "";
        if(!new File(tfIndex.getText()).exists()){
            err += "Index file - not found\n";
        }
        if(jrbPE.isSelected()){
            if(!new File(tfName1.getText()).exists()){
                err += "Input 1 - not found\n";
            }
            if(!new File(tfName2.getText()).exists()){
                err += "Input 2 - not found\n";
            }
        }else if(jrbSE.isSelected()){
            if(!new File(tfName1.getText()).exists()){
                err += "Input - not found\n";
            }
        }
        if(!err.equals("")){
            JOptionPane.showMessageDialog(dialog, err, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
        file1 = new File(tfName1.getText());
        file2 = new File(tfName2.getText());
        index = IndexBuilder.read( tfIndex.getText() );
        ifStart = true;
        dialog.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butStartActionPerformed

    private void jrbPEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbPEActionPerformed
        groupButt();
    }//GEN-LAST:event_jrbPEActionPerformed

    private void tfanalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfanalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfanalActionPerformed

public void groupButt(){
    if(jrbSE.isSelected()){
        tfName2.setEnabled(false); tfName2.setBackground(new Color(240,240,240));  butSelect2.setEnabled(false);
    }else if(jrbPE.isSelected()){
        tfName2.setEnabled(true); tfName2.setBackground(Color.white); butSelect2.setEnabled(true);
    }
}
    
public boolean ifStart(){return ifStart;}
public boolean ifPE(){return ifpe;}
public File file_1(){return file1;}
public File file_2(){return file2;}
public Index index(){return index;}
public String analName(){return tfanal.getText();}


    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgroupSEPE;
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butSelect1;
    private javax.swing.JButton butSelect2;
    private javax.swing.JButton butSelect3;
    private javax.swing.JButton butStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jrbPE;
    private javax.swing.JRadioButton jrbSE;
    private javax.swing.JTextField tfIndex;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfName1;
    private javax.swing.JTextField tfName2;
    private javax.swing.JTextField tfanal;
    // End of variables declaration//GEN-END:variables

 
    
    
public static void main(String[] args) throws Exception { //debug 
    JDialog d= new JDialog();
    PanelNew pan = new PanelNew( d );
    d.setModal(true);
    d.setContentPane( pan );
    d.setSize( new Dimension(500,600) ); 
    d.setVisible(true);
    
    
    System.out.println(pan.ifStart());
    System.out.println(pan.index());
}

}


