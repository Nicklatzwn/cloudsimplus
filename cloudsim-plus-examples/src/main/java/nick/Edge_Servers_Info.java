package nick;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

public class Edge_Servers_Info extends Device_Info {

	private double radious=100.0; //Default
	private List<Integer> signal_strength;
	private List<Integer> RSSI;
	private List<Integer> Cloudlets_for_Mobiles;
	private List<Cloudlet> Cloudlets;
	
	public Edge_Servers_Info(Datacenter datacenter) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		signal_strength=new ArrayList<Integer>();
		RSSI=new ArrayList<Integer>();
		Cloudlets_for_Mobiles = new ArrayList<Integer>();
		Cloudlets=new ArrayList<Cloudlet>();
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
	
	public double get_Response_Time_from_Edge() {
		double respone_time=0;
		for(Cloudlet cloudlet:Cloudlets) {
			if(cloudlet.isFinished())	respone_time+=cloudlet.getWallClockTimeInLastExecutedDatacenter();		
		}
		return respone_time;
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
}