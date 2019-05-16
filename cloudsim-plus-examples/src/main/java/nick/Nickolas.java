package nick;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.plotter.Plotter;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Processor;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScalingSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTextField;

import static java.lang.Math.*;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.point.*;
import org.cloudbus.cloudsim.power.models.*;

import java.io.*;
import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;

public class Nickolas {
	public static double Response_Var=0;
	private static String Results="C:/Users/nickl/Desktop/Results/";
	private static boolean Mobi_Het_And_Random_Compare=true;
    private static boolean random=false;
    
	private final int TIME_TO_FINISH_SIMULATION=1800;
    private final int NUMBER_OF_CLOUDLETS_DYNAMICALLY=1;
    private final int VM_PES_NUMBER = 2;
    private final int HOST_PES = VM_PES_NUMBER;
    private final double DATACENTER_SCHEDULING_INTERVAL = 1; 
    private final int Num_Of_Mobiles=20;
    private final CloudSim simulation;
    private final List<Cloudlet> finishedCloudletsatall;
    private final int time_to_change_direction=2;
    private final int Interval=2;
    private final int speed_3g=50;
    private final int Scale_Cloud=3;
    private final int grid_value=850;
    private final int start_battery=3000;
    private final double threshold_battery=0.01;
    private final int radious_scale=60;
    private final int CHECK_TIMER = 5; // Interval time 
    private final int CPI=5;
    
    private int no_fit_mobiles_counter=0;
    private int cloudletId = -1;
    private int createsVms = 0;
    private int createsDatacenters = 0;
    private int max_grid;
    private double prev_time=0;
    private double total_created_mips=0;
    
    private List<Double> lista;
    private List<ArrayList<Vm>> vm_total_list;
    private List<List<Host>> host_total_list;
    
    //Main Lists
    private List<Mobiles_Info> Mobiles_Info_List;
    private List<Edge_Servers_Info> Edge_Servers_Info_List;
    private List<Cloud_Servers_Info> Cloud_Servers_Info_List;
    //---------
    
    private int counter=0;
    private int counter_tick=0;
    private int counter_change_direction_of_the_mobiles=0;
    
    private List<Double> List_of_Total_Response;
    private List<Double> List_of_Times_for_Response;
    private List<Double> Temp_List_Of_Minimum_Values;
    private List<Edge_Servers_Info> Temp_List_Of_The_Best_Edges;
    private List<Mobiles_Info> Temp_List_Of_The_Best_Mobiles;
    private List<Mobiles_Info> Temp_List_Of_Not_Fit_Mobiles_To_Edges;
    private List<Double> max_power_for_the_mobiles;
    private List<Double> static_power_percent_for_the_mobiles;
    private List<Double> mobi_het_total_time_edges;
    private double mobi_het_total_time_edges_var=0;
    private double random_total_time_edges_var=0;
    private double real_response_var=0;
    private double mobi_het_associativity=0;
    private double random_associativity=0;
    private List<Double> random_total_time_edges;
    private List<Double> Times;
    private double mobi_het_total_time_edges_temp=0;
    private double random_total_time_edges_temp=0;
    private List<Double> list_data_zones_services_mobi_het;
    private List<Double> list_data_zones_services_random;
    private double total_service_mobi_het=0;
	private double total_service_random=0;
	private double standard_deviation_mobi_het=0;
	private double standard_deviation_random=0;
	private List<Double> list_standard_deviation_mobi_het;
	private List<Double> list_standard_deviation_random;
	private double total_standard_deviation_random=0;
	private double total_standard_deviation_mobi_het=0;
    
public static void main(String[] args) {
    new Nickolas();
    if(Mobi_Het_And_Random_Compare) {
    	Mobi_Het_And_Random_Compare=false;
    	random=true;
    	Results="C:/Users/nickl/Desktop/Results/Random/";
    	Response_Var=0;
    	new Nickolas();
    } 
}

public Nickolas() {
	
	//Lists
	lista=new ArrayList<>();
	finishedCloudletsatall = new ArrayList<Cloudlet>();
	mobi_het_total_time_edges = new ArrayList<Double>();
	random_total_time_edges = new ArrayList<Double>();
	list_data_zones_services_mobi_het= new ArrayList<Double>();
	list_data_zones_services_random= new ArrayList<Double>();
	list_standard_deviation_random= new ArrayList<Double>();
	list_standard_deviation_mobi_het= new ArrayList<Double>();
	List_of_Total_Response=new ArrayList<Double>();
	List_of_Times_for_Response=new ArrayList<Double>();
	Times = new ArrayList<Double>();
	//Read Input
	 read_file();
	 //Set the result folder
	 File TheMainDir = new File(Results);
	 if(!TheMainDir.exists()) {
		 try {
			 TheMainDir.mkdir();
		 }
		 catch(SecurityException se){
			 //handle it
		 } 
	 }
	 System.out.println("Starting " + getClass().getSimpleName());
     simulation = new CloudSim();
     //First step
     set_Datacenters_and_brokers();      
     //Second step
     set_the_vms();
    //Third step
     create_new_cloudlets_and_possible_direction_of_the_mobiles(simulation.clock());
     set_listeners();
     // Fourth step: Starts the simulation
     simulation.start();  
     // Final step: Print results when simulation is over
     //List<Cloudlet> newList = broker.getCloudletFinishedList();
     checkMobileCpuUtilizationAndPowerConsumption();
     print_results();  
     //new CloudletsTableBuilder(finishedCloudletsatall).build();
 }

private void read_file() {
	 File file = new File("file.txt");
	    BufferedReader reader = null;
	    try {
	       
	    	String text = null;
	    	reader = new BufferedReader(new FileReader(file));
	 
	    	while ((text = reader.readLine()) != null) {
	            lista.add(Double.parseDouble(text));
	        }
	    }
	    	catch (FileNotFoundException e) {
	    	    e.printStackTrace();
	    	} 
	    catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (reader != null) {
	                reader.close();
	            }
	        } catch (IOException e) {
	        }
	    }
}

private void set_listeners() {
	 this.simulation.addOnEventProcessingListener(simEvent -> pauseSimulationAtSpecificTime(simEvent));
     this.simulation
     .addOnSimulationPauseListener(this::printCloudletsFinishedSoFarAndResumeSimulation);
     simulation.addOnClockTickListener(this::onClockTickListener);
     simulation.terminateAt(TIME_TO_FINISH_SIMULATION);
}

private void set_the_vms( ) {
    //Third step
    vm_total_list= new ArrayList<ArrayList<Vm>>();
    for(int i=0; i<Mobiles_Info_List.size(); i++) { ArrayList<Vm> vmList = createVms(0); vm_total_list.add(vmList); }
    for(int i=0; i<Edge_Servers_Info_List.size(); i++) { ArrayList<Vm> vmList = createVms(1); vm_total_list.add(vmList); }
    for(int i=0; i<Cloud_Servers_Info_List.size(); i++) { ArrayList<Vm> vmList = createVms(20); vm_total_list.add(vmList); }
    vm_total_list.subList(Num_Of_Mobiles, vm_total_list.size()).forEach(vmList -> vmList.forEach(vm->vm.setPeVerticalScaling(createVerticalPeScaling())));
    for(int i=0; i<Mobiles_Info_List.size(); i++)  Mobiles_Info_List.get(i).getBroker().submitVmList(vm_total_list.get(i));    
    for(int i=0; i<Edge_Servers_Info_List.size(); i++)  Edge_Servers_Info_List.get(i).getBroker().submitVmList(vm_total_list.get(Mobiles_Info_List.size()+i)); 
    for(int i=0; i<Cloud_Servers_Info_List.size(); i++)  Cloud_Servers_Info_List.get(i).getBroker().submitVmList(vm_total_list.get(Mobiles_Info_List.size()+Edge_Servers_Info_List.size()+i));
}

