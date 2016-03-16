package lab;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
	final static int ROW_NUMBERS = 15000000;
	final static int FIXED_LENGTH = 10;
	final static int RANDOM_NUMBER = 50000;
	final static String FILE_NAME1 = "/Users/Yanhan/Downloads/Study/Database/lab/sorted15000000";
	final static String FILE_NAME2 = "/Users/Yanhan/Downloads/Study/Database/lab/unsorted15000000";

	
//	public static void main(String[] args) throws IOException{
//		Random r = new Random();
//		List<BenchMark> ben_lst = new ArrayList<BenchMark>();
//		for(int i = 0; i < ROW_NUMBERS; i++){
//			int id = i;
//			int columnA = r.nextInt(RANDOM_NUMBER);
//			int columnB = r.nextInt(RANDOM_NUMBER);
//			String filler = generateStr();
//			BenchMark bm = new BenchMark(id, columnA, columnB, filler);
//			ben_lst.add(bm);
//		}
//		
//		output(ben_lst, FILE_NAME1);
//		shuffle(ben_lst);
//		output(ben_lst, FILE_NAME2);
//		
//	}
	
	private static void shuffle(List<BenchMark> ben_lst){
		
		Random r = new Random();
		for(int i = 0; i < ben_lst.size(); i++){
			
			int k = r.nextInt(ben_lst.size());
			BenchMark bm1 = ben_lst.get(i);
			BenchMark bm2 = ben_lst.get(k);
			ben_lst.set(k, bm1);
			ben_lst.set(i, bm2);
		}
	}
	
	
	private static void output(List<BenchMark> ben_lst, String filename) throws IOException{
		FileWriter fw = new FileWriter(filename);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(BenchMark bm: ben_lst){
			bw.write(bm.toString());
			bw.newLine();
		}
		bw.flush();
		bw.close();
		fw.close();
		
	}
	
	private static String generateStr(){
		Random r = new Random();
		StringBuilder ret = new StringBuilder();
		for(int i = 0; i < FIXED_LENGTH; i++){
			int x = r.nextInt(26);
			ret.append((char)('a' + x));
		}
		return ret.toString();
	}
	
	static class BenchMark{
		int theKey;
		int columnA;
		int columnB;
		String filler;
		BenchMark(int theKey, int columnA, int columnB, String filler){
			this.theKey = theKey;
			this.columnA = columnA;
			this.columnB = columnB;
			this.filler = filler;
		}
		
		public String toString(){
			return "" + theKey + " " + columnA + " " + columnB + " " + filler;
		}
	}
}
