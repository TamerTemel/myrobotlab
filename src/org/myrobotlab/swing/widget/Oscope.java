package org.myrobotlab.swing.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.myrobotlab.image.SerializableImage;
import org.myrobotlab.service.SwingGui;
import org.myrobotlab.service.data.PinData;
import org.myrobotlab.service.interfaces.PinDefinition;
import org.myrobotlab.swing.ServiceGui;

public class Oscope extends ServiceGui implements ActionListener {

	int w = 600;
	int h = 100;

	// JPanel oscopePanel = new JPanel(new BorderLayout());
	// TODO - bit-blit 2 video panels
	// VideoWidget video;
	GridBagConstraints bgc = new GridBagConstraints();
	GridBagConstraints sgc = new GridBagConstraints();
	JPanel buttonPanel = new JPanel(new GridBagLayout());
	JPanel screenPanel = new JPanel(new GridBagLayout());

	Map<String, Trace> traces = new HashMap<String, Trace>();
	Map<Integer, Trace> traceIndex = new HashMap<Integer, Trace>();
	float gradient;

	// raster level access
	protected int[] data;
	// int w = 600;
	// int h = 100;

	/**
	 * this class extends JPanel so that we can control the painting
	 * 
	 * @author GroG
	 *
	 */
	public class TraceScreen extends JPanel {
		private static final long serialVersionUID = 1L;
		Trace trace;
		
		BufferedImage b0;
		BufferedImage b1;
		
		Graphics2D g0;
		Graphics2D g1;
		
		int timeDivisor = 1;
		
		public TraceScreen(Trace trace) {
			super();
			this.trace = trace;			
			b0 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			b1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			
			g0 = b0.createGraphics();
			g0.setPaint ( Color.GREEN );
			g0.fillRect ( 0, 0, b0.getWidth(), b0.getHeight() );
			g0.setColor(trace.color);
			
			g1 = b1.createGraphics();
			g1.setPaint ( Color.CYAN );
			g1.fillRect ( 0, 0, b1.getWidth(), b1.getHeight() );
			g1.setColor(trace.color);

			// on the top-level Canvas to prevent AWT from repainting it, as
			// you'll
			// typically be doing this yourself within the animation loop.
			setIgnoreRepaint(true);
			setDoubleBuffered(true); // weird no access
			setBackground(Color.GRAY);
			setVisible(false);

			
		}

		// drawing should be done on this thread ! 
		// 'displaying' should be done on the paint swing thread
		public void update(PinData pinData) {
			trace.pinData = pinData;
			trace.xpos+=timeDivisor;
			/*
			g2d.drawLine(trace.x, trace.y + h / 2, trace.xpos,trace.pinData.value * 20 + h / 2);
			log.info("{},{} - {},{}", trace.x, trace.y + h / 2, trace.xpos,trace.pinData.value * 20 + h / 2);
			if (trace.xpos % (w*timeDivisor) == 0) {
				trace.xpos = 0;
			}
			*/
			// trace.x = trace.xpos;
			// trace.y = trace.pinData.value;
			Graphics2D g0 = trace.screenDisplay.g0;
			Graphics2D g1 = trace.screenDisplay.g1;
			
			// draw on our active image (we swap between the two)
			
			// shift both images and re-draw them			
			
			// ok for ui to update now ..
			repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(w, h);
		}

		// https://www.nutt.net/create-scrolling-background-java/

		// g1 = b1.createGraphics();
			// http://stackoverflow.com/questions/15511282/how-can-i-make-a-java-swing-animation-smoother
		// http://stackoverflow.com/questions/2063607/java-panel-double-buffering
		// http://gamedev.stackexchange.com/questions/70711/how-do-i-double-buffer-renders-to-a-jpanel
		// http://stackoverflow.com/questions/4430356/java-how-to-do-double-buffering-in-swing
		// http://www.cokeandcode.com/info/tut2d.html
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g); // possible slow parent method to fill

