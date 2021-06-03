import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class ClientManager extends Thread{
    DataOutputStream dataOutputStream=null;
    DataInputStream dataInputStream=null;
    Socket client=null;
    public String name;
    public String character;
    public boolean firstTime=false;
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
            String msg;
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
                System.out.print("");
            }
            dataOutputStream.writeUTF(character);
            while (Server.canContinuePlaying())
            {
                Date startDate = new Date();
                Date endDate = new Date();
                while ((int)((endDate.getTime() - startDate.getTime()) / 1000)<60)
                {
                 msg=dataInputStream.readUTF();
                 sendToAll(this.name,msg);
                 endDate=new Date();
                }
                Server.phase="Vote";
                while (Server.phase.equalsIgnoreCase("Vote"))
                {

                }
                while (Server.phase.equalsIgnoreCase("Night"))
                {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendToAll(String Name,String msg) throws IOException {
        if(firstTime)
        for(int i=0;i<Server.clients.size();i++)
            Server.clients.get(i).dataOutputStream.writeUTF(Name+":"+msg);
        else
            firstTime=true;
    }
}
