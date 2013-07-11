/**
 * http://lcn.epfl.ch/tutorial/english/index.html
 * 
 */
import java.awt.*;

class Uniform extends Module {
	public Uniform(int xSize, int ySize, double w) {
		super(xSize, ySize, w);
	}

	public void randomKernel(double w) {
		weight = w;
	}

	public void paint(Graphics g, Database db) {
	}

	public double density(int x, int y) {
		return weight / xsiz / ysiz;
	}

	public void EMpar(Database db, double prior) {
	}
}
