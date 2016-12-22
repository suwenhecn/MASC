import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.apache.spark.broadcast.Broadcast;

public class dna_msa {

	public dna_msa(){}
	public static void run(String filename){
		long t1 = System.currentTimeMillis();
		//when running on an ide for development or test your can modify running model here
		//SparkConf conf = new SparkConf().setAppName("msa-spark").setMaster("local[32]").set("spark.driver.maxResultSize","8g");
		//when you running with command line your can modify whatever mode your want with the following statement 
		SparkConf conf = new SparkConf().setAppName("msa-spark").set("spark.driver.maxResultSize","8g");
		JavaSparkContext sc = new JavaSparkContext(conf);
		input in=new input(filename);
		in.read_file();
		
		String[] pi_name=in.get_pi_name();
		String[] pi=in.get_pi();
		final String center=pi[0];
		int max_length=in.get_max_length();
		int num=in.get_n();
		SuffixTree st1=new SuffixTree();
		st1.build(center+"$");
		List<int[][]> name_list=new ArrayList<int[][]>();
	
		for(int i=0;i<num;i++){
			String tmp2=pi[i];
			fcs find=new fcs(st1,tmp2);
			int[][] name=find.find_common_substrings();
			name_list.add(name);
		}
	
		//release some memory
		st1=null;	
		
		int[] max_length_array=new int[1];
		max_length_array[0]=max_length;
		final Broadcast<String> center_b = sc.broadcast(center);
		final Broadcast<int[]> max_length_array_b = sc.broadcast(max_length_array);

		JavaRDD<String> pi_rdd=sc.parallelize(Arrays.asList(pi));
		pi_rdd.cache();
		JavaRDD<int[][]> name_list_rdd=sc.parallelize(name_list);
		//release memory
		pi=null;
		name_list=null;		
		JavaPairRDD<String,int[][]> step2=pi_rdd.zip(name_list_rdd);
	
		JavaPairRDD<int[],int[]> step3_1=step2.mapToPair(
				new PairFunction< Tuple2<String,int[][]>,int[],int[]>(){
					public Tuple2<int[],int[]> call(Tuple2<String,int[][]> a){
						int max=max_length_array_b.value()[0];
						pair_wise_alignment pw=new pair_wise_alignment(center_b.value(),a._1(),a._2(),max);
						pw.pwa();
						int[] se=pw.get_spaceevery();
						int[] so=pw.get_spaceother();
						return new Tuple2( se, so );
					}
				}
		);
		
		step3_1.cache();
		JavaRDD<int[]> box1_rdd=step3_1.keys();
	
		List<int[]> box1_list=box1_rdd.collect();
	
		int[] [] box1 = (int[] [])box1_list.toArray(new int[box1_list.size()] []);
	
		box1_rdd=null;
		//box2_rdd=null;
		box1_list=null;
		//box2_list=null;
		combine cb=new combine();
		int[] space=cb.combine_space(center.length(), box1, num);
		box1=null;
		
		output out=new output();
		
		String center_re=out.get_re_center(center, center.length(), space);
	
		final Broadcast<String> center_re_b = sc.broadcast(center_re);
		final Broadcast<int[]> space_b = sc.broadcast(space);
		int[] num_array0=new int[1];
		num_array0[0]=num;
		final Broadcast<int[]> num_array0_b = sc.broadcast(num_array0);
		//pi + box1+box2
		JavaPairRDD<String,Tuple2<int[],int[]> > step4=pi_rdd.zip(step3_1);
		//pi_align
		//long t111111 = System.currentTimeMillis();
		JavaRDD<String> step5=step4.map(
				new Function<Tuple2<String,Tuple2<int[],int[]> >,String>(){
					public String call(Tuple2<String,Tuple2<int[],int[]> > t){
						String pi=t._1().trim();
						int[] spaceevery=t._2()._1();
						int[] spaceother=t._2()._2();
						get_every_sequences ges=new get_every_sequences();
						String tmp=ges.get_every_sequeces(center_re_b.value().trim(), pi, num_array0_b.value()[0], center_b.value().trim(), center_b.value().trim().length(), space_b.value(), spaceevery, spaceother);
						return tmp;
					}
				}		
		);
	
		
		List<String> pi_re_list=step5.collect();
		String[] pi_re=(String[]) pi_re_list.toArray(new String[pi_re_list.size()]);
		pi_re_list=null;
		pi_re[0]=center_re;
	
		out.output_to_file(pi_re, pi_name, num);
		long t2222222 = System.currentTimeMillis();
	
		System.out.print("output::");
		System.out.print(t2222222-t1);
		System.out.print("ms all time long!\n");
		
		sc.stop();
	
	}
}
