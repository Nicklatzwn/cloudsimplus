package nick;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.Cloudlet.Status;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Processor;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static java.lang.Math.*;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudsimplus.listeners.EventListener;
import org.cloudbus.cloudsim.point.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.io.*;
import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;
import java.util.Collections;
import java.util.*;

public class Nickolas {
	

	private static final int TIME_TO_FINISH_SIMULATION=300;
	 /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    
    private static final int NUMBER_OF_CLOUDLETS_DYNAMICALLY=1;
    
    /**
     * Number of Cloudlets to create dynamically.
     */
    
    private static final int VM_PES_NUMBER = 2;
     
    /**
     * 
     * Number of Cloudlets ID to create dynamically.
     */
    
    private int cloudletId = -1;
    
    private int createsVms = 0;
    
    private static final int HOSTS = 4;
    
    private static final int NUMBER_OF_VMS = 2;
    
    private static final double DATACENTER_SCHEDULING_INTERVAL = 1;
    
    private static final int HOST_PES = 50;
    
    private static final int Num_Of_Mobiles=4;
    
    private static final int Num_Of_Edge_Servers=4;
    
    private final CloudSim simulation;
    private final List<Cloudlet> finishedCloudletsatall;
    private final List<Double> lista;
    private List<ArrayList<Vm>> vm_total_list;
    private int CHECK_TIMER = 5; // Interval time 
    //Main Lists
    private List<Mobiles_Info> Mobiles_Info_List;
    private List<Edge_Servers_Info> Edge_Servers_Info_List;
    private List<Cloud_Servers_Info> Cloud_Servers_Info_List;
    //---------
    private int CPI=10;
    private int counter=0;
    private int counter_tick=0;
    private int counter_change_direction_of_the_mobiles=0;
    private final int time_to_change_direction=2;
    private int Interval=3;
    
