package com.github.toy.constructor.testng.integration.properties;

import com.github.toy.constructor.core.api.properties.PropertySupplier;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.toy.constructor.testng.integration.properties.RefreshEachTimeBefore.METHOD_STARTING;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.List.of;

public final class TestNGRefreshStrategyProperty implements PropertySupplier<List<RefreshEachTimeBefore>> {

    private static final String PROPERTY_NAME = "testng.refresh.before";
    public static final TestNGRefreshStrategyProperty REFRESH_STRATEGY_PROPERTY = new TestNGRefreshStrategyProperty();

    private TestNGRefreshStrategyProperty() {
        super();
    }

    @Override
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * This method returns a list filled by elements of {@link RefreshEachTimeBefore}. These elements
     * should be defined by the property {@code 'testng.refresh.before'} as a comma-separated string of elements names
     * taken from the {@link RefreshEachTimeBefore}. When the property is not defined then a list filled by
     * {@link com.github.toy.constructor.testng.integration.properties.RefreshEachTimeBefore#METHOD_STARTING} is
     * returned.
     *
     * @return list of defined strategies how to invoke the
     * {@link com.github.toy.constructor.core.api.Refreshable#refresh()}. Each strategy is described by
     * elements of the {@link RefreshEachTimeBefore}.
     */
    @Override
    public List<RefreshEachTimeBefore> get() {
        return returnOptionalFromEnvironment().map(s ->
                stream(s.split(",")).map(s1 -> {
                    try {
                        return RefreshEachTimeBefore.valueOf(s1.trim());
                    }
                    catch (Throwable e) {
                        throw new IllegalArgumentException(format("Unknown refresh strategy %s. Use name " +
                                "of an element of %s", s1, RefreshEachTimeBefore.class.getName()));
                    }
                }).collect(Collectors.toList())).orElseGet(() -> of(METHOD_STARTING));
    }
}
