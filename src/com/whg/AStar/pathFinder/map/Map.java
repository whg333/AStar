package com.whg.AStar.pathFinder.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Map extends java.awt.Panel implements Serializable {
	
	private int w = 20;
	private int h = 20;
	
	/** double buffer即双缓冲机制，去除闪烁 */
	private transient Image buffer;

	private GridCell[][] gridCell = new GridCell[w][h];

	public Map() {
		super();
		// {{INIT_CONTROLS
		setLayout(new GridLayout(w, h));
		setSize(getInsets().left + getInsets().right + 430, getInsets().top + getInsets().bottom + 270);
		// }}
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				gridCell[j][i] = new GridCell();
				gridCell[j][i].setPosition(new Point(j, i));
				add(gridCell[j][i]);
			}
		}
	}
	
	public int witdth(){
		return w;
	}
	
	public int height(){
		return h;
	}
	
	public GridCell cell(int j, int i){
		return gridCell[j][i];
	}

	@Override
	public void paint(Graphics g) {
		if (buffer == null) {
			buffer = createImage(getBounds().width, getBounds().height);
		}
		Graphics bg = buffer.getGraphics();
		super.paint(bg);
		bg.setColor(Color.black);
		g.drawImage(buffer, 0, 0, null);
		// g.drawRect(0,0,getBounds().width-1,getBounds().height-1);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public Point getStartPosition() {
		GridCell start = GridCell.getStartCell();
		return start.getPosition();
	}

	/** 获取相邻的地图板块中距离最短的，即到开始距离最短的（或者说cost最小的） */
	public GridCell getLowestAdjacent(GridCell g) {
		GridCell next[] = getAdjacent(g);
		GridCell small = next[0];
		double dist = Double.MAX_VALUE;
		for (int i = 0; i < next.length; i++) {
			if (next[i] == null) {
				continue;
			}
			double nextDist = next[i].getDistFromStart();
			if (nextDist < dist && nextDist >= 0) {
				small = next[i];
				dist = next[i].getDistFromStart();
			}
		}
		return small;
	}
	
	/** 获取相邻的地图板块 */
	public GridCell[] getAdjacent(GridCell g) {
		GridCell next[] = new GridCell[8];
		Point p = g.getPosition();
		if (p.y != 0) {
			next[0] = gridCell[p.x][p.y - 1];		//top
		}
		if (p.x != w - 1) {
			next[1] = gridCell[p.x + 1][p.y];		//right
		}
		if (p.y != h - 1) {
			next[2] = gridCell[p.x][p.y + 1];		//bottom
		}
		if (p.x != 0) {
			next[3] = gridCell[p.x - 1][p.y];		//left
		}
		if(p.y != 0 && p.x != 0 && (!next[0].isTotalBlock() || !next[3].isTotalBlock())){
			next[4] = gridCell[p.x - 1][p.y - 1];	//left-top
			if(!next[4].isTotalBlock()) next[4].setCost(1.41);
		}
		if(p.y != 0 && p.x != w - 1 && (!next[0].isTotalBlock() || !next[1].isTotalBlock())){
			next[5] = gridCell[p.x + 1][p.y - 1];	//right-top
			if(!next[5].isTotalBlock()) next[5].setCost(1.41);
		}
		if(p.x != 0 && p.y != h - 1 && (!next[3].isTotalBlock() || !next[2].isTotalBlock())){
			next[6] = gridCell[p.x - 1][p.y + 1];	//left-bottom
			if(!next[6].isTotalBlock()) next[6].setCost(1.41);
		}
		if(p.x != w - 1 && p.y != h - 1 && (!next[1].isTotalBlock() || !next[2].isTotalBlock())){
			next[7] = gridCell[p.x + 1][p.y + 1];	//right-bottom
			if(!next[7].isTotalBlock()) next[7].setCost(1.41);
		}
		return next;
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		GridCell.tidy = false;
		ois.defaultReadObject();
		GridCell.setShowPath(false);
	}
	// {{REGISTER_LISTENERS
	// };
	// {{DECLARE_CONTROLS
	// }}

}