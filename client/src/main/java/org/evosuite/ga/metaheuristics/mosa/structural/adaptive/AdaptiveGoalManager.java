package org.evosuite.ga.metaheuristics.mosa.structural.adaptive;

import org.apache.commons.lang3.ArrayUtils;
import org.evosuite.Properties;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.line.LineCoverageTestFitness;
import org.evosuite.coverage.method.MethodCoverageFactory;
import org.evosuite.coverage.mutation.WeakMutationTestFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.archive.CoverageArchive;
import org.evosuite.ga.comparators.PerformanceScoreComparator;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.mosa.structural.MultiCriteriaManager;
import org.evosuite.performance.AbstractIndicator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The version of the budget manager needed for the adaptive version (implement the different calculation of the
 * fitness function)
 * todo-gio: is this now the right place for the computation of the fitness function?
 * @author Annibale Panichella, Giovanni Grano
 */
public class AdaptiveGoalManager extends MultiCriteriaManager {

    private static final Logger logger = LoggerFactory.getLogger(AdaptiveGoalManager.class);

    // todo-gio: check those
    protected final Map<Integer, TestFitnessFunction> lineMap = new LinkedHashMap<>();
    protected final Map<Integer, TestFitnessFunction> weakMutationMap = new LinkedHashMap<>();

    // todo-gio: we can replace this one!
    private final PerformanceScoreComparator comparator = new PerformanceScoreComparator();

    /**
     * Stores the best values to check for heuristic stagnation
     */
    private final Map<TestFitnessFunction, Double> bestValues;
    private boolean hasBetterObjectives = false;
    protected List<AbstractIndicator> indicators;

    public AdaptiveGoalManager(List<TestFitnessFunction> fitnessFunctions) {
        super(fitnessFunctions);
        this.bestValues = new HashMap<>();
        for (TestFitnessFunction f : fitnessFunctions) {
            if (f instanceof LineCoverageTestFitness)
                lineMap.put(((LineCoverageTestFitness) f).getLine(), f);
            else if (f instanceof WeakMutationTestFitness) {
                weakMutationMap.put(((WeakMutationTestFitness) f).getMutation().getId(), f);
            }
        }
    }

    public void runTest(TestChromosome c) {
        if (c.getLastExecutionResult() == null) {
            // run the test
            TestCase test = c.getTestCase();
            ExecutionResult result = TestCaseExecutor.runTest(test);
            c.setLastExecutionResult(result);
            c.setChanged(false);
        }
    }

    @Override
    //todo-gio: this is the new interface for the calculate fitness that I should implement
    public void calculateFitness(TestChromosome c, GeneticAlgorithm<TestChromosome> ga) {
        super.calculateFitness(c, ga);

    }

//    @Override
//    public void calculateFitness(TestChromosome c) {
//        this.runTest(c);
//
//        ExecutionResult result = c.getLastExecutionResult();
//        computePerformanceMetrics(c);
//
//        /* check exceptions and if the test does not cover anything */
//        if (result.hasTimeout() || result.hasTestException() || result.getTrace().getCoveredLines().size() == 0) {
//            for (FitnessFunction<T> f : currentGoals)
//                c.setFitness(f, Double.MAX_VALUE);
//
//            c.setPerformanceScore(Double.MAX_VALUE);
//            return;
//        }
//
//        /* ------------------------------------- update of best values ----------------------------------- */
//        // 1) we update the set of currents goals
//        Set<TestFitnessFunction> visitedTargets = new LinkedHashSet<>(getUncoveredGoals().size() * 2);
//        LinkedList<TestFitnessFunction> targets = new LinkedList<>(this.currentGoals);
//
//        boolean toArchive = false;
//
//        while (targets.size() > 0) {
//            TestFitnessFunction fitnessFunction = targets.poll();
//
//            int past_size = visitedTargets.size();
//            visitedTargets.add(fitnessFunction);
//            if (past_size == visitedTargets.size())
//                continue;
//
//            double value = fitnessFunction.getFitness(c);
//            if (bestValues.get(fitnessFunction) == null || value < bestValues.get(fitnessFunction)) {
//                bestValues.put(fitnessFunction, value);
//                this.hasBetterObjectives = true;
//                toArchive = true;
//            }
//
//            if (value == 0.0) {
//                toArchive = true;
//                updateCoveredGoals(fitnessFunction, c);
//                this.bestValues.remove(fitnessFunction);
//                if (fitnessFunction instanceof BranchCoverageTestFitness) {
//                    for (TestFitnessFunction child : graph.getStructuralChildren(fitnessFunction)) {
//                        targets.addLast(child);
//                    }
//                    for (TestFitnessFunction dependentTarget : dependencies.get(fitnessFunction)) {
//                        targets.addLast(dependentTarget);
//                    }
//                }
//            } else {
//                currentGoals.add(fitnessFunction);
//            }
//
//        }
//        currentGoals.removeAll(this.getCoveredGoals());
//        /* update of the archives */
//        if (toArchive)
//            updateArchive(c, result);
//    }

//    @Override
//    public void updateArchive(TestChromosome c, ExecutionResult result) {
//        // todo-gio: there is no update archive anymore in the super class
//        super.updateArchive(c, result);
//        if (ArrayUtils.contains(Properties.CRITERION, Properties.Criterion.LINE)) {
//            for (Integer line : result.getTrace().getCoveredLines()) {
//                updateCoveredGoals(this.lineMap.get(line), c);
//            }
//        }
//        if (ArrayUtils.contains(Properties.CRITERION, Properties.Criterion.WEAKMUTATION)) {
//            for (Integer id : result.getTrace().getInfectedMutants()) {
//                if (this.weakMutationMap.containsKey(id))
//                    updateCoveredGoals(this.weakMutationMap.get(id), c);
//            }
//        }
//        if (ArrayUtils.contains(Properties.CRITERION, Properties.Criterion.METHOD)) {
//            for (String id : result.getTrace().getCoveredMethods()) {
//                TestFitnessFunction ff = MethodCoverageFactory.createMethodTestFitness(Properties.TARGET_CLASS, id);
//                updateCoveredGoals(ff, c);
//            }
//        }
//    }

