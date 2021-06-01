import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server {
    static ArrayList<Thread> clients=new ArrayList<Thread>();
    static ArrayList<Thread> mafias=new ArrayList<Thread>();
    static ArrayList<Thread> citizens=new ArrayList<Thread>();
    static ArrayList<String> characters=new ArrayList<String>();
    static HashMap<Thread,String> names=new HashMap<Thread,String>();
    static HashMap<Thread,Integer> life=new HashMap<Thread,Integer>();
    static HashMap<Thread,String> clientCharacters=new HashMap<Thread,String>();
    static int ready=0,playersNumber=10;
    public static void main(String[] args) throws IOException {
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

     while (Server.canContinuePlaying())
     {

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
    public static void setThreadName(Thread thread,String name)
    {
        names.put(thread,name);
    }
    public static boolean canContinuePlaying()
    {
        int deadMafias=0,deadCitizens=0;
        for (int i=0;i<mafias.size();i++)
            if(life.get(mafias.get(i))==0)
                deadMafias++;
        for (int i=0;i<citizens.size();i++)
            if(life.get(citizens.get(0))==0)
                deadCitizens++;
        if((deadMafias==mafias.size())||(mafias.size()-deadMafias==citizens.size()-deadCitizens))
            return false;
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
    public static String giveCharacter(Thread thread)
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
