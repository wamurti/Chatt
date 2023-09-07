package MultiChat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); //Array som innehåller klienter att broadcasta meddelanden till
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientUsername;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = in.readLine();        //Väntar på användarnamnet
            clientHandlers.add(this);
            broadCastMessage("SERVER: "+clientUsername+" har anslutit till chatten!\n");
        } catch (IOException e){
            closeEverything(socket,in,out);
        }

    }
    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()){
            try{
                messageFromClient = in.readLine();      //Här stannar och väntar programmet på nytt meddelande, därför viktigt att köra på egen tråd

                if(messageFromClient.equals(clientUsername)){ //Skickar namn först i eget meddelande. sen kommer meddelandet
                    this.clientUsername = messageFromClient;
                } else {
                    broadCastMessage(clientUsername+" "+messageFromClient);
                }



            } catch (IOException e){
                System.out.println("Aha");
                closeEverything(socket,in,out);
                break;

            } catch (NullPointerException e){
                System.out.println("Mhmmmm");
                closeEverything(socket,in,out);
                break;

            }
        }

    }

    public void broadCastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){ //Om användarnamnet inte är sitt egna användarnamn
                    clientHandler.out.write(messageToSend);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();

                }
            } catch (IOException e){
                System.out.println("Vart ärdet");
                closeEverything(socket,in,out);
            }

        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadCastMessage("SERVER: "+clientUsername+" har lämnat chatten \n");
    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out){
        removeClientHandler();
        System.out.println("Klient borttagen...");
        try{
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e){
            System.out.println("Kan det vara hör");
            e.printStackTrace();
        }
    }
}
