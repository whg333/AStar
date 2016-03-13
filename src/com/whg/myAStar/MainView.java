package com.whg.myAStar;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainView extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = TileCell.WIDTH * 24;
	public static final int HEIGHT = TileCell.HEIGHT * 22;

	public MainView() {
		super("A星寻路");
		
		// 创建用以绘制游戏画面的画板mainPanel
		GamePanel gamePanel = new GamePanel();
		gamePanel.initGUI();
		
		getContentPane().add(gamePanel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 5, WIDTH, HEIGHT);
		setVisible(true);
		setResizable(false);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				new MainView();
			}
		});
	}

}
