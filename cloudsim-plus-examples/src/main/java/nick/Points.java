package nick;

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
	private final int step;
	private final int Time_To_Finish_Record;
	
	public Points(List<ArrayList<Integer>> coordinates_x,List<ArrayList<Integer>> coordinates_y,List<Integer> coordinates_edge_x,List<Integer> coordinates_edge_y,List<Integer> radious,int step,int Time_To_Finish_Record) {
		this.coordinates_x=coordinates_x;
		this.coordinates_y=coordinates_y;
		this.coordinates_edge_x=coordinates_edge_x;
		this.coordinates_edge_y=coordinates_edge_y;
		this.radious=radious;
		this.step=step;
		this.Time_To_Finish_Record=Time_To_Finish_Record;
	}
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g;
	    List<Color> colors = set_the_colors();
	    for(int i=0; i<coordinates_x.size(); i++) {
	    	g2d.setColor(colors.get(i % colors.size()));
	    	for(int j=0; j<coordinates_x.get(i).size(); j++) {
	    		//g2d.drawLine(coordinates_x.get(i).get(j)+step, coordinates_y.get(i).get(j)+step, coordinates_x.get(i).get(j)+step, coordinates_y.get(i).get(j)+step);
	    		g2d.drawLine(coordinates_x.get(i).get(j)+step-1, coordinates_y.get(i).get(j)+step-1, coordinates_x.get(i).get(j)+step+1, coordinates_y.get(i).get(j)+step+1);
	    		g2d.drawLine(coordinates_x.get(i).get(j)+step-1, coordinates_y.get(i).get(j)+step+1, coordinates_x.get(i).get(j)+step+1, coordinates_y.get(i).get(j)+step-1);
	    	}
	    }
	    g2d.setColor(Color.RED);
	    for(int i=0; i<coordinates_edge_x.size(); i++) {
	    	int x = coordinates_edge_x.get(i)-(radious.get(i)/2)+step;
	    	int y = coordinates_edge_y.get(i)-(radious.get(i)/2)+step;
	    	g2d.drawOval(x, y, radious.get(i), radious.get(i));
	    }
	    g2d.setColor(Color.black);
	    g2d.drawLine((int) step/2, (int) step/2,(int) step/2 + Time_To_Finish_Record + step , (int) step/2);
	    g2d.drawLine((int) step/2, (int) step/2,(int) step/2 , (int) step/2 + Time_To_Finish_Record + step);
	    g2d.drawLine((int) step/2 + Time_To_Finish_Record + step + step - step/4 , (int) step/2 - step/4 ,(int) step/2 + Time_To_Finish_Record + step + step + step/4, (int) step/2 + step/4 );
	    g2d.drawLine((int) step/2 + Time_To_Finish_Record + step + step - step/4, (int) step/2 +step/4,(int) step/2 + Time_To_Finish_Record + step + step + step/4, (int) step/2 - step/4);
	    g2d.drawLine((int) step/4, (int) step/2 + Time_To_Finish_Record + step + step/3 , (int) step/2 , (int) step/2 + Time_To_Finish_Record + step + 2*step/3);
	    g2d.drawLine((int) step/2 , (int) step/2 + Time_To_Finish_Record + step + 2*step/3,(int) 2*step/3,(int) step/2 + Time_To_Finish_Record + step +step/3);
	    g2d.drawLine((int) step/2 , (int) step/2 + Time_To_Finish_Record + step + 2*step/3,(int) step/3, (int) step/2 + Time_To_Finish_Record + step + step);
	}
	private List<Color> set_the_colors() {
		List<Color> colors =new ArrayList<Color>();
		colors.add(Color.black);
		colors.add(Color.blue);
		colors.add(Color.cyan);
		colors.add(Color.green);
		colors.add(Color.red);
		colors.add(Color.magenta);
		colors.add(Color.orange);
		colors.add(Color.pink);
		colors.add(Color.yellow);
		colors.add(new Color(255,102,102));	
		return colors;
	}
}
