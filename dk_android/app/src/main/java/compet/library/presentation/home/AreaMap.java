package compet.library.presentation.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AreaMap {
	@Expose
	@SerializedName("width")
	public float width;

	@Expose
	@SerializedName("height")
	public float height;

	@Expose
	@SerializedName("vertices")
	public float[][] vertices;
}
