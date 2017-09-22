package allocation.algorithms;

import static allocation.algorithms.convexLPSet.MatrixHelper.arrayDiff;
import static allocation.algorithms.convexLPSet.MatrixHelper.getColumn;
import static allocation.algorithms.convexLPSet.MatrixHelper.getColumns;
import static allocation.algorithms.convexLPSet.MatrixHelper.getFullfillingIndices;
import static allocation.algorithms.convexLPSet.MatrixHelper.getFullfillingRows;
import static allocation.algorithms.convexLPSet.MatrixHelper.getRow;
import static allocation.algorithms.convexLPSet.MatrixHelper.getRows;
import static allocation.algorithms.convexLPSet.MatrixHelper.isPartOfInvertible;
import static allocation.algorithms.convexLPSet.MatrixHelper.matAbs;
import static allocation.algorithms.convexLPSet.MatrixHelper.matMin;
import static allocation.algorithms.convexLPSet.MatrixHelper.matRound;
import static allocation.algorithms.convexLPSet.MatrixHelper.matchAll;
import static allocation.algorithms.convexLPSet.MatrixHelper.nchoosek;
import static allocation.algorithms.convexLPSet.MatrixHelper.removeRows;
import static allocation.algorithms.convexLPSet.MatrixHelper.rref;
import static allocation.algorithms.convexLPSet.MatrixHelper.sortColumns;
import static allocation.algorithms.convexLPSet.MatrixHelper.truthReduction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import org.ejml.simple.SimpleMatrix;

import allocation.Allocation;
import allocation.AllocationStrategyFamily;
import allocation.algorithms.convexLPSet.Considerer;
import allocation.algorithms.convexLPSet.EquationSearcher;
import allocation.algorithms.convexLPSet.InequalityFactory;
import allocation.algorithms.convexLPSet.LPSetException;
import allocation.algorithms.convexLPSet.PreferenceInequallities;
import allocation.algorithms.convexLPSet.RedundancyChecker;
import allocation.algorithms.convexLPSet.StartingCornerCalculator;
import main.Main;
import preference.scenario.Scenario;

public class PopularConvexSet implements AllocationStrategyFamily {
	@Override
	public List<Allocation> getAllAllocations(Scenario sc) {
		if(!canApply(sc)) {
			return null;
		}

		Vector<Allocation> out = new Vector<>();
		try {
			out = convexPopularSet(sc);
		}catch(LPSetException e) {
			e.printStackTrace();
		}

		return out;
	}
	
