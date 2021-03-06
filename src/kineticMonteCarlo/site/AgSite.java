/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package kineticMonteCarlo.site;

import java.util.Arrays;
import java.util.List;
import javafx.geometry.Point3D;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class AgSite extends AbstractGrowthSite {

  // Redefined atom types
  public static final byte TERRACE = 0;
  public static final byte CORNER = 1;
  public static final byte EDGE_A = 2;
  public static final byte KINK_A = 3;
  public static final byte ISLAND = 4;
  public static final byte EDGE_B = 5;
  public static final byte KINK_B = 6;
  // Before we actually know the value of those, we simply use A type
  public static final byte EDGE = EDGE_A;
  public static final byte KINK = KINK_A;
  
  // Attributes
  private static AgTypesTable typesTable;
  private final AgSite[] neighbours = new AgSite[6];
  /** Number of immobile neighbours. */
  private int nImmobile;
  /** Number of mobile neighbours. */
  private int nMobile;
  /**
   * Position within unit cell.
   */
  private final int pos;
  
  public AgSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 6, 2);
    if (typesTable == null) {
      typesTable = new AgTypesTable();
    }
    nImmobile = 0;
    nMobile   = 0;
    pos = 0;
  }

  /**
   * Constructor for unit cell.
   *
   * @param id atom identifier.
   * @param pos position within the unit cell
   */
  public AgSite(int id, int pos) {
    super(id, 6);
    if (typesTable == null) {
      typesTable = new AgTypesTable();
    }
    this.pos = pos;
    nImmobile = 0;
    nMobile   = 0;
  }

  /**
   * Returns the position within the unit cell.
   *
   * @return coordinates in unit cell
   */
  @Override
  public Point3D getPos() {
    switch (pos) {
      case 0:
        return new Point3D(0, 0, 0);
      case 1:
        return new Point3D(0.5, AbstractGrowthLattice.Y_RATIO, 0);
      default:
        throw new UnsupportedOperationException("Trying to acces to an atom within the unit cell that doesn't exists");
    }
  }

  @Override
  public void setNeighbour(AbstractSurfaceSite a, int pos) {
    neighbours[pos] = (AgSite) a;
  }

  @Override
  public AgSite getNeighbour(int pos) {
    return neighbours[pos];
  }

  @Override
  public List getAllNeighbours() {
    return Arrays.asList(neighbours);
  }
  
  @Override
  public boolean isEligible() {
    return isOccupied() && getType() < KINK_A;
  }

  @Override
  public boolean isPartOfImmobilSubstrate() {
    return isOccupied() && getType() == ISLAND;
  }

  /**
   * 
   * @return the number of immobile neighbours.
   */
  public int getNImmobile() {
    return nImmobile;
  }

  /**
   * 
   * @return the number of mobile neighbours.
   */
  public int getNMobile() {
    return nMobile;
  }

  public void setNImmobile(int nImmobile) {
    this.nImmobile = (byte) nImmobile;
  }

  public void setNMobile(int nMobile) {
    this.nMobile = (byte) nMobile;
  }
  
  /**
   * Adds the given number to the number of mobile neighbours.
   * 
   * @param quantity 
   */
  public void addNMobile(int quantity) {
    nMobile = nMobile + quantity;
  }
  
  /**
   * Adds the given number to the number of immobile neighbours.
   * 
   * @param quantity 
   */
  public void addNImmobile(int quantity) {
    nImmobile = nImmobile + quantity;
  }

  /**
   * Calculates the new atom type when adding or removing a neighbour.
   *
   * @param addToImmobile variation of the number of immobile neighbours. Must be -1, 0 or 1
   * @param addToMobile variation of the number of mobile neighbours. Must be -1, 0 or 1
   * @return new type
   */
  public byte getNewType(int addToImmobile, int addToMobile) {
    return typesTable.getCurrentType(nImmobile + addToImmobile, nMobile + addToMobile);
  }
  
  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    
    super.clear();
    setType(TERRACE);
    nImmobile = nMobile = 0; // current atom has no neighbour
    
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(0, i);
    }
  }

  /**
   * Computes the orientation with respect to the surface, for edges and kinks.
   * <pre>
   * A type: _
   *        \ /
   * B type:
   *        /_\
   * (central area would be island)
   * </pre>
   *
   * @return a number between 0 and 5 inclusive. Even number if A type or odd if B type.
   */
  @Override
  public int getOrientation() {
    return getOrientation(null);
  }

  /**
   * See {@link #getOrientation()}.
   *
   * @param atom atom to be excluded to compute the orientation. If null, none is excluded.
   * @return a number between 0 and 5 inclusive. Even number if A type or odd if B type.
   */
  public int getOrientation(AgSite atom) {
    byte type = getType();
    int neighbourPosition = 0;
    // Create the occupation code shifting the number of positions with the neighbours of the current atom
    int occupationCode = 0;
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      if (neighbours[i].isOccupied() && !neighbours[i].equals(atom)) {
        occupationCode |= (1 << i);
      }
      if (neighbours[i].equals(atom)) {
        neighbourPosition = i;
      }
    }

    if (atom != null) {
      type = getTypeWithoutNeighbour(neighbourPosition);
    }
          
    if (type == EDGE) { // can be either A or B
      return calculateEdgeType(occupationCode);
    }
    if (type == KINK) { // can be either A or B
      return calculateKinkType(occupationCode);
    }
    return -1;
  }

  /**
   * It chooses one neighbour atom randomly.
   * 
   * @return atom to be moved.
   */
  @Override
  public AbstractGrowthSite chooseRandomHop() {
    double linearSearch = StaticRandom.raw() * getProbability();
    double sum = 0;
    int cont = 0;
    while (true) {
      sum += getBondsProbability(cont++);
      if (sum >= linearSearch) {
        break;
      }
      if (cont == getNumberOfNeighbours()) {
        break;
      }
    }
    cont--;

    if (getType() == EDGE_A && neighbours[cont].getType() == CORNER) {
      return aheadCornerAtom(cont);
    }

    return neighbours[cont];
  }

  /**
   * This method tells if two terraces are going to form a dimer. This method should be called from
   * an empty lattice location where atom is going to jump. In affirmative case, generally, it has
   * two terrace neighbours. It only detects step-by-step collisions (it is not valid for Devita or
   * perimeter reentrance).
   *
   * @return true if two terraces are going to be together.
   */
  @Override
  public boolean areTwoTerracesTogether() {
    // Another way to say that it is an unoccupied edge
    if (nMobile != 2 || nImmobile != 0) {
      return false;
    }

    int cont = 0;
    int i = 0;
    while (cont < 2 && i < getNumberOfNeighbours()) {
      if (neighbours[i].isOccupied()) {
        if (neighbours[i].getType() != TERRACE) {
          return false;
        }
        cont++;
      }
      i++;
    }
    return true;
  }

  @Override
  public void obtainRateFromNeighbours() {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(probJumpToNeighbour(1, i), i);
      addProbability(getBondsProbability(i));
    }
  }

  /**
   * 
   * @param pos position of the neighbour.
   * @return change in the probability.
   */
  @Override
  public double updateOneBound(int pos) {
    // Store previous probability
    double probabilityChange = -getBondsProbability(pos);
    // Update to the new probability and save
    setBondsProbability(probJumpToNeighbour(1, pos), pos);
    probabilityChange += getBondsProbability(pos);
    addProbability(probabilityChange);

    return probabilityChange;
  }
 
  /**
   * Returns the real type of the current atom. This means, that is
   * able to determine whether edge or kink is of type A or B. If the
   * orientation number is odd the type is B.
   *
   * @return real type of the current atom.
   */
  @Override
  public byte getRealType() {
    byte type = getType();
    if (type == EDGE_A && (getOrientation() & 1) != 0) {
      return EDGE_B;
    }
    if (type == KINK_A && (getOrientation() & 1) != 0) {
      return KINK_B;
    }
    return type;
  }
    
  @Override
  public double probJumpToNeighbour(int ignored, int position) {
    if (neighbours[position].isOccupied()) {
      return 0;
    }

    byte originType = getRealType();
    byte destination = neighbours[position].getTypeWithoutNeighbour(position);

    // the current atom is an edge and destination is a corner. 
    // We will skip over the corner and go to the next edge (but instead of a corner might be another corner).
    if (getType() == EDGE && destination == CORNER) { //soy un edge y el vecino es un corner, eso significa que podemos girar, a ver a donde
      if (originType == EDGE_A) destination = EDGE_B;
      if (originType == EDGE_B) destination = EDGE_A;
    } else {
      if (destination == EDGE_A && (neighbours[position].getOrientation() & 1) == 0) {
        destination = EDGE_B;
      }
      if (destination == KINK && (neighbours[position].getOrientation() & 1) != 0) {
        destination = KINK_B;
      }
    }
    return getProbability(originType, destination);
  }

  /**
   * Returns the type of the neighbour atom if current one would not exist.
   *
   * @param position this position is the original one; has to be inverted.
   * @return the type.
   */
  @Override
  public byte getTypeWithoutNeighbour(int position) {
    int myPositionForNeighbour = (position + 3) % getNumberOfNeighbours();
    if (!neighbours[myPositionForNeighbour].isOccupied()) return getType(); // impossible to happen

    if (neighbours[myPositionForNeighbour].getType() < KINK_A) {
      // current atom is mobile
      return typesTable.getCurrentType(nImmobile, nMobile - 1);
    } else {
      // current atom is immobile
      return typesTable.getCurrentType(nImmobile - 1, nMobile);
    }
  }
  
  /**
   * This atom is an edge (it has two neighbours). There are 6 possible positions for the edge,
   * depending on its neighbours. In the next "figure" the current atom is [] and the numbers are
   * its neighbours:
   * <pre>
   *    0  1
   *   5 [] 2
   *    4  3
   * </pre> A proper image of the positions is documented here:
   * https://bitbucket.org/Nesferjo/ekmc-project/wiki/Relationship%20between%20Cartesian%20and%20hexagonal%20representations
   *
   * @param code binary code with the occupied neighbours.
   * @return orientation (a number between 0 and 5 inclusive).
   */
  private int calculateEdgeType(int code) {
    switch (code) {
      case 3: //1+2 positions    (0+1 neighbours)  B type in principle \ /
        return 3; //                                                    º   /_      
      case 6: //2+4 positions    (1+2 neighbours)  A type in principle     º 
        return 4;
      case 12: //4+8positions    (2+3 neighbours)  B type in principle  ._ 
        return 5; //                                                     \ 
      case 24: //8+16 positions  (3+4 neighbours)  A type in principle  . 
        return 0; //                                                   / \
      case 48: //16+32 positions (4+5 neighbours)  B type in principle      _.
        return 1; //                                                        /
      case 33: //1+32 positions  (5+0 neighbours)  A type in principle _\
        return 2; //                                                     º 
      default:
        return -1; // Unknown
    }
  }

  /**
   * This atom is a kink (it has three neighbours). There are 6 possible positions for the kink,
   * depending on its neighbours. In the next "figure" the current atom is [] and the numbers are
   * its neighbours:
   * <pre>
   *    0  1
   *   5 [] 2
   *    4  3
   * </pre> A proper image of the positions is documented here:
   * https://bitbucket.org/Nesferjo/ekmc-project/wiki/Relationship%20between%20Cartesian%20and%20hexagonal%20representations
   *
   * @param code binary code with the occupied neighbours.
   * @return orientation (a number between 0 and 5 inclusive).
   */
  private int calculateKinkType(int code) {

    switch (code) {
      case 7:  //1 + 2 + 4   (0+1+2 neighbours)
        return 0;
      case 14: //2 + 4 + 8   (1+2+3 neighbours)
        return 1;
      case 28: //4 + 8 + 16  (2+3+4 neighbours)
        return 2;
      case 56: //8 + 16 + 32 (3+4+5 neighbours)
        return 3;
      case 49: //16 + 32 + 1 (4+5+0 neighbours)
        return 4;
      case 35: //32 + 1 + 2  (5+0+1 neighbours)
        return 5;
      default:
        return -1;
    }
  }

  /**
   * When the current atom's destination is a corner, it jumps over it to the different type edge.
   * 
   * @bug Destination can be any other type (TERRACE, CORNER, same type EDGE or KINK 
   * @param cornerPosition position of the neighbour, from current atom
   * @return other type edge
   */
  private AgSite aheadCornerAtom(int cornerPosition) {
    if ((getOrientation() & 1) != 0) { // B type edge

      switch (cornerPosition) {
        case 0:
          return neighbours[5].getNeighbour(0);
        case 1:
          return neighbours[2].getNeighbour(1);
        case 2:
          return neighbours[1].getNeighbour(2);
        case 3:
          return neighbours[4].getNeighbour(3);
        case 4:
          return neighbours[3].getNeighbour(4);
        case 5:
          return neighbours[0].getNeighbour(5);
      }
    } else { // A type edge

      switch (cornerPosition) { 
        case 0:
          return neighbours[1].getNeighbour(0);
        case 1:
          return neighbours[0].getNeighbour(1);
        case 2:
          return neighbours[3].getNeighbour(2);
        case 3:
          return neighbours[2].getNeighbour(3);
        case 4:
          return neighbours[5].getNeighbour(4);
        case 5:
          return neighbours[4].getNeighbour(5);
      }
    }
    return null;
  }

  /**
   * This should be called when a edge jumps to a corner (instead of
   * expected edge).  It happens when a sharp vertex is found in the
   * island geometry and the atom has to jump over two corners and
   * finish in the same type of edge (or any other type: ISLAND, KINK).
   * 
   * @param cornerPosition position of the neighbour, from current atom
   * @return any other type (EDGE, KINK or BULK)
   */
  private AgSite ahead2CornersAtom(int cornerPosition) {
    if ((getOrientation() & 1) != 0) {

      switch (cornerPosition) {
        case 0:
          return neighbours[5].getNeighbour(0).getNeighbour(4); // Directions: west + northwest + southwest
        case 1:
	  return neighbours[2].getNeighbour(1).getNeighbour(3); // Directions: east + northeast + southeast
        case 2:
          return neighbours[1].getNeighbour(2).getNeighbour(0); // Directions: northeast + east + northwest
        case 3:
          return neighbours[4].getNeighbour(3).getNeighbour(5); // Directions: southwest + southeast + west
        case 4:
          return neighbours[3].getNeighbour(4).getNeighbour(2); // Directions: southeast + southwest + east
        case 5:
          return neighbours[0].getNeighbour(5).getNeighbour(1); // Directions: nortwest + west + northeast
      }
    } else {

      switch (cornerPosition) {
        case 0:
          return neighbours[1].getNeighbour(0).getNeighbour(2); // Directions: northeast + northwest + east
        case 1:
          return neighbours[0].getNeighbour(1).getNeighbour(5); // Directions: northwest + northeast + east
        case 2:
          return neighbours[3].getNeighbour(2).getNeighbour(4); // Directions: southeast + east + southwest
        case 3:
          return neighbours[2].getNeighbour(3).getNeighbour(1); // Directions: east + southeast + northeast
        case 4:
          return neighbours[5].getNeighbour(4).getNeighbour(0); // Directions: west + southwest + northwest
        case 5:
          return neighbours[4].getNeighbour(5).getNeighbour(3); // Directions: southwest + west + southeast
      }
    }
    return null;
  }
}
