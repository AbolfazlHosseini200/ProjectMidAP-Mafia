import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
public class ClientManager extends Thread{
    DataOutputStream dataOutputStream=null;
    DataInputStream dataInputStream=null;
    Socket client=null;
    public String name;
    public String character;
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
            {
                while (Server.checkName(name))
                {
                    dataOutputStream.writeUTF("1");
                    name=dataInputStream.readUTF();
                    Server.setThreadName(this,name);
                }
                dataOutputStream.writeUTF("0");
            }
            else
                dataOutputStream.writeUTF("0");
            character=Server.giveCharacter(this);
            dataInputStream.readUTF();
            Server.ready();
            while (true)
            {
                if(!Server.startGame())
                    break;
                System.out.println("test "+name);
            }

            dataOutputStream.writeUTF(character);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