			g.setColor(trace.color);
			// trace.xpos+=timeDivisor;
			// g.drawImage(b1, trace.xpos, 0, b1.getWidth(), b1.getHeight(), this);
			// g.drawImage(bufferImage, 0, 0, null); <-- FIXME - update buffer
			// outside of this method
			// g.drawLine(trace.x, trace.y + h / 2, trace.xpos,
			// trace.pinData.value * 20 + h / 2);
			// log.info("{},{} {}, {}", trace.x, trace.y + h / 2, trace.xpos,
			// trace.pinData.value * 20 + h / 2);
			// g.drawLine(trace.x, trace.y+20, trace.xpos, trace.pinData.value +
			// 20);
			
		}
	}

	public class Trace implements ActionListener {
		JButton b;
		PinDefinition pinDef;
		Color color;
		Oscope oscope;
		ActionListener relay;
		TraceScreen screenDisplay;
		int width = 600;
		int height = 100;
		

		int xpos;
		int x;
		int y;
		PinData pinData;
		PinData lastPinData;
		// BufferedImage buffer;

		/*
		BufferedImage buffer0 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		BufferedImage buffer1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		*/

		public Trace(Oscope oscope, PinDefinition pinDef, Color hsv) {
			this.oscope = oscope;
			this.pinDef = pinDef;
			b = new JButton(pinDef.getName());
			b.setMargin(new Insets(0, 0, 0, 0));
			b.setBorder(null);
			b.setPreferredSize(new Dimension(30, 30));
			b.setBackground(hsv);
			color = hsv;
			b.addActionListener(this);
			// relay
			addActionListener(oscope);
			

			/*
			 * screen = new BufferedImage(width, height,
			 * BufferedImage.TYPE_INT_RGB);
			 * 
			 * g2d = screen.createGraphics(); g2d.setBackground(Color.BLACK);
			 * g2d.fillRect(0, 0, width, height); g2d.setColor(color);
			 * BasicStroke bs = new BasicStroke(1); g2d.setStroke(bs);
			 */
			/*
			g2d = buffer0.createGraphics(); 
			g2d.setBackground(Color.BLACK);
			g2d.dispose();
			
			g2d = buffer1.createGraphics(); 
			g2d.setBackground(Color.GREEN);
			g2d.dispose();
			*/
			screenDisplay = new TraceScreen(this);

		}

		public Component getButtonDisplay() {
			return b;
		}

		/**
		 * events from button components are relayed to
		 * 
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			e.setSource(this);
			relay.actionPerformed(e);
		}

		public void addActionListener(ActionListener relay) {
			this.relay = relay;
		}

		public PinDefinition getPinDef() {
			return pinDef;
		}

		public Component getScreenDisplay() {
			return screenDisplay;
		}

		public void update(PinData pinData) {
			screenDisplay.setVisible(true);
			screenDisplay.update(pinData);
		}

	}

	public void onPinList(List<PinDefinition> pinList) {
		traces.clear();
		addButtons(pinList);
	}

	public Oscope(String boundServiceName, SwingGui myService) {
		super(boundServiceName, myService);
		addTop(new JButton("clear"));
		JPanel flow = new JPanel();
		flow.add(buttonPanel);
		addLeftLine(flow);
		flow = new JPanel();
		flow.add(screenPanel);
		add(flow);

		// since this is a widget - subscribeGui is not auto-magically called
		// by the framework
		subscribeGui();
		// video.displayFrame(sensorImage);
	}

	public void subscribeGui() {
		subscribe("publishPinDefinition");
		subscribe("publishPinArray");
		subscribe("getPinList");
	}

	public void unsubscribeGui() {
		unsubscribe("publishPinDefinition");
		unsubscribe("publishPinArray");
		unsubscribe("getPinList");
	}

	// enabled -> true | false
	public void onPinDefinition(PinDefinition pinDef) {
		updatePinDisplay(pinDef);
	}

	public void updatePinDisplay(PinDefinition pinDef) {

	}

	public void addButtons(List<PinDefinition> pinList) {

		gradient = 1.0f / pinList.size();

		bgc.gridy = bgc.gridx = 0;
		sgc.gridy = sgc.gridx = 0;

		// gc.fill = GridBagConstraints.BOTH; // ???
		bgc.fill = GridBagConstraints.HORIZONTAL;
		sgc.fill = GridBagConstraints.HORIZONTAL;
		bgc.weighty = bgc.weightx = 1;
		sgc.weighty = sgc.weighty = 1;

		// List<PinDefinition> pinList = pinDefs.getList();
		for (int i = 0; i < pinList.size(); ++i) {
			PinDefinition pinDef = pinList.get(i);
			Color hsv = new Color(Color.HSBtoRGB((i * (gradient)), 0.5f, 1.0f));
			Trace trace = new Trace(this, pinDef, hsv);
			traces.put(pinDef.getName(), trace);
			traceIndex.put(pinDef.getAddress(), trace);
			buttonPanel.add(trace.getButtonDisplay(), bgc);
			screenPanel.add(trace.getScreenDisplay(), bgc);
			bgc.gridx++;
			sgc.gridy++;

			if (bgc.gridx % 2 == 0) {
				bgc.gridx = 0;
				bgc.gridy++;
			}

		}

	}

	public void initialize() {
		data = new int[h * w];

		// Fill data array with pure solid black
		Arrays.fill(data, 0xff000000);

		// Java's endless black magic to get it working
		DataBufferInt db = new DataBufferInt(data, h * w);
		ColorModel cm = ColorModel.getRGBdefault();
		SampleModel sm = cm.createCompatibleSampleModel(w, h);
		WritableRaster wr = Raster.createWritableRaster(sm, db, null);
		BufferedImage buffer = new BufferedImage(cm, wr, false, null);
		SerializableImage sensorImage = new SerializableImage(buffer, "oscope");
	}

	// ??? - onPaint in extended JPanel or JLabel ???
	public void onPinArray(final PinData[] data) {
		for (PinData pinData : data) {

			Trace trace = traceIndex.get(pinData.address);
			trace.update(pinData);
			// PinDefinition pinDef = trace.getPinDef();

			// log.info("PinData:{}", pin);
			// sensorImage.setSource(pinDef.getName());
			// video.displayFrame(sensorImage);

			/*
			 * if (!traceData.containsKey(pin.address)) { TraceData td = new
			 * TraceData(); float gradient = 1.0f / pinComponentList.size();
			 * Color color = new Color(Color.HSBtoRGB((pin.address *
			 * (gradient)), 0.8f, 0.7f)); td.color = color;
			 * traceData.put(pin.address, td); td.index = lastTraceXPos; }
			 * 
			 * int value = pin.value / 2;
			 * 
			 * TraceData t = traceData.get(pin.address); t.index++;
			 * lastTraceXPos = t.index; t.data[t.index] = value; ++t.total;
			 * t.sum += value; t.mean = t.sum / t.total;
			 * 
			 * g.setColor(t.color);
			 * 
			 * int yoffset = pin.address * 15 + 35; int quantum = -10;
			 * 
			 * g.drawLine(t.index, t.data[t.index - 1] * quantum + yoffset,
			 * t.index, value * quantum + yoffset);
			 * 
			 * // computer min max and mean // if different then blank & post to
			 * screen if (value > t.max) t.max = value; if (value < t.min) t.min
			 * = value;
			 * 
			 * if (t.index < DATA_WIDTH - 1) { } else { // TODO - when hit marks
			 * all startTracePos - cause the // screen is // blank - must
			 * iterate through all t.index = 0;
			 * 
			 * clearScreen(); drawGrid();
			 * 
			 * g.setColor(Color.BLACK); g.fillRect(20, t.pin * 15 + 5, 200, 15);
			 * g.setColor(t.color);
			 * 
			 * g.drawString(String.format("min %d max %d mean %d ", t.min,
			 * t.max, t.mean), 20, t.pin * 15 + 20);
			 * 
			 * t.total = 0; t.sum = 0;
			 * 
			 * }
			 */
		}

		// video.displayFrame(sensorImage);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Add relay ?
		Object o = e.getSource();
		if (o instanceof Trace) {
			Trace b = (Trace) o;
			PinDefinition pinDef = b.getPinDef();
			if (pinDef.isEnabled()) {
				send("disablePin", pinDef.getAddress());
			} else {
				send("enablePin", pinDef.getAddress());
			}
		}
	}
}
