package com.whg.myAStart;

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
		// 设置该mainPanel(JPanel的子类)布局为空,这样方可使用自己设置的坐标（Component的setBounds()方法）来安排组件的位置
		gamePanel.setLayout(null);
		// Component的setBounds()方法移动组件并调整其大小。由 x 和 y 指定左上角的新位置，由 width 和 height
		// 指定新的大小。
		gamePanel.setBounds(0, 22, WIDTH, HEIGHT);
		// gamePanel.setBackground(new Color(128, 64, 0)); //设置背景颜色
		getContentPane().add(gamePanel);
		gamePanel.setFocusable(true); // 画板初始时便可以获得焦点

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