    List<Double> Temp_List_Of_Minimum_Values;
    List<Edge_Servers_Info> Temp_List_Of_The_Best_Edges;
    List<Mobiles_Info> Temp_List_Of_The_Best_Mobiles;
    double prev_time=0;
    private double sum_of_sq;
    private int nickos=0;
    
public static void main(String[] args) {
    new Nickolas();
}

public Nickolas() {
	
	//Lists
	lista=new ArrayList<>();
	finishedCloudletsatall = new ArrayList<Cloudlet>();
	//Read Input
	 read_file();
   
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
     print_results();  
  //   new CloudletsTableBuilder(finishedCloudletsatall).build();
     
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
    for(int i=0; i<Mobiles_Info_List.size(); i++) { ArrayList<Vm> vmList = createVms(NUMBER_OF_VMS, VM_PES_NUMBER); vm_total_list.add(vmList); }
    for(int i=0; i<Edge_Servers_Info_List.size(); i++) { ArrayList<Vm> vmList = createVms(NUMBER_OF_VMS, VM_PES_NUMBER); vm_total_list.add(vmList); }
    for(int i=0; i<Cloud_Servers_Info_List.size(); i++) { ArrayList<Vm> vmList = createVms(NUMBER_OF_VMS, VM_PES_NUMBER); vm_total_list.add(vmList); }
    vm_total_list.subList(Num_Of_Mobiles, vm_total_list.size()).forEach(vmList -> vmList.forEach(vm->vm.setPeVerticalScaling(createVerticalPeScaling())));
    for(int i=0; i<Mobiles_Info_List.size(); i++)  Mobiles_Info_List.get(i).getBroker().submitVmList(vm_total_list.get(i));    
    for(int i=0; i<Edge_Servers_Info_List.size(); i++)  Edge_Servers_Info_List.get(i).getBroker().submitVmList(vm_total_list.get(Num_Of_Mobiles+i)); 
    for(int i=0; i<Cloud_Servers_Info_List.size(); i++)  Cloud_Servers_Info_List.get(i).getBroker().submitVmList(vm_total_list.get(Num_Of_Mobiles+Num_Of_Edge_Servers+i));
}

private void set_Datacenters_and_brokers() {

	//First step
	Mobiles_Info_List=new ArrayList<Mobiles_Info>();
	Edge_Servers_Info_List=new ArrayList<Edge_Servers_Info>();
	Cloud_Servers_Info_List=new ArrayList<Cloud_Servers_Info>();
	
	Temp_List_Of_Minimum_Values =  new ArrayList<Double>();
	Temp_List_Of_The_Best_Edges =  new ArrayList<Edge_Servers_Info>();
	Temp_List_Of_The_Best_Mobiles = new ArrayList<Mobiles_Info>();
    int number=TIME_TO_FINISH_SIMULATION;
	for(int i=0; i<Num_Of_Mobiles; i++) {
		int x=(new Random()).nextInt(number);
		int y=(new Random()).nextInt(number);
		Mobiles_Info_List.add(new Mobiles_Info(createDatacenter(6,x,y),2000,i,number));
		Mobiles_Info_List.get(Mobiles_Info_List.size()-1).getDatacenter().setName("Mobile_Device_"+i);
		energy_cpu_for_hosts(Mobiles_Info_List.get(Mobiles_Info_List.size()-1));
	}
	int number_1=TIME_TO_FINISH_SIMULATION/4;
	int number_2=3*TIME_TO_FINISH_SIMULATION/4;
	
	Edge_Servers_Info_List.add(new Edge_Servers_Info(createDatacenter(1,number_1,number_1)));
	Edge_Servers_Info_List.get(Edge_Servers_Info_List.size()-1).getDatacenter().setName("Edge_Server_1");
	Edge_Servers_Info_List.add(new Edge_Servers_Info(createDatacenter(2,number_1,number_2)));
	Edge_Servers_Info_List.get(Edge_Servers_Info_List.size()-1).getDatacenter().setName("Edge_Server_2");
	Edge_Servers_Info_List.add(new Edge_Servers_Info(createDatacenter(3,number_2,number_1)));
	Edge_Servers_Info_List.get(Edge_Servers_Info_List.size()-1).getDatacenter().setName("Edge_Server_3");
	Edge_Servers_Info_List.add(new Edge_Servers_Info(createDatacenter(4,number_2,number_2)));
	Edge_Servers_Info_List.get(Edge_Servers_Info_List.size()-1).getDatacenter().setName("Edge_Server_4");
	Cloud_Servers_Info_List.add(new Cloud_Servers_Info(createDatacenter(5,1000,1000),100));
	Cloud_Servers_Info_List.get(Cloud_Servers_Info_List.size()-1).getDatacenter().setName("Cloud_Server");

    //Second_step
	for(int i=0; i<Mobiles_Info_List.size(); i++) Mobiles_Info_List.get(i).setBroker(new DatacenterBrokerSimple(simulation,"Mobile_Broker_"+i));
    for(int i=0; i<Edge_Servers_Info_List.size(); i++) {
    	Edge_Servers_Info_List.get(i).setBroker(new DatacenterBrokerSimple(simulation,"Edge_Server_Broker_"+i));
    	Edge_Servers_Info_List.get(i).add_zone(54);
    	Edge_Servers_Info_List.get(i).add_zone(48);
    	Edge_Servers_Info_List.get(i).add_zone(36);
    	Edge_Servers_Info_List.get(i).add_zone(24);
    	Edge_Servers_Info_List.get(i).add_zone(11);
    	Edge_Servers_Info_List.get(i).add_zone(6);
    	Edge_Servers_Info_List.get(i).add_zone(2);
    	Edge_Servers_Info_List.get(i).add_zone(1);
    }
    for(int i=0; i<Cloud_Servers_Info_List.size(); i++) Cloud_Servers_Info_List.get(i).setBroker(new DatacenterBrokerSimple(simulation,"Cloud_Broker_"+i)); 
    
    set_broker_utils(Mobiles_Info_List);
    set_broker_utils(Edge_Servers_Info_List);
    set_broker_utils(Cloud_Servers_Info_List);
    
    for(Mobiles_Info mobiles:Mobiles_Info_List) mobiles.getDatacenter().getPoint().set_speed(1,1);
    for(Edge_Servers_Info egde:Edge_Servers_Info_List) egde.getDatacenter().getPoint().set_speed(0,0);
    for(Cloud_Servers_Info cloud:Cloud_Servers_Info_List) cloud.getDatacenter().getPoint().set_speed(0,0); 
}

private void set_broker_utils(final List<? extends Device_Info> list) {
	 list.forEach(info -> { 
		 info.getBroker().setDatacenterSupplier(() -> info.getDatacenter());
      	 info.getBroker().setVmDestructionDelayFunction(vm -> { return (double) TIME_TO_FINISH_SIMULATION; });
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
			if(Math.pow(Mobiles_Info_List.get(id_mob).getDatacenter().getPoint().getxPoint()- Edge_Servers_Info_List.get(id_edge).getDatacenter().getPoint().getxPoint(),2)+Math.pow(Mobiles_Info_List.get(id_mob).getDatacenter().getPoint().getyPoint()-Edge_Servers_Info_List.get(id_edge).getDatacenter().getPoint().getyPoint(),2) < Math.pow(Edge_Servers_Info_List.get(id_edge).get_radious(),2)) {
				Mobiles_Info_List.get(id_mob).add_edge_point_to_mobile(id_edge);
				nickos++;
			}
		}	
	}
	
	for(Mobiles_Info info:Mobiles_Info_List) {
		if(!info.get_edge_point_to_mobile().isEmpty()) {
			compute_objective_fuction_for_this_mobile(info);
			Temp_List_Of_Mobiles.add(info);
		}
		else Temp_List_Of_Lonely_Mobiles.add(info);
	}
	
	for(int i=0; i<Temp_List_Of_Mobiles.size(); i++) {
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
		Temp_List_Of_Minimum_Values.clear();
		Temp_List_Of_The_Best_Edges.clear();
		Temp_List_Of_The_Best_Mobiles.clear();
		for(Mobiles_Info info:Temp_List_Of_Mobiles) compute_objective_fuction_for_this_mobile(info);
	}
	
	Comparator<Mobiles_Info> sort_by_battery= comparingLong(mobile->mobile.get_Battery());
	Temp_List_Of_Lonely_Mobiles.sort(sort_by_battery);
	if(!Temp_List_Of_Lonely_Mobiles.isEmpty()) {
		Cloud_Servers_Info_List.get(0).execute_the_cloudlets_from_mobile_to_main_Cloud_Server(Temp_List_Of_Lonely_Mobiles.get(0));
		Temp_List_Of_Lonely_Mobiles.get(0).reduce_by_send_recieve_to_Cloud_Server();
		Temp_List_Of_Lonely_Mobiles.get(0).add_to_the_battery_plot(time);
		Temp_List_Of_Lonely_Mobiles.get(0).add_3g_power(Cloud_Servers_Info_List.get(0).get_E_speed(), time);
	}
	for(int i=1; i<Temp_List_Of_Lonely_Mobiles.size(); i++) {
		Temp_List_Of_Lonely_Mobiles.get(i).execute_your_own_tasks();
	}
	
	Mobiles_Info_List.forEach(mobile->mobile.clear_edge_point_and_uncheck_mobile_and_submitted_list_of_cloudlets());
	Temp_List_Of_Minimum_Values.clear();
	Temp_List_Of_The_Best_Edges.clear();
	Temp_List_Of_The_Best_Mobiles.clear();
}
		
private void check_the_mobile(Mobiles_Info mobile, Edge_Servers_Info the_best_edge,double time) {
	// TODO Auto-generated method stub
		int temp2=mobile.get_the_list_of_cloudlets_that_are_going_to_be_submitted().size();
		int perfect_data_zone=predict_the_zone(mobile,the_best_edge,0,0);
		mobile.add_wifi_power_to_send_cloudlets(perfect_data_zone,time);
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
				Edge_Servers_Info the_best_edge=Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(0));
				for(int i=0; i<info.get_edge_point_to_mobile().size(); i++) {
					double assosiativity=calculate_assosiativity(Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)),info);
					int zwnh_send=predict_the_zone(info,Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)),0,0);
					double s=computation_of_standard_deviation(Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)));
				
					double bytes_to_send=0.0,MI_to_exec=0.0,delay=0.0,bytes_to_send_back=0.0;
					for(Cloudlet cloudlet:info.get_the_list_of_cloudlets_that_are_going_to_be_submitted()) { bytes_to_send+=cloudlet.getFileSize(); MI_to_exec+=cloudlet.getTotalLength(); delay+=cloudlet.getSubmissionDelay(); bytes_to_send_back+=cloudlet.getOutputSize(); }
					double t_send=bytes_to_send/(zwnh_send*1000000);
					double t_exec=compute_t_exec(Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)),MI_to_exec);
				
					double t_so_far=delay+t_send+t_exec;
					int zwnh_receive=predict_the_zone(info,Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i)),t_so_far*speed_x,t_so_far*speed_y);
					double t_send_back=bytes_to_send_back/(zwnh_receive*1000000);
					s_fn=(t_send_back+t_so_far)/assosiativity+s;
					if(s_fn<s_old) {
						s_old=s_fn;
						the_best_edge=Edge_Servers_Info_List.get(info.get_edge_point_to_mobile().get(i));
					}
			}
				Temp_List_Of_Minimum_Values.add(s_old);
				Temp_List_Of_The_Best_Edges.add(the_best_edge);
				Temp_List_Of_The_Best_Mobiles.add(info);
		}
}

