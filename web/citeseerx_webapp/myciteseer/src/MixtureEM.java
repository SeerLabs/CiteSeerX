/**
 * http://lcn.epfl.ch/tutorial/english/index.html
 */
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;

public class MixtureEM extends Applet {
	ControlPanel cPanel;
	PlotCanvas pCanvas;
	CurvedGaussMixture CGMix;
	GaussLineMixture GLMix;
	Database DB;
	final int xSize = 600;
	final int ySize = 600;

	public void init() {
		setLayout(new BorderLayout());
		DB = new Database(xSize, ySize, Color.black);
		CGMix = new CurvedGaussMixture(xSize, ySize, DB);
		GLMix = new GaussLineMixture(xSize, ySize, DB);
		pCanvas = new PlotCanvas(CGMix, GLMix, DB, xSize, ySize);
		cPanel = new ControlPanel(pCanvas, CGMix, GLMix, DB);
		add("Center", pCanvas);
		add("South", cPanel);
	}
}

class ControlPanel extends Panel implements Runnable {
	Choice nKernels;
	Choice selectmix;
	Choice EMswitch;
	PlotCanvas pcanvas;
	CurvedGaussMixture cgmix;
	GaussLineMixture glmix;
	int select;
	Database db;
	double[] ws, ws2;
	Thread th;
	boolean runMode = false;

