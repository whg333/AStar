package com.whg.myAStart.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.whg.myAStar.TileCell;

/**
 * 老版本且低效率的A*算法，每一次寻路查找步骤都是遍历删除所有open列表的同时，又加入更多的节点至open列表，直到找到目标end节点
 *
 */
public class OldAStar extends AStar{

	@Override
	protected boolean stepFind(){
		List<TileCell> cloneOpen = new ArrayList<TileCell>(open);
		Iterator<TileCell> openIt = cloneOpen.iterator();
		while(openIt.hasNext()){
			TileCell cell = openIt.next();
			TileCell[] adjacents = map.getAdjacents(cell);
			for(TileCell adjacent:adjacents){
				if(adjacent == null){
					continue;
				}
				
				if(adjacent.equals(end)){
					return true;
				}else if(!closed.contains(adjacent) && !open.contains(adjacent)){
					adjacent.addG(cell);
					adjacent.show();
					open.add(adjacent);
				}
			}
			
			closed.add(cell);
			openIt.remove();
		}
		
		slowShowFindPath();
		return false;
	}
	
}
