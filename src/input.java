/*
public class input {

}
package multiple_sequence_alignment;
*/
/*this is a spark version*/
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.spark.api.java.JavaSparkContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;

public class input {
	String filepath="";
	int n;//序列个数
	String Pi[]; // 记录每一个序列
	String Piname[]; // 记录每一个序列的名字
	//JavaSparkContext sc;
	//JavaRDD<String> pi_rdd;
	//JavaRDD<String> piname_rdd;
	int max_length;
	/*====================================================*/
	public int get_n(){
		return n;
	}
	public String[] get_pi(){
		return Pi;
	}
	public String[] get_pi_name(){
		return Piname;
	}
	/*
	public JavaRDD<String> get_pi_rdd(){
		return pi_rdd;
	}
	public JavaRDD<String> get_piname_rdd(){
		return piname_rdd;
	}
	*/
	public int get_max_length(){
		return max_length;
	}
	/*====================================================*/
	/*
	public input(String f,JavaSparkContext s){
		filepath=f;
		sc=s;
	}
	*/
	public input(String f){
		filepath=f;
	}
	public int countnum() {
		int num = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String s;
			while (br.ready()) {
				s = br.readLine();
				if (s.charAt(0) == '>')
					num++;
			}
			br.close();
		} catch (Exception ex) {
		}
		return (num);
	}
	public String format(String s) {
		s = s.toLowerCase();
		s = s.replace('u', 't');
		StringBuffer sb = new StringBuffer(s);

		for (int i = 0; i < sb.length(); i++) {
			switch (sb.charAt(i)) {
			case 'a':
				break;
			case 'c':
				break;
			case 'g':
				break;
			case 't':
				break;
			case 'n':
				break;
			default:
				sb = sb.replace(i, i + 1, "n");
			}
		}
		return (sb.toString());
	}
	/*
	public void read_file() {
		n=countnum();
		Pi = new String[n];
		Piname = new String[n];
		int i = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));

			String BR = br.readLine();
			while (br.ready()) {

				if (BR.length() != 0 && BR.charAt(0) == '>') {
					Piname[i] = BR;
					Pi[i] = "";
					while (br.ready() && (BR = br.readLine()).charAt(0) != '>') {
						Pi[i] += BR;
					}
					Pi[i] = format(Pi[i]);
					i++;
				} else
					BR = br.readLine();
			}

			br.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.exit(0);
		}
		pi_rdd=sc.parallelize(Arrays.asList(Pi));
		piname_rdd=sc.parallelize(Arrays.asList(Piname));
		max_length=computeMaxLength(0);
	}
	*/
	public void read_file(){
		n=countnum();
		Pi = new String[n];
		Piname = new String[n];
		int i = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));

			String BR = br.readLine();
			while (br.ready()) {

				if (BR.length() != 0 && BR.charAt(0) == '>') {
					Piname[i] = BR;
					Pi[i] = "";
					while (br.ready() && (BR = br.readLine()).charAt(0) != '>') {
						Pi[i] += BR;
					}
					Pi[i] = format(Pi[i]);
					i++;
				} else
					BR = br.readLine();
			}

			br.close();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.exit(0);
		}
		max_length=computeMaxLength(0);
	}
	public int computeMaxLength(int center) {
		int maxlength = 0;
		for (int i = 0; i < n; i++) {
			if (i == center)
				continue;
			if (Pi[i].length() > maxlength)
				maxlength = Pi[i].length();
		}
		return (maxlength);
	}
}