private double compute_t_exec(Edge_Servers_Info edge_Servers_Info, double mI_to_exec) {
	// TODO Auto-generated method stub
	double t_exec;
	if(edge_Servers_Info.getBroker().getVmCreatedList().isEmpty()) 	t_exec=mI_to_exec/edge_Servers_Info.getBroker().getWaitingVm(0).getTotalMipsCapacity();
	else {
		double total_time_of_the_queue_of_the_vm=0,total_min_time_of_the_queue_of_the_vm=0;
		double capacity_of_the_perfect_vm=edge_Servers_Info.getBroker().getVmCreatedList().get(0).getTotalMipsCapacity();
		for(Vm vm:edge_Servers_Info.getBroker().getVmCreatedList()) {
		
			for(CloudletExecution cloudlet:vm.getCloudletScheduler().getCloudletWaitingList()) {
				total_time_of_the_queue_of_the_vm=vm.getTotalMipsCapacity()/cloudlet.getCloudletLength();
			}
			for(CloudletExecution cloudlet:vm.getCloudletScheduler().getCloudletExecList()) {
				total_time_of_the_queue_of_the_vm=vm.getTotalMipsCapacity()/cloudlet.getRemainingCloudletLength();
			}
			if(total_time_of_the_queue_of_the_vm<=total_min_time_of_the_queue_of_the_vm) {
				total_min_time_of_the_queue_of_the_vm=total_time_of_the_queue_of_the_vm;
				capacity_of_the_perfect_vm=vm.getTotalMipsCapacity();
			}
		
		}
		t_exec=total_min_time_of_the_queue_of_the_vm+capacity_of_the_perfect_vm/mI_to_exec;
	}
	return t_exec;
}

