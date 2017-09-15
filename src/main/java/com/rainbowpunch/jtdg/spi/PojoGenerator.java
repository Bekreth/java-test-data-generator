package com.rainbowpunch.jtdg.spi;

import com.rainbowpunch.jtdg.core.DataGenerator;
import com.rainbowpunch.jtdg.core.DefaultPojoAnalyzer;
import com.rainbowpunch.jtdg.core.PojoAnalyzer;
import com.rainbowpunch.jtdg.core.PojoAttributes;
import com.rainbowpunch.jtdg.core.limiters.Limiter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 */
public class PojoGenerator<T> {

    private Class<T> pojo;
    private PojoAnalyzer<T> pojoAnalyzer;
    private DataGenerator dataGenerator;
    private PojoAttributes<T> pojoAttributes;

    public PojoGenerator(Class<T> clazz) {
        this.pojo = clazz;
        pojoAnalyzer = new DefaultPojoAnalyzer<>();
        dataGenerator = new DataGenerator();
        pojoAttributes = new PojoAttributes<>(clazz);
    }

    public PojoGenerator<T> andLimitField(String fieldName, Limiter<?> limiter) {
        pojoAttributes.putFieldLimiter(fieldName, limiter);
        return this;
    }

    /**
     * This method populates the PojoAttributes with the needed fieldSetters and dataGenerators
     *      necessary to create Pojos on demand.
     * @return
     */
    public PojoGenerator<T> analyzePojo() {
        pojoAnalyzer.parsePojo(pojo, pojoAttributes);
        dataGenerator.populateSuppliers(pojoAttributes);
        return this;
    }

    public Stream<T> generatePojoStream() {
        return IntStream.iterate(0, i -> i + 1)
                .mapToObj(this::generatePojo);
    }

    public List<T> generatePojoList(int count) {
        return generatePojoStream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public T generatePojo() {
        return generatePojo(0);
    }

    private T generatePojo(int i) {
        try {
            T newInstance = pojo.newInstance();
            pojoAttributes.apply(newInstance);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}