package ravensproject;

import java.util.HashMap;

public class ProblemFrame {
	private HashMap<String, Double> votes;

	public ProblemFrame(String[] figures) {
		super();
		for (String figure : figures) {
			votes.put(figure,0.0);
		}
		
	}
}
