import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class Casino {
	private Player player = new Player();
	private JTextField one;
	private JTextField two;
	private JTextField three;

	private JTextField[] bars = new JTextField[3];

	private JTextField balanceText;

	public static void main(String[] args) {
		Casino c = new Casino();
		c.setUpGui();
	}

	private void setUpGui() {
		Font bigFont = new Font("arial", Font.BOLD, 25);
		Font BiggerFont = new Font("arial", Font.BOLD, 140);

		Color themeColor = new Color(200,200,20);

		balanceText = new JTextField(player.getBalanceString(), 10);
		balanceText.setFont(bigFont);
		balanceText.setEditable(false);

		JFrame frame = new JFrame("Slots");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

		JPanel centerPanel = new JPanel();
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




	void spin(JTextField[] _bars) {
		int[] values = new int[3];

		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 100; j++) {
				int ranNum = (int) (Math.random() * 5);
				values[i] = ranNum;
				try {
					System.out.println(ranNum);
					_bars[i].setText(Integer.toString(ranNum));
					//Thread.sleep(1000);
				} catch (Exception ex) {ex.printStackTrace(); }
			}
		}

		boolean win = values[0] == values[1] && values[2] == values[0];
		System.out.println(values[0] + " " + values[1] + " " + values[2]);

		if(win) {
			System.out.println("WIN!");
			player.manageBalance(80, true);
		} else {
			System.out.println("No win.");
		}
	}


	class SpinButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
						if(player.getBalance() >= 5) {
								player.manageBalance(5, false);
								spin(bars);
						}
						else
							System.out.println("Not enough monei");
		}
	}

	private void save() {

	}

	class Player implements Serializable {
		private double balance = 100f;

		public double getBalance() {
			return balance;
		}
		public String getBalanceString() {
			return  "$ " + balance;
		}
		public void manageBalance(float balance, boolean additon) {
			this.balance = additon ? this.balance + balance : this.balance - balance;
			balanceText.setText(getBalanceString());
			save();
		}

	}


}
