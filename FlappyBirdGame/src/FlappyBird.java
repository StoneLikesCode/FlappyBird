import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{ 
    int borderHeight = 640;
    int borderWidth = 360;

    //Images
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image botPipeImage;

    //Bird
    int birdX = borderWidth/8;
    int birdY = borderWidth/2;
    int birdWidth = 34;
    int birdHeight = 24;

    //asd
    public class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //Pipes
    int pipeX = borderWidth;
    int pipeY = 0;
    int pipeWidth = 64; //scaled by 1/6
    int pipeHeight = 512; //the actual image is six times bigger

    class Pipe{ 
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        Boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic 
    Bird bird;
    int velocityY =  0; 
    int velocityX = -4;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;

    double score = 0;


    FlappyBird(){ 
        setPreferredSize(new Dimension(borderWidth, borderHeight));
        // setBackground(Color.blue);

        //this takes the inputs
        setFocusable(true);
        addKeyListener(this);

         //load images 
         backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
         birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
         topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
         botPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
         
         //bird 
         bird = new Bird(birdImage);
         pipes = new ArrayList<Pipe>();

         //placePipeTimer 
         placePipesTimer = new Timer(1500,new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
         });
         placePipesTimer.start();

         //game timer 
         gameLoop = new Timer(1000/60,this); //1000/60 = 16.6
         gameLoop.start();

        }

        public void placePipes(){ 
            //(0-1) * pipeheight/2 -> (0-256)
            //128
            //0 - 128 - (0-256) --> pipeheight/4 -> 3/4 pipeHeight

            int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
            int openingSpace = borderHeight/4;

            Pipe topPipe = new Pipe(topPipeImage);
            topPipe.y = randomPipeY;
            pipes.add(topPipe);

            Pipe bottomPipe = new Pipe(botPipeImage);
            bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
            pipes.add(bottomPipe);

        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            draw(g);
        }

        public void draw(Graphics g) {
            //background
            g.drawImage(backgroundImage, 0, 0, borderWidth, borderHeight, null);

            //bird
            g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

            //pipes
            for (int i=0; i < pipes.size(); i++){
                Pipe pipe = pipes.get(i);
                g.drawImage(pipe.img, pipe.x, pipe.y, pipeWidth, pipeHeight, null);
            }

            //score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 32));

            if(gameOver){ 
                g.drawString("Game Over! \nScore:" +String.valueOf((int) score), 10, 35);
            }else {
                g.drawString("Score:" +String.valueOf((int) score), 10, 35);
            }
        }

        public void move(){ 
            //bird 
            velocityY += gravity;
            bird.y += velocityY;
            bird.y = Math.max(bird.y,0);


            //pipes 
            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                pipe.x +=velocityX;

                if (collision(bird, pipe)){ 
                    gameOver = true;
                }
                if(!pipe.passed && bird.x > pipe.x + pipe.width){
                    pipe.passed = true;
                    score += 0.5; //two pipes 
                }

            if(bird.y >= borderHeight){ 
                gameOver = true;
            }
        }
    }

        public boolean collision(Bird a, Pipe b){
            return  a.x < b.x + b.width &&  //a's top left corner doesn't reach b's top right corner
                    a.x + a.width > b.x &&  //a's top right corner doesn't reach b's top right corner
                    a.y < b.y + b.height && //a's top left corner doesn't reach b's top right corner
                    a.y + a.height > b.y;   //a's bottom right corner doesn't reach b's top right corner
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
            move();
            repaint();
            if (gameOver){ 
                placePipesTimer.stop();
                gameLoop.stop();
            }

        } 
        
        @Override
        public void keyPressed(KeyEvent e) {
           if (e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;
            if(gameOver){ 
                //restart the game by resetting the conditions
                bird.y = birdY;
                velocityY=0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
           }
        }
       
        @Override
        public void keyTyped(KeyEvent e) {}
        @Override
        public void keyReleased(KeyEvent e) {}

}