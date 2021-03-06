package scripts;


import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Graph;
import scripts.Graph.Vertex;
import scripts.Parser.GraphParser;
import scripts.Pathfinding.Path;
import scripts.Pathfinding.PathGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeveloperGUI extends JFrame {
    private JPanel panel;
    private JLabel vertexdesc;
    private JTextField nearestvertex;
    private JButton selectVertexButton;
    private JButton newVertex;
    private JTextField selectedvertex;
    private JLabel info;
    private JButton vertexjoin;
    private JTextField prefix;
    private JTextField destinationvertex;
    private JButton calculatePathButton;
    private JButton save;

    private Developer dev;
    private ClientContext ctx;
    private Graph graph;

    public DeveloperGUI(final Developer dev) {
        this.dev = dev;
        this.ctx = dev.getCtx();
        this.graph = dev.getGraph();
        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        selectVertexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vertex nearest = graph.getNearestVertex(ctx.players.local().tile());
                dev.setSelected(nearest);
                selectedvertex.setText(nearest.getId());
                String prefixLetters = nearest.getId().replaceAll("[^A-Za-z]+", "");
                prefix.setText(prefixLetters);
            }
        });
        newVertex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vertex selected = dev.getSelected();
                Vertex newVertex = new Vertex(prefix.getText() + graph.nextID(), ctx.players.local().tile(), 3, false);
                newVertex.addEdge(selected);
                selected.addEdge(newVertex);
                graph.addNode(newVertex);
                dev.setSelected(newVertex);
                selectedvertex.setText(newVertex.getId());
            }
        });
        vertexjoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vertex selected = dev.getSelected();
                Vertex nearest = graph.getNearestVertex(ctx.players.local().tile());

                if (!selected.equals(nearest)) {
                    selected.addEdge(nearest);
                    nearest.addEdge(selected);
                } else {
                    JOptionPane.showMessageDialog(panel, "Can't join it self.");
                }
            }
        });
        calculatePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String search_destination = destinationvertex.getText();

                Vertex nearest = graph.getNearestReachableVertex(ctx);
                Vertex destination = null;
                if (search_destination.equals("bank")) {
                    System.out.println("Searching for path to nearest bank");
                    destination = new Vertex("bank", null, -1, true);
                } else {
                    destination = graph.findNode(search_destination);
                }
                if (destination != null) {
                    PathGenerator generator = new PathGenerator(nearest, destination);
                    Path path = generator.compute(ctx);
                    dev.setPath(path);

                } else {
                    System.out.println("Unknown vertex");
                }
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    GraphParser.createXML(graph, "C:\\Users\\Emil\\Rsbot_walkweb\\data\\graph.xml");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void setNearestVertexID(String id) {
        this.nearestvertex.setText(id);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel.add(panel1);
        vertexdesc = new JLabel();
        vertexdesc.setText("Nearest vertex:");
        panel1.add(vertexdesc, BorderLayout.WEST);
        nearestvertex = new JTextField();
        nearestvertex.setEditable(false);
        nearestvertex.setText("test");
        panel1.add(nearestvertex, BorderLayout.CENTER);
        selectVertexButton = new JButton();
        selectVertexButton.setText("Select vertex");
        panel1.add(selectVertexButton, BorderLayout.EAST);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.SOUTH);
        final JLabel label1 = new JLabel();
        label1.setText("Selected Vertex:");
        panel2.add(label1, BorderLayout.NORTH);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel2.add(panel3, BorderLayout.EAST);
        selectedvertex = new JTextField();
        selectedvertex.setEditable(false);
        selectedvertex.setHorizontalAlignment(2);
        panel2.add(selectedvertex, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel2.add(panel4, BorderLayout.SOUTH);
        final JLabel label2 = new JLabel();
        label2.setText("Prefix:");
        panel4.add(label2, BorderLayout.NORTH);
        prefix = new JTextField();
        panel4.add(prefix, BorderLayout.CENTER);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        panel4.add(panel5, BorderLayout.SOUTH);
        final JLabel label3 = new JLabel();
        label3.setText("Walk to vertex:");
        panel5.add(label3, BorderLayout.NORTH);
        destinationvertex = new JTextField();
        panel5.add(destinationvertex, BorderLayout.CENTER);
        calculatePathButton = new JButton();
        calculatePathButton.setText("Calculate path");
        panel5.add(calculatePathButton, BorderLayout.SOUTH);
        info = new JLabel();
        info.setText("Go near the vertex you want to add to. Click select vertex, then walk to desired position for new vertex and click \"Create vertex\".\"Join vertex\" will join two existing vertices together. ");
        panel1.add(info, BorderLayout.NORTH);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel.add(panel6);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        panel6.add(panel7, BorderLayout.NORTH);
        save = new JButton();
        save.setText("Save to XML");
        panel7.add(save, BorderLayout.NORTH);
        vertexjoin = new JButton();
        vertexjoin.setText("Join vertex");
        panel7.add(vertexjoin, BorderLayout.CENTER);
        newVertex = new JButton();
        newVertex.setMaximumSize(new Dimension(50, 32));
        newVertex.setMinimumSize(new Dimension(50, 32));
        newVertex.setText("Create vertex");
        panel7.add(newVertex, BorderLayout.SOUTH);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        panel.add(panel8);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
