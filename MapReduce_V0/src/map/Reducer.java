package map;

import java.io.Serializable;

import formats.FormatReader;
import formats.FormatWriter;

// Programme reduce pour concatener les résultats des maps
public interface Reducer extends Serializable {
	public void reduce(FormatReader reader, FormatWriter writer);
}
