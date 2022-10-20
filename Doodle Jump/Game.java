//MORMAL INITILISATION FOR GRAPHICS AND OTHER UTILITY
import javax.swing.*;
import java.awt.*;
import java.awt.event.*; 
import java.util.Random;

//IMAGES BUFFER
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


//MUSICS AND EXCEPTIONS
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

class FloorPos{
    int x,y;
    boolean jumper=false,jetpack=false;
    int coin=0;

}


public class Game extends JPanel implements Runnable,KeyListener{
    final int width = 500;
    final  int height = 733;

    int gfloorx = 56,gfloory=16,
        jumperx=40,jumpery=40,
        jetpackx=45,jetpacky=45,
        coinx=16,coiny=16;

    int bulletx,bullety,bulletCnt=0;

    int scores=0,fontW=0,fontH=0;float heightScore,prev,rocketStartHeight;

    boolean isRun,isover;
    Thread gameThread;
    

    BufferedImage view,background,floor,doodleL,doodleR,doodleT,jumper,jetpack,bullet,coin,doodlerocket;

    FloorPos[] floorPos;
    int floorCnt=0,floorOffset=65;

    int isjump=0,jumperPos=20;
    int isjetpack=0,jetpackPos=100;
    

    int x=100, y=100 ,h=90;
    float dy=0;
    boolean right,left,doodleRocket;
    int faceDir=0;

