public class Psychologist extends Citizens{
    public Psychologist(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    @Override
    public void action(ClientManager player) {
Server.setSilentMan(player);
    }
}
