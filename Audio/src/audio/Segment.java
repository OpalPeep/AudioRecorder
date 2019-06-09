package audio;

public class Segment {
	byte[] value;
	byte[] origValue;
	double avg;
	double potentialAvg;
	Segment(byte[] data){
		origValue = data;
		value = data;
		computeAvrg();
	}
	
	public void computeAvrg() {
		long cnt = 0;
		long holder = 0;
		for(byte b : value) {
			if(b == 0)continue;
			cnt++;
			holder += b;
		}
		avg = (double)holder / (double)cnt;
		potentialAvg = avg;
	}
	
	public double getVal() {return avg;}
	public double getPotenVal() {return potentialAvg;}
	public void setPotenVal(double d) {potentialAvg = d;}
	public byte[] getFull() {return value;}
	public byte[] getOrigFull() {return origValue;}
	public void addToAll(byte b) {
		for(int I = 0; I < value.length; I++) {
			value[I] += b;
		}
		computeAvrg();
	}
	
}
