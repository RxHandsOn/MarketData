package com.handson.infra;

import com.handson.infra.RandomSequenceGenerator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RandomSequenceGeneratorTest {

    @Test
    public void should_generate_a_bounded_number() {
        // given
        double min = 3.14;
        double max = 5.42;
        RandomSequenceGenerator generator = new RandomSequenceGenerator(min, max);
        // when
        double next = generator.computeNextNumber(4);
        // then
        assertThat(next).isBetween(min, max);
    }

    @Test
    public void should_generate_a_bounded_number_close_to_previous() {
        // given
        double min = 3;
        double max = 5;
        RandomSequenceGenerator generator = new RandomSequenceGenerator(min, max);
        // when
        int previous = 4;
        double next = generator.computeNextNumber(previous);
        // then
        assertThat(next).isBetween(previous - (max-min)/10, previous + (max-min)/10);
    }
}