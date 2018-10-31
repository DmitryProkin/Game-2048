package Main;

/**
 * Created by Dmitry on 28.01.2018.
 */

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;



public class game2048 extends JPanel {

    private static final Color BG_COLOR = new Color(0xbbada0);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 64;
    private static final int TILES_MARGIN = 16;

    public  static final int CELL_SIZE=64;

    //Кол-во ячеек на экране
    public static final  int COUNT_CELLS_X=4;
    public static final  int COUNT_CELLS_Y=4;

    // Вероятность появления плитки 2, а не 4

    public static final int CHANCE_CELL=17;

    //Начальные значени

    public static final int CELL_STATE1=2;
    public static final int CELL_STATE2=4;


    static boolean myWin = false;
   static boolean myLose = false;
    static int myScore = 0;
    private Tile tile= new Tile() ;


    game2048() {
        setPreferredSize(new Dimension(340,400));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    resetGame();
                }
                if(tile.isFull()){
                    myLose = true;
                }
                if (!myWin && !myLose) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            left();
                            break;
                        case KeyEvent.VK_RIGHT:
                            right();
                            break;
                        case KeyEvent.VK_DOWN:
                            down();
                            break;
                        case KeyEvent.VK_UP:
                            up();
                            break;
                    }
                }
                if (!myWin && tile.isFull()) {
                    myLose = true;
                }

                repaint();
            }
        });
        resetGame();

    }

    private static class ShiftRowResult{
        boolean didAnythingMove;
        int[] shiftedRow;
    }

    public void left() {
        boolean doMove=false;
        /*По очереди сдвигаем числа всех строк в нужном направлении*/
        for (int i = 0; i < COUNT_CELLS_Y; i++) {
                    /*Запрашиваем очередную строку*/
            int[] arg = tile.getLine(i);


            ShiftRowResult result = shiftRow(arg);





                    /*Записываем изменённую строку*/
            tile.setLine(i, result.shiftedRow);
            doMove = doMove || result.didAnythingMove;

        }
        if (doMove) generateNewTile();

    }

    public void right(){
        boolean doMove=false;
        /*По очереди сдвигаем числа всех строк в нужном направлении*/
        for (int i = 0; i < COUNT_CELLS_Y; i++) {
                    /*Запрашиваем очередную строку*/
            int[] arg = tile.getLine(i);

            int[] tmp = new int[arg.length];
            for (int e = 0; e < tmp.length; e++) {
                tmp[e] = arg[tmp.length - e - 1];
            }
            arg = tmp;

            ShiftRowResult result = shiftRow(arg);

                    /*Возвращаем линию в исходный порядок*/

            int[] tmp2 = new int[result.shiftedRow.length];
            for (int e = 0; e < tmp2.length; e++) {
                tmp2[e] = result.shiftedRow[tmp2.length - e - 1];
            }
            result.shiftedRow = tmp2;


                    /*Записываем изменённую строку*/
            tile.setLine(i, result.shiftedRow);

            doMove = doMove || result.didAnythingMove;
        }
        if (doMove) generateNewTile();
    }
    public void down() {
        boolean doMove=false;
        /*По очереди сдвигаем числа всех столбцов в нужном направлении*/
        for(int i = 0; i< COUNT_CELLS_X; i++){
                    /*Запрашиваем очередной столбец*/
            int[] arg =  tile.getColumn(i);

                    /*В зависимости от направления сдвига, меняем или не меняем порядок чисел на противоположный*/

                int[] tmp = new int[arg.length];
                for(int e = 0; e < tmp.length; e++){
                    tmp[e] = arg[tmp.length-e-1];
                }
                arg = tmp;


                    /*Пытаемся сдвинуть числа в этом столбце*/
            ShiftRowResult result = shiftRow (arg);

                    /*Возвращаем линию в исходный порядок*/


                int[] tmp2 = new int[result.shiftedRow.length];
                for(int e = 0; e < tmp2.length; e++){
                    tmp2[e] = result.shiftedRow[tmp2.length-e-1];
                }
                result.shiftedRow = tmp2;


                    /*Записываем изменённый столбец*/
            tile.setColumn(i, result.shiftedRow);

            doMove = doMove || result.didAnythingMove;

        }
        if (doMove) generateNewTile();

    }

    public void up(){
        boolean doMove=false;
        /*По очереди сдвигаем числа всех столбцов в нужном направлении*/
        for(int i = 0; i< COUNT_CELLS_X; i++){
                    /*Запрашиваем очередной столбец*/
            int[] arg =  tile.getColumn(i);


                    /*Пытаемся сдвинуть числа в этом столбце*/
            ShiftRowResult result = shiftRow (arg);
            doMove=result.didAnythingMove;

                    /*Записываем изменённый столбец*/
            tile.setColumn(i, result.shiftedRow);

                    /*Если хоть одна линия была изменена, значит было изменено всё поле*/
            doMove = doMove || result.didAnythingMove;
        }
        if (doMove)
        generateNewTile();



    }

    private static ShiftRowResult shiftRow (int[] oldRow) {
        ShiftRowResult ret = new ShiftRowResult();

        int[] oldRowWithoutZeroes = new int[oldRow.length];
        {
            int q = 0;

            for (int i = 0; i < oldRow.length; i++) {
                if(oldRow[i] != 0){
                    if(q != i){
                        /*
                         * Это значит, что мы передвинули ячейку
                         * на место какого-то нуля (пустой плитки)
                         */
                        ret.didAnythingMove = true;
                    }

                    oldRowWithoutZeroes[q] = oldRow[i];
                    q++;
                }
            }

            /* Чтобы избежать null'ов в конце массива */
            for(int i = q; i < oldRowWithoutZeroes.length; i++) {
                oldRowWithoutZeroes[i] = 0;
            }
        }

        ret.shiftedRow = new int[oldRowWithoutZeroes.length];

        {
            int q = 0;

            {
                int i = 0;


                while (i < oldRowWithoutZeroes.length) {
                    if((i+1 < oldRowWithoutZeroes.length) && (oldRowWithoutZeroes[i] == oldRowWithoutZeroes[i + 1])
                            && oldRowWithoutZeroes[i]!=0) {
                        ret.didAnythingMove = true;
                        ret.shiftedRow[q] = oldRowWithoutZeroes[i] * 2;
                        myScore+=ret.shiftedRow[q];
                        if(ret.shiftedRow[q] == 2048) {myLose=true; myWin=true;}
                        i++;
                    } else {
                        ret.shiftedRow[q] = oldRowWithoutZeroes[i];
                    }

                    q++;
                    i++;
                }

            }
            //Чтобы избежать null'ов в конце массива
            for(int j = q; j < ret.shiftedRow.length; j++) {
                ret.shiftedRow[j] = 0;
            }
        }

        return ret;
    }


    public  void resetGame() {
        myScore = 0;
        myWin = false;
        myLose = false;
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 4; j++) {
//                tile.setTile(i, j, 1);
//            }
//
//        }

        tile.clear();
        generateNewTile();
        generateNewTile();
    }
    /**
     * Создаёт в случайной пустой клетке игрового поля плитку (с ненулевым состоянием).
     */
    private  void generateNewTile() {
        int state = new Random().nextInt() <= CHANCE_CELL ? CELL_STATE2 : CELL_STATE1;

        int randX = new Random().nextInt(COUNT_CELLS_X);
        int currentX = randX;

        int randY = new Random().nextInt(COUNT_CELLS_Y);
        int currentY = randY;

        boolean placed = false;
        while (!placed) {
            if (tile.isEmpty(currentX, currentY)) {
                tile.setTile(currentX, currentY, state);
                placed = true;
            } else {
                if (currentX + 1 < COUNT_CELLS_X) {
                    currentX++;
                } else {
                    currentX = 0;
                    if (currentY + 1 < COUNT_CELLS_Y) {
                        currentY++;
                    } else {
                        currentY = 0;
                    }
                }

            }
        }
    }



