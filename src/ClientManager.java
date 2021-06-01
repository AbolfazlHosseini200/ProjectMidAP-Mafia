import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
public class ClientManager extends Thread{
    DataOutputStream dataOutputStream=null;
    DataInputStream dataInputStream=null;
    Socket client=null;
    public String name;
    public ClientManager(Socket client) throws IOException {
        this.client=client;
        dataInputStream=new DataInputStream(client.getInputStream());
        dataOutputStream=new DataOutputStream(client.getOutputStream());
    }
    public String getNames() {
        return name;
    }
    @Override
    public void run() {
        try {
            name=dataInputStream.readUTF();
            Server.setThreadName(this,name);
            if(Server.checkName(name))
            while (Server.checkName(name))
            {
                dataOutputStream.writeUTF("1");
                name=dataInputStream.readUTF();
                Server.setThreadName(this,name);
            }
            else
                dataOutputStream.writeUTF("0");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
