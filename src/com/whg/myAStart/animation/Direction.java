package com.whg.myAStart.animation;

public enum Direction {

	down{
		@Override
		public int frameIndex(int currentFrame){
			return 0 + currentFrame;
		}
	},
	left{
		@Override
		public int frameIndex(int currentFrame){
			return 4 + currentFrame;
		}
	},
	right{
		@Override
		public int frameIndex(int currentFrame){
			return 8 + currentFrame;
		}
	},
	up{
		@Override
		public int frameIndex(int currentFrame){
			return 12 + currentFrame;
		}
	},
	idle{
		@Override
		public int frameIndex(int currentFrame){
			return 0;
		}
	};
	
	public abstract int frameIndex(int currentFrame);
	
}