    Game(){
        setPreferredSize(new Dimension(width,height));
        addKeyListener(this);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Game");
        f.setSize(500,733); 
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Game());
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);

        // Frame sel = new UiPicker("Selector");
        // sel.setSize(400,200); 
        
        // sel.setLocationRelativeTo(null);
        // sel.setVisible(true);

    }

    //-------------------START AN INFINITE THREAD TILL GAME ENDS----------------------
    public void addNotify(){
        super.addNotify();
        if(gameThread == null){
            gameThread = new Thread(this);
            isRun = true;
            gameThread.start();
        }
    }

    //----------------INTILISATION OF THE GAME WITH FLOORS CHARACTERS ETC--------------------
    public void start(){
        try {
            view  = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            background = ImageIO.read(getClass().getResource("images/background.png"));
            floor= ImageIO.read(getClass().getResource("images/floorg.png"));
            doodleL = ImageIO.read(getClass().getResource("images/doodleL.png"));
            doodleR = ImageIO.read(getClass().getResource("images/doodleR.png"));
            doodleT = ImageIO.read(getClass().getResource("images/doodleT.png"));
            jetpack = ImageIO.read(getClass().getResource("images/jetpack.png"));
            bullet = ImageIO.read(getClass().getResource("images/bullet.png"));
            coin = ImageIO.read(getClass().getResource("images/coin.png"));
            doodlerocket = ImageIO.read(getClass().getResource("images/rocket.png"));
            jumper = ImageIO.read(getClass().getResource("images/jumper.png"));
            floorPos = new FloorPos[20];
            floorCnt=15;
            for(int i=0;i<floorCnt;i++){
                floorPos[i] = new FloorPos();
                floorPos[i].x = new Random().nextInt(width);
                floorPos[i].y = new Random().nextInt(height);
                if(floorPos[i].x<15) floorPos[i].x=15;
                if(floorPos[i].y<15) floorPos[i].y=15;
                if(floorPos[i].x>width-gfloorx) floorPos[i].x=width-10;
                isjump+=1;

            }

            // SORTING ACCORDING TO HIEGHT TO TAKE CARE DOODLE COULD JUMP
            // ATLEAST TO UP NEXT PLAFORM
            FloorPos temp;
            for(int i=0;i<floorCnt;i++){
                for(int j=i+1;j<floorCnt;j++){
                    if(floorPos[j].y<floorPos[i].y){
                        temp = floorPos[j];
                        floorPos[j]=floorPos[i];
                        floorPos[i]=temp;
                    }
                }
            }
            for(int i=1;i<floorCnt;i++){
                if(floorPos[i-1].y+floorOffset<floorPos[i].y){
                    floorPos[i].y=floorPos[i-1].y+floorOffset;
                    
                }
            }
        } catch (Exception e) {
            System.out.print(e.getStackTrace());
        }
    }

    //-----------------MUSIC AND SOUNDS ON CRASH ANF GAME STARTS-----------------------
    public void voice(String soundName){  
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile())) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            
            e.printStackTrace();
        }
    }

    //-----------------UPDATES IN PAINT-----------------------------------
    public void update(){

        //MOVEMENT LEFT AND RIGHT OF DOODLE
        if(right){
            x+=5;
        }
        else if(left){
            x-=5;
        }

        //UPSIDE CONTROL OF DOODLES FOR INFINITE UPSIDE MOVE
        dy+=0.2;
        if(dy<0){
            heightScore+=0.2;
        }
        else{
            heightScore-=0.2;
        }
        y+=dy;
        if(y>height){
            voice("sounds/fall.wav");
            floorPos=null;
            floorCnt=0;
            y=0;
            dy=20;
            isover=true;
        }
        System.out.println(y);

        //BULLET CONTROL STILL IN PROCESS
        bulletx+=2;
        bullety-=2;
        bulletCnt+=1;

        // if(y<=0){
        //     dy=10;
        // }
        
        if(doodleRocket && rocketStartHeight+72<=heightScore){
            doodleRocket=false;

        }

        //INFINITE SCROLL UPSIDE
        if(y<h){
            for(int i=0;i<floorCnt;i++){
                y=h;
                floorPos[i].y=floorPos[i].y-(int)dy;
                if(floorPos[i].y>height){
                    if(isjump==jumperPos){floorPos[i].jumper=true;isjump=0;}
                    else{floorPos[i].jumper=false;}

                    if(isjetpack==jetpackPos){floorPos[i].jetpack=true;isjetpack=0;}
                    else{floorPos[i].jetpack=false;}

                    
                    int c= (int) (Math.random()*100);
                    if(c%7==0 && floorPos[i].jumper==false && floorPos[i].jetpack==false){
                        floorPos[i].coin=2;
                    }
                    else if(c%2==0 && floorPos[i].jumper==false && floorPos[i].jetpack==false){
                        floorPos[i].coin=1;
                    }
                     

                    floorPos[i].y=0;
                    floorPos[i].x = new Random().nextInt(width-gfloorx-50);
                    isjump+=1;
                    isjetpack+=1;
                }
            }
        }

        //SORTING ACCORDING TO HIEGHT TO TAKE CARE DOODLE COULD JUMP
        // ATLEAST TO UP NEXT PLAFORM
        // FloorPos temp;
        // for(int i=0;i<floorCnt;i++){
        //     for(int j=i+1;j<floorCnt;j++){
        //         if(floorPos[j].y<floorPos[i].y){
        //             temp = floorPos[j];
        //             floorPos[j]=floorPos[i];
        //             floorPos[i]=temp;
        //         }
        //     }
        // }
        // for(int i=1;i<floorCnt;i++){
        //         if(floorPos[i-1].y+floorOffset<floorPos[i].y){
        //             floorPos[i].y=floorPos[i-1].y+floorOffset;
                    
        //         }
        //     }

        //ADDING A JUMPER AND ROCKET TO SPEEDUP 
        for(int i=0;i<floorCnt;i++){
            if(x+20<=floorPos[i].x+gfloorx+7 && x+50>=floorPos[i].x && y+54<=floorPos[i].y+gfloory && y+54>=floorPos[i].y && dy>0){
                if(floorPos[i].jumper){
                    
                    dy=-30;
                    
                    voice("sounds/jumper.wav");
                    jumperPos=40+(int)(Math.random()*50);
                }
                else if(floorPos[i].jetpack){
                    voice("sounds/rocket.wav");
                    dy=-80;
                    doodleRocket=true;
                    rocketStartHeight=heightScore;
                    jetpackPos=100+(int)(Math.random()*50);
                }
                else{
                    voice("sounds/sound.wav");
                    dy=-10;
                }
                // if(floorPos[i].coin==1){
                //     scores+=1;
                //     voice("sounds/coin.wav");
                //     floorPos[i].coin=0;
                // }
                // else if(floorPos[i].coin==2){
                //     scores+=2;
                //     voice("sounds/coin.wav");
                //     floorPos[i].coin=0;
                // }
                
            }
        }

        for(int i=0;i<floorCnt;i++){
            if(x+20<=floorPos[i].x+gfloorx+7 && x+50>=floorPos[i].x && y+54<=floorPos[i].y+gfloory && y+54>=floorPos[i].y){
                
                if(floorPos[i].coin==1){
                    scores+=1;
                    voice("sounds/coin.wav");
                    floorPos[i].coin=0;
                }
                else if(floorPos[i].coin==2){
                    scores+=2;
                    voice("sounds/coin.wav");
                    floorPos[i].coin=0;
                }
                
            }
        }
    }

    //-------------------------ALL GRAPHICS OF THE GAME-------------------------------
    public void draw(){
        
        Graphics2D g2 =(Graphics2D) view.getGraphics();
        g2.drawImage(background, 0, 0, width, height,null);

        //LEFT DOODLE
        if(doodleRocket){
            g2.drawImage(doodlerocket, x, y, doodlerocket.getWidth(),doodlerocket.getHeight(),null);
        }
        else if(faceDir==0){
            g2.drawImage(doodleL, x, y, doodleL.getWidth(),doodleL.getHeight(),null);
        } 

        // RIGHT DOODLE
        else if(faceDir==1){
            g2.drawImage(doodleR, x, y, doodleR.getWidth(),doodleR.getHeight(),null);
        } 


        //UPSIDE DOODLE FOR SHOOT
        else{
            g2.drawImage(doodleT, x, y, doodleT.getWidth(),doodleR.getHeight(),null);
            g2.drawImage(bullet, bulletx, bullety, bullet.getWidth(),bullet.getHeight(),null);
        }
        

        if(prev<heightScore){
            
            prev=heightScore;
        }   
        Font f = new Font("Arial",Font.BOLD,20);
        g2.setFont(f);
        g2.setColor(Color.BLACK);
        String s = Integer.toString((int)prev);
        FontMetrics fm = g2.getFontMetrics(f);
        fontW = fm.stringWidth(s);
        fontH = fm.getAscent();
        g2.drawString(s, fontW-5, fontH+15);
        

        s = Integer.toString(scores);
        fontW = fm.stringWidth(s);
        fontH = fm.getAscent();
        g2.drawImage(coin, width-fontW-60, fontH, coinx,coiny,null);
        g2.drawString(s, width-fontW-30, fontH+15);


        //FLOORS & JUMPERS & ROCKETS
        for(int i=0;i<floorCnt;i++){
            g2.drawImage(floor, floorPos[i].x, floorPos[i].y, gfloorx,gfloory,null);
            
            if(floorPos[i].jetpack){
                g2.drawImage(jetpack, floorPos[i].x+(gfloorx-jetpackx)/2, floorPos[i].y-35, jetpackx,jetpacky,null);
            }
            else if(floorPos[i].jumper){
                g2.drawImage(jumper, floorPos[i].x+(gfloorx-jumperx)/2, floorPos[i].y-20, jumperx,jumpery,null);
            }
            else if(floorPos[i].coin==1){
                g2.drawImage(coin, floorPos[i].x+(gfloorx-coinx)/2, floorPos[i].y-20, coinx,coiny,null);
            }
            else if(floorPos[i].coin==2){
                g2.drawImage(coin, floorPos[i].x+(gfloorx-coinx)/2-7, floorPos[i].y-20, coinx,coiny,null);
                g2.drawImage(coin, floorPos[i].x+(gfloorx-coinx)/2+7, floorPos[i].y-20, coinx,coiny,null);
            }
        }

        if(isover){
            f = new Font("Arial",Font.ITALIC,40);
            g2.setFont(f);
            fm = g2.getFontMetrics(f);
            fontW = fm.stringWidth("GAME OVER");
            fontH = fm.getAscent();
            g2.drawString("GAME OVER", (width-fontW)/2, (height-fontH)/2);

            f = new Font("Arial",Font.ITALIC,20);
            g2.setFont(f);
            fm = g2.getFontMetrics(f);
            s= Integer.toString(scores);
            fontW = fm.stringWidth("SCORE: "+s);
            fontH = fm.getAscent();
            g2.setColor(Color.YELLOW);
            g2.drawString("SCORE: "+s, (width-fontW)/2, (height-fontH+50)/2);
        }
        //-------------------//
        Graphics g = getGraphics();
        g.drawImage(view,0,0,width,height,null);
        g.dispose();
    }

    //-----------------------STARTING AN INIFINTE THREAD TILL GAME ENDS-------------------
    @Override
    public void run() {
        try {
            requestFocus();
            start();
            while(isRun){
                update();
                draw();
                // gameThread.sleep(1000/60);
                Thread.sleep(1000/60);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    //-----------------------KEYBOARD LISTENERS FOR MOVEMENTS -------------------
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            right=true;
            faceDir=1;

        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            left=true;
            faceDir=0;
        }
        if(e.getKeyCode() == KeyEvent.VK_UP){
            faceDir=2;
            bulletx=x;
            bullety=y+16;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            right=false;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            left=false;
        }
        if(e.getKeyCode() == KeyEvent.VK_UP){
          faceDir=0;
          
        }
        
    }
}