private int predict_the_zone(Mobiles_Info inform,Edge_Servers_Info info,double Dx,double Dy) {
	double x0=info.getDatacenter().getPoint().getxPoint();
	double y0=info.getDatacenter().getPoint().getyPoint();
	double xmob=inform.getDatacenter().getPoint().getxPoint()+Dx;
	double ymob=inform.getDatacenter().getPoint().getyPoint()+Dx;
	int i=0;
	int temp=info.get_the_zones_lists().get(0);
	while(Math.pow(x0-xmob, 2)+Math.pow(y0-ymob, 2)>Math.pow(info.get_radious(), 2)*(i+1)/info.get_the_zones_lists().size()) {
		temp=info.get_the_zones_lists().get(i);
		i++;
		if(i==info.get_the_zones_lists().size()) break;
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

private double computation_of_standard_deviation(Edge_Servers_Info inform) {
	sum_of_sq=0.0;
	ArrayList<Double> list_of_values=new ArrayList<Double>();
	
	if(inform.getBroker().getVmCreatedList().isEmpty()) {
		for(int i=0; i<inform.getBroker().getVmWaitingList().size()-1; i++) list_of_values.add(0.0);
	}
	else {
	inform.getBroker().getVmCreatedList().forEach(vm-> {
			list_of_values.add(vm.getTotalCpuMipsUsage()/vm.getTotalMipsCapacity());		
	});
	}
	double m=list_of_values.stream().count()/list_of_values.size();
	list_of_values.forEach(val-> sum_of_sq+=Math.pow(val-m,2));
	return sum_of_sq/Math.sqrt(list_of_values.size());
	
}
		
private void print_results() {
	
	take_finished_cloudlets_from_all_brokers(simulation.clock());
	System.out.printf("\t\t----->>> %d cloudlets",finishedCloudletsatall.size());

	final Comparator<Cloudlet> sortByCloudletId = comparingDouble(c -> c.getId());
	final Comparator<Cloudlet> sortByStartTime = comparingDouble(Cloudlet::getExecStartTime);
	finishedCloudletsatall.sort(sortByCloudletId.thenComparing(sortByStartTime));
	for(final Cloudlet cloudlet:finishedCloudletsatall) {
		System.out.printf(
                "\t\tTime %6.1f: CPU Usage Time: %6.1f inside Datacenter : %d with name: %s and id: %d\n",
                cloudlet.getFinishTime(), cloudlet.getActualCpuTime(), cloudlet.getLastDatacenter().getId(),cloudlet.getLastDatacenter().getName(),cloudlet.getId());
	}
	System.out.printf("\t\t>>>>>CLOUD_SERVER_INFO<<<<<\n");
	for(Cloud_Servers_Info information:Cloud_Servers_Info_List) {
		System.out.printf("\t\t------------------------\n");
		System.out.printf("\t\tIn: %s has executed %d Cloudlets with average response time %f\n",information.getDatacenter().getName(),information.number_of_cloudlets(),information.response_time());
		System.out.printf("\t\t------------------------\n");	
	}
	System.out.printf("\t\t>>>>>EDGE_SERVER_INFO<<<<<\n");
	for(Edge_Servers_Info information:Edge_Servers_Info_List) {
		System.out.printf("\t\t------------------------\n");
		System.out.printf("\t\tIn: %s has executed %d Cloudlets with average response time %f\n",information.getDatacenter().getName(),information.number_of_cloudlets(),information.response_time());
		System.out.printf("\t\t------------------------\n");
	}
	System.out.printf("\t\t>>>>>MOBILE_INFO<<<<<\n");
	for(Mobiles_Info information:Mobiles_Info_List) {
		System.out.printf("\t\t------------------------\n");
		System.out.printf("\t\tIn: %s has executed %d Cloudlets with average response time %f with Total Energy Consumption: (CPU): %f, (Wifi): %f, (3g): %f\n",information.getDatacenter().getName(),information.number_of_cloudlets(),information.response_time(),information.getPower(),information.get_energy_for_wifi_module(),information.get_3g_power());
		System.out.printf("And the final Possition of the Mobile is: x:%.2f y:%.2f and the final Battery is: %d\n",information.getDatacenter().getPoint().getxPoint(),information.getDatacenter().getPoint().getyPoint(),information.get_Battery());
		System.out.printf("\t\t------------------------\n");
	}
	Mobiles_Info_List.forEach(mobile->mobile.show_the_plots());
	System.out.println(getClass().getSimpleName() + " finished!");
	System.out.printf("\t\t----->>> %d", nickos);
}
     
private void energy_cpu_for_hosts(Mobiles_Info mobile) {
	double B_freq,B_idle;
    double freq_base=lista.get(0);
    int index=0;
    double freq=mobile.getDatacenter().getHost(0).getMips()/CPI;
    
    if(freq<freq_base){  B_freq=lista.get(1); B_idle=lista.get(2); }
    else if(freq>lista.get(lista.size()-3)) {  B_freq=lista.get(lista.size()-2); B_idle=lista.get(lista.size()-1); }
    else {
   	 while(freq>freq_base) {
   		 freq_base=lista.get(3*index);
   		 if(freq_base==lista.size()-3) break;
   		 index++;
   	 }
   	 B_freq=abs(((lista.get(3*index+1)-lista.get(3*index-3+1))/(lista.get(3*index)-lista.get(3*index-3)))*(freq-lista.get(3*index-3))+lista.get(3*index-3+1));
   	 B_idle=abs(((lista.get(3*index+2)-lista.get(3*index-3+2))/(lista.get(3*index)-lista.get(3*index-3)))*(freq-lista.get(3*index-3))+lista.get(3*index-3+2));
    }
    mobile.set_cpu_B_freq(B_freq);
    mobile.set_cpu_B_idle(B_idle);
}

private ArrayList<Vm> createVms(int vms, int pesNumber) {
	
    //Creates a container to store VMs. This list is passed to the broker later
	ArrayList<Vm> list = new ArrayList<>(vms);

    //VM Parameters
    long size = 10000; //image size (Megabyte)
    int ram = 1024; //vm memory (Megabyte)
    int mips = 2000;
    long bw = 1000;
    //int pesNumber = 1; //number of cpus

    //create VMs
    for (int i = 0; i < vms; i++) {
    	final int id = createsVms++;
        Vm vm = new VmSimple(id, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());

        list.add(vm);
    }

    return list;
}

private List<Cloudlet> createCloudlets(int cloudlets,int pesNumber) {
	List<Cloudlet> list = new ArrayList<>(cloudlets);
	for(int i=0; i<cloudlets; i++) {
    //cloudlet parameters
    long length = ThreadLocalRandom.current().nextInt(10000,15000)/10;
    long fileSize = ThreadLocalRandom.current().nextInt(300,400);
    long outputSize = ThreadLocalRandom.current().nextInt(300,400);
    //int pesNumber = 1;
 //   UtilizationModel utilizationModel = new UtilizationModelFull();
    
    /*Define that the utilization of CPU, RAM and Bandwidth is random.*/
    UtilizationModel cpuUtilizationModel = new UtilizationModelStochastic();
    UtilizationModel ramUtilizationModel = new UtilizationModelStochastic();
    UtilizationModel bwUtilizationModel  = new UtilizationModelStochastic();

    double delay=1.5;
    Cloudlet cloudlet = new CloudletSimple(++cloudletId,length, pesNumber);
    		
    cloudlet.setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModelCpu(cpuUtilizationModel)
            .setUtilizationModelRam(ramUtilizationModel)
            .setUtilizationModelBw(bwUtilizationModel)
            .setSubmissionDelay(delay);
    list.add(cloudlet);
	}
	
    return list;
}

private Datacenter createDatacenter(int id,double x,double y) {
    // Here are the steps needed to create a DatacenterSimple:
    // 1. We need to create a list to store one or more
    //    Machines
    List<Host> hostList = new ArrayList<>();
    long mips,ram,storage,bw;

    // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
    //    create a list to store these PEs before creating
    //    a Machine.
    List<Pe> peList = new ArrayList<>();
    switch(id) {
    	case 1 :
    		//Edge Server 1
    		mips = 4000; ram = 2*4048; storage = 2000000; bw = 10000; 
    		for(int i=0; i<HOST_PES; i++) peList.add(new PeSimple(mips, new PeProvisionerSimple()));
  
    	case 2 : 
    		//Edge Server 2
    		mips = 4000; ram = 2*4048; storage = 2000000; bw = 10000; 
    		for(int i=0; i<HOST_PES; i++) peList.add(new PeSimple(mips, new PeProvisionerSimple())); 
    		
    	case 3 : 
    		//Edge Server 3
    		mips = 4000; ram = 2*4048; storage = 2000000; bw = 10000; 
    		for(int i=0; i<HOST_PES; i++) peList.add(new PeSimple(mips, new PeProvisionerSimple())); 
    		
    	case 4 : 
    		//Edge Server 4
    		mips = 4000; ram = 2*4048; storage = 2000000; bw = 10000; 
    		for(int i=0; i<HOST_PES; i++) peList.add(new PeSimple(mips, new PeProvisionerSimple())); 	
    				
    	case 5 :
    		//Cloud Server
    		mips = 6000; ram = 4*4048; storage = 2*2000000; bw = 10000;
    		for(int i=0; i<HOST_PES; i++) peList.add(new PeSimple(mips, new PeProvisionerSimple()));
    		
    	default :
    		//Mobile Device
    		mips = 2000;
    		//dual-core machine
    		for(int i=0; i<HOST_PES; i++) peList.add(new PeSimple(mips, new PeProvisionerSimple())); 
    		
    		//4. Create Hosts with its id and list of PEs and add them to the list of machines
    		 ram = 4048; //host memory (Megabyte)
    		 storage = 2000000; //host storage (Megabyte)
    		 bw = 10000; //Megabits/s	
    }
    Host host = new HostSimple(ram, bw, storage, peList)
        .setRamProvisioner(new ResourceProvisionerSimple())
        .setBwProvisioner(new ResourceProvisionerSimple())
        .setVmScheduler(new VmSchedulerTimeShared());
    for(int i=0; i<HOSTS; i++) hostList.add(host);
    //Point of datacenter
    Point thesh=new Point(x,y);
    
    return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple(), thesh).setSchedulingInterval(DATACENTER_SCHEDULING_INTERVAL);
            
}

private void onClockTickListener(EventInfo eventInfo) {
	
    //CPU_METER_ENERGY
	if(Math.floor(eventInfo.getTime()) % Interval == 0) {
		if(counter_tick==0) {
			
			for(Mobiles_Info information:Mobiles_Info_List) {
				double power_temp=0.0;
				for(Host host:information.getDatacenter().getHostList()) power_temp+=host.getUtilizationOfCpu()*information.get_cpu_B_freq()+information.get_cpu_B_idle();
				double energy_density=power_temp+information.get_prev_Energy()*(eventInfo.getTime()-prev_time)/2;
				information.addPower(energy_density);
				information.set_prev_Energy(power_temp);
				information.add_to_the_cpu_energy_plot(eventInfo.getTime(), energy_density);
			}
			
			
			//Move Mobile_Device
			Mobiles_Info_List.forEach(mobile -> mobile.getDatacenter().getPoint().move(Math.floor(eventInfo.getTime())-prev_time));
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

	for(Mobiles_Info information:Mobiles_Info_List) {
		double averageResponseTime=0.0;
		int size=0;
		List<Cloudlet> temp_cloudlets = new ArrayList<>();
		temp_cloudlets.addAll(information.getBroker().getCloudletFinishedList());
		information.getBroker().getCloudletFinishedList().clear();
		temp_cloudlets.removeAll(finishedCloudletsatall);
		Mobiles_Info_List.forEach(info->info.find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(temp_cloudlets,0, 0, time));
		for(Cloudlet cloudlet:temp_cloudlets) {
				averageResponseTime+=cloudlet.getActualCpuTime();
				size+=1;
				finishedCloudletsatall.add(cloudlet);
	  }
		
		information.add_responsetime_size(averageResponseTime, size);
	}
	
	
	for(Edge_Servers_Info information:Edge_Servers_Info_List) {
		double averageResponseTime=0.0;
		int size=0;
		List<Cloudlet> temp_cloudlets = new ArrayList<>();
		temp_cloudlets.addAll(information.getBroker().getCloudletFinishedList());
		information.getBroker().getCloudletFinishedList().clear();
		temp_cloudlets.removeAll(finishedCloudletsatall);
		
		Mobiles_Info_List.forEach(info->info.find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(temp_cloudlets,1,predict_the_zone(info,information, 0, 0), time));
	for(Cloudlet cloudlet:temp_cloudlets) {
		averageResponseTime+=cloudlet.getActualCpuTime();
		size+=1;
		finishedCloudletsatall.add(cloudlet);
				
	}
	information.add_responsetime_size(averageResponseTime, size);
}
	for(Cloud_Servers_Info information:Cloud_Servers_Info_List) {
		double averageResponseTime=0.0;
		int size=0;
		List<Cloudlet> temp_cloudlets = new ArrayList<>();
		temp_cloudlets.addAll(information.getBroker().getCloudletFinishedList());
		information.getBroker().getCloudletFinishedList().clear();
		temp_cloudlets.removeAll(finishedCloudletsatall);
		Mobiles_Info_List.forEach(info->info.find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(temp_cloudlets,2,information.get_E_speed(), time));

	for(Cloudlet cloudlet:temp_cloudlets) {
		averageResponseTime+=cloudlet.getActualCpuTime();
		size+=1;
		finishedCloudletsatall.add(cloudlet);
		}
	information.add_responsetime_size(averageResponseTime, size);
	
	}
}

private void create_new_cloudlets_and_possible_direction_of_the_mobiles(double time) {
		for(Mobiles_Info info:Mobiles_Info_List) info.store_the_created_cloudlets(createCloudlets(NUMBER_OF_CLOUDLETS_DYNAMICALLY,VM_PES_NUMBER));
		
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
}


