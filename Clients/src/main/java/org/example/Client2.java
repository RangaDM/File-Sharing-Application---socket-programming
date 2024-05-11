package org.example;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Client2 {
    public Client2() {
    }

    public static void main(String[] args) {
        final File[] fileToSend = new File[1];
        JFrame jFrame = new JFrame(" Client 02");
        jFrame.setSize(300, 400);
        String colorCode = "#cfcfcf";
        Color backgroundColor = Color.decode(colorCode);
        jFrame.getContentPane().setBackground(backgroundColor);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), 1));
        jFrame.setDefaultCloseOperation(3);

        //create the components
        JLabel Title = new JLabel("File Sender");
        Title.setFont(new Font("forte", 1, 30));
        Title.setBorder(new EmptyBorder(20, 0, 10, 0));
        Title.setAlignmentX(0.5F);
        jFrame.setIconImage(null);
        //set file name
        final JLabel jlFileName = new JLabel("Select a file to send");
        jlFileName.setFont(new Font("Arial", 1, 15));
        jlFileName.setBorder(new EmptyBorder(0, 0, 10, 0));
        jlFileName.setAlignmentX(0.5F);

        //set button
        JPanel Button = new JPanel();
        Button.setBorder(new EmptyBorder(100, 0, 0, 0));

        //create send button
        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(110, 50));
        jbSendFile.setFont(new Font("Arial", 1, 11));
        jbSendFile.setBackground(new Color(54, 235, 102));

        //create choose file button
        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setPreferredSize(new Dimension(110, 50));
        jbChooseFile.setFont(new Font("Arial", 1, 11));
        jbChooseFile.setBackground(new Color(245, 71, 242));
        //add components
        Button.add(jbSendFile);
        Button.add(jbChooseFile);

        //add action listener to choose file button
        jbChooseFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Select a file to send");
                if (jFileChooser.showOpenDialog((Component)null) == 0) {
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("Selected File: " + fileToSend[0].getName());
                }

            }
        });

        //add action listener to send file button
        jbSendFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null) {
                    // if file is not selected
                    jlFileName.setText("Select a file to send");
                } else {
                    try {
                        //set fileInputStream
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());

                        //set host and port
                        Socket socket = new Socket("localhost", 1234);

                        //set dataOutputStream
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        //set file name
                        String fileName = fileToSend[0].getName();
                        byte[] fileNameBytes = fileName.getBytes();
                        byte[] fileBytes = new byte[(int)fileToSend[0].length()];

                        fileInputStream.read(fileBytes);
                        dataOutputStream.writeInt(fileNameBytes.length);
                        dataOutputStream.write(fileNameBytes);
                        dataOutputStream.writeInt(fileBytes.length);

                        dataOutputStream.write(fileBytes);
                    } catch (IOException var8) {
                        var8.printStackTrace();
                    }
                }

            }
        });

        // add components
        jFrame.add(Title);
        jFrame.add(jlFileName);
        jFrame.add(Button);
        jFrame.setVisible(true);
    }
}

