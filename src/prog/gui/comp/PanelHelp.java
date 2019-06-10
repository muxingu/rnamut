

package prog.gui.comp;

import javax.swing.JFrame;


public class PanelHelp extends javax.swing.JPanel {

    public PanelHelp(String s) {
        initComponents();
        this.ta.setText(s);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        ta = new javax.swing.JTextArea();

        ta.setBackground(new java.awt.Color(255, 255, 153));
        ta.setColumns(20);
        ta.setEditable(false);
        ta.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ta.setRows(5);
        ta.setPreferredSize(new java.awt.Dimension(264, 374));
        jScrollPane1.setViewportView(ta);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents




    public static void main(String[] args){
        JFrame f= new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PanelHelp d= new PanelHelp("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        
        f.setContentPane(d); f.setSize(200,300);
        f.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea ta;
    // End of variables declaration//GEN-END:variables

}
