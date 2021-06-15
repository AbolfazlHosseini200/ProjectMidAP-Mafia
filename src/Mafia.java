public abstract class Mafia extends Roles{
    public Mafia(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    public abstract void action(ClientManager player);
}
