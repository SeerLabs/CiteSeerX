/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    SpectralClusterer.java
 *    Copyright (C) 2002 Luigi Dragone
 *
 */

import cern.colt.matrix.*;
import cern.colt.*;
import cern.colt.matrix.linalg.*;
import cern.colt.map.*;
import cern.colt.list.*;
import cern.jet.math.*;

import weka.*;
import weka.core.*;
import weka.clusterers.*;

import java.util.*;

/**
 * <p>
 * Spectral clusterer class. For more information see:
 * <ul>
 * <li>Shi, J., and J. Malik (1997) "Normalized Cuts and Image Segmentation",
 * in Proc. of IEEE Conf. on Comp. Vision and Pattern Recognition, Puerto Rico</li>
 * <li>Kannan, R., S. Vempala, and A. Vetta (2000) "On Clusterings - Good, Bad
 * and Spectral", Tech. Report, CS Dept., Yale University.</li>
 * <li>Ng, A. Y., M. I. Jordan, and Y. Weiss (2001) "On Spectral Clustering:
 * Analysis and an algorithm", in Advances in Neural Information Processing
 * Systems 14.</li>
 * </ul>
 * </p>
 * <p>
 * This implementation assumes that the data instances are points in an
 * Euclidean space. The algorithm is based on the concept of similarity between
 * points instead of distance. Given two points <var>x</var> and <var>y</var>
 * and their Euclidean distance <tt>d(x, y)</tt>, their similarity is defined
 * as <tt>exp(- d(x, y)^2 / (2 * sigma^2))</tt>, where <var>sigma</var> is a
 * scaling factor (its default value is 1.0).
 * </p>
 * <p>
 * There is a distance cut factor <var>r</var>, if the distance
 * <tt>d(x, y)</tt> between two points is greater than <var>r</var> then
 * their similarity is set to 0. This parameter combined with the use of sparse
 * matrices can improve the performances w.r.t. the memory.
 * </p>
 * <p>
 * To classify a new instance w.r.t. the partitions found this implementation
 * applies a naive min-distance algorithm that assigns the instance to the
 * cluster that contains the nearest point. Since the distance to similarity
 * function is bijective and monotone the nearest point is also the most similar
 * one.
 * </p>
 * <p>
 * Valid options are:
 * <ul>
 * <li> -A &lt;0-1&gt; <br>
 * Specifies the alpha star factor. The algorithm stops the recursive
 * partitioning when it does not find a cut that has a value below this factor.<br>
 * Use this argument to limit the number of clusters. </li>
 * <li> -S &lt;positive number&gt; <br>
 * Specifies the value of the scaling factor sigma. </li>
 * <li> -R &lt;-1 or a positive number&gt; <br>
 * Specifies the distance cut factor. -1 is equivalent to the positive infinity.
 * </li>
 * <li> -M <br>
 * Requires the use of sparse representation of similarity matrices. </li>
 * </ul>
 * <p>
 * This implementation relies on the COLT numeric package for Java written by
 * Wolfgang Hoschek. For other information about COLT see its home page at <a
 * href="http://nicewww.cern.ch/~hoschek/colt/index.htm">http://nicewww.cern.ch/~hoschek/colt/index.htm</a>.
 * </p>
 * According to the license, the copyright notice is reported below:<br>
 * 
 * <pre>
 *   Written by Wolfgang Hoschek. Check the Colt home page for more info.
 *   Copyright &amp;copy 1999 CERN - European Organization for Nuclear Research.
 *   Permission to use, copy, modify, distribute and sell this software and
 *   its documentation for any purpose is hereby granted without fee,
 *   provided that the above copyright notice appear in all copies and that
 *   both that copyright notice and this permission notice appear in
 *   supporting documentation. CERN makes no representations about
 *   the suitability of this software for any purpose. It is provided &quot;as is&quot;
 *   without expressed or implied warranty.
 * </pre>
 * 
 * </p>
 * <p>
 * This software is issued under GNU General Public License.<br>
 * 
 * <pre>
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * </pre>
 * 
 * </p>
 * 
 * @author Luigi Dragone (<a
 *         href="mailto:luigi@luigidragone.com">luigi@luigidragone.com</a>)
 * @version 1.0
 * 
 * @see <a href="http://nicewww.cern.ch/~hoschek/colt/index.htm">COLT</a>
 */
