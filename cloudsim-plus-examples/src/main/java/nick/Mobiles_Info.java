package org.cloudbus.cloudsim.nickos;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import java.util.Comparator;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;
import java.awt.Color;
import org.cloudbus.cloudsim.plotter.Plotter;

public class Mobiles_Info extends Device_Info{

	private int BATTERY_LIFE; 
	private int start_battery;
    private final int BATTERY_REDUCE_EXE=10;
    private final int BATTERY_REDUCE_SEND_RECEIVE_WIFI=1;
    private final int BATTERY_REDUCE_SEND_RECEIVE_3G=2;
    private final int BATTERY_REDUCE_IDLE_PERIOD=1;
    private List<Cloudlet> list_of_belonging_cloudlets;
    private List<Cloudlet> list_of_cloudlets_that_are_going_to_be_submitted;
    private List<Integer> pointer_to_Edge_Server;
    private final double rangeMin=5;
	private final double rangeMax=10;
	private final double energy_per_byte_for_Trans_15=316.67;
	private final double scale_Trans=0.5;
	private final double scale_Data_Rate=0.0067;
	private double energy_for_Wifi_module;
	private double energy_for_3g_module;
	private int mob_id;
	private boolean checked;
	private double B_freq,B_idle;
	
	//Plots
	private final XYSeries CPU_SERIES;
	private final XYSeries WIFI_SERIES;
	private final XYSeries E_SERIES;
	private final XYSeries BATTERY_SERIES;
	
    
	public Mobiles_Info(Datacenter datacenter,int BATTERY_LIFE,int mob_id,int limit) {
		super(datacenter);
		// TODO Auto-generated constructor stub
		this.BATTERY_LIFE=BATTERY_LIFE;
		start_battery=BATTERY_LIFE;
		super.getDatacenter().getPoint().set_limit(limit);
		list_of_belonging_cloudlets=new ArrayList<Cloudlet>();
		list_of_cloudlets_that_are_going_to_be_submitted=new ArrayList<Cloudlet>();
		pointer_to_Edge_Server=new ArrayList<Integer>();
		this.mob_id=mob_id;
		checked=false;
		//Plot
		CPU_SERIES = new XYSeries("CPU_Energy");
		WIFI_SERIES = new XYSeries("WIFI_Energy");
		E_SERIES = new XYSeries("3G_Energy");
		BATTERY_SERIES = new XYSeries("BATTERY");
	}
	public int get_Battery() {
		return BATTERY_LIFE;
	}
	public void reduce_by_exe() {
		BATTERY_LIFE-=BATTERY_REDUCE_EXE;
	}
	public void reduce_by_send_receive_wifi() {
		BATTERY_LIFE-=BATTERY_REDUCE_SEND_RECEIVE_WIFI;
	}
	public void reduce_by_send_recieve_to_Cloud_Server() {
		BATTERY_LIFE-=BATTERY_REDUCE_SEND_RECEIVE_3G;
	}
	public void reduce_by_idle() {
		BATTERY_LIFE-=BATTERY_REDUCE_IDLE_PERIOD;
	}
	public void store_the_created_cloudlets(List<Cloudlet> list_cloudlet) {
		for(Cloudlet cloudlet:list_cloudlet) {
			list_of_belonging_cloudlets.add(cloudlet);
			list_of_cloudlets_that_are_going_to_be_submitted.add(cloudlet);
		}
	}
	public void find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(List<Cloudlet> cloudlets,int device_id,int data_rate_zone,double time) {
		List<Cloudlet> temp_cloudlets = new ArrayList<>();
		temp_cloudlets.addAll(list_of_belonging_cloudlets);
		temp_cloudlets.retainAll(cloudlets);
		for(Cloudlet cloudlet:temp_cloudlets) {
			if(device_id==0) reduce_by_exe();
			else if(device_id==1) {
			reduce_by_send_receive_wifi();
			reduce_by_idle();
			add_wifi_power_to_receive_finished_cloudlets(data_rate_zone,cloudlet.getFileSize(),time);
			}
			else {
				reduce_by_send_recieve_to_Cloud_Server();
				reduce_by_idle();
				add_3g_power(data_rate_zone,time);
			}
		}
		add_to_the_battery_plot(time);
		list_of_belonging_cloudlets.removeAll(cloudlets);
	}
	public void add_edge_point_to_mobile(int id) {
		pointer_to_Edge_Server.add(id);
	}
	public List<Integer> get_edge_point_to_mobile() {
		return pointer_to_Edge_Server;
	}
	public void clear_edge_point_and_uncheck_mobile_and_submitted_list_of_cloudlets() {
		pointer_to_Edge_Server.clear();
		list_of_cloudlets_that_are_going_to_be_submitted.clear();
		checked=false;	
	}
	public void change_direction_and_speed() {
		double dt=1;
		double x=super.getDatacenter().getPoint().getxPoint();
		double y=super.getDatacenter().getPoint().getyPoint();
		double x_new=x+ rangeMin + (rangeMax - rangeMin) * (new Random()).nextDouble();
		double y_new=y+rangeMin + (rangeMax - rangeMin) * (new Random()).nextDouble();
		double a=new Random().nextDouble();
		double b=new Random().nextDouble();
		if(a>0.5 && b>0.5) {
			a=b=1.0;
		}
		else if(a<0.5 && b>0.5) {
			a=-1.0;
			b=1.0;
		}
		else if(a<0.5 && b<0.5) {
			a=b=-1.0;
		}
		else {
			a=1.0;
			b=-1.0;
		}
		x_new*=a;
		y_new*=b;
		if(x_new>x && y_new>y) super.getDatacenter().getPoint().set_speed((x_new-x)/dt,(y_new-y)/dt);
		else if(x_new<x && y_new<y) super.getDatacenter().getPoint().set_speed((x_new+x)/dt,(y_new+y)/dt);
		else if(x_new>x && y_new<y) super.getDatacenter().getPoint().set_speed((x_new-x)/dt,(y_new+y)/dt);
		else super.getDatacenter().getPoint().set_speed((x_new+x)/dt,(y_new-y)/dt);
	
	}
	public List<Cloudlet> get_the_list_of_cloudlets_that_are_going_to_be_submitted() {
		return list_of_cloudlets_that_are_going_to_be_submitted;
	}
	public void execute_your_own_tasks() {
		super.getBroker().submitCloudletList(list_of_cloudlets_that_are_going_to_be_submitted);
	}
	public void add_energy_for_wifi_module(double energy) {
		energy_for_Wifi_module+=energy;
	}
	public double get_energy_for_wifi_module() {
		return energy_for_Wifi_module;
	}
	public void add_wifi_power_to_send_cloudlets(int data_rate_of_the_zone,double time) {
		int length=0;
		for(Cloudlet cloudlet:list_of_cloudlets_that_are_going_to_be_submitted) length+=cloudlet.getFileSize();
		double T_trans=length/data_rate_of_the_zone;
		double energy=calculate_the_wifi_energy(T_trans,data_rate_of_the_zone);
		add_energy_for_wifi_module(energy);
		add_to_the_wifi_plot(time,energy);
		
	}
	private double calculate_the_wifi_energy(double t_trans, int data_rate_of_the_zone) {
		// TODO Auto-generated method stub
		double temp=15;
		double energy=energy_per_byte_for_Trans_15;
		while(t_trans>temp) {
			temp*=2;
			energy*=scale_Trans;
		}
		energy+=data_rate_of_the_zone*scale_Data_Rate;
		return energy;
	}
	public void add_wifi_power_to_receive_finished_cloudlets(int data_rate_of_the_zone,long total_length,double time) {
		double T_trans=total_length/data_rate_of_the_zone;
		double energy=calculate_the_wifi_energy(T_trans,data_rate_of_the_zone);
		add_energy_for_wifi_module(energy);
		add_to_the_wifi_plot(time,energy);
	}
	public void add_3g_power(double f,double time) {
		add_to_the_3g_energy_plot(time,20+0.0905*f);
		energy_for_3g_module+=20+0.0905*f;
	}
	public double get_3g_power() {
		return energy_for_3g_module;
	}
	public int get_mob_id() {
		return mob_id;
	}
	public void checked() {
		checked=true;
	}
	public boolean is_checked() {
		return checked;
	}
	public void set_cpu_B_freq(double B_freq) {
		this.B_freq=B_freq;
	}
	public double get_cpu_B_freq() {
		return B_freq;
	}
	public void set_cpu_B_idle(double B_idle) {
		this.B_idle=B_idle;
	}
	public double get_cpu_B_idle() {
		return B_idle;
	}
	public void add_to_the_battery_plot(double time) {
		BATTERY_SERIES.add(time, BATTERY_LIFE);
	}
	public void add_to_the_wifi_plot(double time,double wifi_energy) {
		WIFI_SERIES.add(time, wifi_energy);
	}
	public void add_to_the_cpu_energy_plot(double time,double cpu_energy) {
		CPU_SERIES.add(time, cpu_energy);
	}
	public void add_to_the_3g_energy_plot(double time,double E_energy) {
		E_SERIES.add(time, E_energy);
	}
	public void show_the_plots() {
		int r,g,b;
		 //initializes Plotter
		switch(mob_id) {
			case 0:
				g=b=0;
				r=255;
			case 1:
				r=b=0;
				g=255;
			case 2:
				r=g=0;
				b=255;
			default:
				r=102;
				g=0;
				b=102;
		}
		List<Plotter> windowPlots = new ArrayList<>();
		Plotter cpuWin = new Plotter(String.format("Energy_CPU of the Mobile:%d with total energy Sum:%6.2f",mob_id,super.getPower()), CPU_SERIES,new Color(r,g,b));
		Plotter wifiWin = new Plotter(String.format("Energy_WIFI of the Mobile:%d with total energy Sum:%6.2f",mob_id,energy_for_Wifi_module), WIFI_SERIES,new Color(r,g,b));
		Plotter EWin = new Plotter(String.format("Energy_3G of the Mobile:%d with total energy Sum:%6.2f",mob_id,energy_for_3g_module), E_SERIES,new Color(r,g,b));
		Plotter BatteryWin = new Plotter(String.format("The Battery of the Mobile:%d",mob_id), BATTERY_SERIES,new Color(r,g,b));
		windowPlots.add(cpuWin);
        windowPlots.add(wifiWin);
        windowPlots.add(EWin);
        windowPlots.add(BatteryWin);
        int i=0;
        for (Plotter win : windowPlots) {
            win.pack();
            //RefineryUtilities.centerFrameOnScreen(win);
            RefineryUtilities.positionFrameOnScreen(win, i*0.1, i*0.1);
            win.setVisible(true);
            i++;
        }
	}
	public int start_battery() {
		return start_battery;
	}
}