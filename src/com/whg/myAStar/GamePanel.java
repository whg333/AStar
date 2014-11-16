package com.whg.myAStar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.whg.myAStart.path.AStar;

public class GamePanel extends JPanel implements MouseInputListener, KeyListener, Runnable {

	private static final long serialVersionUID = 5898969301038642894L;

	private static Random random = new Random();

	public Image offScreenImage; // 用于绘制游戏图像
	private Thread gameLoop;
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	private TileMap map;
	private Set<Player> players;
	private Player player;
	private Player player2;
	
	private Set<Player> actors = new HashSet<Player>();
	private Point mousePressedPoint;
	private Rectangle drawRouseDraggedRect = new Rectangle();
	
	private Map<Integer, Future<?>> findPathFutureMap = new HashMap<Integer, Future<?>>();
	private Map<Integer, AStar> findPathTaskMap = new HashMap<Integer, AStar>();

	public GamePanel() {
		init();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		start();
	}

	private void init() {
		map = new TileMap();
		
		player = new Player(1, 50, 48, 0, 0, "death_scythe.png");
		player2 = new Player(2, 48, 64, 50, 50, "ryuk.png");
		players = new HashSet<Player>();
		players.add(player);
		players.add(player2);
	}

	private void start() {
		gameLoop = new Thread(this);
		gameLoop.start();
	}

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		while (currentThread == gameLoop) {
			updateGame();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}

	private void updateGame() {
		updatePlayers();
	}
	
