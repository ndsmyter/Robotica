package brains.algorithmsnew;

import brains.Brains;

public class Stopper {
	private Brains brains;

	public Stopper(Brains brains) {
		this.brains = brains;
	}

	public void execute() {
		brains.stop(true);
	}
}
