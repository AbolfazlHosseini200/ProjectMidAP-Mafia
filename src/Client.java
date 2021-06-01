import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
public class Client {
    static String name;
    static String character;
    public static void main(String[] args) throws IOException {
        Socket client=new Socket("localhost",8585);
        DataInputStream dataInputStream=new DataInputStream(client.getInputStream());
        DataOutputStream dataOutputStream=new DataOutputStream(client.getOutputStream());

        System.out.print("Welcome To Our Game\nEnter Your Name:");
        Scanner scanner=new Scanner(System.in);
        name=scanner.next();
        dataOutputStream.writeUTF(name);
        String bool=dataInputStream.readUTF();
        //System.out.println("check");
        while (bool.equals("1"))
        {
            System.out.println("This Name Has Been Chosen Before\n Enter New Name:");
            name=scanner.next();
            dataOutputStream.writeUTF(name);
            bool=dataInputStream.readUTF();
        }
        System.out.println("Enter \"Ready\" When Your Ready!!!");
        String ready=scanner.next();
        while(!ready.equalsIgnoreCase("ready"))
        {
            ready=scanner.next();
        }
        dataOutputStream.writeUTF("ready");
        System.out.println("Waiting For Other Players...");
        character=dataInputStream.readUTF();
        System.out.println("Your Character Is "+character);
        scanner.next();
    }
}
