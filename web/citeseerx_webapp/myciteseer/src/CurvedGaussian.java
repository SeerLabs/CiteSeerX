/**
 * http://lcn.epfl.ch/tutorial/english/index.html
 */
import java.awt.*;

class CurvedGaussian extends Module {
	double kmx, kmy, ksx, ksy, ksxy;
	final int mins = 8;
	boolean plotcircle = true;

	public CurvedGaussian(int xSize, int ySize, double w) {
		super(xSize, ySize, w);
	}

	public void randomKernel(double w) {
		weight = w;
		kmx = xsiz * Math.random();
		kmy = ysiz * Math.random();
		ksx = xsiz / 4 + mins;
		ksy = ysiz / 4 + mins;
		ksxy = 0;
	}

	public void setplotline() {
		plotcircle = false;
	}

	public double[] getPar() {
		double pars[];
		pars = new double[6];
		pars[0] = weight;
		pars[1] = kmx;
		pars[2] = kmy;
		pars[3] = ksx;
		pars[4] = ksy;
		pars[5] = ksxy;
		return pars;
	}

	public void paint(Graphics g, Database db) {
		int j;
		double theta, u1x, u1y, u2x, u2y, r1, r2, tmp, varx, vary;

		if (Math.abs(ksxy) <= 1e-4) {
			if (ksx > ksy) {
				u1x = u2y = 1;
				u1y = u2x = 0;
				r1 = ksx;
				r2 = ksy;
			} else {
				u1x = u2y = 0;
				u1y = u2x = 1;
				r1 = ksy;
				r2 = ksx;
			}
		} else {
			varx = ksx * ksx;
			vary = ksy * ksy;
			// eigen value
			tmp = varx - vary;
			tmp = Math.sqrt(tmp * tmp + 4 * ksxy * ksxy);
			r1 = Math.sqrt((varx + vary + tmp) / 2);
			r2 = Math.sqrt((varx + vary - tmp) / 2);

			// eigen vectors
			u1x = r1 * r1 - vary;
			tmp = Math.sqrt(u1x * u1x + ksxy * ksxy);
			u1x /= tmp;
			u1y = ksxy / tmp;

			u2x = r2 * r2 - vary;
			tmp = Math.sqrt(u2x * u2x + ksxy * ksxy);
			u2x /= tmp;
			u2y = ksxy / tmp;
		}
		g.setColor(Color.red);
		g.drawString(weight + "", (int) kmx, (int) kmy);
		g.setColor(Color.blue);
		if (plotcircle) {
			for (j = 1; j < 4; j++) {
				drawCurvedOval(g, u1x, u1y, u2x, u2y, r1 * j, r2 * j);
			}
		} else {
			g.drawLine((int) (kmx + 3 * r1 * u1x), (int) (kmy + 3 * r1 * u1y),
					(int) (kmx - 3 * r1 * u1x), (int) (kmy - 3 * r1 * u1y));
		}
	}

	public void drawCurvedOval(Graphics g, double x1, double y1, double x2,
			double y2, double r1, double r2) {
		int fx, fy, tx, ty;
		double w1, w2;

		fx = (int) (kmx + r1 * x1);
		fy = (int) (kmy + r1 * y1);
		for (double th = 0.1; th < 6.4; th += 0.1) {
			w1 = Math.cos(th);
			w2 = Math.sin(th);
			tx = (int) (kmx + r1 * x1 * w1 + r2 * x2 * w2);
			ty = (int) (kmy + r1 * y1 * w1 + r2 * y2 * w2);
			g.drawLine(fx, fy, tx, ty);
			fx = tx;
			fy = ty;
		}
	}

	public double density(int x, int y) {
		double tmpx, tmpy, tmpxy, det, varx, vary;
		varx = ksx * ksx;
		vary = ksy * ksy;
		det = varx * vary - ksxy * ksxy;
		tmpx = (x - kmx) * (x - kmx);
		tmpy = (y - kmy) * (y - kmy);
		tmpxy = (x - kmx) * (y - kmy);
		return weight
				/ Math.sqrt(det)
				/ 6.28319
				* Math.exp(-(tmpx * vary + tmpy * varx - 2 * tmpxy * ksxy)
						/ det / 2);
	}

	public void EMpar(Database db, double prior) {
		int j, np;
		double x, y, tmp, tmpx, tmpy, tmpsx, tmpsy, tmpsxy;

		np = db.nPoints();
		tmpsx = tmpsy = tmpsxy = kmx = kmy = 0;
		for (j = 0; j < np; j++) {
			x = db.xVal(j);
			y = db.yVal(j);
			kmx += probs[j] * x;
			kmy += probs[j] * y;
			tmpsx += probs[j] * x * x;
			tmpsy += probs[j] * y * y;
			tmpsxy += probs[j] * x * y;
		}
		tmp = np * weight;
		kmx /= tmp;
		kmy /= tmp;
		ksx = Math.sqrt(tmpsx / tmp - kmx * kmx);
		ksy = Math.sqrt(tmpsy / tmp - kmy * kmy);
		ksxy = tmpsxy / tmp - kmx * kmy;
		if (ksx < mins)
			ksx = mins;
		if (ksy < mins)
			ksy = mins;
		weight = 0.9 * weight + 0.1 * prior;
	}
}
