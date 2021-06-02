import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class Server {
    static ArrayList<ClientManager> clients=new ArrayList<ClientManager>();
    static ArrayList<ClientManager> mafias=new ArrayList<ClientManager>();
    static ArrayList<ClientManager> citizens=new ArrayList<ClientManager>();
    static ArrayList<String> characters=new ArrayList<String>();
    static HashMap<ClientManager,String> names=new HashMap<ClientManager,String>();
    static HashMap<ClientManager,Integer> life=new HashMap<ClientManager,Integer>();
    static HashMap<ClientManager,String> clientCharacters=new HashMap<ClientManager,String>();
    static String phase="Day";
    static int ready=0,playersNumber=10;
    public static void main(String[] args) throws IOException, InterruptedException {
    ServerSocket server=new ServerSocket(8585);
    Socket client;
    Server.makeCharacter();
	System.out.println("Server iS On\nWaiting For Clients...");

	for (int i=0;i<playersNumber;i++)
    {
        client=server.accept();
        System.out.println("Client Number "+(clients.size()+1) +" Detected!!!");
        clients.add(new ClientManager(client));
        Server.setThreadName(clients.get(clients.size()-1),"player"+clients.size());
        clients.get(clients.size()-1).start();
    }
	while (ready!=10)
    {
        System.out.print("");
    }
     while (Server.canContinuePlaying())
     {
         int sec=0;
         while (sec!=60)
       {
           sec++;
           Thread.sleep(1000);
           if(sec%10==0 && sec!=60)
               clients.get(0).sendToAll("God",(60-sec)+" Second Remaining");
       }
         clients.get(0).sendToAll("God","EveryOne Say His/Her Last Conversation");
         phase="Vote";
         while (phase.equalsIgnoreCase("Vote"))
         {


         }
         while (phase.equalsIgnoreCase("Night"))
         {

         }
     }
    }
    public static boolean checkName(String name)
    {
     for(int i=0,j=0;i< clients.size();i++)
     {

         //System.out.println((i+1)+")"+names.get(clients.get(i)));
         if(names.get(clients.get(i)).equals(name))
             if(j==1)
             return true;
             else
                 j++;
     }

     return false;
    }
    public static void setThreadName(ClientManager thread,String name)
    {
        names.put(thread,name);
    }
    public static boolean canContinuePlaying()
    {
        int deadMafias=0,deadCitizens=0;
        for (int i=0;i<mafias.size();i++)
        {
            if(life.get(mafias.get(i))==0)
                deadMafias++;
        }
        for (int i=0;i<citizens.size();i++)
        {
            if(life.get(citizens.get(0))==0)
                deadCitizens++;
        }
        System.out.println(deadCitizens+" "+deadMafias+" "+mafias.size()+" "+citizens.size());
        if((deadMafias==mafias.size())||(mafias.size()-deadMafias==citizens.size()-deadCitizens))
        {
            return false;
        }
        return true;

    }
    public static void makeCharacter()
    {
        characters.add("Dr.Lecter");
        characters.add("Mafia");
        characters.add("GodFather");
        characters.add("Citizen");
        characters.add("Doctor");
        characters.add("Sniper");
        characters.add("Detector");
        characters.add("Psychologist");
        characters.add("Mayor");
        characters.add("DieHard");
    }
    public static String giveCharacter(ClientManager thread)
    {
        Random random=new Random();
        int n=random.nextInt(characters.size());
        String chara=characters.get(n);
        characters.remove(n);
        if(chara.equals("Dr.Lecter")||chara.equals("Mafia")||chara.equals("GodFather"))
            mafias.add(thread);
        else
            citizens.add(thread);
        if(chara.equals("DieHard"))
            life.put(thread,2);
        else
            life.put(thread,1);
        clientCharacters.put(thread,chara);
        System.out.println(names.get(thread)+":"+chara);
        return chara;
    }
    public static void ready()
    {
        ready++;
    }
    public static boolean startGame()
    {
        if(ready==playersNumber)
            return false;
        else
            return true;
    }
}
