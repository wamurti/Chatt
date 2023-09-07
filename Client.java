package MultiChat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame implements ActionListener {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    public static String userName;
    JButton avslutaKnapp = new JButton("Avsluta");
    JTextField textFält = new JTextField("Skriv här");
    JTextArea textArea = new JTextArea(10,15);
    //Private String userName = JOptionPane.showInputDialog(null,"Ange ditt namn:");

    public Client(Socket socket,String userName){
        try{
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //this.userName = userName;
            this.userName = JOptionPane.showInputDialog(null,"Ange ditt namn:");

            JPanel p = new JPanel();
            setTitle(this.userName);
            p.setLayout(new BorderLayout());
            p.add(avslutaKnapp,BorderLayout.NORTH);
            avslutaKnapp.addActionListener(this);
            p.add(textArea,BorderLayout.CENTER);
            p.add(textFält,BorderLayout.SOUTH);
            textFält.addActionListener(this);
            this.add(p);
            this.pack();
            setLocation(300,200);
            setVisible(true);
            setDefaultCloseOperation(EXIT_ON_CLOSE);


        } catch (IOException e){
            closeEverything(socket,in,out);
            //e.printStackTrace();
        }
    }

    public void sendMessage(String ms){
        try{

            out.write(userName);
            out.newLine();
            out.flush();



            //Scanner scanner = new Scanner(System.in);
            if(socket.isConnected()){
                String messageToSend = ms;
                out.write(": "+messageToSend);
                out.newLine();
                out.flush();
            }
        } catch (IOException e){
            closeEverything(socket,in,out);
            //e.printStackTrace();
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            String msgFromGroup;
            while(socket.isConnected()){
                try {
                    msgFromGroup = in.readLine().trim();
                    if(!msgFromGroup.equals(userName)){
                        System.out.println(msgFromGroup);
                        textArea.append(msgFromGroup+"\n");
                    }

                } catch (IOException e){
                    closeEverything(socket,in,out);
                    //e.printStackTrace();
                }
            }
            }
        }).start();

    }

    public void closeEverything(Socket socket,BufferedReader in,BufferedWriter out){

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost",1234);
        Client client = new Client(socket,userName);
        client.listenForMessage();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == avslutaKnapp){
            System.exit(2);
        }

        if(e.getSource() == textFält){
            textArea.append(userName+": "+textFält.getText()+"\n");
            String ms = textFält.getText();
            textFält.setText("");
            sendMessage(ms);


        }



    }
}