public class SpectralClusterer extends Clusterer implements OptionHandler {
	/**
	 * The points of the dataset
	 */
	protected DoubleMatrix2D v;
	/**
	 * The class number of each point in the dataset
	 */
	protected int[] cluster;
	/**
	 * The number of different clusters found
	 */
	protected int numOfClusters = 0;
	/**
	 * The alpha star parameter value
	 */
	protected double alpha_star = 0.5;
	/**
	 * The distance cut factor
	 */
	protected double r = -1;
	/**
	 * The sigma scaling factor
	 */
	protected double sigma = 1.0;

	/**
	 * The using sparse matrix flag
	 */
	protected boolean useSparseMatrix = false;

	protected static Vector options = new Vector();
	/**
	 * The static initializer sets up the options vector
	 */
	static {
		options.addElement(new Option("\tAlpha star. (default = 0.5).", "A", 1,
				"-A <0-1>"));
		options.addElement(new Option("\tSigma. (default = 1.0).", "S", 1,
				"-S <num>"));
		options
				.addElement(new Option(
						"\tR. All points that are far away more than this value have a zero similarity. (default = -1).",
						"R", 1, "-R <num>"));
		options.addElement(new Option(
				"\tUse sparse matrix representation. (default = false).", "M",
				0, "-M"));
	}

	/**
	 * Returns the Euclidean distance between two points. It is used to compute
	 * the similarity degree of these ones.
	 * 
	 * @param x
	 *            the first point
	 * @param y
	 *            the second point
	 * @return the Euclidean distance between the points
	 */
	protected static double distnorm2(DoubleMatrix1D x, DoubleMatrix1D y) {
		DoubleMatrix1D z = x.copy();
		z.assign(y, Functions.minus);
		return z.zDotProduct(z);
	}

	/**
	 * Merges two sets of points represented as integer vectors. The sets are
	 * not overlapped.
	 * 
	 * @param a
	 *            the first set of points
	 * @param b
	 *            the second set of points
	 * @return the union of the two sets
	 */
	protected static int[] merge(int[] a, int[] b) {
		int[] v = new int[a.length + b.length];
		System.arraycopy(a, 0, v, 0, a.length);
		System.arraycopy(b, 0, v, a.length, b.length);
		return v;
	}

	/**
	 * Computes the association degree between two partitions of a graph.<br>
	 * The association degree is defined as the sum of the weights of all the
	 * edges between points of the two partitions.
	 * 
	 * @param W
	 *            the weight matrix of the graph
	 * @param a
	 *            the points of the first partition
	 * @param b
	 *            the points of the second partition
	 * @return the association degree
	 */
	protected static double asso(DoubleMatrix2D W, int[] a, int[] b) {
		return W.viewSelection(a, b).zSum();
	}

	/**
	 * Returns the normalized association degree between two partitions of a
	 * graph.
	 * 
	 * @param W
	 *            the weight matrix of the graph
	 * @param a
	 *            the points of the first partition
	 * @param b
	 *            the points of the second partition
	 * @return the normalized association degree
	 */
	protected static double Nasso(DoubleMatrix2D W, int[] a, int[] b) {
		int[] v = merge(a, b);
		return Nasso(W, a, b, v);
	}

	/**
	 * Returns the normalized association degree between two partitions of a
	 * graph w.r.t. a given subgraph.
	 * 
	 * @param W
	 *            the weight matrix of the graph
	 * @param a
	 *            the points of the first partition
	 * @param b
	 *            the points of the second partition
	 * @param v
	 *            the points of the subgraph
	 * @return the normalized association degree
	 */
	protected static double Nasso(DoubleMatrix2D W, int[] a, int[] b, int[] v) {
		return asso(W, a, a) / asso(W, a, v) + asso(W, b, b) / asso(W, b, v);
	}

	/**
	 * Returns the normalized dissimilarity degree (or cut) between two
	 * partitions of a graph.
	 * 
	 * @param W
	 *            the weight matrix of the graph
	 * @param a
	 *            the points of the first partition
	 * @param b
	 *            the points of the second partition
	 * @return the normalized cut
	 */
	protected static double Ncut(DoubleMatrix2D W, int[] a, int[] b) {
		return 2 - Nasso(W, a, b);
	}

