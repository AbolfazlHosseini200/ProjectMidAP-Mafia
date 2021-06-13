public class DrLecter extends Mafia{
    public DrLecter(String character,ClientManager player)
    {
        super(character,player);
    }
    @Override
    public void action(ClientManager player) {
      Server.setSafeMafia(player);
    }
}
