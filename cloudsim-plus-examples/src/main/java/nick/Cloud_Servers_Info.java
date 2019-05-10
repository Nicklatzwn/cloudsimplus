package nick;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.plotter.Plotter;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;

import java.awt.Color;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Cloud_Servers_Info extends Device_Info {

	private int upload_speed;
	private int download_speed;
	private List<Cloudlet> buffer;
	private List<Integer> Cloudlets_for_Mobiles;
	private int cloud_id;
	private int counter_of_received_cloudlets_from_edges=0;
	private final XYSeries RESPONSE_SERIES;
	
	public Cloud_Servers_Info(Datacenter datacenter,int upload_speed,int download_speed,int cloud_id) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		this.upload_speed=upload_speed;
		this.download_speed=download_speed;
		buffer=new ArrayList<Cloudlet>();
		Cloudlets_for_Mobiles = new ArrayList<Integer>();
		RESPONSE_SERIES = new XYSeries("RESPONE");
		this.cloud_id=cloud_id;
	}
	
	public int get_upload_speed() {
		return upload_speed;
	}
	
	public int get_download_speed() {
		return download_speed;
	}
	
	public void add_to_buffer(List<Cloudlet> cloudlets) {
		buffer.addAll(cloudlets);
		counter_of_received_cloudlets_from_edges+=cloudlets.size();
	}
	
	public List<Cloudlet> get_buffer_list() {
		return buffer;
	}
	
	public void clear_buffer() {
		buffer.clear();
	}
	
	public int get_counter() {
		return counter_of_received_cloudlets_from_edges;
	}
	
	public void execute_the_cloudlets_from_mobile_to_main_Cloud_Server(Mobiles_Info info) {
		info.get_the_list_of_cloudlets_that_are_going_to_be_submitted().forEach(Cloudlet->Cloudlet.setSubmissionDelay(info.get_delay()));
		super.getBroker().submitCloudletList(info.get_the_list_of_cloudlets_that_are_going_to_be_submitted());
		add_to_Cloudlets_for_Mobiles(info.get_mob_id(),info.get_the_list_of_cloudlets_that_are_going_to_be_submitted());
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
		Plotter averageWin =  new Plotter(String.format("Average Time for Tasks Execution of the Cloud:%d with median average response time:%6.2f Sec-(Executed Cloudlets)",cloud_id,medianΑverageResponseTime), RESPONSE_SERIES,Color.black);
		String name = get_the_path() + "Cloud_"+ cloud_id+"/";
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
				getSaveSnapShot(averageWin, name + "Cloud_" + cloud_id +"_0.png");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
	}
}