	public ControlPanel(PlotCanvas pC, CurvedGaussMixture CGMix,
			GaussLineMixture GLMix, Database DB) {
		cgmix = CGMix;
		glmix = GLMix;
		db = DB;
		pcanvas = pC;
		pcanvas.connectControlPanel(this);
		ws = new double[9];
		ws2 = new double[9];
		ws[0] = 0.1;
		ws2[0] = 10;
		for (int i = 1; i < 9; i++)
			ws[i] = ws2[i] = 1;

		setLayout(new FlowLayout());
		setselect(3);
		selectmix = new Choice();
		ItemListener selectmixListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String item = (String) e.getItem();
				if ("GaussMix".equals(item)) {
					setselect(3);
					cgmix.randomKernels(ws2);
				} else if ("LineMix".equals(item)) {
					setselect(4);
					glmix.randomKernels(ws2);
				}
				pcanvas.repaint();
			}
		};
		selectmix.addItem("GaussMix");
		selectmix.addItem("LineMix");
		add(selectmix);
		selectmix.addItemListener(selectmixListener);

		Button ringPts = new Button("RingPts");
		ActionListener ringPtsListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean thflag = (th != null);
				stop();
				db.circlePoints(100);
				pcanvas.repaint();
				if (thflag)
					start();
			}
		};
		add(ringPts);
		ringPts.addActionListener(ringPtsListener);

		Button randomPts = new Button("RandomPts");
		ActionListener randomPtsListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean thflag = (th != null);
				stop();
				db.randomPoints(10);
				pcanvas.repaint();
				if (thflag)
					start();
			}
		};
		add(randomPts);
		randomPts.addActionListener(randomPtsListener);

		Button clearPts = new Button("ClearPts");
		ActionListener clearPtsListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean thflag = (th != null);
				stop();
				db.clearPoints();
				pcanvas.repaint();
				if (thflag)
					start();
			}
		};
		add(clearPts);
		clearPts.addActionListener(clearPtsListener);

		Button initKernels = new Button("InitKernels");
		ActionListener initKernelsListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean thflag = (th != null);
				stop();
				cgmix.randomKernels(ws);
				glmix.randomKernels(ws);
				pcanvas.repaint();
				if (thflag)
					start();
			}
		};
		add(initKernels);
		initKernels.addActionListener(initKernelsListener);

		nKernels = new Choice();
		ItemListener nKernelsListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean thflag = (th != null);
				stop();
				int nK = Integer.parseInt((String) e.getItem());
				cgmix.setnk(nK + 1, ws);
				glmix.setnk(nK + 1, ws);
				if (thflag)
					start();
				pcanvas.repaint();
			}
		};
		nKernels.addItem("1");
		nKernels.addItem("2");
		nKernels.addItem("3");
		nKernels.addItem("4");
		nKernels.addItem("5");
		nKernels.addItem("6");
		nKernels.addItem("7");
		nKernels.addItem("8");
		add(nKernels);
		nKernels.addItemListener(nKernelsListener);

		EMswitch = new Choice();
		ItemListener EMswitchListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String item = (String) e.getItem();
				if ("EM Stop".equals(item)) {
					stop();
				} else if ("EM Run".equals(item)) {
					stop();
					start();
				} else if ("EM 1 Step".equals(item)) {
					stop();
					EM();
				}
				pcanvas.repaint();
			}
		};
		EMswitch.addItem("EM Stop");
		EMswitch.addItem("EM Run");
		EMswitch.addItem("EM 1 Step");
		add(EMswitch);
		EMswitch.addItemListener(EMswitchListener);
	}

	public void start() {
		if (th == null) {
			th = new Thread(this);
		}
		th.start();
	}

	public void stop() {
		th = null;
	}

	public void run() {
		while (th != null) {
			try {
				for (int i = 0; i < 5; i++) {
					EM();
				}
				pcanvas.repaint();
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void EM() {
		if (select == 3) {
			cgmix.EM(ws);
		} else if (select == 4) {
			glmix.EM(ws);
		}
	}

	public void dbpush(int x, int y) {
		if (th != null) {
			stop();
			db.push(x, y);
			start();
		} else {
			db.push(x, y);
		}
	}

	public void setselect(int sel) {
		select = sel;
		pcanvas.setselect(sel);
	}

}

class PlotCanvas extends Canvas implements MouseListener {
	ControlPanel cp;
	Database db;
	CurvedGaussMixture cgmix;
	GaussLineMixture glmix;
	int select;
	int xsiz, ysiz;

	public PlotCanvas(CurvedGaussMixture CGMix, GaussLineMixture GLMix,
			Database DB, int xSize, int ySize) {
		cgmix = CGMix;
		glmix = GLMix;
		db = DB;
		setBackground(Color.white);
		xsiz = xSize;
		ysiz = ySize;
		setSize(xSize, ySize);
		this.addMouseListener(this);
	}

	public void connectControlPanel(ControlPanel cPanel) {
		cp = cPanel;
	}

	public void paint(Graphics g) {
		g.clearRect(0, 0, xsiz, ysiz);
		db.paint(g);
		if (select == 3) {
			cgmix.paint(g);
		} else if (select == 4) {
			glmix.paint(g);
		}
	}

	public void setselect(int sel) {
		select = sel;
	}

	public void mouseClicked(MouseEvent e) {
		cp.dbpush(e.getX(), e.getY());
		repaint();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}

class CurvedGaussMixture extends Mixture {
	final int kmax = 10;

	public CurvedGaussMixture(int xSize, int ySize, Database DB) {
		super(xSize, ySize, DB);
		initKernel(new Uniform(xsiz, ysiz, 0.0), typeuniform, 0);
		for (int i = 1; i < kmax; i++) {
			initKernel(new CurvedGaussian(xsiz, ysiz, 0.0), typecurvedgauss, i);
		}
		setnk(2);
	}
}

class GaussLineMixture extends Mixture {
	final int kmax = 10;

	public GaussLineMixture(int xSize, int ySize, Database DB) {
		super(xSize, ySize, DB);
		CurvedGaussian cgmix;
		initKernel(new Uniform(xsiz, ysiz, 0.0), typeuniform, 0);
		for (int i = 1; i < kmax; i++) {
			cgmix = new CurvedGaussian(xsiz, ysiz, 0.0);
			cgmix.setplotline();
			initKernel(cgmix, typecurvedgauss, i);
		}
		setnk(2);
	}
}
