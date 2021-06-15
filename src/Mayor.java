public class Mayor extends Citizens{
    public Mayor(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    int chances=2;
    @Override
    public void action(ClientManager player) {
        chances--;
     Server.kill(player);
    }

    public int getChances() {
        return chances;
    }
}
