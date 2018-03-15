package org.interview.big.data.storm;

public class APP {

	public static void main(String[] args) {
		
		Topology topology = new Topology("D:\\data\\txt\\storm", "\t", 5, 600);
		topology.submit();
	}

}
