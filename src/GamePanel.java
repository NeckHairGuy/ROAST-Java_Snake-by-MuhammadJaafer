import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH=1280;
    static final int SCREEN_HEIGHT=720;
    static final int UNIT_SIZE = 40;
    static final int GAME_UNITS=((SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE);
    static final int MAX_DELAY = 90;
    static int DELAY = 50;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char appleType='N';
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame(){
        //Start Position
        x[0]=UNIT_SIZE;
        y[0]=UNIT_SIZE;
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if(running){
            //Draw Borders
            g.setColor(Color.red);
            g.fillRect(0,0,SCREEN_WIDTH + (UNIT_SIZE*2),UNIT_SIZE);
            g.fillRect(0,0,UNIT_SIZE,SCREEN_HEIGHT+ (UNIT_SIZE*2));
            g.fillRect(SCREEN_WIDTH+UNIT_SIZE,0,UNIT_SIZE,SCREEN_HEIGHT+(UNIT_SIZE*2));
            g.fillRect(0,SCREEN_HEIGHT+UNIT_SIZE,SCREEN_WIDTH+(UNIT_SIZE*2),UNIT_SIZE);

            //Place an apple
            int appleSize = UNIT_SIZE;
            Color appleColor = Color.red;
            if(appleType == 'L'){
                appleSize=UNIT_SIZE*2;
            }
            g.setColor(appleColor);
            g.fillOval(appleX,appleY,appleSize,appleSize);
            //draw the snake
            for(int i = 0;i<bodyParts;i++){
                if(i==0){
                    g.setColor(Color.green);
                    drawSnakeHead(g, direction, x[0], y[0], UNIT_SIZE);
                }else{
                    g.setColor(new Color(45,180,0));
                    g.fillRect(x[i],y[i],UNIT_SIZE,UNIT_SIZE);
                }
            }
            String str = "Score: "+applesEaten;
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free",Font.BOLD,40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(str,(SCREEN_WIDTH-metrics.stringWidth(str))/2,g.getFont().getSize()+UNIT_SIZE);
        }else{
            gameOver(g);
        }
    }
    // Method to draw a snake head with additional details
    private void drawSnakeHead(Graphics g, char direction, int x, int y, int size) {
        g.setColor(new Color(0, 150, 0));  // Green color for the head
        g.fillRect(x, y, size, size);

        // Eyes
        int eyeSize = size / 4;  // Adjust the eye size as needed
        int eyeOffsetX = size / 3;
        int eyeOffsetY = size / 5;

        g.setColor(Color.white);
        g.fillOval(x + eyeOffsetX, y + eyeOffsetY, eyeSize, eyeSize);
        g.fillOval(x + eyeOffsetX * 2, y + eyeOffsetY, eyeSize, eyeSize);

        // Pupils
        int pupilSize = eyeSize / 2;
        int pupilOffsetX = eyeOffsetX + eyeSize / 4;
        int pupilOffsetY = eyeOffsetY + eyeSize / 4;

        g.setColor(Color.black);
        g.fillOval(x + pupilOffsetX, y + pupilOffsetY, pupilSize, pupilSize);
        g.fillOval(x + pupilOffsetX * 2, y + pupilOffsetY, pupilSize, pupilSize);

        // Tongue
        int tongueWidth = size / 3;
        int tongueHeight = size / 4;

        g.setColor(new Color(255, 0, 0));  // Red color for the tongue
        g.fillRoundRect(x + size / 2 - tongueWidth / 2, y + size / 2, tongueWidth, tongueHeight, 5, 5);


    }
    public void newApple(){
        if (random.nextInt(100) < 70) {
            appleType= 'N';  // 70% chance for 'N'
        } else{
            appleType= 'L';  // 30% chance for 'L'
        }
        if(appleType == 'L'){
            appleX = random.nextInt((int)((SCREEN_WIDTH-(UNIT_SIZE*2))/UNIT_SIZE))*UNIT_SIZE+UNIT_SIZE*2;
            appleY = random.nextInt((int)((SCREEN_HEIGHT-(UNIT_SIZE*2))/UNIT_SIZE))*UNIT_SIZE+UNIT_SIZE*2;
        }else{
            appleX = random.nextInt((int)((SCREEN_WIDTH)/UNIT_SIZE))*UNIT_SIZE+UNIT_SIZE;
            appleY = random.nextInt((int)((SCREEN_HEIGHT)/UNIT_SIZE))*UNIT_SIZE+UNIT_SIZE;
        }
    }
    public void move(){
        for(int i = bodyParts;i>0;i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch(direction){
            case 'U':
                y[0]=y[0]-UNIT_SIZE;
                break;
            case 'D':
                y[0]=y[0]+UNIT_SIZE;
                break;
            case 'L':
                x[0]=x[0]-UNIT_SIZE;
                break;
            case 'R':
                x[0]=x[0]+UNIT_SIZE;
                break;
        }
    }
    public void checkApple() {
        if (appleType == 'L') {
            if (checkAdjacentGrids()) {
                bodyParts += 5;
                for(int i=bodyParts-5;i<bodyParts;i++){
                    x[i]=x[bodyParts-5];
                    y[i]=y[bodyParts-5];
                }
                applesEaten += 5;
                if(DELAY<MAX_DELAY){
                    DELAY+=3;
                }
                newApple();
            }
        } else {
            if (x[0] == appleX && y[0] == appleY) {
                bodyParts++;
                applesEaten++;
                if(DELAY<MAX_DELAY){
                    DELAY+=1;
                }
                newApple();
            }
        }
        timer.setDelay(DELAY);
    }

    private boolean checkAdjacentGrids() {
        // Loop through all possible adjacent and diagonal positions
        for (int i = 0; i < x.length; i++) {
            if (isAdjacentGrid(x[i], y[i], appleX, appleY)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdjacentGrid(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) <= UNIT_SIZE && Math.abs(y1 - y2) <= UNIT_SIZE;
    }

    public void checkCollisions(){
        //check if snake collides with itself
        for(int i=bodyParts;i>0;i--){
            if((x[0]==x[i])&&(y[0]==y[i])){
                running= false;
            }
        }
        //check if snake collides with the borders
        if(x[0]<=0){
            running = false;
        }else if(x[0]>SCREEN_WIDTH){
            running = false;
        }else if(y[0]<=0){
            running = false;
        }else if(y[0]>SCREEN_HEIGHT){
            running = false;
        }
        //stop the timer;
        if(!running){
            timer.stop();
        }

    }
    public void gameOver(Graphics g) {
        drawCenteredString(g, "Score: " + applesEaten, Color.green, 50, SCREEN_HEIGHT / 4);
        drawCenteredString(g, "Game Over", Color.red, 75, SCREEN_HEIGHT / 2);

        // Optionally, you can add more information or buttons, e.g., a restart prompt
        drawCenteredString(g, "Press R to Restart", Color.white, 30, SCREEN_HEIGHT * 3 / 4);
    }

    private void drawCenteredString(Graphics g, String text, Color color, int fontSize, int yPosition) {
        g.setColor(color);
        Font font = new Font("Ink Free", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (SCREEN_WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, yPosition);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction !='R'){
                        direction='L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction !='L'){
                        direction='R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction !='D'){
                        direction='U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction !='U'){
                        direction='D';
                    }
                    break;
                case KeyEvent.VK_R:{
                    if(!running){
                       restartGame();
                    }
                }
            }
        }
    }
    private void restartGame() {
         bodyParts = 5;
         applesEaten=0;
         for(int i=0;i<=bodyParts;i++){
             x[i]=UNIT_SIZE;
             y[i]=UNIT_SIZE;
         }
         appleType='N';
         direction = 'R';
         newApple();
         running = true;
         DELAY=50;
         timer.setDelay(DELAY);
         timer.start();
    }
}
