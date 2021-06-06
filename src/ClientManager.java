import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ClientManager extends Thread {
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private String clientsVoted = null;
    private Socket client = null;
    private String name;
    private String character;
    private boolean firstTime = false;

    public ClientManager(Socket client) throws IOException {
        this.client = client;
        dataInputStream = new DataInputStream(client.getInputStream());
        dataOutputStream = new DataOutputStream(client.getOutputStream());
    }

    public String getNames() {
        return name;
    }

    @Override
    public void run() {
        try {
            String msg;
            System.out.println("test");
            name = dataInputStream.readUTF();
            System.out.println(name);
            GamingSystem.setThreadName(this, name);

            if (GamingSystem.checkName(name)) {
                while (GamingSystem.checkName(name)) {
                    dataOutputStream.writeUTF("1");
                    name = dataInputStream.readUTF();
                    GamingSystem.setThreadName(this, name);
                }
                dataOutputStream.writeUTF("0");
            } else
                dataOutputStream.writeUTF("0");
            character = GamingSystem.giveCharacter(this);
            dataInputStream.readUTF();
            GamingSystem.ready();
            while (true) {
                if (!GamingSystem.startGame())
                    break;
                System.out.print("");
            }
            dataOutputStream.writeUTF(character);
            while (GamingSystem.canContinuePlaying()) {
                Date startDate = new Date();
                Date endDate = new Date();
                while ((int) ((endDate.getTime() - startDate.getTime()) / 1000) < 60) {
                    msg = dataInputStream.readUTF();
                    sendToAll(this.name, msg);
                    endDate = new Date();
                }
                GamingSystem.ready();
                while (GamingSystem.startGame()) {
                    System.out.print("");
                }
                Server.setPhase("Vote");
                startDate = new Date();
                endDate = new Date();
                while (((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 40) {
                    endDate = new Date();
                    System.out.print("");
                    clientsVoted = dataInputStream.readUTF();
                    if (!GamingSystem.checkName(clientsVoted) || clientsVoted.equalsIgnoreCase(name)) {
                        while (!GamingSystem.checkName(clientsVoted) || clientsVoted.equalsIgnoreCase(name)) {
                            dataOutputStream.writeUTF("1");
                            name = dataInputStream.readUTF();
                        }
                        dataOutputStream.writeUTF("0");
                    } else
                        dataOutputStream.writeUTF("0");
                }
                if (GamingSystem.checkName(clientsVoted))
                    GamingSystem.vote(this, clientsVoted);
                GamingSystem.clientsVoted();
                GamingSystem.ready();
                while (GamingSystem.startGame()) {
                    System.out.print("");
                }
                while (Server.getPhase().equalsIgnoreCase("Night")) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendToAll(String Name, String msg) throws IOException {
        ArrayList<ClientManager> clients = Server.getClients();
        if (firstTime)
            for (int i = 0; i < clients.size(); i++)
                clients.get(i).dataOutputStream.writeUTF(Name + ":" + msg);
        else
            firstTime = true;
    }

    public void sendToAll(String msg) throws IOException {
        ArrayList<ClientManager> clients = Server.getClients();
        for (int i = 0; i < clients.size(); i++)
            clients.get(i).dataOutputStream.writeUTF(msg);
    }

    public void sendForThisClient(String msg) throws IOException {
        this.dataOutputStream.writeUTF(msg);
    }
}
