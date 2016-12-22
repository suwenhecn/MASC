import jaligner.matrix.MatrixLoaderException;
import jaligner.util.SequenceParserException;

import java.io.BufferedReader;
import java.io.FileReader;
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

public class main {
	public main(){
	}
	public static void main(String[] args) throws ClassNotFoundException, SequenceParserException, MatrixLoaderException, IOException, InterruptedException{
		dna_msa dm=new dna_msa();
		String filename=args[0];
		
		dm.run(filename);
		//dm.run("1x.fasta");	
		//dm.run("16s_small.fasta");
		//protein_msa pm=new protein_msa();
		//pm.start("protein.fasta", "protein_out");
		//pm.run("protein.fasta", "protein_out_spark");
	} 
}
