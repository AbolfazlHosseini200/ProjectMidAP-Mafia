import java.io.IOException;

public class DieHard extends Citizens{
    public DieHard(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    private int chances=2;
    @Override
    public void action(ClientManager player) throws IOException {
    chances--;
     Server.statistics();
    }
    public int getChances() {
        return chances;
    }
}
