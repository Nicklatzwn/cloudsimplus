package org.cloudbus.cloudsim.point;
import java.util.Random;

public class Point {
	
	private double x;
	private double y;
	
	private double speed_x;
	private double speed_y;
	private double limit;
	private final int range=5;
	
	private final double rangeMin=5;
	private final double rangeMax=10;
	
	public Point(double x,double y) {
		this.x=x;
		this.y=y;
	}
	
	public Point() {
		this.x=rangeMin + (rangeMax - rangeMin) * (new Random()).nextDouble();
		this.y=rangeMin + (rangeMax - rangeMin) * (new Random()).nextDouble();
	}
	
	public double getxPoint() {
		return x;
	}
	public double getyPoint() {
		return y;
	}
	public void setxPoint(double x) {
		this.x=x;
	}
	public void setyPoint(double y) {
		this.y=y;
	}
	public void move(double dt) {
		
		double x_new=x + speed_x*dt;
		double y_new=y + speed_y*dt;
		
		setxPoint(x_new);
		setyPoint(y_new);
		
		if(x_new>limit) {
			setxPoint(limit-range);
			speed_x*=-1;
		}
		
			
		else if(x_new<0) {
				setxPoint(range);
				speed_x*=-1;
			}
		if(y_new>limit) {
			setyPoint(limit-range);
			speed_y*=-1;
		}	
			
		else if(y_new<0) {
			setyPoint(range);
			speed_y*=-1;
		}

	}

	public void set_speed(double speed_x,double speed_y) {
		this.speed_x=speed_x;
		this.speed_y=speed_y;
	}
	public void set_limit(double limit) {
		this.limit=limit;
	}
	public double get_speed_x() {
		return speed_x;
	}
	public double get_speed_y() {
		return speed_y;
	}
}