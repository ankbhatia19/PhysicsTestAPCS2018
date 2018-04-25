import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CustomKeyListener extends KeyAdapter {
		
		private Wheel firstWheel;
		private Wheel secondWheel;
		
		public CustomKeyListener(Wheel wheel1, Wheel wheel2)
		{
			firstWheel = wheel1;
			secondWheel = wheel2;
		}
		
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				firstWheel.setLeft(true);
				break;
			case KeyEvent.VK_RIGHT:
				firstWheel.setRight(true);
				break;
			case KeyEvent.VK_UP:
				firstWheel.setUp(true);
				break;
			case KeyEvent.VK_W:
				secondWheel.setUp(true);
				break;
			case KeyEvent.VK_A:
				secondWheel.setLeft(true);
				break;
			case KeyEvent.VK_D:
				secondWheel.setRight(true);
				break;

			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				firstWheel.setLeft(false);
				break;
			case KeyEvent.VK_RIGHT:
				firstWheel.setRight(false);
				break;
			case KeyEvent.VK_UP:
				firstWheel.setUp(false);
				break;
			case KeyEvent.VK_W:
				secondWheel.setUp(false);
				break;
			case KeyEvent.VK_A:
				secondWheel.setLeft(false);
				break;
			case KeyEvent.VK_D:
				secondWheel.setRight(false);
				break;
			}
		}
	}
