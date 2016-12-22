/*
public class output {

}
package multiple_sequence_alignment;
*/
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class output {
	public output(){
		
	}
	/*函数功能：计算比对后的序列，并将序列输出
	 * 输入：DNA序列，DNA名字数组，DNA序列数组规模，中心序列，中心序列长度，中心序列空格信息表，
	 * 				中心序列空格信息大表，其他序列空格信息大表
	 * 输出：无输出
	 * 
	 * */
	public String get_re_center(String center,int center_len,int[] Space){
		int i, j;
		//long t1 = System.currentTimeMillis();
		// ---------输出中心序列----------
		//String PiAlign[] = new String[n];
		String center_re=new String();
		center_re="";
		//PiAlign[0] = "";
		for (i = 0; i < center_len; i++) {
			for (j = 0; j < Space[i]; j++)
				center_re=center_re.concat("-");
				//PiAlign[0] = PiAlign[0].concat("-");
			//PiAlign[0] = PiAlign[0].concat(center.substring(i, i + 1));
			center_re=center_re.concat(center.substring(i,i+1));
		}
		for (j = 0; j < Space[center.length()]; j++)
			center_re=center_re.concat("-");
			//PiAlign[0] = PiAlign[0].concat("-");
		// --------中心序列输出完毕------
		return center_re;
	}
	public String[] get_all_sequeces(String center_re,String[] Pi,int n,String center,int center_len,int[] Space,int[][] Spaceevery,int[][]Spaceother){
		int i,j;
		String PiAlign[] = new String[n];
		PiAlign[0]=center_re;
		//long t11 = System.currentTimeMillis();
		for (i = 0; i < n; i++) {
			if (i == 0)
				continue;
			// ----计算和中心序列比对后的P[i],记为Pi-----
			PiAlign[i] = "";
			for (j = 0; j < Pi[i].length(); j++) {
				String kong = "";
				for (int k = 0; k < Spaceother[i][j]; k++)
					kong = kong.concat("-");
				PiAlign[i] = PiAlign[i].concat(kong).concat(Pi[i].substring(j, j + 1));
			}
			String kong = "";
			for (j = 0; j < Spaceother[i][Pi[i].length()]; j++)
				kong = kong.concat("-");
			PiAlign[i] = PiAlign[i].concat(kong);
			// ---Pi计算结束---------
			// ----计算差异数组----
			int Cha[] = new int[center.length() + 1];
			int position = 0; // 用来记录插入差异空格的位置
			for (j = 0; j < center.length() + 1; j++) {
				Cha[j] = 0;
				if (Space[j] - Spaceevery[i][j] > 0)
					Cha[j] = Space[j] - Spaceevery[i][j];
				// ----差异数组计算完毕---
				// ----填入差异空格----
				position = position + Spaceevery[i][j];
				if (Cha[j] > 0) { // 在位置position处插入Cha[j]个空格
					kong = "";
					for (int k = 0; k < Cha[j]; k++)
						kong = kong.concat("-");
					PiAlign[i] = PiAlign[i].substring(0, position).concat(kong).concat(PiAlign[i].substring(position));
				}
				position = position + Cha[j] + 1;
				// ----差异空格填入完毕--
			}
		}
		//long t22 = System.currentTimeMillis();
		//System.out.print("output:: ");
		//System.out.print(t22-t11);
		//System.out.print("ms to get other sequeces!\n");
		return PiAlign;
	}
	//use spark to parallelized the get_all_sequences
	public void output_to_file(String[] PiAlign,String[] Piname,int n){
		int i,j;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
			for (i = 0; i < n; i++) {
				bw.write(Piname[i]);
				bw.newLine();
				bw.flush();
				bw.write(PiAlign[i]);
				bw.newLine();
				bw.flush();
			}
			bw.close();
		} catch (Exception ex) {
		}
	}
	public void output(String[] Pi,String[] Piname,int n,String center,int center_len,int[] Space,int[][] Spaceevery,int[][]Spaceother) {
		int i, j;
		long t1 = System.currentTimeMillis();
		// ---------输出中心序列----------
		String PiAlign[] = new String[n];
		PiAlign[0] = "";
		for (i = 0; i < center_len; i++) {
			for (j = 0; j < Space[i]; j++)
				PiAlign[0] = PiAlign[0].concat("-");
			PiAlign[0] = PiAlign[0].concat(center.substring(i, i + 1));
		}
		for (j = 0; j < Space[center.length()]; j++)
			PiAlign[0] = PiAlign[0].concat("-");
		// --------中心序列输出完毕------
		// ---------输出其他序列-------
		long t2 = System.currentTimeMillis();
		System.out.print("output:: ");
		System.out.print(t2-t1);
		System.out.print("ms to get center!\n");
		
		long t11 = System.currentTimeMillis();
		for (i = 0; i < n; i++) {
			if (i == 0)
				continue;
			// ----计算和中心序列比对后的P[i],记为Pi-----
			PiAlign[i] = "";
			for (j = 0; j < Pi[i].length(); j++) {
				String kong = "";
				for (int k = 0; k < Spaceother[i][j]; k++)
					kong = kong.concat("-");
				PiAlign[i] = PiAlign[i].concat(kong).concat(Pi[i].substring(j, j + 1));
			}
			String kong = "";
			for (j = 0; j < Spaceother[i][Pi[i].length()]; j++)
				kong = kong.concat("-");
			PiAlign[i] = PiAlign[i].concat(kong);
			// ---Pi计算结束---------
			// ----计算差异数组----
			int Cha[] = new int[center.length() + 1];
			int position = 0; // 用来记录插入差异空格的位置
			for (j = 0; j < center.length() + 1; j++) {
				Cha[j] = 0;
				if (Space[j] - Spaceevery[i][j] > 0)
					Cha[j] = Space[j] - Spaceevery[i][j];
				// ----差异数组计算完毕---
				// ----填入差异空格----
				position = position + Spaceevery[i][j];
				if (Cha[j] > 0) { // 在位置position处插入Cha[j]个空格
					kong = "";
					for (int k = 0; k < Cha[j]; k++)
						kong = kong.concat("-");
					PiAlign[i] = PiAlign[i].substring(0, position).concat(kong).concat(PiAlign[i].substring(position));
				}
				position = position + Cha[j] + 1;
				// ----差异空格填入完毕--
			}
		}
		long t22 = System.currentTimeMillis();
		System.out.print("output:: ");
		System.out.print(t22-t11);
		System.out.print("ms to get other sequeces!\n");
		
		long t111 = System.currentTimeMillis();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
			for (i = 0; i < n; i++) {
				bw.write(Piname[i]);
				bw.newLine();
				bw.flush();
				bw.write(PiAlign[i]);
				bw.newLine();
				bw.flush();
			}
			bw.close();
		} catch (Exception ex) {
		}
		long t222 = System.currentTimeMillis();
		System.out.print("output:: ");
		System.out.print(t222-t111);
		System.out.print("ms to output all sequeces!\n");
		
	}
}
