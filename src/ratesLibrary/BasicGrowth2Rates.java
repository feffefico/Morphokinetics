/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static kineticMonteCarlo.atom.BasicGrowthAtom.EDGE;
import static kineticMonteCarlo.atom.BasicGrowthAtom.ISLAND;
import static kineticMonteCarlo.atom.BasicGrowthAtom.KINK;
import static kineticMonteCarlo.atom.BasicGrowthAtom.TERRACE;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowth2Rates extends AbstractBasicGrowthRates{

  /**
   * Same as {@link BasicGrowthSyntheticRates} but with only 3 different energies.
   */
  public BasicGrowth2Rates() {    
    double Ed = 0.100;
    double Ea = 0.400;
    double Einf = 9999999;
    
    double[][] energies = new double[4][4];
    energies[TERRACE][TERRACE] = Ed;
    energies[TERRACE][EDGE] = Ed;
    energies[TERRACE][KINK] = Ed;
    energies[TERRACE][ISLAND] = Ed;

    energies[EDGE][TERRACE] = Einf;
    energies[EDGE][EDGE] = Ea;
    energies[EDGE][KINK] = Ea;
    energies[EDGE][ISLAND] = Ea;

    energies[KINK][TERRACE] = Einf;
    energies[KINK][EDGE] = Einf;
    energies[KINK][KINK] = Einf;
    energies[KINK][ISLAND] = Ea;

    energies[ISLAND][TERRACE] = Einf;
    energies[ISLAND][EDGE] = Einf;
    energies[ISLAND][KINK] = Einf;
    energies[ISLAND][ISLAND] = Einf;
    
    setEnergies(energies);
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. 
   * These values are taken from section 4 of the paper of Cox et al.
   * 
   * (But are not consistent with, for example, the multi-flake
   * simulations: 180K, 250x250)
   * @param temperature
   * @return a double value from 1e-4 to 2e-5
   */
  @Override
  public double getIslandDensity(double temperature) {
    if (temperature < 135) {//120 degrees Kelvin
      return 1e-2;
    }
    if (temperature < 150) {//135 degrees Kelvin
      return 5e-3;
    }
    if (temperature < 165) {//150 degrees Kelvin
      return 4e-3;
    }
    if (temperature < 180) {//165 degrees Kelvin
      return 3e-3;
    }
    return 2e-3; //180 degrees Kelvin
  }
}
