public class DrLecter extends Mafia{
    public DrLecter(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    @Override
    public void action(ClientManager player) {
      Server.setLectersHeal(player);
    }
}
