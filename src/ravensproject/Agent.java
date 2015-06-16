package ravensproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

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
		HashMap<String, RavensFigure> figures = problem.getFigures();

		System.out.println(problem.getName());
		if (problem.hasVerbal()) {

			if (Heuristics.testUnchanged(figures.get("A"), figures.get("B"))) {
				System.out.println("  Heuristic: unchanged");
				int proposedAnswer = -1;
				int numProposedAnswers = 0;
				for (int i = 1; i <= 6; i++) {
					System.out.println("  "
							+ String.valueOf(i)
							+ ": "
							+ Heuristics.testUnchanged(figures.get("C"),
									figures.get(String.valueOf(i))));
					if (Heuristics.testUnchanged(figures.get("C"),
							figures.get(String.valueOf(i)))) {
						proposedAnswer = i;
						numProposedAnswers++;
					}
				}
				if (numProposedAnswers == 1 && proposedAnswer > 0) {
					System.out.println(problem.checkAnswer(proposedAnswer));
				}
			} else if (Heuristics.testRotateSquare(figures.get("A"),
					figures.get("B"), figures.get("C"))) {
				System.out.println("  Heuristic: rotated square");
				int proposedAnswer = -1;
				int numProposedAnswers = 0;
				for (int i = 1; i <= 6; i++) {
					System.out.println("  "
							+ String.valueOf(i)
							+ ": "
							+ Heuristics.answerRotateSquare(figures.get("A"),
									figures.get("B"), figures.get("C"),
									figures.get(String.valueOf(i))));
					if (Heuristics.answerRotateSquare(figures.get("A"),
							figures.get("B"), figures.get("C"),
							figures.get(String.valueOf(i)))) {
						proposedAnswer = i;
						numProposedAnswers++;
					}
				}
				if (numProposedAnswers == 1 && proposedAnswer > 0) {
					System.out.println(problem.checkAnswer(proposedAnswer));
				}
			} else if (Heuristics
					.testRotate(figures.get("A"), figures.get("B"))) {
				System.out.println("  Heuristic: rotated");
				int proposedAnswer = -1;
				int numProposedAnswers = 0;
				for (int i = 1; i <= 6; i++) {
					System.out.println("  "
							+ String.valueOf(i)
							+ ": "
							+ Heuristics.testRotate(figures.get("C"),
									figures.get(String.valueOf(i))));
					if (Heuristics.testRotate(figures.get("C"),
							figures.get(String.valueOf(i)))) {
						proposedAnswer = i;
						numProposedAnswers++;
					}
				}
				if (numProposedAnswers == 1 && proposedAnswer > 0) {
					System.out.println(problem.checkAnswer(proposedAnswer));
				}
			} else {
				int proposedAnswer = Heuristics.generateSemNetSingle(problem);
				System.out.println("sem net" + proposedAnswer);
				if (proposedAnswer > 0) {
					System.out.println("answer: " + proposedAnswer + " correct: " + problem.checkAnswer(proposedAnswer));
				} else {
					proposedAnswer = Heuristics.generateSemNetMultiple(problem);
					System.out.println("answer: " + proposedAnswer + " correct: " + problem.checkAnswer(proposedAnswer));
				}
			}
		}

		return -1;
	}
}

class Heuristics {
	static boolean testUnchanged(RavensFigure figureA, RavensFigure figureB) {
		ArrayList<String> mappedBObjects = new ArrayList<String>();
		for (String objectAName : figureA.getObjects().keySet()) {
			RavensObject objectA = figureA.getObjects().get(objectAName);
			boolean mappedObjectFound = false;
			for (String objectBName : figureB.getObjects().keySet()) {
				RavensObject objectB = figureB.getObjects().get(objectBName);
				boolean attributesMatch = true;
				for (String attr : objectA.getAttributes().keySet()) {
					switch (attr) {
					case "inside":
					case "above":
						break;
					default:
						if (!objectA.getAttributes().get(attr)
								.equals(objectB.getAttributes().get(attr))) {
							attributesMatch = false;
						}
					}

				}
				if (attributesMatch) {
					mappedObjectFound = true;
				}

			}
			if (mappedObjectFound == false) {
				return false;
			}
		}
		return true;
	}

