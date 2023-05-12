import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BankersAlgorithm {
    private int numProcesses = 0;
    private int numResources = 0;
    private int[][] allocation;
    private int[][] maxNeed;
    private int[] available;

    public BankersAlgorithm() {
        // Create the main window
        JFrame frame = new JFrame("Banker's Algorithm");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // Add the process and resource input fields
        JLabel processLabel = new JLabel("Number of processes:");
        c.gridx = 0;
        c.gridy = 0;
        inputPanel.add(processLabel, c);
        JTextField processField = new JTextField(10);
        c.gridx = 1;
        c.gridy = 0;
        inputPanel.add(processField, c);

        JLabel resourceLabel = new JLabel("Number of resources:");
        c.gridx = 0;
        c.gridy = 1;
        inputPanel.add(resourceLabel, c);
        JTextField resourceField = new JTextField(10);
        c.gridx = 1;
        c.gridy = 1;
        inputPanel.add(resourceField, c);

        // Add the allocation input fields
        JLabel allocationLabel = new JLabel("Allocation:");
        c.gridx = 0;
        c.gridy = 2;
        inputPanel.add(allocationLabel, c);

        JPanel allocationPanel = new JPanel(new GridLayout(0, 5));
        c.gridx = 1;
        c.gridy = 2;
        inputPanel.add(allocationPanel, c);

        // Add the max need input fields
        JLabel maxNeedLabel = new JLabel("Max Need:");
        c.gridx = 0;
        c.gridy = 3;
        inputPanel.add(maxNeedLabel, c);

        JPanel maxNeedPanel = new JPanel(new GridLayout(0, 5));
        c.gridx = 1;
        c.gridy = 3;
        inputPanel.add(maxNeedPanel, c);

        // Add the available input fields
        JLabel availableLabel = new JLabel("Available:");
        c.gridx = 0;
        c.gridy = 4;
        inputPanel.add(availableLabel, c);

        JPanel availablePanel = new JPanel(new GridLayout(0, 5));
        c.gridx = 1;
        c.gridy = 4;
        inputPanel.add(availablePanel, c);

        // Add the request input fields
        JLabel requestLabel = new JLabel("Request:");
        c.gridx = 0;
        c.gridy = 5;
        inputPanel.add(requestLabel, c);

        JPanel requestPanel = new JPanel(new GridLayout(0, 5));
        c.gridx = 1;
        c.gridy = 5;
        inputPanel.add(requestPanel, c);

        // Add the buttons
        JButton addButton = new JButton("Add");
        c.gridx = 0;
        c.gridy = 6;
        inputPanel.add(addButton, c);

        JButton requestButton = new JButton("Request");
        c.gridx = 1;
        c.gridy = 6;
        inputPanel.add(requestButton, c);

        // Add the output panel
        JTextArea outputArea = new JTextArea(20, 50);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Add the panels to the main window
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(outputScrollPane, BorderLayout.CENTER);

        // Add action listeners to the buttons
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get the number of processes and resources
                    numProcesses = Integer.parseInt(processField.getText());
                    numResources = Integer.parseInt(resourceField.getText());

                    // Create the allocation, max need, and available arrays
                    allocation = new int[numProcesses][numResources];
                    maxNeed = new int[numProcesses][numResources];
                    available = new int[numResources];

                    // Remove the request input fields if they exist
                    requestPanel.removeAll();

                    // Add the allocation input fields
                    for (int i = 0; i < numProcesses; i++) {
                        for (int j = 0; j < numResources; j++) {
                            JTextField field = new JTextField(5);
                            allocationPanel.add(field);
                        }
                    }

                    // Add the max need input fields
                    for (int i = 0; i < numProcesses; i++) {
                        for (int j = 0; j < numResources; j++) {
                            JTextField field = new JTextField(5);
                            maxNeedPanel.add(field);
                        }
                    }

                    // Add the available input fields
                    for (int i = 0; i < numResources; i++) {
                        JTextField field = new JTextField(5);
                        availablePanel.add(field);
                    }

                    // Add the request input fields
                    for (int i = 0; i < numResources; i++) {
                        JTextField field = new JTextField(5);
                        requestPanel.add(field);
                    }

                    // Update the window
                    inputPanel.revalidate();
                    inputPanel.repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        requestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (numProcesses == 0 || numResources == 0) {
                    JOptionPane.showMessageDialog(frame, "Please enter the number of processes and resources", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Get the request vector
                    int[] request = new int[numResources];
                    for (int i = 0; i < numResources; i++) {
                        JTextField field = (JTextField) requestPanel.getComponent(i);
                        request[i] = Integer.parseInt(field.getText());
                    }

                    // Check if the request is valid
                    if (!isSafe(request)) {
                        outputArea.append("Request denied\n");
                    } else {
                        // Update the allocation, max need, and available arrays
                        for (int i = 0; i < numResources; i++) {
                            allocation[numProcesses - 1][i] += request[i];
                            available[i] -= request[i];
                            maxNeed[numProcesses - 1][i] -= request[i];
                        }

                        // Check if the state is still safe
                        if (!isSafe()) {
                            // Roll back the allocation, max need, and available arrays
                            for (int i = 0; i < numResources; i++) {
                                allocation[numProcesses - 1][i] -= request[i];
                                available[i] += request[i];
                                maxNeed[numProcesses - 1][i] += request[i];
                            }

                            outputArea.append("Request denied\n");
                        } else {
                            outputArea.append("Request granted\n");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Display the window
        frame.pack();
        frame.setVisible(true);
    }

    private boolean isSafe(int[] request) {
        int[] work = new int[numResources];
        boolean[] finish = new boolean[numProcesses];

        // Initialize the work and finish arrays
        for (int i = 0; i < numResources; i++) {
            work[i] = available[i] - request[i];
        }

        for (int i = 0; i < numProcesses; i++) {
            finish[i] = false;
        }

        // Find a process that can be finished
        int count = 0;
        while (count < numProcesses) {
            boolean found = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i]) {
                    int j;
                    for (j = 0; j < numResources; j++) {
                        if (maxNeed[i][j] - allocation[i][j] > work[j])
                            break;
                    }
                    if (j == numResources) {
                        for (int k = 0; k < numResources; k++) {
                            work[k] += allocation[i][k];
                        }
                        finish[i] = true;
                        found = true;
                        count++;
                    }
                }
            }
            if (!found) {
                break;
            }
        }

        return count == numProcesses;
    }

    private boolean isSafe() {
        int[] work = new int[numResources];
        boolean[] finish = new boolean[numProcesses];

        // Initialize the work and finish arrays
        for (int i = 0; i < numResources; i++) {
            work[i] = available[i];
        }

        for (int i = 0; i < numProcesses; i++) {
            finish[i] = false;
        }

        // Find a process that can be finished
        int count = 0;
        while (count < numProcesses) {
            boolean found = false;
            for (int i = 0; i < numProcesses; i++) {
                if (!finish[i]) {
                    int j;
                    for (j = 0; j < numResources; j++) {
                        if (maxNeed[i][j] - allocation[i][j] > work[j]) {
                            break;
                        }
                    }
                    if (j == numResources) {
                        for (int k = 0; k < numResources; k++) {
                            work[k] += allocation[i][k];
                        }
                        finish[i] = true;
                        found = true;
                        count++;
                    }
                }
            }
            if (!found) {
                break;
            }
        }
        return count == numProcesses;
    }

    public static void main(String[] args) {
        new BankersAlgorithm();
    }
}