	/**
	 * Entry point to the convex set calculation. For more in depth documentation see the original matlab code.
	 * @param scenario
	 * @return
	 * @throws LPSetException
	 */
	public Vector<Allocation> convexPopularSet(Scenario scenario) throws LPSetException {
		/*
		 * To anyone who has to debug/modify this pice of code:
		 * First of all I'm sorry. This won't be fun but let me elaborate.
		 * If something broke that works in matlab the reason is probably a rounding error.
		 * Matlab has really low precision (I'm taking 1e-4) and some of the comparisons in the
		 * original code are with 0 so things like 0e-9 will not trigger the expression.
		 * Also the LP library I'm using is a lot more precise and might discover unbounded
		 * equations that matlab won't find. 
		 * I'm fairly certain in the correctness of "constructInequalities" and this code up
		 * to (and including) the corner calculation. Past that it gets a little more difficult.
		 * Especially searchEquations gets a little... You'll see... but I ran a couple test and it worked.
		 * If there is something wrong it is probably the LP.
		 * Just compare the results with the matlab version and look for the first difference.
		 */
		
		PreferenceInequallities ineqalities = InequalityFactory.constructInequalities(scenario);
		SimpleMatrix ASearchE = new SimpleMatrix(ineqalities.AInequality);
		SimpleMatrix bSearchE = new SimpleMatrix(ineqalities.bInequality);
		SimpleMatrix Aeq = new SimpleMatrix(ineqalities.AEquality);
		SimpleMatrix beq = new SimpleMatrix(ineqalities.bEquality);
		int[] clearedEqalities = ineqalities.cleared.stream().mapToInt(Integer::intValue).toArray();

		//Solve LP for starting corner
		SimpleMatrix corner = StartingCornerCalculator.CalculateStartingCorner(ASearchE,bSearchE);
		int[] activeIneqInd = getFullfillingIndices(matAbs(ASearchE.mult(corner).minus(bSearchE)), d->d<Main.F_POINT_PRECISION);
		
		//Search for Edges and follow them
		boolean[] equationLog = EquationSearcher.searchEquations(getRows(ASearchE,activeIneqInd),getRows(bSearchE, activeIneqInd)).searchLog;
		
		int[] equationInd = truthReduction(activeIneqInd, equationLog); 

		PreferenceInequallities inEq = InequalityFactory.reduceInequalities(ASearchE, bSearchE, getRows(ASearchE, equationInd), getRows(bSearchE, equationInd));
		SimpleMatrix ANew = new SimpleMatrix(inEq.AInequality);
		SimpleMatrix bNew = new SimpleMatrix(inEq.bInequality);
		int[] clearedInequalities = inEq.cleared.stream().mapToInt(Integer::intValue).toArray();

		corner = removeRows(corner, clearedInequalities);

		int[] toKeepLog = getFullfillingRows(matAbs(ANew), d-> d>Main.F_POINT_PRECISION);
		if(matchAll(removeRows(bNew, toKeepLog), d->d>-Main.F_POINT_PRECISION)) {
			ANew = getRows(ANew, toKeepLog);
			bNew = getRows(bNew, toKeepLog);
		} else {
			throw new LPSetException("No feasible point");
		}

		SimpleMatrix extremalPointsOld = corner.copy();
		if(ANew.numRows() != 0) {
			int dim = corner.getNumElements();

			for(int k=0;k<extremalPointsOld.numCols();k++) {
				SimpleMatrix x = getColumn(extremalPointsOld, k);
				int[] activeIneqLog = getFullfillingIndices(matAbs(ANew.mult(x).minus(bNew)), d-> d<Main.F_POINT_PRECISION);
				int[] nonRedundantIndLog = RedundancyChecker.checkRedundantConstraints(getRows(ANew,activeIneqLog),getRows(bNew,activeIneqLog));
			
				//Line 65
				IntPredicate nContains = n-> {
					for(int i=0;i<nonRedundantIndLog.length;i++) {
						if(nonRedundantIndLog[i] == n) {
							return false;
						}
					}
					return true;
				};

				int[] redundantIntLog = IntStream.range(0, activeIneqLog.length).filter(nContains).toArray();
				//Line 66
				activeIneqInd = activeIneqLog.clone();

				//Line 67
				Vector<Integer> red = new Vector<>();
				for(int j=0;j<redundantIntLog.length;j++) {
						red.add(activeIneqLog[redundantIntLog[j]]);
				}
				int[] redundantInd = red.stream().mapToInt(Integer::intValue).toArray();
				//Line 70+
				ANew = removeRows(ANew, redundantInd);
				bNew = removeRows(bNew, redundantInd);
				
				//Line 72
				Vector<Integer> tempVec = new Vector<>();
				for(int i=0;i<activeIneqLog.length;i++) {
					boolean add = true;
					for(int j=0;j<redundantInd.length;j++) {
						if(redundantInd[j] == activeIneqLog[i]) {
							add = false;
						}
					}
					if(add) {
						tempVec.add(activeIneqLog[i]);
					}
				}
				activeIneqLog = tempVec.stream().mapToInt(Integer::intValue).toArray();
				
				for(int i=0;i<activeIneqLog.length;i++) {
					int sub = 0;
					for(int j=0;j<redundantInd.length;j++) {
						if(redundantInd[j] < activeIneqLog[i]) {
							sub++;
						}
					}
					activeIneqLog[i] -= sub;
				}
				
				//Line 74-76
				Vector<Integer> activeVec 	= new Vector<>();
				Vector<Integer> inActiveVec = new Vector<>();

				for(int i=0;i<ANew.numRows();i++) {
					boolean add = false;
					for(int j=0;j<activeIneqLog.length;j++) {
						if(activeIneqLog[j]==i) {
							add = true;
							break;
						}
					}

					if(add) {
						activeVec.addElement(i);
					} else {
						inActiveVec.addElement(i);
					}
				}
				activeIneqInd 			= activeVec.stream().mapToInt(Integer::intValue).toArray();
				int[] notActiveIneqInd 	= inActiveVec.stream().mapToInt(Integer::intValue).toArray();
				//Line 78
				Vector<int[]> possibleEdges = new Vector<int[]>();
				if(dim != 1) {
					possibleEdges = nchoosek(activeIneqInd, dim-1);
					Collections.reverse(possibleEdges);
				}  else {
					possibleEdges.addElement(new int[0]);
				}

				for(int[] edge:possibleEdges) {
					SimpleMatrix stayingConditions = getRows(ANew, edge);

					if(isPartOfInvertible(stayingConditions)) {
						boolean foundRemainingOld = false;
						Vector<Integer> search = arrayDiff(activeIneqInd, edge);
						if(search.isEmpty()){
							continue;
						}
						Collections.sort(search);
						int searchStep = 0;

						while(!foundRemainingOld && searchStep < search.size()) {
							SimpleMatrix s = stayingConditions.combine(stayingConditions.numRows(), 0, getRow(ANew, search.elementAt(searchStep)));
							//s = matRound(s,6);
							foundRemainingOld = isPartOfInvertible(s);
							searchStep++;
						}
						if(!foundRemainingOld) {
							System.err.println("NO INEQ "+ k + " : "+Arrays.toString(edge));
							continue;
							//I stopped this because it was causing errors that weren't errors but rather correctly filtered equations
							//throw new LPSetException("Could not find the remaining inequality");
						}

						int old = search.get(searchStep-1);
						SimpleMatrix oldConditionA = getRow(ANew, old);
						SimpleMatrix possibleNewConditionsA = getRows(ANew, notActiveIneqInd);
						SimpleMatrix possibleNewConditionsb = getRows(bNew, notActiveIneqInd);

						SimpleMatrix standard = rref(stayingConditions, true);
						SimpleMatrix diagonal = standard.extractDiag();

						int[] lambdaCoordinate = getFullfillingIndices(diagonal, d->(Math.abs(d)<=Main.LOW_F_POINT_PRECISION));
						if(lambdaCoordinate.length == 0) {
							lambdaCoordinate = new int[] {dim-1};
						}
						lambdaCoordinate = new int[]{lambdaCoordinate[0]};
						int trueLambdaCoordinate = lambdaCoordinate[0];
						int[] nonLambdaCoordinate = IntStream.range(0, dim).filter(n->n!=trueLambdaCoordinate).sorted().toArray();
						SimpleMatrix optimizationVectorDependent = getColumns(standard, nonLambdaCoordinate).solve(getColumns(standard, lambdaCoordinate)).scale(-1);
						SimpleMatrix optimizationVector = new SimpleMatrix(dim, 1);
						
						for(int i=0;i<nonLambdaCoordinate.length;i++) {
							optimizationVector.set(nonLambdaCoordinate[i],optimizationVectorDependent.get(i));
						}
						
						for(int i:lambdaCoordinate) {
							optimizationVector.set(i, 1);
						}

						SimpleMatrix vec = oldConditionA.mult(optimizationVector);
						int[] pos = getFullfillingIndices(vec, d -> d>Main.LOW_F_POINT_PRECISION);
						if(pos.length==vec.getNumElements()) {
							optimizationVector = optimizationVector.scale(-1);
						}

						if(matchAll(getRows(ANew,activeIneqInd).mult(optimizationVector), d -> d<Main.LOW_F_POINT_PRECISION)) {
							SimpleMatrix stock = possibleNewConditionsb.minus(possibleNewConditionsA.mult(x));
							SimpleMatrix costs = possibleNewConditionsA.mult(optimizationVector);
							int[] restrictionInd = getFullfillingIndices(costs, d->d>0);
							double stepLenght = matMin(getRows(stock.elementDiv(costs),restrictionInd)).minElement;

							SimpleMatrix xNew = x.plus(optimizationVector.scale(stepLenght));
							SimpleMatrix ones = new SimpleMatrix(1, extremalPointsOld.numCols());
							ones.set(1);
							//Line 146
							SimpleMatrix temp = matAbs(extremalPointsOld.minus(xNew.mult(ones))).transpose();
							/*
							 * temp = temp.transpose();
							int[] tInd = getFullfillingRows(temp, d->d<Main.F_POINT_PRECISION);
							// If there is no such column add xNew
							if(tInd.length == 0) {
								extremalPointsOld = extremalPointsOld.combine(0, extremalPointsOld.numCols(), xNew);
							}
							 */
							
							int[] tInd = getFullfillingRows(temp, d->Math.abs(d)>=Main.LOW_F_POINT_PRECISION);
							if(tInd.length == temp.numRows()) {
								extremalPointsOld = extremalPointsOld.combine(0, extremalPointsOld.numCols(), xNew);
							}
						}
					}
				}
			}
		} else {
			extremalPointsOld = new SimpleMatrix(0, 1);
		}
		
		SimpleMatrix extremalPointsInequalities = Considerer.ConsiderEquations(extremalPointsOld,getRows(ASearchE, equationInd),getRows(bSearchE, equationInd),clearedInequalities);
		SimpleMatrix extremalPoints 			= Considerer.ConsiderEquations(extremalPointsInequalities, Aeq, beq, clearedEqalities);

		extremalPoints = matAbs(extremalPoints);
		extremalPoints = matRound(extremalPoints, 6);
		extremalPoints = sortColumns(extremalPoints);

		return compress(extremalPoints,scenario.getAgentCount());
	}

	private Vector<Allocation> compress(SimpleMatrix extremalPoints, int numAgents) {
		Vector<Allocation> out = new Vector<>();
		for(int i=0;i<extremalPoints.numCols();i++) {
			SimpleMatrix extremalPointsTrans = getColumn(extremalPoints, i);
			extremalPointsTrans.reshape(numAgents, numAgents);

			double[][] alloc = new double[numAgents][numAgents];
			for(int j=0;j<numAgents;j++) {
				for(int k=0;k<numAgents;k++) {
					alloc[j][k] = extremalPointsTrans.get(j, k);
				}
			}
			out.addElement(new Allocation(alloc));
		}
		return out;
	}

	@Override
	public List<String> getViolatedConstraints(Scenario scenario) {
		List<String> out = AllocationStrategyFamily.super.getViolatedConstraints(scenario);

		if(scenario.getAgentCount()!=scenario.getObjectCount()) {
			out.add("Number of agents must be equal to te number of objects");
		}
		
		if(scenario.getAgentCount() > 10) {
			out.add("Problem size to big.");
		}

		return out;
	}
}
