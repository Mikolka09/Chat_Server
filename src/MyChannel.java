import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyChannel implements Runnable {
    DataInputStream in;
    DataOutputStream out;
    boolean flag = true;
    String name;

    public MyChannel(Socket client) {
        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
        } catch (IOException ex1) {
            System.out.println(ex1.getMessage());
            flag = false;
            try {
                in.close();
                out.close();
            } catch (IOException ex2) {
                System.out.println(ex2.getMessage());
            }
        }
    }

    //Получение информации о клиенте
    String receive() {
        String msg = "";
        try {
            msg = in.readUTF();
        } catch (IOException ex1) {
            flag = false;
            System.out.println(ex1.getMessage());
            Server.channels.remove(this);
            try {
                in.close();
            } catch (IOException ex2) {
                System.out.println(ex2.getMessage());
            }
        }
        return msg;
    }

    //Отправить информацию клиенту
    private void send(String msg) {
        if (null == msg || msg.equals("")) {
            return;
        }
        try {
            out.writeUTF(time());
            out.writeUTF(msg);
        } catch (IOException ex1) {
            flag = false;
            System.out.println(ex1.getMessage());
            Server.channels.remove(this);  // удалить себя
            try {
                out.close();
            } catch (IOException ex2) {
                System.out.println(ex2.getMessage());
            }
        }
    }

    private void sendOthers(String msg) {
        // Определяем, является ли это приватным чатом
        if (msg.contains("@") && msg.indexOf("：") > msg.indexOf("@")){
            String spot = null;

            String secretName = msg.substring(msg.indexOf("@") + 1, msg.indexOf("："));
            String secretMsg = msg.substring(msg.indexOf("：") + 1);
            for (MyChannel other : Server.channels) {
                if (secretName.equals(other.name)) {
                    other.send(name + "Скажу тихо:" + secretMsg);
                }
            }
        }else{
            for (MyChannel other : Server.channels) {
                if (other == this) {
                    continue;
                }
                other.send(msg);
            }
        }
    }

    private String time () {
        Date now = new Date(System.currentTimeMillis());
        String time = new SimpleDateFormat("yyyy.MM.dd  hh:mm:ss").format(now);
        return time;
    }

    @Override
    public void run() {
        send("Добро пожаловать в групповой чат");
        name = receive();
        sendOthers(name + "Регистрация в групповом чате");
        while (flag) {
            sendOthers(receive());
        }
    }
}
