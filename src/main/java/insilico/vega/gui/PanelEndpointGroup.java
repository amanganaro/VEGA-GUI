package insilico.vega.gui;

import insilico.core.model.iInsilicoModel;
import insilico.core.model.iInsilicoModelConsensus;
import insilico.vega.gui.models.VegaModelsWrapper;
import insilico.vega.gui.resources.VegaVersion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Alberto
 */
public class PanelEndpointGroup extends JPanel {

    // background gradient color
    private final Color GradientEnd = new Color(255,255,255);
    private final Color GradientStart;
    
    // for labels and checkbox positions
    private final static int H_OFFSET_INFOLABEL = 15;
    private final static int H_OFFSET_CHECKBOX = 40;
    private final static int V_STARTING_VALUE = 5; // 8
    private final static int V_STEP = 24; // 23
    
    private final VegaModelsWrapper.VegaEndpoint EP;
    
    public final int Group;
    
    public JCheckBox[] ComboboxModels;
    public JCheckBox ComboboxSelectAll;
    public JLabel[] LabelModelInfo;
    public JLabel[] LabelModelConsInfo;
    
    
    /**
     * Creates new form PanelEndpointGroup
     */
    public PanelEndpointGroup(VegaModelsWrapper.VegaEndpoint EndPoint) {
        
        this.EP = EndPoint;
        this.Group = EndPoint.Section;
        this.setOpaque(true);
        this.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        
        this.setBorder(BorderFactory.createLineBorder(new Color(64,64,64), 1));
                
        // set gradient color
        switch(Group) {
            case VegaModelsWrapper.SECTION_HUMAN: 
                GradientStart = new Color(255, 182, 173);
                break;
            case VegaModelsWrapper.SECTION_ECOTOX: 
                GradientStart = new Color(175, 237, 175);
                break;
            case VegaModelsWrapper.SECTION_FATE: 
                GradientStart = new Color(184, 216, 227);
                break;
            case VegaModelsWrapper.SECTION_PHYS: 
                GradientStart = new Color(208, 219, 158);
                break;
            case VegaModelsWrapper.SECTION_HUMAN_PBPK: 
                GradientStart = new Color(209, 182, 123);
                break;
            case VegaModelsWrapper.SECTION_ECO_PBPK: 
                GradientStart = new Color(223, 204, 227);
                break;
            default:
                GradientStart = GradientEnd;
        }
        
        int nModels = EP.Models.size() + EP.ModelsConsensus.size();
        
        ComboboxModels = new JCheckBox[nModels];
        LabelModelInfo = new JLabel[nModels];
        int CurYPos = V_STARTING_VALUE;

        // title label
        JLabel labelName = new JLabel();
        labelName.setOpaque(false);
        labelName.setFont(new Font("Verdana", Font.BOLD, 14));
        labelName.setForeground(new Color(24, 24, 255));
        labelName.setText(EP.Name);
        this.add(labelName, new org.netbeans.lib.awtextra.AbsoluteConstraints(H_OFFSET_CHECKBOX, (CurYPos), -1, -1));
        CurYPos += V_STEP + 2;
        
        // select all combobox
        if (nModels > 1) {
            ComboboxSelectAll = new JCheckBox();
            ComboboxSelectAll.setOpaque(false);
            ComboboxSelectAll.setFont(new Font("Verdana", 1, 12));
            ComboboxSelectAll.setForeground(new Color(54, 96, 181));
            ComboboxSelectAll.setText(" Select all models");
            ComboboxSelectAll.setRequestFocusEnabled(false);
            ComboboxSelectAll.addItemListener(new java.awt.event.ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent evt) {
                    OnSelectAllClick(evt);
                }
            });
            this.add(ComboboxSelectAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(H_OFFSET_CHECKBOX, (CurYPos), -1, -1));
            CurYPos += V_STEP + 2;
        }

        int idx = 0;
        
        // models comboboxes
        for (int i=0; i<EP.Models.size(); i++) {
            
            iInsilicoModel curModel = EP.Models.get(i).Model;
            
            JCheckBox CurCB = new JCheckBox();
            CurCB.setOpaque(false);
            CurCB.setFont(new Font("Verdana", 0, 12));
            CurCB.setForeground(new Color(54, 96, 181));
            CurCB.setText(" " + curModel.getInfo().getName() + " - v. " + curModel.getInfo().getVersion());
            CurCB.setRequestFocusEnabled(false);            
            CurCB.addActionListener(new CBListener(CurCB, EP.Models.get(i))  );            
            ComboboxModels[idx] = CurCB;
            this.add(ComboboxModels[idx], new org.netbeans.lib.awtextra.AbsoluteConstraints(H_OFFSET_CHECKBOX, CurYPos, -1, -1));

            if (VegaVersion.SET_ALL_CB_SELECTED) {
                ComboboxModels[idx].setSelected(true);
                EP.Models.get(i).Selected = true;
            }

            JLabel CurInfo = new JLabel();
            CurInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/question5.png"))); 
            CurInfo.setToolTipText("Show available information for " + curModel.getInfo().getName());
            final iInsilicoModel ModelToShow = curModel;
            CurInfo.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    OnInfoLabelMouseClick(ModelToShow);
                }
                @Override
                public void mouseEntered(MouseEvent evt) {
                    OnClickableLabelMouseOver(evt);
                }
            });
            LabelModelInfo[idx] = CurInfo;
            this.add(LabelModelInfo[idx], new org.netbeans.lib.awtextra.AbsoluteConstraints(H_OFFSET_INFOLABEL, CurYPos+4, -1, -1));

            idx++;
            CurYPos += V_STEP;
        }        
        
        // consensus comboboxes
        for (int i=0; i<EP.ModelsConsensus.size(); i++) {
            
            iInsilicoModelConsensus curModel = EP.ModelsConsensus.get(i).Model;
            
            JCheckBox CurCB = new JCheckBox();
            CurCB.setOpaque(false);
            CurCB.setFont(new Font("Verdana", 0, 12));
            CurCB.setForeground(new Color(54, 96, 181));
            CurCB.setText(" " + curModel.getInfo().getName() + " - v. " + curModel.getInfo().getVersion());
            CurCB.setRequestFocusEnabled(false);            
            CurCB.addActionListener(new CBListener(CurCB, EP.ModelsConsensus.get(i))  );            
            ComboboxModels[idx] = CurCB;
            this.add(ComboboxModels[idx], new org.netbeans.lib.awtextra.AbsoluteConstraints(H_OFFSET_CHECKBOX, CurYPos, -1, -1));
            
            JLabel CurInfo = new JLabel();
            CurInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/question5.png"))); 
            CurInfo.setToolTipText("Show available information for " + curModel.getInfo().getName());
            final iInsilicoModelConsensus ModelToShow = curModel;
            CurInfo.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    OnInfoConsLabelMouseClick(ModelToShow);
                }
                @Override
                public void mouseEntered(MouseEvent evt) {
                    OnClickableLabelMouseOver(evt);
                }
            });
            LabelModelInfo[idx] = CurInfo;
            this.add(LabelModelInfo[idx], new org.netbeans.lib.awtextra.AbsoluteConstraints(H_OFFSET_INFOLABEL, CurYPos+4, -1, -1));

            idx++;
            CurYPos += V_STEP;
        }        


        JPanel emptyPanel = new JPanel();
        emptyPanel.setMaximumSize(new Dimension(2, 2));
        this.add(emptyPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(H_OFFSET_INFOLABEL, CurYPos, -1, -1));
        emptyPanel.setVisible(false);
       
    }
        
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent(g);
        int w = getWidth( );
        int h = getHeight( );
        Graphics2D g2d = (Graphics2D)g;
        // Paint a gradient from top to bottom
        GradientPaint gp = new GradientPaint(
            0, 0, GradientStart,
            50, 0, GradientEnd);

        g2d.setPaint( gp );
        g2d.fillRect( 0, 0, w, h );
    }    
    
    private void OnClickableLabelMouseOver(MouseEvent evt) {
        JLabel src = (JLabel) evt.getSource();
        src.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));         
    }

    
    private void OnInfoLabelMouseClick(iInsilicoModel Model) {
        FrameModelInfo InfoFrame = new FrameModelInfo(Model);
        InfoFrame.setLocationRelativeTo(null);
        InfoFrame.setVisible(true);
    }
    
    private void OnInfoConsLabelMouseClick(iInsilicoModelConsensus Model) {
        FrameModelInfo InfoFrame = new FrameModelInfo(Model);
        InfoFrame.setLocationRelativeTo(null);
        InfoFrame.setVisible(true);
    }
    
    private void OnSelectAllClick(ItemEvent evt) {
        JCheckBox src = (JCheckBox) evt.getItem();
        boolean status = src.isSelected();
        for (JCheckBox ComboboxModel : ComboboxModels) {
            ComboboxModel.setSelected(status);
        }
        for (VegaModelsWrapper.VegaModel m : EP.Models)
            m.Selected = status;
        for (VegaModelsWrapper.VegaModelConsensus m : EP.ModelsConsensus)
            m.Selected = status;
    }
    
    
    private class CBListener implements ActionListener {

        private JCheckBox Ref;
        private VegaModelsWrapper.VegaModel Model;
        private VegaModelsWrapper.VegaModelConsensus ModelCons;
        
        public CBListener(JCheckBox CB, VegaModelsWrapper.VegaModel model) {
            Ref = CB;
            Model = model;
            ModelCons = null;
        }
        
        public CBListener(JCheckBox CB, VegaModelsWrapper.VegaModelConsensus model) {
            Ref = CB;
            Model = null;
            ModelCons = model;            
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Model == null)
                ModelCons.Selected = Ref.isSelected();
            if (ModelCons == null)
                Model.Selected = Ref.isSelected();
        }
        
    }

    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
