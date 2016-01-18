/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 *
 * Created on 31-ene-2012, 12:47:47
 */
package graphicInterfaces.surfaceViewer2D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import utils.MathUtils;

/**
 *
 * @author U010531
 */
public class Panel2D extends javax.swing.JPanel {

  public static int COLOR_BW = 0;
  public static int COLOR_HSV = 1;

  private float[][] psd;
  private double max;
  private double min;
  private boolean log = false;
  private boolean shift = false;
  private boolean auto = true;
  private int colormap = COLOR_HSV;

  private String textInfo = "This is a 2D surface";

  public Panel2D() {
    initComponents();
  }

  public void setPsd(float[][] psd) {
    this.psd = psd;
    if (auto) {
      getMinMax(psd);
    }
    this.repaint();
  }

  public void setLogScale(boolean log) {
    this.log = log;
    if (psd != null && auto) {
      getMinMax(psd);
    }
    this.repaint();
  }

  public void setTextInfo(String textInfo) {
    this.textInfo = textInfo;
    this.repaint();
  }

  public void setColormap(int colormap) {
    this.colormap = colormap;
    this.repaint();
  }

  public void setShift(boolean shift) {
    this.shift = shift;
    this.repaint();
  }

  public double getMax() {
    return max;
  }

  public void setMax(double max) {
    this.max = max;
    this.auto = false;
    this.repaint();
  }

  public double getMin() {
    return min;
  }

  public void setMin(double min) {
    this.min = min;
    this.auto = false;
    this.repaint();

  }

  public boolean isAuto() {
    return auto;
  }

  public void setAuto(boolean auto) {
    this.auto = auto;
    if (auto) {
      getMinMax(psd);
      this.repaint();
    }

  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (psd != null) {

      int binSizeX = psd.length;
      int binSizeY = psd[0].length;
      float scaleY = this.getHeight() / (float) binSizeY;
      float scaleX = this.getWidth() / (float) binSizeX;
      ((Graphics2D) g).scale(scaleX, scaleY);

      for (int i = 0; i < binSizeX; i++) {
        for (int j = 0; j < binSizeY; j++) {

          double temp = psd[i][j];
          if (log) {
            temp = Math.log(temp);
          }
          setColor(temp, g);

          int posX = i;
          int posY = j;
          if (shift) {
            posX = (posX + binSizeX / 2) % binSizeX;
            posY = (posY + binSizeY / 2) % binSizeY;
          }
          g.fillRect(posX, posY, 1, 1);
        }
      }
      ((Graphics2D) g).scale(1 / scaleX, 1 / scaleY);

      g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.66f));
      g.fillRect(0, this.getHeight() - 15, this.getWidth(), 15);
      g.fillRect(0, 0, this.getWidth(), 15);

      g.setColor(Color.black);
      g.drawString("Min:" + MathUtils.truncate(min, 4) + ",   Max:" + MathUtils.truncate(max, 4), 2, this.getHeight() - 2);
      g.drawString(textInfo, 2, 14);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 604, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 976, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables

  private void getMinMax(float[][] psd) {

    if (log) {
      min = max = Math.log(psd[0][0]);
    } else {
      min = max = psd[0][0];
    }
    int binSizeX = psd.length;
    int binSizeY = psd[0].length;
    for (int i = 0; i < binSizeX; i++) {
      for (int j = 0; j < binSizeY; j++) {
        double temp = psd[i][j];
        if (log) {
          temp = Math.log(temp);
        }
        if (temp > max) {
          max = temp;
        }
        if (temp < min) {
          min = temp;
        }
      }
    }
  }

  private void setColor(double temp, Graphics g) {
    float normalizedValue = (float) ((temp - min) / (max - min));
    normalizedValue = Math.min(Math.max(normalizedValue, 0), 1);

    if (colormap == COLOR_BW) {
      g.setColor(new Color(normalizedValue, normalizedValue, normalizedValue));
    } else {
      double hue = (2.0 / 3.0) - normalizedValue * (2.0 / 3.0);
      g.setColor(Color.getHSBColor((float) hue, 1.0f, 1.0f));
    }
  }
}
