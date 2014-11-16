package com.whg.AStar.pathFinder;

import java.awt.Point;
import java.util.Vector;

import com.whg.AStar.pathFinder.map.GridCell;
import com.whg.AStar.pathFinder.map.Map;

/**
 * 经典A*算法。
 * <br/>1、首先在寻路查找（findPath）前，先找出全地图的最小cost来作为曼哈顿距离计算的基数；
 * <br/>2、每一步遍历周围地图板块前，选择open列表里面的曼哈顿距离最小的作为最优首选路径；
 * <br/>3、把已经寻路的节点挪出open列表并放入close列表；
 * <br/>4、遍历每个可通过的相邻的板块，并设置其到起点的距离值（cost），并把不在close列表的节点加入open列表；
 * <br/>5、重复步骤2，直到找到路径或者当遍历完所有地图板块后（open列表为空时）表示没有找到路径。
 *
 */
public class HuristicAStar extends OneTailAStar{
	
	private double minCost;

	public HuristicAStar() {
		super();
		stepSpeed = 20;
	}

	@Override
	public GridCell[] findPath(Map map) {
		initMinCost(map);
		return super.findPath(map);
	}
	
	private void initMinCost(Map map){
		minCost = Double.MAX_VALUE;
		for (int i = 0; i < map.witdth(); i++) {
			for (int j = 0; j < map.height(); j++) {
				minCost = Math.min(map.cell(j, i).getCost(), minCost);
			}
		}
		// minCost=0.9;
		System.out.println("Cheepest Tile = " + minCost);
	}

	@Override
	public int step() {
		int tests = 0;
		GridCell finish = GridCell.getFinishCell();
		Point end = finish.getPosition();
		
		Vector<GridCell> snapshot = (Vector<GridCell>) open.clone();
		// find the most promesing open cell
		double min = Double.MAX_VALUE;
		double score;
		// int best = -1;
		//当发现在open列表中有多个和值相同的，那需要选择哪个优先呢？
		//最简单（快速）的方法是一直跟着最近被添加到open列表中的方块。
		GridCell best = snapshot.elementAt(snapshot.size() - 1);
		GridCell now;
		for (int i = 0; i < snapshot.size(); i++) {
			now = snapshot.elementAt(i);
			if(close.contains(now)){
				continue;
			}
			score = now.getDistFromStart();
			score += cbDist(now.getPosition(), end, minCost);
			if (score < min) {
				min = score;
				best = now;
			}
		}
		
		now = best;
		// System.out.println(now.getPosition()+" Selected for expansion");
		open.removeElement(now);
		close.addElement(now);
		GridCell next[] = map.getAdjacent(now);
		for (int i = 0; i < next.length; i++) {
			if(next[i] == null){
				continue;
			}
			if (next[i] == finish) {
				return FOUND;
			}
			if(next[i].isTotalBlock()){
				continue;
			}
			next[i].addToPathFromStart(now.getDistFromStart());
			tests++;
			if (!open.contains(next[i]) && !close.contains(next[i])) {
				open.addElement(next[i]);
			}
		}
		
		map.repaint();
		if (open.isEmpty()) {
			return NO_PATH;
		}

		// now process best.
		return NOT_FOUND;
	}
	
	/** calculate the weighted manhattan distance from a to b (计算从a到b的加权曼哈顿距离) */
	protected double cbDist(Point a, Point b, double low) {
		return low * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y) - 1);
	}
	
	public static void main(String[] args) {
		double low = 1.0;
		Point start = new Point(0, 0);
		Point end = new Point(4, 3);
		System.out.println(cbDist1(new Point(0, 0), end, low)+", "+cbDist2(start, new Point(0, 0), end, low));
		System.out.println(cbDist1(new Point(1, 0), end, low)+", "+cbDist2(start, new Point(1, 0), end, low));
		System.out.println(cbDist1(new Point(1, 1), end, low)+", "+cbDist2(start, new Point(1, 1), end, low));
		System.out.println(cbDist1(new Point(0, 1), end, low)+", "+cbDist2(start, new Point(0, 1), end, low));
		System.out.println(cbDist1(new Point(2, 0), end, low)+", "+cbDist2(start, new Point(2, 0), end, low));
		System.out.println(cbDist1(new Point(2, 1), end, low)+", "+cbDist2(start, new Point(2, 1), end, low));
		System.out.println(cbDist1(new Point(2, 2), end, low)+", "+cbDist2(start, new Point(2, 2), end, low));
		System.out.println(cbDist1(new Point(1, 2), end, low)+", "+cbDist2(start, new Point(1, 2), end, low));
		System.out.println(cbDist1(new Point(0, 2), end, low)+", "+cbDist2(start, new Point(0, 2), end, low));
	}
	
	public static double cbDist1(Point a, Point b, double low){
		return low * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y) - 1);
	}
	
	protected static double cbDist2(Point start, Point a, Point b, double low) {
		double dx1 = a.x - b.x;
		double dy1 = a.y - b.y;
		double dx2 = start.x - b.x;
		double dy2 = start.y - b.y;
		double cross = dx1 * dy2 - dx2 * dy1;
		if (cross < 0)
			cross = -cross; // absolute value

		return low * (Math.abs(dx1) + Math.abs(dy1) + cross * 0.0002);

		// return low * (Math.abs(a.x-b.x)+Math.abs(a.y-b.y)-1);
	}

}