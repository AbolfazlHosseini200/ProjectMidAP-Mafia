import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

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
        Socket client,clientReader;
        Server.makeCharacter();
        System.out.println("Server iS On\nWaiting For Clients...");

        for (int i = 0; i < playersNumber; i++) {
            client = server.accept();
            clientReader=server.accept();
            System.out.println("Client Number " + (clients.size() + 1) + " Detected!!!");
            clients.add(new ClientManager(client,clientReader,clients));
            Server.setThreadName(clients.get(clients.size() - 1), "player" + clients.size());
            clients.get(clients.size() - 1).start();
        }
        while (ready != 10) {
            System.out.print("");
        }
        for (ClientManager clientManager : clients) {
            votes.put(clientManager, 0);
        }
        for (int i=0;i<mafias.size();i++)
            mafias.get(i).sendForThisClient(names.get(mafias.get(0))+" Is "+clientCharacters.get(mafias.get(0))+"\n"+names.get(mafias.get(2))+" Is "+clientCharacters.get(mafias.get(2))+"\n"+names.get(mafias.get(1))+" Is "+clientCharacters.get(mafias.get(1)));
        for(int i=0;i<clients.size();i++)
            if(clientCharacters.get(clients.get(i)).equalsIgnoreCase("Mayor"))
                for(int j=0;j<clients.size();j++)
                    if(clientCharacters.get(clients.get(j)).equalsIgnoreCase("Doctor"))
                    {
                        clients.get(i).sendForThisClient(names.get(clients.get(j))+" Is Doctor");
                        clients.get(j).sendForThisClient(names.get(clients.get(i))+" Is Mayor");
                    }
        while (Server.canContinuePlaying()) {
            int sec = 0;
            while (sec != 60) {
                sec++;
                Thread.sleep(1000);
                if (sec % 10 == 0 && sec != 60)
                    clients.get(0).sendToAll((60 - sec) + " Second Remaining");
            }
            clients.get(0).sendToAll("EveryOne Say His/Her Last Conversation");
            ready = 0;
            while (true) {
                if (!Server.startGame())
                    break;
                System.out.print("");
            }
            phase = "Vote";
            if (phase.equalsIgnoreCase("Vote")) {
                clients.get(0).sendToAll("God", "You Can Vote Now");
                clientsVoted = 0;
                for (int i = 0; i < clients.size(); i++) {
                    if (life.get(clients.get(i)) != 0)
                        clients.get(0).sendToAll(names.get(clients.get(i)));
                }
                clientsVoted = 0;
                sec = 0;
                while (sec != 40) {
                    sec++;
                    Thread.sleep(1000);
                    if (sec % 10 == 0 && sec != 40)
                        clients.get(0).sendToAll((40 - sec) + " Second Remaining");
                }

                while (!everyOneVoted()) {
                    System.out.print("");
                }
                for (String msg : votesReport) {
                    clients.get(0).sendToAll(msg);
                }
                checkWhoseOut();
                for (ClientManager clientManager : clients) {
                    votes.put(clientManager, 0);
                }
            }
            setPhase("Night");
            while (phase.equalsIgnoreCase("Night")) {

            }
        }
    }

    public static boolean checkName(String name) {
        for (int i = 0, j = 0; i < clients.size(); i++) {
            if (names.get(clients.get(i)).equalsIgnoreCase(name))
                if (j == 1)
                    return true;
                else
                    j++;
        }

        return false;
    }

    public static void setThreadName(ClientManager thread, String name) {
        HashMap<ClientManager, String> names = Server.getNames();
        names.put(thread, name);
    }

    public static boolean canContinuePlaying() {
        ArrayList<ClientManager> mafias = Server.getMafias();
        ArrayList<ClientManager> citizens = Server.getCitizens();
        HashMap<ClientManager, Integer> life = Server.getLife();
        int deadMafias = 0, deadCitizens = 0;
        for (int i = 0; i < mafias.size(); i++) {
            if (life.get(mafias.get(i)) == 0)
                deadMafias++;
        }
        for (int i = 0; i < citizens.size(); i++) {
            if (life.get(citizens.get(0)) == 0)
                deadCitizens++;
        }
        if ((deadMafias == mafias.size()) || (mafias.size() - deadMafias == citizens.size() - deadCitizens)) {
            return false;
        }
        return true;
    }

    public static void makeCharacter() {
        ArrayList<String> characters = Server.getCharacters();
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

    public static String giveCharacter(ClientManager thread) {
        Random random = new Random();
        int n = random.nextInt(characters.size());
        String chara = characters.get(n);
        characters.remove(n);
        if (chara.equals("Dr.Lecter") || chara.equals("Mafia") || chara.equals("GodFather"))
            mafias.add(thread);
        else
            citizens.add(thread);
        if (chara.equals("DieHard"))
            life.put(thread, 2);
        else
            life.put(thread, 1);
        clientCharacters.put(thread, chara);
        System.out.println(names.get(thread) + ":" + chara);
        return chara;
    }

    public static void ready() {
        int ready = Server.getReady();
        ready++;
        Server.setReady(ready);
    }

    public static boolean startGame() {
        int ready = Server.getReady(), playersNumber = Server.getPlayersNumber(), alive = 0;
        HashMap<ClientManager, Integer> life = Server.getLife();
        ArrayList<ClientManager> clients = Server.getClients();
        for (int i = 0; i < clients.size(); i++)
            if (life.get(clients.get(i)) != 0)
                alive++;
        if (ready == alive)
            return false;
        else
            return true;
    }

    public static boolean everyOneVoted() {
        ArrayList<ClientManager> mafias = Server.getMafias();
        ArrayList<ClientManager> citizens = Server.getCitizens();
        HashMap<ClientManager, Integer> life = Server.getLife();
        int playersNumber = Server.getPlayersNumber(), clientsVoted = Server.getClientsVoted();
        int deadMafias = 0, deadCitizens = 0;
        for (int i = 0; i < mafias.size(); i++) {
            if (life.get(mafias.get(i)) == 0)
                deadMafias++;
        }
        for (int i = 0; i < citizens.size(); i++) {
            if (life.get(citizens.get(0)) == 0)
                deadCitizens++;
        }
        int alive = playersNumber - deadCitizens - deadMafias;
        if (alive == clientsVoted)
            return true;
        else
            return false;
    }

    public static void vote(ClientManager client, String vote) {
        int aliveClients = 0;
        for (int i = 0; i < clients.size(); i++)
            if (life.get(clients.get(i)) != 0)
                aliveClients++;
        for (ClientManager aClient : clients) {
            if (vote.equalsIgnoreCase(names.get(aClient))) {
                votesReport.add(names.get(client) + " voted for " + names.get(aClient));
                votes.put(aClient, votes.get(aClient) + 1);
            }
        }
    }

    public static void clientsVoted() {
        int clientsVoted = Server.getClientsVoted();
        Server.setClientsVoted(clientsVoted + 1);
    }

    public static void checkWhoseOut() throws IOException {
        ClientManager manOut, samePlayer=clients.get(0);
        boolean itHasSame = false;
        manOut = clients.get(0);
        for (int i = 1; i < clients.size(); i++) {
            if (votes.get(manOut) < votes.get(clients.get(i))) {
                manOut = clients.get(i);
                itHasSame = false;
            } else if (votes.get(manOut) == votes.get(clients.get(i))) {
                itHasSame = true;
                samePlayer = clients.get(i);
            }
        }
        if (itHasSame) {
            clients.get(0).sendToAll("No Ones Out Because " + names.get(manOut) + " & " + names.get(samePlayer) + " Have Same Number Of Votes.");
            return;
        }
        clients.get(0).sendToAll(names.get(manOut) + " Is Out Now.");
        life.put(manOut, 0);
    }

    public static boolean validVote(String vote,String name)
    {
        if(vote.equalsIgnoreCase(name))
            return false;
        for (int i=0;i<clients.size();i++)
            if(names.get(clients.get(i)).equalsIgnoreCase(vote) && life.get(clients.get(i))!=0)
                return true;
        return false;
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
