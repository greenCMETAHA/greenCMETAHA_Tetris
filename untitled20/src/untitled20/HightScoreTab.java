package untitled20;

import java.lang.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

class HightScoreTab{
  private String Name;
  private long Score;
  private boolean ThisHightScore;

  public HightScoreTab(String valueName, long valueScore, boolean valueThisHightScore){
    Name=valueName;
    Score=valueScore;
    ThisHightScore=valueThisHightScore;
  }

  public HightScoreTab(String valueName, long valueScore){
    Name=valueName;
    Score=valueScore;
    ThisHightScore=false;
  }


  public HightScoreTab(){
    Name="";
    Score=0;
    ThisHightScore=false;
  }

  public long getScore(){
    return Score;
  }

  public String getName(){
  return Name;
  }

  public boolean getThisHightScore(){
  return ThisHightScore;
  }


}