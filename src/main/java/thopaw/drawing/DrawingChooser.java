package thopaw.drawings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 * Simple Frame for Drawing images with mathematic functions
 * 
 * @author Thomas Pawlitzki
 */
public class DrawingChooser {

	/**
	 * Interface for Calculate Functions
	 * 
	 * @version $Revision$
	 * @author hv11193
	 */
	public interface CalculateFunction {
		/**
		 * Takes a point a calculates a value
		 * 
		 * @param x X coordinate
		 * @param y Y coordinate
		 * @return Value
		 */
		double calculate(double x, double y);
	}

	/**
	 * Interface for Color Calculator Functions
	 */
	public interface ColorFunction {
		Color determineColor(double value);
	}

	/**
	 * Simple Main Method
	 * 
	 * @param args Arguments
	 */
	public static void main(String[] args) {
		new DrawingChooser();
	}

	private JFrame frame;
	private JComponent canvas;
	private JScrollPane scrollPane;

	private ColorFunction currentColorFunction;

	private CalculateFunction currentCalculateFunction;

	private double zoom = 1;

	public void setCurrentCalculateFunction(CalculateFunction currentCalculateFunction) {
		this.currentCalculateFunction = currentCalculateFunction;
		zoom = 1;
		canvas.repaint();
	}

	public void setCurrentColorFunction(ColorFunction currentColorFunction) {
		this.currentColorFunction = currentColorFunction;
		canvas.repaint();
	}

