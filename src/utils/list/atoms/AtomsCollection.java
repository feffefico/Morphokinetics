/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.list.atoms;

import basic.Parser;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AtomsCollection {
  
  IAtomsCollection atomsCollection;

  public AtomsCollection(Parser parser, byte process) {
    if (parser.isDiffusionFixed()) {
      atomsCollection = new AtomsAvlTree(process);
    } else {
      atomsCollection = new AtomsArrayList(process);
    }
  }
  
  public IAtomsCollection getCollection() {
    return atomsCollection;
  }
}