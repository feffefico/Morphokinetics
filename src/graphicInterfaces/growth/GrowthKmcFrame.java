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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

public class GrowthKmcFrame extends javax.swing.JFrame {

  private int mouseX, mouseY;
  private int startMouseX = 0;
  private int startMouseY = 0;
  private boolean paused;
  private KmcCanvas canvas1;
  private JButton pauseButton;
  private JToggleButton bwButton;
  private JLabel jLabelScale;
  private JPanel jPanel1;
  private JSpinner jSpinnerScale;
  private JLabel statusbar;
  private JCheckBoxMenuItem bwMi;

  /**
   * Creates new form NewJFrame
   *
   * @param canvas1
   */
  public GrowthKmcFrame(KmcCanvas canvas1) {
    createMenuBar();
    initComponents();
    this.canvas1 = canvas1;
    canvas1.setSize(canvas1.getSizeX(), canvas1.getSizeY());
    jPanel1.add(canvas1);
    canvas1.initialise();
    jSpinnerScale.setValue(((KmcCanvas) canvas1).getScale());
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
    paused = false;
    jLabelScale.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pause");
    jLabelScale.getActionMap().put("pause", new Pause());
  }

  public void repaintKmc() {
    canvas1.performDraw();
    canvas1.setBaseLocation(mouseX, mouseY);
    mouseX = 0;
    mouseY = 0;
    
    validate();
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
    setResizable(true);
    setMinimumSize(new Dimension(300, 110));

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
    
    pauseButton.addActionListener((java.awt.event.ActionEvent evt) -> {
      pause();
    });
    bwButton.addActionListener((java.awt.event.ActionEvent evt) -> {
      bwMi.setSelected(!bwMi.isSelected());
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
  
  private void pause() {
    paused = !paused;
    canvas1.setPaused(paused);
    if (paused) {
      pauseButton.setText("Resume");
    } else {
      pauseButton.setText("Pause");
    }
  }

  private class Pause extends AbstractAction {

    public Pause() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      pause();
    }
  }
  
  private void createMenuBar() {
    JMenuBar menubar = new JMenuBar();

    ImageIcon iconNew = new ImageIcon("new.png");
    ImageIcon iconOpen = new ImageIcon("open.png");
    ImageIcon iconSave = new ImageIcon("save.png");
    ImageIcon iconExit = new ImageIcon("exit.png");

    JMenu fileMenu = new JMenu("File");
    JMenu viewMenu = new JMenu("View");

    JMenu impMenu = new JMenu("Import");

    JMenuItem newsfMi = new JMenuItem("Import newsfeed list...");
    JMenuItem bookmMi = new JMenuItem("Import bookmarks...");
    JMenuItem mailMi = new JMenuItem("Import mail...");

    impMenu.add(newsfMi);
    impMenu.add(bookmMi);
    impMenu.add(mailMi);

    JMenuItem newMi = new JMenuItem("New", iconNew);
    JMenuItem openMi = new JMenuItem("Open", iconOpen);
    JMenuItem saveMi = new JMenuItem("Save", iconSave);

    JMenuItem exitMi = new JMenuItem("Exit", iconExit);
    exitMi.setToolTipText("Exit application");
    exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));

    exitMi.addActionListener((ActionEvent event) -> {
      System.exit(0);
    });

    fileMenu.add(newMi);
    fileMenu.add(openMi);
    fileMenu.add(saveMi);
    fileMenu.addSeparator();
    fileMenu.add(impMenu);
    fileMenu.addSeparator();
    fileMenu.add(exitMi);
    
    JCheckBoxMenuItem sbarMi = new JCheckBoxMenuItem("Show statubar");
    sbarMi.setMnemonic(KeyEvent.VK_S);
    sbarMi.setDisplayedMnemonicIndex(5);
    sbarMi.setSelected(true);
    
    bwMi = new JCheckBoxMenuItem("B/W output");
    bwMi.setMnemonic(KeyEvent.VK_C);
    bwMi.setDisplayedMnemonicIndex(0);
    bwMi.setSelected(false);
    
    JCheckBoxMenuItem idMi = new JCheckBoxMenuItem("Show atom identifiers");
    idMi.setMnemonic(KeyEvent.VK_S);
    idMi.setDisplayedMnemonicIndex(0);
    idMi.setSelected(true);
        
    JCheckBoxMenuItem islandsMi = new JCheckBoxMenuItem("Show island numbers");
    islandsMi.setMnemonic(KeyEvent.VK_L);
    islandsMi.setDisplayedMnemonicIndex(0);
    islandsMi.setSelected(false);
    
    viewMenu.add(sbarMi);
    viewMenu.add(bwMi);
    viewMenu.add(idMi);
    viewMenu.add(islandsMi);
    menubar.add(fileMenu);
    menubar.add(viewMenu);

    setJMenuBar(menubar);
    
    sbarMi.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          statusbar.setVisible(true);
        } else {
          statusbar.setVisible(false);
        }
      }
    });    
    
    bwMi.addItemListener((ItemEvent e) -> {
      canvas1.changeBlackAndWhite();
      bwButton.setSelected(!bwButton.isSelected());
    });

    idMi.addItemListener((ItemEvent e) -> {
      canvas1.changePrintId();
    });
    
    islandsMi.addItemListener((ItemEvent e) -> {
      canvas1.changePrintIslandNumber();
    });
  }
}
