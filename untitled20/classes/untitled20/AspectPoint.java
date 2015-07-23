package untitled20;

import java.awt.Component;
import java.util.*;
import java.awt.*;

public class AspectPoint{
  private Point point;
  private byte aspect;

  public AspectPoint(){};

  public AspectPoint(Point p, byte a){
    point=new Point();
    point.x=(byte)p.getX();
    point.y=(byte)p.getY();
    aspect=a;
  };

  public AspectPoint(byte x, byte y, byte a){
    point=new Point();
    point.x=x;
    point.y=y;
    aspect=a;
  };

  public void setPoint(Point p){
    point=new Point();
    point.x=(byte)p.getX();
    point.y=(byte)p.getY();
  }

  public void setPoint(byte x, byte y){
    point=new Point();
    point.x=x;
    point.y=y;
  }

  public Point getPoint(){
    return point;
  }

  public byte getX(){
    return (byte)point.getX();
  }

  public byte getY(){
    return (byte)point.getY();
  }

  public byte getAspect(){
    return aspect;
  }

  public void setAspect(byte a){
      aspect=a;
    }
  }