    /**
     * We overrides here the default behavior that looks at the size for the update of the archive!
     * Here, we look at the min-max normalized performance score; the higher the better.
     * Consider to use the base implementation providing some comparators.
     * todo-gio: we don't need this; just define a new secondary objective with the performance indicators
     * (we can maybe set this in the constructor of the adaptiveDynaMOSA class
     */
//    @Override
//    @SuppressWarnings("Duplicates")
//    protected void updateCoveredGoals(FitnessFunction<T> f, T tc) {
//        TestChromosome tch = (TestChromosome) tc;
//        tch.getTestCase().getCoveredGoals().add((TestFitnessFunction) f);
//
//        // update covered targets
//        T best = coveredGoals.get(f);
//        if (best == null) {
//            coveredGoals.put(f, tc);
//            uncoveredGoals.remove(f);
//            currentGoals.remove(f);
//            CoverageArchive.getArchiveInstance().updateArchive((TestFitnessFunction) f, tch, tc.getFitness(f));
//        } else {
//            boolean toUpdate = comparator.compare(tc, best) == -1;
//            if (toUpdate) {
//                coveredGoals.put(f, tc);
//                CoverageArchive.getArchiveInstance().updateArchive((TestFitnessFunction) f, tch, tc.getFitness(f));
//            }
//        }
//    }

    //todo-gio: handle there the update of the archive
    @Override
    protected void updateCoveredGoals(TestFitnessFunction f, TestChromosome tc) {
        super.updateCoveredGoals(f, tc);
    }

    /**
     * Updates the best value map
     *
     * @param function the fitness function
     * @param fitness  the currently best value
     */
    public void updateBestValue(TestFitnessFunction function, Double fitness) {
        this.bestValues.put(function, fitness);
    }

    public Map<TestFitnessFunction, Double> getBestValues() {
        return bestValues;
    }

    /**
     * Returns the flag for better or worst objective reached
     */
    public boolean hasBetterObjectives() {
        return hasBetterObjectives;
    }

    public void setHasBetterObjectives(boolean hasBetterObjectives) {
        this.hasBetterObjectives = hasBetterObjectives;
    }

    public void setIndicators(List<AbstractIndicator> indicators) {
        this.indicators = indicators;
    }

    public void computePerformanceMetrics(TestChromosome test) {
        if (test.getIndicatorValues().size() > 0)
            return;

        double sum = 0.0;
        for (AbstractIndicator indicator : this.indicators) {
            double value = indicator.getIndicatorValue(test);
            sum += value / (value + 1);
        }
        test.setPerformanceScore(sum);
        logger.debug("performance score for {} = {}", test.hashCode(), test.getPerformanceScore());
    }

//    @Override
//    public Set<T> getArchive() {
//        Set<T> set = new HashSet<>();
//        for (TestChromosome tch : CoverageArchive.getArchiveInstance().getSolutions()) {
//            set.add((T) tch);
//        }
//        return set;
//    }
}
