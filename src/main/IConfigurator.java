/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import basic.Parser;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public interface IConfigurator {

  public void evolutionarySimulation(Parser parser);
  
  public void psdFromSurfaces(Parser parser);
}