package com.whg.AStar.pathFinder;

import java.util.Vector;

import com.whg.AStar.pathFinder.map.GridCell;
import com.whg.AStar.pathFinder.map.Map;

/**
 * 老版本且低效率的A*算法，不会把已经寻路过的节点从open列表删除，每次都重头遍历所有节点；
 * close列表是每次都加上已寻路过的节点且有重复项，根本就没有用处这里的close列表，导致效率极低；
 *
 */
public class OneTailAStar implements PathFinder, Runnable {
	
	protected static final int NO_PATH = -1, NOT_FOUND = 0, FOUND = 1;
	private static final int MAX_STEP = 1000;

	protected Vector<GridCell> open;
	protected Vector<GridCell> close;
	protected Map map;
	protected int stepSpeed = 100;// higher is slower
	
	private Thread loop;
	private double distFromStart = 0;
	private boolean findFirst = false;

	@Override
	public GridCell[] findPath(Map map) {
		this.map = map;
		GridCell.reset();
		open = new Vector<GridCell>();
		close = new Vector<GridCell>();
		
		System.out.println("calculating route");
		if (GridCell.getStartCell() == null) {
			System.out.println("No start point set");
			return null;
		}
		if (GridCell.getFinishCell() == null) {
			System.out.println("No finish point set");
			return null;
		}
		System.out.println("Starting from " + map.getStartPosition());
		
		loop = new Thread(this);
		loop.start();
		return null;
	}

	@Override
	public void run() {
		open.addElement(GridCell.getStartCell());
		int pass = 0;
		boolean found = false;
		double start, diff;
		int state = NOT_FOUND;
		
		while (state == NOT_FOUND && pass < MAX_STEP) {
			pass++;
			start = System.currentTimeMillis();
			state = step();
			diff = System.currentTimeMillis() - start;
			try {
				Thread.sleep(Math.max((long) (stepSpeed - diff), 0));
				//TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
			}
			// System.out.println(diff);
		}
		
		if (state == FOUND) {
			showPath(map);
		} else {
			System.out.println("No Path Found");
		}
	}

	/** 每一次遍历周围地图板块查找的步骤 */
	protected int step() {
		int tests = 0;
		boolean found = false;
		boolean growth = false;
		GridCell finish = GridCell.getFinishCell();
		Vector<GridCell> snapshot = (Vector<GridCell>) open.clone();
		
		for (int i = 0; i < snapshot.size(); i++) {
			GridCell now = snapshot.elementAt(i);
			GridCell next[] = map.getAdjacent(now);
			for (int j = 0; j < next.length; j++) {
				if (next[j] == null) {
					continue;
				}
				if (next[j] == finish) {
					found = true;
					break;
				}
				next[j].addToPathFromStart(now.getDistFromStart());
				tests++;
				if (!next[j].isTotalBlock() && !open.contains(next[j])) {
					open.addElement(next[j]);
					growth = true;
				}
			}
			
			if (found) {
				return FOUND;
			}
			
			//close列表在此处根本没用，就是用来拖垮性能的，此处语句注释掉也没问题
			close.addElement(now);
			// edge.removeElement(now);
		}
		
		map.repaint();
		if (!growth) {
			return NO_PATH;
		}
		// System.out.println("Tests:"+tests+" Edge:"+edge.size()+" Done:"+done.size());
		return NOT_FOUND;
	}

	/** 显示最短路径 */
	private void showPath(Map map) {
		System.out.println("Path Found");
		GridCell.setShowPath(true);
		boolean finished = false;
		GridCell next;
		GridCell now = GridCell.getFinishCell();
		GridCell stop = GridCell.getStartCell();
		while (!finished) {
			next = map.getLowestAdjacent(now);
			now = next;
			now.setPartOfPath(true);
			now.repaint();
			if (now == stop) {
				finished = true;
			}
			try {
				Thread.sleep(stepSpeed);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("Done");
	}

}