	static boolean testRotate(RavensFigure figureA, RavensFigure figureB) {
		ArrayList<String> mappedBObjects = new ArrayList<String>();
		for (String objectAName : figureA.getObjects().keySet()) {
			RavensObject objectA = figureA.getObjects().get(objectAName);
			boolean mappedObjectFound = false;
			for (String objectBName : figureB.getObjects().keySet()) {
				RavensObject objectB = figureB.getObjects().get(objectBName);
				boolean attributesMatch = true;
				for (String attr : objectA.getAttributes().keySet()) {
					switch (attr) {
					case "inside":
					case "above":
						break;
					case "angle":
						if (objectA.getAttributes().get(attr)
								.equals(objectB.getAttributes().get(attr))) {
							attributesMatch = false;
						}
						break;
					default:
						if (!objectA.getAttributes().get(attr)
								.equals(objectB.getAttributes().get(attr))) {
							attributesMatch = false;
						}
					}

				}
				if (attributesMatch) {
					mappedObjectFound = true;
				}

			}
			if (mappedObjectFound == false) {
				return false;
			}
		}
		return true;
	}

	static boolean testRotateSquare(RavensFigure figureA, RavensFigure figureB,
			RavensFigure figureC) {

		ArrayList<RavensFigure> figures = new ArrayList<RavensFigure>();
		figures.add(figureA);
		figures.add(figureB);
		figures.add(figureC);
		ArrayList<RavensObject> objects = new ArrayList<RavensObject>();
		for (RavensFigure f : figures) {
			if (f.getObjects().keySet().size() != 1) {
				return false;
			}
		}

		RavensObject objA = getSingleRavensObject(figureA);
		RavensObject objB = getSingleRavensObject(figureB);
		RavensObject objC = getSingleRavensObject(figureC);

		if (!objA.getAttributes().get("shape")
				.equals(objB.getAttributes().get("shape"))) {
			return false;
		}
		if (!objA.getAttributes().get("shape")
				.equals(objC.getAttributes().get("shape"))) {
			return false;
		}

		int angleA = Integer.valueOf(objA.getAttributes().get("angle"));
		int angleB = Integer.valueOf(objB.getAttributes().get("angle"));
		int angleC = Integer.valueOf(objC.getAttributes().get("angle"));

		if (((angleB - angleA + 360) % 360 == 90)
				&& ((angleA - angleC + 360) % 360 == 90)) {
			return true;
		}

		return false;
	}

	static boolean answerRotateSquare(RavensFigure figureA,
			RavensFigure figureB, RavensFigure figureC, RavensFigure figureD) {

		ArrayList<RavensFigure> figures = new ArrayList<RavensFigure>();
		figures.add(figureA);
		figures.add(figureB);
		figures.add(figureC);
		figures.add(figureD);
		ArrayList<RavensObject> objects = new ArrayList<RavensObject>();
		for (RavensFigure f : figures) {
			if (f.getObjects().keySet().size() != 1) {
				return false;
			}
		}

		RavensObject objA = getSingleRavensObject(figureA);
		RavensObject objB = getSingleRavensObject(figureB);
		RavensObject objC = getSingleRavensObject(figureC);
		RavensObject objD = getSingleRavensObject(figureD);

		for (String attr : objC.getAttributes().keySet()) {
			switch (attr) {
			case "inside":
			case "above":
			case "angle":
				break;
			default:
				if (!objC.getAttributes().get(attr)
						.equals(objD.getAttributes().get(attr))) {
					return false;
				}
			}
		}
		if (!objA.getAttributes().get("shape")
				.equals(objB.getAttributes().get("shape"))) {
			return false;
		}
		if (!objA.getAttributes().get("shape")
				.equals(objC.getAttributes().get("shape"))) {
			return false;
		}
		if (!objA.getAttributes().get("shape")
				.equals(objD.getAttributes().get("shape"))) {
			return false;
		}

		int angleA = Integer.valueOf(objA.getAttributes().get("angle"));
		int angleB = Integer.valueOf(objB.getAttributes().get("angle"));
		int angleC = Integer.valueOf(objC.getAttributes().get("angle"));
		int angleD = Integer.valueOf(objD.getAttributes().get("angle"));

		if ((angleD != angleA)
				&& (angleD != angleB)
				&& (angleD != angleC)
				&& (((angleD - angleC + 360) % 360 == 90) || ((angleD - angleC + 360) % 360 == 270))) {
			return true;
		}

		return false;
	}

