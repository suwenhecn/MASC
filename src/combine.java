
public class combine {
	public int[] combine_space(int center_len,int[][] Spaceevery,int n) {
		int Space[] = new int[center_len + 1];// 
		int i, j;
		for (i = 0; i < center_len + 1; i++) {
			int max = 0;
			for (j = 0; j < n; j++)
				if (Spaceevery[j][i] > max)
					max = Spaceevery[j][i];
			Space[i] = max;
		}
		return (Space);
	}
}
