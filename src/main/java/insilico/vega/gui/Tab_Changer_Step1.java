package insilico.vega.gui;

import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
/**
 *
 * @author ASpiliopoulos
 */
public class Tab_Changer_Step1 {
    
    JPanel SideBar,Step1,Step2,Step3,Progress_Bar,Debug_Panel;
    JLabel Step1_Over,Step1_Label,Step2_Over,Step2_Label,Step3_Over,Step3_Label,
           Predict_Btn,SideBar_Label,Header_Img1,Header_Img2,Header_Img3,
            Cancel_Btn1,Cancel_Label;
    JProgressBar Bar_Lbl;
 //   JButton Cancel_Btn;
    
    Object[][] arg1;

    public Tab_Changer_Step1(Object[][] arg1){
        
        this.Step1=(JPanel) arg1[0][0];
        this.Step2=(JPanel) arg1[0][1];
        this.Step3=(JPanel) arg1[0][2];         
        this.SideBar=(JPanel) arg1[0][3];
        this.Progress_Bar=(JPanel) arg1[0][4];
        this.Debug_Panel = (JPanel) arg1[0][5];
        this.Step1_Over = (JLabel) arg1[1][0];
        this.Step2_Over = (JLabel) arg1[1][1];
        this.Step3_Over = (JLabel) arg1[1][2];
        this.Predict_Btn = (JLabel) arg1[1][3];
        this.SideBar_Label = (JLabel) arg1[1][4];
        this.Step1_Label = (JLabel) arg1[2][0];
        this.Step2_Label = (JLabel) arg1[2][1];
        this.Step3_Label = (JLabel) arg1[2][2];
        this.Header_Img1 = (JLabel) arg1[3][0];
        this.Header_Img2 = (JLabel) arg1[3][1];
        this.Header_Img3 = (JLabel) arg1[3][2];
        this.Cancel_Btn1 = (JLabel) arg1[4][0];
        this.Bar_Lbl = (JProgressBar) arg1[4][1];
        this.Cancel_Label = (JLabel) arg1[4][2];
        
        
        this.arg1 = arg1;
        
    }
    
    public void gotoStep1(){
        
        
        //animation.interrupt();
        SideBar.setVisible(true);
        Step1.setVisible(true);
        Step2.setVisible(false);
        Step3.setVisible(false);
        Progress_Bar.setVisible(false);
     //   Debug_Panel.setVisible(false);
        Step1_Label.setVisible(false);
        Step1_Over.setVisible(true);
        Step2_Label.setVisible(true);
        Step2_Over.setVisible(false);
        Step3_Label.setVisible(true);
        Step3_Over.setVisible(false);
        Cancel_Btn1.setVisible(false);
        Cancel_Label.setVisible(false);
        Bar_Lbl.setVisible(false);

             
    }
    
    public void gotoStep2(){
        
        SideBar.setVisible(true);
        Step1.setVisible(false);
        Step2.setVisible(true);
        Step3.setVisible(false);
        Progress_Bar.setVisible(false);
//    Debug_Panel.setVisible(false);
        Step1_Label.setVisible(true);
        Step1_Over.setVisible(false);
        Step2_Label.setVisible(false);
        Step2_Over.setVisible(true);
        Step3_Label.setVisible(true);
        Step3_Over.setVisible(false);
        Cancel_Btn1.setVisible(false);
        Cancel_Label.setVisible(false);
        Bar_Lbl.setVisible(false);
        

    }
    
    public void gotoStep3(){
        
        SideBar.setVisible(true);
        Step1.setVisible(false);
        Step2.setVisible(false);
        Step3.setVisible(true);
        Progress_Bar.setVisible(false);
    //    Debug_Panel.setVisible(false);
        Step1_Label.setVisible(true);
        Step1_Over.setVisible(false);
        Step2_Label.setVisible(true);
        Step2_Over.setVisible(false);
        Step3_Label.setVisible(false);
        Step3_Over.setVisible(true);
        Cancel_Btn1.setVisible(false);
        Cancel_Label.setVisible(false);
        Bar_Lbl.setVisible(false);

               
    }
    
    public void gotoNoStep(){
        
        SideBar.setVisible(true);
        Step1_Label.setVisible(true);
        Step1_Over.setVisible(false);
        Step2_Label.setVisible(true);
        Step2_Over.setVisible(false);
        Step3_Label.setVisible(true);
        Step3_Over.setVisible(false);
               
    }
    

    public void predict() {
        Step1.setVisible(false);
        Step2.setVisible(false);
        Step3.setVisible(false);
        SideBar.setVisible(false);
        Progress_Bar.setVisible(true);
        Cancel_Btn1.setVisible(true);
        Cancel_Label.setVisible(true);
        Bar_Lbl.setVisible(true);
    }
    
    
    public void endProcess() {
        this.gotoStep1();
        JOptionPane.showMessageDialog(null, "Calculation completed. Reports Generated");
    }
    

    public void cancelProcess() {
        this.gotoStep1();
        JOptionPane.showMessageDialog(null, "Calculation cancelled by user");
    }
        
  
    
}

