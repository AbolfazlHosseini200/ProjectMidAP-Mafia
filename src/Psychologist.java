public class Psychologist extends Citizens{
    public Psychologist(String character,ClientManager player)
    {
        super(character,player);
    }
    @Override
    public void action(ClientManager player) {
Server.setSilentMan(player);
    }
}
