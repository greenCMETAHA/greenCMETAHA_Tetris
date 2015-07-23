package untitled20;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.borland.jbcl.layout.*;
import java.lang.*;
import java.awt.image.*;
import java.util.*;
import java.beans.*;
import com.borland.dbswing.*;
import java.io.*;
import javax.sound.midi.*;
import sun.audio.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Frame1 extends JFrame implements Runnable{
  private JPanel contentPane;
  private exJField exJField1 = new exJField();
  private exJField exJField2 = new exJField();
  private JPanel jPanel1 = new JPanel();
  private TitledBorder titledBorder1;
  private JPanel jPanel5 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JPanel jPanel3 = new JPanel();
  private JSlider jSliderLevel = new JSlider();
  private JPanel jPanel4 = new JPanel();
  private JSlider jSliderSpeed = new JSlider();
  private JButton jButton1 = new JButton();
  private JButton jButton2 = new JButton();
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabelScore = new JLabel();
  private XYLayout xYLayout2 = new XYLayout();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JButton jButton3 = new JButton();
  private JPanel jGamePanel = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private BorderLayout borderLayout3 = new BorderLayout();

  private int xSizeWindow, ySizeWindow;
  private byte btPause; //0 - игра закончена, 1 - игра, 2 - пауза;
  private boolean bResult, bEndMove, bNewBlock, bActionContinued, bSpaceDown,
      bIsChange;
  private long lgScore;
  private byte[][] Glass = new byte[35][16];
  private JPanel jPanelNextObject = new JPanel(); //рабочая матрица стакана.
  private Thread GameThread;
  private BlockClass TurnLineObject, btGameBlock;
  private BlockClass btTurnLineObject;

  private byte btLevelBlock, bLineCounter, bRowCounter, bPointOfChangeLevel;
  private byte[] GlassCell; //координаты кубиков падающего блока
  private boolean bChangeLevel, isAvalanche;
  private byte btKilling, BlockLimit;
  private Image iLevelImage, GlassImage;
  private Sequencer sequencer;
  int iSoundNumber;

  public java.util.Random random = new java.util.Random();

  private JButton jButton4 = new JButton();
  private JLabel jLabel2 = new JLabel(); //количество разных блоков. Нарастает с количеством очков от 7 до 22
  private Vector CoordinateVectorAvalanche = new Vector();
  private JCheckBox jCheckBox1 = new JCheckBox();
  private JCheckBox jCheckBox2 = new JCheckBox();

  //Construct the frame
  public Frame1() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void update(Graphics g) {
    MyPaint(g);
  }

  private boolean IsIn(AvalancheMassive pAvalanche, Vector MyVector) {
    boolean result = false;

    for (byte i = 0; i < MyVector.size(); i++) {
      AvalancheMassive p = (AvalancheMassive) MyVector.get(i);
      if ( (p.getX() == pAvalanche.getX()) & (p.getY() == pAvalanche.getY())) {
        result = true; //т.е. такая точка уже помечена на удаление
        break;
      }
    }

    return result;
  }

  private void AvalancheCoordinateVectorAdd(byte x, byte y, Vector MyVector) {
    byte aspect = Glass[y][x];
    byte newaspect = aspect;
    if (aspect == 11) newaspect = 10;
    if (aspect == 12) newaspect = 22;
    if (aspect == 13) newaspect = 33;
    if (aspect == 14) newaspect = 44;

    AvalancheMassive pAvalanche = new AvalancheMassive(x, y, newaspect);
    if (!IsIn(pAvalanche, MyVector))
      MyVector.add(pAvalanche);
    else return;
    if (aspect == 12) { //огонь
      byte lenght=(byte)Math.round((jSliderLevel.getValue()/20)-1);
      lenght=(lenght<2?2:lenght);
      if (btKilling == 1) { //если по горизонтали, сожжем вертикаль на 2 пункта
        for (byte j = (byte) ( (y + lenght) < 35 ? y + lenght : 34);
             j >= ( (y - lenght) > 5 ? (y - lenght) : 6); j--) {
          pAvalanche = new AvalancheMassive(x, j, (byte) 22);
          AvalancheCoordinateVectorAdd(x, j, MyVector);
        }
      }
      else {
        if (btKilling == 2) { //если по ветикали, сожжем горизонталь на 2 пункта
          for (byte j = (byte) ( (x - lenght) < 0 ? 0 : x - lenght);
               j <= ( (x + lenght) > 15 ? 15 : (x + lenght)); j++) {
            pAvalanche = new AvalancheMassive(j, y, (byte) 22);
            AvalancheCoordinateVectorAdd(j, y, MyVector);
          }
        }
      }
    }
    if (aspect == 13) { //граната
      byte lenght=(byte)Math.round((jSliderLevel.getValue()/20)-1);
      lenght=(lenght<1?1:lenght);
      for (byte i = ( (x - lenght) < 0 ? 0 : (byte) (x - lenght));
           (i <= ( (byte) (x + lenght) < 16 ? (x + lenght) : 15)); i++) {
        for (byte j = ( (y - lenght) > 5 ? (byte) (y - lenght) : 6);
             (j <= ( (byte) (y + lenght) < 35 ? (y + lenght) : 34)); j++) {
          pAvalanche = new AvalancheMassive(i, j, (byte) 33);
          AvalancheCoordinateVectorAdd(i, j, MyVector);
        }
      }
    }
    if (aspect == 14) { //магия. уничтожим пустые места в квадрате +2/-2. Кубики съедутся
      byte lenght=(byte)Math.round((jSliderLevel.getValue()/20)-1);
      lenght=(lenght<2?2:lenght);
      for (byte i = ( (x - lenght) >= 0 ? (byte) (x - lenght) : 0);
           (i <= ( (byte) (x + lenght) < 16 ? (x + lenght) : 15)); i++) {
        for (byte j = ( (y - lenght) > 5 ? (byte) (y - lenght) : 6);
             (j <= ( (byte) (y + lenght) < 35 ? (y + lenght) : 34)); j++) {
          if (Glass[j][i] == 0) {
            pAvalanche = new AvalancheMassive(i, j, (byte) 44);
            AvalancheCoordinateVectorAdd(i, j, MyVector);
          }
        }
      }
    }
  }

  private void MyPaint(Graphics gr) {
    int xGamePanelSize = (int) jGamePanel.getSize().getWidth();
    int yGamePanelSize = (int) jGamePanel.getSize().getHeight();

    if (bChangeLevel | jSliderLevel.getValue() > 29) { //showing a picture of game level
      int xSize = (int) jPanel3.getSize().getWidth() - 4;
      int ySize = (int) + jPanel3.getSize().getHeight() - 4;
      int x0 = (jPanel5.getLocation().x + jPanel3.getLocation().x + 1);
      int y0 = (int) (jPanel5.getLocation().y + jPanel3.getLocation().y +
                      jPanel2.getSize().getHeight() + 1);
      gr.setColor(this.getBackground());
      gr.fillRect(x0 + 5, y0, xSize - 3, ySize - 3);
      gr.drawImage(iLevelImage,
                   (int) (x0 + 4 +
                          ( (jSliderLevel.getValue() / 9.) - 3) * (xSize / 9)),
                   y0, ySize - 3, ySize - 3, this);
      bChangeLevel = false;
    }

    //making the image of nxt block
    int xSize = (int) jGamePanel.getSize().getWidth();
    int ySize = (int) jGamePanel.getSize().getHeight();
    int x0 = jGamePanel.getX();
    int y0 = jGamePanel.getY() + 20;
    float removalX = (float) (xSize / (float) 16),
        removalY = (float) (ySize / (float) 30);

    //building the game Glass whith current block
    BufferedImage buffImage = new BufferedImage(xSize, ySize,
                                                BufferedImage.SCALE_DEFAULT);
    Graphics2D big = buffImage.createGraphics();

    //drawing map
    big.setColor(new Color(145, 145, 145));
    for (int i = 0; i <= 30; i++) {
      big.drawLine(0, (int) (i * removalY), xSize, (int) (i * removalY));
    }
    for (int j = 0; j <= 16; j++) {
      big.drawLine( (int) (j * removalX), 0, (int) (j * removalX), ySize);
    }

    //drawing  coloured blocks
    if (btPause > 0) {
      for (byte i = 5; i < 35; i++) //и кубики на ней
        for (byte j = 0; j <= 15; j++) {
          byte aspect = Glass[i][j];
          if (aspect < 0) {
            if (aspect > -10) {
              big.setColor(new Color(256 + (aspect * 40), 256 + (aspect * 40),
                                     256 + (aspect * 40)));
              big.fillRect( (int) (j * removalX) + 1,
                           (int) ( (i - 5) * removalY) + 1,
                           (int) (removalX) - 1, (int) (removalY) - 1);
            }
          }
          else {
            if (aspect < 11) { //((aspect<11) & (aspect>0)) { //рисуем цветной кубик
              PixelClass pc = new PixelClass(aspect);
              big.setColor(pc.getColor());
              big.fillRect( (int) (j * removalX) + 1,
                           (int) ( (i - 5) * removalY) + 1,
                           (int) (removalX) - 1, (int) (removalY) - 1);
            }
            else { //выведем бонусный кубик
              Image img = getToolkit().getImage("jpg/Aspect" +
                                                Integer.toString(aspect) +
                                                ".jpg");
              big.drawImage(img, (int) (j * removalX) + 1,
                            (int) ( (i - 5) * removalY) + 1,
                            (int) (removalX) - 1, (int) (removalY) - 1, this);
            }
          }
        }
    }
    gr.drawImage(buffImage, x0 + 3, y0 + 3, this);

    //showing next block
    if (btPause != 0) { //обновить "блок будущего"  //  btTurnLineObject
      xSize = (int) jPanelNextObject.getSize().getWidth() - 10;
      ySize = (int) jPanelNextObject.getSize().getHeight() - 10;
      x0 = (int) (xGamePanelSize + jPanelNextObject.getX() + 15);
      y0 = jPanelNextObject.getY() + 5;
      double koefX = xSize / 5.;
      double koefY = ySize / 5.;

      //Создаём Имидж, потом чуть-чуть его наклоним, и выведем в панель jPanelNextObject
      buffImage = new BufferedImage(xSize, ySize, BufferedImage.SCALE_DEFAULT);
      big = buffImage.createGraphics();
      big.setColor(this.getBackground());
      big.fillRect(0, 0, xSize + 8, ySize + 10);

      Vector CoordinateVector = btTurnLineObject.getCoordinateVector();
      for (byte i = 0; i < CoordinateVector.size(); i++) {
        AspectPoint p = (AspectPoint) CoordinateVector.get(i);
        byte aspect = p.getAspect();
        if (aspect != 0) {
          byte x = (byte) (p.getX() - 6);
          byte y = p.getY();
          big.setColor(Color.black);
          big.drawRect( (int) (x * koefX), (int) (y * koefY), (int) koefX,
                       (int) koefY);
          if (aspect < 10) { //color
            PixelClass pc = new PixelClass(aspect);
            big.setColor(pc.getColor());
            big.fillRect( (int) (x * koefX) + 1, (int) (y * koefY) + 1,
                         (int) koefX - 1, (int) koefY - 1);
          }
          else { //picture
            Image img = getToolkit().getImage("jpg/Aspect" +
                                              Integer.toString(aspect) + ".jpg");
            big.drawImage(img, (int) ( (x * koefX)) + 1,
                          (int) ( (y * koefY)) + 1, (int) koefX - 1,
                          (int) koefY - 1, this);
          }
        }
      }
      gr.drawImage(buffImage, x0 + 10, y0 + 20, this);
      //AffineTransform at = new AffineTransform();   //пока не будем. Untitled3, стр 1430
      //at.rotate(Math.PI/12);     // Задаем поворот на 45 градусов
      bNewBlock = false;
    }

    //showing pause panel
    if (btPause == 2) {
      xSize = (int) jGamePanel.getSize().getWidth();
      ySize = (int) jGamePanel.getSize().getHeight() / 2;
      x0 = jGamePanel.getX();
      y0 = jGamePanel.getY() + 20;
      gr.setColor(Color.red);
      gr.fillRect(10, 30 + ySize - 60, xSize - 1, 60);
      gr.setColor(Color.black);
      gr.setFont(new java.awt.Font("Dialog", 1, 35));
      gr.drawString("PAUSE", (int) (10 + (xSize / 2) - 58), ySize+10);
    }
  }

  private void SelectKillingBlocks() {
    if (CoordinateVectorAvalanche.size() >= BlockLimit) {
      Vector MyVector = new Vector(); //будем записывать/считывать сразу по 3 элемента: 16 колонок, 1 позиция, с которой начинаем уничтоэение, 30 позиций максимум, которые уничтожаем.
      //MyVector=CoordinateVectorAvalanche;
      for (byte i = 0; i < CoordinateVectorAvalanche.size(); i++) {
        AvalancheMassive p = (AvalancheMassive) CoordinateVectorAvalanche.get(i);
        AvalancheCoordinateVectorAdd(p.getX(), p.getY(), MyVector);
      }
      AddScore(MyVector); //подсчет очков
      CoordinateVectorAvalanche = MyVector;
    }
  }

  private void RollUpGlass() {
    for (byte i = 6; i < 35; i++) {
      boolean bKilling = false;
      for (byte j = 0; j < 16; j++) {
        if (Glass[i][j] < 0) {
          for (byte k = (byte) (i - 1); k > 5; k--) {
            Glass[k + 1][j] = (k < 5 ? 0 : Glass[k][j]);
          }
          bKilling = true;
        }
      }
      if (bKilling) {
        MyPaint(getGraphics());
        try {
          GameThread.sleep(30);
        }
        catch (InterruptedException ie) {}
      }
    }
    CoordinateVectorAvalanche.clear();
    for (byte i = 6; i < 35; i++) {
      for (byte j = 0; j < 16; j++) {
        if (Glass[i][j] < 0) Glass[i][j] = 0;
      }
    }

  }

  private void ShowKillingBlocks() {
    for (byte j = 1; j < 7; j++) {
      for (byte i = 0; i < CoordinateVectorAvalanche.size(); i++) {
        AvalancheMassive p = (AvalancheMassive) CoordinateVectorAvalanche.get(i);
        Glass[p.getY()][p.getX()] = (byte) ( -1 * j);
      }
      MyPaint(getGraphics());
      try {
        GameThread.sleep(70);
      }
      catch (InterruptedException ie) {}
    }
  }

  private Vector ReadFromFile() {
    Vector ResultVector = new Vector();
    File file = new File("result.txt");
    boolean exists = file.exists();
    if (exists) {
      int bytesAvailable = 0;
      if (file.canRead()) {
        try {
          //FileInputStream inFile = new FileInputStream(new File("result.txt"),"UTF8");
          //BufferedReader in = new BufferedReader(inFile);
          BufferedReader in = new BufferedReader(new InputStreamReader(new
              FileInputStream("result.txt"), "UTF8"));
          String str;
          while ( (str = in.readLine()) != null) {
            String Name = "", Score = "";
            byte bNextWord = 0;
            for (int i = 2; i < str.length(); i++) { //2 байта - на код UTF8 строки
              char ch = str.charAt(i);
              if (ch != '|') {
                if (bNextWord == 0) Name = Name + ch;
                if (bNextWord == 1) Score = Score + ch;
              }
              else {
                bNextWord = ++bNextWord;
                if (bNextWord == 2) {
                  long ll = Long.parseLong(Score);
                  ResultVector.add(ResultVector.size(),
                                   new HightScoreTab(Name, ll));
                  break;
                }
              }
            }
          }
          in.close();
        }
        catch (IOException ex) {}
        ;
      }
    }

    return ResultVector;
  }

  private void InsertNewHightScore() {
    Vector ResultVector = ReadFromFile();
    boolean bIsHightScore = false;
    if (ResultVector.size() < 20)
      bIsHightScore = true;
    else
      for (int i = 0; i < ResultVector.size(); i++) {
        HightScoreTab h = (HightScoreTab) ResultVector.get(i);
        if (h.getScore() < lgScore) {
          bIsHightScore = true;
          break;
        }
      }
    if (bIsHightScore = true) {
      PlaySample("APPLAUSE.WAV");
      new DialogInsertResult(this, "Ваше имя", true, ResultVector, lgScore);
    }
  }

  public void run() {
    isAvalanche = false;
    while (bActionContinued) {
      if (btPause != 2) {
        try {

          if (!isAvalanche) //на большой скорости не успеват стереться старая, а уже новая лезет
            TheGame();
          MyPaint(getGraphics());
          for (byte i = 0; i < 5; i++) {
            MyPaint(getGraphics());
            if (bSpaceDown) {
              //GameThread.sleep(2);
            }
            else {
              GameThread.sleep(1000 / jSliderSpeed.getValue());
            }
          }
        }
        catch (InterruptedException ie) {}

        if (bEndMove) {
          //проверяем на сходимость строк и в озможность из удаления
          // 6 кубиков подряд в строке. Их удаление вызовет смещение вниз.
          while (true) {
            isAvalanche = true;
            Avalanche();
            if (CoordinateVectorAvalanche.size() == 0) {
              isAvalanche = false;
              break;
            }
            else {
              SelectKillingBlocks();
              ShowKillingBlocks();
              MyPaint(getGraphics());
              RollUpGlass();
            }
          }
        }
      }
    }
  }

  //Component initialization
  public void paint(Graphics g) {
    super.paint(g);
    MyPaint(g);
    setVisible(true);
  }

  private void PlaySample(String valuefilename) {
    if (jCheckBox2.isSelected()) {
      try {
        File soundFile = new File(valuefilename);
        InputStream in = new FileInputStream(soundFile);
        AudioStream as = new AudioStream(in);
        AudioPlayer.player.start(as);
      }
      catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }
  }

  private void AddScore(Vector MyVector) {
    if (MyVector != null) {
      if (!MyVector.isEmpty()) lgScore = lgScore + BlockLimit;
      if (MyVector.size() > BlockLimit) {
        int i = MyVector.size() - BlockLimit;
        for (int j = 1; j <= i; j++) {
          lgScore = lgScore + j + j;
          //PlaySemple("KILL.WAV");
        }
        boolean bPlayed = false;
        for (i = 0; i < MyVector.size(); i++) {
          AvalancheMassive p = (AvalancheMassive) MyVector.get(i);
          byte aspect = p.getAspect();
          if (aspect > 9) {
            lgScore = lgScore + p.getAspect();
            if (aspect == 22 & !bPlayed) {
              PlaySample("BURN.WAV");
              bPlayed = true;
            }
            if (aspect == 33 & !bPlayed) {
              PlaySample("BOMB.WAV");
              bPlayed = true;
            }
            if (aspect == 44 & !bPlayed) {
              PlaySample("MAGIC.WAV");
              bPlayed = true;
            }
          }
        }
      }
    }
    jLabelScore.setText(String.valueOf(lgScore));
  }

  private void jbInit() throws Exception {
    //setIconImage(Toolkit.getDefaultToolkit().createImage(Frame1.class.getResource("[Your Icon]")));
    contentPane = (JPanel)this.getContentPane();
    titledBorder1 = new TitledBorder("");
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(600, 500));
    xSizeWindow = 600;
    ySizeWindow = 500;
    this.setTitle("Тетрис Зеленой СМЕТАНЫ");
    this.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        this_propertyChange(e);
      }
    });
    jPanel5.setBorder(titledBorder1);
    jPanel5.setLayout(xYLayout2);
    jPanel2.setBorder(titledBorder1);
    jPanel2.setLayout(null);
    jPanel3.setBorder(titledBorder1);
    jSliderLevel.setMaximum(90);
    jSliderLevel.setMinimum(30);
    jSliderLevel.setVerifyInputWhenFocusTarget(false);
    jSliderLevel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        jSliderLevel_mouseDragged(e);
      }
    });
    jSliderLevel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jSliderLevel_mouseClicked(e);
      }
    });
    jPanel4.setBorder(titledBorder1);
    jPanel4.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        jPanel4_focusLost(e);
      }
    });
    jSliderSpeed.setMinimum(10);
    jSliderSpeed.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        jSliderSpeed_mouseDragged(e);
      }
    });
    jSliderSpeed.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jSliderSpeed_mouseClicked(e);
      }
    });
    jButton1.setNextFocusableComponent(jGamePanel);
    jButton1.setToolTipText("");
    jButton1.setVerifyInputWhenFocusTarget(false);
    jButton1.setText("Начать игру");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jButton2.setVerifyInputWhenFocusTarget(false);
    jButton2.setHorizontalAlignment(SwingConstants.RIGHT);
    jButton2.setHorizontalTextPosition(SwingConstants.RIGHT);
    jButton2.setText("Рекорды >>>");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton2_actionPerformed(e);
      }
    });
    jLabel1.setFont(new java.awt.Font("Dialog", 1, 12));
    jLabel1.setText("Score:");
    jLabel1.setBounds(new Rectangle(4, 4, 52, 17));
    jLabelScore.setHorizontalAlignment(SwingConstants.RIGHT);
    jLabelScore.setHorizontalTextPosition(SwingConstants.RIGHT);
    jLabelScore.setText("0");
    jLabelScore.setBounds(new Rectangle(71, 3, 208, 18));
    contentPane.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        contentPane_componentResized(e);
      }
    });
    jPanel1.setLayout(borderLayout2);
    jPanel1.setPreferredSize(new Dimension(310, 480));
    jPanel1.setSize(new Dimension(290, 490));
    jButton3.setText("Выход");
    jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jButton3_mouseClicked(e);
      }
    });
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton3_actionPerformed(e);
      }
    });
    jGamePanel.setLayout(borderLayout3);
    jPanelNextObject.setBorder(BorderFactory.createLineBorder(Color.black));
    jButton4.setVerifyInputWhenFocusTarget(false);
    jButton4.setText("Инструкция >>>");
    jButton4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton4_actionPerformed(e);
      }
    });
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 25));
    jLabel2.setText("jLabel2");
    jGamePanel.setBackground(SystemColor.textHighlightText);
    jGamePanel.addKeyListener(new Frame1_jGamePanel_keyAdapter(this));
    jCheckBox1.setText("Играть музыку");
    jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jCheckBox1_actionPerformed(e);
      }
    });
    jCheckBox2.setText("Звуковые эффекты");
    jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jCheckBox2_actionPerformed(e);
      }
    });
    contentPane.setFocusable(true);  //for java 1.6
    contentPane.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jGamePanel, BorderLayout.CENTER);
    contentPane.add(jPanel5, BorderLayout.EAST);
    jPanel5.add(jPanel2, new XYConstraints( -2, 1, 283, 26));
    jPanel2.add(jLabel1, null);
    jPanel5.add(jPanel3, new XYConstraints( -2, 31, 283, 59));
    jPanel5.add(jSliderLevel, new XYConstraints( -2, 93, 282, 49));
    jPanel5.add(jPanel4, new XYConstraints(1, 152, 279, 66));
    jPanel4.add(jLabel2, null);
    jPanel5.add(jSliderSpeed, new XYConstraints(2, 220, 278, 45));
    jPanel5.add(jPanelNextObject, new XYConstraints(3, 344, 138, 115));
    jPanel5.add(jButton1, new XYConstraints(0, 275, 281, 33));
    jPanel5.add(jButton4, new XYConstraints(154, 324, 126, 28));
    jPanel5.add(jCheckBox2, new XYConstraints(152, 436, -1, 20));
    jPanel5.add(jCheckBox1, new XYConstraints(152, 419, 125, 19));
    jPanel5.add(jButton3, new XYConstraints(154, 388, 126, 28));
    jPanel5.add(jButton2, new XYConstraints(154, 355, 126, 30));
    jPanel2.add(jLabelScore, null);
    jPanel1.setBorder(titledBorder1);

    for (int i = 0; i < 30; i++)
      for (int j = 0; j < 16; j++)
        Glass[i][j] = 0;

    btPause = 0;
    bResult = false;
    bEndMove = false;
    lgScore = 0;
    TurnLineObject = new BlockClass();
    btLevelBlock = 7;
    BlockLimit = 6;
    btKilling = 0;
    jSliderSpeed.setValue(10);
    jLabel2.setText(Integer.toString(jSliderSpeed.getValue()));
    jSliderLevel.setValue(30);
    jSliderLevel_move();
    bChangeLevel = true;
    bNewBlock = false;
    bSpaceDown = false;
    iLevelImage = getToolkit().getImage("jpg/kubic3.jpg");
    bActionContinued = true;
    bIsChange = false;
    bChangeLevel = false;
    iSoundNumber = 772;
    try {
      String filename = "sound/bwv" + String.valueOf(iSoundNumber) + ".mid";
      Sequence sequence = MidiSystem.getSequence(new File(filename));
      sequencer = MidiSystem.getSequencer();
      sequencer.addMetaEventListener(new MetaEventListener() {
        public void meta(MetaMessage event) {
          if (event.getType() == 47) {
            iSoundNumber = (iSoundNumber == 781 ? 772 : iSoundNumber + 1);
            String filename = "sound/bwv" + String.valueOf(iSoundNumber) +
                ".mid";
            try {
              Sequence sequence = MidiSystem.getSequence(new File(filename));
              sequencer.open();
              sequencer.setSequence(sequence);
              sequencer.start();
            }
            catch (Exception e) {
            }
          }
        }
      });
      sequencer.open();
      sequencer.setSequence(sequence);
    }
    catch (Exception e) {}
    ;

    jCheckBox1.setSelected(true);
    jCheckBox2.setSelected(true);
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  void contentPane_componentResized(ComponentEvent e) {
    int xSize = (int)this.getSize().getWidth();
    int ySize = (int)this.getSize().getHeight();
    if (ySize != ySizeWindow) {
      if (ySize <= 500) {
        xSize = 600;
        ySize = 500;
      }
      else {
        xSize = (int) (ySize * (6. / 5));
      }
    }
    else {
      if (xSize != xSizeWindow) {
        if (xSize <= 600) {
          xSize = 600;
          ySize = 500;
        }
        else {
          ySize = (int) (ySize * (5. / 6));
        }
      }
    }
    if (ySize > Toolkit.getDefaultToolkit().getScreenSize().height) {
      ySize = Toolkit.getDefaultToolkit().getScreenSize().height;
      xSize = (int) (ySize * (6. / 5));
    }
    xSizeWindow = xSize;
    ySizeWindow = ySize;

    this.setSize(new Dimension(xSize, ySize));
    //jGamePanel.setSize(new Dimension((int)(jPanel1.getSize().getWidth()-2),(int)(jPanel1.getSize().getHeight()-2)));
    super.paint(getGraphics());
    repaint();
  }

  void jButton3_actionPerformed(ActionEvent e) {

  }

  void jButton3_mouseClicked(MouseEvent e) {
    if (GameThread != null) GameThread.stop();
    System.exit(0);
  }

  private void StartGame() {
    for (int i = 0; i < 35; i++)
      for (int j = 0; j < 16; j++)
        Glass[i][j] = 0;
    btPause = 1;
    lgScore = 0;
    jLabelScore.setText("0");
    bLineCounter = 0;
    bRowCounter = 0;
    bPointOfChangeLevel = 0;
    bEndMove = true;
    isAvalanche = false;
    btTurnLineObject = new BlockClass( (byte) btLevelBlock,
                                      (byte) (Math.round(jSliderLevel.getValue() /
        10.)), random);
    jButton1.setText("Играем");
    GameThread = new Thread(this);
    GameThread.start();
    if (jCheckBox1.isSelected()) sequencer.start();
    jGamePanel.grabFocus();
  }

  private void PauseGame() {
    if (btPause != 0)
      if (btPause == 2) {
        btPause = 1;
        jButton1.setText("Продолжаем игру");
        if (jCheckBox1.isSelected()) sequencer.start();
      }
      else {
        btPause = 2;
        jButton1.setText("Пауза...");
        if (jCheckBox1.isSelected()) sequencer.stop();
      }
  }

  private void MakeMoveInGlass() {
    if (bEndMove) { //появляется новый блок
      boolean bAddColor = false;
      byte OldPoints = bPointOfChangeLevel;

      if (lgScore >= 1000 & bPointOfChangeLevel == 0) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 2000 & bPointOfChangeLevel == 1) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 3000 & bPointOfChangeLevel == 2) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }

      if (lgScore >= 5000 & bPointOfChangeLevel == 3) {
        int value = ( (jSliderSpeed.getValue() - 30) < 10 ? 10 :
                     jSliderSpeed.getValue() - 30);
        jSliderSpeed.setValue(value);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bAddColor = true;
        bPointOfChangeLevel++;
      }

      if (lgScore >= 6000 & bPointOfChangeLevel == 4) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 7000 & bPointOfChangeLevel == 5) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 8000 & bPointOfChangeLevel == 6) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 9000 & bPointOfChangeLevel == 7) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 10000 & bPointOfChangeLevel == 8) {
        int value = ( (jSliderSpeed.getValue() - 30) < 10 ? 10 :
                     jSliderSpeed.getValue() - 30);
        jSliderSpeed.setValue(value);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bAddColor = true;
        bPointOfChangeLevel++;
      }

      if (lgScore >= 11000 & bPointOfChangeLevel == 9) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 12000 & bPointOfChangeLevel == 10) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 13000 & bPointOfChangeLevel == 11) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 14000 & bPointOfChangeLevel == 12) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 150000 & bPointOfChangeLevel == 13) {
        int value = ( (jSliderSpeed.getValue() - 30) < 10 ? 10 :
                     jSliderSpeed.getValue() - 30);
        jSliderSpeed.setValue(value);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bAddColor = true;
        bPointOfChangeLevel++;
      }
      if (lgScore >= 16000 & bPointOfChangeLevel == 14) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 17000 & bPointOfChangeLevel == 15) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 18000 & bPointOfChangeLevel == 16) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 19000 & bPointOfChangeLevel == 17) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 20000 & bPointOfChangeLevel == 18) {
        int value = ( (jSliderSpeed.getValue() - 30) < 10 ? 10 :
                     jSliderSpeed.getValue() - 30);
        jSliderSpeed.setValue(value);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bAddColor = true;
        bPointOfChangeLevel++;
      }
      if (lgScore >= 22000 & bPointOfChangeLevel == 19) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 24000 & bPointOfChangeLevel == 20) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 26000 & bPointOfChangeLevel == 21) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 28000 & bPointOfChangeLevel == 22) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 30000 & bPointOfChangeLevel == 23) {
        int value = ( (jSliderSpeed.getValue() - 30) < 10 ? 10 :
                     jSliderSpeed.getValue() - 30);
        jSliderSpeed.setValue(value);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bAddColor = true;
        bPointOfChangeLevel++;
      }
      if (lgScore >= 32000 & bPointOfChangeLevel == 24) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 34000 & bPointOfChangeLevel == 25) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 36000 & bPointOfChangeLevel == 26) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 38000 & bPointOfChangeLevel == 27) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 40000 & bPointOfChangeLevel == 28) {
        int value = ( (jSliderSpeed.getValue() - 30) < 10 ? 10 :
                     jSliderSpeed.getValue() - 30);
        jSliderSpeed.setValue(value);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bAddColor = true;
        bPointOfChangeLevel++;
      }
      if (lgScore >= 42000 & bPointOfChangeLevel == 29) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 44000 & bPointOfChangeLevel == 30) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 46000 & bPointOfChangeLevel == 31) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 48000 & bPointOfChangeLevel == 32) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 50000 & bPointOfChangeLevel == 33) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 52000 & bPointOfChangeLevel == 34) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 53000 & bPointOfChangeLevel == 35) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (lgScore >= 54000 & bPointOfChangeLevel == 36) {
        jSliderSpeed.setValue(jSliderSpeed.getValue() + 10);
        jLabel2.setText(String.valueOf(jSliderSpeed.getValue()));
        bPointOfChangeLevel++;
      }
      if (OldPoints > bPointOfChangeLevel)
        PlaySample("nextlevel.wav");
      if (OldPoints < bPointOfChangeLevel)
        PlaySample("nextspeed.wav");
      if (bAddColor) {
        if (jSliderLevel.getValue() < 90) {
          jSliderLevel.setValue(jSliderLevel.getValue() + 10);
          jSliderLevel_move(); //получаем картинку нового левела, и обрабатываем её.
        }
      }
    }
  }

  private void Avalanche() {
    byte i, j, aspect, standartaspect;

    for (i = 34; i > 5; i--) { //проверили по горизонтали
      CoordinateVectorAvalanche.clear();
      for (j = 0; j < 16; j++) {
        aspect = Glass[i][j];
        if ( (aspect <= 0) | (aspect == 11)) { //если пустота, или камень - начинаем поиск сначала
          CoordinateVectorAvalanche.clear();
          continue;
        }
        AvalancheMassive standart = new AvalancheMassive(j, i, aspect);
        CoordinateVectorAvalanche.add(standart);
        standartaspect = (aspect > 11 ? -1 : aspect);
        for (byte k = (byte) (j + 1); k < 16; k++) {
          aspect = Glass[i][k];
          if (standartaspect < 0 & ( (aspect > 0) & (aspect < 10)))
              standartaspect = aspect; //если последовательность начиналась с бонуса, это тоже надо учесть
          if (aspect == standartaspect | aspect > 11 | aspect < 0) { //бонусные блоки, или такой же цвет
            CoordinateVectorAvalanche.add(new AvalancheMassive(k, i, aspect));
          }
          else {
            if (CoordinateVectorAvalanche.size() >= BlockLimit) {
              btKilling = 1; //по горизонтали
              return;
            }
            else
              CoordinateVectorAvalanche.clear();
              //j=(byte)(k-1);
            break;
          }
        }
        if (CoordinateVectorAvalanche.size() >= BlockLimit) {
          btKilling = 1; //по горизонтали
          return;
        }
        else {
          CoordinateVectorAvalanche.clear();
        }
      }
      if (CoordinateVectorAvalanche.size() >= BlockLimit) {
        btKilling = 1; //по горизонтали
        return;
      }
      else {
        CoordinateVectorAvalanche.clear();
      }
    }

    for (j = 0; j < 16; j++) { //проверили по вертикали
      CoordinateVectorAvalanche.clear();
      for (i = 34; i > 5; i--) {
        aspect = Glass[i][j];
        if ( (aspect <= 0) | (aspect == 11)) { //если пустота, или камень - начинаем поиск сначала
          CoordinateVectorAvalanche.clear();
          continue;
        }
        AvalancheMassive standart = new AvalancheMassive(j, i, aspect);
        CoordinateVectorAvalanche.add(standart);
        standartaspect = (aspect > 11 ? -1 : aspect);

        for (byte k = (byte) (i - 1); k > 5; k--) {
          aspect = Glass[k][j];
          if (standartaspect < 0 & ( (aspect > 0) & (aspect < 10)))
              standartaspect = aspect; //если последовательность начиналась с бонуса, это тоже надо учесть
          if (aspect == standartaspect | aspect > 11 | aspect < 0) { //бонусные блоки, или такой же цвет
            CoordinateVectorAvalanche.add(new AvalancheMassive(j, k, aspect));
          }
          else {
            if (CoordinateVectorAvalanche.size() >= BlockLimit) {
              btKilling = 2; //по вертикали
              return;
            }
            else
              CoordinateVectorAvalanche.clear();
            break;
          }
        }
        if (CoordinateVectorAvalanche.size() >= BlockLimit) {
          btKilling = 2; //по вертикали
          return;
        }
        else {
          CoordinateVectorAvalanche.clear();
        }
      }
      if (CoordinateVectorAvalanche.size() >= BlockLimit) {
        btKilling = 2; //по вертикали
        return;
      }
      else {
        CoordinateVectorAvalanche.clear();
      }
    }

  }

  private boolean MoveDown() {
    boolean result = true; //можем ли спуститься ещё ниже

    bIsChange = true;

    Vector CoordinateVector = new Vector();
    if (btGameBlock != null)
      CoordinateVector = btGameBlock.getCoordinateVector(); //CoordinateVector);

    for (int i = 0; i < CoordinateVector.size(); i++) {
      AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
      byte x = (byte) p.getX();
      byte y = (byte) p.getY();
      byte aspect = p.getAspect();
      if (y == 34) { //кубик упал на дно, последняя линия
        result = false;
        bEndMove = true; //блок приземлился
        break;
      }
      boolean bPointIsBusy = false; //здесь проверим точку, куда падает блок (внизу. боковые проверим при движении блока вбок, и решим, может ли он падать дальше, или пора вызывать лавину свёрток)
      for (int j = 0; j < CoordinateVector.size(); j++) { //Проверим, не будет ли занимать данный кубик место кубика из падающей фигуры. В этом случае, он не будет тоже нам интересен.
        AspectPoint newP = (AspectPoint) CoordinateVector.get(j); //это координаты продвигающейся вниз ячейки
        if (i != j) {
          if ( (x == newP.getX()) && ( (y + 1) == newP.getY())) {
            bPointIsBusy = true;
            break;
          } //т.е. нашли такую точку
        }
      }
      if (bPointIsBusy)continue;
      if (Glass[y + 1][x] != 0) { //значит, где-то приземлились
        result = false;
        bEndMove = true; //блок приземлился
        break;
      }
    }
    bIsChange = false;

    return result;
  }

  private void TheGame() {
    if (btPause == 2)return; //Это - Паузааааа!

    MakeMoveInGlass();
    if (bEndMove) { //блок ударился о дно стакана.Нужно генерировать новый блок в память, а из памяти забирать готовый.
      //запускаем новый блок в свободное падение
      btGameBlock = btTurnLineObject; //этот блок падает в стакан
      bNewBlock = true;
      bLineCounter = 0;
      bRowCounter = 0;
      btTurnLineObject = new BlockClass( (byte) btLevelBlock,
                                        (byte) (Math.
                                                round(jSliderLevel.getValue() /
          10.)), random);
      bEndMove = false;
      bSpaceDown = false;

      for (byte i = 0; i < 5; i++) //вносим блок в пространство над стаканом.
        for (byte j = 0; j < 5; j++) {
          if (Glass[i][j + 6] == 0) {
            Glass[i][j + 6] = btGameBlock.getBlock(i, j);
          }
          else { //Игра закончена
            btPause = 0;
            jButton1.setText("Игра закончена");
            if (jCheckBox1.isSelected()) sequencer.stop();
            if (lgScore > 0) InsertNewHightScore();
            GameThread.stop();
            return;
          }
        }
      bActionContinued = MoveDown(); //можем ли мы спуститься вниз на клетку. Если не можем с момента создания блока - конец игре.
    }
    else { //блок двигается вниз
      if (bIsChange)return;
      Vector CoordinateVector = btGameBlock.getCoordinateVector();
      if (!CoordinateVector.isEmpty()) {
        if (MoveDown()) { //обработаем движение вниз
          for (int i = 0; i < CoordinateVector.size(); i++) {
            AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
            Glass[ (byte) p.getY()][ (byte) p.getX()] = 0; //затёрли старое расположение блока...
          }
          for (int i = 0; i < CoordinateVector.size(); i++) {
            AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
            Glass[ (byte) (p.getY() + 1)][ (byte) p.getX()] = p.getAspect(); //...и записали на новок место
          }
          btGameBlock.setMoveDown();
          bLineCounter++;
        }
      }
    }
  }

  void jButton1_actionPerformed(ActionEvent e) {
    jGamePanel.grabFocus();
    switch (btPause) {
      case 0: {
        StartGame();
        break;
      }
      case 1: {
        PauseGame();
        break;
      }
      case 2: {
        btPause = 1;
        jButton1.setText("Играем");
        sequencer.start();
        break;
      }
    }
    jGamePanel.grabFocus();
  }

  void jPanel4_focusLost(FocusEvent e) {
    PauseGame();
    paint(this.getGraphics());
  }

  void jButton4_actionPerformed(ActionEvent e) {
    if (btPause != 0) PauseGame();

    DialogInstruction f = new DialogInstruction(this, "Инструкция к игре", true,
                                                0, new Vector());
    if (btPause != 0) {
      btPause = 1;
      jButton1.setText("Играем");
    }
    jGamePanel.grabFocus();
  }

  void jButton2_actionPerformed(ActionEvent e) { //Button "Результаты"
    if (btPause != 0) PauseGame();

    DialogInstruction f = new DialogInstruction(this, "Топ результатов", true,
                                                1, ReadFromFile());
    if (btPause != 0) {
      btPause = 1;
      jButton1.setText("Играем");
    }
    jGamePanel.grabFocus();
  }

  void jSliderSpeed_mouseClicked(MouseEvent e) {
    jLabel2.setText(Integer.toString(jSliderSpeed.getValue()));
    jGamePanel.grabFocus();
  }

  void jSliderSpeed_mouseDragged(MouseEvent e) {
    jLabel2.setText(Integer.toString(jSliderSpeed.getValue()));
    jGamePanel.grabFocus();
  }

  private void jSliderLevel_move() {
    int indexPicture = (int) Math.round( (byte) jSliderLevel.getValue() / 10.);
    iLevelImage = getToolkit().getImage("jpg/kubic" + Integer.toString(indexPicture) +
                                        ".jpg");
    bChangeLevel = true;
    this.repaint();
    jGamePanel.grabFocus();
  }

  void jSliderLevel_mouseClicked(MouseEvent e) {
    jSliderLevel_move();

  }

  void jSliderLevel_mouseDragged(MouseEvent e) {
    jSliderLevel_move();
  }

  private boolean MoveSide(byte Side) {
    boolean result = true; //можем ли спуститься ещё ниже
    Vector CoordinateVector = btGameBlock.getCoordinateVector();
    bIsChange = true;

    for (int i = 0; i < CoordinateVector.size(); i++) {
      AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
      byte x = (byte) p.getX();
      byte y = (byte) p.getY();
      byte aspect = p.getAspect();
      if ( ( (x + Side) < 0) || ( (x + Side) > 15)) { //кубик на 1 или последней колонке
        result = false;
        break;
      }
      boolean bPointIsBusy = false; //здесь проверим точку, куда падает блок (внизу. боковые проверим при движении блока вбок, и решим, может ли он падать дальше, или пора вызывать лавину свёрток)
      for (int j = 0; j < CoordinateVector.size(); j++) { //Проверим, не будет ли занимать данный кубик место кубика из падающей фигуры. В этом случае, он не будет тоже нам интересен.
        AspectPoint newP = (AspectPoint) CoordinateVector.get(j); //это координаты продвигающейся в сторону ячейки
        if (i != j) {
          if ( ( (x + Side) == newP.getX()) && (y == newP.getY())) {
            bPointIsBusy = true;
            break;
          } //т.е. нашли такую точку
        }
      }
      if (bPointIsBusy)continue;
      if (Glass[y][ (byte) (x + Side)] != 0) { //значит, где-то чего-то коснулись
        result = false;
        break;
      }
    }
    bIsChange = false;

    return result;
  }

  private void MoveBlock(int Movement) {
    bIsChange = true;
    Vector CoordinateVector = btGameBlock.getCoordinateVector();

    switch (Movement) {
      case 1: { //мена цвета по вектору
        btGameBlock.setOrietationUp();
        break;
      }
      case 2: { //движение влево
        if (MoveSide( (byte) - 1)) {
          for (int i = 0; i < CoordinateVector.size(); i++) {
            AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
            Glass[ (byte) p.getY()][ (byte) p.getX()] = 0; //затёрли старое расположение блока...
          }
          for (int i = 0; i < CoordinateVector.size(); i++) {
            AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
            Glass[ (byte) p.getY()][ (byte) (p.getX() - 1)] = p.getAspect(); //...и записали на новок место
          }
          btGameBlock.setMoveSize( -1);
          bRowCounter--;
        }
        break;
      }
      case 3: { //движение вправо
        if (MoveSide( (byte) 1)) {
          for (int i = 0; i < CoordinateVector.size(); i++) {
            AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
            Glass[ (byte) p.getY()][ (byte) p.getX()] = 0; //затёрли старое расположение блока...
          }
          for (int i = 0; i < CoordinateVector.size(); i++) {
            AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
            Glass[ (byte) p.getY()][ (byte) (p.getX() + 1)] = p.getAspect(); //...и записали на новок место
          }
          btGameBlock.setMoveSize(1);
          bRowCounter++;
        }
        break;
      }
      case 4: { //вращаем: Пробуем, если на новых координатах нет чужих точек - то поворачиваем.
        BlockClass btGameBlockTemp = btGameBlock;
        btGameBlockTemp.setOrietationNext();
        Vector CoordinateVectorTemp = btGameBlockTemp.getCoordinateVector();
        boolean blItsPossible = true; //проверям, не выскочит ли полученный поворотом блок за пределы стакана, и не заденет ли он другие фигуры
        for (int i = 0; i < CoordinateVectorTemp.size(); i++) {
          AspectPoint pTemp = (AspectPoint) (CoordinateVectorTemp.get(i));
          byte x = pTemp.getX();
          byte y = pTemp.getY();
          boolean bBlockIsSelf = false;
          for (byte j = 0; j < CoordinateVector.size(); j++) {
            AspectPoint p = (AspectPoint) (CoordinateVector.get(j));
            if ( (p.getX() == x) & (p.getY() == y)) { //эта ячейка свободна, но её занимает сам падающий блок
              bBlockIsSelf = true;
              break;
            }
          }
          if (!bBlockIsSelf) {
            byte aspect = Glass[y][x];
            if (aspect != 0) {
              blItsPossible = false;
              break;
            }
          }
        }

        if (blItsPossible) {
          for (int i = 0; i < CoordinateVector.size(); i++) {
            AspectPoint p = (AspectPoint) CoordinateVector.get(i); //это координаты продвигающейся вниз ячейки
            Glass[ (byte) p.getY()][ (byte) p.getX()] = 0; //затёрли старое расположение блока...
          }
          for (int i = 0; i < CoordinateVectorTemp.size(); i++) {
            AspectPoint p = (AspectPoint) CoordinateVectorTemp.get(i); //это координаты продвигающейся вниз ячейки
            Glass[ (byte) p.getY()][ (byte) (p.getX())] = p.getAspect(); //...и записали на новок место
          }
        }
        btGameBlock = btGameBlockTemp;
      }
    }
    bIsChange = false;
    MyPaint(getGraphics());
    try {
      GameThread.sleep(80); //падаем быстро
    }
    catch (InterruptedException ie) {}
  }

  void PressingKey(KeyEvent e) {

    int sCode = e.getKeyCode();
    if (!isAvalanche) {
      if (sCode == 38 || sCode == 87 || sCode == 104) MoveBlock(1); //сменим цвет по вектору
      else
      if (sCode == 37 || sCode == 65 || sCode == 100) MoveBlock(2); //движение влево
      else
      if (sCode == 39 || sCode == 68 || sCode == 102) MoveBlock(3); //движение вправо
      else
      if (sCode == 40 || sCode == 83 || sCode == 98 || sCode == 101) MoveBlock(
          4); //вращаем блок
      else
      if (sCode == 32) bSpaceDown = true; //движение вниз, ускорение
    }
    if (sCode == 19) PauseGame();
  }

  void this_propertyChange(PropertyChangeEvent e) {
    repaint();
  }

  void jCheckBox1_actionPerformed(ActionEvent e) {
    if (btPause == 1) {
      if (jCheckBox1.isSelected()) sequencer.start();
      else sequencer.stop();
    }
    jGamePanel.grabFocus();
  }

  void jCheckBox2_actionPerformed(ActionEvent e) {
    jGamePanel.grabFocus();
  }
}

 class Frame1_jGamePanel_keyAdapter extends java.awt.event.KeyAdapter {
 Frame1 adaptee;

 Frame1_jGamePanel_keyAdapter(Frame1 adaptee) {
   this.adaptee = adaptee;
 }
 public void keyPressed(KeyEvent e) {
   adaptee.PressingKey(e);
 }

}





