package untitled20;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class DialogInsertResult extends JDialog {
  private JPanel panel1 = new JPanel();
  private TitledBorder titledBorder1;
  private JPanel jPanel1 = new JPanel();
  private XYLayout xYLayout1 = new XYLayout();
  private JLabel jLabel1 = new JLabel();
  private JButton jButton1 = new JButton();
  private Vector ResultVector = new Vector();
  private long lgScore;
  private JTextField jTextField1 = new JTextField();

  public DialogInsertResult(Frame frame, String title, boolean modal, Vector FormVector,long valueScore) {
    super(frame, title, modal);
    ResultVector=FormVector;
    lgScore=valueScore;
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public DialogInsertResult() {
    this(null, "", false, new Vector(),0);
  }
  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    this.setSize(new Dimension(385, 85));
    panel1.setLayout(null);
    this.setTitle("¬ведите своЄ им€");
    panel1.setBorder(titledBorder1);
    jPanel1.setBorder(titledBorder1);
    jPanel1.setBounds(new Rectangle(6, 8, 362, 45));
    jPanel1.setLayout(xYLayout1);

    jLabel1.setText("¬ведите своЄ им€:");
    jButton1.setText("jButton1");
    jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jButton1_mouseClicked(e);
      }
    });
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jTextField1.setText("√риша с посЄлка");
    jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        jTextField1_keyPressed(e);
      }
    });
    getContentPane().add(panel1);
    panel1.add(jPanel1, null);
    jPanel1.add(jTextField1,   new XYConstraints(109, 7, 220, 22));
    jPanel1.add(jLabel1, new XYConstraints(-1, 4, 109, 26));
    jPanel1.add(jButton1, new XYConstraints(333, 7, 17, 21));
    setLocationRelativeTo(null);
    setVisible(true);
  }

  void jButton1_actionPerformed(ActionEvent e) {

  }

  private void InsertNewHightScore(){
    String Name=jTextField1.getText();
    Name.replace('|','l');
    boolean isAdded=false;
    HightScoreTab h=new HightScoreTab(Name,lgScore,true);
    for (byte i=0;i<ResultVector.size();i++){
      HightScoreTab h2=(HightScoreTab)ResultVector.get(i);
      if (lgScore>h2.getScore()){  //получаетс€, сразу и сортируем
        ResultVector.insertElementAt(h,i);
        isAdded=true;
        break;
      }
    }
    if (ResultVector.size()<20 & !isAdded) ResultVector.add(h); //а если он меньше всех,добавл€ем в конец
    if (ResultVector.size()>20){
      for (int i=(ResultVector.size()-1);i>19;i--){
        ResultVector.removeElementAt(i);
      }
    }
    //итого - в векторе новый список
    String str="";
    try{
      RandomAccessFile aFile=new RandomAccessFile("result.txt","rw");//открываем дл€ записи
      BufferedReader in=new BufferedReader(new InputStreamReader(System.in,"cp866"));

      for (int i=0;i<(ResultVector.size()<20?ResultVector.size():20);i++){
        h=(HightScoreTab)ResultVector.get(i);
        str=h.getName()+"|"+String.valueOf(h.getScore())+"|\n";
        aFile.writeUTF(str);
      }
    }
    catch (Exception e){
      e.printStackTrace();
    }
    setVisible(false);
    dispose();
   // DialogInstruction f = new DialogInstruction(this, "»нструкци€ к игре", true, 0, ResultVector);
  }

  void jButton1_mouseClicked(MouseEvent e) {
    InsertNewHightScore();
  }

  void jTextField1_keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {
      case KeyEvent.VK_ENTER:
        InsertNewHightScore();
        break;
    }
  }
}