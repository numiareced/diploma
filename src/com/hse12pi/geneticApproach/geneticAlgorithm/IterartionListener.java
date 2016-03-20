
package com.hse12pi.geneticApproach.geneticAlgorithm;

public interface IterartionListener<C extends Chromosome<C>, T extends Comparable<T>> {

    void update( GeneticAlgorithm<C, T> environment );
    
}
