public class Doctor extends Citizens{
    public Doctor(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    @Override
    public void action(ClientManager player) {
Server.setSafeCitizen(player);
    }
}
