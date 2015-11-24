package com.alpha.quiztomizador.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.ArrayList;
import java.util.List;

public class GenericExclusionStrategy implements ExclusionStrategy {

    private final List<String> _skipFields = new ArrayList<String>();
    private final Class<?> _clazz;

    /**
     * Create a new exclusion strategy for gson.
     *
     * @param clazz
     *            The class (transfer object) on which this exclusion will work
     * @param fields
     *            The field names which should not be serialized
     */
    public GenericExclusionStrategy(Class<?> clazz, String... fields) {
        _clazz = clazz;

        for (String field : fields) {
            _skipFields.add(field);
        }
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaringClass() == _clazz
                && _skipFields.contains(f.getName());
    }
}

//GenericExclusionStrategy ges = new GenericExclusionStrategy(Person.class, "lastname");
//Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy[]{ges}).create();