	/**
	 * Returns the normalized dissimilarity degree (or cut) between two
	 * partitions of a graph w.r.t. a given subgraph.
	 * 
	 * @param W
	 *            the weight matrix of the graph
	 * @param a
	 *            the points of the first partition
	 * @param b
	 *            the points of the second partition
	 * @param v
	 *            the points of the subgraph.
	 * @return the normalized cut
	 */
	protected static double Ncut(DoubleMatrix2D W, int[] a, int[] b, int[] v) {
		return 2 - Nasso(W, a, b, v);
	}

	/**
	 * Returns the best cut of a graph w.r.t. the degree of dissimilarity
	 * between points of different partitions and the degree of similarity
	 * between points of the same partition.
	 * 
	 * @param W
	 *            the weight matrix of the graph
	 * @return an array of two elements, each of these contains the points of a
	 *         partition
	 */
	protected static int[][] bestCut(DoubleMatrix2D W) {
		int n = W.columns();
		// Builds the diagonal matrices D and D^(-1/2) (represented as their
		// diagonals)
		DoubleMatrix1D d = DoubleFactory1D.dense.make(n);
		DoubleMatrix1D d_minus_1_2 = DoubleFactory1D.dense.make(n);
		for (int i = 0; i < n; i++) {
			double d_i = W.viewRow(i).zSum();
			d.set(i, d_i);
			d_minus_1_2.set(i, 1 / Math.sqrt(d_i));
		}
		DoubleMatrix2D D = DoubleFactory2D.sparse.diagonal(d);

		DoubleMatrix2D X = D.copy();
		// X = D^(-1/2) * (D - W) * D^(-1/2)
		X.assign(W, Functions.minus);
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				X.set(i, j, X.get(i, j) * d_minus_1_2.get(i)
						* d_minus_1_2.get(j));

		// Computes the eigenvalues and the eigenvectors of X
		EigenvalueDecomposition e = new EigenvalueDecomposition(X);
		DoubleMatrix1D lambda = e.getRealEigenvalues();

		// Selects the eigenvector z_2 associated with the second smallest
		// eigenvalue
		// Creates a map that contains the pairs <index, eigevalue>
		AbstractIntDoubleMap map = new OpenIntDoubleHashMap(n);
		for (int i = 0; i < n; i++)
			map.put(i, Math.abs(lambda.get(i)));
		IntArrayList list = new IntArrayList();
		// Sorts the map on the value
		map.keysSortedByValue(list);
		// Gets the index of the second smallest element
		int i_2 = list.get(1);

		// y_2 = D^(-1/2) * z_2
		DoubleMatrix1D y_2 = e.getV().viewColumn(i_2).copy();
		y_2.assign(d_minus_1_2, Functions.mult);

		// Creates a map that contains the pairs <i, y_2[i]>
		map.clear();
		for (int i = 0; i < n; i++)
			map.put(i, y_2.get(i));
		// Sorts the map on the value
		map.keysSortedByValue(list);
		// Search the element in the map previuosly ordered that minimizes the
		// cut
		// of the partition
		double best_cut = Double.POSITIVE_INFINITY;
		int[][] partition = new int[2][];

		// The array v contains all the elements of the graph ordered by their
		// projection on vector y_2
		int[] v = list.elements();
		// For each admissible splitting point i
		for (int i = 1; i < n; i++) {
			// The array a contains all the elements that have a projection on
			// vector
			// y_2 less or equal to the one of i-th element
			// The array b contains the remaining elements
			int[] a = new int[i];
			int[] b = new int[n - i];
			System.arraycopy(v, 0, a, 0, i);
			System.arraycopy(v, i, b, 0, n - i);
			double cut = Ncut(W, a, b, v);
			if (cut < best_cut) {
				best_cut = cut;
				partition[0] = a;
				partition[1] = b;
			}
		}
		return partition;
	}

