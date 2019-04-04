package org.cloudbus.cloudsim.nickos;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;

public abstract class Device_Info {
	private Datacenter datacenter;
	private double TotalPower;
	private DatacenterBroker broker;
	private double temp_prev_energy;
	private double averageResponseTime;
	private int size;
	
	public Device_Info(Datacenter datacenter) {
		this.datacenter=datacenter;
		TotalPower=0.0;
		temp_prev_energy=0.0;
		averageResponseTime=0.0;
		size=0;
	}
	
	public void addPower(double Power) {
		TotalPower+=(Power/1000000);
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
		size+=s;
	}
	public double response_time() {
		return averageResponseTime;
	}
	public int number_of_cloudlets() {
		return size;
	}	
}