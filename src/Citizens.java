import java.io.IOException;

public abstract class Citizens extends Roles{
    public Citizens(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    public abstract void action(ClientManager player) throws IOException;
}
