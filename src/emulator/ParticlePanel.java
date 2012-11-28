package emulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

import javax.swing.JPanel;

import brains.Particle;

import common.Config;

public class ParticlePanel extends JPanel {
	private Particle particle = null;
	private double scale = 0.2;
	private final Emulator emulator;

	public ParticlePanel(Emulator emulator) {
		this.emulator = emulator;
	}

	public void setParticle(Particle particle) {
		this.particle = particle;
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (particle == null)
			return;

		Graphics2D g2 = (Graphics2D) g;

		// Move the (0,0) point to middle of screen
		g.translate(getWidth() / 2, getHeight() / 2);
		g2.scale(1, -1);

		// Draw map
		drawMap(g2);

		// Draw previous points
		drawPreviousPoints(g);
	}

	private void drawMap(Graphics g) {
		try {
			g.setColor(MapPanel.MAP_COLOR);
			int gridSize = scale(Config.GRID_CELL_SIZE);
			int halfGridSize = (int) (0.5 * Config.GRID_CELL_SIZE);
			for (Point p : emulator.getBackground()) {
				g.fillRect(scale(p.x - halfGridSize),
						scale(p.y - halfGridSize), gridSize, gridSize);
			}
		} catch (Exception e) {
		}
	}

	private void drawPreviousPoints(Graphics g) {
		try {
			g.setColor(MapPanel.PATH_COLOR);
			ArrayList<Point> historyOfPoints = particle.getMap().getPath();
			for (Point state : historyOfPoints) {
				g.drawRect(scale(state.x), scale(state.y), 1, 1);
			}
			for (int i = 0; i < historyOfPoints.size() - 1; i++) {
				Point first = historyOfPoints.get(i);
				Point last = historyOfPoints.get(i + 1);
				g.drawLine(scale(first.x), scale(first.y), scale(last.x),
						scale(last.y));
			}
		} catch (ConcurrentModificationException e) {
		}
	}

	private int scale(double value) {
		return (int) (scale2(value) + 0.5);
	}

	private double scale2(double value) {
		return scale * value;
	}

	private int descale(double value) {
		return (int) (descale2(value) + 0.5);
	}

	private double descale2(double value) {
		return value / scale;
	}
}
