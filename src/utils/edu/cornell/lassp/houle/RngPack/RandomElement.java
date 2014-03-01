package utils.edu.cornell.lassp.houle.RngPack;

import java.util.*;

//
// RngPack 1.1a by Paul Houle
// http://www.honeylocust.com/RngPack/
//

/**
*   RandomElement is an abstract class that encapsulates random number
* generators.  To base a class on it,  you must define the method 
* <CODE>raw()</CODE> as described below.  It is also likely that you
* will want to define a constructor or another mechanism for seeding the
* the generator.  The other classes defined in <CODE>RandomElement</CODE>
* add value to the numbers generated by <CODE>raw()</CODE>
*
* <P>
* <A HREF="/RngPack/src/edu/cornell/lassp/houle/RngPack/RandomElement.java">
* Source code </A> is available.
* 
* @author <A HREF="http://www.honeylocust.com/"> Paul Houle </A> (E-mail: <A HREF="mailto:paul@honeylocust.com">paul@honeylocust.com</A>)
* @version 1.1a
* 
* @see RandomJava
* @see RandomShuffle
*/

public abstract class RandomElement extends Object implements Cloneable {

    Double BMoutput;                // constant needed by Box-Mueller algorithm

/**
 * The abstract method that must be defined to make a working RandomElement.
 * See the class <CODE>RandomJava</CODE> for an example of how to do this.
 *
 * @see RandomJava
 *
 * @return a random double in the range [0,1]
 */
    
    abstract public double raw();
    
/**
* Fill part or all of an array with doubles.  The method defined here uses multiple calls to
* <CODE>raw()</CODE> to fill the array.  You can eliminate the overhead of
* multiple method calls by subclassing this with a version of the generator
* that fills the array.  On our system this improves the efficiency of
* <CODE>Ranecu</CODE> by 20% when filling large arrays.
*
*
* @param d array to be filled with doubles
* @param n number of doubles to generate
*/     

    public void raw(double d[],int n) {
	for(int i=0;i<n;i++)
	    d[i]=raw();
    }
    
    
/**
* Fill an entire array with doubles.  This method calls <CODE>raw(double d[],int n)</CODE>
* with <CODE>d=d.length</CODE>.  Since this adds little overhead for <CODE>d.length</CODE>
* large,  it is only necessary to override <CODE>raw(double d[],int n)</CODE>
*
* @param d array to be filled with doubles.
*/

    public void raw(double d[]) {
	raw(d,d.length);
    }

/**  
@param hi upper limit of range
@return a random integer in the range 1,2,... ,<STRONG>hi</STRONG>
*/

    public int choose(int hi) {
  	return choose(1,hi);
    }
  
/**
 * @param lo lower limit of range
 * @param hi upper limit of range
 * @return a random integer in the range <STRONG>lo</STRONG>, <STRONG>lo</STRONG>+1, ... ,<STRONG>hi</STRONG>
 */
  
    public int choose(int lo,int hi) {
  	int value=lo + (int) ((hi-lo)*raw());
	if (value>hi)
	    value=hi;          /* otherwise a generator on [0,1] could return
                                  a result outside of the legal range:  you
			          can override this if you know a generator
			          returns [0,1) or (0,1). */
	return value;
    }

/**
@return a boolean that's true 0.5 of the time;  equivalent to coin(0.5).
*/

    public boolean coin() {
	return raw()<=0.5;
    }

/**
@param p probability that function will return true
@return a boolean that's true p of the time.
*/

    public boolean coin(double p) {
	return raw()<=p;
    }

/**
@param lo lower limit of range
@param hi upper limit of range
@return a uniform random real in the range [<STRONG>lo</STRONG>,<STRONG>hi</STRONG>]
*/
    public double uniform(double lo,double hi) {
	return (lo+(hi-lo)*raw());
    }

/** 
gaussian() uses the Box-Muller algorithm to transform raw()'s into
gaussian deviates.

@return a random real with a gaussian distribution,  standard deviation 

*/
 
    public double gaussian() {
	double out,x,y,r,z;
	
	if (BMoutput != null)    
	    {
		out = BMoutput.doubleValue();
		BMoutput = null;
		return(out);
	    };
	
	do {
	    x=uniform(-1,1);
	    y=uniform(-1,1);
	    r=x*x+y*y;
	} while (r >= 1.0);
	
	z=Math.sqrt(-2.0*Math.log(r)/r);
	BMoutput=new Double(x*z);
	return(y*z); 
    }
    

/**
* 
* @param sd standard deviation
* @return a gaussian distributed random real with standard deviation <STRONG>sd</STRONG>
*/

    public double gaussian(double sd) {
	return(gaussian()*sd);
    }

/**
*
* generate a power-law distribution with exponent <CODE>alpha</CODE>
* and lower cutoff
* <CODE>cut</CODE>
* <CENTER>
* </CENTER>
*
*@param alpha the exponent 
*@param cut the lower cutoff
*
*/

    public double powlaw(double alpha,double cut) {
	return cut*Math.pow(raw(), 1.0/(alpha+1.0) ) ;
    }

    public Object clone() throws java.lang.CloneNotSupportedException {
	return super.clone();
    }

};






