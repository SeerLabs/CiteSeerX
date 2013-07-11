/**
 * http://lcn.epfl.ch/tutorial/english/index.html
 */
import java.awt.*;
import java.io.*;

abstract class Module {
	int xsiz, ysiz;
	double weight;
	double[] probs;

	public Module(int xSiz, int ySiz, double w) {
		xsiz = xSiz;
		ysiz = ySiz;
		weight = w;
		randomKernel(w);
	}

	public void setweight(double w) {
		weight = w;
	}

	public abstract void randomKernel(double w);

	public abstract void paint(Graphics g, Database db);

	public abstract double density(int x, int y);

	public double[] calcp(Database db) {
		int j, np;

		np = db.nPoints();
		probs = new double[np];
		for (j = 0; j < np; j++)
			probs[j] = density(db.xVal(j), db.yVal(j));
		return probs;
	}

	public void EMprob(double[] px, Database db) {
		int np;
		np = db.nPoints();
		weight = 0;
		for (int j = 0; j < np; j++) {
			probs[j] /= px[j];
			weight += probs[j];
		}
		weight /= np;
	}

	public abstract void EMpar(Database db, double prior);
}
