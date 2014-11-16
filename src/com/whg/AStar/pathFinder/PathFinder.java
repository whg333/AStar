package com.whg.AStar.pathFinder;

import com.whg.AStar.pathFinder.map.GridCell;
import com.whg.AStar.pathFinder.map.Map;

public interface PathFinder {
	
	/**
	 * Finds the shortest route through the map and returns an array of all of
	 * the cells.
	 */
	public GridCell[] findPath(Map map);
	
}