private void set_Datacenters_and_brokers() {
	//First step
	host_total_list = new ArrayList<List<Host>>();
	Mobiles_Info_List=new ArrayList<Mobiles_Info>();
	Edge_Servers_Info_List=new ArrayList<Edge_Servers_Info>();
	Cloud_Servers_Info_List=new ArrayList<Cloud_Servers_Info>();
	
	Temp_List_Of_Minimum_Values =  new ArrayList<Double>();
	Temp_List_Of_The_Best_Edges =  new ArrayList<Edge_Servers_Info>();
	Temp_List_Of_The_Best_Mobiles = new ArrayList<Mobiles_Info>();
	Temp_List_Of_Not_Fit_Mobiles_To_Edges = new ArrayList<Mobiles_Info>();
	
	max_power_for_the_mobiles = new ArrayList<Double>();
	static_power_percent_for_the_mobiles = new ArrayList<Double>();
	max_grid = (TIME_TO_FINISH_SIMULATION < grid_value) ? TIME_TO_FINISH_SIMULATION : grid_value;
    int number=max_grid;
	for(int i=0; i<Num_Of_Mobiles; i++) {
		int x=(new Random()).nextInt(number);
		int y=(new Random()).nextInt(number);
		Mobiles_Info_List.add(new Mobiles_Info(createDatacenter(0,x,y),start_battery,i,number));
		Mobiles_Info_List.get(Mobiles_Info_List.size()-1).getDatacenter().setName("Mobile_Device_"+i);
		Mobiles_Info_List.get(Mobiles_Info_List.size()-1).set_the_path(Results);
	}
	int number_1=max_grid/4;
	int number_2=3*max_grid/4;
	
	Edge_Servers_Info_List.add(new Edge_Servers_Info(createDatacenter(1,number_1,number_1),Edge_Servers_Info_List.size()));
	Edge_Servers_Info_List.get(Edge_Servers_Info_List.size()-1).getDatacenter().setName("Edge_Server_1");
	Edge_Servers_Info_List.add(new Edge_Servers_Info(createDatacenter(1,number_1,number_2),Edge_Servers_Info_List.size()));
	Edge_Servers_Info_List.get(Edge_Servers_Info_List.size()-1).getDatacenter().setName("Edge_Server_2");
	Edge_Servers_Info_List.add(new Edge_Servers_Info(createDatacenter(1,number_2,number_1),Edge_Servers_Info_List.size()));
	Edge_Servers_Info_List.get(Edge_Servers_Info_List.size()-1).getDatacenter().setName("Edge_Server_3");
	Edge_Servers_Info_List.add(new Edge_Servers_Info(createDatacenter(1,number_2,number_2),Edge_Servers_Info_List.size()));
	Edge_Servers_Info_List.get(Edge_Servers_Info_List.size()-1).getDatacenter().setName("Edge_Server_4");
	Cloud_Servers_Info_List.add(new Cloud_Servers_Info(createDatacenter(20,Math.pow(TIME_TO_FINISH_SIMULATION,2),Math.pow(TIME_TO_FINISH_SIMULATION,2)),speed_3g,Scale_Cloud*speed_3g,0));
	Cloud_Servers_Info_List.get(Cloud_Servers_Info_List.size()-1).getDatacenter().setName("Cloud_Server");

    //Second_step
	for(int i=0; i<Mobiles_Info_List.size(); i++) Mobiles_Info_List.get(i).setBroker(new DatacenterBrokerSimple(simulation,"Mobile_Broker_"+i));
    for(int i=0; i<Edge_Servers_Info_List.size(); i++) {
    	Edge_Servers_Info_List.get(i).setBroker(new DatacenterBrokerSimple(simulation,"Edge_Server_Broker_"+i));
    	Edge_Servers_Info_List.get(i).add_zone(550,-64+40);
    	Edge_Servers_Info_List.get(i).add_zone(500,-65+40);
    	Edge_Servers_Info_List.get(i).add_zone(450,-69+40);
    	Edge_Servers_Info_List.get(i).add_zone(400,-73+40);
    	Edge_Servers_Info_List.get(i).add_zone(350,-75+40);
    	Edge_Servers_Info_List.get(i).add_zone(300,-77+40);
    	Edge_Servers_Info_List.get(i).add_zone(250,-79+40);
    	Edge_Servers_Info_List.get(i).add_zone(200,-81+40);
    	//Set properly the radious
    	Edge_Servers_Info_List.get(i).set_radious(radious_scale*(i+1)); 
    	Edge_Servers_Info_List.get(i).set_the_path(Results);
    }
    for(int i=0; i<Cloud_Servers_Info_List.size(); i++) Cloud_Servers_Info_List.get(i).setBroker(new DatacenterBrokerSimple(simulation,"Cloud_Broker_"+i)); 
    
    set_broker_utils(Mobiles_Info_List);
    set_broker_utils(Edge_Servers_Info_List);
    set_broker_utils(Cloud_Servers_Info_List);
    
    int index=0;
    for(Mobiles_Info mobiles:Mobiles_Info_List) {
    	mobiles.getDatacenter().getPoint().set_speed(1,1);
    	mobiles.set_hostList(host_total_list.get(index));
    	index++;
    }
    for(Edge_Servers_Info egde:Edge_Servers_Info_List) {
    	egde.getDatacenter().getPoint().set_speed(0,0);
    	egde.set_hostList(host_total_list.get(index));
    	index++;
    }
    for(Cloud_Servers_Info cloud:Cloud_Servers_Info_List) {
    	cloud.getDatacenter().getPoint().set_speed(0,0); 
    	cloud.set_hostList(host_total_list.get(index));
    	cloud.set_the_path(Results);
    }
}

private void set_broker_utils(final List<? extends Device_Info> list) {
	 list.forEach(info -> { 
		 info.getBroker().setDatacenterSupplier(() -> info.getDatacenter());
      	 info.getBroker().setVmDestructionDelayFunction(vm -> { return 1.0*TIME_TO_FINISH_SIMULATION; });
      	 info.getBroker().setVmMapper(cloudlet-> {   return cloudlet
              .getBroker()
              .getVmCreatedList()
              .stream()
              .filter(vm -> vm.getNumberOfPes() >= cloudlet.getNumberOfPes())
              .min(Comparator.comparingLong(vm->vm.getCloudletScheduler().getCloudletWaitingList().size()+vm.getCloudletScheduler().getCloudletExecList().size()))
              .orElse(info.getBroker().defaultVmMapper(cloudlet));
       });	 
	 });
}

private void check(double time) {
	List<Mobiles_Info> Temp_List_Of_Lonely_Mobiles = new ArrayList<Mobiles_Info>();
	List<Mobiles_Info> Temp_List_Of_Mobiles = new ArrayList<Mobiles_Info>();
	
	for(int id_edge=0; id_edge<Edge_Servers_Info_List.size(); id_edge++) {
		
		for(int id_mob=0; id_mob<Mobiles_Info_List.size(); id_mob++) {
			if(!Mobiles_Info_List.get(id_mob).is_checked()) {
				if(Math.pow(Mobiles_Info_List.get(id_mob).getDatacenter().getPoint().getxPoint()- Edge_Servers_Info_List.get(id_edge).getDatacenter().getPoint().getxPoint(),2)+Math.pow(Mobiles_Info_List.get(id_mob).getDatacenter().getPoint().getyPoint()-Edge_Servers_Info_List.get(id_edge).getDatacenter().getPoint().getyPoint(),2) < Math.pow(Edge_Servers_Info_List.get(id_edge).get_radious(),2)) {
					Mobiles_Info_List.get(id_mob).add_edge_point_to_mobile(id_edge);
				}
			}
		}	
	}
	for(Mobiles_Info info:Mobiles_Info_List) {
		if(!info.is_checked()) {
			if(!info.get_edge_point_to_mobile().isEmpty()) {
				compute_objective_fuction_for_this_mobile(info);
				if(!Temp_List_Of_Not_Fit_Mobiles_To_Edges.contains(info))	Temp_List_Of_Mobiles.add(info);
			}
			else Temp_List_Of_Lonely_Mobiles.add(info);
		}
	}
	
	Temp_List_Of_Lonely_Mobiles.addAll(Temp_List_Of_Not_Fit_Mobiles_To_Edges);
	Temp_List_Of_Not_Fit_Mobiles_To_Edges.forEach(mobile->mobile.checked());
	no_fit_mobiles_counter+=Temp_List_Of_Not_Fit_Mobiles_To_Edges.size();
	
	for(int i=0; i<Temp_List_Of_Mobiles.size(); i++) {
		if(!Temp_List_Of_Minimum_Values.isEmpty()) {
			double s_min_of_min=Temp_List_Of_Minimum_Values.get(0);
			int temp=0;
			for(int j=1; j<Temp_List_Of_Minimum_Values.size(); j++) {
				if(Temp_List_Of_Minimum_Values.get(j)<s_min_of_min) {
					s_min_of_min=Temp_List_Of_Minimum_Values.get(j);
					temp=j;
			}
		}
			Mobiles_Info mobile=Temp_List_Of_The_Best_Mobiles.get(temp);
			Edge_Servers_Info edge=Temp_List_Of_The_Best_Edges.get(temp);
			check_the_mobile(mobile,edge,time);
			mobi_het_total_time_edges_temp+=mobile.get_temp_time();
			mobi_het_associativity+=mobile.get_the_associativity();
			standard_deviation_mobi_het+=mobile.get_the_standard_deviation();
			clear_the_temp_lists();
			for(Mobiles_Info info:Temp_List_Of_Mobiles) compute_objective_fuction_for_this_mobile(info);
			Temp_List_Of_Lonely_Mobiles.addAll(Temp_List_Of_Not_Fit_Mobiles_To_Edges);
			Temp_List_Of_Not_Fit_Mobiles_To_Edges.forEach(mob->mob.checked());
			no_fit_mobiles_counter+=Temp_List_Of_Not_Fit_Mobiles_To_Edges.size();
		}
	}
	if(Mobi_Het_And_Random_Compare) {
		mobi_het_total_time_edges.add(mobi_het_total_time_edges_temp);
		random_total_time_edges.add(random_total_time_edges_temp);
		list_data_zones_services_random.add(random_associativity);
		list_data_zones_services_mobi_het.add(mobi_het_associativity);
		list_standard_deviation_random.add(standard_deviation_random);
		list_standard_deviation_mobi_het.add(standard_deviation_mobi_het);
		Times.add(time);
		mobi_het_total_time_edges_temp=0;
		random_total_time_edges_temp=0;
		random_associativity=0;
		mobi_het_associativity=0;
		standard_deviation_random=0;
		standard_deviation_mobi_het=0;
	}
	else if(!Mobi_Het_And_Random_Compare && random) {
		mobi_het_total_time_edges.add(mobi_het_total_time_edges_temp);
		list_data_zones_services_mobi_het.add(mobi_het_associativity);
		list_standard_deviation_mobi_het.add(standard_deviation_mobi_het);
		Times.add(time);
		mobi_het_total_time_edges_temp=0;
		mobi_het_associativity=0;
		standard_deviation_mobi_het=0;
	}
	check_balance_between_lonely_mobiles_and_cloud_server(Temp_List_Of_Lonely_Mobiles,time);
	Mobiles_Info_List.forEach(mobile->mobile.clear_edge_point_and_uncheck_mobile_and_submitted_list_of_cloudlets());
	Mobiles_Info_List.forEach(mobile->mobile.set_temp_time_data_zone_standard_deviation(0, 0, 0));
	Mobiles_Info_List.forEach(mobile->mobile.check_mobile_for_random(true));
	clear_the_temp_lists();
}

