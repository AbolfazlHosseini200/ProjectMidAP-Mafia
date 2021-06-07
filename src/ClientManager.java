import jdk.swing.interop.SwingInterOpUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ClientManager extends Thread{
    DataOutputStream dataOutputStream=null,dataOutputStream2=null;
    DataInputStream dataInputStream=null;
    private String Vote = null;
    private Socket client = null;
    private String name;
    private String character;
    private ArrayList<ClientManager> clients;
    private boolean firstTime = false;
    private String vote;
    public ClientManager(Socket client,Socket clientReader,ArrayList<ClientManager> clients) throws IOException {
        this.client=client;
        this.clients=clients;
        dataOutputStream2=new DataOutputStream(clientReader.getOutputStream());
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
            while (Server.getReady() != 10) {
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
                 endDate=new Date();
                 sendToAll(this.name,msg);
                }
                Server.ready();
                while (true)
                {
                    if(!Server.startGame())
                        break;
                    System.out.print("");
                }
                Server.setPhase("Vote");
                vote="";
                startDate = new Date();
                endDate = new Date();
                String input;
                while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 40) || !vote.equalsIgnoreCase("done")) {
                    input=dataInputStream.readUTF();
                    if(input.equalsIgnoreCase("done"))
                        break;
                    else
                        vote=input;
                    if(Server.validVote(vote,name))
                    sendForThisClient("Your Vote Is "+vote+" Till Now\nYou Can Still Change It Or Enter Done");
                    else
                    sendForThisClient("You Haven't Vote Yet");
                    endDate=new Date();
                }
                if (Server.validVote(vote,name))
                    Server.vote(this, vote);
                if(Server.validVote(vote,name))
                sendForThisClient("Wait For Other Players To Vote\nYour Vote Is : "+vote);
                else
                    sendForThisClient("Wait For Other Players To Vote\nYou Didnt Vote");
                Server.clientsVoted();
                while (!Server.everyOneVoted()) {
                    System.out.print("");
                }
                while (Server.getPhase().equalsIgnoreCase("Night"))
                {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendToAll(String Name,String msg) throws IOException {
        if(firstTime)
            for(int i=0;i<clients.size();i++)
                clients.get(i).dataOutputStream2.writeUTF(Name+":"+msg);
        else
            firstTime=true;
    }
    public void sendToAll(String msg) throws IOException {
        if(firstTime)
            for(int i=0;i<clients.size();i++)
                clients.get(i).dataOutputStream2.writeUTF(msg);
        else
            firstTime=true;
    }
    public void sendForThisClient(String msg) throws IOException {
        this.dataOutputStream2.writeUTF(msg);
    }
}
