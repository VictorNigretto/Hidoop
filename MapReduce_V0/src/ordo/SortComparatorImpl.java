package ordo;

public class SortComparatorImpl implements SortComparator{
	
	public SortComparatorImpl(){}
	
	public int compare(String k1, String k2) {
		return k1.compareTo(k2);
	}

}
