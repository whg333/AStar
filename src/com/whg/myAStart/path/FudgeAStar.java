package com.whg.myAStart.path;

import java.awt.Point;

import com.whg.myAStar.TileCell;

public class FudgeAStar extends ClassicAStar{

	/** An implementation of Amit Patel's 'fudge' huristic. */
	@Override
	protected double manhattanDistance(TileCell from, TileCell to, double low){
		Point a = from.getPosition();
		Point b = to.getPosition();
		
		double dx1 = a.x - b.x;
		double dy1 = a.y - b.y;
		double dx2 = start.getPosition().x - b.x;
		double dy2 = start.getPosition().y - b.y;
		double cross = dx1 * dy2 - dx2 * dy1;
		if (cross < 0)
			cross = -cross; // absolute value

		return low * (Math.abs(dx1) + Math.abs(dy1) + cross * 0.0002);

		// return low * (Math.abs(a.x-b.x)+Math.abs(a.y-b.y)-1);
	}
	
}
