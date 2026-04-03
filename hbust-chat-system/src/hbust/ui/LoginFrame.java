package hbust.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class LoginFrame extends JFrame {
    
    private JTextField nicknameField;
    private JButton enterButton;
    private JButton cancelButton;
    private Socket socket;
    
    public LoginFrame() {
        initUI();
    }
    
    private void initUI() {
        // 设置窗口标题
        setTitle("思同的聊天室 - 登录");
        
        // 设置窗口大小
        setSize(550, 450);
        
        // 设置窗口居中
        setLocationRelativeTo(null);
        
        // 设置关闭操作
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // 设置窗口背景色
        getContentPane().setBackground(new Color(245, 245, 245));
        
        // 使用 BorderLayout
        setLayout(new BorderLayout());
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        
        // ========== 标题 ==========
        JLabel titleLabel = new JLabel("欢迎来到思同的聊天室");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // ========== 昵称区域 ==========
        JLabel nicknameLabel = new JLabel("昵称");
        nicknameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        nicknameLabel.setForeground(new Color(80, 80, 80));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(nicknameLabel, gbc);
        
        // 输入框容器（带边框效果）
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),  // 外边框
            BorderFactory.createEmptyBorder(12, 15, 12, 15)      // 内边距
        ));
        
        // 昵称输入框 - 优化显示
        nicknameField = new JTextField();
        nicknameField.setFont(new Font("微软雅黑", Font.PLAIN, 18));  // 大字体，清晰可见
        nicknameField.setBorder(null);  // 去掉输入框自带边框
        nicknameField.setBackground(Color.WHITE);
        nicknameField.setForeground(new Color(50, 50, 50));  // 深色文字
        nicknameField.setCaretColor(new Color(52, 152, 219));  // 光标颜色
        nicknameField.setSelectionColor(new Color(52, 152, 219, 50));  // 选中颜色
        
        // 添加占位符效果
        addPlaceholder(nicknameField, "请输入您的昵称");
        
        inputPanel.add(nicknameField, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(inputPanel, gbc);
        
        // ========== 按钮区域 ==========
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // 进入按钮
        enterButton = new JButton("登录");
        enterButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        enterButton.setBackground(new Color(46, 204, 113));
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);
        enterButton.setBorderPainted(false);
        enterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enterButton.setPreferredSize(new Dimension(0, 48));  // 高度48px
        
        // 鼠标悬停效果
        enterButton.addActionListener(e->{
            String nickname = nicknameField.getText();
            nicknameField.setText("");
            if (!nickname.isEmpty()) {
                try {
                    login(nickname);
                    new ChatRoomFrame(nickname, socket);

                    dispose();


                } catch (Exception e1) {
                   e1.printStackTrace();
                }


            } else {
                JOptionPane.showMessageDialog(this, "请输入昵称");
            }

        });

        
        // 取消按钮
        cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(0, 48));
        
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(new Color(127, 140, 141));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(new Color(149, 165, 166));
            }
        });
        cancelButton.addActionListener(e -> cancelLogin());
        
        buttonPanel.add(enterButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(buttonPanel, gbc);
        
        // ========== 提示信息 ==========
        JLabel tipLabel = new JLabel(" 输入昵称后点击进入，即可开始聊天");
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        tipLabel.setForeground(new Color(150, 150, 150));
        tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(tipLabel, gbc);
        
        // 添加主面板
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void login(String nickname) throws Exception {
        socket = new Socket(Constant.SERVER_IP, Constant.SERVER_PORT);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(1);
        dos.writeUTF(nickname);
        dos.flush();
    }

    /**
     * 添加占位符效果
     */
    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setForeground(new Color(150, 150, 150));
        textField.setText(placeholder);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(new Color(50, 50, 50));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(new Color(150, 150, 150));
                    textField.setText(placeholder);
                }
            }
        });
    }
    

    private void shakeInputPanel() {
        Component inputPanel = ((JPanel) nicknameField.getParent()).getParent();
        final int SHAKE_DISTANCE = 8;
        final int SHAKE_DURATION = 50;
        
        Point originalPos = inputPanel.getLocation();
        Timer timer = new Timer(SHAKE_DURATION, null);
        timer.addActionListener(new ActionListener() {
            int shakes = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shakes >= 4) {
                    inputPanel.setLocation(originalPos);
                    timer.stop();
                } else {
                    int offset = (shakes % 2 == 0) ? SHAKE_DISTANCE : -SHAKE_DISTANCE;
                    inputPanel.setLocation(originalPos.x + offset, originalPos.y);
                    shakes++;
                }
            }
        });
        timer.start();
    }
    
    /**
     * 取消登录
     */
    private void cancelLogin() {
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要退出程序吗？", 
            "确认退出", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        new LoginFrame();
    }
}