package untitled20;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class DialogInstruction extends JDialog {
  private JButton jButton1 = new JButton();
  private JTextArea jTextArea1 = new JTextArea();
  private TitledBorder titledBorder1;
  private TitledBorder titledBorder2;
  private byte bFormType;
  private Vector ResultVector;

  public DialogInstruction(Frame frame, String title, boolean modal, int FormType, Vector FormVector) {
    super(frame, title, modal);
    bFormType=(byte)FormType;  //0 - is instruction; 1 - is results;
    ResultVector=FormVector;
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public DialogInstruction() {
    this(null, "", false,0,new Vector());
  }
  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    titledBorder2 = new TitledBorder("");
    this.setSize(new Dimension(408,510));
    jButton1.setBounds(new Rectangle(153, 455, 89, 27));
    jButton1.setText("�������");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    this.getContentPane().setLayout(null);
    jTextArea1.setBackground(SystemColor.text);
    jTextArea1.setEnabled(false);
    jTextArea1.setBorder(titledBorder2);
    jTextArea1.setDisabledTextColor(Color.black);
    jTextArea1.setEditable(false);
    jTextArea1.setBounds(new Rectangle(5, 7, 390, 439));
    this.getContentPane().add(jTextArea1, null);
    this.getContentPane().add(jButton1, null);
    if (bFormType==0){//instruction
      jTextArea1.setText("\n��� ������:\n");
      jTextArea1.append("�� ������: �������� ������ �����/������ (�� �������� \n ����������), ��� A/D ��� ����������� �����.\n");
      jTextArea1.append("������ �����, ��� W - ��� ����������� ����� �� �����.\n");
      jTextArea1.append("������ ����, ��� S - ��� �������� �����.\n");
      jTextArea1.append("������ - ��� �������� ���������� ����� � ������\n");
      jTextArea1.append("����� �������� ���������� ������ � �������� ����\n");
      jTextArea1.append("������� - �������� ����� ������������� �����. ����� -\n ������� �����������, ��� ���������. ����� - �������\n ������� � �������� 2 �������.\n");
      jTextArea1.append("������ - ������ �� ������, �� ������ ������.\n\n");
      jTextArea1.append("���� ���� - ������� 6 ����������� ������ ��\n �����������, ��� ���������.\n\n");
      jTextArea1.append("�����. :)");
    }else{ //Hight score
      jTextArea1.setText("����������:\n");
      if (ResultVector.size()>0){
        for (byte i=0;i<ResultVector.size();i++){
          HightScoreTab h=(HightScoreTab)ResultVector.get(i);
          if (h.getThisHightScore()){ //marking current result
            Graphics gr=this.getGraphics();
            gr.setColor(Color.white);
            gr.fillRect(5, (int)(70+(i*20)), 390, 20);
          }
          String Name=h.getName();
          String Score=String.valueOf(h.getScore());
          String str="";
          for (byte j=0;j<(51-(Name.length()+Score.length()));j++)
            str=str+".";
          jTextArea1.append(Name+" "+str+" "+Score+"\n");
        }
      }else{
        for (byte i=0;i<20;i++)
          jTextArea1.append("......................................................\n");
      }
    }
    setLocationRelativeTo(null);
    setVisible(true);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    setVisible(false);
    dispose();
  }

}