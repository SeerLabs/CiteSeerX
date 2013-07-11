/**
 * http://lcn.epfl.ch/tutorial/english/index.html
 */
import java.awt.*;

abstract class Mixture {
	final int maxkp = 20;
	final int typegauss = 0;
	final int typeuniform = 1;
	final int typecurvedgauss = 2;
	final int typescaleshift = 3;
	final int typeline = 101;
	Object[] kernel = new Object[maxkp];
	int type[] = new int[maxkp];
	double[] weight = new double[maxkp];
	int nk;
	int xsiz, ysiz;
	double[] px;
	Database db;

	public Mixture(int xSize, int ySize, Database DB) {
		xsiz = xSize;
		ysiz = ySize;
		db = DB;
	}

	public void initKernel(Object mod, int tp, int pos) {
		kernel[pos] = mod;
		type[pos] = tp;
	}

	public void setnk(int nK) {
		if (nk == nK)
			return;
		nk = nK;
		randomKernels();
	}

	public void setnk(int nK, double[] ws) {
		double sum;
		int i;
		double[] w;

		nk = nK;
		w = new double[nk];
		sum = 0;
		for (i = 0; i < nk; i++) {
			sum += ws[i];
		}
		for (i = 0; i < nk; i++) {
			w[i] = ws[i] / sum;
		}
		randomKernels(w);
	}

	public void randomKernels(double[] ws) {
		double sum, tmp;
		int i;

		sum = 0;
		for (i = 0; i < nk; i++) {
			sum += ws[i];
		}
		for (i = 0; i < nk; i++) {
			tmp = ws[i] / sum;
			switch (type[i]) {
			/*
			 * case typegauss : ((Gaussian) kernel[i]).randomKernel(tmp); break;
			 */
			case typecurvedgauss:
				((CurvedGaussian) kernel[i]).randomKernel(tmp);
				break;
			case typeuniform:
				((Uniform) kernel[i]).randomKernel(tmp);
				break;
			/*
			 * case typeline : ((Line) kernel[i]).randomKernel(tmp); break;
			 */
			}
		}
	}

	public void randomKernels() {
		double[] ws = new double[nk];
		for (int i = 0; i < nk; i++)
			ws[i] = 1.0 / nk;
		randomKernels(ws);
	}

	public void paint(Graphics g) {
		int i;
		for (i = 0; i < nk; i++) {
			switch (type[i]) {
			/*
			 * case typegauss : ((Gaussian) kernel[i]).paint(g, db); break;
			 */
			case typecurvedgauss:
				((CurvedGaussian) kernel[i]).paint(g, db);
				break;
			case typeuniform:
				((Uniform) kernel[i]).paint(g, db);
				break;
			/*
			 * case typeline : ((Line) kernel[i]).paint(g, db); break;
			 */

			}
		}
		g.setColor(Color.red);
		g.drawString("Mean Likelihood = " + likelihood(), 0, 30);
	}

	private void calcpx() {
		int i, j, np;
		double[] probs;

		np = db.nPoints();
		px = new double[np];
		for (j = 0; j < np; j++) {
			px[j] = 0;
		}
		for (i = 0; i < nk; i++) {
			probs = ((Module) kernel[i]).calcp(db);
			for (j = 0; j < np; j++) {
				px[j] += probs[j];
			}
		}
	}

	public void EM(double[] ws) {
		EMmain(ws);
	}

	public void EMmain(double[] ws) {
		double sum, newlike;
		int i, j;

		if (db.nPoints() <= 2)
			return;
		sum = 0;
		for (i = 0; i < nk; i++) {
			sum += ws[i];
		}
		calcpx();
		for (i = 0; i < nk; i++) {
			((Module) kernel[i]).EMprob(px, db);
			switch (type[i]) {
			/*
			 * case typegauss : ((Gaussian)kernel[i]).EMpar(db, ws[i] / sum);
			 * break;
			 */
			case typecurvedgauss:
				((CurvedGaussian) kernel[i]).EMpar(db, ws[i] / sum);
				break;
			case typeuniform:
				((Uniform) kernel[i]).EMpar(db, ws[i] / sum);
				break;
			/*
			 * case typeline : ((Line)kernel[i]).EMpar(db, ws[i] / sum); break;
			 */
			}
		}
		return;
	}

	public double likelihood() {
		int i, j, np;
		double tmp;

		np = db.nPoints();
		if (np == 0)
			return 0;
		calcpx();
		tmp = 0;
		for (j = 0; j < np; j++) {
			tmp += Math.log(px[j]);
		}
		return tmp / np;
	}
}