	static int generateSemNetSingle(RavensProblem problem) {
		if (problem.getFigures().get("A").getObjects().keySet().size() != 1)
			return -1;
		if (problem.getFigures().get("B").getObjects().keySet().size() != 1)
			return -1;
		if (problem.getFigures().get("C").getObjects().keySet().size() != 1)
			return -1;

		RavensObject objA = getSingleRavensObject(problem.getFigures().get("A"));
		RavensObject objB = getSingleRavensObject(problem.getFigures().get("B"));
		RavensObject objC = getSingleRavensObject(problem.getFigures().get("C"));

		HashMap<String, String> changesAB = new HashMap<String, String>();
		for (String attr : objA.getAttributes().keySet()) {
			if (!objB.getAttributes().containsKey(attr)) {
				changesAB.put("A" + attr, objA.getAttributes().get(attr));
				changesAB.put("B" + attr, "delete");
			} else if (!objA.getAttributes().get(attr)
					.equals(objB.getAttributes().get(attr))) {
				if (attr.equals("alignment")
						&& objA.getAttributes().get(attr).contains("-")) {
					String alA = objA.getAttributes().get(attr);
					String alB = objB.getAttributes().get(attr);
					String[] tokA = alA.split("-");
					String[] tokB = alB.split("-");
					if (tokA[0].equals(tokB[0])) {

					} else {
						changesAB.put("AalignmentV", tokA[0]);
						changesAB.put("BalignmentV", tokB[0]);
					}
					if (tokA[1].equals(tokB[1])) {

					} else {
						changesAB.put("AalignmentH", tokA[1]);
						changesAB.put("BalignmentH", tokB[1]);
					}
				} else {
					changesAB.put("A" + attr, objA.getAttributes().get(attr));
					changesAB.put("B" + attr, objB.getAttributes().get(attr));
				}
			}
		}

		HashMap<String, String> proposeD = new HashMap<String, String>();

		for (String attr : objC.getAttributes().keySet()) {
			if (changesAB.containsKey("A" + attr)) {
				if (changesAB.get("B" + attr).equals("delete")) {

				} else {
					proposeD.put(attr, changesAB.get("B" + attr));
				}
			} else if (attr.equals("alignment")
					&& objA.getAttributes().get(attr).contains("-")) {
				String alC = objC.getAttributes().get(attr);
				String[] tokC = alC.split("-");
				String proposeDV = "";
				String proposeDH = "";
				if (changesAB.containsKey("AalignmentV")) {
					proposeDV = changesAB.get("BalignmentV");
				} else {
					proposeDV = tokC[0];
				}
				if (changesAB.containsKey("AalignmentH")) {
					proposeDH = changesAB.get("BalignmentH");
				} else {
				}
				proposeD.put(attr, proposeDV + "-" + proposeDH);

			} else {
				proposeD.put(attr, objC.getAttributes().get(attr));
			}

		}

		int proposedAnswer = -1;
		int numProposedAnswers = 0;
		for (int i = 1; i <= 6; i++) {
			String n = String.valueOf(i);
			RavensFigure figureN = problem.getFigures().get(n);
			if (figureN.getObjects().keySet().size() != 1)
				continue;

			RavensObject objN = getSingleRavensObject(problem.getFigures().get(
					n));

			boolean testN = true;
			for (String attr : proposeD.keySet()) {

				if (objN.getAttributes().containsKey(attr)
						&& objN.getAttributes().get(attr)
								.equals(proposeD.get(attr))) {

				} else {
					testN = false;
				}
			}
			if (testN == true) {
				proposedAnswer = i;
				numProposedAnswers++;
			}

			System.out.println("  " + n + ": " + testN);

		}
		if (numProposedAnswers == 1 && proposedAnswer > 0) {
			return proposedAnswer;
		}
		return -1;
	}

