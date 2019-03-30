package nick;

import org.cloudbus.cloudsim.datacenters.Datacenter;

public class Cloud_Servers_Info extends Device_Info {

	private int E_speed;
	
	public Cloud_Servers_Info(Datacenter datacenter,int E_speed) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		this.E_speed=E_speed;
	}
	public void set_E_speed(int E_speed) {
		this.E_speed=E_speed;
	}
	
	public int get_E_speed() {
		return E_speed;
	}
	public void execute_the_cloudlets_from_mobile_to_main_Cloud_Server(Mobiles_Info info) {
		super.getBroker().submitCloudletList(info.get_the_list_of_cloudlets_that_are_going_to_be_submitted());
	}
}
