import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.midi.*;

class Casino {
	private Player player = new Player();
	private SoundGenerator midi = new SoundGenerator();

	private JFrame frame;



	private boolean isRunning = false;

	private JTextField one;
	private JTextField two;
	private JTextField three;

	private JPanel centerPanel;
	private Color themeColor;

	private JTextField[] bars = new JTextField[3];

	static JLabel balanceText;

	public static void main(String[] args) {
		Casino c = new Casino();
		c.setUpGui();
		//c.load();
	}

	private void setUpGui() {
		Font bigFont = new Font("arial", Font.BOLD, 35);
		Font BiggerFont = new Font("arial", Font.BOLD, 90);

		themeColor = new Color(200,20,70);

		frame = new JFrame("Slots");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Files");
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new SaveListener());
		JMenuItem loadItem = new JMenuItem("Load");
		loadItem.addActionListener(new LoadListener());
		menu.add(saveItem);
		menu.add(loadItem);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);

		balanceText = new JLabel("$ " + player.getBalance());
		balanceText.setFont(bigFont);
		balanceText.setForeground(Color.green);

		JButton spinButton = new JButton("Spin");
		spinButton.setFont(bigFont);
		spinButton.addActionListener(new SpinButtonActionListener());

		JPanel topPanel = new JPanel();
		topPanel.setBackground(themeColor);
		topPanel.add(balanceText);
		frame.getContentPane().add(BorderLayout.NORTH, topPanel);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(themeColor);
		bottomPanel.add(spinButton);
		frame.getContentPane().add(BorderLayout.SOUTH, bottomPanel);

		centerPanel = new JPanel();
		centerPanel.setBackground(Color.blue);
		for(int i = 0; i < 3; i++) {
		  bars[i] = new JTextField(1);
			bars[i].setFont(BiggerFont);
			bars[i].setEditable(false);
			centerPanel.add(bars[i]);
		}

		 one = new JTextField(1);
		 one.setFont(BiggerFont);
		 one.setEditable(false);

		 two = new JTextField(1);
		 two.setFont(BiggerFont);
		 two.setEditable(false);

		 three = new JTextField(1);
		 three.setFont(BiggerFont);
		 three.setEditable(false);

		 frame.getContentPane().add(BorderLayout.CENTER, centerPanel);


		frame.setSize(500,500);
		frame.setVisible(true);
	}

	private void spin(JTextField[] _bars) {
		int[] values = new int[3];

		for(int i = 0; i < 3; i++) {
			int ranNum = (int) (Math.random() * 2);
			values[i] = ranNum;
			_bars[i].setText(Integer.toString(ranNum));
		}

		boolean win = values[0] == values[1] && values[2] == values[0];
		//System.out.println(values[0] + " " + values[1] + " " + values[2]);

		if(win) {
			Thread winThread = new Thread(new homo());
			winThread.start();
			System.out.println("WIN!");
		} else {
			System.out.println("No win.");
			isRunning = false;
		}
	}

	class homo implements Runnable {
		public void run() {
			onWin();
		}
	}

	private void onWin() {
		player.manageBalance(80, true);
		for(int i = 0; i < 3; i++) {
			midi.playSound(10,100, 1);
		  centerPanel.setBackground(Color.yellow);
			try {
			 	Thread.sleep(150);
			} catch (Exception ex) {ex.printStackTrace(); }
			centerPanel.setBackground(Color.blue);
		}
		isRunning = false;
	}

	class SpinButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if(!isRunning && player.getBalance() >= 5) {
				isRunning = true;
				midi.playSound(30,120, 1);
				player.manageBalance(5, false);
				spin(bars);
			}
			else if (player.getBalance() < 5){
				midi.playSound(30,20, 1);
				System.out.println("Not enough monei");
			} else  {
				System.out.println("Already running.");
				System.out.println(isRunning);
			}
		}
	}

	class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			save();
		}
	}

	class LoadListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			load();
		}
	}

	private void save() {
		try {
			ObjectOutputStream OOS = new ObjectOutputStream(new FileOutputStream(new File("player.ser")));
			OOS.writeObject(player);
			OOS.close();
		} catch (Exception ex) {ex.printStackTrace(); }
		System.out.println("Saved.");
	}

	private void load() {
		try {
			ObjectInputStream OIS = new ObjectInputStream(new FileInputStream(new File("player.ser")));
			player = (Player) OIS.readObject();
			OIS.close();
		} catch (Exception ex) {ex.printStackTrace(); System.out.println("No saves found."); }
		player.UpdateText();
		System.out.println("Loaded.");
	}

}

	class Player implements Serializable {

	private double balance = 100f;

	public double getBalance() {
		return balance;
	}

	public void UpdateText() {
		Casino.balanceText.setText("$ " + this.balance);
	}

	public void manageBalance(float balance, boolean additon) {
		this.balance = additon ? this.balance + balance : this.balance - balance;
		UpdateText();
	}
}

	class SoundGenerator {

		public void playSound(int _instrument, int _note, int burst) {
			try {
				for(int i = 0; i < burst; i++) {
				Sequencer player = MidiSystem.getSequencer();
				player.open();
				Sequence seq = new Sequence(Sequence.PPQ, 4);
				Track track = seq.createTrack();

				MidiEvent event = null;


				ShortMessage first = new ShortMessage();
				first.setMessage(192, 1, _instrument, 0);
				MidiEvent changeInstrument = new MidiEvent(first, 1);
				track.add(changeInstrument);


					ShortMessage a = new ShortMessage();
					a.setMessage(144, 1, _note, 100);
					MidiEvent NoteOn = new MidiEvent(a, 1);
					track.add(NoteOn);

					ShortMessage b = new ShortMessage();
					b.setMessage(128, 1, _note, 100);
					MidiEvent NoteOff = new MidiEvent(b, 2);
					track.add(NoteOff);
					player.setSequence(seq);
					player.start();
					if(burst > 1) {
						try {
						 	Thread.sleep(120);
						} catch (Exception ex) {ex.printStackTrace(); }
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}



}
