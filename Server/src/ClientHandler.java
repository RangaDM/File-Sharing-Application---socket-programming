import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private JPanel jPanel;
    private JFrame jFrame;

    int fileId = 0;

    public ClientHandler(Socket socket, JPanel jPanel, JFrame jFrame) {
        this.socket = socket;
        this.jPanel = jPanel;
        this.jFrame = jFrame;
    }

    @Override
    public void run() {
        try {
            // receive data from the client through the socket.
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            // Read the size of the file name
            int fileNameLength = dataInputStream.readInt();

            if (fileNameLength > 0) {
                // Byte array
                byte[] fileNameBytes = new byte[fileNameLength];


                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);

                // Create file name from byte array.
                String fileName = new String(fileNameBytes);


                // Read the size of the content
                int fileContentLength = dataInputStream.readInt();


                if (fileContentLength > 0) {
                    // hold the file data.
                    byte[] fileContentBytes = new byte[fileContentLength];


                    dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);

                    // Add the file to the recieved list
                    synchronized (Server.myFiles) {
                        Server.myFiles.add(new MyFile(fileId, fileName, fileContentBytes, Server.getFileExtension(fileName)));
                        fileId++;
                    }

                    //Panel of showing recived files
                    JPanel jpFileRow = new JPanel();
                    jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.X_AXIS));


                    // Set the file name.
                    JLabel jlFileName = new JLabel(fileName);
                    jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                    jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));


                    if (Server.getFileExtension(fileName).equalsIgnoreCase("txt")) {


                        jpFileRow.setName(String.valueOf(Server.myFiles.size() - 1));
                        jpFileRow.addMouseListener(getMyMouseListener());


                        jpFileRow.add(jlFileName);
                        jPanel.add(jpFileRow);
                        jFrame.validate();
                    } else {

                        // Set the name to be the fileId
                        jpFileRow.setName(String.valueOf(Server.myFiles.size() - 1));

                        // Add a mouse listener to load the download window
                        jpFileRow.addMouseListener(getMyMouseListener());

                        // Add the file to the panel
                        jpFileRow.add(jlFileName);
                        jPanel.add(jpFileRow);

                        jFrame.validate();
                    }
                }
            }


            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private MouseListener getMyMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {


                JPanel jPanel = (JPanel) e.getSource();

                // Get the ID of the file
                int fileId = Integer.parseInt(jPanel.getName());


                // Select a file
                for (MyFile myFile : Server.myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {

            // Download Frame
            JFrame jFrame = new JFrame(" Download the File");
            jFrame.setSize(500, 450);

            // Panel to show files
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

            // Single panel for both labels with the same background color
            JPanel labelPanel = new JPanel();
            labelPanel.setBackground(Color.decode("#cfcfcf"));
            labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

            // Add the "Save the File" label
            JLabel jlTitle = new JLabel("Save the File");
            jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            jlTitle.setFont(new Font("forte", 1, 30));
            jlTitle.setBorder(new EmptyBorder(20, 0, 0, 0));
            labelPanel.add(jlTitle);

            // Add the "Do you want to save the file ..." label
            JLabel jlPrompt = new JLabel("Do you want to save the file " + fileName + "?");
            jlPrompt.setFont(new Font("Arial", 1, 15));
            jlPrompt.setBorder(new EmptyBorder(0, 0, 10, 0));
            jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
            labelPanel.add(jlPrompt);

            // Add the labelPanel to the main panel
            jPanel.add(labelPanel);

            // Yes button
            JButton jbYes = new JButton("Yes");
            jbYes.setPreferredSize(new Dimension(110, 50));
            jbYes.setFont(new Font("Arial", 1, 11));
            jbYes.setBackground(new Color(56, 52, 224));
            // No button
            JButton jbNo = new JButton("No");
            jbNo.setPreferredSize(new Dimension(110, 50));
            jbNo.setFont(new Font("Arial", 1, 11));
            jbNo.setBackground(new Color(227, 19, 11));

            // Label to hold the content of the file whether it be text of images.
            JLabel jlFileContent = new JLabel();
            jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

            //scrollable.
            JScrollPane jDScrollPane = new JScrollPane(jlFileContent);
            jDScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            jDScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            //Button pANEL
            JPanel jpButtons = new JPanel();
            jpButtons.setBorder(new EmptyBorder(20, 0, 20, 0));

            // Add the yes and no buttons.
            jpButtons.add(jbYes);
            jpButtons.add(jbNo);

            // Display the text of a text file
            if (fileExtension.equalsIgnoreCase("txt")) {
                jlFileContent.setText("<html>" + new String(fileData) + "</html>");
            } else {// Display the icon
                jlFileContent.setIcon(new ImageIcon(fileData));
            }

            // Listener to download the file
            jbYes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Create a file
                    File fileToDownload = new File(fileName);
                    try {
                        // Create a stream to write data to the file.
                        FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                        fileOutputStream.write(fileData);
                        fileOutputStream.close();
                        jFrame.dispose();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            });

            // If press No button, close the window
            jbNo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.dispose();
                }
            });

            // Add components to the panel
            jPanel.add(jDScrollPane);
            jPanel.add(jpButtons);

            // Add panel to the frame.
            jFrame.add(jPanel);

            return jFrame;
        }
    }
