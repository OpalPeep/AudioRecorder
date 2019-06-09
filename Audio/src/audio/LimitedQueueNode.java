package audio;

public class LimitedQueueNode <T> {
	
	T val;
	int lengthToEnd;
	LimitedQueueNode<T> nxt = null;
	LimitedQueueNode(T var){
		val = var;
		lengthToEnd = 0;
	}
	
	LimitedQueueNode(T var, T ... n){
		val = var;
		lengthToEnd = n.length;
		addToEnd(n);
	}
	
	public void addToEnd(T ... vals) {
		if(vals.length == 0)return;
		if(nxt == null)	{
				T[] tem = (T[]) new Object[vals.length - 1];
				for(int I = 0; I < tem.length; I++) {
					tem[I] = vals[I + 1];
				}
				nxt = new LimitedQueueNode<T>(vals[0], tem);
				return;
		}
		else {
			nxt.addToEnd(vals);
		}
		return;
	}
	
	public void makeNext(T val) {
		lengthToEnd++;
		if(nxt != null)nxt.makeNext(val);
		else nxt = new LimitedQueueNode<T>(val);
	}
	
	public LimitedQueueNode<T> getNext() {return nxt;}
	public T getValue() {return val;}
	public int getTailLength() {return lengthToEnd;}
	
}