private void clear_the_temp_lists() {
	Temp_List_Of_Minimum_Values.clear();
	Temp_List_Of_The_Best_Edges.clear();
	Temp_List_Of_The_Best_Mobiles.clear();
	Temp_List_Of_Not_Fit_Mobiles_To_Edges.clear();
}
		
private void check_the_mobile(Mobiles_Info mobile, Edge_Servers_Info the_best_edge,double time) {
	// TODO Auto-generated method stub
		int temp2=mobile.get_the_list_of_cloudlets_that_are_going_to_be_submitted().size();
		int perfect_data_zone=predict_the_zone(mobile,the_best_edge,0,0);
		mobile.add_wifi_power_to_send_cloudlets(perfect_data_zone,the_best_edge.get_the_RSSI_Value(perfect_data_zone),time);
		for(int i=0; i<temp2; i++) mobile.reduce_by_send_receive_wifi();
		mobile.add_to_the_battery_plot(time);
		the_best_edge.execute_the_cloudlets_from_mobile(mobile);
		mobile.checked();	
}

private void compute_objective_fuction_for_this_mobile(Mobiles_Info info) {
	//Main-Core-Algorithm->Mobi-Het Architecture
			if(!info.is_checked()) {
				double speed_x=info.getDatacenter().getPoint().get_speed_x();
				double speed_y=info.getDatacenter().getPoint().get_speed_y();
				double s_fn;
				double s_old=Double.MAX_VALUE;
				int temp=0;
				double m=computation_of_m_for_standard_deviation();
				Edge_Servers_Info the_best_edge=Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(0));
				for(int i=0; i<info.get_edge_point_to_mobile().size(); i++) {
					double assosiativity=calculate_assosiativity(Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)),info);
					int zwnh_send=predict_the_zone(info,Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)),0,0);
					double s=computation_of_standard_deviation(info.get_edge_point_to_mobile().get(i),info.get_the_list_of_cloudlets_that_are_going_to_be_submitted(),m);	
					double bytes_to_send=0.0,MI_to_exec=0.0,bytes_to_send_back=0.0;
					for(Cloudlet cloudlet:info.get_the_list_of_cloudlets_that_are_going_to_be_submitted()) { bytes_to_send+=cloudlet.getFileSize(); MI_to_exec+=cloudlet.getTotalLength(); bytes_to_send_back+=cloudlet.getOutputSize(); }
					double t_send=bytes_to_send/(zwnh_send);			
					double t_exec=compute_t_exec_on_selected_vms(Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)).getBroker().getVmWaitingList(),Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)).getBroker().getVmCreatedList(),MI_to_exec,info.get_the_list_of_cloudlets_that_are_going_to_be_submitted().size());
					double t_so_far=t_send+t_exec;		
					int zwnh_receive=predict_the_zone(info,Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)),t_so_far*speed_x,t_so_far*speed_y);		
					double t_send_back=bytes_to_send_back/(zwnh_receive);				
					if(!check_if_the_mobile_is_going_to_stay_in_the_edge(info,Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)),(t_send_back+t_so_far)*speed_x,(t_send_back+t_so_far)*speed_y))	temp++;
					else {
						if(Mobi_Het_And_Random_Compare && info.check_for_random()) {
							random_total_time_edges_temp+=t_so_far+t_send_back;
							random_associativity+=assosiativity;
							standard_deviation_random+=s;
							info.check_mobile_for_random(false);
						}
						s_fn=(t_send_back+t_so_far)/assosiativity+s;
						if(s_fn<s_old && (!random) ) {
							info.set_temp_time_data_zone_standard_deviation(t_send_back+t_so_far,assosiativity,s);
							info.set_delay(t_send);
							s_old=s_fn;
							the_best_edge=Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i));
						}
						else if(random && i==temp) {
							info.set_temp_time_data_zone_standard_deviation(t_send_back+t_so_far,assosiativity,s);
							info.set_delay(t_send);
							s_old=s_fn;
							the_best_edge=Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i));
						}
					}
			}
				if(temp==info.get_edge_point_to_mobile().size()) Temp_List_Of_Not_Fit_Mobiles_To_Edges.add(info);
				else {
					Temp_List_Of_Minimum_Values.add(s_old);
					Temp_List_Of_The_Best_Edges.add(the_best_edge);
					Temp_List_Of_The_Best_Mobiles.add(info);
				}
		}
}

private boolean check_if_the_mobile_is_going_to_stay_in_the_edge(Mobiles_Info info, Edge_Servers_Info edge_Servers_Info,double dx,double dy) {
	// TODO Auto-generated method stub
	double x_current=info.getDatacenter().getPoint().getxPoint();
	double y_current=info.getDatacenter().getPoint().getyPoint();
	double x_after=x_current+dx;
	double y_after=y_current+dy;
	if(Math.pow(x_after - edge_Servers_Info.getDatacenter().getPoint().getxPoint(),2)+Math.pow(y_after - edge_Servers_Info.getDatacenter().getPoint().getyPoint(),2) < Math.pow(edge_Servers_Info.get_radious(),2)) {
		return true;
	}
	else return false;
}

private int predict_the_zone(Mobiles_Info inform,Edge_Servers_Info info,double Dx,double Dy) {
	double x0=info.getDatacenter().getPoint().getxPoint();
	double y0=info.getDatacenter().getPoint().getyPoint();
	double xmob=inform.getDatacenter().getPoint().getxPoint()+Dx;
	double ymob=inform.getDatacenter().getPoint().getyPoint()+Dx;
	int i=0;
	int temp=info.get_the_zones_lists().get(0);
	while(Math.pow(x0-xmob, 2)+Math.pow(y0-ymob, 2)>Math.pow(info.get_radious(), 2)*(i+1)/info.get_the_zones_lists().size()) {
		i++;
		temp=info.get_the_zones_lists().get(i);
		if(i==info.get_the_zones_lists().size()-1) break;
	}
	return temp;
}

private double calculate_assosiativity(Edge_Servers_Info inform,Mobiles_Info info) {
	
	double x0=inform.getDatacenter().getPoint().getxPoint();
	double y0=inform.getDatacenter().getPoint().getyPoint();
	double x_mob=info.getDatacenter().getPoint().getxPoint();
	double y_mob=info.getDatacenter().getPoint().getyPoint();
	double v_speed_x=info.getDatacenter().getPoint().get_speed_x();
	double v_speed_y=info.getDatacenter().getPoint().get_speed_y();
	double a=v_speed_y/v_speed_x;
	double b=y_mob-a*x_mob; //y=ax+b
	double D=4*(Math.pow(a*b-a*y0-x0, 2)-(1+Math.pow(a, 2))*(Math.pow(x0,2)+Math.pow(b-y0, 2)-Math.pow(inform.get_radious(), 2)));
	double x_tomhs_1=(2*(x0+a*y0-a*b)+Math.sqrt(D))/(2*(1+Math.pow(a,2)));
	double x_tomhs_2=(2*(x0+a*y0-a*b)-Math.sqrt(D))/(2*(1+Math.pow(a,2)));
	double y_tomhs_1=a*x_tomhs_1+b;
	double y_tomhs_2=a*x_tomhs_2+b;
	double x_tomhs_final,y_tomhs_final;
	if(v_speed_x>=0) {
		if(x_tomhs_1>=x_tomhs_2) {
			x_tomhs_final=x_tomhs_1;
			y_tomhs_final=y_tomhs_1;
		}
		else {
			x_tomhs_final=x_tomhs_2;
			y_tomhs_final=y_tomhs_2;
		}
	}
	else {
		if(x_tomhs_1<x_tomhs_2) {
			x_tomhs_final=x_tomhs_1;
			y_tomhs_final=y_tomhs_1;
		}
		else {
			x_tomhs_final=x_tomhs_2;
			y_tomhs_final=y_tomhs_2;
		}
	}
	double distance=Math.sqrt(Math.pow(x_tomhs_final-x_mob, 2)+Math.pow(y_tomhs_final-y_mob, 2));
	return distance/Math.sqrt(Math.pow(v_speed_x, 2)+Math.pow(v_speed_y, 2));
}

