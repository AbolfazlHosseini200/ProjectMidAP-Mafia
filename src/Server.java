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
    private static ArrayList<ClientManager> nightDeaths = new ArrayList<ClientManager>();
    private static ArrayList<String> characters = new ArrayList<String>();
    private static ArrayList<String> votesReport = new ArrayList<String>();
    private static ArrayList<Roles> rolesList = new ArrayList<Roles>();
    private static HashMap<ClientManager, String> names = new HashMap<ClientManager, String>();
    private static HashMap<ClientManager, Integer> life = new HashMap<ClientManager, Integer>();
    private static HashMap<ClientManager, String> clientCharacters = new HashMap<ClientManager, String>();
    private static HashMap<ClientManager, Integer> votes = new HashMap<ClientManager, Integer>();
    private static ClientManager silentMan = null;
    private static ClientManager mafiasShot = null;
    private static ClientManager doctorsHeal = null;
    private static ClientManager snipersShot = null;
    private static ClientManager lectersHeal = null;
    private static boolean tellStatistics = false;
    private static String statistics;


    public static void setSafeCitizen(ClientManager safeCitizen) {
        Server.safeCitizen = safeCitizen;
    }

    private static ClientManager safeCitizen = null;
    private static String phase = "Day";
    private static int ready = 0, playersNumber = 10, clientsVoted = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket server = new ServerSocket(8585);
        Socket client, clientReader;
        Server.makeCharacter();
        System.out.println("Server iS On\nWaiting For Clients...");

        for (int i = 0; i < playersNumber; i++) {
            client = server.accept();
            clientReader = server.accept();
            System.out.println("Client Number " + (clients.size() + 1) + " Detected!!!");
            clients.add(new ClientManager(client, clientReader, clients));
            Server.setThreadName(clients.get(clients.size() - 1), "player" + clients.size());
            clients.get(clients.size() - 1).start();
        }
        while (ready != 10) {
            System.out.print("");
        }
        for (ClientManager clientManager : clients) {
            votes.put(clientManager, 0);
        }
        for (int i = 0; i < mafias.size(); i++)
            mafias.get(i).sendForThisClient(names.get(mafias.get(0)) + " Is " + clientCharacters.get(mafias.get(0)) + "\n" + names.get(mafias.get(2)) + " Is " + clientCharacters.get(mafias.get(2)) + "\n" + names.get(mafias.get(1)) + " Is " + clientCharacters.get(mafias.get(1)));
        for (int i = 0; i < clients.size(); i++)
            if (clientCharacters.get(clients.get(i)).equalsIgnoreCase("Mayor"))
                for (int j = 0; j < clients.size(); j++)
                    if (clientCharacters.get(clients.get(j)).equalsIgnoreCase("Doctor")) {
                        clients.get(i).sendForThisClient(names.get(clients.get(j)) + " Is Doctor");
                        clients.get(j).sendForThisClient(names.get(clients.get(i)) + " Is Mayor");
                    }
        while (Server.canContinuePlaying()) {
            int sec = 0;
            if(tellStatistics)
                clients.get(0).sendToAll(statistics);
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
                ready=0;
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
                votesReport.clear();
                checkWhoseOut();
                while (true) {
                    if (!Server.startGame())
                        break;
                    System.out.print("");
                }
                for (ClientManager clientManager : clients) {
                    votes.put(clientManager, 0);
                }
            }
            setPhase("Night");
            setLectersHeal(null);
            setMafiasShot(null);
            setSnipersShot(null);
            setDoctorsHeal(null);
            setSilentMan(null);
            tellStatistics=false;
            nightDeaths.clear();
            ready=0;
            if (phase.equalsIgnoreCase("Night")) {
                for(int i=0;i<clients.size();i++)
                    if(life.get(clients.get(i))!=0)
                    {
                        if(clientCharacters.get(clients.get(i)).equalsIgnoreCase("Citizen")||clientCharacters.get(clients.get(i)).equalsIgnoreCase("Mafia")||clientCharacters.get(clients.get(i)).equalsIgnoreCase("DieHard"))
                            clients.get(i).sendForThisClient("Wait for Other Players");
                        if(clientCharacters.get(clients.get(i)).equalsIgnoreCase("Sniper"))
                        {
                            clients.get(i).sendForThisClient("Choose A Target:");
                            for(int j=0;j<clients.size();j++)
                                if(life.get(clients.get(j))!=0)
                                    clients.get(i).sendForThisClient(names.get(clients.get(j)));
                        }
                        if(clientCharacters.get(clients.get(i)).equalsIgnoreCase("Psychologist"))
                        {
                            clients.get(i).sendForThisClient("Choose A Target:");
                            for(int j=0;j<clients.size();j++)
                                if(life.get(clients.get(j))!=0)
                                    clients.get(i).sendForThisClient(names.get(clients.get(j)));
                        }
                        if(clientCharacters.get(clients.get(i)).equalsIgnoreCase("Godfather"))
                        {
                            clients.get(i).sendForThisClient("Choose A Target:");
                            for(int j=0;j<citizens.size();j++)
                                if(life.get(citizens.get(j))!=0)
                                    clients.get(i).sendForThisClient(names.get(citizens.get(j)));
                        }
                        if(clientCharacters.get(clients.get(i)).equalsIgnoreCase("Dr.Lecter"))
                        {
                            clients.get(i).sendForThisClient("Choose A Target:");
                            for(int j=0;j<mafias.size();j++)
                                if(life.get(mafias.get(j))!=0)
                                    clients.get(i).sendForThisClient(names.get(mafias.get(j)));
                        }
                        if(clientCharacters.get(clients.get(i)).equalsIgnoreCase("Doctor"))
                        {
                            clients.get(i).sendForThisClient("Choose A Target:");
                            for(int j=0;j<clients.size();j++)
                                if(life.get(clients.get(j))!=0)
                                    clients.get(i).sendForThisClient(names.get(clients.get(j)));
                        }
                        if(clientCharacters.get(clients.get(i)).equalsIgnoreCase("Detector"))
                        {
                            clients.get(i).sendForThisClient("Choose A Target:");
                            for(int j=0;j<clients.size();j++)
                                if(life.get(clients.get(j))!=0)
                                    clients.get(i).sendForThisClient(names.get(clients.get(j)));
                        }
                    }
                while (true) {
                    if (!Server.startGame())
                        break;
                    System.out.print("");
                }
                nightDeaths();
                statistics();
            }
        }
    }

    private static void nightDeaths() {
        if(!doctorsHeal.equals(mafiasShot))
        {
            nightDeaths.add(mafiasShot);
            life.put(mafiasShot,life.get(mafiasShot)-1);
        }
        if(lectersHeal.equals(!snipersShot.equals(lectersHeal)))
        {
            nightDeaths.add(snipersShot);
            life.put(snipersShot,life.get(snipersShot)-1);
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
        if (chara.equalsIgnoreCase("Doctor"))
            rolesList.add(new Doctor(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("Dr.Lecter"))
            rolesList.add(new DrLecter(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("DieHard"))
            rolesList.add(new DieHard(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("Sniper"))
            rolesList.add(new Sniper(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("Citizen"))
            rolesList.add(new SimpleCitizen(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("Mafia"))
            rolesList.add(new SimpleMafia(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("GodFather"))
            rolesList.add(new GodFather(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("Detector"))
            rolesList.add(new Detector(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("Mayor"))
            rolesList.add(new Mayor(chara, thread, names.get(thread)));
        if (chara.equalsIgnoreCase("Psychologist"))
            rolesList.add(new Psychologist(chara, thread, names.get(thread)));
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

    public static ClientManager checkWhoseOut() throws IOException {
        ClientManager manOut, samePlayer = clients.get(0);
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
            return null;
        }

        clients.get(0).sendToAll(names.get(manOut) + " Can Be out If Mayor Give Permission.");
        return manOut;
    }

    public static boolean validVote(String vote, String name) {
        if (vote.equalsIgnoreCase(name))
            return false;
        for (int i = 0; i < clients.size(); i++)
            if (names.get(clients.get(i)).equalsIgnoreCase(vote) && life.get(clients.get(i)) != 0)
                return true;
        return false;
    }

    public static boolean validMafiaKill(String vote) {
        for (int i = 0; i < mafias.size(); i++)
            if (names.get(mafias.get(i)).equalsIgnoreCase(vote))
                return false;
        for (int i = 0; i < clients.size(); i++)
            if (names.get(clients.get(i)).equalsIgnoreCase(vote) && life.get(clients.get(i)) != 0)
                return true;
        return false;
    }

    public static void kill(ClientManager client) {
        life.put(client, 0);
    }
    public static void killSniper(ClientManager player) {
        life.put(player, 0);
        nightDeaths.add(player);
    }
    public static void statistics() throws IOException {
        int aliveMafia = 0, aliveCitizen = 0;
        for (int i = 0; i < mafias.size(); i++)
            if (life.get(mafias.get(i)) != 0)
                aliveMafia++;
        for (int i = 0; i < citizens.size(); i++)
            if (life.get(citizens.get(i)) != 0)
                aliveCitizen++;
            statistics="We Have " + aliveCitizen + " Citizens & " + aliveMafia + " Mafias Alive.";
            tellStatistics=true;
    }

    public static String playerCharacter(ClientManager player) {
        return clientCharacters.get(player);
    }

    public static void setSilentMan(ClientManager SilentMan) {
        silentMan = SilentMan;
    }

    public static void checkForDetector(ClientManager detector, ClientManager player1) throws IOException {
        if(citizens.contains(player1))
        {
            detector.sendForThisClient("He Is A Citizen.");
            return;
        }
        for (int i=0;i<rolesList.size();i++)
          if(rolesList.get(i) instanceof GodFather)
              if(rolesList.get(i).name.equalsIgnoreCase(names.get(player1)))
              {
                  detector.sendForThisClient("He Is A Citizen.");
                  return;
              }
        detector.sendForThisClient("He Is A Mafia.");
              return;

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

    public static ArrayList<Roles> getRolesList() {
        return rolesList;
    }

    public static ClientManager getSilentMan() {
        return silentMan;
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

    public static void setMafiasShot(ClientManager mafiasShot) {
        Server.mafiasShot = mafiasShot;
    }

    public static void setDoctorsHeal(ClientManager doctorsHeal) {
        Server.doctorsHeal = doctorsHeal;
    }

    public static void setSnipersShot(ClientManager snipersShot) {
        Server.snipersShot = snipersShot;
    }

    public static void setLectersHeal(ClientManager lectersHeal) {
        Server.lectersHeal = lectersHeal;
    }

    public static void setTellStatistics(boolean tellStatistics) {
        Server.tellStatistics = tellStatistics;
    }



}
