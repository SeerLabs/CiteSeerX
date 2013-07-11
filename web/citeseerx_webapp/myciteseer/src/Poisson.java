/*
 * Copyright (c) 2005, Regents of the University of California
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * * Neither the name of the University of California, Berkeley nor
 *   the names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.Serializable;

/**
 * A Poisson distribution with mean and variance lambda. This is a distribution
 * over non-negative integers. The probability of n is exp(-lambda) lambda^n /
 * n!.
 */
public class Poisson implements IntegerDist, Serializable {
	/**
	 * Creates a new Poisson distribution with lambda = 1.
	 */
	public Poisson() {
		this(1);
	}

	/**
	 * Creates a new Poisson distribution with the specifies lambda parameter.
	 */
	public Poisson(double lambda) {
		this.lambda = lambda;
	}

	/**
	 * Returns the probability of the integer n under this distribution.
	 */
	public double getProb(int n) {
		return (Math.exp(-lambda) * Math.pow(lambda, n) / Util.factorial(n));
	}

	/**
	 * Returns the log probability of the integer n under this distribution.
	 */
	public double getLogProb(int n) {
		return (-lambda + (n * Math.log(lambda)) - Util.logFactorial(n));
	}

	/**
	 * Returns an integer sampled according to this distribution. This
	 * implementation takes time proportional to the magnitude of the integer
	 * returned. I got the algorithm from Anuj Kumar's course page for IEOR
	 * E4404 at Columbia University, specifically the file: <blockquote>
	 * http://www.columbia.edu/~ak2108/ta/summer2003/poisson1.c </blockquote>
	 */
	public int sample() {
		int n = 0;
		double probOfN = Math.exp(-lambda); // start with prob of 0
		double cumProb = probOfN;

		double u = Util.random();
		while (cumProb < u) {
			n++;
			// ratio between P(n) and P(n-1) is lambda / n
			probOfN *= (lambda / n);
			cumProb += probOfN;
		}

		return n;
	}

	double lambda;
}
