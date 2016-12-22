import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

public class protein_msa {

	public protein_msa(){}
	public static void run(String filename,String outputfile){
		long t1 = System.currentTimeMillis();
		//SparkConf conf = new SparkConf().setAppName("msa-spark").setMaster("local[32]").set("spark.driver.maxResultSize","8g");
		SparkConf conf = new SparkConf().setAppName("msa-spark").set("spark.driver.maxResultSize","8g");
		JavaSparkContext sc = new JavaSparkContext(conf);
		input_protein in=new input_protein();
		in.read("protein.fasta");
		ArrayList<String> key=in.key();
		ArrayList<String> value=in.value();
		
		final String center=value.get(0);
		final int center_len=center.length();
		
		int num=key.size();
		ArrayList<String> center_ga=new ArrayList<String>();
		for(int i=0;i<num;i++){
			center_ga.add(center);
		}
		JavaRDD<String> pi_rdd=sc.parallelize(value);
		JavaRDD<String> center_rdd=sc.parallelize(center_ga);
		JavaPairRDD<String,String> step2=pi_rdd.zip(center_rdd);
		//release
		center_ga=null;
		value=null;
		JavaPairRDD<String,String> step3=step2.mapToPair(
				new PairFunction< Tuple2<String,String>,String,String>(){
					public Tuple2<String,String> call(Tuple2<String,String> a){
						protein_pwa pp=new protein_pwa();
						pp.align(a._1(), a._2());
						return new Tuple2(new String(pp.alignResultOne),new String(pp.aligntResultTwo));
					}
				}
		);
		step3.cache();
		int sequenceLen1=center_len;
		List<String> s_out1=step3.values().collect();
		
		//get aligned center
		int index;
        int insertSpace1[] = new int[sequenceLen1 + 1];
        for (String line2 : s_out1) {
            int tempSpace1[] = new int[sequenceLen1 + 1];
            index = 0;
            for (int j=0; j < line2.length(); j++) {
                if (line2.charAt(j) == '-') {
                    tempSpace1[index]++;
                } else {
                    if (insertSpace1[index] < tempSpace1[index]) {
                        insertSpace1[index]=tempSpace1[index];
                    }
                    index++;
                }
            }
        }
        //build center string
        String sequence1=center;
        //System.out.println(insertSpace1);
        StringBuilder stringBuilder = new StringBuilder();
        int insertSpaceLen1 = insertSpace1.length;
        for(int i=0; i<insertSpaceLen1; i++){
            for(int j=0; j<insertSpace1[i]; j++) {
                stringBuilder.append('-');
            }
            if(i != insertSpaceLen1-1) {
                stringBuilder.append(sequence1.charAt(i));
            }
        }
        sequence1 = stringBuilder.toString();
        ArrayList<String> aligned_center_list=new ArrayList<String>();
        for (int i=0;i<key.size();i++){
        	aligned_center_list.add(sequence1);
        }
        JavaRDD<String> aligned_center_rdd=sc.parallelize(aligned_center_list);
        JavaRDD<String> pi2_rdd=step3.keys();
        JavaPairRDD<String,String> step4=pi2_rdd.zip(aligned_center_rdd);
        //aligned for the second time
        JavaPairRDD<String,String> step5=step4.mapToPair(
				new PairFunction< Tuple2<String,String>,String,String>(){
					public Tuple2<String,String> call(Tuple2<String,String> a){
						protein_pwa pp=new protein_pwa();
						pp.align(a._1(), a._2());
						return new Tuple2(new String(pp.alignResultOne),new String(pp.aligntResultTwo));
					}
				}
		);
        List<String> s_out2=step5.keys().collect();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile));
            //protein_pwa centerAlign=new protein_pwa();
            for (int i=0; i<key.size(); i++) {
                bw.write(key.get(i));
                bw.newLine();
                if (i == 0) {
                    bw.write(sequence1);
                } else {
                    //centerAlign.align(sequence1, s_out2.get(i-1));
                    //bw.write(new String(protein_pwa.aligntResultTwo));
                	bw.write(s_out2.get(i));
                }
                bw.newLine();
                bw.flush();
            }
            bw.close();
            sc.stop();
            long t2 = System.currentTimeMillis();
            System.out.print("output::all time long is ");
            System.out.print(t2-t1);
            System.out.print(" ms!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