//
//   public void GameField() {
//        theField = new Tile[COUNT_CELLS_X][COUNT_CELLS_Y];
//
//        for (int i = 0; i < theField.Field.length; i++) {
//            for (int j = 0; j < theField.Field.length; j++) {
//
//                theField.Field[i][j] = 0;
//            }
//        }
//    }


//    //Возвращает значения поля по координатам
//    public int getState(int x, int y) {
//        return (theField[x][y]);
//    }
//
//    //Изменяет состояние поля по координатам
//    public void setState(int x ,int y, int state){
//        theField[x][y]=state;
//    }
//
//    public void setColumn(int j , int [] newColumn){
//        for(int i=0; i<COUNT_CELLS_X; i++){
//            theField[i][j]=newColumn[i];
//        }
//    }
//
//    public int[] getColumn(int j ){
//        int [] prom = new int[COUNT_CELLS_X];
//        for( int i=0;i<COUNT_CELLS_X;i++){
//            prom[i]=theField[i][j];
//        }
//        return prom;
//    }
//
//    public void setLine(int i,int[] newLine){
//        theField[i] = newLine;
//    }
//    public int[] getLine(int i){
//        return theField[i];
//    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
               draws(g,tile,x,y);
            }
        }
        }

        private void draws(Graphics g2, Tile tile, int x, int y) {
            Graphics2D g = ((Graphics2D) g2);
            //Этого достаточно, чтобы включить сглаживание всех отрисовываемых в дальнейшем фигур.
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);


            int value = tile.Field[x][y];
            int xOffset = offsetCoors(x);
            int yOffset = offsetCoors(y);
            g.setColor(tile.getBackground(value));
            g.fillRoundRect(xOffset, yOffset+60, TILE_SIZE, TILE_SIZE, 14, 14);
            g.setColor(tile.getForeground(value));
            final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
            final Font font = new Font(FONT_NAME, Font.BOLD, size);
            g.setFont(font);

            String s = String.valueOf(value);
            final FontMetrics fm = getFontMetrics(font);

            final int w = fm.stringWidth(s);
            final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

            if (value != 0)
                g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset+60 + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);

            if (myWin || myLose) {
                g.setColor(new Color(255, 255, 255, 30));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(78, 139, 202));
                g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
                if (myWin) {
                    g.drawString("You won!", 68, 150);
                }
                if (myLose) {
                    g.drawString("Game over!", 50, 130);
                    g.drawString("You lose!", 64, 200);
                }
                if (myWin || myLose) {
                    g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
                    g.setColor(new Color(128, 128, 128, 128));
                    g.drawString("Press ESC to play again", 80, getHeight() - 40);
                }
            }
            g.fillRoundRect(250,10,70, 50,14,14 );
            g.setColor(new Color(0xcdc1b4));
            g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
            g.drawString("Score " , 260, 30);
            g.setColor(new Color(0xeee4da));
            g.drawString(""+ myScore,270,50);

            g.setColor(new Color(0x776E65));
            g.setFont(new Font(FONT_NAME, Font.BOLD, 72));
            g.drawString("2048",30,60);


        }

    private static int offsetCoors(int arg) {
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
    }

    static class Tile {
        int value;
        int[][] Field= new int[4][4];
         Tile(){
             value =0;
            for(int i=0; i<4;i++) {
                for (int j = 0; j < 4; j++) {
                    Field[i][j] =value;
                }
            }
        }

        public Tile(int i, int j ,int num){
            Field[i][j] = num;
        }

        public void setTile(int i, int j ,int num){
            Field[i][j] = num;
        }

        public int getTile(int i, int j){return Field[i][j];}

        public boolean isEmpty(int i , int j){
            return Field[i][j] == 0;
        }

        public boolean isFull(){
            for(int i=0; i<4;i++){
                for(int j=0; j<4;j++){
                    if(Field[i][j]==0) return false;
                }
        }
            return true;
        }

        public void clear(){
            for(int i=0; i<4;i++) {
                for (int j = 0; j < 4; j++) {
                    Field[i][j] = 0;
                }
            }
        }

        public int[] getColumn(int j ) {
//            int[] prom = new int[COUNT_CELLS_X];
//            for (int i = 0; i < COUNT_CELLS_X; i++) {
//                prom[i] = Field[j][i];
//            }
//            return prom;
            return  Field[j];
        }

        public void setColumn(int j , int [] newColumn){
//        for(int i=0; i<COUNT_CELLS_X; i++){
//            Field[j][i]=newColumn[i];
//        }
            Field[j] = newColumn;


    }

        public void setLine(int j,int[] newLine){
            for(int i=0; i<COUNT_CELLS_Y; i++){
            Field[i][j]=newLine[i];
        }
        }
        public int[] getLine(int j){
            int[] prom = new int[COUNT_CELLS_Y];
            for (int i = 0; i < COUNT_CELLS_Y; i++) {
                prom[i] = Field[i][j];
            }
            return prom;
        }

        public Color getForeground(int val){

            return val < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
        }

        public Color getBackground(int val){
            switch(val){
                case 2:    return new Color(0xeee4da);
                case 4:    return new Color(0xede0c8);
                case 8:    return new Color(0xf2b179);
                case 16:   return new Color(0xf59563);
                case 32:   return new Color(0xf67c5f);
                case 64:   return new Color(0xf65e3b);
                case 128:  return new Color(0xedcf72);
                case 256:  return new Color(0xedcc61);
                case 512:  return new Color(0xedc850);
                case 1024: return new Color(0xedc53f);
                case 2048: return new Color(0xedc22e);
            }
            return new Color(0xcdc1b4);
        }

    }






    public  static  void  main(String  args[])  {

        JFrame jfrm = new JFrame("Game 2048");
        jfrm.setSize(340, 420);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setResizable(false);
//        JLabel scr = new JLabel ("Score");
//        jfrm.add(scr);
        jfrm.add(new game2048());
        jfrm.setLocationRelativeTo(null);
        jfrm.setVisible(true);
}
}
