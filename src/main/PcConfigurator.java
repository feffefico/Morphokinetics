/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import basic.Parser;
import basic.io.Restart;
import geneticAlgorithm.GeneticAlgorithm;
import geneticAlgorithm.AbstractGeneticAlgorithm;
import geneticAlgorithm.GeneticAlgorithmDcmaEs;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import graphicInterfaces.MainInterface;
import graphicInterfaces.growth.KmcCanvas;
import kineticMonteCarlo.kmcCore.growth.AbstractGrowthKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.AgRatesFromPrbCox;
import ratesLibrary.BasicGrowthSyntheticRates;
import ratesLibrary.SiRatesFromPreGosalvez;
import utils.MathUtils;
import utils.Wait;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class PcConfigurator implements IConfigurator{
  
  private static double simulationTime;
  
  @Override
  public void evolutionarySimulation(Parser parser) {
    AbstractGeneticAlgorithm ga;
    switch (parser.getEvolutionaryAlgorithm()) {
      case "original":
        ga = new GeneticAlgorithm(parser);
        break;
      case "dcma":
        ga = new GeneticAlgorithmDcmaEs(parser);
        break;
      default:
        System.err.println("Error: Default evolutionary algorithm. This evolutionary algorithm is not implemented!");
        System.err.println("Current value: "+parser.getEvolutionaryAlgorithm());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }

    AbstractGrowthKmc myKmc = (AbstractGrowthKmc) ga.getKmc();
    AbstractGrowthLattice myLattice = myKmc.getLattice();
    KmcCanvas myCanvas = new KmcCanvas(myLattice);
    MainInterface mainInterface = null;
    if (parser.withGui()) mainInterface = new MainInterface(myCanvas);
    if (parser.withGui() && parser.visualise()) {
      mainInterface.setVisible(true);
      ga.setMainInterface(mainInterface);
    }
    float[][] experimentalPsd;
    if (parser.getReadReference())
      experimentalPsd = readExperimentalData();
    else {
      experimentalPsd = createExperimentalData(parser, ga);
      ga.setExpectedSimulationTime(simulationTime);
    }
    
    if (parser.withGui() && parser.visualise()) {
      Wait.manyMilliSec(250);
      mainInterface.setExperimentalMesh(experimentalPsd);
      Wait.manyMilliSec(250);
    }
    ga.setExperimentalPsd(experimentalPsd);
    ga.initialise();
    
    ga.iterate();
    printResult(parser, ga);
    if (parser.withGui() && parser.visualise()) mainInterface.setStatusBar("Finished");
  }
  
  @Override
  public void psdFromSurfaces(Parser parser) {
    PsdFromSurfaces psd = new PsdFromSurfaces(parser);
  }
  
  private static float[][] createExperimentalData(Parser parser, AbstractGeneticAlgorithm ga) {
    AbstractPsdEvaluator evaluator = ga.getMainEvaluator();
    evaluator.setRepeats(50);
    
    double[] rates = null;
    double[] energies = null;
    double[] genes = null;
    switch (parser.getCalculationMode()) {
      case "basic":
        rates = new BasicGrowthSyntheticRates().getReduced5Rates(parser.getTemperature());
        energies = new BasicGrowthSyntheticRates().getReduced5Energies();
        break;
      case "Ag":
        //rates = new AgRatesFactory().getRates(parser.getTemperature());
        rates = new AgRatesFromPrbCox().getReduced6Rates(parser.getTemperature());
        energies = new AgRatesFromPrbCox().getReduced6Energies();
        break;
      case "Si":
        rates = new SiRatesFromPreGosalvez().getRates(parser.getTemperature());
        break;
      default:
        System.err.println("Error: Default case calculation mode. This simulation mode is not implemented!");
        System.err.println("Current value: "+parser.getCalculationMode());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    Individual individual;
    switch (parser.getEvolutionarySearchType()) {
      case "rates" :
        genes = rates;
        ga.setHierarchy(rates);
        individual = new Individual(rates);
        break;
      case "energies" :
        genes = energies;
        ga.setHierarchy(energies);
        individual = new Individual(energies);
        break;
      default :
        individual = null;
    }
    float[][] experimentalPsd = evaluator.calculatePsdFromIndividual(individual);
    simulationTime = individual.getSimulationTime();
    System.out.println("Experimental (objective) simulation time " + simulationTime);
    System.out.print("Experimental (objective) genes");
    for (int i = 0; i < genes.length; i++) {
      System.out.print(" " + genes[i]);;
    }
    System.out.println(" ");
    evaluator.setRepeats(parser.getRepetitions());
    return experimentalPsd;
  }
  
  private static float[][] readExperimentalData() {
    Restart restart = new Restart();
    int[] sizes = null;
    float[][] readSurface = null;
    int islandCount = 0;
    int numberOfFiles = 43;
    String surfaceFileName = "psdFromImage/islands/3.OuterIsolatedSmall/island";
    PsdSignature2D psd = null;
    for (int i = 1; i <= numberOfFiles; i++) {
      try {
        readSurface = restart.readSurfaceBinary2D(surfaceFileName + i + ".mko", 2);
        if (psd == null) {
          sizes = new int[2];
          sizes[0] = readSurface.length;
          sizes[1] = readSurface[0].length;
          psd = new PsdSignature2D(sizes[0], sizes[1], 1);
        }
      } catch (Exception e){
        System.err.println("Provided filename [" + surfaceFileName + i + ".mko] does not exist. Exiting");
        //throw e;
        continue;
      }
      islandCount++;
      MathUtils.applyGrowthAccordingDistanceToPerimeter(readSurface);
      psd.addSurfaceSample(readSurface);
    }

    psd.printAvgToFile();
    return psd.getPsd();
  }
  
  private static void printResult(Parser parser, IGeneticAlgorithm ga) {
    Individual individual = ga.getIndividual(0);
    
    System.out.println("These are the results: ");
    System.out.println("Total error is:" + individual.getTotalError());
    System.out.print("Genes: ");
    for (int gene = 0; gene < individual.getGeneSize(); gene++) {
      System.out.print(individual.getGene(gene) + " ");
    }
    System.out.println();
    double kB = 8.617332e-5; // Boltzmann constant
    System.out.print("Energies: ");
    for (int gene = 0; gene < individual.getGeneSize(); gene++) {
      System.out.print(-kB * parser.getTemperature() * Math.log(individual.getGene(gene) / 1e13) + " ");
    }
    System.out.println();
  }
}
