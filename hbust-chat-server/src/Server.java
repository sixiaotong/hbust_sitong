
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    public static final HashMap<Socket,String> onlinesocket=new HashMap<>();
    public static void main(String[] args) {
        System.out.println("服务器启动");
        try {
            ServerSocket serversocket=new ServerSocket(Constant.PORT);
            while (true) {
                Socket socket=serversocket.accept();
                new ServerReaderThread(socket).start();
                System.out.println("一个客户端上线了"+"IP:"+socket.getInetAddress().getHostAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
