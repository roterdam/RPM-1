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

		boolean debug = true;

		HashMap<String, FigureFrame> figureFrames = new HashMap<String, FigureFrame>();
		HashMap<String, Double> finalVotes = new HashMap<String, Double>();

		// 2x2 problems
		if (problem.getProblemType().equals("2x2")) {
			String[] figureNames = { "A", "B", "C", "1", "2", "3", "4", "5", "6" };
			String[] answerNames = { "1", "2", "3", "4", "5", "6" };
			for (String answerName : answerNames) {
				finalVotes.put(answerName, 0.0);
			}

			// Visual first pass
			calculateRatios(problem, false, figureFrames, figureNames);

			// voteABRatio(problem, figureFrames, answerNames, false,
			// finalVotes);
			// voteACRatio(problem, figureFrames, answerNames, false,
			// finalVotes);
			voteABDiff(problem, figureFrames, answerNames, false, finalVotes);
			// voteACDiff(problem, figureFrames, answerNames, false,
			// finalVotes);
			voteACSym(problem, answerNames, false, finalVotes);
			voteABMatch(problem, answerNames, false, finalVotes);

			aggregateVotes(problem, true, finalVotes, answerNames);

			// Has verbal
			if (problem.hasVerbal()) {

			}
		} else if (problem.getProblemType().equals("3x3")) {
			String[] figureNames = { "A", "B", "C", "D", "E", "F", "G", "H", "1", "2", "3", "4", "5", "6", "7", "8" };
			String[] answerNames = { "1", "2", "3", "4", "5", "6", "7", "8" };
			for (String answerName : answerNames) {
				finalVotes.put(answerName, 0.0);
			}

			// Visual first pass
			calculateRatios(problem, false, figureFrames, figureNames);

			voteABCRatio(problem, figureFrames, answerNames, false, finalVotes);
			voteABCDiff(problem, figureFrames, answerNames, false, finalVotes);
			voteABCMatch(problem, answerNames, false, finalVotes);
			voteAGSym(problem, answerNames, false, finalVotes);
			voteACHTrans(problem, answerNames, true, finalVotes);
			aggregateVotes(problem, true, finalVotes, answerNames);
		}

		return -1;
	}

	private void voteACHTrans(RavensProblem problem, String[] answerNames, boolean debug, HashMap<String, Double> finalVotes) {
		double min = 999;
		double ACmatch = compareFigureHTrans(problem, "A", "C");
		double DFmatch = compareFigureHTrans(problem, "D", "F");

		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";

		// Place vote
		for (String answerName : answerNames) {
			if (ACmatch > 0.75 && DFmatch > 0.75 && compareFigureHTrans(problem, "G", answerName) > 0.75) {
				votes.put(answerName, compareFigureHTrans(problem, "G", answerName));

			} else {
				votes.put(answerName, compareFigureHTrans(problem, "G", answerName));
				 finalVotes.put(answerName, finalVotes.get(answerName) + 1);
			}
		}

		// Debugging
		if (debug) {
			System.out.println("AC match: " + ACmatch + " DF Match: " + DFmatch);
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			System.out.println("Voted: " + vote);
		}
	}

	private double compareFigureHTrans(RavensProblem problem, String figureName1, String figureName2) {
		int match = 0;
		int total = 0;
		try {
			BufferedImage image1 = ImageIO.read(new File(problem.getFigures().get(figureName1).getVisual()));
			BufferedImage image2 = ImageIO.read(new File(problem.getFigures().get(figureName2).getVisual()));

			int i1 = 0;
			int i2 = image1.getWidth() / 2;
			int j1 = 0;
			int j2 = 0;
			boolean hasBlack = false;
			while ((i1 < image1.getWidth() / 2) && (hasBlack == false)) {
				for (int j = 0; j < image1.getHeight(); j++) {
					if (isBlack(image1, i1, j) == 1) {
						hasBlack = true;
					}
				}
				i1++;
			}
			hasBlack = false;
			while ((i2 < image2.getWidth()) && (hasBlack == false)) {
				for (int j = 0; j < image1.getHeight(); j++) {
					if (isBlack(image2, i2, j) == 1) {
						hasBlack = true;
					}
				}
				i2++;
			}
			while ((i1 < image1.getWidth() / 2) || (i2 < image1.getWidth())) {
				for (int j = 0; j < image1.getHeight(); j++) {
					if ((i1 > image1.getWidth() / 2) && (isBlack(image2, i2, j) == 1)) {
						total++;
					}
					if ((i2 > image1.getWidth() && (isBlack(image1, i1, j) == 1))) {
						total++;
					}
					if ((i1 < image1.getWidth() / 2) && (i2 < image1.getWidth()) && ((isBlack(image1, i1, j) == 1) || (isBlack(image2, i2, j) == 1))) {
						if (isBlack(image1, i1, j) == isBlack(image2, i2, j)) {
							match++;
						}
						total++;
					}
				}
				i1++;
				i2++;
			}

			if (total == 0) {
				return 0;
			} else {
				double percentMatch = (double) match / total;
				return percentMatch;
			}
		} catch (IOException e) {
		}

		return total;
	}

	private void voteACSym(RavensProblem problem, String[] answerNames, boolean debug, HashMap<String, Double> finalVotes) {
		double min = 999;
		double ACmatch = compareFigureSym(problem, "A", "C");

		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Place vote
		for (String answerName : answerNames) {
			// if (ACmatch > 0.98 && compareFigureSym(problem, "B", answerName)
			// > 0.98) {
			if (Math.abs(ACmatch - compareFigureSym(problem, "B", answerName)) < 0.02) {
				votes.put(answerName, 0.0);

			} else {
				votes.put(answerName, 1.0);
				finalVotes.put(answerName, finalVotes.get(answerName) + 1);
			}
		}

		// Debugging
		if (debug) {
			System.out.println("AC match: " + ACmatch);
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			System.out.println("Voted: " + vote);
		}

	}

	private void voteAGSym(RavensProblem problem, String[] answerNames, boolean debug, HashMap<String, Double> finalVotes) {
		double min = 999;
		double AGmatch = compareFigureSym(problem, "A", "G");
		double BHmatch = compareFigureSym(problem, "B", "H");

		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";

		// Place vote
		for (String answerName : answerNames) {
			if (AGmatch > 0.98 && BHmatch > 0.98 && compareFigureSym(problem, "C", answerName) > 0.98) {
				votes.put(answerName, 0.0);

			} else {
				votes.put(answerName, 1.0);
				finalVotes.put(answerName, finalVotes.get(answerName) + 1);
			}
		}

		// Debugging
		if (debug) {
			System.out.println("AG match: " + AGmatch);
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			System.out.println("Voted: " + vote);
		}

	}

	private double compareFigureSym(RavensProblem problem, String figureName1, String figureName2) {
		int match = 0;
		int total = 0;
		try {
			BufferedImage image1 = ImageIO.read(new File(problem.getFigures().get(figureName1).getVisual()));
			BufferedImage image2 = ImageIO.read(new File(problem.getFigures().get(figureName2).getVisual()));
			for (int i = 0; i < image1.getWidth(); i++) {
				for (int j = 0; j < image1.getHeight(); j++) {
					if (isBlack(image1, i, j) == isBlack(image2, i, image1.getHeight() - j - 1)) {
						match++;
					}
					total++;
				}
			}
			if (total == 0) {
				return 0;
			} else {
				double percentMatch = (double) match / total;
				return percentMatch;
			}
		} catch (IOException e) {
		}

		return total;
	}

	private void voteABMatch(RavensProblem problem, String[] answerNames, boolean debug, HashMap<String, Double> finalVotes) {
		double min = 999;
		double ABmatch = compareFigure(problem, "A", "B");

		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes
		for (String answerName : answerNames) {
			double match = compareFigure(problem, "C", answerName);
			votes.put(answerName, Math.abs(ABmatch - match));
			normal += Math.abs(ABmatch - match);
		}

		// Normalize votes
		for (String answerName : answerNames) {
			double normalVote = votes.get(answerName) / normal;
			votes.put(answerName, normalVote);
			finalVotes.put(answerName, finalVotes.get(answerName) + normalVote);
		}

		// Debugging
		if (debug) {
			System.out.println("AB match: " + ABmatch);
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			System.out.println("Voted: " + vote);
		}

	}

	private void voteABCMatch(RavensProblem problem, String[] answerNames, boolean debug, HashMap<String, Double> finalVotes) {
		double min = 999;
		double ABmatch = compareFigure(problem, "A", "C");
		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes
		for (String answerName : answerNames) {
			double match = compareFigure(problem, "G", answerName);
			votes.put(answerName, Math.abs(ABmatch - match));
			normal += Math.abs(ABmatch - match);
		}

		// Normalize votes
		for (String answerName : answerNames) {
			double normalVote = votes.get(answerName) / normal;
			votes.put(answerName, normalVote);
			finalVotes.put(answerName, finalVotes.get(answerName) + normalVote);
		}

		// Debugging
		if (debug) {
			System.out.println("ABC match: " + ABmatch);
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			System.out.println("Voted: " + vote);
		}

	}

	private double compareFigure(RavensProblem problem, String figureName1, String figureName2) {
		int match = 0;
		int total = 0;
		try {
			BufferedImage image1 = ImageIO.read(new File(problem.getFigures().get(figureName1).getVisual()));
			BufferedImage image2 = ImageIO.read(new File(problem.getFigures().get(figureName2).getVisual()));
			for (int i = 0; i < image1.getWidth(); i++) {
				for (int j = 0; j < image1.getHeight(); j++) {
					if (isBlack(image1, i, j) == 1) {
						if (isBlack(image1, i, j) == isBlack(image2, i, j)) {
							match++;
						}
						total++;
					}
				}
			}
			if (total == 0) {
				return 0;
			} else {
				double percentMatch = (double) match / total;
				return percentMatch;
			}
		} catch (IOException e) {
		}

		return total;
	}

	private void aggregateVotes(RavensProblem problem, boolean debug, HashMap<String, Double> finalVotes, String[] answerNames) {
		String vote = "";
		double min = 999;
		for (String answerName : answerNames) {
			if (finalVotes.get(answerName) < min) {
				min = finalVotes.get(answerName);
				vote = answerName;
			}
		}

		if (debug) {
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + finalVotes.get(answerName));

			}
			int correct = problem.checkAnswer(Integer.valueOf(vote));
			System.out.println("Correct: " + correct + " Voted: " + vote);
		}
	}

	private void calculateRatios(RavensProblem problem, boolean debug, HashMap<String, FigureFrame> figureFrames, String[] figureNames) {
		for (String figureName : figureNames) {
			FigureFrame frame = new FigureFrame();
			RavensFigure thisFigure = problem.getFigures().get(figureName);

			try {
				frame.setRatioBlack(blackRatio(thisFigure));
			} catch (IOException e) {
			}
			if (debug) {
				System.out.println("Ratio: " + frame.getRatioBlack());
				printObjects(thisFigure);
				printBlackRatio(thisFigure);
			}

			figureFrames.put(figureName, frame);
		}
	}

	private void voteABRatio(RavensProblem problem, HashMap<String, FigureFrame> figureFrames, String[] answerNames, boolean debug,
			HashMap<String, Double> finalVotes) {
		double min = 999;
		double ABratio = figureFrames.get("A").getRatioBlack() / figureFrames.get("B").getRatioBlack();
		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes
		for (String answerName : answerNames) {
			double ratio = figureFrames.get("C").getRatioBlack() / figureFrames.get(answerName).getRatioBlack();
			votes.put(answerName, Math.abs(ABratio - ratio));
			normal += Math.abs(ABratio - ratio);
		}

		// Normalize votes
		for (String answerName : answerNames) {
			double normalVote = votes.get(answerName) / normal;
			votes.put(answerName, normalVote);
			finalVotes.put(answerName, finalVotes.get(answerName) + normalVote);
		}

		// Debugging
		if (debug) {
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			int correct = problem.checkAnswer(Integer.valueOf(vote));
			System.out.println("Correct: " + correct + " Voted: " + vote);
		}
	}

	private void voteABCRatio(RavensProblem problem, HashMap<String, FigureFrame> figureFrames, String[] answerNames, boolean debug,
			HashMap<String, Double> finalVotes) {
		double min = 999;
		double ABratio = 0;
		if (figureFrames.get("A").getRatioBlack() != 0) {
			ABratio = figureFrames.get("A").getRatioBlack() / figureFrames.get("C").getRatioBlack()
					* (figureFrames.get("G").getRatioBlack() / figureFrames.get("H").getRatioBlack())
					/ (figureFrames.get("A").getRatioBlack() / figureFrames.get("B").getRatioBlack());

		}
		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes

		for (String answerName : answerNames) {
			double ratio = figureFrames.get("G").getRatioBlack() / figureFrames.get(answerName).getRatioBlack();
			votes.put(answerName, Math.abs(ABratio - ratio));
			normal += Math.abs(ABratio - ratio);
		}

		// Normalize votes
		for (String answerName : answerNames) {
			double normalVote = votes.get(answerName) / normal;
			votes.put(answerName, normalVote);
			if (Math.abs((figureFrames.get("A").getRatioBlack() / figureFrames.get("B").getRatioBlack())
					- (figureFrames.get("D").getRatioBlack() / figureFrames.get("E").getRatioBlack())) > .5) {
				finalVotes.put(answerName, finalVotes.get(answerName) + normalVote);
			} else {
				finalVotes.put(answerName, finalVotes.get(answerName) + 0);
			}
			// finalVotes.put(answerName, finalVotes.get(answerName) +
			// normalVote);
		}
		// Debugging
		if (debug) {
			System.out.println("A:B:C:: " + figureFrames.get("A").getRatioBlack() / figureFrames.get("B").getRatioBlack() + " : "
					+ figureFrames.get("B").getRatioBlack() / figureFrames.get("C").getRatioBlack());
			System.out.println("D:E:F:: " + figureFrames.get("D").getRatioBlack() / figureFrames.get("E").getRatioBlack() + " : "
					+ figureFrames.get("E").getRatioBlack() / figureFrames.get("F").getRatioBlack());
			System.out.println("G:H:: " + figureFrames.get("G").getRatioBlack() / figureFrames.get("H").getRatioBlack());
			System.out.println("ideal ratio: " + ABratio);
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			System.out.println("Voted: " + vote);
		}
	}

	private void voteACRatio(RavensProblem problem, HashMap<String, FigureFrame> figureFrames, String[] answerNames, boolean debug,
			HashMap<String, Double> finalVotes) {
		double min = 999;
		double ABratio = figureFrames.get("A").getRatioBlack() / figureFrames.get("C").getRatioBlack();
		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes
		for (String answerName : answerNames) {
			double ratio = figureFrames.get("B").getRatioBlack() / figureFrames.get(answerName).getRatioBlack();
			votes.put(answerName, Math.abs(ABratio - ratio));
			normal += Math.abs(ABratio - ratio);
		}

		// Normalize votes
		for (String answerName : answerNames) {
			double normalVote = votes.get(answerName) / normal;
			votes.put(answerName, normalVote);
			finalVotes.put(answerName, finalVotes.get(answerName) + normalVote);
		}

		// Debugging
		if (debug) {
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			int correct = problem.checkAnswer(Integer.valueOf(vote));
			System.out.println("Correct: " + correct + " Voted: " + vote);
		}
	}

	private void voteABDiff(RavensProblem problem, HashMap<String, FigureFrame> figureFrames, String[] answerNames, boolean debug,
			HashMap<String, Double> finalVotes) {
		double min = 999;
		double ABratio = figureFrames.get("A").getRatioBlack() - figureFrames.get("B").getRatioBlack();
		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes
		for (String answerName : answerNames) {
			double ratio = figureFrames.get("C").getRatioBlack() - figureFrames.get(answerName).getRatioBlack();
			votes.put(answerName, Math.abs(ABratio - ratio));
			normal += Math.abs(ABratio - ratio);
		}

		// Normalize votes
		for (String answerName : answerNames) {
			double normalVote = votes.get(answerName) / normal;
			votes.put(answerName, normalVote);
			finalVotes.put(answerName, finalVotes.get(answerName) + normalVote);
		}

		// Debugging
		if (debug) {
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			System.out.println("Voted: " + vote);
		}
	}

	private void voteACDiff(RavensProblem problem, HashMap<String, FigureFrame> figureFrames, String[] answerNames, boolean debug,
			HashMap<String, Double> finalVotes) {
		double min = 999;
		double ABratio = figureFrames.get("A").getRatioBlack() - figureFrames.get("C").getRatioBlack();
		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes
		for (String answerName : answerNames) {
			double ratio = figureFrames.get("B").getRatioBlack() - figureFrames.get(answerName).getRatioBlack();
			votes.put(answerName, Math.abs(ABratio - ratio));
			normal += Math.abs(ABratio - ratio);
		}

		// Normalize votes
		for (String answerName : answerNames) {
			double normalVote = votes.get(answerName) / normal;
			votes.put(answerName, normalVote);
			finalVotes.put(answerName, finalVotes.get(answerName) + normalVote);
		}

		// Debugging
		if (debug) {
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			int correct = problem.checkAnswer(Integer.valueOf(vote));
			System.out.println("Correct: " + correct + " Voted: " + vote);
		}
	}

	private void voteABCDiff(RavensProblem problem, HashMap<String, FigureFrame> figureFrames, String[] answerNames, boolean debug,
			HashMap<String, Double> finalVotes) {
		double min = 999;
		double ABratio = figureFrames.get("G").getRatioBlack() - figureFrames.get("H").getRatioBlack();

		HashMap<String, Double> votes = new HashMap<String, Double>();
		String vote = "";
		double normal = 0;

		// Calculate votes

		for (String answerName : answerNames) {
			double ratio = figureFrames.get("H").getRatioBlack() - figureFrames.get(answerName).getRatioBlack();
			votes.put(answerName, Math.abs(ABratio - ratio));
			normal += Math.abs(ABratio - ratio);
		}

		// Normalize votes
		for (String answerName : answerNames) {
			double normalVote = votes.get(answerName) / normal;
			votes.put(answerName, normalVote);
			finalVotes.put(answerName, finalVotes.get(answerName) + normalVote);
		}

		// Debugging
		if (debug) {
			System.out.println("AB: " + (figureFrames.get("A").getRatioBlack() - figureFrames.get("B").getRatioBlack()) + " DE: "
					+ (figureFrames.get("D").getRatioBlack() - figureFrames.get("E").getRatioBlack()));
			for (String answerName : answerNames) {
				System.out.println(answerName + ": " + votes.get(answerName) + " " + finalVotes.get(answerName));

				if (votes.get(answerName) < min) {
					min = votes.get(answerName);
					vote = answerName;
				}
			}
			System.out.println("Voted: " + vote);
		}
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
