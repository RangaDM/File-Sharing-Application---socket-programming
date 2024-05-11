import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


// this is the main server class
public class Server {

    // Array list to hold information about the files received.
    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        // Create a new frame.
        JFrame jFrame = new JFrame("Server");// Main container, set the name.
        jFrame.setSize(700, 450);
        String colorCode = "#cfcfcf";
        Color backgroundColor = Color.decode(colorCode);
        jFrame.getContentPane().setBackground(backgroundColor);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// When closing the frame also close the program.


        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        //scrollable.
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Title
        JLabel jlTitle = new JLabel(" File Receiver");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setFont(new Font("forte", 1, 30));
        jlTitle.setBorder(new EmptyBorder(20,0,0,0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        //set file name
        final JLabel jlFileName = new JLabel("Click the file you want to download");
        jlFileName.setFont(new Font("Arial", 1, 15));
        jlFileName.setBorder(new EmptyBorder(0, 0, 10, 0));
        jlFileName.setAlignmentX(0.5F);

        // Add components to the UI.
        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(1234);

        while (true) {
            try {
                Socket socket = serverSocket.accept();

                // Create a new thread to handle the client connection.
                Thread clientThread = new Thread(new ClientHandler(socket, jPanel, jFrame));
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFileExtension(String fileName) {
        // Get the file type

        int i = fileName.lastIndexOf('.');

        if (i > 0) {
            // Set the extension to the extension of the filename.
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }
}
