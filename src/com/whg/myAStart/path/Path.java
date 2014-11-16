package com.whg.myAStart.path;

import java.util.List;

import com.whg.myAStar.TileCell;

public class Path {

	private final List<TileCell> nodes;

	public Path(List<TileCell> nodes) {
		this.nodes = nodes;
	}

	public List<TileCell> getNodes() {
		return nodes;
	}

}