private double computation_of_m_for_standard_deviation() {
	double m=0;
	for(int id=0; id<Edge_Servers_Info_List.size(); id++) {
		double total_mips_for_cloudlets=0;
		double total_capacity_mips_for_vms=0;
		for(Vm vm:vm_total_list.get(Num_Of_Mobiles+id)) {
			total_capacity_mips_for_vms+=vm.getTotalMipsCapacity();
			for(Cloudlet cloudlet:vm.getCloudletScheduler().getCloudletList()) total_mips_for_cloudlets+=cloudlet.getTotalLength();
		}
		m+=total_mips_for_cloudlets/total_capacity_mips_for_vms;
	}		
	return m/Edge_Servers_Info_List.size();
}

private double computation_of_standard_deviation(int edge_id,List<Cloudlet> cloudlets_to_add,double m) {
	double sum_of_sq=0;
	for(int id=0; id<Edge_Servers_Info_List.size(); id++) {
		double total_mips_for_cloudlets=0;
		double total_capacity_mips_for_vms=0;
		
		for(Vm vm:vm_total_list.get(Num_Of_Mobiles+id)) {
			total_capacity_mips_for_vms+=vm.getTotalMipsCapacity();
			for(Cloudlet cloudlet:vm.getCloudletScheduler().getCloudletList()) total_mips_for_cloudlets+=cloudlet.getTotalLength();
		}
		if(edge_id==id) {
			for(Cloudlet cloudlet:cloudlets_to_add) total_mips_for_cloudlets+=cloudlet.getTotalLength();
		}
		sum_of_sq+=Math.pow((total_mips_for_cloudlets/total_capacity_mips_for_vms)-m,2);
	}
	return sum_of_sq/Math.sqrt(Edge_Servers_Info_List.size());
}

