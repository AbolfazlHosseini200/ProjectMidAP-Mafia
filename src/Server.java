import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    static ArrayList<Thread> clients=new ArrayList<Thread>();
    static HashMap<Thread,String> names=new HashMap<Thread,String>();
    public static void main(String[] args) throws IOException {
    ServerSocket server=new ServerSocket(8585);
    Socket client;
	System.out.println("Server iS On\nWaiting For Clients...");
	while (true)
    {
        client=server.accept();
        System.out.println("Client Number "+(clients.size()+1) +" Detected!!!");
        clients.add(new ClientManager(client));
        Server.setThreadName(clients.get(clients.size()-1),"player"+clients.size());
        clients.get(clients.size()-1).start();
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
}
