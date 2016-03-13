import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.whg.myAStar.GamePanel;
import com.whg.myAStar.MainView;

/**
 * 即可当做Applet运行，又可以当做Application运行的AStar。<br>
 * 当做Applet是因为继承了JApplet作为容器，把GamePanel添加到该容器面板中。<br>
 * 而作为Application是又在MainApplet外层包装了一个JFrame容器，把MainApplet添加到该容器面板中。
 */
public class MainApplet extends JApplet {

	private static final long serialVersionUID = 5898969301038642894L;

	public MainApplet() {
		// 创建用以绘制游戏画面的画板mainPanel
		GamePanel gamePanel = new GamePanel();
		gamePanel.initGUI();
		
		getContentPane().add(gamePanel);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				new JFrame("A星寻路Applet"){
					private static final long serialVersionUID = 3075306026918163429L;
					{
						getContentPane().add(new MainApplet());
						setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						setBounds(10, 5, MainView.WIDTH, MainView.HEIGHT);
						setVisible(true);
						setResizable(false);
					}
				};
			}
		});
	}

}