	public DrawingChooser() {
		frame = new JFrame("Drawings");
		frame.setJMenuBar(createMenuBar());
		frame.setSize(800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		canvas = new JComponent() {
			private static final long serialVersionUID = 338270813311065258L;

			@Override
			public void paint(Graphics g) {
				int startx = 0;
				int starty = 0;
				for (int x = startx; x < startx + canvas.getWidth(); x++) {
					for (int y = starty; y < starty + canvas.getHeight(); y++) {
						final double value = currentCalculateFunction.calculate(x, y) * zoom;
						g.setColor(currentColorFunction.determineColor(value));
						g.fillRect(x, y, 1, 1);
					}
				}
			}
		};
		canvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				setZoom(zoom + e.getWheelRotation() * 0.1);
			}
		});

		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(canvas, BorderLayout.CENTER);

		setCurrentCalculateFunction((x, y) -> x + y);
		setCurrentColorFunction((v) -> getColor(v, 1, 0.5, 0.5, 2 * Math.PI / 3));
		frame.setVisible(true);

	}

	public void setZoom(double zoom) {
		this.zoom = Math.max(zoom, 0.0);
		System.out.println("zoom : " + this.zoom);
		canvas.repaint();
	}

	private float getRainbow(double value, double frequency, double amplitude, double center, double shift) {
		return (float) (Math.sin(value * frequency + shift) * amplitude + center);
	}

	protected Color getColor(double value, double frequency, double amplitude, double center, double shiftFact) {
		return new Color(//
				getRainbow(value, frequency, amplitude, center, 0.0 * shiftFact), //
				getRainbow(value, frequency, amplitude, center, 1.0 * shiftFact), //
				getRainbow(value, frequency, amplitude, center, 2.0 * shiftFact));
	}

	private JMenuBar createMenuBar() {
		JMenuBar menu = new JMenuBar();
		menu.add(createMenuCalculate());
		menu.add(createMenuColor());
		menu.add(createMenuTools());
		return menu;
	}

	private JMenu createMenuColor() {
		JMenu menu = new JMenu("Color");
		menu.add(createMenuItemColor("Rainbow", (v) -> getColor(v, 1, 0.5, 0.5, 2 * Math.PI / 3)));
		menu.add(createMenuItemColor("Rainbow PI/3 - cold", (v) -> getColor(v, 1, 0.5, 0.5, Math.PI / 3)));
		menu.add(createMenuItemColor("Rainbow Pastell", (v) -> getColor(v, 1.0, 0.78, 0.2, 2 * Math.PI / 3)));
		menu.add(createMenuItemColor("BLACK/WHITE", (v) -> {
			int c = (int) (v % 255);
			return new Color(c, c, c);
		}));
		return menu;
	}

	private JMenuItem createMenuItemColor(String string, ColorFunction function) {
		JMenuItem item = new JMenuItem(string);
		item.addActionListener((e) -> {
			setCurrentColorFunction(function);
		});
		return item;
	}

	private JMenu createMenuCalculate() {
		JMenu menu = new JMenu("Drawings");

		menu.add(createMenuItemCalculate("sin (x) cos(y) tan(x*y)", (x, y) -> {
			double facSin = 0.05;
			double facCos = 0.05;
			double facTan = 0.0015;
			return Math.sin(x * facSin) * Math.cos(y * facCos) * Math.tan((x * y) * facTan);
		}));
		menu.add(createMenuItemCalculate("sin (x) tan(x*y)", (x, y) -> {
			double facSin = 0.0005;
			double facTan = 0.000015;
			double delta = 1000;
			return Math.cos(x * y * facSin + delta) - Math.tan(x * y * facTan + delta);
		}));
		menu.add(createMenuItemCalculate("centered sin (x) tan(x*y)", (x, y) -> {
			double facSin = 0.0002;
			double facTan = 0.024;
			double delta = 100;
			return Math.cos(x * y * facSin + delta) - Math.tan(x * y * facTan + delta);
		}));
		menu.add(createMenuItemCalculate("centered sin (x) tan(x*y)", (x, y) -> {
			double facSin = 0.00002;
			double facTan = 0.0000024;
			double delta = 100;
			return Math.cos(x * y * facSin + delta) - Math.tan(x * y * facTan + delta);
		}));

		menu.add(createMenuItemCalculate("AND", (x, y) -> {
			return ((int) x & (int) y);
		}));
		menu.add(createMenuItemCalculate("OR", (x, y) -> {
			return ((int) x | (int) y);
		}));
		menu.add(createMenuItemCalculate("XOR", (x, y) -> {
			return ((int) x ^ (int) y);
		}));

		menu.add(createMenuItemCalculate("x XOR x & y", (x, y) -> {
			double fac = 0.005;
			double v = ((int) x ^ ((int) x & (int) y)) * fac;
			return v;

		}));

		menu.add(createMenuItemCalculate("x | y XOR x & y", (x, y) -> {
			double fac = 0.005;
			double v = (((int) x | (int) y) ^ ((int) x & (int) y)) * fac;
			return v;
		}));

		menu.add(createMenuItemCalculate("x*y", (x, y) -> {
			double v = x * y;
			return v;
		}));
		menu.add(createMenuItemCalculate("x/y", (x, y) -> {
			return x / y;
		}));
		menu.add(createMenuItemCalculate("sin(x) * sin(y)", (x, y) -> {
			double fac = 0.05;
			return Math.sin(x * fac) * Math.sin(y * fac);
		}));
		menu.add(createMenuItemCalculate("cos(x) * cos(y)", (x, y) -> {
			double fac = 0.05;
			return Math.cos(x * fac) * Math.cos(y * fac);
		}));
		menu.add(createMenuItemCalculate("tan(x) * tan(y)", (x, y) -> {
			double fac = 2.1;
			return Math.tan(x * fac) * Math.tan(y * fac);
		}));
		menu.add(createMenuItemCalculate("sin(x) * tan(y)", (x, y) -> {
			double facX = 0.01;
			double facY = 2.1;
			return Math.sin(x * facX) * Math.tan(y * facY);
		}));
		menu.add(createMenuItemCalculate("x + y", (x, y) -> {
			double facX = 0.01;
			double facY = 0.05;
			return x * facX + y * facY;
		}));
		menu.add(createMenuItemCalculate("x - y", (x, y) -> {
			double facX = 0.01;
			double facY = 0.05;
			return x * facX - y * facY;
		}));
		menu.add(createMenuItemCalculate("x pow y", (x, y) -> {
			double facX = 0.01;
			double facY = 0.01;
			return Math.pow(x * facX, y * facY);
		}));
		menu.add(createMenuItemCalculate("x log y", (x, y) -> {
			double facX = 1.5;
			double facY = 1.5;
			return Math.log(x * facX) * Math.log(y * facY);
		}));
		menu.add(createMenuItemCalculate("x hypot y", (x, y) -> {
			double facX = 0.1;
			double facY = 0.1;
			return Math.hypot(x * facX, y * facY);
		}));

		menu.add(createMenuItemCalculate("e^x * e^y", (x, y) -> {
			double facX = 0.01;
			double facY = 0.005;
			return Math.exp(x * facX) * Math.exp(y * facY);
		}));
		return menu;
	}

	private JMenuItem createMenuItemCalculate(String name, CalculateFunction function) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener((e) -> {
			this.setCurrentCalculateFunction(function);
		});
		return item;
	}

	private JMenu createMenuTools() {
		JMenu menu = new JMenu("Tools");
		{
			JMenuItem export = new JMenuItem("Export as Image");
			export.addActionListener((e) -> {
				export();
			});
			menu.add(export);
		}
		menu.addSeparator();
		{
			JMenuItem setZoom = new JMenuItem("Set Zoom");
			setZoom.addActionListener((e) -> {
				String newZoom = JOptionPane.showInputDialog(canvas, "Choose the Zoom Level", zoom);
				try {
					setZoom(Double.parseDouble(newZoom));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			menu.add(setZoom);
		}
		return menu;
	}

	private void export() {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			chooser.setSelectedFile(new File("drawing.png"));
			int selection = chooser.showSaveDialog(canvas);
			if (selection == JFileChooser.APPROVE_OPTION) {
				BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getWidth(), BufferedImage.TYPE_INT_ARGB);
				// currentDrawFunction.draw((Graphics2D) image.getGraphics(), new Dimension(image.getWidth(), image.getHeight()));
				ImageIO.write(image, "png", chooser.getSelectedFile());
				Desktop.getDesktop().open(chooser.getSelectedFile());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
