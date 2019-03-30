package nick;
import org.cloudbus.cloudsim.datacenters.Datacenter;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;

public class Informations {

	private Datacenter datacenter;
	private double TotalPower;
	private DatacenterBroker broker;
	private double temp_prev_energy;
	private double averageResponseTime;
	private int size=0;
	private double radious=500.0;
	private double func;
	public Informations(Datacenter datacenter,double TotalPower) {
		this.datacenter=datacenter;
		this.TotalPower=TotalPower;
		temp_prev_energy=0.0;
		averageResponseTime=0.0;
		//size=0;
	}
	public double addPower(double Power) {
		return TotalPower+=(Power/1000000);
	}
	
	public Datacenter getDatacenter() {
		return datacenter;
	}
	public double getPower() {
		return TotalPower;
	}
	public void setBroker(DatacenterBroker broker) {
		this.broker=broker;
	}
	public DatacenterBroker getBroker() {
		return broker;
	}
	public double get_prev_Energy() {
		return temp_prev_energy;
	}
	public void set_prev_Energy(double temp_prev_energy) {
		this.temp_prev_energy=temp_prev_energy;
	}
	public void add_responsetime_size(double ResponseTime,int s) {
		averageResponseTime+=ResponseTime;
		size=size+s;
	}
	public double response_time() {
		return averageResponseTime;
	}
	public int number_of_cloudlets() {
		return size;
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
}
