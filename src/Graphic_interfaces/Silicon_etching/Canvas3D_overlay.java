/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Graphic_interfaces.Silicon_etching;

/**
 *
 * @author nestor
 */

import javax.media.j3d.*;
import java.awt.*;



//Esto es un simple canvas3D: elemento donde se renderiza el universo creado con Java3D
//Tiene operaciones de post-Render para dibujar el GUI por encima del entorno 3D.

public class Canvas3D_overlay extends Canvas3D {


    private String  overlayText="Silicon etching KMC viewer 1.0";

    
    public void setOverlayText(String p){
        overlayText=p;this.postRender();
    }



    public Canvas3D_overlay(java.awt.GraphicsConfiguration graphicsConfiguration){
        super(graphicsConfiguration);
    }


    @Override
    public void postRender() {
        super.postRender();

        J3DGraphics2D g2d= getGraphics2D();
               try{
       postRenderActions(g2d);
               }catch(Exception e){}
       g2d.flush(true);

}



private void postRenderActions(Graphics2D g2d){


      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);


      g2d.drawString(overlayText, 5, 15);
      

}


}
