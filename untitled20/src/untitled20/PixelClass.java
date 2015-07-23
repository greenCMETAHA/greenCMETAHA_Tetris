package untitled20;

import java.awt.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PixelClass {
  private Color cColor;
  private byte btAspect;

  public PixelClass() {
    cColor = new Color(0,0,0);
    // ��� - ������� �������, ������� ����� ����������� ������
    btAspect=0;
    /*�������� ������:
    0 - ������ ����, ������� - ������ ���
    1 - �������
    2 - �������
    3 - ������
    4 - �����
    5 - ����������
    6 - �����
    7 - �������
    8 - �����-�������
    9 - ���������
    11 - ������
    12 - �����
    13 - �������
    14 - �����
    */
  }

  public PixelClass(byte aspect) {
    btAspect=aspect;
    setBlockColor(aspect);
  }

  public void setAspect(byte Value){
    this.btAspect=Value;
  }

  public void setAspect(int Value){
    this.btAspect=(byte)Value;
  }

  public byte getAspect(){
    return btAspect;
  }

  public Color getColor(){
    return cColor;
  }

  public void setBlockColor(byte Value){
    switch ((int)Value){
      case 0://0 - ������ ����, ������� - ������ ���
      {
        cColor=new Color(0,0,0);
        break;
      }
      case 1://1 - �������
      {
        cColor=new Color(255,0,0);
        break;
      }
      case 2://2 - �������
      {
        cColor=new Color(0,255,0);
        break;
      }
      case 3: //3 - ������
      {
        cColor=new Color(255,255,0);
        break;
      }
      case 4: //4 - �����
      {
        cColor=new Color(0,0,255);
        break;
      }
      case 5://5 - ����������
      {
        cColor=new Color(93,0,0);
        break;
      }
      case 6: //6 - �����
      {
        cColor=new Color(255,255,255);
        break;
      }
      case 7: //7 - �������
      {
        cColor=new Color(0,255,255);
        break;
      }
      case 8: //8 - �����-�������
      {
        cColor=new Color(0,97,0);
        break;
      }
      case 9: //9 - ���������
      {
        cColor=new Color(255,127,0);
        break;
      }
    }
  }
}


