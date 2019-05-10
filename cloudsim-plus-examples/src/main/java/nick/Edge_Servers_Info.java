package nick;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.plotter.Plotter;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Edge_Servers_Info extends Device_Info {

	private double radious=100.0; //Default
	private List<Integer> signal_strength;
	private List<Integer> RSSI;
	private List<Integer> Cloudlets_for_Mobiles;
	private final XYSeries RESPONSE_SERIES;
	private int edge_id;
	
	public Edge_Servers_Info(Datacenter datacenter,int edge_id) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		signal_strength=new ArrayList<Integer>();
		RSSI=new ArrayList<Integer>();
		Cloudlets_for_Mobiles = new ArrayList<Integer>();
		RESPONSE_SERIES = new XYSeries("RESPONE");
		this.edge_id=edge_id;
	}
	
	public void set_radious(double rad) {
		radious=rad;
	}
	
	public double get_radious() {
		return radious;
	}
	
	public void add_zone(int zone,int RSSI) {
		signal_strength.add(zone);
		this.RSSI.add(RSSI);
	}
	
	public List<Integer> get_the_zones_lists() {
		return signal_strength;
	}
	
	public void execute_the_cloudlets_from_mobile(Mobiles_Info info) {
		info.get_the_list_of_cloudlets_that_are_going_to_be_submitted().forEach(Cloudlet->Cloudlet.setSubmissionDelay(info.get_delay()));
		super.getBroker().submitCloudletList(info.get_the_list_of_cloudlets_that_are_going_to_be_submitted());
		add_to_Cloudlets_for_Mobiles(info.get_mob_id(),info.get_the_list_of_cloudlets_that_are_going_to_be_submitted());
	}
	
	public int get_the_RSSI_Value(int data_rate) {
		int index=signal_strength.indexOf(data_rate);
		return RSSI.get(index);
	}
	
	public void add_to_Cloudlets_for_Mobiles(int mob_id,List<Cloudlet> cloudlets) {
		Cloudlets.addAll(cloudlets);
		for(int i=0; i<cloudlets.size(); i++) Cloudlets_for_Mobiles.add(mob_id);
	}
	
	public List<Double> get_the_percent_partiotion_of_edge_on_mobiles(int num_of_mobiles) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<num_of_mobiles; i++) {
			int temp=0;
			for(int j=0; j<Cloudlets_for_Mobiles.size(); j++) {
				if(Cloudlets_for_Mobiles.get(j)==i && Cloudlets.get(j).isFinished()) temp++;
			}
			list.add(temp);
		}
		int count=0;
		for(int i=0; i<list.size(); i++) count+=list.get(i);
		List<Double> ResultList = new ArrayList<Double>();
		if(count==0) {
			for(int i=0; i<list.size(); i++) ResultList.add(0.0);		
		}
		else {
		for(int i=0; i<list.size(); i++) ResultList.add( 1.0* (list.get(i))/count);
		}
		return ResultList;
	}
	public void show_the_plots(double time) {
		List<Double> ΑverageResponseTime_List = get_the_ΑverageResponseTime_List();
		List<Double> Times_List = get_the_Times_List();
		if(Times_List.get(Times_List.size()-1)<time) {
			ΑverageResponseTime_List.add(0.0);
			Times_List.add(time);
		}
		double medianΑverageResponseTime=0;
		for(int i=0; i<ΑverageResponseTime_List.size(); i++) {
			RESPONSE_SERIES.add(Times_List.get(i),ΑverageResponseTime_List.get(i));
			medianΑverageResponseTime+=ΑverageResponseTime_List.get(i);
		}
		medianΑverageResponseTime=medianΑverageResponseTime/ΑverageResponseTime_List.size();
		Plotter averageWin =  new Plotter(String.format("Average Time for Tasks Execution of the Edge:%d with median average response time:%6.2f Sec-(Executed Cloudlets)",edge_id,medianΑverageResponseTime), RESPONSE_SERIES,Color.black);
		String name = get_the_path() + "Edge_"+ edge_id+"/";
		 File TheInsDir = new File(name);
	        if(!TheInsDir.exists()) {
	   	 		try {
	   	 			TheInsDir.mkdir();
	   	 		}
	   	 		catch(SecurityException se){
		        //handle it
	   	 		} 
	        }
	        averageWin.pack();
	        averageWin.setVisible(true);
	        RefineryUtilities.centerFrameOnScreen(averageWin);
	        try {
            	TimeUnit.SECONDS.sleep(1);
				getSaveSnapShot(averageWin, name + "Edge_" + edge_id +"_0.png");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
	}
}
