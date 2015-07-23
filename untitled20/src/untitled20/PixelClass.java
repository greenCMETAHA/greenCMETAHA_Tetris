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
    // это - кусочек рисунка, которым будет заполняться стакан
    btAspect=0;
    /*признаки блоков:
    0 - пустой блок, условно - черный фон
    1 - красный
    2 - зеленый
    3 - желтый
    4 - синий
    5 - коричневый
    6 - белый
    7 - голубой
    8 - темно-зеленый
    9 - оранжевый
    11 - камень
    12 - огонь
    13 - граната
    14 - магия
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
      case 0://0 - пустой блок, условно - черный фон
      {
        cColor=new Color(0,0,0);
        break;
      }
      case 1://1 - красный
      {
        cColor=new Color(255,0,0);
        break;
      }
      case 2://2 - зеленый
      {
        cColor=new Color(0,255,0);
        break;
      }
      case 3: //3 - желтый
      {
        cColor=new Color(255,255,0);
        break;
      }
      case 4: //4 - синий
      {
        cColor=new Color(0,0,255);
        break;
      }
      case 5://5 - коричневый
      {
        cColor=new Color(93,0,0);
        break;
      }
      case 6: //6 - белый
      {
        cColor=new Color(255,255,255);
        break;
      }
      case 7: //7 - голубой
      {
        cColor=new Color(0,255,255);
        break;
      }
      case 8: //8 - темно-зеленый
      {
        cColor=new Color(0,97,0);
        break;
      }
      case 9: //9 - оранжевый
      {
        cColor=new Color(255,127,0);
        break;
      }
    }
  }
}