	private void updatePlayers(){
		for(Player currPlayer:players){
			currPlayer.update();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// keyChar = e.getKeyChar();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_UP) {
			player.moveUp();
		} else if (keyCode == KeyEvent.VK_DOWN) {
			player.moveDown(getHeight());
		} else if (keyCode == KeyEvent.VK_LEFT) {
			player.moveLeft();
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			player.moveRight(getWidth());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		player.idle();
	}

	@Override
	public void paintComponent(Graphics g) {
		if (offScreenImage == null) {
			// Component的createImage()方法创建一幅用于双缓冲的、可在屏幕外绘制的图像。参数指定该宽度和高度
			offScreenImage = createImage(getWidth(), getHeight());
		}
		// Image的getGraphics()方法创建供绘制闭屏图像使用的图形上下文
		drawGame(offScreenImage.getGraphics());
		// Graphics的drawImage()方法绘制指定图像中当前可用的图像。
		// 第一个参数表示要绘制的指定的图像,第二和第三个参数表示指定图像的x,y坐标,第四个参数表示当转换了更多图像时要通知的对象。
		g.drawImage(offScreenImage, 0, 0, this);
	}

	private void drawGame(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// if (random.nextInt(100) > 70) {
		// randomDraw(g2d);
		// }

		TileMap map = actors.isEmpty() ? this.map : actorsMap();
		drawMap(g2d, map);
		drawPlayers(g2d, map);
		drawMouseDragged(g2d);
		drawActors(g2d);
	}

	private TileMap actorsMap() {
		 Player player = actors.iterator().next();
		 AStar astart = findPathTaskMap.get(player.getId());
		 return astart == null ? this.map : astart.getMap();
	}

	private void drawMap(Graphics2D g2d, TileMap map) {
		for (TileCell[] columns : map.getCells()) {
			for (TileCell cell : columns) {
				if (cell.isEnd()) {
					drawCellWithColor(g2d, cell, Color.GREEN);
				} else if (cell.isPath()) {
					if (!cell.isStart()) {
						drawCellWithColor(g2d, cell, Color.YELLOW);
					}
					drawCellG(g2d, cell);
				} else if (!cell.isCanPass()) {
					drawCellWithColor(g2d, cell, Color.DARK_GRAY);
				} else {
					g2d.setColor(Color.BLACK);
					g2d.drawRect(cell.getX(), cell.getY(), TileCell.WIDTH, TileCell.HEIGHT);
					drawCellG(g2d, cell);
				}
			}
		}
	}

	private void drawCellWithColor(Graphics2D g2d, TileCell cell, Color color) {
		g2d.setColor(color);
		g2d.fill(new Rectangle(cell.getX() + 2, cell.getY() + 2, TileCell.WIDTH - 3, TileCell.HEIGHT - 3));
		g2d.setColor(Color.BLACK);
		g2d.drawRect(cell.getX(), cell.getY(), TileCell.WIDTH, TileCell.HEIGHT);
	}

	private void drawCellG(Graphics2D g2d, TileCell cell) {
		if (!cell.isShow() || cell.getG() < 0) {
			return;
		}
		g2d.drawString("" + cell.getG(), cell.getX(), (cell.getY() + TileCell.WIDTH / 2));
		// System.out.println("draw... g=" +
		// cell.getG()+", x="+cell.getX()+", y="+ (cell.getY()+RECT_SIZE/2));
	}
	
	private void drawPlayers(Graphics2D g2d, TileMap map) {
//		g2d.setColor(Color.LIGHT_GRAY);
//		g2d.fill(new Rectangle(player.getX(), player.getY(), TileCell.WIDTH, TileCell.HEIGHT));
//		g2d.setColor(Color.BLACK);
//		g2d.drawRect(player.getX(), player.getY(), TileCell.WIDTH, TileCell.HEIGHT);

		for(Player currPlayer:players){
			currPlayer.draw(g2d);
		}
	}
	
	private void drawMouseDragged(Graphics2D g2d) {
		g2d.setColor(Color.GREEN);
		g2d.drawRect(drawRouseDraggedRect.x, drawRouseDraggedRect.y, 
				drawRouseDraggedRect.width, drawRouseDraggedRect.height);
	}
	
	private void drawActors(Graphics2D g2d) {
		g2d.setColor(Color.GREEN);
		for(Player actor:actors){
			g2d.drawRect(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
		}
	}

	private void randomDraw(Graphics2D g2d) {
		int w = random.nextInt(100);
		int h = random.nextInt(100);
		int x = random.nextInt(getSize().width - w);
		int y = random.nextInt(getSize().height - h);
		Rectangle rectangle = new Rectangle(x, y, w, h);

		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);

		g2d.setColor(new Color(red, green, blue));
		g2d.fill(rectangle);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// System.out.println("mouseClicked:"+e.getX()+", "+e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// System.out.println("mouseEntered:"+e.getX()+", "+e.getY());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// System.out.println("mouseExited:"+e.getX()+", "+e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressedPoint = e.getPoint();
		
		TileCell targetCell = map.getCell(TileCell.transformCellPoint(e.getPoint()));
		if (targetCell == null || !targetCell.isCanPass()) {
			return;
		}
		
		if(isMouseLeftKey(e)){
			recollectActors(targetCell);
			return;
		}
		// System.out.println(targetCell);
		
		actorsCancelTaskAndSubmitNew(targetCell);
		
	}
	
	private void recollectActors(TileCell targetCell){
		actors.clear();
		for(Player player:players){
			if(!player.isCollision(targetCell.getRectangle())){
				continue;
			}
			actors.add(player);
		}
	}
	
	private void actorsCancelTaskAndSubmitNew(TileCell targetCell){
		for(Player actor:actors){
			cancelTaskAndSubmitNew(actor, targetCell);
		}
	}
	
	private void cancelTaskAndSubmitNew(Player actor, TileCell targetCell){
		AStar lastFindPath = findPathTaskMap.get(actor.getId());
		//如果终点和当前任务查找的终点一样就不重新再找了
		if(lastFindPath != null && lastFindPath.isEnd(targetCell)){
			return;
		}
		
		Future<?> lastfindPathTask = findPathFutureMap.get(actor.getId());
		if(lastfindPathTask != null 
				&& !lastfindPathTask.isDone()
				&& lastfindPathTask.cancel(true)){
				System.out.println("cancel——"+lastfindPathTask);
		}
		
		AStar task = actor.newFindPathTask(map, targetCell);
		Future<?> findPathFuture = executor.submit(task);
		findPathFutureMap.put(actor.getId(), findPathFuture);
		findPathTaskMap.put(actor.getId(), task);
		System.out.println("init——"+findPathFutureMap.get(actor.getId()));
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// System.out.println("mouseReleased:"+e.getX()+", "+e.getY());
		drawRouseDraggedRect.setSize(0, 0);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//actors.clear();
		
		Point mouseDraggedPoint = e.getPoint();
		int mouseDraggedWidth = (int)(mouseDraggedPoint.x - mousePressedPoint.x);
		int mouseDraggedHeight = (int)(mouseDraggedPoint.y - mousePressedPoint.y);
		
		//根据玩家鼠标拖拽来变换绘制鼠标拖拽矩形的基点
		if(mouseDraggedWidth < 0 && mouseDraggedHeight < 0){
		}else if(mouseDraggedWidth < 0){
			mouseDraggedPoint.y -= mouseDraggedHeight;
		}else if(mouseDraggedHeight < 0){
			mouseDraggedPoint.x -= mouseDraggedWidth;
		}else{
			mouseDraggedPoint = mousePressedPoint;
		}
		
		//绘制鼠标拖拽矩形的长和宽必须是正数
		mouseDraggedWidth = Math.abs(mouseDraggedWidth);
		mouseDraggedHeight = Math.abs(mouseDraggedHeight);
		
		drawRouseDraggedRect.setBounds(mouseDraggedPoint.x, mouseDraggedPoint.y, mouseDraggedWidth, mouseDraggedHeight);;
		for(Player player:players){
			if(!player.isCollision(drawRouseDraggedRect)){
				continue;
			}
			actors.add(player);
		}
	}
	
	private boolean isMouseLeftKey(MouseEvent e){
		return e.getButton() == MouseEvent.BUTTON1;
	}
	
	private boolean isMouseRightKey(MouseEvent e){
		return e.getButton() == MouseEvent.BUTTON3;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

}
