package nick;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import java.util.Comparator;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;

import org.cloudbus.cloudsim.plotter.Plotter;
import org.cloudbus.cloudsim.power.models.PowerAware;

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
	private double energy_for_Wifi_module;
	private double energy_for_3g_module;
	private int mob_id;
	private boolean checked;
	private String path;
	
	//Plots
	private final List<XYSeries> CPU_SERIES;
	private final XYSeries WIFI_SERIES;
	private final XYSeries E_SERIES;
	private final XYSeries BATTERY_SERIES;
	private final XYSeries RESPONSE_SERIES;
	
    
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
		CPU_SERIES=new ArrayList<XYSeries>();
		WIFI_SERIES = new XYSeries("WIFI_Energy");
		E_SERIES = new XYSeries("3G_Energy");
		BATTERY_SERIES = new XYSeries("BATTERY");
		RESPONSE_SERIES = new XYSeries("RESPONE");
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
	public void find_requested_cloudlet_reduce_battery_and_add_wifi_or_3g_energy(List<Cloudlet> cloudlets,int device_id,int data_rate_zone,int RSSI,double time) {
		List<Cloudlet> temp_cloudlets = new ArrayList<>();
		temp_cloudlets.addAll(list_of_belonging_cloudlets);
		temp_cloudlets.retainAll(cloudlets);
		for(Cloudlet cloudlet:temp_cloudlets) {
			if(device_id==0) reduce_by_exe();
			else if(device_id==1) {
			reduce_by_send_receive_wifi();
			reduce_by_idle();
			add_wifi_power_to_receive_finished_cloudlets(data_rate_zone,cloudlet.getOutputSize(),RSSI,time);
			}
			else {
				reduce_by_send_recieve_to_Cloud_Server();
				reduce_by_idle();
				double T_trans=cloudlet.getOutputSize()/data_rate_zone;
				add_3g_power(data_rate_zone,time,T_trans);
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
	public void add_wifi_power_to_send_cloudlets(int data_rate_of_the_zone,int RSSI,double time) {
		int length=0;
		for(Cloudlet cloudlet:list_of_cloudlets_that_are_going_to_be_submitted) length+=cloudlet.getFileSize();
		double T_trans=length/data_rate_of_the_zone;
		double energy=calculate_the_wifi_energy(T_trans,RSSI);
		add_energy_for_wifi_module(energy);
		add_to_the_wifi_plot(time,energy);
		
	}
	private double calculate_the_wifi_energy(double t_trans,int RSSI) {
		// TODO Auto-generated method stub
		double Joule=(0.009 * Math.pow(RSSI, 2) - 0.7 * Math.abs(RSSI) + 14.87 ) * t_trans + 1.76;
		double WiFiWatt=(Joule/t_trans);
		return WiFiWatt;
	}
	public void add_wifi_power_to_receive_finished_cloudlets(int data_rate_of_the_zone,long total_length,int RSSI,double time) {
		double T_trans=total_length/data_rate_of_the_zone;
		double energy=calculate_the_wifi_energy(T_trans,RSSI);
		add_energy_for_wifi_module(energy);
		add_to_the_wifi_plot(time,energy);
	}
	public void add_3g_power(double f,double time,double T_trans) {
		double energy=(20+0.0905*f)*T_trans;
		add_to_the_3g_energy_plot(time,energy);
		energy_for_3g_module+=energy;
	}
	public void add_zero_3g_power(double time) {
		add_to_the_3g_energy_plot(time,0);
		energy_for_3g_module+=0;
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
	public void add_to_the_battery_plot(double time) {
		BATTERY_SERIES.add(time, BATTERY_LIFE);
	}
	public void add_to_the_wifi_plot(double time,double wifi_energy) {
		WIFI_SERIES.add(time, wifi_energy);
	}
	public void add_to_the_cpu_energy_plot_for_desired_host(int index) {
		CPU_SERIES.add(new XYSeries("CPU Energy For Host " + index));
	}
	public void set_the_values_plot_for_cpu_energy_for_desired_host(double time,double energy) {
		CPU_SERIES.get(CPU_SERIES.size()-1).add(time, energy);
	}
	public void add_to_the_3g_energy_plot(double time,double E_energy) {
		E_SERIES.add(time, E_energy);
	}
	public void show_the_plots(double time) {
		int r,g,b;
		 //initializes Plotter
		r=(0+mob_id*10)%255;
		g=(20+mob_id*30)%255;
		b=(40+mob_id*50)%255;
		List<Plotter> windowPlots = new ArrayList<>();
		for(int i=0; i<CPU_SERIES.size(); i++) {
		Plotter cpuWin = new Plotter(String.format("Energy_CPU of the Mobile:%d for Host:%d with total energy consumption %.0f Watt (%.5f KWatt-Hour) and Mean %.4f Watt-Sec",mob_id,i, super.getTotalPowerForTheHost(i),PowerAware.wattsSecToKWattsHour(super.getTotalPowerForTheHost(i)),super.getTotalPowerForTheHost(i)/time), CPU_SERIES.get(i),new Color(r,g,b));
		windowPlots.add(cpuWin);
		}
		Plotter wifiWin = new Plotter(String.format("Energy_WIFI of the Mobile:%d with total energy Sum:%6.2f Watt",mob_id,energy_for_Wifi_module), WIFI_SERIES,new Color(r,g,b));
		Plotter EWin = new Plotter(String.format("Energy_3G of the Mobile:%d with total energy Sum:%6.2f Watt",mob_id,energy_for_3g_module), E_SERIES,new Color(r,g,b));
		Plotter BatteryWin = new Plotter(String.format("The Battery of the Mobile:%d",mob_id), BATTERY_SERIES,new Color(r,g,b));
		List<Double> ΑverageResponseTime_List = get_the_ΑverageResponseTime_List();
		List<Double> Times_List = get_the_Times_List();
		double medianΑverageResponseTime=0;
		for(int i=0; i<ΑverageResponseTime_List.size(); i++) {
			RESPONSE_SERIES.add(Times_List.get(i),ΑverageResponseTime_List.get(i));
			medianΑverageResponseTime+=ΑverageResponseTime_List.get(i);
		}
		medianΑverageResponseTime=medianΑverageResponseTime/ΑverageResponseTime_List.size();
		Plotter averageWin =  new Plotter(String.format("Average Time for Tasks Execution of the Mobile:%d with median average response time:%6.2f Sec-(Executed Cloudlet)",mob_id,medianΑverageResponseTime), RESPONSE_SERIES,new Color(r,g,b));
        windowPlots.add(wifiWin);
        windowPlots.add(EWin);
        windowPlots.add(BatteryWin);
        windowPlots.add(averageWin);
        String name = get_the_path() + "Mobile_"+get_mob_id()+"/";
        File TheInsDir = new File(name);
        if(!TheInsDir.exists()) {
   	 		try {
   	 			TheInsDir.mkdir();
   	 		}
   	 		catch(SecurityException se){
	        //handle it
   	 		} 
        }
        int i=0;
        for (Plotter win : windowPlots) {
            win.pack(); 
            win.setVisible(true);
           //RefineryUtilities.centerFrameOnScreen(win);
            RefineryUtilities.positionFrameOnScreen(win, i*0.1, i*0.1); 
            try {
            	TimeUnit.SECONDS.sleep(1);
				getSaveSnapShot(win, name + "Mobile_" + get_mob_id() +"_" + i + ".png");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       
            i++;
        }      
	}
	public int start_battery() {
		return start_battery;
	}
	public int get_random_length() {
		return ThreadLocalRandom.current().nextInt(3000,5000);
	}
	public int get_random_filesize() {
		return ThreadLocalRandom.current().nextInt(200,300);
	}
	public double get_random_delay() {
		Random r = new Random();
		double val=r.nextDouble()*1000;
		int temp_val= (int) val;
		return  temp_val/1000;	
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
	 public void set_the_path(String s) {
		 path=s;
	 }
	 public String get_the_path() {
		 return path;
	 }
}
