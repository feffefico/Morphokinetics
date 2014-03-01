/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Reinsertion;

import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public class ElitistReinsertion implements IReinsertion {

    public Population Reinsert(Population origin, Population offpring, int substitutions) {

        //we order, just in case    
        origin.order();
        offpring.order();

        for (int i = 0; i < substitutions; i++) {
            origin.setIndividual(offpring.getIndividual(i), origin.size() - 1 - i);
        }

        //we reorder
        origin.order();
        return origin;
    }
}
