package nick;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import java.util.*;

public class Cloud_Servers_Info extends Device_Info {

	private int upload_speed;
	private int download_speed;
	private List<Cloudlet> buffer;
	
	public Cloud_Servers_Info(Datacenter datacenter,int upload_speed,int download_speed) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		this.upload_speed=upload_speed;
		this.download_speed=download_speed;
		buffer=new ArrayList<Cloudlet>();
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
	}	
}
