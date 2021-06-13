import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
public class Client {
    static String name;
    static String character;
    static String phase="Day";
    public static void main(String[] args) throws IOException {
        Socket client=new Socket("localhost",8585);
        Socket clientReader=new Socket("localhost",8585);
        DataInputStream dataInputStream=new DataInputStream(client.getInputStream());
        DataOutputStream dataOutputStream=new DataOutputStream(client.getOutputStream());
        DataInputStream dataInputStream2=new DataInputStream(clientReader.getInputStream());
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
        Thread chat=new Chats(dataInputStream2);
        chat.start();
        while (true)
        {
            System.out.println("Its Day Now And You Can Chat As "+name);

            Date startDate = new Date();
            Date endDate = new Date();
            dataOutputStream.flush();
            while (((int)((endDate.getTime() - startDate.getTime()) / 1000))<60)
            {
             dataOutputStream.writeUTF(scanner.nextLine());
                endDate = new Date();
            }
            phase="Vote";
            if (phase.equalsIgnoreCase("Vote"))
            {
                startDate = new Date();
                endDate = new Date();
                String vote = "";
                    while (!vote.equalsIgnoreCase("done") &&((int) ((endDate.getTime() - startDate.getTime()) / 1000)) < 40) {
                        vote = scanner.next();
                        dataOutputStream.writeUTF(vote);
                        endDate=new Date();
                    }
            }
            while (phase.equalsIgnoreCase("Night"))
            {
                   System.out.println(" ");
            }
        }
    }
}
