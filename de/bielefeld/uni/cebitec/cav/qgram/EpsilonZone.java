/***************************************************************************
//
// Copyright (c) 2004-2006, Kim Roland Rasmussen <krr@elreki.net>
//
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation files
// (the "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the 
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
// SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
 *   adapted from swift: epsilonzone.*pp by
 *   Peter Husemann
 *   phuseman ät cebitec.uni-bielefeld.de                                     *

 ***************************************************************************/
package de.bielefeld.uni.cebitec.cav.qgram;

/**
 * @author phuseman
 * 
 */
public class EpsilonZone {

	private int threshold_ = 0;
	private int width_ = 0;
	private int height_ = 0;
	private int minimumMatchLength;
	private int qGramSize;
	private double errorrate;

	public EpsilonZone() {
	};

	/**
	 * Helper Class to determine the height and width of the matching buckets as
	 * well as the minimum q-Hits which are required as a necessary matching
	 * criterion.
	 * 
	 * For details see:<br>
	 * K. Rasmussen, J. Stoye, and E. W. Myers.<br>
	 * Efficient q-Gram Filters for Finding All epsilon-Matches over a Given
	 * Length<br>
	 * J. Comp. Biol., 13(2):296-308, 2006.<br>
	 * 
	 * @param minimumMatchLength
	 * @param gramSize
	 * @param errorRate
	 */
	public EpsilonZone(int minimumMatchLength, int gramSize, double errorRate) {
		init(minimumMatchLength, gramSize, errorRate);
	}

	public boolean init(int minimumMatchLength, int gramSize, double errorRate) {
		this.minimumMatchLength = minimumMatchLength;
		this.qGramSize = gramSize;
		this.errorrate = errorRate;
		this.threshold_ = 0;
		this.width_ = 0;
		this.height_ = 0;

		if (minimumMatchLength < 0 || gramSize < 0 || errorRate < 0.0) {
			return false;
		}

		if (errorRate == 0.0) {
			threshold_ = minimumMatchLength - gramSize + 1;
			width_ = 1;
			height_ = minimumMatchLength;
			return true;
		}

		int gramSizeMax = (int) (Math.ceil(1.0 / errorRate));

		if (gramSize >= gramSizeMax) {
			System.err.println("q-Gram size (" + gramSize
					+ ") too big for error rate (" + errorRate
					+ "%); should be <= " + gramSizeMax);
			return false;
		}

		int tau = tau(minimumMatchLength, gramSize, errorRate);
		if (tau <= 0) {
			return false;
		}
		int e = (int) (Math.floor((2 * tau + gramSize - 3)
				/ (1.0 / errorRate - gramSize)));
		int w = tau - 1 + gramSize * (e + 1);

		this.threshold_ = tau;
		this.width_ = e;
		this.height_ = w;

		System.out.println("minmatch:" + minimumMatchLength + " q:" + gramSize
				+ " epsilon:" + errorRate + " -> thresh:" + threshold_
				+ " width:" + width_ + " heig:" + height_);

		return true;
	}

	// public boolean init(int minimumMatchLength, int gramSize, double
	// epsilon);

	/**
	 * For details see:<br>
	 * K. Rasmussen, J. Stoye, and E. W. Myers.<br>
	 * Efficient q-Gram Filters for Finding All epsilon-Matches over a Given
	 * Length<br>
	 * J. Comp. Biol., 13(2):296-308, 2006.<br>
	 * 
	 * Page 299
	 * 
	 * @param n
	 *            is the minimum match length
	 * @param q
	 *            is the q-Gram size
	 * @param epsilon
	 *            is the maximal error rate (n*epsilon are the max. allowed
	 *            errors)
	 * @return
	 */
	public static int U(int n, int q, double epsilon) {
		return (n + 1 - q * ((int) (Math.floor(epsilon * n)) + 1));
	}

	/**
	 * For details see:<br>
	 * K. Rasmussen, J. Stoye, and E. W. Myers.<br>
	 * Efficient q-Gram Filters for Finding All epsilon-Matches over a Given
	 * Length<br>
	 * J. Comp. Biol., 13(2):296-308, 2006.<br>
	 * 
	 * Page 299
	 * 
	 * @param n
	 *            is the minimum match length
	 * @param q
	 *            is the q-Gram size
	 * @param epsilon
	 *            is the maximal error rate (n*epsilon are the max. allowed
	 *            errors)
	 * @return
	 */
	public static int tau(int n, int q, double epsilon) {
		int n0 = n;
		int n1 = (int) (Math.ceil((Math.floor(epsilon * n0) + 1) / epsilon));
		return Math.min(U(n0, q, epsilon), U(n1, q, epsilon));
	}

	/**
	 * Returns true if a valid zone criterion could be established for the
	 * specified construction parameters.
	 */
	public boolean isValid() {
		return threshold_ > 0;
	}

	/**
	 * Returns the minimum required number of q-grams in the epsilon zone.
	 */
	public int getThreshold() {
		return threshold_;
	}

	/**
	 * Returns the width of the epsilon zone.
	 */
	public int getWidth() {
		return width_;
	}

	/**
	 * Returns the height of the epsilon zone.
	 */
	public int getHeight() {
		return height_;
	}

	public int getOverlap() {
		return (int) Math.floor(errorrate * minimumMatchLength);
	}

	/**
	 * The paralellograms can have the dimensions w x (e + delta).
	 * Delta should be greater than e and a power of two, in order to use bitoperations when determining the bucket boundaries.
	 * this funcion gives the exponent of 2, where 2^x is just above e
	 * @return
	 */
	public int getDeltaExponent() {
		return 1+(int) Math.floor(Math.log(width_)/Math.log(2));
	}

	/**
	 * This function gives the delta in the (w x (e+delta)) parallelograms
	 * @return
	 */
	public int getDelta() {
		return (int) Math.pow(2, getDeltaExponent());
	}


	public int getNumberOfZones(int inputSize) {
		int delta= this.getDelta();
		return 1 + (int) inputSize/delta;
//		return (int) Math.ceil( (inputSize-width_-delta)/((double) delta + 1.0) );
	}

}