private void print_results() {
	String name = Results + "General/";
    File TheInsDir = new File(name);
    if(!TheInsDir.exists()) {
	 		try {
	 			TheInsDir.mkdir();
	 		}
	 		catch(SecurityException se){
        //handle it
	 		} 
    }
	take_finished_cloudlets_from_all_brokers(simulation.clock());
	compare_mobi_het_with_random(Mobi_Het_And_Random_Compare);
	compare_mobi_het_with_random(random);
	long total_length_of_the_cloudlets_on_edges=0, total_size_of_the_cloudlets_on_edges_trensport=0;
	for(Edge_Servers_Info edge:Edge_Servers_Info_List) { 
		total_length_of_the_cloudlets_on_edges+=edge.get_total_length_of_executed_cloudlets();
		total_size_of_the_cloudlets_on_edges_trensport+=edge.get_the_files_size_of_transport();
	}
	final Comparator<Cloudlet> sortByCloudletId = comparingDouble(c -> c.getId());
	final Comparator<Cloudlet> sortByStartTime = comparingDouble(Cloudlet::getExecStartTime);
	finishedCloudletsatall.sort(sortByCloudletId.thenComparing(sortByStartTime));
	try {
		PrintWriter writer = new PrintWriter(name+"Cloudlets_Results.txt", "UTF-8");
		writer.printf("\t\t\t\t\t\t---------->>>>>CLOUDLETS RESULTS<<<<<<----------\n");
		for(final Cloudlet cloudlet:finishedCloudletsatall) {
			int datacenter_id;
			if(cloudlet.getLastDatacenter().getId()>Num_Of_Mobiles) datacenter_id=(int) cloudlet.getLastDatacenter().getId()-Num_Of_Mobiles;
			else datacenter_id=(int) cloudlet.getLastDatacenter().getId()-1;
			writer.printf("\t\tReal Time Arrival/Exec-Start: %6.3f | ,Real Time Finish: %6.3f | ,CPU Usage Time: %6.3f ,Perfect CPU Usage Time: %6.3f | ,Inside Datacenter : %d with name: %s and id: %d\n", cloudlet.getLastDatacenterArrivalTime(), cloudlet.getFinishTime(), cloudlet.getActualCpuTime(), cloudlet.getTotalLength()/vm_total_list.get((int) cloudlet.getLastDatacenter().getId()-1).get(0).getTotalMipsCapacity(), datacenter_id ,cloudlet.getLastDatacenter().getName(),cloudlet.getId());
		}
		writer.printf("\t\t----->>>Total Executed Cloudlets: %d <<<-----\n",finishedCloudletsatall.size());
		writer.close();
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	try {
		PrintWriter writer = new PrintWriter(name+"Mobiles_Edges_Cloud_Results.txt", "UTF-8");
		writer.printf("\t\t\t\t\t\t---------->>>>>CLOUD_SERVER_INFO<<<<<----------\n");
		for(Cloud_Servers_Info information:Cloud_Servers_Info_List) { 
			writer.printf("\t\t------------------------\n");
			writer.printf("\t\tIn: %s has executed: %d (Total received from edges: %d) Cloudlets(Total submitted cloudlets: %d,Total executed mips: %d,Total unexecuted mips: %d) with total response time(EXECUTION): %f\n",information.getDatacenter().getName(),information.number_of_cloudlets(),information.get_counter(),information.total_length_of_belonging_cloudlets(),information.get_total_length_of_executed_cloudlets(),information.get_total_length_of_unexecuted_cloudlets(),information.get_total_response_time());
			writer.printf("\t\t------------------------\n");
			if(Mobi_Het_And_Random_Compare) {
				writer.printf("\t\t--->>> %f (real timing on edges transport,execution response time), %f (mobi het algorith measure response), %f(random mapping mobile to edge response) <<<<---- \n",real_response_var,mobi_het_total_time_edges_var,random_total_time_edges_var);
				writer.printf("\t\t--->>>  %f (mobi het algorith measure average Quality of Service), %f (random mapping mobile to edge average Quality of Service) <<<<---- \n", total_service_mobi_het,total_service_random);
				writer.printf("\t\t--->>>  %f (mobi het algorith measure average standard deviation), %f (random mapping mobile to edge average standard deviation) <<<<---- \n", total_standard_deviation_mobi_het,total_standard_deviation_random);
				writer.printf("\t\t--->>> %d total times have rejected from Edges the mobiles <<<<---- \n",no_fit_mobiles_counter);
				writer.printf("\t\t--->>> %d total Mips have executed on all edges <<<<---- \n",total_length_of_the_cloudlets_on_edges);
				writer.printf("\t\t--->>> %d total Bytes have transferd on all edges <<<<---- \n",total_size_of_the_cloudlets_on_edges_trensport);
				writer.printf("\t\t--->>> %d total Cloudlets have created with %.0f total Mips <<<<---- \n",cloudletId+1,total_created_mips);
				writer.printf("\t\t------------------------\n");
			}
			else if(random) {
				writer.printf("\t\t--->>> %f (real timing on edges transport,execution response time),  %f (random measure response) <<<<---- \n",real_response_var,mobi_het_total_time_edges_var);
				writer.printf("\t\t--->>>  %f (random measure average Quality of Service) <<<<---- \n",total_service_mobi_het);
				writer.printf("\t\t--->>>  %f (random measure average standard deviation) <<<<---- \n", total_standard_deviation_mobi_het);
				writer.printf("\t\t--->>> %d total times have rejected from Edges the mobiles <<<<---- \n",no_fit_mobiles_counter);
				writer.printf("\t\t--->>> %d total Mips have executed on all edges <<<<---- \n",total_length_of_the_cloudlets_on_edges);
				writer.printf("\t\t--->>> %d total Bytes have transferd on all edges <<<<---- \n",total_size_of_the_cloudlets_on_edges_trensport);
				writer.printf("\t\t--->>> %d total Cloudlets have created with %.0f total Mips <<<<---- \n",cloudletId+1,total_created_mips);
				writer.printf("\t\t------------------------\n");
			}
			List<Double> ResultLista = information.get_the_percent_partiotion_of_edge_on_mobiles(Num_Of_Mobiles);
			for(int i=0; i<Num_Of_Mobiles; i++) {
				writer.printf("\t\tThe %4.2f%% percent of executed cloudlets belong to mobile : %d\n",ResultLista.get(i)*100,i);
				writer.printf("\t\t------------------------\n");
			}
		}
		writer.printf("\t\t\t\t\t\t---------->>>>>EDGE_SERVER_INFO<<<<<----------\n");
		for(Edge_Servers_Info information:Edge_Servers_Info_List) { 
			writer.printf("\t\t------------------------\n");
			writer.printf("\t\tIn: %s has executed %d Cloudlets(Total submitted cloudlets: %d,Total executed mips: %d,Total unexecuted mips: %d) with total response time(EXECUTION): %f\n",information.getDatacenter().getName(),information.number_of_cloudlets(),information.total_length_of_belonging_cloudlets(),information.get_total_length_of_executed_cloudlets(),information.get_total_length_of_unexecuted_cloudlets(),information.get_total_response_time());
			writer.printf("\t\t------------------------\n");
			List<Double> ResultList = information.get_the_percent_partiotion_of_edge_on_mobiles(Num_Of_Mobiles);
			for(int i=0; i<Num_Of_Mobiles; i++) {
				writer.printf("\t\tThe %4.2f%% percent of executed cloudlets belong to mobile : %d\n",ResultList.get(i)*100,i);
				writer.printf("\t\t------------------------\n");
			}
		}
		writer.printf("\t\t\t\t\t\t---------->>>>>MOBILE_INFO<<<<<----------\n");
		for(Mobiles_Info information:Mobiles_Info_List) { 
			writer.printf("\t\t------------------------\n");
			writer.printf("\t\tIn: %s has executed %d Cloudlets(Total submitted cloudlets: %d,Total executed mips: %d,Total unexecuted mips: %d) with total response time(EXECUTION): %f\n",information.getDatacenter().getName(),information.number_of_cloudlets(),information.total_length_of_belonging_cloudlets(),information.get_total_length_of_executed_cloudlets(),information.get_total_length_of_unexecuted_cloudlets(),information.get_total_response_time());
			writer.printf("\t\t------------------------\n");
		}
		writer.printf("\t\t----->>>Total Executed Cloudlets: %d And Total Time of Simulation: %.2f <<<-----\n",finishedCloudletsatall.size(),simulation.clock());
		writer.printf(getClass().getSimpleName() + " finished!");
		writer.close();
		
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	Cloud_Servers_Info_List.forEach(cloud_server->cloud_server.show_the_plots(simulation.clock()));
	Edge_Servers_Info_List.forEach(edge->edge.show_the_plots(simulation.clock()));
	Mobiles_Info_List.forEach(mobile->mobile.show_the_plots(simulation.clock()));
	try {
		show_the_footprints();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println('\n' + getClass().getSimpleName() + " finished!");
}
     
private void energy_cpu_for_hosts(double total_mips) {
	double B_freq,B_idle;
    double freq_base=lista.get(0);
    int index=0;
    double freq=total_mips/CPI;
    
    if(freq<freq_base){  B_freq=lista.get(1); B_idle=lista.get(2); }
    else if(freq>lista.get(lista.size()-3)) {  B_freq=lista.get(lista.size()-2); B_idle=lista.get(lista.size()-1); }
    else {
   	 while(freq>freq_base) {
   		 index++;
   		 freq_base=lista.get(3*index);
   		 if(freq_base==lista.get(lista.size()-3)) break;
   	 }
   	 B_freq=abs(((lista.get(3*index+1)-lista.get(3*index-3+1))/(lista.get(3*index)-lista.get(3*index-3)))*(freq-lista.get(3*index-3))+lista.get(3*index-3+1));
   	 B_idle=abs(((lista.get(3*index+2)-lista.get(3*index-3+2))/(lista.get(3*index)-lista.get(3*index-3)))*(freq-lista.get(3*index-3))+lista.get(3*index-3+2));
    }
    max_power_for_the_mobiles.add(B_freq+B_idle);
    static_power_percent_for_the_mobiles.add(B_idle/(B_freq+B_idle));
}

private ArrayList<Vm> createVms(int category) {
    //VM Parameters
    long size = 10000; //image size (Megabyte)
    int ram,mips,vms;
    long bw = 1000;
    int pesNumber=VM_PES_NUMBER;
    int vms_for_mobiles=2;
    int vms_for_edges=4;
    int vms_for_cloud=4;
    if(category==0) {
    	mips = 400; ram=256; vms=vms_for_mobiles; 
    }
    else if(category==1) {
    	int id_vm=(createsVms-Num_Of_Mobiles*vms_for_mobiles)/vms_for_edges;
    	mips = (int) (1000+Math.pow(id_vm, 2)*100); ram=1024; vms=vms_for_edges; 
    }
    else {
    	mips = 2000; ram=2048; vms=vms_for_cloud;
    }
    //Creates a container to store VMs. This list is passed to the broker later
    ArrayList<Vm> list = new ArrayList<>(vms);
    //create VMs
    for (int j = 0; j < vms; j++) {
    	final int id_vm = createsVms++;
        Vm vm = new VmSimple(id_vm, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerSpaceShared());

        if(category==0) vm.getUtilizationHistory().enable();
        list.add(vm);
    }
    return list;
}

private List<Cloudlet> createCloudlets(int cloudlets,int pesNumber,Mobiles_Info mobile) {
	List<Cloudlet> list = new ArrayList<>(cloudlets);
	for(int i=0; i<cloudlets; i++) {
    //cloudlet parameters
    long length = mobile.get_random_length();
    long fileSize = mobile.get_random_filesize();
    long outputSize = fileSize;
    UtilizationModel utilizationModel = new UtilizationModelFull();
    total_created_mips+=length*pesNumber;

    Cloudlet cloudlet = new CloudletSimple(++cloudletId,length, pesNumber);
    		
    cloudlet.setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModelCpu(utilizationModel)
            .setUtilizationModelRam(utilizationModel)
            .setUtilizationModelBw(utilizationModel);
    list.add(cloudlet);
	}
    return list;
}

private Datacenter createDatacenter(int category,double x,double y) {
    long mips,ram,storage,bw;
    int hosts;
    int pesNumber=HOST_PES;

    List<Pe> peList = new ArrayList<>();
    if(category==0) {
    	//Mobile Device
		mips = 800; ram = 1024; storage = 1000000/2; bw = 10000; hosts=1; 
    }
    else if(category==1) {
    	//Edge Server
    	int datacenter_id=createsDatacenters-Num_Of_Mobiles;
    	mips = (long) (1000+Math.pow(datacenter_id, 2)*100); ram = 2048; storage = 1000000; bw = 10000; hosts=4;

    }
    else {
    	//Cloud Server
		mips = 2000; ram = 4096; storage = 2*1000000; bw = 10000; hosts=4; 
    }
    if(createsDatacenters<Num_Of_Mobiles) energy_cpu_for_hosts(mips*pesNumber*hosts);  
    for(int i=0; i<pesNumber; i++) peList.add(new PeSimple(mips, new PeProvisionerSimple())); 	
    List<Host> hostList = new ArrayList<>(hosts);
    for(int i=0; i<hosts; i++) hostList.add(createPowerHost(ram, bw, storage, peList));
    host_total_list.add(hostList);
    createsDatacenters++;
    //Point of datacenter
    Point thesh=new Point(x,y);
    return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple(), thesh).setSchedulingInterval(DATACENTER_SCHEDULING_INTERVAL);            
}

private Host createPowerHost(long ram,long bw,long storage,List<Pe> peList) {
	Host host;
	if(createsDatacenters<Num_Of_Mobiles) {
		final PowerModel powerModel;
		if(createsDatacenters==Num_Of_Mobiles-7)   powerModel = new PowerModelSpecPowerHpProLiantMl110G3PentiumD930(); 
		else if(createsDatacenters==Num_Of_Mobiles-6)  powerModel = new PowerModelSpecPowerHpProLiantMl110G4Xeon3040(); 
		else if(createsDatacenters==Num_Of_Mobiles-5)   powerModel = new PowerModelSpecPowerHpProLiantMl110G5Xeon3075();
		else if(createsDatacenters==Num_Of_Mobiles-4)   powerModel = new PowerModelSpecPowerIbmX3250XeonX3470(); 
		else if(createsDatacenters==Num_Of_Mobiles-3)   powerModel = new PowerModelSpecPowerIbmX3250XeonX3480(); 
		else if(createsDatacenters==Num_Of_Mobiles-2) powerModel = new PowerModelSpecPowerIbmX3550XeonX5670();
		else if(createsDatacenters==Num_Of_Mobiles-1) powerModel = new PowerModelSpecPowerIbmX3550XeonX5675();
		else {
			final double MAX_POWER_WATTS_SEC=max_power_for_the_mobiles.get(createsDatacenters);
			final double STATIC_POWER_PERCENT = static_power_percent_for_the_mobiles.get(createsDatacenters);
			powerModel = new PowerModelLinear(MAX_POWER_WATTS_SEC, STATIC_POWER_PERCENT);	
		}
		 host = new HostSimple(ram, bw, storage, peList)
    			.setRamProvisioner(new ResourceProvisionerSimple())
    			.setBwProvisioner(new ResourceProvisionerSimple())
    			.setVmScheduler(new VmSchedulerTimeShared())
    			.setPowerModel(powerModel);
	}
	else {
		 host = new HostSimple(ram, bw, storage, peList)
    			.setRamProvisioner(new ResourceProvisionerSimple())
    			.setBwProvisioner(new ResourceProvisionerSimple())
    			.setVmScheduler(new VmSchedulerTimeShared());
	}
	return host;
}

private void checkMobileCpuUtilizationAndPowerConsumption() {
	for(Mobiles_Info mobile:Mobiles_Info_List) {
		int temp=0;
		for(Host host:mobile.get_hostlist()) {		
			List<Double> CPU_ENERGY=new ArrayList<Double>();
			List<Double> TIME_FOR_CPU_ENERGY=new ArrayList<Double>();
			final Map<Double, DoubleSummaryStatistics> utilizationPercentHistory = host.getUtilizationHistory();	
			//time difference from the current to the previous line in the history
			double utilizationHistoryTimeInterval;
			double prevTime=0;
			double total_cpu_power=0;
			CPU_ENERGY.add(host.getPowerModel().getPower(1));
			TIME_FOR_CPU_ENERGY.add(0.0);
			for (Map.Entry<Double, DoubleSummaryStatistics> entry : utilizationPercentHistory.entrySet()) {
				utilizationHistoryTimeInterval = entry.getKey() - prevTime;
				//The total Host's CPU utilization for the time specified by the map key
				final double utilizationPercent = entry.getValue().getSum();
				final double wattsSec = host.getPowerModel().getPower(utilizationPercent);
				double wattsPerInterval;
				if(utilizationHistoryTimeInterval<=DATACENTER_SCHEDULING_INTERVAL) {
					wattsPerInterval = wattsSec*utilizationHistoryTimeInterval;
					total_cpu_power+=wattsPerInterval;
					CPU_ENERGY.add(wattsSec);
					CPU_ENERGY.add(wattsSec);
					TIME_FOR_CPU_ENERGY.add(prevTime);
					TIME_FOR_CPU_ENERGY.add(entry.getKey());	
				} 
				else {
					double start_time=prevTime + DATACENTER_SCHEDULING_INTERVAL;
					while(start_time < entry.getKey()) {
						CPU_ENERGY.add(host.getPowerModel().getPower(0));
						CPU_ENERGY.add(host.getPowerModel().getPower(0));
						TIME_FOR_CPU_ENERGY.add(start_time - DATACENTER_SCHEDULING_INTERVAL);
						TIME_FOR_CPU_ENERGY.add(start_time);
						start_time+=DATACENTER_SCHEDULING_INTERVAL;
						wattsPerInterval=host.getPowerModel().getPower(0)*DATACENTER_SCHEDULING_INTERVAL;
						total_cpu_power+=wattsPerInterval;
					}
					
					CPU_ENERGY.add(wattsSec);
					CPU_ENERGY.add(wattsSec);
					TIME_FOR_CPU_ENERGY.add(start_time - DATACENTER_SCHEDULING_INTERVAL);
					TIME_FOR_CPU_ENERGY.add(entry.getKey());
					total_cpu_power+=wattsSec*(entry.getKey() - start_time + DATACENTER_SCHEDULING_INTERVAL);		
				} 				
				prevTime = entry.getKey();
			}
			if(prevTime<TIME_TO_FINISH_SIMULATION) {
				final double wattsSec = host.getPowerModel().getPower(0);
				final double wattsPerInterval = wattsSec*(TIME_TO_FINISH_SIMULATION-prevTime);
				CPU_ENERGY.add(wattsSec);
				CPU_ENERGY.add(wattsSec);
				TIME_FOR_CPU_ENERGY.add(prevTime);
				TIME_FOR_CPU_ENERGY.add((double) TIME_TO_FINISH_SIMULATION);
				total_cpu_power+=wattsPerInterval;
			}
			mobile.add_to_the_cpu_energy_plot_for_desired_host(temp);		
			for(int i=0; i<CPU_ENERGY.size(); i++) mobile.set_the_values_plot_for_cpu_energy_for_desired_host(TIME_FOR_CPU_ENERGY.get(i), CPU_ENERGY.get(i));
			mobile.setTotalPower(total_cpu_power);
			temp++;	
		}			
	}
}

private void onClockTickListener(EventInfo eventInfo) {
	
    //CPU_METER_ENERGY
	if(Math.floor(eventInfo.getTime()) % Interval == 0) {
		if(counter_tick==0) {			
			//Move Mobile_Device
			Mobiles_Info_List.forEach(mobile -> mobile.getDatacenter().getPoint().move(Math.floor(eventInfo.getTime())-prev_time));
			
			for(Mobiles_Info mobile:Mobiles_Info_List) {
				mobile.add_idle_3g_power(eventInfo.getTime(),Math.floor(eventInfo.getTime())-prev_time);
				mobile.add_wifi_idle_energy(eventInfo.getTime(),Math.floor(eventInfo.getTime())-prev_time);
			}
			prev_time=Math.floor(eventInfo.getTime());
			take_finished_cloudlets_from_all_brokers(eventInfo.getTime());
			counter_tick=1;
		}
	}
	if(Math.floor(eventInfo.getTime()) % Interval != 0) counter_tick=0;	
}

private void printCloudletsFinishedSoFarAndResumeSimulation(EventInfo pauseInfo) {
    System.out.printf("\n# Simulation paused at %.2f second\n", pauseInfo.getTime());
    create_new_cloudlets_and_possible_direction_of_the_mobiles(pauseInfo.getTime());
    add_to_Response_Lists(pauseInfo.getTime());
    this.simulation.resume();
}

private void pauseSimulationAtSpecificTime(SimEvent simEvent) {

	if(Math.floor(simEvent.getTime()) %  CHECK_TIMER==0){
		if (counter == 0) {
		simulation.pause();
		take_finished_cloudlets_from_all_brokers(simEvent.getTime());
		counter = 1;
		}
	}
	if ((Math.floor(simEvent.getTime()) %  CHECK_TIMER != 0))  counter = 0;
	    
}

private void take_finished_cloudlets_from_all_brokers(double time) {
	
	List<Cloudlet> executed_cloudlets_sended_to_cloud=new ArrayList<Cloudlet>();
	executed_cloudlets_sended_to_cloud.addAll(Cloud_Servers_Info_List.get(0).get_buffer_list());
	executed_cloudlets_sended_to_cloud.forEach(Cloudlet->Response_Var+=Cloudlet.getOutputSize()/Cloud_Servers_Info_List.get(0).get_download_speed());
	Mobiles_Info_List.forEach(info->info.find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(executed_cloudlets_sended_to_cloud, 2, Cloud_Servers_Info_List.get(0).get_download_speed() ,0 , time));
	Cloud_Servers_Info_List.get(0).clear_buffer();

	for(Mobiles_Info information:Mobiles_Info_List) {
		double averageResponseTime=0.0;
		int size=0;
		List<Cloudlet> temp_cloudlets = new ArrayList<>();
		temp_cloudlets.addAll(information.getBroker().getCloudletFinishedList());
		information.getBroker().getCloudletFinishedList().clear();
		temp_cloudlets.removeAll(finishedCloudletsatall);
		Mobiles_Info_List.forEach(info->info.find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(temp_cloudlets, 0, 0, 0, time));
		for(Cloudlet cloudlet:temp_cloudlets) {
				averageResponseTime+=cloudlet.getActualCpuTime();
				size+=1;
				finishedCloudletsatall.add(cloudlet);
	  }
		information.add_total_responsetime_size(averageResponseTime, size, time);
	}	
	for(Edge_Servers_Info information:Edge_Servers_Info_List) {
		double averageResponseTime=0.0;
		int size=0;
		List<Cloudlet> temp_cloudlets = new ArrayList<>();
		temp_cloudlets.addAll(information.getBroker().getCloudletFinishedList());
		information.getBroker().getCloudletFinishedList().clear();
		temp_cloudlets.removeAll(finishedCloudletsatall);
		for(Mobiles_Info info:Mobiles_Info_List) {
			if(Math.pow(info.getDatacenter().getPoint().getxPoint()- information.getDatacenter().getPoint().getxPoint(),2)+Math.pow(info.getDatacenter().getPoint().getyPoint()-information.getDatacenter().getPoint().getyPoint(),2) < Math.pow(information.get_radious(),2)) {
				int data_rate=predict_the_zone(info,information, 0, 0);
				int RSSI=information.get_the_RSSI_Value(data_rate);
				info.find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(temp_cloudlets, 1, data_rate, RSSI, time);	
			}
			else {
				List<Cloudlet> belonging_cloudlets_to_mobile = new ArrayList<>();
				belonging_cloudlets_to_mobile.addAll(info.get_the_list_of_belonging_cloudlets());
				belonging_cloudlets_to_mobile.retainAll(temp_cloudlets);
				Cloud_Servers_Info_List.get(0).add_to_buffer(belonging_cloudlets_to_mobile);
			}
		}
	for(Cloudlet cloudlet:temp_cloudlets) {
		averageResponseTime+=cloudlet.getActualCpuTime();
		Response_Var+=cloudlet.getWallClockTimeInLastExecutedDatacenter();
		size+=1;
		finishedCloudletsatall.add(cloudlet);
				
	}
	information.add_total_responsetime_size(averageResponseTime, size, time);
}
	for(Cloud_Servers_Info information:Cloud_Servers_Info_List) {
		double averageResponseTime=0.0;
		int size=0;
		List<Cloudlet> temp_cloudlets = new ArrayList<>();
		temp_cloudlets.addAll(information.getBroker().getCloudletFinishedList());
		information.getBroker().getCloudletFinishedList().clear();
		temp_cloudlets.removeAll(finishedCloudletsatall);
		Mobiles_Info_List.forEach(info->info.find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(temp_cloudlets, 2, information.get_download_speed() ,0 , time));

	for(Cloudlet cloudlet:temp_cloudlets) {
		averageResponseTime+=cloudlet.getActualCpuTime();
		size+=1;
		finishedCloudletsatall.add(cloudlet);
		}
	information.add_total_responsetime_size(averageResponseTime, size, time);
	}
}

private void create_new_cloudlets_and_possible_direction_of_the_mobiles(double time) {
		for(Mobiles_Info info:Mobiles_Info_List) {
			if(info.get_Battery()>threshold_battery*info.start_battery()) info.store_the_created_cloudlets(createCloudlets(NUMBER_OF_CLOUDLETS_DYNAMICALLY,VM_PES_NUMBER,info));
			else info.checked();
		}	
		if(counter_change_direction_of_the_mobiles % time_to_change_direction == 0) {
			Mobiles_Info_List.forEach(mobile->mobile.change_direction_and_speed());
			counter_change_direction_of_the_mobiles=1;
		}
		counter_change_direction_of_the_mobiles++;
		check(time);
}

private double lowerCpuUtilizationThreshold(Vm vm) {
    return 0.2;
}

private double upperCpuUtilizationThreshold(Vm vm) {
    return 0.8;
}

private VerticalVmScaling createVerticalPeScaling() {
    //The percentage in which the number of PEs has to be scaled
    final double scalingFactor = 0.1;
    VerticalVmScalingSimple verticalCpuScaling = new VerticalVmScalingSimple(Processor.class, scalingFactor);
    verticalCpuScaling.setResourceScaling(vs -> 2*vs.getScalingFactor()*vs.getAllocatedResource());

    verticalCpuScaling.setLowerThresholdFunction(this::lowerCpuUtilizationThreshold);
    verticalCpuScaling.setUpperThresholdFunction(this::upperCpuUtilizationThreshold);

    return verticalCpuScaling;
}

private void show_the_footprints() throws Exception {

	ArrayList<ArrayList<Integer>> coordinates_x= new ArrayList<ArrayList<Integer>>();
	List<ArrayList<Integer>> coordinates_y= new ArrayList<ArrayList<Integer>>();
	List<Integer> coordinates_edge_x= new ArrayList<Integer>();
	List<Integer> coordinates_edge_y= new ArrayList<Integer>();
	List<Integer> radious= new ArrayList<Integer>();
	for(Mobiles_Info mobile:Mobiles_Info_List) {
		coordinates_x.add((ArrayList<Integer>) mobile.getDatacenter().getPoint().get_coordinates_List_x());
		coordinates_y.add((ArrayList<Integer>) mobile.getDatacenter().getPoint().get_coordinates_List_y());
	}
	for(Edge_Servers_Info edge:Edge_Servers_Info_List) {
		double x=edge.getDatacenter().getPoint().getxPoint();
		double y=edge.getDatacenter().getPoint().getyPoint();
		long longValue_x = Math.round(x);
		int intValue_x = (int) longValue_x;
		long longValue_y = Math.round(y);
		int intValue_y = (int) longValue_y;
		coordinates_edge_x.add(intValue_x);
		coordinates_edge_y.add(intValue_y);
		double r=edge.get_radious();
		long rad =Math.round(r);
		int R = (int) rad;
		radious.add(R);
	}
	int step=50;
	for(Mobiles_Info mobile:Mobiles_Info_List) {
		ArrayList<ArrayList<Integer>> coordinates_mob_x= new ArrayList<ArrayList<Integer>>();
		List<ArrayList<Integer>> coordinates_mob_y= new ArrayList<ArrayList<Integer>>();
		coordinates_mob_x.add(coordinates_x.get(mobile.get_mob_id()));
		coordinates_mob_y.add(coordinates_y.get(mobile.get_mob_id()));
		Points graph = new Points(coordinates_mob_x,coordinates_mob_y,coordinates_edge_x,coordinates_edge_y,radious,step,max_grid);
		JFrame frame = new JFrame("Points_of_Mobile_"+mobile.get_mob_id());
		frame.add(graph);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if(max_grid==grid_value) {
			Toolkit tk = Toolkit.getDefaultToolkit();
			int xsize = (int) tk.getScreenSize().getWidth();
			int ysize = (int) tk.getScreenSize().getHeight();
			frame.setSize(xsize, ysize);
		}
		else frame.setSize(2*max_grid,2*max_grid);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    TimeUnit.SECONDS.sleep(1);
	    getSaveSnapShot(frame, Results + "Mobile_" + mobile.get_mob_id() + "/footprint_" + mobile.get_mob_id() + ".png"); 
	    
	}
	String name = Results + "General/";
	Points graph = new Points(coordinates_x,coordinates_y,coordinates_edge_x,coordinates_edge_y,radious,step,max_grid);
	JFrame frame = new JFrame("Points_of_All_Mobiles");
	frame.add(graph);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	if(max_grid==grid_value) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		int xsize = (int) tk.getScreenSize().getWidth();
		int ysize = (int) tk.getScreenSize().getHeight();
		frame.setSize(xsize, ysize);
	}
	else frame.setSize(2*max_grid,2*max_grid);
    frame.setLocationRelativeTo(null);
    final JTextField text = new JTextField();
    frame.add(text,BorderLayout.SOUTH);
    frame.addMouseListener(new MouseListener() {
        public void mousePressed(MouseEvent me) {}
        public void mouseReleased(MouseEvent me) {}
        public void mouseEntered(MouseEvent me) {}
        public void mouseExited(MouseEvent me) {}
        public void mouseClicked(MouseEvent me) { 
        	int x = me.getX();
            int y = me.getY();
            text.setText("X:" + (x-step) + ", Y:" + (y-step));
        }
    });
    frame.setVisible(true);
    TimeUnit.SECONDS.sleep(1);
    getSaveSnapShot(frame, name+"Total_Footprint.png");  
	}

private void check_balance_between_lonely_mobiles_and_cloud_server(List<Mobiles_Info> list_of_lonely_mobiles,double time) {
	double threshold=0.3;
	for(Mobiles_Info mobile:list_of_lonely_mobiles) {
		if(mobile.get_Battery()<threshold*mobile.start_battery()) Cloud_Servers_Info_List.get(0).execute_the_cloudlets_from_mobile_to_main_Cloud_Server(mobile);
		else {
			double total_mips_to_submit=0, total_file_size_input=0, total_file_size_ouput=0;
			for(Cloudlet cloudlet:mobile.get_the_list_of_cloudlets_that_are_going_to_be_submitted()) {
				total_mips_to_submit+=cloudlet.getTotalLength();
				total_file_size_input+=cloudlet.getFileSize();
				total_file_size_ouput+=cloudlet.getOutputSize();
			}
			double t_mob=compute_t_exec_on_selected_vms(mobile.getBroker().getVmWaitingList(),mobile.getBroker().getVmCreatedList(),total_mips_to_submit,mobile.get_the_list_of_cloudlets_that_are_going_to_be_submitted().size());
			double t_send_to_cloud_server=total_file_size_input/Cloud_Servers_Info_List.get(0).get_upload_speed();
			double t_exe_to_cloud_server=compute_t_exec_on_selected_vms(Cloud_Servers_Info_List.get(0).getBroker().getVmWaitingList(),Cloud_Servers_Info_List.get(0).getBroker().getVmCreatedList(),total_mips_to_submit,mobile.get_the_list_of_cloudlets_that_are_going_to_be_submitted().size());
			double t_get_the_response_from_cloud_server=total_file_size_ouput/Cloud_Servers_Info_List.get(0).get_download_speed();
			double t_send_ex_get_cloud_server=t_send_to_cloud_server+t_exe_to_cloud_server+t_get_the_response_from_cloud_server;
			mobile.set_delay(t_send_to_cloud_server);
			if(t_send_ex_get_cloud_server<=t_mob)  {
				Cloud_Servers_Info_List.get(0).execute_the_cloudlets_from_mobile_to_main_Cloud_Server(mobile);
				mobile.reduce_by_send_recieve_to_Cloud_Server();
				mobile.add_to_the_battery_plot(time);
				mobile.add_3g_power(time, t_send_to_cloud_server);
			}
			else mobile.execute_your_own_tasks();
		} 
	} 
}

public BufferedImage getScreenShot(Component component) {

    BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
    // paints into image's Graphics
    component.paint(image.getGraphics());
    return image;
}

public void getSaveSnapShot(Component component, String fileName) throws Exception {
        BufferedImage img = getScreenShot(component);
        // write the captured image as a PNG
        ImageIO.write(img, "png", new File(fileName));
    }

private void compare_mobi_het_with_random(boolean check) {
	  if(check) {
		  //Response Graph
		  List<Plotter> windowPlots = new ArrayList<>();
		  String[] s = new String[3];
		  XYSeries Mobi_Het = new XYSeries("Mobi-Het-Response");
		  for(int i=0; i<mobi_het_total_time_edges.size(); i++) {
			  mobi_het_total_time_edges_var+=mobi_het_total_time_edges.get(i);
			  Mobi_Het.add(Times.get(i), mobi_het_total_time_edges.get(i));
		  }
		  XYSeries Real_Response = new XYSeries("Real-Response");
		  for(int i=0; i<List_of_Total_Response.size(); i++) {
			  real_response_var+=List_of_Total_Response.get(i);
			  Real_Response.add(List_of_Times_for_Response.get(i), List_of_Total_Response.get(i));
		  }
 
		  if(Mobi_Het_And_Random_Compare) {
			 XYSeries Random = new XYSeries("Random-Response");
			 for(int i=0; i<random_total_time_edges.size(); i++) {
				 random_total_time_edges_var+=random_total_time_edges.get(i);
				 Random.add(Times.get(i), random_total_time_edges.get(i));
			 }
			 XYSeriesCollection dataset_1 = new XYSeriesCollection();
			 dataset_1.addSeries(Mobi_Het);
			 dataset_1.addSeries(Random);
			 dataset_1.addSeries(Real_Response);
			 Plotter Compare_Response = new Plotter(String.format("Compare Real-Response with total Response Time: %.4f and Mobi-Het with total Response Time: %.4f and Random with total Response Time: %.4f",real_response_var , mobi_het_total_time_edges_var,random_total_time_edges_var),dataset_1,"Response_Time");	 
			 s[0]="Compare_Response_Graph.png";
			 windowPlots.add(Compare_Response);
		  }
		  else {
			  XYSeriesCollection dataset_1 = new XYSeriesCollection();
			  dataset_1.addSeries(Mobi_Het);
			  dataset_1.addSeries(Real_Response);
			  Plotter Random_Response =  new Plotter(String.format("Random experiment with Real-Response Time: %.4f and Random Expected Response Time: %.4f",real_response_var, mobi_het_total_time_edges_var),dataset_1,"Response_Time");
			  s[0]="Random_Response_Graph.png";
			  windowPlots.add(Random_Response);
		  }
		  //Service Graph
		  XYSeries Mobi_Het_Service = new XYSeries("Mobi-Het-Service");
		  
		  for(int i=0; i<list_data_zones_services_mobi_het.size(); i++) {
			  total_service_mobi_het+=list_data_zones_services_mobi_het.get(i);
			  Mobi_Het_Service.add(Times.get(i), list_data_zones_services_mobi_het.get(i));
		  }
		  total_service_mobi_het=total_service_mobi_het/list_data_zones_services_mobi_het.size();
		  if(Mobi_Het_And_Random_Compare) {
			  XYSeries Random_Service = new XYSeries("Random_Service");
			  for(int i=0; i<list_data_zones_services_random.size(); i++) {
				  total_service_random+=list_data_zones_services_random.get(i);
				  Random_Service.add(Times.get(i), list_data_zones_services_random.get(i));
			  }
			  total_service_random=total_service_random/list_data_zones_services_random.size();
			  XYSeriesCollection dataset_2 = new XYSeriesCollection();
			  dataset_2.addSeries(Mobi_Het_Service);
			  dataset_2.addSeries(Random_Service);
			  Plotter Compare_Service = new Plotter(String.format("Compare Mobi-Het with total Average Quality of Service: %.4f and Random with total Average Quality of Service: %.4f", total_service_mobi_het,total_service_random),dataset_2,"Sevice");
			  s[1]="Compare_Service_Graph.png";
			  windowPlots.add(Compare_Service);	
		  }
		  else {
			  Plotter Random_Service = new Plotter(String.format("Random experiment with total Average Quality of Service: %.4f", total_service_mobi_het),Mobi_Het_Service,Color.BLUE);
			  s[1]="Random_Service_Graph.png";
			  windowPlots.add(Random_Service);	
		  }
		  //Standard_Deviation Graph	 
		  XYSeries Mobi_Het_Standard_Deviation = new XYSeries("Mobi-Het-Standard-Deviation");
		  for(int i=0; i<list_standard_deviation_mobi_het.size(); i++) {
			  total_standard_deviation_mobi_het+=list_standard_deviation_mobi_het.get(i);
			  Mobi_Het_Standard_Deviation.add(Times.get(i), list_standard_deviation_mobi_het.get(i));
		  }
		  total_standard_deviation_mobi_het=total_standard_deviation_mobi_het/list_standard_deviation_mobi_het.size();
		  if(Mobi_Het_And_Random_Compare) {
			  XYSeries Random_Standard_Deviation = new XYSeries("Random_Standard_Deviation");
			  for(int i=0; i<list_standard_deviation_random.size(); i++) {
				  total_standard_deviation_random+=list_standard_deviation_random.get(i);
				  Random_Standard_Deviation.add(Times.get(i), list_standard_deviation_random.get(i));
			  }
			  total_standard_deviation_random=total_standard_deviation_random/list_standard_deviation_random.size();
			  XYSeriesCollection dataset_3 = new XYSeriesCollection();
			  dataset_3.addSeries(Mobi_Het_Standard_Deviation);
			  dataset_3.addSeries(Random_Standard_Deviation);
			  Plotter Compare_Standard_Deviation = new Plotter(String.format("Compare Mobi-Het with total Average Standard_Deviation: %.4f and Random with total Average Standard_Deviation: %.4f", total_standard_deviation_mobi_het,total_standard_deviation_random),dataset_3,"Standard_Deviation");
			  s[2]="Compare_Standard_Deviation_Graph.png";
			  windowPlots.add(Compare_Standard_Deviation);
		  }
		  else {
			  Plotter Random_Standard_Deviation = new Plotter(String.format("Random experiment with total Average Standard_Deviation: %.4f", total_standard_deviation_mobi_het),Mobi_Het_Standard_Deviation,Color.BLUE);
			  s[2]="Random_Standard_Deviation_Graph.png";
			  windowPlots.add(Random_Standard_Deviation);
		  }
		 //Plot
		  String name = Results + "General/";
		  int i=0;
	        for (Plotter win : windowPlots) {
	            win.pack(); 
	            win.setVisible(true);
	           //RefineryUtilities.centerFrameOnScreen(win);
	            RefineryUtilities.positionFrameOnScreen(win, i*0.1, i*0.1); 
	            try {
	            	TimeUnit.SECONDS.sleep(1);
					getSaveSnapShot(win,  name + s[i]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}       
	            i++;
	        }      		  
	  	}
  }

private double compute_t_exec_on_selected_vms(List<Vm> Vm_waiting_list,List<Vm> Vm_Created_list,double mips_to_add,int number_of_cloudlets) {
	  double t_exec;
		if(Vm_Created_list.isEmpty()) 	{
			t_exec=mips_to_add/Vm_waiting_list.get(0).getTotalMipsCapacity();
		}
		else {
			double total_mips_of_the_queue_of_the_vm=0;
			double capacity_of_the_perfect_vm=Vm_Created_list.get(0).getTotalMipsCapacity();
			int size=0;
			for(Vm vm:Vm_Created_list) {		
				for(CloudletExecution cloudlet:vm.getCloudletScheduler().getCloudletWaitingList()) {
					total_mips_of_the_queue_of_the_vm+=(cloudlet.getNumberOfPes()*cloudlet.getCloudletLength());
					size++;
				}
				for(CloudletExecution cloudlet:vm.getCloudletScheduler().getCloudletExecList()) {
					total_mips_of_the_queue_of_the_vm+=(cloudlet.getNumberOfPes()*cloudlet.getRemainingCloudletLength());
					size++;
				}				
			}
			for(int i=0; i<number_of_cloudlets; i++) size++;
			if(size>Vm_Created_list.size()) t_exec=(total_mips_of_the_queue_of_the_vm+mips_to_add)/(capacity_of_the_perfect_vm*Vm_Created_list.size());
			else t_exec=(total_mips_of_the_queue_of_the_vm+mips_to_add)/(capacity_of_the_perfect_vm*size);
		}
		return t_exec;
  }

private void add_to_Response_Lists(double time) {
	List_of_Total_Response.add(Response_Var);
	List_of_Times_for_Response.add(time);
	Response_Var=0;
	}
}
