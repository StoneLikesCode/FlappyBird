import javax.swing.*;


//main
public class Main {
    public static void main(String args[]){
        int borderHeight = 640;
        int borderWidth = 360; 

        JFrame frame = new JFrame("Flappy Bird");
       
        frame.setVisible(true);
        frame.setSize(borderWidth, borderHeight);
        frame.setLocationRelativeTo(null);
     
        FlappyBird flappyBird = new FlappyBird();

        frame.add(flappyBird);
        frame.pack();
        frame.setResizable(false);
        flappyBird.requestFocus();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
}