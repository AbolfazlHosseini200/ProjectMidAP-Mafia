import java.io.DataInputStream;
import java.io.IOException;

public class Chats extends Thread{
    private DataInputStream in;
    public Chats(DataInputStream dataInputStream)
    {
        in=dataInputStream;
    }

    @Override
    public void run() {
        try {
        while (true)
        {
                System.out.println(in.readUTF());
        }
        } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
