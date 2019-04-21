package org.cloudbus.cloudsim.plotter;

/**
 * Created by Latzwn on 29/03/2019.
 */


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;

public class Plotter extends ApplicationFrame {

    public Plotter(final String title, final XYSeries seriesOfInterest, Color color) {
        super(title);
        XYPlot plot;
        final XYSeriesCollection data = new XYSeriesCollection(seriesOfInterest);
        final JFreeChart chart = ChartFactory.createXYLineChart(
            title,
            "Seconds",
            "",
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        plot = (XYPlot)chart.getPlot();
        plot.getRenderer().setSeriesPaint(0, color);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
    }
    
    public Plotter(XYDataset y,double total_response_mobi_het,double total_response_random) {
    	  super(String.format("Compare Mobi-Het with total Response Time: %.4f and Random with total Response Time: %.4f", total_response_mobi_het,total_response_random));
    	  final JFreeChart chart = ChartFactory.createXYLineChart(String.format("Compare Mobi-Het with total Response Time: %.4f and Random with total Response Time: %.4f", total_response_mobi_het,total_response_random), "Time", "Response_Time", y, PlotOrientation.VERTICAL, true, true, false);
    	  XYPlot plot;
    	  plot = (XYPlot)chart.getPlot();
          plot.getRenderer().setSeriesPaint(0, Color.BLACK);
          plot.getRenderer().setSeriesPaint(1, Color.RED);
          final ChartPanel chartPanel = new ChartPanel(chart);
          chartPanel.setPreferredSize(new Dimension(500, 270));
          setContentPane(chartPanel);
   }
    
    public Plotter(double total_service_mobi_het,XYDataset y,double total_service_random) {
  	  super(String.format("Compare Mobi-Het with total Service: %.4f and Random with total Service: %.4f", total_service_mobi_het,total_service_random));
  	  final JFreeChart chart = ChartFactory.createXYLineChart(String.format("Compare Mobi-Het with total Service: %.4f and Random with total Service: %.4f", total_service_mobi_het,total_service_random), "Time", "Sevice", y, PlotOrientation.VERTICAL, true, true, false);
  	  XYPlot plot;
  	  plot = (XYPlot)chart.getPlot();
        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
        plot.getRenderer().setSeriesPaint(1, Color.GREEN);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
 }
}
