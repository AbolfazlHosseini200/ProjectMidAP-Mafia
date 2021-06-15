public class GodFather extends Mafia{
    public GodFather(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    @Override
    public void action(ClientManager player) {
      Server.setMafiasShot(player);
    }
}
