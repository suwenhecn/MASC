/*
public class fcs {

}
*/
//package multiple_sequence_alignment;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;


//fcs是find_common_substring的缩写
//这里的fcs类只比较中心序列构造的树和一条序列
public class fcs {
	SuffixTree center_tree;
	String si;
	public fcs(SuffixTree c,String i){
		center_tree=c;
		si=i;
	}
	public int[][] find_common_substrings(){
		int index = 0;
		int totalmatch = 0;
		String seq=si;
		SuffixTree st1=center_tree;
		ArrayList<Integer> result = new ArrayList();
		while (index <seq.length()){
			int[] a = st1.selectPrefixForAlignment(seq, index);
			if (a[1] > Math.abs(a[0] - index)) {
				result.add(a[0]);
				result.add(index);
				result.add(a[1]);
				index += a[1];
				totalmatch += a[1];
			} else if (a[1] > 0)
				index += a[1];
			else
				index++;
		}
		int[][] tmp = new int[3][result.size() / 3];
		int k = 0;
		while (k < result.size()) {
			tmp[0][k / 3] = result.get(k);
			k++;
			tmp[1][k / 3] = result.get(k);
			k++;
			tmp[2][k / 3] = result.get(k);
			k++;
		}
		return tmp;
	}
}