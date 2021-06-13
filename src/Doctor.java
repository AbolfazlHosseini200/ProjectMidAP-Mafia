public class Doctor extends Citizens{
    public Doctor(String character,ClientManager player)
    {
        super(character,player);
    }
    @Override
    public void action(ClientManager player) {
Server.setSafeCitizen(player);
    }
}
