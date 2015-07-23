package untitled20;

import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

 class exJField extends JTextField {

   public exJField() {

     super.repaint();
     //paint();
     //repaint();
   }
   public void paintComponent() {
  // public void paint() {
     if (this!=null){
     Graphics gr=this.getGraphics();
     Rectangle rc=gr.getClipBounds();
     for(int i=1;i<=20;i++){
     gr.drawLine((int)(rc.getX()-i),(int)(rc.getY())
                 ,(int)(rc.getX()-i+10),(int)(rc.getY()+rc.getHeight()+10));
     super.paint(gr);
     this.setVisible(true);

     }
     }
   }


}