	/**
	 * Splits recursively the points of the graph while the value of the best
	 * cut found is less of a specified limit (the alpha star factor).
	 * 
	 * @param W
	 *            the weight matrix of the graph
	 * @param alpha_star
	 *            the alpha star factor
	 * @return an array of sets of points (partitions)
	 */
	protected static int[][] partition(DoubleMatrix2D W, double alpha_star) {
		// If the graph contains only one point
		if (W.columns() == 1) {
			int[][] p = new int[1][1];
			p[0][0] = 0;
			return p;
			// Otherwise
		} else {
			// Computes the best cut
			int[][] cut = bestCut(W);
			// Computes the value of the found cut
			double cutVal = Ncut(W, cut[0], cut[1], null);
			// If the value is less than alpha star
			if (cutVal < alpha_star) {
				// Recursively partitions the first one found ...
				DoubleMatrix2D W0 = W.viewSelection(cut[0], cut[0]);
				int[][] p0 = partition(W0, alpha_star);
				// ... and the second one
				DoubleMatrix2D W1 = W.viewSelection(cut[1], cut[1]);
				int[][] p1 = partition(W1, alpha_star);
				// Merges the partitions found in the previous recursive steps
				int[][] p = new int[p0.length + p1.length][];
				for (int i = 0; i < p0.length; i++) {
					p[i] = new int[p0[i].length];
					for (int j = 0; j < p0[i].length; j++)
						p[i][j] = cut[0][p0[i][j]];
				}
				for (int i = 0; i < p1.length; i++) {
					p[i + p0.length] = new int[p1[i].length];
					for (int j = 0; j < p1[i].length; j++)
						p[i + p0.length][j] = cut[1][p1[i][j]];
				}
				return p;
			} else {
				// Otherwise returns the partitions found in current step
				// w/o recursive invocation
				int[][] p = new int[1][W.columns()];
				for (int i = 0; i < p[0].length; i++)
					p[0][i] = i;
				return p;
			}
		}
	}

	/**
	 * Returns the number of clusters found.
	 * 
	 * @return the number of clusters
	 */
	public int numberOfClusters() throws java.lang.Exception {
		return numOfClusters;
	}

	/**
	 * Classifies an instance w.r.t. the partitions found. It applies a naive
	 * min-distance algorithm.
	 * 
	 * @param instance
	 *            the instance to classify
	 * @return the cluster that contains the nearest point to the instance
	 */
	public int clusterInstance(Instance instance) throws java.lang.Exception {
		DoubleMatrix1D u = DoubleFactory1D.dense.make(instance.toDoubleArray());
		double min_dist = Double.POSITIVE_INFINITY;
		int c = -1;
		for (int i = 0; i < v.rows(); i++) {
			double dist = distnorm2(u, v.viewRow(i));
			if (dist < min_dist) {
				c = cluster[i];
				min_dist = dist;
			}
		}
		return c;
	}

	/**
	 * Generates a clusterer by the mean of spectral clustering algorithm.
	 * 
	 * @param data
	 *            set of instances serving as training data
	 * @exception Exception
	 *                if the clusterer has not been generated successfully
	 */
	public void buildClusterer(Instances data) throws java.lang.Exception {
		int n = data.numInstances();
		int k = data.numAttributes();
		DoubleMatrix2D w;
		if (useSparseMatrix)
			w = DoubleFactory2D.sparse.make(n, n);
		else
			w = DoubleFactory2D.dense.make(n, n);
		double[][] v1 = new double[n][];
		for (int i = 0; i < n; i++)
			v1[i] = data.instance(i).toDoubleArray();
		v = DoubleFactory2D.dense.make(v1);
		double sigma_sq = sigma * sigma;
		// Sets up similarity matrix
		for (int i = 0; i < n; i++)
			for (int j = i; j < n; j++) {
				double dist = distnorm2(v.viewRow(i), v.viewRow(j));
				if ((r == -1) || (dist < r)) {
					double sim = Math.exp(-(dist * dist) / (2 * sigma_sq));
					w.set(i, j, sim);
					w.set(j, i, sim);
				}
			}

		// Partitions points
		int[][] p = partition(w, alpha_star);

		// Deploys results
		numOfClusters = p.length;
		cluster = new int[n];
		for (int i = 0; i < p.length; i++)
			for (int j = 0; j < p[i].length; j++)
				cluster[p[i][j]] = i;
	}

	/**
	 * Returns a string describing this clusterer
	 * 
	 * @return a description of the evaluator suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Cluster data using spectral methods";
	}

	/**
	 * Returns an enumeration describing the available options.
	 * <p>
	 * 
	 * @return an enumeration of all the available options
	 */
	public Enumeration listOptions() {
		return options.elements();
	}

