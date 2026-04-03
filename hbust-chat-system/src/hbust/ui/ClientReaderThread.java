package hbust.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientReaderThread extends Thread{

private Socket socket;
private ChatRoomFrame win;
private DataInputStream dis;
    public ClientReaderThread(Socket socket, ChatRoomFrame win) {
        this.socket = socket;
        this.win = win;
    }

    @Override
    public void run() {
        try {
            dis=new DataInputStream(socket.getInputStream());
            while (true) {
                int type=dis.readInt();
                switch (type) {
                    case 1:
                        updataclentonlinelist();

                        break;
                    case 2:
                        getMsgToWin();
                        break;


                }
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void getMsgToWin() throws Exception {
       String msg=dis.readUTF();
       win.setMsgToWin(msg);
    }


    private void updataclentonlinelist() throws Exception {
        int count=dis.readInt();

        String[] onLineNames=new String[ count];
        for (int i = 0; i < count; i++) {
            String nickname=dis.readUTF();
            onLineNames[i]=nickname;
        }
        win.updateOnlineList(onLineNames);
    }
}
