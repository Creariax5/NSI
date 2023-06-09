package org.example;

import org.example.graphObject.Circle;
import org.example.tool.CsvToArray;

import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.*;

public class Main {
    // NSI COMMENTAIRE : création des objets
    JLabel view;
    BufferedImage surface;
    Random random = new Random();

    Hashtable<Integer, Circle> cells = new Hashtable<>();
    Hashtable<String, Integer> IdWithName = new Hashtable<>();

    List<Integer> listCircle = new ArrayList<>();

    // NSI COMMENTAIRE : deuxieme classe principale qui appelle les autres elle est appellée par 'main'
    // (le code ne s'execute pas du haut vers le bas comme en pyton)
    public Main()
    {
        // NSI COMMENTAIRE : création de l'image
        surface = new BufferedImage(1400,1000,BufferedImage.TYPE_INT_RGB);
        view = new JLabel(new ImageIcon(surface));

        // NSI COMMENTAIRE : s'execute toutes les 50 ms
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                // NSI COMMENTAIRE : création de l'image
                Graphics g = surface.getGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0,0,1400,1000);
                g.dispose();

                // NSI COMMENTAIRE : lecture du csv
                String file = "D:\\devProjects\\PycharmProjects\\scraping\\wikipedia\\brain\\csv\\data.csv";
                int i = 0;
                for (String[] row : Objects.requireNonNull(CsvToArray.catchCsv(file))) {
                    addNewElement(row, i);
                    i++;
                }
                for (int j = 0; j < i; j++) {
                    addCircleAtForeground(j);
                }
            }
        };
        Timer timer = new Timer(50, listener);
        timer.start();
    }

    // NSI COMMENTAIRE : création des cercles aux bonnes coordonnées et de la bonne taille
    private void addCircleAtForeground(int id) {
        Graphics g = surface.getGraphics();
        Circle cell = cells.get(id);

        int intX = (int) cell.getX();
        int intY = (int) cell.getY();
        int size = cell.getSize();

        drawNode(intX, intY, size, g);
    }

    // NSI COMMENTAIRE : calcul des forces
    public void addNewElement(String[] row, int id) {
        // NSI COMMENTAIRE : initialisation
        double force = 0.1;

        double x;
        double y;

        double calcX;
        double calcY;
        double Xbase = 0;
        double Ybase = 0;
        // NSI COMMENTAIRE : si le cercle de la page n'a pas été créé
        if (id >= listCircle.size()) {

            // NSI COMMENTAIRE : on le cré à des coo aleatoires
            x = random.nextDouble(1400);
            y = random.nextDouble(1000);

            listCircle.add(id);

            int size = 1;

            // NSI COMMENTAIRE : l'objet 'circle' est mis dans une Hashtable c'est une sorte de dictionnaire
            cells.put(id, new Circle(x, y, size, row, id));
            IdWithName.put(cells.get(id).getRow()[0], id);
            System.out.println(cells.get(id).getRow()[0] + " " + id);

        } else {
            // NSI COMMENTAIRE : si il existe pas
            Circle cell = cells.get(id);

            x = cell.getX();
            y = cell.getY();
            Xbase = x;
            Ybase = y;

            calcX = x - 700;
            calcY = y - 500;
            // NSI COMMENTAIRE : ___ première force (vers le centre) ___
            // calcul de la disctance centre au cercle grace au theoreme de pythagore
            double distanceToC = Math.sqrt(Math.pow(calcX, 2) + Math.pow(calcY, 2));
            // NSI COMMENTAIRE : plus le cercle est éloingné plus la force est forte
            double puissance = Math.pow(distanceToC/100, 1.15);
            if (distanceToC>500) {
                puissance = 100;
            }

            if (distanceToC > 300) {
                // NSI COMMENTAIRE : on ajuste la force et on calcule la position suivante du cercle grace au theoreme de tales
                calcX = (puissance * force * calcX) / distanceToC;
                calcY = (puissance * force * calcY) / distanceToC;

                x = x - calcX;
                y = y - calcY;
            }

            double FcalcX = 0;
            double FcalcY = 0;
            int k = 0;

            int size = 1;

            // NSI COMMENTAIRE : pour chaque cercle
            for (int j : listCircle) {
                calcX = x - 700;
                calcY = y - 500;

                // NSI COMMENTAIRE : on verifi si ce n'est pas le cercle actuel
                if (j != id) {

                    // NSI COMMENTAIRE : ___ deuxieme force (quand les ceclessont trop proches ils se repoussent) ___
                    Circle newCell = cells.get(j);
                    double newX = newCell.getX() - 700;
                    double newY = newCell.getY() - 500;

                    // NSI COMMENTAIRE : calcul de la disctance entre deux cercles grace au theoreme de pythagore
                    double distanceToNew = Math.sqrt(Math.pow(newX - calcX, 2) + Math.pow(newY - calcY, 2));

                    double distanceX = calcX - newX;
                    double distanceY = calcY - newY;


                    // NSI COMMENTAIRE : la puissance de la force d'éloignement est calculée avec une fonction sigmoide
                    // (plus la distance est grande moins la force sera faible)
                    puissance = -(1 / (1 + Math.exp(-(distanceToNew - 0) / 80)) - 1);

                    //System.out.println(distanceX +" "+distanceY);
                    if (distanceToNew < 60) {
                        // NSI COMMENTAIRE : on ajuste la force et on calcule la position suivante du cercle grace au theoreme de tales
                        FcalcX = FcalcX + (newCell.getSize() * 160 * puissance * distanceX) / distanceToNew;
                        FcalcY = FcalcY + (newCell.getSize() * 160 * puissance * distanceY) / distanceToNew;
                    }

                    puissance = Math.pow(distanceToNew, 1);

                    // NSI COMMENTAIRE : ___ troisieme force (vers les liens entre les cecles) ___

                    // NSI COMMENTAIRE : pour chaque lien du cercle
                    for (String link : cell.getRow()) {
                        if (Objects.equals(link, newCell.getRow()[0])) {
                            // NSI COMMENTAIRE : on ajuste la force et on calcule la position suivante du cercle grace au theoreme de tales
                            FcalcX = FcalcX - (newCell.getSize() * 0.01 * puissance * distanceX) / distanceToNew;
                            FcalcY = FcalcY - (newCell.getSize() * 0.01 * puissance * distanceY) / distanceToNew;
                            Graphics g = surface.getGraphics();
                            drawLink((int) cell.getX(), (int) cell.getY(), (int) newCell.getX(), (int) newCell.getY(), g);
                            size = size + 1;
                        }
                    }
                }
                k = k + 1;
            }
            FcalcX = FcalcX / k * force;
            FcalcY = FcalcY / k * force;
            x = x + FcalcX;
            y = y + FcalcY;

            // NSI COMMENTAIRE : si les coo on sufisament changé on met a jour les coo du cercle dans la hashtable
            if (Math.pow(x - Xbase, 2) > 0 || Math.pow(y - Ybase, 2) > 0) {
                cells.put(id, new Circle(x, y, size, row, id));
            } else {
                x = Xbase;
                y = Ybase;
            }


        }

        // NSI COMMENTAIRE : on affiche a l'écrant le cercle a ses nouvelles coo
        int intX = (int) x;
        int intY = (int) y;

        Graphics g = surface.getGraphics();
        // drawNode(intX, intY, g);
        drawArc((int) (intX + (x - Xbase) * 10), (int) (intY + (y - Ybase) * 10), intX, intY, g);

        g.dispose();
        view.repaint();
    }

    // NSI COMMENTAIRE : classe principale qui s'execute en premier
    public static void main(String[] args)
    {
        // NSI COMMENTAIRE : création de l'interface graphique
        Main canvas = new Main();
        JFrame frame = new JFrame();

        // NSI COMMENTAIRE : on met a jour les propiétés
        int vertexes;

        vertexes = 10;
        int canvasSize = vertexes * vertexes;
        frame.setSize(canvasSize, canvasSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(canvas.view);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    // NSI COMMENTAIRE : afficher un cercle
    public void drawNode(int x, int y, int size, Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;

        graphics2D.setColor(Color.WHITE);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.fillOval(x, y, size, size);

        // detection area
        /*graphics2D.setColor(Color.RED);
        graphics2D.drawOval(x-120, y-120, 240, 240)*/;
    }

    // NSI COMMENTAIRE : afficher le vecteur de la force appliquée aux cercles (deplacement a chaque frame *100)
    public void drawArc(int x, int y, int xx, int yy, Graphics g) {
        // translation vector
        Graphics2D graphics2D = (Graphics2D) g;

        graphics2D.setColor(Color.BLUE);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawLine(x + 4, y + 4, xx + 4, yy + 4);
    }

    // NSI COMMENTAIRE : on affiche les liens entre les cercles
    public void drawLink(int x, int y, int xx, int yy, Graphics g) {

        Graphics2D graphics2D = (Graphics2D) g;

        graphics2D.setColor(Color.GRAY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawLine(x + 4, y + 4, xx + 4, yy + 4);
    }
}