	/**
	 * Parses a given list of options.
	 * 
	 * @param options
	 *            the list of options as an array of strings
	 * @exception Exception
	 *                if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {
		String optionString = Utils.getOption('A', options);
		if (optionString.length() != 0)
			setAlphaStar(Double.parseDouble(optionString));
		optionString = Utils.getOption('S', options);
		if (optionString.length() != 0)
			setSigma(Double.parseDouble(optionString));
		optionString = Utils.getOption('R', options);
		if (optionString.length() != 0)
			setR(Double.parseDouble(optionString));
		setUseSparseMatrix(Utils.getFlag('M', options));
	}

	/**
	 * Gets the current settings of the options.
	 * 
	 * @return an array of strings suitable for passing to setOptions()
	 */
	public String[] getOptions() {
		String[] options = new String[7];
		int current = 0;
		options[current++] = "-A";
		options[current++] = "" + Double.toString(getAlphaStar());
		options[current++] = "-S";
		options[current++] = "" + Double.toString(getSigma());
		options[current++] = "-R";
		options[current++] = "" + Double.toString(getR());
		if (getUseSparseMatrix())
			options[current++] = "-M";
		while (current < options.length)
			options[current++] = "";
		return options;
	}

	/**
	 * Sets the new value of the alpha star factor.
	 * 
	 * @param alpah_star
	 *            the new value (0 &lt; alpha_star &lt; 1)
	 * @exception Exception
	 *                if alpha_star is not between 0 and 1
	 */
	public void setAlphaStar(double alpha_star) throws Exception {
		if ((alpha_star > 0) && (alpha_star < 1))
			this.alpha_star = alpha_star;
		else
			throw new Exception("alpha_star must be between 0 and 1");
	}

	/**
	 * Returns the current value of the alpha star factor.
	 * 
	 * @return the alpha star factor
	 */
	public double getAlphaStar() {
		return alpha_star;
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String alphaStarTipText() {
		return "set maximum allowable normalized cut value. The algorithm stops the recursive partitioning when it does not find a cut that has a value below this factor. Use this argument to limit the number of clusters.";
	}

	/**
	 * Sets the new value of the sigma scaling factor.
	 * 
	 * @param sigma
	 *            the new value (sigma &gt; 0)
	 * @exception Exception
	 *                if sigma is not strictly greater than 0
	 */
	public void setSigma(double sigma) throws Exception {
		if (sigma > 0)
			this.sigma = sigma;
		else
			throw new Exception("sigma must be a positive number");
	}

	/**
	 * Returns the current value of the sigma factor.
	 * 
	 * @return the sigma factor
	 */
	public double getSigma() {
		return sigma;
	}

	/**
	 * Returns the tip text for this property.
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String sigmaTipText() {
		return "set the distance scaling factor. The similarity of two point x and y is defined as exp(- d(x, y) / sigma) where d(x, y) is the Euclidean distance between x and y.";
	}

	/**
	 * Sets the new value of the r distance cut parameter.
	 * 
	 * @param r
	 *            the new value (r &gt; 0 || r == -1)
	 * @exception Exception
	 *                if r is not -1 and r is not a positive number
	 */
	public void setR(double r) throws Exception {
		if ((r > 0) || (r == -1))
			this.r = r;
		else
			throw new Exception("r must be -1 or a positive number");
	}

	/**
	 * Returns the current value of the r distance cur parameter.
	 * 
	 * @return the r distance cut parameter
	 */
	public double getR() {
		return r;
	}

	/**
	 * Returns the tip text for this property.
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String rTipText() {
		return "set the maximum distance value, all points that are far away more than this value have a 0 similarity. Use this parameter to obtain a sparse similarity matrix (see -M).";
	}

	/**
	 * Sets the use of sparse representation for similarity matrix.
	 * 
	 * @param useSparseMatrix
	 *            true for sparse matrix representation
	 */
	public void setUseSparseMatrix(boolean useSparseMatrix) {
		this.useSparseMatrix = useSparseMatrix;
	}

	/**
	 * Returns the status of using sparse matrix flag.
	 * 
	 * @return the status of using sparse matrix flag
	 */
	public boolean getUseSparseMatrix() {
		return useSparseMatrix;
	}

	/**
	 * Returns the tip text for this property.
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String useSparseMatrixTipText() {
		return "use sparse representation for similarity matrix. It can improve the memory efficiency";
	}

	/**
	 * Constructor.
	 */
	public SpectralClusterer() {
	}
}
