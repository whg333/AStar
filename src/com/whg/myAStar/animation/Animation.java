package com.whg.myAStar.animation;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;

public class Animation implements Drawable{

	protected final int width;
	protected final int height;
	protected int x;
	protected int y;
	
	protected Image playerImg;
	protected int currentFrame = 0;
	protected int columns = 4;
	protected int totalFrames = 4;
	protected int animationDirection = 1;
	protected int frameCount = 0;
	protected int frameDelay = 10;
	
	protected Direction direction = Direction.idle;
	
	public Animation(int width, int height, int x, int y, String imgPath){
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.playerImg = Toolkit.getDefaultToolkit().getImage(getURL(imgPath));
	}
	
	private URL getURL(String filename) {
		URL url = null;
		try {
			url = this.getClass().getResource(filename);
		} catch (Exception e) {
		}
		return url;
	}
	
	protected void changeDirection(int oldX, int oldY, int x, int y){
		if(y > oldY){
			direction = Direction.down;
		}else if(y < oldY){
			direction = Direction.up;
		}
		
		if(x > oldX){
			direction = Direction.right;
		}else if(x < oldX){
			direction = Direction.left;
		}
		
	}
	
	public void idle() {
		direction = Direction.idle;
	}

	@Override
	public void update() {
		// see if it's time to animate
		frameCount++;
		if (frameCount > frameDelay) {
			frameCount = 0;
			// update the animation frame
			currentFrame += animationDirection;
			if (currentFrame > totalFrames - 1) {
				currentFrame = 0;
			} else if (currentFrame < 0) {
				currentFrame = totalFrames - 1;
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g2d){
		//System.out.println("direction="+direction+",currentFrame="+currentFrame+",columns="+columns);
		int fx = 0;
		int fy = 0;
		if(direction != Direction.idle){
			fx = (direction.frameIndex(currentFrame) % columns) * width;
			fy = (direction.frameIndex(currentFrame) / columns) * height;
		}
		g2d.drawImage(playerImg, x, y, x + width, y + height, fx, fy, fx + width, fy + height, null);
	}
	
	public int getFrameDelay() {
		return frameDelay;
	}
	
	public Point getPoint(){
		return new Point(x, y);
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
}
