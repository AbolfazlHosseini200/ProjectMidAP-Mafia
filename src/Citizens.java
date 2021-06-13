public abstract class Citizens extends Roles{
    public Citizens(String character,ClientManager player)
    {
        super(character,player);
    }
    public abstract void action(ClientManager player);
}
