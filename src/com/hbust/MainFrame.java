package com.hbust;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;







public class MainFrame extends JFrame {
    private static final String imagePath = "stone_maze/src/image/";
    private int[][]imageData={
            {1,2,3,4},
            {5,6,7,8},
            {9,10,11,12},
            {13,14,15,0}
    };
    private int[][] winData=new int[][]{
            {1,2,3,4},
            {5,6,7,8},
            {9,10,11,12},
            {13,14,15,0}
    };
    private int row ;
    private int col ;
    private int count;
    private int mincount;
    public MainFrame() {
        mincount=ReadFileScore();
        inintFrame();
        initRandonmArray();
        inintimages();//绘制图片
        //初始化系统菜单，点击弹出系统菜单是重启游戏和退出游戏;
        initMenu();
        initKeyPressEvent();
        this.setVisible(true);

    }

    private void initKeyPressEvent() {
        //给当前窗口绑定上下左右按键事件
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode){
                    case KeyEvent.VK_UP:
                        SwitchandMove(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        SwitchandMove(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        SwitchandMove(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        SwitchandMove(Direction.RIGHT);
                        break;
                }
            }
        });
    }

    private void SwitchandMove(Direction r) {
        switch (r) {
            case UP:
                if(row < imageData.length-1){
                    int temp = imageData[row][col];
                    imageData[row][col]=imageData[row+1][col];
                    imageData[row+1][col]=temp;
                    row++;
                    count++;
                }
                break;
            case DOWN:
                if(row > 0){
                    int temp = imageData[row][col];
                    imageData[row][col]=imageData[row-1][col];
                    imageData[row-1][col]=temp;
                    row--;
                    count++;
                }
                break;
            case LEFT:
                if(col < imageData.length-1){
                    int temp = imageData[row][col];
                    imageData[row][col]=imageData[row][col+1];
                    imageData[row][col+1]=temp;
                    col++;
                    count++;
                }
                break;
            case RIGHT:
                if(col > 0){
                    int temp = imageData[row][col];
                    imageData[row][col]=imageData[row][col-1];
                    imageData[row][col-1]=temp;
                    col--;
                    count++;
                }
                break;
        }
        inintimages();
    }





    private void initRandonmArray() {
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[i].length; j++) {
                int j1 = (int)(Math.random()*imageData.length);
                int k1 = (int)(Math.random()*imageData[i].length);

                int j2=(int)(Math.random()*imageData.length);
                int k2=(int)(Math.random()*imageData[i].length);
                int temp = imageData[j1][k1];
                imageData[j1][k1]=imageData[j2][k2];
                imageData[j2][k2]=temp;
            }
        }
        //定位空白色块的位置,遍历整个数组
        OUT:
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[i].length; j++) {
                if (imageData[i][j]==0){
                    row=i;
                    col=j;
                    break OUT;
                }
            }
        }
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("菜单");
        JMenuItem exitJi = new JMenuItem("退出");
        menu.add(exitJi);
        exitJi.addActionListener(e-> {
            dispose();
        });
        JMenuItem restartJi = new JMenuItem("重新开始");
        menu.add(restartJi);//添加菜单项
        restartJi.addActionListener(e-> {
            initRandonmArray();
            inintimages();


            // 重启游戏
                });
        menuBar.add(menu);//添加菜单
        this.setJMenuBar(menuBar);
    }

    private void inintimages() {
        this.getContentPane().removeAll();

        JLabel counttext = new JLabel("当前移动"+count+"步");
        //将字体设置为红色
        counttext.setForeground(Color.RED);
        //将字体设置为楷体
        counttext.setFont(new Font("楷体",Font.BOLD,15));
        counttext.setBounds(30,0,100,20);
        this.add(counttext);

        if(mincount!=0)
        {
            JLabel counttext2 = new JLabel("历史胜利最少" + mincount + "步");
            //将字体设置为红色
            counttext2.setForeground(Color.red);
            //将字体设置为楷体
            counttext2.setFont(new Font("楷体", Font.BOLD, 15));
            counttext2.setBounds(280, 0, 100, 20);
            this.add(counttext2);
         }
        else{
            JLabel counttext2 = new JLabel("当前没有历史步数");
            //将字体设置为红色
            counttext2.setForeground(Color.red);
            //将字体设置为楷体
            counttext2.setFont(new Font("楷体", Font.BOLD, 15));
            counttext2.setBounds(280, 0, 150, 20);
            this.add(counttext2);
            }




        if(iswin()){
            JLabel lable = new JLabel(new ImageIcon(imagePath+"help.png"));
            lable.setBounds(0,0,450,480);//设置图片大小
            this.add(lable);

            int Fileminscore =ReadFileScore();
            if(Fileminscore==0||count<Fileminscore){
                writeFileScore(count);
            }

        }






        //1： 展示一个行列矩阵的图片色块依次铺满窗口（4*4）
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[i].length; j++) {
                String imageName = imageData[i][j] + ".png";
                JLabel label = new JLabel();
                label.setIcon(new ImageIcon( imagePath+ imageName));
                label.setBounds(23+j * 100, 52+i * 100, 100, 100);
                this.add(label);
            }
            }
               JLabel background = new JLabel(new ImageIcon(imagePath+"background.png"));
               background.setBounds(0, 20, 450, 480);
               this.add(background);
               this.repaint();
        }

    private int ReadFileScore() {
        try(
            FileReader fr = new FileReader("stone_maze\\src\\score.txt");
            BufferedReader br = new BufferedReader(fr);
            )
        {
           String line = br.readLine();
           return Integer.valueOf(line);
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void writeFileScore(int count) {
        try(
            FileWriter fw = new FileWriter("stone_maze\\src\\score.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            )
        {
            bw.write(count+"");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean iswin() {
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[i].length; j++) {
                if (imageData[i][j] != winData[i][j]){
                    return false;
                }
            }
        }
        return true;
    }


    private void inintFrame() {
        this.setTitle("石头迷阵V1.0_sitong");
        this.setSize(465, 520);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(null);


    }
}
