package nick;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import java.util.*;

public class Cloud_Servers_Info extends Device_Info {

	private int upload_speed;
	private int download_speed;
	private List<Cloudlet> buffer;
	private List<Integer> Cloudlets_for_Mobiles;
	private List<Cloudlet> Cloudlets;
	
	public Cloud_Servers_Info(Datacenter datacenter,int upload_speed,int download_speed) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		this.upload_speed=upload_speed;
		this.download_speed=download_speed;
		buffer=new ArrayList<Cloudlet>();
		Cloudlets_for_Mobiles = new ArrayList<Integer>();
		Cloudlets=new ArrayList<Cloudlet>();
	}
	
	public int get_upload_speed() {
		return upload_speed;
	}
	public int get_download_speed() {
		return download_speed;
	}
	public void add_to_buffer(List<Cloudlet> cloudlets) {
		buffer.addAll(cloudlets);
	}
	public List<Cloudlet> get_buffer_list() {
		return buffer;
	}
	public void clear_buffer() {
		buffer.clear();
	}
	public void execute_the_cloudlets_from_mobile_to_main_Cloud_Server(Mobiles_Info info) {
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
}
