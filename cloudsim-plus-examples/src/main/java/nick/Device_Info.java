package nick;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import java.util.List;
import java.util.ArrayList;

public abstract class Device_Info {
	private Datacenter datacenter;
	private List<Double> TotalPower;
	private DatacenterBroker broker;
	private double TotalResponseTime=0;
	private double TotalResponseTimeOfSim=0;
	private int size=0;
	private int sizesim=0;
	private List<Double> ΑverageResponseTime_size;
	private List<Double> Times;
	private List<Host> HostList;
	public final int Interval=10;
	
	public Device_Info(Datacenter datacenter) {
		this.datacenter=datacenter;
		HostList=new ArrayList<Host>();
		TotalPower= new ArrayList<Double>();
		TotalResponseTime=0.0;
		ΑverageResponseTime_size = new ArrayList<Double>();
		Times = new ArrayList<Double>();
		ΑverageResponseTime_size.add(0.0);
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
		TotalResponseTimeOfSim+=TotalResponseT;
		TotalResponseTime+=TotalResponseT;
		sizesim+=s;
		size+=s;
		if(time-Times.get(Times.size()-1)>Interval) {
			if(size!=0) {
			ΑverageResponseTime_size.add(TotalResponseTime/size);
			ΑverageResponseTime_size.add(TotalResponseTime/size);
			}
			else {
				ΑverageResponseTime_size.add(0.0);
				ΑverageResponseTime_size.add(0.0);
			}
			Times.add(Times.get(Times.size()-1));
			Times.add(time);
			TotalResponseTime=0;
			size=0;
		}
	}
	public void set_hostList(List<Host> hostlist) {
		HostList.addAll(hostlist);
	}
	public List<Host> get_hostlist() {
		return HostList;
	}
	public int number_of_cloudlets() {
		return sizesim;
	}
	public List<Double> get_the_ΑverageResponseTime_List() {
		return ΑverageResponseTime_size;
	}
	public List<Double> get_the_Times_List() {
		return Times;
	}
	public double get_total_response_time() {
		return TotalResponseTimeOfSim;
	}
}