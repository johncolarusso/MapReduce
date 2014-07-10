package org.myorg;

import java.io.IOException;
import java.util.*;
	
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class FindTriangle {
	/*
	First MapReduce Job: Generate all possible length 2 paths in the graph.
	First Mapper: (Identity - input equals output)

	Input: <Text x, Text y>
	Output: <Int x, Int y> 

	*/
	public static class Map extends MapReduceBase implements Mapper<Text, Text, IntWritable, IntWritable> 		{
		private IntWritable node1 = new IntWritable();
		private IntWritable node2 = new IntWritable();
		public void map(Text key, Text value, OutputCollector<IntWritable, IntWritable> output, Reporter reporter) throws IOException {
			node1.set(Integer.parseInt(key.toString()));
			node2.set(Integer.parseInt(value.toString()));
			
			output.collect(node1, node2);			
		}
    }
	/*
	First Reducer:

	Input: <x, Z = neighbors of x>

	Output:

	Forall y in Z:
		output <(x, y), "-1">		The representation of each edge of the original graph
	Forall (m, n) in the cross-product of Z and Z:
		if m < n: (to remove possible duplicate edges)
			output <(m, n), x>		The representation of the two edges that need to exist in order for a triangle to form, given the edge of the original graph
	
	*/
	public static class Reduce extends MapReduceBase implements Reducer<IntWritable, IntWritable, Text, IntWritable> {
		private Text keyText = new Text();
		private IntWritable negativeOne = new IntWritable(-1);
		public void reduce(IntWritable key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
			ArrayList<IntWritable> valueList = new ArrayList<IntWritable>();

			// Output all edges with largeInt
			while (values.hasNext()) {
				IntWritable value = values.next();
				valueList.add(new IntWritable(value.get()));
				keyText.set(key.get() + ", " + value.get());
				output.collect(keyText, negativeOne);
			}

			// Do Cross Product for length 2 pairs
			for(int i = 0; i < valueList.size(); i++) {
				for(int j = 0; j < valueList.size(); j++) {
					if( valueList.get(i).get() < valueList.get(j).get()) {
						output.collect(new Text(valueList.get(i).get() +
							", " + valueList.get(j).get()), key);
					}
				}
			}
		}
    }
		
	/*
	Second MapReduce Job: See which length 2 paths can be "closed" by an edge of the original graph.

	Second Mapper: (Identity) 
	
	Input: <Text, Text> 
	Output: <Text, Int>	
	*/
	public static class Map2 extends MapReduceBase implements Mapper<Text, Text, Text, IntWritable> {
		private IntWritable node2 = new IntWritable();
		public void map(Text key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
			node2.set(Integer.parseInt(value.toString()));
			output.collect(key, node2);			
			}
    	}
	/*
	Second Reducer:

	Input: <(x, y), (Z = intermediate nodes necessary to get from x to y)>

	Output:
	Initialize ArrayList L
	For each m in Z
		Add m to L
	If L contains -1 (If "-1" is an element of Z):
		For each element e in L
			Output <"e, x, y" , null>
	*/
	public static class Reduce2 extends MapReduceBase implements Reducer<Text, IntWritable, Text, Text> {
		private Text keyText = new Text();
		private Text valueText = new Text();
		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			valueText.set("");
			ArrayList<IntWritable> valueList = new ArrayList<IntWritable>();
			boolean foundNegOne = false;
			while (values.hasNext()) {
				IntWritable value = values.next();
				valueList.add(new IntWritable(value.get()));
			}
			for(int i = 0; i < valueList.size(); i++) {
				if( valueList.get(i).get() == -1 ) {
					foundNegOne = true;
				}
			}
			if(foundNegOne) {
				for(int i = 0; i < valueList.size(); i++) {
					if(valueList.get(i).get() != -1) {
						keyText.set(valueList.get(i).get() + ", " + key.toString());
						output.collect(keyText, valueText);
					}
				}
			}
		}
    }
    public static void main(String[] args) throws Exception {
      JobConf conf = new JobConf(FindTriangle.class);
      conf.setJobName("findtriangle");

      conf.setOutputKeyClass(Text.class);
      conf.setOutputValueClass(IntWritable.class);

	conf.setMapOutputKeyClass(IntWritable.class);
	conf.setMapOutputValueClass(IntWritable.class);

      conf.setMapperClass(Map.class);

      conf.setReducerClass(Reduce.class);

      conf.setInputFormat(KeyValueTextInputFormat.class);
      conf.setOutputFormat(TextOutputFormat.class);

      FileInputFormat.setInputPaths(conf, new Path(args[0]));
      FileOutputFormat.setOutputPath(conf, new Path(args[1]));

      JobClient.runJob(conf);

      JobConf conf2 = new JobConf(FindTriangle.class);
      conf2.setJobName("findtriangle2");

      conf2.setOutputKeyClass(Text.class);
      conf2.setOutputValueClass(IntWritable.class);

	conf2.setMapOutputKeyClass(Text.class);
	conf2.setMapOutputValueClass(IntWritable.class);

      conf2.setMapperClass(Map2.class);

      conf2.setReducerClass(Reduce2.class);

      conf2.setInputFormat(KeyValueTextInputFormat.class);
      conf2.setOutputFormat(TextOutputFormat.class);

      FileInputFormat.setInputPaths(conf2, new Path(args[1]));
      FileOutputFormat.setOutputPath(conf2, new Path(args[2]));

      JobClient.runJob(conf2);
	
    }
}
