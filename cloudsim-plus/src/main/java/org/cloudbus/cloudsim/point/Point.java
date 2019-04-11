package org.cloudbus.cloudsim.point;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Point {
	
	private double x;
	private double y;
	private List<Integer> coordinates_x;
	private List<Integer> coordinates_y;
	
	private double speed_x;
	private double speed_y;
	private double limit;
	private final int range=5;
	
	private final double rangeMin=5;
	private final double rangeMax=10;
	
	public Point(double x,double y) {
		this.x=x;
		this.y=y;
		coordinates_x=new ArrayList<Integer>();
		coordinates_y=new ArrayList<Integer>();
		long longValue_x = Math.round(x);
		long longValue_y = Math.round(y);
		int intValue_x = (int) longValue_x;
		int intValue_y = (int) longValue_y;
		coordinates_x.add(intValue_x);
		coordinates_y.add(intValue_y);
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
		long longValue_x = Math.round(x);
		int intValue_x = (int) longValue_x;
		coordinates_x.add(intValue_x);
	}
	public void setyPoint(double y) {
		this.y=y;
		long longValue_y = Math.round(y);
		int intValue_y = (int) longValue_y;
		coordinates_y.add(intValue_y);
	}
	public void move(double dt) {
		
		double x_new=x + speed_x*dt;
		double y_new=y + speed_y*dt;
		
		if(x_new>limit) {
			x_new=limit-range;
			speed_x*=-1;
		}
	
		else if(x_new<0) {
			    x_new=range;
				speed_x*=-1;
			}
		if(y_new>limit) {
			y_new=limit-range;
			speed_y*=-1;
		}	
			
		else if(y_new<0) {
			y_new=range;
			speed_y*=-1;
		}
		setxPoint(x_new);
		setyPoint(y_new);
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
	public List<Integer> get_coordinates_List_x() {
		return coordinates_x;
	}
	public List<Integer> get_coordinates_List_y() {
		return coordinates_y;
	}
}
