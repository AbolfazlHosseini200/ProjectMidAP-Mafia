public class Sniper extends Citizens {
    private int shots = 2;

    public Sniper(String character, ClientManager player) {
        super(character, player);
    }

    @Override
    public void action(ClientManager player) {
        shots--;
        if (Server.playerCharacter(player).equalsIgnoreCase("Dr.Lecter") || Server.playerCharacter(player).equalsIgnoreCase("GodFather") || Server.playerCharacter(player).equalsIgnoreCase("Mafia")) {
         Server.kill(player);
        }
        else
            Server.kill(this.player);
    }

    public int getShots() {
        return shots;
    }
}