	static int generateSemNetMultiple(RavensProblem problem) {
		RavensFigure figureA = problem.getFigures().get("A");
		RavensFigure figureB = problem.getFigures().get("B");
		RavensFigure figureC = problem.getFigures().get("C");

		HashMap<String, RavensObject> objectsA = figureA.getObjects();
		HashMap<String, RavensObject> objectsB = figureB.getObjects();
		HashMap<String, RavensObject> objectsC = figureC.getObjects();

		// AB

		HashMap<String, String> unchangedAB = new HashMap<String, String>();

		for (String nameObjA : objectsA.keySet()) {
			RavensObject objA = objectsA.get(nameObjA);
			for (String nameObjB : objectsB.keySet()) {
				RavensObject objB = objectsB.get(nameObjB);
				if (unchanged(objA, objB)) {
					unchangedAB.put(nameObjA, nameObjB);
				}
			}
		}
		HashMap<String, String> oneChangeAB = new HashMap<String, String>();

		for (String nameObjA : objectsA.keySet()) {
			if (unchangedAB.containsKey(nameObjA)) {
				continue;
			}
			RavensObject objA = objectsA.get(nameObjA);
			for (String nameObjB : objectsB.keySet()) {
				if (unchangedAB.containsValue(nameObjB)) {
					continue;
				}
				RavensObject objB = objectsB.get(nameObjB);
				if (oneChange(objA, objB)) {
					oneChangeAB.put(nameObjA, nameObjB);
				}
			}
		}

		for (String oneChangeA : oneChangeAB.keySet()) {
			System.out.println("one change between " + oneChangeA + " "
					+ oneChangeAB.get(oneChangeA));
		}

		ArrayList<String> onlyAB = new ArrayList<String>();
		ArrayList<String> onlyB = new ArrayList<String>();

		for (String nameObjA : objectsA.keySet()) {
			if (unchangedAB.containsKey(nameObjA)) {
				continue;
			} else if (oneChangeAB.containsKey(nameObjA)) {
				continue;
			} else {
				onlyAB.add(nameObjA);
			}
		}
		for (String a : onlyAB) {
			// System.out.println("only a: " + a);
		}

		for (String nameObjB : objectsB.keySet()) {
			if (unchangedAB.containsValue(nameObjB)) {
				continue;
			} else if (oneChangeAB.containsValue(nameObjB)) {
				continue;
			} else {
				onlyB.add(nameObjB);
			}
		}

		for (String b : onlyB) {
			// System.out.println("only b: " + b);
		}

		// AC

		HashMap<String, String> unchangedAC = new HashMap<String, String>();

		for (String nameObjA : objectsA.keySet()) {
			RavensObject objA = objectsA.get(nameObjA);
			for (String nameObjC : objectsC.keySet()) {
				RavensObject objC = objectsC.get(nameObjC);
				if (unchanged(objA, objC)) {
					unchangedAC.put(nameObjA, nameObjC);
				}
			}
		}
		HashMap<String, String> oneChangeAC = new HashMap<String, String>();

		for (String nameObjA : objectsA.keySet()) {
			if (unchangedAC.containsKey(nameObjA)) {
				continue;
			}
			RavensObject objA = objectsA.get(nameObjA);
			for (String nameobjC : objectsC.keySet()) {
				if (unchangedAC.containsValue(nameobjC)) {
					continue;
				}
				RavensObject objC = objectsC.get(nameobjC);
				if (oneChange(objA, objC)) {
					oneChangeAC.put(nameObjA, nameobjC);
				}
			}
		}

		ArrayList<String> onlyAC = new ArrayList<String>();
		ArrayList<String> onlyC = new ArrayList<String>();

		for (String nameObjA : objectsA.keySet()) {
			if (unchangedAC.containsKey(nameObjA)) {
				continue;
			} else if (oneChangeAC.containsKey(nameObjA)) {
				continue;
			} else {
				onlyAC.add(nameObjA);
			}
		}
		for (String a : onlyAC) {
			// System.out.println("only a: " + a);
		}

		for (String nameObjC : objectsC.keySet()) {
			if (unchangedAC.containsValue(nameObjC)) {
				continue;
			} else if (oneChangeAC.containsValue(nameObjC)) {
				continue;
			} else {
				onlyC.add(nameObjC);
			}
		}

		for (String c : onlyC) {
			// System.out.println("only c: " + c);
		}

		// Generate D
		ArrayList<HashMap<String, String>> proposeD = new ArrayList<HashMap<String, String>>();

		// Unchanged A-B-C
		for (String objABUnchanged : unchangedAB.keySet()) {
			RavensObject objTestC = objectsA.get(objABUnchanged);
			for (String objACUnchanged : unchangedAC.keySet()) {
				if (objABUnchanged.equals(objACUnchanged)) {
					// System.out.println("ABC unchanged " + objABUnchanged);
					proposeD.add(objectsA.get(objABUnchanged).getAttributes());
				}
			}
		}

		// B only
		for (String objBOnly : onlyB) {
			// System.out.println("B only " + objBOnly);
			proposeD.add(objectsB.get(objBOnly).getAttributes());
		}

		// C Only
		for (String objCOnly : onlyC) {
			// System.out.println("B only " + objBOnly);
			proposeD.add(objectsC.get(objCOnly).getAttributes());
		}

		// One change from A-C
		
		for (String objACOneChange : oneChangeAC.keySet()) {
			boolean inB = false;
			for (String objsInB : unchangedAB.keySet()) {
				if (objACOneChange.equals(objsInB)) {
					inB = true;
				}
			}
			if (inB) {
				proposeD.add(objectsC.get(oneChangeAC.get(objACOneChange))
						.getAttributes());
			}
			for (String objsInB : oneChangeAB.keySet()) {
				if (objACOneChange.equals(objsInB)) {
					inB = true;
				}
				if (inB) {
					String changeAttr = "";
					String changeVal = "";

					RavensObject objA = objectsA.get(objACOneChange);
					RavensObject objB = objectsB.get(oneChangeAB
							.get(objACOneChange));
					for (String attr : objA.getAttributes().keySet()) {
						switch (attr) {
						case "inside":
						case "above":
							break;
						default:
							if (!objA.getAttributes().get(attr)
									.equals(objB.getAttributes().get(attr))) {
								changeAttr = attr;
								changeVal = objB.getAttributes().get(attr);
							}
						}
					}
					HashMap<String, String> newD = objectsC.get(
							oneChangeAC.get(objACOneChange)).getAttributes();
					newD.remove(changeAttr);
					newD.put(changeAttr, changeVal);
					proposeD.add(newD);
				}
			}
		}

		// Test Answers
		int proposedAnswer = -1;
		HashMap<String, Integer> AnswerScore = new HashMap<String, Integer>();
		for (int i = 1; i <= 6; i++) {
			String n = String.valueOf(i);
			RavensFigure figureN = problem.getFigures().get(n);

			Integer score = 0;
			boolean foundMatch = false;

			for (HashMap<String, String> objD : proposeD) {
				for (String objNName : figureN.getObjects().keySet()) {
					RavensObject objN = figureN.getObjects().get(objNName);
					if (unchangedProposal(objN, objD)) {
						score++;
						foundMatch = true;
					}
				}
				if (foundMatch == false) {
					score--;
				}

				int diff = proposeD.size()
						- figureN.getObjects().keySet().size();
				if (diff > 0) {
					diff = 0 - diff;
				}
				score += diff;
			}
			AnswerScore.put(n, score);
		}
		int highestScore = -1000;
		for (String n : AnswerScore.keySet()) {
			System.out.println("  " + n + ": " + AnswerScore.get(n));
			if (AnswerScore.get(n) > highestScore) {
				highestScore = AnswerScore.get(n);
				proposedAnswer = Integer.valueOf(n);

			}
		}

		return proposedAnswer;
	}

