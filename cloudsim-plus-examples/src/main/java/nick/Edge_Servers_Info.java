package nick;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

public class Edge_Servers_Info extends Device_Info {

	private double radious=100.0; //Default
	private List<Integer> signal_strength;
	private List<Integer> RSSI;
	
	public Edge_Servers_Info(Datacenter datacenter) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		signal_strength=new ArrayList<Integer>();
		RSSI=new ArrayList<Integer>();
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
	}
	public int get_the_RSSI_Value(int data_rate) {
		int index=signal_strength.indexOf(data_rate);
		return RSSI.get(index);
	}	
}
