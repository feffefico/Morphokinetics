/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Locale;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.CatalysisAtom;
import static kineticMonteCarlo.atom.CatalysisAtom.CO;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ActivationEnergy {
  /**
   * Attribute to count processes that happened. Used to compute activation energy per each rate.
   */
  private int[][] histogramSuccess;
  private double[][] histogramPossible;
  private long[][] histogramPossibleCounter;
  private double[][] histogramPossibleTmp;
  private long[][] histogramPossibleCounterTmp;
  private final ArrayList<AbstractAtom> surface;
  private final boolean aeOutput;
  private boolean doActivationEnergyStudy;
  private double previousProbability;
  private int length;
  private int numberOfNeighbours;
  private double[][] rates;

  public ActivationEnergy(Parser parser) {
    surface = new ArrayList();
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    doActivationEnergyStudy = false;
    if (aeOutput) {
      if (parser.getCalculationMode().equals("basic")) {
        doActivationEnergyStudy = true;
        length = 4;
        numberOfNeighbours = 4;
      }
      if (parser.getCalculationMode().equals("graphene")) {
        doActivationEnergyStudy = true;
        if (parser.getRatesLibrary().equals("GaillardSimple")) {
          length = 4;
          numberOfNeighbours = 3;
        } else {
          length = 8;
          numberOfNeighbours = 12;
        }
      }
      if (parser.getCalculationMode().equals("Ag") || parser.getCalculationMode().equals("AgUc")) {
        doActivationEnergyStudy = true;
        length = 7;
        numberOfNeighbours = 6;
      }
      if (parser.getCalculationMode().equals("catalysis")){
        doActivationEnergyStudy = true;
        length = 2;
        numberOfNeighbours = 4;
      }
      histogramPossible = new double[length][length];
      histogramPossibleCounter = new long[length][length];
      histogramPossibleTmp = new double[length][length];
      histogramPossibleCounterTmp = new long[length][length];
      histogramSuccess = new int[length][length];
    }
    previousProbability = 0;
  }
  
  public void setRates(double[][] rates) {
    this.rates = rates;
  }
  
  /**
   * Computes possibles for catalysis.
   * 
   * @param surface
   * @param elapsedTime 
   */
  public void updatePossibles(ListIterator<CatalysisAtom> surface, double elapsedTime) {
    if (doActivationEnergyStudy) {
      // iterate over all atoms of the surface to get all possible hops (only to compute multiplicity)
      
      histogramPossibleTmp = new double[length][length];
      while (surface.hasNext()) {
        CatalysisAtom atom = surface.next();
        for (int pos = 0; pos < numberOfNeighbours; pos++) {
          CatalysisAtom neighbour = atom.getNeighbour(pos);
          if (atom.getType() == neighbour.getType() || !neighbour.isOccupied()) {
            continue;
          }
          // [CO^BR][O^BR], [CO^BR][O^CUS], [CO^CUS][O^BR], [CO^CUS][O^CUS]
          if (atom.getType() == CO) {
            histogramPossible[atom.getLatticeSite()][neighbour.getLatticeSite()] += elapsedTime / 2.0;
            //histogramPossibleCounter[atom.getLatticeSite()][neighbour.getLatticeSite()]++;
            histogramPossibleTmp[atom.getLatticeSite()][neighbour.getLatticeSite()] += 0.5;
          } else {
            histogramPossible[neighbour.getLatticeSite()][atom.getLatticeSite()] += elapsedTime / 2.0;
            //histogramPossibleCounter[neighbour.getLatticeSite()][atom.getLatticeSite()]++;
            histogramPossibleTmp[neighbour.getLatticeSite()][atom.getLatticeSite()] += 0.5;
          }
        }
      }
      // it is counting twice each reaction, so dividing by 2
      for (int i = 0; i < histogramPossibleCounter.length; i++) {
        for (int j = 0; j < histogramPossibleCounter[0].length; j++) {
          histogramPossibleCounter[i][j] += histogramPossibleTmp[i][j];
        }
      }
    }
  }
  
  public void updatePossibles(ListIterator<AbstractAtom> surface, double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy) {
      if (previousProbability != totalAndDepositionProbability) {
        histogramPossibleTmp = new double[length][length];
        histogramPossibleCounterTmp = new long[length][length];
        // iterate over all atoms of the surface to get all possible hops (only to compute multiplicity)
        while (surface.hasNext()) {
          AbstractGrowthAtom atom = (AbstractGrowthAtom) surface.next();
          for (int pos = 0; pos < numberOfNeighbours; pos++) {
            AbstractGrowthAtom neighbourAtom = atom.getNeighbour(pos);
            byte destination = neighbourAtom.getTypeWithoutNeighbour(pos);
            byte origin = atom.getRealType();
            if (atom.getBondsProbability(pos) > 0) {
              histogramPossible[origin][destination] += elapsedTime;
              histogramPossibleCounter[origin][destination]++;
              histogramPossibleTmp[origin][destination] += elapsedTime;
              histogramPossibleCounterTmp[origin][destination]++;
            }
          }
        }
        previousProbability = totalAndDepositionProbability;
      } else { // Total probability is the same as at the previous instant, so multiplicities are the same and we can use cached data
        for (int i = 0; i < length; i++) {
          for (int j = 0; j < length; j++) {
            histogramPossible[i][j] += histogramPossibleTmp[i][j];
            histogramPossibleCounter[i][j] += histogramPossibleCounterTmp[i][j];
          }
        }
      }
    }
  }

  public void updateSuccess(int oldType, int newType) {
    histogramSuccess[oldType][newType]++;
  }
  
  public void update(ArrayList<AbstractAtom> surface)  {
    
  }
  
  public void reset() {
    surface.clear();
    previousProbability = 0;
    if (aeOutput) {
      histogramPossible = new double[length][length];
      histogramPossibleCounter = new long[length][length];
      histogramPossibleTmp = new double[length][length];
      histogramPossibleCounterTmp = new long[length][length];
      histogramSuccess = new int[length][length];
    }
  }
  
  public void printAe(PrintWriter print, float coverage) {
    boolean printLineBreak = (coverage == -1);
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%f %s", coverage, "AeInstananeousDiscrete ");
    for (int origin = 0; origin < histogramPossibleCounterTmp.length; origin++) {
      if (printLineBreak) print.print("AeInstananeousDiscrete ");
      for (int destination = 0; destination < histogramPossibleCounterTmp[0].length; destination++) {
        print.print(histogramPossibleCounterTmp[origin][destination] + " ");
      }
      if (printLineBreak) print.println();
    }
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AeSuccess ");
    for (int origin = 0; origin < histogramSuccess.length; origin++) {
      if (printLineBreak) print.print("AeSuccess ");
      for (int destination = 0; destination < histogramSuccess[0].length; destination++) {
        print.print(histogramSuccess[origin][destination] + " ");
      }
      if (printLineBreak) print.println();
    }
    //histogramPossible = ((LinearList) getList()).getHistogramPossible();
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AePossibleFromList ");
    for (int origin = 0; origin < length; origin++) {
      if (printLineBreak) print.print("AePossibleFromList ");
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        print.print(histogramPossible[origin][destination] + " ");
      }
      if (printLineBreak) print.println();
    }

    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AePossibleDiscrete ");
    for (int origin = 0; origin < histogramPossibleCounter.length; origin++) {
      if (printLineBreak) print.print("AePossibleDiscrete ");
      for (int destination = 0; destination < histogramPossibleCounter[0].length; destination++) {
        print.print(histogramPossibleCounter[origin][destination] + " ");
      }
      if (printLineBreak) print.println();
    }
    
    double[][] ratioTimesPossible = new double[histogramPossible.length][histogramPossible[0].length];
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AeRatioTimesPossible ");
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      if (printLineBreak) print.print("AeRatioTimesPossible ");
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        ratioTimesPossible[origin][destination] = rates[origin][destination] * histogramPossible[origin][destination];
        print.print(ratioTimesPossible[origin][destination] + " ");
      }
      if (printLineBreak) print.println();
    }
    
    double[][] multiplicity = new double[histogramPossible.length][histogramPossible[0].length];
    if (printLineBreak) print.println("Ae");
    else print.format(Locale.US, "%s%f %s", "\n", coverage, "AeMultiplicity ");
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      if (printLineBreak) print.print("AeMultiplicity ");
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        multiplicity[origin][destination] = histogramSuccess[origin][destination] / ratioTimesPossible[origin][destination];
        print.print(multiplicity[origin][destination] + " ");
      }
      if (printLineBreak) print.println();
    }
    print.println();
    print.flush();
  }
}
