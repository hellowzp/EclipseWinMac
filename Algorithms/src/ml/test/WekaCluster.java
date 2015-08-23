package ml.test;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaCluster {

	public WekaCluster(String filepath, int clusters) {
		try {
			Instances data = DataSource.read(filepath);
			
			SimpleKMeans kMeans = new SimpleKMeans();
			kMeans.setNumClusters(clusters);
			kMeans.buildClusterer(data);
			
			Instances centroids = kMeans.getClusterCentroids();
			for (int i = 0; i < centroids.numInstances(); i++) {
				System.out.println("Centroid: "+ i + ": " + centroids.instance(i));
			}
			
			for (int i = 0; i < data.numInstances(); i++) {
				System.out.println(i + " in cluster" + kMeans.clusterInstance(data.instance(i)));
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new WekaCluster("cpu.with.vendor.arff", 4);
	}

}
