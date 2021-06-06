import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GamingSystem {
    public static boolean checkName(String name) {
        HashMap<ClientManager, String> names =Server.getNames();
        ArrayList<ClientManager> clients =Server.getClients();
        for (int i = 0, j = 0; i < clients.size(); i++) {
            if (names.get(clients.get(i)).equals(name))
                if (j == 1)
                    return true;
                else
                    j++;
        }

        return false;
    }

    public static void setThreadName(ClientManager thread, String name) {
        HashMap<ClientManager, String> names =Server.getNames();
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
        ArrayList<String> characters =Server.getCharacters();
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
        ArrayList<String> characters = Server.getCharacters();
        ArrayList<ClientManager> mafias = Server.getMafias();
        ArrayList<ClientManager> citizens = Server.getCitizens();
        HashMap<ClientManager, Integer> life = Server.getLife();
        HashMap<ClientManager, String> clientCharacters =Server.getClientCharacters();
        HashMap<ClientManager, String> names =Server.getNames();
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
        int ready=Server.getReady();
        ready++;
        Server.setReady(ready);
    }

    public static boolean startGame() {
        int ready=Server.getReady(),playersNumber=Server.getPlayersNumber(),alive=0;
        HashMap<ClientManager, Integer> life = Server.getLife();
        ArrayList<ClientManager> clients = Server.getClients();
        for(int i=0;i<clients.size();i++)
            if(life.get(clients.get(i))!=0)
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
        int playersNumber=Server.getPlayersNumber(),clientsVoted=Server.getClientsVoted();
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

    public static void vote(ClientManager client, String voteList) {
        ArrayList<ClientManager> clients = Server.getClients();
        HashMap<ClientManager, String> names =Server.getNames();
        HashMap<ClientManager, Integer> life = Server.getLife();
        ArrayList<String> votesReport = Server.getVotesReport();
        HashMap<ClientManager, Integer> votes = Server.getVotes();
        int aliveClients = 0;
        for (int i = 0; i < clients.size(); i++)
            if (life.get(clients.get(i)) != 0)
                aliveClients++;
        for (ClientManager aClient : clients) {
            if (voteList.equals(names.get(client))) {
                votesReport.add(names.get(client) + " voted for " + names.get(aClient));
                votes.put(aClient, votes.get(aClient) + 1);
            }
        }
    }
    public static void clientsVoted()
    {
        int clientsVoted=Server.getClientsVoted();
        Server.setClientsVoted(clientsVoted+1);
    }

    public static void checkWhoseOut() throws IOException {
        HashMap<ClientManager, Integer> life =Server.getLife();
        ArrayList<ClientManager> clients = Server.getClients();
        HashMap<ClientManager, Integer> votes =Server.getVotes();
        HashMap<ClientManager, String> names =Server.getNames();
        ClientManager manOut,samePlayer;
        boolean itHasSame=false;
        manOut=clients.get(0);
        for(int i=1;i<clients.size();i++)
        {
            if(votes.get(manOut)< votes.get(clients.get(i)))
            {
                manOut= clients.get(i);
                itHasSame=false;
            }
            else if(votes.get(manOut) == votes.get(clients.get(i)))
            {
                itHasSame=true;
                samePlayer= clients.get(i);
            }
        }
        if(itHasSame)
        {
            samePlayer=manOut;
            clients.get(0).sendToAll("No Ones Out Because "+names.get(manOut)+" & "+names.get(samePlayer)+" Have Same Number Of Votes.");
            return;
        }
        clients.get(0).sendToAll(names.get(manOut)+" Is Out Now.");
        life.put(manOut,0);
    }
}
