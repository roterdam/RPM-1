package ravensproject;

// Uncomment these lines to access image processing.
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures: public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
	/**
	 * The default constructor for your Agent. Make sure to execute any
	 * processing necessary before your Agent starts solving problems here.
	 * 
	 * Do not add any variables to this signature; they will not be used by
	 * main().
	 * 
	 */
	public Agent() {

	}

	/**
	 * The primary method for solving incoming Raven's Progressive Matrices. For
	 * each problem, your Agent's Solve() method will be called. At the
	 * conclusion of Solve(), your Agent should return a String representing its
	 * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
	 * are also the Names of the individual RavensFigures, obtained through
	 * RavensFigure.getName().
	 * 
	 * In addition to returning your answer at the end of the method, your Agent
	 * may also call problem.checkAnswer(String givenAnswer). The parameter
	 * passed to checkAnswer should be your Agent's current guess for the
	 * problem; checkAnswer will return the correct answer to the problem. This
	 * allows your Agent to check its answer. Note, however, that after your
	 * agent has called checkAnswer, it will *not* be able to change its answer.
	 * checkAnswer is used to allow your Agent to learn from its incorrect
	 * answers; however, your Agent cannot change the answer to a question it
	 * has already answered.
	 * 
	 * If your Agent calls checkAnswer during execution of Solve, the answer it
	 * returns will be ignored; otherwise, the answer returned at the end of
	 * Solve will be taken as your Agent's answer to this problem.
	 * 
	 * @param problem
	 *            the RavensProblem your agent should solve
	 * @return your Agent's answer to this problem
	 */
	public int Solve(RavensProblem problem) {
		System.out.println(problem.getName());
		String correctAnswer = String.valueOf(problem.correctAnswer);
		HashMap<String, FigureFrame> figureFrames = new HashMap<String, FigureFrame>();
		HashMap<String, Double> finalVotes = new HashMap<String, Double>();
		
		// 2x2 problems
		if (problem.getProblemType().equals("2x2")) {
			String[] figureNames = { "A", "B", "C", "1", "2", "3", "4", "5", "6" };
			String[] answerNames = { "1", "2", "3", "4", "5", "6" };
			for(String answerName : answerNames) {
				finalVotes.put(answerName, 0.0);
			}
			
			// Has verbal
			if (problem.hasVerbal()) {
				for (String figureName : figureNames) {
					// if (figureName.equalsIgnoreCase(correctAnswer)) {
					// System.out.println(figureName + " - Correct");
					// } else {
					// System.out.println(figureName);
					// }

					FigureFrame frame = new FigureFrame();
					RavensFigure thisFigure = problem.getFigures().get(figureName);

					try {
						frame.setRatioBlack(blackRatio(thisFigure));
					} catch (IOException e) {
					}

					// System.out.println("Ratio: " + frame.getRatioBlack());
					// printObjects(thisFigure);
					// printBlackRatio(thisFigure);

					figureFrames.put(figureName, frame);
				}

				voteABBlackRatio(problem, correctAnswer, figureFrames, answerNames);

			} else {
				System.out.println("Visual only");
			}
		}

		return -1;
	}

	private void voteABBlackRatio(RavensProblem problem, String correctAnswer, HashMap<String, FigureFrame> figureFrames, String[] answerNames) {
		double min = 999;
		double ABratio = figureFrames.get("A").getRatioBlack() / figureFrames.get("B").getRatioBlack();
		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes
		for (String answerName : answerNames) {
			double ratio = figureFrames.get("C").getRatioBlack() / figureFrames.get(answerName).getRatioBlack();
			votes.put(answerName, Math.abs(ABratio - ratio));
			normal += ratio;
		}

		// Normalize votes
		for (String answerName : answerNames) {
			votes.put(answerName, votes.get(answerName) / normal);
			prob
			if (votes.get(answerName) < min) {
				min = votes.get(answerName);
				vote = answerName;
			}
		}

		// Debugging
//		for (String answerName : answerNames) {
//			if (answerName.equalsIgnoreCase(correctAnswer)) {
//				System.out.println(answerName + " - Correct");
//			} else {
//				System.out.println(answerName);
//			}
//
//			System.out.println(votes.get(answerName));
//		}
//		int correct = problem.checkAnswer(Integer.valueOf(vote));
//		System.out.println("Correct: " + correct + " Voted: " + vote);
	}

	private void printBlackRatio(RavensFigure thisFigure) {
		try { // Required by Java for ImageIO.read
			double ratioBlack = blackRatio(thisFigure);
			System.out.println("Ratio: " + ratioBlack);
		} catch (Exception ex) {
		}
	}

	private double blackRatio(RavensFigure thisFigure) throws IOException {
		int black = 0;
		int total = 0;
		BufferedImage figureImage = ImageIO.read(new File(thisFigure.getVisual()));
		for (int i = 0; i < figureImage.getWidth(); i++) {
			for (int j = 0; j < figureImage.getHeight(); j++) {
				black += isBlack(figureImage, i, j);
				total++;
			}
		}
		double ratioBlack = (double) black / total;
		return ratioBlack;
	}

	private int isBlack(BufferedImage image, int x, int y) {
		if (image.getRGB(x, y) == -1) {
			return 0;
		} else {
			return 1;
		}
	}

	private void printObjects(RavensFigure figure) {
		for (String objectName : figure.getObjects().keySet()) {
			System.out.println("  " + objectName);
			RavensObject thisObject = figure.getObjects().get(objectName);
			for (String attributeName : thisObject.getAttributes().keySet()) {
				String attributeValue = thisObject.getAttributes().get(attributeName);
				System.out.println("    " + attributeName + ": " + attributeValue);

			}
		}
	}
}
