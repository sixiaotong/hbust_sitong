package hbust.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatRoomFrame extends JFrame {
    
    // 用户信息
    private String nickname;
    private Socket socket;
    
    // 聊天界面组件
    private JTextArea chatArea;           // 聊天内容显示区
    private JTextArea inputArea;          // 输入框
    private JButton sendButton;           // 发送按钮
    private JLabel onlineCountLabel;      // 在线人数显示
    private JList<String> onlineUserList; // 在线用户列表

    public ChatRoomFrame() {
       initUI();
       this.setVisible(true);
    }

    public ChatRoomFrame(String nickname, Socket socket) {
        this();
        this.setTitle(nickname+"的聊天窗口");
        this.nickname = nickname;
        this.socket = socket;

        new ClientReaderThread(socket,this).start();

    }

    private void initUI() {
        // 设置窗口
        setTitle("思同的聊天室 - 欢迎 " + nickname);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 设置整体布局为 BorderLayout
        setLayout(new BorderLayout());
        
        // ========== 创建主面板 ==========
        // 使用 JSplitPane 分割左右区域
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(650);  // 左侧聊天区宽度
        mainSplitPane.setDividerSize(2);
        mainSplitPane.setBorder(null);
        
        // 左侧面板（聊天区域）
        JPanel leftPanel = createLeftPanel();
        
        // 右侧面板（用户信息区域）
        JPanel rightPanel = createRightPanel();
        
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // 设置窗口可见
        setVisible(true);
    }
    
    /**
     * 创建左侧聊天区域
     */
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 1. 聊天内容显示区（左上）
        JPanel chatPanel = createChatDisplayPanel();
        leftPanel.add(chatPanel, BorderLayout.CENTER);
        
        // 2. 输入区域（左下）
        JPanel inputPanel = createInputPanel();
        leftPanel.add(inputPanel, BorderLayout.SOUTH);
        
        return leftPanel;
    }
    
    /**
     * 创建聊天内容显示区域
     */
    private JPanel createChatDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "聊天内容",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("微软雅黑", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        
        // 聊天内容文本框
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chatArea.setBackground(Color.WHITE);
        chatArea.setForeground(new Color(50, 50, 50));
        chatArea.setLineWrap(true);        // 自动换行
        chatArea.setWrapStyleWord(true);   // 按单词换行
        
        // 设置光标颜色
        chatArea.setCaretColor(new Color(52, 152, 219));
        
        // 添加滚动条
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 创建输入区域（左下）
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "输入消息",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14),
                new Color(52, 152, 219)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(245, 245, 245));
        
        // 输入框
        inputArea = new JTextArea();
        inputArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        inputArea.setRows(3);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // 输入框滚动条
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        panel.add(inputScroll, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        // 发送按钮
        sendButton = new JButton("发送消息");
        sendButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        sendButton.setBackground(new Color(46, 204, 113));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(150, 35));
        
        // 鼠标悬停效果
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(new Color(39, 174, 96));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(new Color(46, 204, 113));
            }
        });
        
        sendButton.addActionListener(e -> {
            String msg = inputArea.getText();
            inputArea.setText("");
            SendMsgToServer(msg);


                });
        
        // 清空按钮
        JButton clearButton = new JButton("清空");
        clearButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setPreferredSize(new Dimension(80, 35));
        
        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                clearButton.setBackground(new Color(127, 140, 141));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                clearButton.setBackground(new Color(149, 165, 166));
            }
        });
        
        clearButton.addActionListener(e -> inputArea.setText(""));
        
        buttonPanel.add(clearButton);
        buttonPanel.add(sendButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 添加快捷键 Ctrl+Enter 发送

        return panel;
    }

    private void SendMsgToServer(String  msg) {
        try {
            DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
            dos.writeInt(2);
            dos.writeUTF(msg);
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建右侧面板（显示在线人数和用户列表）
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        
        // 在线人数统计面板（右上）
        JPanel onlineCountPanel = createOnlineCountPanel();
        rightPanel.add(onlineCountPanel, BorderLayout.NORTH);
        
        // 在线用户列表
        JPanel userListPanel = createUserListPanel();
        rightPanel.add(userListPanel, BorderLayout.CENTER);
        
        return rightPanel;
    }
    
    /**
     * 创建在线人数统计面板
     */
    private JPanel createOnlineCountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        // 在线人数标题
        JLabel titleLabel = new JLabel("在线用户");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 152, 219));
        panel.add(titleLabel, BorderLayout.WEST);
        
        // 在线人数显示
        onlineCountLabel = new JLabel();
        onlineCountLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        onlineCountLabel.setForeground(new Color(46, 204, 113));
        panel.add(onlineCountLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * 创建在线用户列表
     */
    private JPanel createUserListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // 用户列表
        onlineUserList = new JList<>();
        onlineUserList.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        onlineUserList.setBackground(Color.WHITE);
        onlineUserList.setForeground(new Color(80, 80, 80));
        onlineUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlineUserList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 自定义列表项渲染
        onlineUserList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                
                // 添加用户图标
                label.setIcon(UIManager.getIcon("FileView.computerIcon"));
                label.setFont(new Font("微软雅黑", Font.PLAIN, 13));
                
                if (value.equals(nickname)) {
                    label.setForeground(new Color(46, 204, 113));
                    label.setText(value + " (我)");
                }
                
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(onlineUserList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }


    
    /**
     * 添加消息到聊天区
     */
    private void addMessage(String sender, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());

        // 根据发送者不同显示不同样式
        if (sender.equals(nickname)) {
            // 自己的消息
            chatArea.append(String.format(
                "[%s] %s:\n  %s\n\n",
                time, sender, message
            ));
        } else {
            // 别人的消息
            chatArea.append(String.format(
                "[%s] %s:\n  %s\n\n",
                time, sender, message
            ));
        }

        // 自动滚动到底部
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    /**
     * 添加系统消息
     */
    private void addSystemMessage(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());
        
        chatArea.append(String.format(
            "[%s] 系统消息:\n  %s\n\n",
            time, message
        ));
        
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    
    public static void main(String[] args) {
            new ChatRoomFrame();
    }

    public void updateOnlineList(String[] onLineNames) {
       //把这个线程读取到的用户名字添加到在线用户列表中
        onlineUserList.setListData(onLineNames);
        if (onLineNames != null) {
            int count = onLineNames.length;
            onlineCountLabel.setText("在线人数：" + count);
        } else {
            onlineCountLabel.setText("在线人数：0");
        }
    }

    public void setMsgToWin(String msg) {
        //更新群聊消息到界面展示
        chatArea.append(msg);
    }
}