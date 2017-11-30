package map;

import java.io.Serializable;

import formats.FormatReader;
import formats.FormatWriter;


// Programme map à appliquer sur un fragment
public interface Mapper extends Serializable {
	public void map(FormatReader reader, FormatWriter writer);
}
