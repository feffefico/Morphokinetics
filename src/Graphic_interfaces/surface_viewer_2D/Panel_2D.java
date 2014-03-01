/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * dibujaKMC.java
 *
 * Created on 31-ene-2012, 12:47:47
 */
package Graphic_interfaces.surface_viewer_2D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import utils.MathUtils;

/**
 *
 * @author U010531
 */
public class Panel_2D extends javax.swing.JPanel {

    public static int COLOR_BW=0;
    public static int COLOR_HSV=1;
    
    protected float[][] PSD;
    protected double max;
    protected double min;
    protected boolean log = false;
    protected boolean shift = false;
    protected boolean auto=true;
    protected int colormap =COLOR_HSV;
    
    protected String text_info="This is a 2D surface";



    /**
     * Creates new form dibujaKMC
     */
    public Panel_2D() {
        initComponents();
    }

    public void setPSD(float[][] PSD) {
        this.PSD = PSD;
        if (auto)
            get_min_max(PSD);
        this.repaint();
    }
    
    public void setLogScale(boolean log) {
        this.log = log;
        if (PSD != null && auto) {
            get_min_max(PSD);
        }
        this.repaint();
    }
    
    public void setText_info(String text_info) {
        this.text_info = text_info;
        this.repaint();
    }
    
    public void setColormap(int colormap){
        this.colormap=colormap;
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
       this.auto=false;
       this.repaint();
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
       this.min = min;
       this.auto=false;
       this.repaint();
        
    }
    
    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
        if (auto)  {
            get_min_max(PSD);
            this.repaint();
        }
            
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (PSD != null) {

            float scaleY = this.getHeight() / (float) PSD.length;
            float scaleX = this.getWidth() / (float) PSD[0].length;
            ((Graphics2D) g).scale(scaleX, scaleY);


            for (int i = 0; i < PSD.length; i++) {
                for (int j = 0; j < PSD[0].length; j++) {

                    double temp = PSD[i][j];
                    if (log) {
                        temp = Math.log(temp);
                    }
                    setColor(temp, g);
                    
                    int posX = i;
                    int posY = j;
                    if (shift) {
                        posX = (posX + PSD[0].length / 2) % PSD[0].length;
                        posY = (posY + PSD.length / 2) % PSD.length;
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
            g.drawString(text_info, 2, 14);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
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

    private void get_min_max(float[][] PSD) {

        if (log) {
            min = max = Math.log(PSD[0][0]);
        } else {
            min = max = PSD[0][0];
        }
        for (int i = 0; i < PSD.length; i++) {
            for (int j = 0; j < PSD[0].length; j++) {
                double temp = PSD[i][j];
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
        float normalizedValue = (float)((temp - min) / (max - min));
        normalizedValue=Math.min(Math.max(normalizedValue, 0), 1);
        
        if (colormap==COLOR_BW)         
            g.setColor(new Color(normalizedValue,normalizedValue,normalizedValue));
        else{
            double hue = (2.0 / 3.0) - normalizedValue * (2.0 / 3.0);
            g.setColor(Color.getHSBColor((float) hue, 1.0f, 1.0f));
        }
    }
}
