package nick;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import java.util.List;
import java.util.ArrayList;

public abstract class Device_Info {
	private Datacenter datacenter;
	private List<Double> TotalPower;
	private DatacenterBroker broker;
	private double TotalResponseTime;
	private int size;
	private List<Double> ΑverageResponseTime;
	private List<Double> Times;
	
	public Device_Info(Datacenter datacenter) {
		this.datacenter=datacenter;
		TotalPower= new ArrayList<Double>();
		TotalResponseTime=0.0;
		size=0;
		ΑverageResponseTime = new ArrayList<Double>();
		Times = new ArrayList<Double>();
		ΑverageResponseTime.add(0.0);
		Times.add(0.0);
	}
	
	public Datacenter getDatacenter() {
		return datacenter;
	}
	public double getTotalPowerForTheHost(int index) {
		return TotalPower.get(index);
	}
	public void setTotalPower(double Power) {
		TotalPower.add(Power);
	}
	public void setBroker(DatacenterBroker broker) {
		this.broker=broker;
	}
	public DatacenterBroker getBroker() {
		return broker;
	}
	public void add_total_responsetime_size(double TotalResponseT,int s,double time) {
		TotalResponseTime+=TotalResponseT;
		size+=s;
		if(s!=0) { 
			ΑverageResponseTime.add(TotalResponseTime/size);
			Times.add(time);
		}
		else {
			ΑverageResponseTime.add(ΑverageResponseTime.get(ΑverageResponseTime.size()-1));
			Times.add(time);
		} 
	}
	public double get_total_response_time() {
		return TotalResponseTime;
	}
	public int number_of_cloudlets() {
		return size;
	}
	public List<Double> get_the_ΑverageResponseTime_List() {
		return ΑverageResponseTime;
	}
	public List<Double> get_the_Times_List() {
		return Times;
	}
}
