public abstract class Mafia extends Roles{
    public Mafia(String character,ClientManager player)
    {
        super(character,player);
    }
    public abstract void action(ClientManager player);
}
