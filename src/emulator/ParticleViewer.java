package emulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import brains.Particle;

@SuppressWarnings("serial")
public class ParticleViewer extends JFrame implements ActionListener {

	private ParticlePanel particlePanel;
	private JComboBox particleBox;
	private final Emulator emulator;

	public ParticleViewer(Emulator emulator) {
		super("Particle Viewer");
		this.emulator = emulator;

		particlePanel = new ParticlePanel(emulator);
		particleBox = new JComboBox();
		setParticles(emulator.getBrains().getParticles());

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(particleBox, BorderLayout.NORTH);
		panel.add(particlePanel, BorderLayout.CENTER);

		particlePanel.setParticle((Particle) particleBox.getSelectedItem());

		this.setContentPane(panel);
		this.setPreferredSize(new Dimension(600, 600));
		this.pack();
		this.setVisible(true);

		emulator.addParticleViewer(this);
		this.addWindowListener(new WindowClosedListener(this));
	}

	public void setParticles(List<Particle> particles) {
		int index = particleBox.getSelectedIndex();
		particleBox.removeActionListener(this);
		particleBox.removeAllItems();
		for (Particle particle : particles)
			particleBox.addItem(particle);
		particleBox.addActionListener(this);
		if (!particles.isEmpty())
			particleBox.setSelectedItem(particles.get(index >= 0 ? index : 0));
	}

	public void viewUpdated() {
		particlePanel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		particlePanel.setParticle((Particle) particleBox.getSelectedItem());
	}

	private class WindowClosedListener extends WindowAdapter {
		private final ParticleViewer particleViewer;

		public WindowClosedListener(ParticleViewer particleViewer) {
			this.particleViewer = particleViewer;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent e) {
			super.windowClosed(e);
			emulator.removeParticleViewer(particleViewer);
		}
	}
}
