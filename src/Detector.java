import java.io.IOException;

public class Detector extends Citizens{
    public Detector(String character,ClientManager player,String name)
    {
        super(character,player,name);
    }
    @Override
    public void action(ClientManager player) throws IOException {
          Server.checkForDetector(this.player,player);
    }
}
