import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private static ArrayList<ClientManager> clients = new ArrayList<ClientManager>();
    private static ArrayList<ClientManager> mafias = new ArrayList<ClientManager>();
    private static ArrayList<ClientManager> citizens = new ArrayList<ClientManager>();
    private static ArrayList<String> characters = new ArrayList<String>();
    private static ArrayList<String> votesReport = new ArrayList<String>();
    private static HashMap<ClientManager, String> names = new HashMap<ClientManager, String>();
    private static HashMap<ClientManager, Integer> life = new HashMap<ClientManager, Integer>();
    private static HashMap<ClientManager, String> clientCharacters = new HashMap<ClientManager, String>();
    private static HashMap<ClientManager, Integer> votes = new HashMap<ClientManager, Integer>();
    private static String phase = "Day";
    private static int ready = 0, playersNumber = 10, clientsVoted = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket server = new ServerSocket(8585);
        Socket client;
        GamingSystem.makeCharacter();
        System.out.println("Server iS On\nWaiting For Clients...");

        for (int i = 0; i < playersNumber; i++) {
            client = server.accept();
            System.out.println("Client Number " + (clients.size() + 1) + " Detected!!!");
            clients.add(new ClientManager(client));
            GamingSystem.setThreadName(clients.get(clients.size() - 1), "player" + clients.size());
            clients.get(clients.size() - 1).start();
        }
        while (ready!=playersNumber) {
            System.out.print("");
        }
        ready=0;
        for (ClientManager clientManager : clients) {
            votes.put(clientManager, 0);
        }
        for (int i=0;i<clients.size();i++)
        {
            if(mafias.contains(clients.get(i)))
                clients.get(i).sendForThisClient(names.get(mafias.get(0))+" is "+clientCharacters.get(mafias.get(0))+".\n"+names.get(mafias.get(1))+" is "+clientCharacters.get(mafias.get(1))+".\n"+names.get(mafias.get(2))+" is "+clientCharacters.get(mafias.get(2))+".");
        }
        while (GamingSystem.canContinuePlaying()) {
            int sec = 0;
            while (sec != 60) {
                sec++;
                Thread.sleep(1000);
                if (sec % 10 == 0 && sec != 60)
                    clients.get(0).sendToAll("God", (60 - sec) + " Second Remaining");
            }
            clients.get(0).sendToAll("God", "EveryOne Say His/Her Last Conversation");
            phase = "Vote";
            while (GamingSystem.startGame()) {
                System.out.print("");
            }
            if (phase.equalsIgnoreCase("Vote")) {
                clients.get(0).sendToAll("God", "If You Said Your Last Conversation For Today , You Can Vote Now");
                clientsVoted = 0;
                for (int i = 0; i < clients.size(); i++) {
                    if (life.get(clients.get(i)) != 0)
                        clients.get(0).sendToAll(names.get(clients.get(i)));
                }
                ready=0;
                while (!GamingSystem.everyOneVoted()) {
                    System.out.print("");
                }
                for (String msg : votesReport) {
                    clients.get(0).sendToAll(msg);
                }
                GamingSystem.checkWhoseOut();
                for (ClientManager clientManager : clients) {
                    votes.put(clientManager, 0);
                }
            }
            while (phase.equalsIgnoreCase("Night")) {

            }
        }
    }

    public static int getClientsVoted() {
        return clientsVoted;
    }

    public static int getPlayersNumber() {
        return playersNumber;
    }

    public static int getReady() {
        return ready;
    }

    public static String getPhase() {
        return phase;
    }

    public static HashMap<ClientManager, Integer> getVotes() {
        return votes;
    }

    public static HashMap<ClientManager, String> getClientCharacters() {
        return clientCharacters;
    }

    public static HashMap<ClientManager, Integer> getLife() {
        return life;
    }

    public static HashMap<ClientManager, String> getNames() {
        return names;
    }

    public static ArrayList<String> getVotesReport() {
        return votesReport;
    }

    public static ArrayList<String> getCharacters() {
        return characters;
    }

    public static ArrayList<ClientManager> getCitizens() {
        return citizens;
    }

    public static ArrayList<ClientManager> getMafias() {
        return mafias;
    }

    public static ArrayList<ClientManager> getClients() {
        return clients;
    }
    public static void setPhase(String phase) {
        Server.phase = phase;
    }

    public static void setReady(int ready) {
        Server.ready = ready;
    }

    public static void setClientsVoted(int clientsVoted) {
        Server.clientsVoted = clientsVoted;
    }
}
