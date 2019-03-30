package nick;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

public class Edge_Servers_Info extends Device_Info {

	private double radious=100.0;
	private double func;
	private List<Integer> signal_strength;
	
	public Edge_Servers_Info(Datacenter datacenter) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		signal_strength=new ArrayList<Integer>();
	}
	public void set_radious(double rad) {
		radious=rad;
	}
	
	public double get_radious() {
		return radious;
	}
	public void set_objective_function(double func) {
		this.func=func;
	}
	public double get_objective_function( ) {
		return func;
	}
	public void add_zone(int zone) {
		signal_strength.add(zone);
	}
	public List<Integer> get_the_zones_lists() {
		return signal_strength;
	}
	public void execute_the_cloudlets_from_mobile(Mobiles_Info info) {
		super.getBroker().submitCloudletList(info.get_the_list_of_cloudlets_that_are_going_to_be_submitted());
	}
	
}
