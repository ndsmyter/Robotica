package emulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import brains.Particle;

@SuppressWarnings("serial")
public class ParticleViewer extends JFrame implements ActionListener {

	private ParticlePanel particlePanel;
	private JComboBox<Particle> particleBox;

	public ParticleViewer(Emulator emulator) {
		super("Particle Vierwer");

		particlePanel = new ParticlePanel(emulator);
		particleBox = new JComboBox<Particle>();
		setParticles(emulator.getBrains().getParticles());

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(particleBox, BorderLayout.NORTH);
		panel.add(particlePanel, BorderLayout.CENTER);

		particlePanel.setParticle((Particle) particleBox.getSelectedItem());

		this.setContentPane(panel);
		this.setPreferredSize(new Dimension(600, 600));
		this.pack();
		this.setVisible(true);
	}

	public void setParticles(List<Particle> particles) {
		particleBox.removeActionListener(this);
		particleBox.removeAllItems();
		for (Particle particle : particles)
			particleBox.addItem(particle);
		particleBox.addActionListener(this);
		if (!particles.isEmpty())
			particleBox.setSelectedItem(particles.get(0));
	}

	public void viewUpdated() {
		particlePanel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		particlePanel.setParticle((Particle) particleBox.getSelectedItem());
	}
}
