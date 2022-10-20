import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class BrickGame extends JPanel implements KeyListener,ActionListener {
    private boolean play = false;
    private int score = 0;
    private int brickCount =31;
    private Timer timer;
    private int delay = 8;
    Random rand = new Random();
    private int plate=310,ballX=rand.nextInt(700),ballY=320,ballShiftY=-3,ballShiftX=-4;
    // private PlayGround brick;
    public int brick[][];
    public int brickWidth,brickHeight,row=4,col=11;
    public BrickGame(){
        brick=new int[row][col];
        
        for(int i=0;i<brick.length;i++){
            for(int j=0;j<brick[0].length;j++){
                brick[i][j]=1;
            }
        }
        brickWidth=640/col;
        brickHeight=250/row;
        
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay,this);
        timer.start();
    }
    public void paint(Graphics g){
        g.setColor(new Color(46, 59, 79));
        g.fillRect(1, 1,892, 792);

        

        draw((Graphics2D)g);
        Graphics2D g2d = (Graphics2D) g;

        g.setFont(new Font("serif",Font.BOLD,30));
        g.setColor(new Color(255,255,255));
        g.drawString("Scores"+" "+score, 10, 30);
        
        g.setColor(new Color(24, 28, 33));
        g.fillRect(0,0,3,792);
        g.fillRect(0,0,892,3);
        g.fillRect(791,0,3,792);

        g.setColor(new Color(247, 249, 250));
        g.fillRect(plate, 650, 100, 10);
        g2d.drawRoundRect(plate, 650, 100, 10, 10,10);
        g.setColor(new Color(24, 28, 33));
        g.fillOval(ballX, ballY,25,25);

        g.dispose();
    }
    public void draw(Graphics2D g){
        for(int i=0;i<brick.length;i++){
            for(int j=0;j<brick[0].length;j++){
                if(brick[i][j]>0){
                    g.setColor(new Color(89, 70, 29));
                    g.fillRect(j*brickWidth+80, i*brickHeight+50, brickWidth, brickHeight);

                    g.setStroke(new BasicStroke(3));
                    g.setColor(new Color(48, 66, 94));
                    g.drawRect(j*brickWidth+80, i*brickHeight+50, brickWidth, brickHeight);
                }
            }
        }
    }
    public void setBrickVal(int value,int row,int col){
        brick[row][col]=value;
    }
    public void voice(){
        String soundName = "sound.wav";    
try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile())) {
    Clip clip = AudioSystem.getClip();
    clip.open(audioInputStream);
    clip.start();
} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if(play){
            if(new Rectangle(ballX,ballY,20,20).intersects(new Rectangle(plate,650,100,8))){
                ballShiftY=-ballShiftY;
                
            }
            A: for(int i=0;i<brick.length;i++){
                for(int j=0;j<brick[0].length;j++){
                    if(brick[i][j]>0){
                        int brickX = j*brickWidth+80;
                        int brickY = i*brickHeight + 50;
                        Rectangle rect = new Rectangle(brickX,brickY,brickWidth,brickHeight);
                        Rectangle ballRect = new Rectangle(ballX,ballY,20,20);
                        Rectangle brickRect = rect;
                        if(ballRect.intersects(brickRect)){
                            setBrickVal(0,i,j);
                            brickCount--;
                            score+=10;
                            voice();
                            if(ballX<=brickRect.x || ballX+1>=brickRect.x+brickRect.width){
                                ballShiftX=-ballShiftX;
                            }
                            else{
                                ballShiftY=-ballShiftY;
                            }
                            
                            break A;
                        }
                    }
                }
            }
            ballX+=ballShiftX;
            ballY+=ballShiftY;
            if(ballX<0){
                ballShiftX=-ballShiftX;
            }
            if(ballY<0){
                ballShiftY=-ballShiftY;
            }
            if(ballX>770){
                ballShiftX=-ballShiftX;
            }
        }
        
        repaint();
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(keyCode == KeyEvent.VK_RIGHT){
            if(plate>=700){
                plate=700;
            }
            else{
                play=true;
                plate+=40;
            }
        }
        
        if(keyCode == KeyEvent.VK_LEFT){
            if(plate<10){
                plate=10;
            }
            else{
                play=true;
                plate-=40;
            }
        }
    }
    

    @Override
    public void keyReleased(KeyEvent e) {}
    public static void main(String[] args){
        JFrame obj = new JFrame();
        BrickGame gameScreen = new BrickGame();
        obj.setBounds(10,10, 800,700);
        obj.setTitle("Brick");
   
        obj.setResizable(false);
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gameScreen);
    }
    
}


