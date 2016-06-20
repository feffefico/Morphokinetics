/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * NewJFrame.java
 *
 * Created on 31-ene-2012, 13:09:43
 */
package graphicInterfaces.growth;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;

public class GrowthKmcFrame extends javax.swing.JFrame {

  private int mouseX, mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private KmcCanvas canvas1;
  private JButton pauseButton;
  private JToggleButton bwButton;
  private JLabel jLabelScale;
  private JPanel jPanel1;
  private JSpinner jSpinnerScale;

  /**
   * Creates new form NewJFrame
   *
   * @param canvas1
   */
  public GrowthKmcFrame(KmcCanvas canvas1) {
    initComponents();
    this.canvas1 = canvas1;
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    jPanel1.add(canvas1);
    canvas1.initialise();
    jSpinnerScale.setValue(((KmcCanvas) canvas1).getScale());
    setResizable(true);
    setSize(canvas1.getSizeX() + 50, canvas1.getSizeY() + 100);

    canvas1.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }

      @Override
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel1MousePressed(evt);
      }
    });
  }

  public void repaintKmc() {
    canvas1.performDraw();
    canvas1.setBaseLocation(mouseX, mouseY);
    mouseX = 0;
    mouseY = 0;
  }

  /**
   * Prints the current canvas to a png image in folder $PWD/results
   *
   * @param i simulation number
   */
  public void printToImage(int i) {
    canvas1.performDrawToImage(i);
  }

  /**
   * Prints the current canvas to a png image in folder $PWD/folder/results
   *
   * @param folder folder to save the current image
   * @param i simulation number
   */
  public void printToImage(String folder, int i) {
    canvas1.performDrawToImage(folder, i);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  private void initComponents() {
    jLabelScale = new JLabel();
    jSpinnerScale = new JSpinner();
    jPanel1 = new JPanel();
    pauseButton = new JButton();
    bwButton = new JToggleButton("B/W");

    JScrollPane scrollPane = new javax.swing.JScrollPane(jPanel1);
    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Morphoniketics");
    setResizable(false);

    jLabelScale.setText("Scale");
    pauseButton.setText("Pause");

    jSpinnerScale.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
    jSpinnerScale.setFocusCycleRoot(true);
    jSpinnerScale.setFocusable(false);
    jSpinnerScale.addChangeListener((javax.swing.event.ChangeEvent evt) -> {
      jSpinnerScaleStateChanged(evt);
    });

    jPanel1.addMouseWheelListener((java.awt.event.MouseWheelEvent evt) -> {
      jPanel1MouseWheelMoved(evt);
    });
    
    jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mousePressed(java.awt.event.MouseEvent evt) {
        jPanel1MousePressed(evt);
      }

      @Override
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        jPanel1MouseReleased(evt);
      }
    });
    pauseButton.addActionListener((java.awt.event.ActionEvent evt) -> {
      pauseButton.setText("Resume");
    });
    bwButton.addActionListener((java.awt.event.ActionEvent evt) -> {
      canvas1.changeBlackAndWhite();
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabelScale)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jSpinnerScale, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(pauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, 80)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(bwButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, 80)
                                    .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap())
    );
    layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelScale)
                            .addComponent(pauseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bwButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSpinnerScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
    );

    pack();
  }

  private void jSpinnerScaleStateChanged(javax.swing.event.ChangeEvent evt) {
    canvas1.setScale((Integer) jSpinnerScale.getValue());
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    setSize(canvas1.getSizeX() + 50, canvas1.getSizeY() + 100);
  }

  private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {
    startMouseX = evt.getX();
    startMouseY = evt.getY();        // TODO add your handling code here:
  }

  private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {
    if (evt.getX() == startMouseX && evt.getY() == startMouseY) {
      canvas1.changeOccupationByHand(startMouseX, startMouseY);
    }
  }

  private void jPanel1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
    int zoom = (Integer) jSpinnerScale.getValue();
    if ((Integer) evt.getWheelRotation() == -1) {
      zoom *= 2;
    } else {
      zoom /= 2;
    }
    if (zoom <= 0) {
      zoom = 1;
    }
    if (zoom >= 32) {
      zoom = 32;
    }
    jSpinnerScale.setValue(zoom);
    canvas1.setScale(zoom);
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    setSize(canvas1.getSizeX() + 50, canvas1.getSizeY() + 100);
  }
}
