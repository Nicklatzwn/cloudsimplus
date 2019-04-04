package org.cloudbus.cloudsim.nickos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import java.util.*;

public class Points extends JPanel {
	private List<ArrayList<Integer>> coordinates_x;
	private List<ArrayList<Integer>> coordinates_y;
	private List<Integer> coordinates_edge_x;
	private List<Integer> coordinates_edge_y;
	private List<Integer> radious;
	private final int step=100;
	
	public Points(List<ArrayList<Integer>> coordinates_x,List<ArrayList<Integer>> coordinates_y,List<Integer> coordinates_edge_x,List<Integer> coordinates_edge_y,List<Integer> radious) {
		this.coordinates_x=coordinates_x;
		this.coordinates_y=coordinates_y;
		this.coordinates_edge_x=coordinates_edge_x;
		this.coordinates_edge_y=coordinates_edge_y;
		this.radious=radious;
	}

	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g;
	    List<Color> colors =new ArrayList<Color>();
	    colors.add(Color.RED);
	    colors.add(Color.blue);
	    colors.add(Color.gray);
	    colors.add(Color.green);
	    for(int i=0; i<coordinates_x.size(); i++) {
	    	g2d.setColor(colors.get(i));
	    	for(int j=0; j<coordinates_x.get(i).size(); j++) {
	    		g2d.drawLine(coordinates_x.get(i).get(j)+step, coordinates_y.get(i).get(j)+step, coordinates_x.get(i).get(j)+step, coordinates_y.get(i).get(j)+step);
	    	}
	    }
	    g2d.setColor(Color.RED);
	    for(int i=0; i<coordinates_edge_x.size(); i++) {
	    	int x = coordinates_edge_x.get(i)-(radious.get(i)/2)+step;
	    	int y = coordinates_edge_y.get(i)-(radious.get(i)/2)+step;
	    	g2d.drawOval(x, y, radious.get(i), radious.get(i));
	    }
	}
}