	private static boolean unchanged(RavensObject objA, RavensObject objB) {
		for (String attr : objA.getAttributes().keySet()) {
			switch (attr) {
			case "inside":
			case "above":
				// String[] tokA = objA.getAttributes().get(attr).split(",");
				// String[] tokB = objB.getAttributes().get(attr).split(",");
				// if(tokA.length != tokB.length) {
				// return false;
				// }
				break;
			default:
				if (!objA.getAttributes().get(attr)
						.equals(objB.getAttributes().get(attr))) {
					return false;
				}

			}
		}

		return true;
	}

	private static boolean unchangedProposal(RavensObject objA,
			HashMap<String, String> objD) {
		for (String attr : objA.getAttributes().keySet()) {
			switch (attr) {
			case "inside":
			case "above":
				// String[] tokA = objA.getAttributes().get(attr).split(",");
				// String[] tokB = objB.getAttributes().get(attr).split(",");
				// if(tokA.length != tokB.length) {
				// return false;
				// }
				break;
			default:
				if (!objA.getAttributes().get(attr).equals(objD.get(attr))) {
					return false;
				}

			}
		}

		return true;
	}

	private static boolean oneChange(RavensObject objA, RavensObject objB) {
		int numChanges = 0;
		for (String attr : objA.getAttributes().keySet()) {
			switch (attr) {
			case "inside":
				break;
			case "above":
				if(!objB.getAttributes().containsKey(attr)) {
					numChanges++;
				}
				break;
			default:
				if (!objA.getAttributes().get(attr)
						.equals(objB.getAttributes().get(attr))) {
					numChanges++;
				}
			}
		}
		if (numChanges == 1) {
			return true;
		}
		return false;
	}

	private static RavensObject getSingleRavensObject(RavensFigure figure) {
		String s = figure.getObjects().keySet().iterator().next();
		return figure.getObjects().get(s);

	}
}
