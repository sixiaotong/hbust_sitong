import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerReaderThread extends Thread{

private Socket socket;
    public ServerReaderThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis=new DataInputStream(socket.getInputStream());
            while (true) {
                int type=dis.readInt();
                switch (type) {
                    case 1:
                       //登录消息
                        String nickname=dis.readUTF();
                        Server.onlinesocket.put(socket,nickname);
                        updataclentonlinelist();
                        break;
                    case 2:
                       //群聊消息
                        String message=dis.readUTF();
                        sendMsgToAll(message);
                        break;
                    case 3:
                       //饲料消息
                        break;
                    default:

                }
            }
        } catch (Exception e) {
            System.out.println("客户端下线了"+"IP："+socket.getInetAddress().getHostAddress());
            Server.onlinesocket.remove(socket);
            updataclentonlinelist();
        }
    }

    private void sendMsgToAll(String message) {
       StringBuilder sb=new StringBuilder();
       String nickname=Server.onlinesocket.get(socket);
       LocalDateTime now=LocalDateTime.now();
       DateTimeFormatter sdf=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss EEE a");
       String time=sdf.format(now);

       StringBuilder msgResult=sb.append(nickname).append(":").append(time).append("\r\n").append(message).append("\r\n");
       for (Socket socket : Server.onlinesocket.keySet()) {
           try {
               OutputStream os=socket.getOutputStream();
               DataOutputStream dos=new DataOutputStream(os);
               dos.writeInt(2);
               dos.writeUTF(msgResult.toString());
               dos.flush();
           } catch (Exception e) {
               System.out.println("客户端下线了"+"IP："+socket.getInetAddress().getHostAddress());
               Server.onlinesocket.remove(socket);
           }
       }
    }

    private void updataclentonlinelist() {
        for (Socket socket : Server.onlinesocket.keySet()) {
            try {
                OutputStream os=socket.getOutputStream();
                DataOutputStream dos=new DataOutputStream(os);
                dos.writeInt(1);
                dos.writeInt(Server.onlinesocket.size());
                for (String nickname : Server.onlinesocket.values()) {
                    dos.writeUTF(nickname);
                }
                dos.flush();
            } catch (Exception e) {
                System.out.println("客户端下线了"+"IP："+socket.getInetAddress().getHostAddress());
                Server.onlinesocket.remove(socket);
            }
        }
    }
}
