package org.evosuite.testcase.secondaryobjectives;

import org.evosuite.ga.SecondaryObjective;
import org.evosuite.testsuite.TestSuiteChromosome;

public class PerformanceSecondaryObjective extends SecondaryObjective<TestSuiteChromosome> {

    @Override
    public int compareChromosomes(TestSuiteChromosome chromosome1, TestSuiteChromosome chromosome2) {
        logger.debug("Comparing performance scores: " + chromosome1.size() + " vs "
                + chromosome2.size());
        double diff = chromosome1.getPerformanceScore() - chromosome2.getPerformanceScore();
        return diff <= 0 ? -1 : +1;
    }

    @Override
    public int compareGenerations(TestSuiteChromosome parent1, TestSuiteChromosome parent2,
                                  TestSuiteChromosome child1, TestSuiteChromosome child2) {
        logger.debug("Comparing performance scores: " + parent1.size() + ", " + parent1.size()
                + " vs " + child1.size() + ", " + child2.size());
        double diff = Math.min(parent1.size(), parent2.size())
                - Math.min(child1.size(), child2.size());
        return diff <= 0 ? -1 : +1;
    }
}
