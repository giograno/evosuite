package org.evosuite.ga.metaheuristics.ibea.crossover;//package org.evosuite.ga.operators;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.operators.crossover.CrossOverFunction;
import org.evosuite.utils.Randomness;


public class SBXCrossover<T extends Chromosome> extends CrossOverFunction {

    /**
     * EPS defines the minimum difference allowed between real values
     */
    private static final double EPS = 1e-10;
    private static final double ETA_C_DEFAULT_ = 20.0;

    @Override
    public void crossOver(Chromosome parent1, Chromosome parent2) throws ConstructionFailedException {
        Chromosome t1 = parent1.clone();
        Chromosome t2 = parent2.clone();

        int upperBound = Math.min(parent1.size(), parent2.size()); // max number of Genes
        int lowerBound = 0;

        int[] result = doCrossover(parent1.size(), parent2.size(), 0, upperBound);

        parent1.crossOver(t2, result[0], result[1]);
        parent2.crossOver(t1, result[1], result[0]);
    }

    private int[] doCrossover(double size1, double size2, double lowerBound, double upperBound) {
        double distributionIndex = ETA_C_DEFAULT_;

        double rand;
        double y1, y2, yL, yu;
        double c1, c2;
        double alpha, beta, betaq;
        double valueX1, valueX2;

        //TODO : check if this is correct
        yL = lowerBound;
        yu = upperBound;

        //TODO : check if this is correct
        valueX1 = size1;
        valueX2 = size2;

        if (Math.abs(valueX1 - valueX2) > EPS) {
            if (valueX1 < valueX2) {
                y1 = valueX1;
                y2 = valueX2;
            } else {
                y1 = valueX2;
                y2 = valueX1;
            }

            rand = Randomness.nextDouble();
            beta = 1.0 + (2.0 * (y1 - yL) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha))
                betaq = Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            else
                betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (distributionIndex + 1.0)));

            c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
            beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha))
                betaq = Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            else
                betaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (distributionIndex + 1.0)));

            c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));

            if (c1 < yL)
                c1 = yL;
            if (c2 < yL)
                c2 = yL;

            if (c1 > yu)
                c1 = yu;
            if (c2 > yu)
                c2 = yu;

            if (Randomness.nextDouble() <= 0.5) {
                valueX1 = c2;
                valueX2 = c1;
            } else {
                valueX1 = c1;
                valueX2 = c2;
            }
        }
        return new int[]{(int) valueX1, (int) valueX2};
    }

}
