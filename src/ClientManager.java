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
    private int chance=2;
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
            while (Server.canContinuePlaying() && Server.getLife().get(this)!=0)
            {

                Date startDate = new Date();
                Date endDate = new Date();

                while ((int)((endDate.getTime() - startDate.getTime()) / 1000)<60 && !Server.getSilentMan().equals(this))
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
                if(character.equalsIgnoreCase("Mayor")&&chance!=0&&Server.checkWhoseOut()!=null)
                {
                    sendForThisClient("Do You Want To Cancel Voting For Today?");
                    vote="";
                    startDate = new Date();
                    endDate = new Date();
                    while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 20) || !vote.equalsIgnoreCase("done")) {
                        input=dataInputStream.readUTF();
                        if(input.equalsIgnoreCase("done"))
                            break;
                        else
                            vote=input;
                        if("yes".equalsIgnoreCase(vote))
                            sendForThisClient("Your Wanna Accept The Voting "+vote+" Till Now\nYou Can Still Change It Or Enter Done");
                        else
                            sendForThisClient("You Dont Wanna Accept The Voting Till Now");
                        endDate=new Date();
                    }
                    if(vote.equalsIgnoreCase("yes"))
                    {
                        sendToAll(Server.getNames().get(Server.checkWhoseOut())+"Is In The Game");

                    }
                    else {
                        chance--;
                        sendToAll(Server.getNames().get(Server.checkWhoseOut())+"Is Dead Now");
                        for (int i=0;i<Server.getRolesList().size();i++)
                            if(Server.getRolesList().get(i) instanceof Mayor)
                                ((Mayor) Server.getRolesList().get(i)).action(Server.checkWhoseOut());
                    }
                }
                if(Server.getLife().get(this)==0)
                    break;
                Server.ready();
                while (true) {
                    if (!Server.startGame())
                        break;
                    System.out.print("");
                }
                Server.setPhase("Night");
                if (Server.getPhase().equalsIgnoreCase("Night"))
                {
                   if(character.equalsIgnoreCase("GodFather")||character.equalsIgnoreCase("Mafia")||character.equalsIgnoreCase("Dr.Lecter"))
                   {
                       while ((int)((endDate.getTime() - startDate.getTime()) / 1000)<30)
                       {
                           msg=dataInputStream.readUTF();
                           endDate=new Date();
                           ArrayList<ClientManager> mafias=Server.getMafias();
                           for(int i=0;i<mafias.size();i++)
                           mafias.get(i).sendForThisClient(this.name+":"+msg);
                       }
                       if(character.equalsIgnoreCase("GodFather"))
                       {
                           vote="";
                           startDate = new Date();
                           endDate = new Date();
                           while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 20) || !vote.equalsIgnoreCase("done")) {
                               input=dataInputStream.readUTF();
                               if(input.equalsIgnoreCase("done"))
                                   break;
                               else
                                   vote=input;
                               if(Server.validMafiaKill(vote))
                                   sendForThisClient("Your Wanna Kill "+vote+" Till Now\nYou Can Still Change It Or Enter Done");
                               else
                                   sendForThisClient("You Dont Wanna Kill AnyOne Till Now");
                               endDate=new Date();
                           }
                           ClientManager deadMan=null;
                           for(int i=0;i<Server.getCitizens().size();i++)
                               if(Server.getNames().get(Server.getCitizens().get(i)).equalsIgnoreCase(vote))
                                   deadMan=Server.getCitizens().get(i);
                           if (Server.validMafiaKill(vote))
                               for (int i=0;i<Server.getRolesList().size();i++)
                               if(Server.getRolesList().get(i) instanceof GodFather)
                                   ((GodFather) Server.getRolesList().get(i)).action(deadMan);

                           if(Server.validMafiaKill(vote))
                               sendForThisClient("Wait For Other Players To Vote\nYour Vote Is : "+vote);
                           else
                               sendForThisClient("Wait For Other Players To Vote\nYou Didnt Vote");
                       }
                       else if(character.equalsIgnoreCase("Dr.Lecter"))
                       {
                           vote="";
                           startDate = new Date();
                           endDate = new Date();
                           while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 20) || !vote.equalsIgnoreCase("done")) {
                               input=dataInputStream.readUTF();
                               if(input.equalsIgnoreCase("done"))
                                   break;
                               else
                                   vote=input;
                               if(!Server.validMafiaKill(vote))
                                   sendForThisClient("Your Wanna Save "+vote+" Till Now\nYou Can Still Change It Or Enter Done");
                               else
                                   sendForThisClient("You Dont Wanna Save AnyOne Till Now");
                               endDate=new Date();
                           }
                           ClientManager safeMan=null;
                           for(int i=0;i<Server.getClients().size();i++)
                               if(Server.getNames().get(Server.getClients().get(i)).equalsIgnoreCase(vote))
                                   safeMan=Server.getCitizens().get(i);
                           if (!Server.validMafiaKill(vote))
                               for (int i=0;i<Server.getRolesList().size();i++)
                                   if(Server.getRolesList().get(i) instanceof DrLecter)
                                       ((DrLecter) Server.getRolesList().get(i)).action(safeMan);

                           if(!Server.validMafiaKill(vote))
                               sendForThisClient("Wait For Other Players\nYour Save Is : "+vote);
                           else
                               sendForThisClient("Wait For Other Players\nYou Didnt Save AnyOne");
                       }
                   }
                    if(character.equalsIgnoreCase("Doctor"))
                    {
                        vote="";
                        startDate = new Date();
                        endDate = new Date();
                        while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 20) || !vote.equalsIgnoreCase("done")) {
                            input=dataInputStream.readUTF();
                            if(input.equalsIgnoreCase("done"))
                                break;
                            else
                                vote=input;
                            if(!Server.checkName(vote)||vote.equalsIgnoreCase(this.name))
                                sendForThisClient("Your Wanna Save "+vote+" Till Now\nYou Can Still Change It Or Enter Done");
                            else
                                sendForThisClient("You Dont Wanna Save AnyOne Till Now");
                            endDate=new Date();
                        }
                        ClientManager safeMan=null;
                        for(int i=0;i<Server.getClients().size();i++)
                            if(Server.getNames().get(Server.getClients().get(i)).equalsIgnoreCase(vote))
                                safeMan=Server.getCitizens().get(i);
                        if (!Server.checkName(vote)||vote.equalsIgnoreCase(this.name))
                            for (int i=0;i<Server.getRolesList().size();i++)
                                if(Server.getRolesList().get(i) instanceof Doctor)
                                    ((Doctor) Server.getRolesList().get(i)).action(safeMan);

                        if(!Server.checkName(vote)||vote.equalsIgnoreCase(this.name))
                            sendForThisClient("Wait For Other Players\nYour Save Is : "+vote);
                        else
                            sendForThisClient("Wait For Other Players\nYou Didnt Save AnyOne");
                    }
                    if(character.equalsIgnoreCase("Sniper"))
                    {
                        vote="";
                        startDate = new Date();
                        endDate = new Date();
                        while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 20) || !vote.equalsIgnoreCase("done")) {
                            input=dataInputStream.readUTF();
                            if(input.equalsIgnoreCase("done"))
                                break;
                            else
                                vote=input;
                            if(!Server.checkName(vote))
                                sendForThisClient("Your Shot Is "+vote+" Till Now\nYou Can Still Change It Or Enter Done");
                            else
                                sendForThisClient("You Dont Shoot AnyOne Till Now");
                            endDate=new Date();
                        }
                        ClientManager snipersShot=null;
                        for(int i=0;i<Server.getClients().size();i++)
                            if(Server.getNames().get(Server.getClients().get(i)).equalsIgnoreCase(vote))
                                snipersShot=Server.getCitizens().get(i);
                        if (!Server.checkName(vote))
                            for (int i=0;i<Server.getRolesList().size();i++)
                                if(Server.getRolesList().get(i) instanceof Sniper)
                                    if(((Sniper)Server.getRolesList().get(i)).getShots()!=0)
                                    ((Sniper) Server.getRolesList().get(i)).action(snipersShot);
                                     else
                                    {
                                        vote="";
                                        sendForThisClient("You're Out Of Shots.");
                                    }
                        if(!Server.checkName(vote))
                            sendForThisClient("Wait For Other Players\nYour Shot Is : "+vote);
                        else
                            sendForThisClient("Wait For Other Players\nYou Didnt Shoot AnyOne");
                    }
                    if(character.equalsIgnoreCase("Psychologist"))
                    {
                        vote="";
                        startDate = new Date();
                        endDate = new Date();
                        while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 20) || !vote.equalsIgnoreCase("done")) {
                            input=dataInputStream.readUTF();
                            if(input.equalsIgnoreCase("done"))
                                break;
                            else
                                vote=input;
                            if(!Server.checkName(vote)||vote.equalsIgnoreCase(this.name))
                                sendForThisClient("Your Wanna Silent "+vote+" Till Now\nYou Can Still Change It Or Enter Done");
                            else
                                sendForThisClient("You Dont Wanna Silent AnyOne Till Now");
                            endDate=new Date();
                        }
                        ClientManager silentMan=null;
                        for(int i=0;i<Server.getClients().size();i++)
                            if(Server.getNames().get(Server.getClients().get(i)).equalsIgnoreCase(vote))
                                silentMan=Server.getCitizens().get(i);
                        if (!Server.checkName(vote)||vote.equalsIgnoreCase(this.name))
                            for (int i=0;i<Server.getRolesList().size();i++)
                                if(Server.getRolesList().get(i) instanceof Psychologist)
                                        ((Psychologist) Server.getRolesList().get(i)).action(silentMan);
                        if(!Server.checkName(vote)||vote.equalsIgnoreCase(this.name))
                            sendForThisClient("Wait For Other Players\nYour Target Is : "+vote);
                        else
                            sendForThisClient("Wait For Other Players\nYou Didnt Silent AnyOne");
                    }
                    if(character.equalsIgnoreCase("Detector"))
                    {
                        vote="";
                        startDate = new Date();
                        endDate = new Date();
                        while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 20) || !vote.equalsIgnoreCase("done")) {
                            input=dataInputStream.readUTF();
                            if(input.equalsIgnoreCase("done"))
                                break;
                            else
                                vote=input;
                            if(!Server.checkName(vote))
                                sendForThisClient("Your Target Is "+vote+" Till Now\nYou Can Still Change It Or Enter Done");
                            else
                                sendForThisClient("You Dont Have Target Till Now");
                            endDate=new Date();
                        }
                        ClientManager target=null;
                        for(int i=0;i<Server.getClients().size();i++)
                            if(Server.getNames().get(Server.getClients().get(i)).equalsIgnoreCase(vote))
                                target=Server.getCitizens().get(i);
                        if (!Server.checkName(vote))
                            for (int i=0;i<Server.getRolesList().size();i++)
                                if(Server.getRolesList().get(i) instanceof Detector)
                                        ((Detector) Server.getRolesList().get(i)).action(target);
                        if(!Server.checkName(vote))
                            sendForThisClient("Wait For Other Players\nYour Target Is : "+vote);
                        else
                            sendForThisClient("Wait For Other Players\nYou Didnt Have Target.");
                    }
                    if(character.equalsIgnoreCase("DieHard"))
                    {
                        vote="No";
                        startDate = new Date();
                        endDate = new Date();
                        while ((((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 20) || !vote.equalsIgnoreCase("done")) {
                            input=dataInputStream.readUTF();
                            if(input.equalsIgnoreCase("done"))
                                break;
                            else
                                vote=input;
                            if(vote.equalsIgnoreCase("Yes"))
                                sendForThisClient("You Want To Know Statistics.");
                            else
                                sendForThisClient("You Don't Want To Know Statistics.");
                            endDate=new Date();
                        }
                        if (vote.equalsIgnoreCase("yes"))
                            for (int i=0;i<Server.getRolesList().size();i++)
                                if(Server.getRolesList().get(i) instanceof DieHard)
                                    if(((DieHard) Server.getRolesList().get(i)).getChances()!=0)
                                    ((DieHard) Server.getRolesList().get(i)).action(this);
                                    else
                                    {
                                        vote="No";
                                        sendForThisClient("Your Chances Are Finished.");
                                    }
                        if(vote.equalsIgnoreCase("yes"))
                            sendForThisClient("Wait For Other Players\nYou Want To Know Statistics.");
                        else
                            sendForThisClient("Wait For Other Players\nYou Don't Want To Know Statistics.");
                    }
                    Server.ready();
                    while (true) {
                        if (!Server.startGame())
                            break;
                        System.out.print("");
                    }
                }
            }
            if(Server.canContinuePlaying())
                sendForThisClient("You're Dead Now\nWhat Are You Going To Do?\n1)exit\n2)watch\nChoose:");
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
