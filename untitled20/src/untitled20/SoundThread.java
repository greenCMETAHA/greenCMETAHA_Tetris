package untitled20;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

//сначала сделал через просто через процедуру, но звуки не хотели отображаться. Через поток - та же херня.

class SoundThread extends Thread{
  private void MakeSound(boolean ItIsPossible, String SoundFile){
    if (ItIsPossible){
      try{
        String str=SoundFile;//"/sound/"+SoundFile;
        java.applet.AudioClip ac = java.applet.Applet.newAudioClip(new java.net.URL("file:sound/"+str));
        ac.play();
      }catch(Exception e){
       System.out.print(e);
      }
    }
  }

  public SoundThread(boolean ItIsPossible, String SoundFile) {
    MakeSound(ItIsPossible,SoundFile);

  }
  public void run(){
  }

}