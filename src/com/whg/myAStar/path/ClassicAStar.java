package com.whg.myAStar.path;

import java.awt.Point;
import java.util.Iterator;

import com.whg.myAStar.TileCell;

/**
 * 经典A*算法。
 * <br/>1、首先在寻路查找（findPath）前，先找出全地图的最小cost来作为曼哈顿距离计算的基数；
 * <br/>2、每一步遍历周围地图板块前，选择open列表里面的曼哈顿距离最小的作为最优首选路径；
 * <br/>3、把已经寻路的节点挪出open列表并放入close列表；
 * <br/>4、遍历每个可通过的相邻的板块，并设置其到起点的距离值（cost），并把不在close列表的节点加入open列表；
 * <br/>5、重复步骤2，直到找到路径或者当遍历完所有地图板块后（open列表为空时）表示没有找到路径。
 *
 */
public class ClassicAStar extends OldAStar{

	private double minCost;
	
	@Override
	public Path findPath() {
		initMinCost();
		return super.findPath();
	}
	
	private void initMinCost(){
		minCost = Double.MAX_VALUE;
		
		for(TileCell[] columns:map.getCells()){
			for(TileCell row:columns){
				minCost = Math.min(row.getCost(), minCost);
			}
		}
		
		//minCost=0.9;
		//System.out.println("Cheepest Tile = " + minCost);
	}
	
	@Override
	protected boolean stepFind(){
		TileCell minFCell = findMinFCellFromOpen();
		TileCell[] adjacents = map.getAdjacents(minFCell);
		for(TileCell adjacent:adjacents){
			if(adjacent == null){
				continue;
			}
			
			if(adjacent.equals(end)){
				return true;
			}else if(!closed.contains(adjacent) && !open.contains(adjacent)){
				adjacent.addG(minFCell);
				adjacent.show();
				open.add(adjacent);
			}
		}
		
		closed.add(minFCell);
		open.remove(minFCell);
		
		slowShowFindPath();
		return false;
	}
	
	/** 选择open列表里面的曼哈顿距离最小的作为最优首选路径 */
	private TileCell findMinFCellFromOpen(){
		double minF = Double.MAX_VALUE;
		TileCell minFCell = open.get(open.size() - 1);
		Iterator<TileCell> openIt = open.iterator();
		while(openIt.hasNext()){
			TileCell cell = openIt.next();
			cell.setH(manhattanDistance(cell, end, minCost));
			if(cell.getF() < minF){
				minF = cell.getF();
				minFCell = cell;
			}
		}
		return minFCell;
	}
	
	/** 计算从from到to的加权曼哈顿距离 */
	protected double manhattanDistance(TileCell from, TileCell to, double low){
		Point a = from.getPosition();
		Point b = to.getPosition();
		return low * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y) - 1);
	}
	
}
