package ru.tinkoff.qa.neptune.data.base.test.persistable.object.operations.select;

import org.testng.annotations.Test;
import ru.tinkoff.qa.neptune.data.base.api.query.*;
import ru.tinkoff.qa.neptune.data.base.api.test.*;
import ru.tinkoff.qa.neptune.data.base.test.persistable.object.operations.BaseDbOperationTest;
import ru.tinkoff.qa.neptune.data.base.test.persistable.object.operations.ConnectionDataSupplierForTestBase2;

import java.time.Duration;
import java.util.function.Supplier;

import static java.lang.System.currentTimeMillis;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.tinkoff.qa.neptune.core.api.steps.StoryWriter.condition;
import static ru.tinkoff.qa.neptune.data.base.api.connection.data.DBConnectionStore.getKnownConnection;
import static ru.tinkoff.qa.neptune.data.base.api.properties.WaitingForQueryResultDuration.QueryTimeUnitProperties.WAITING_FOR_SELECTION_RESULT_TIME_UNIT;
import static ru.tinkoff.qa.neptune.data.base.api.properties.WaitingForQueryResultDuration.QueryTimeValueProperties.WAITING_FOR_SELECTION_RESULT_TIME_VALUE;
import static ru.tinkoff.qa.neptune.data.base.api.query.GetSelectedFunction.selected;
import static ru.tinkoff.qa.neptune.data.base.api.query.SelectListGetSupplier.listOfTypeByIds;
import static ru.tinkoff.qa.neptune.data.base.api.query.SelectOneGetSupplier.aSingleOfTypeById;

public class SelectByIds extends BaseDbOperationTest {

    private static final Supplier<NothingIsSelectedException> TEST_SUPPLIER = () ->
            new NothingIsSelectedException("Test exception");

    @Test
    public void selectListTest() {
        var catalogItems = dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, -3, "0-930267-39-7", "0-671-73246-3")));
        assertThat(catalogItems, hasSize(2));
        assertThat(catalogItems.stream().map(Catalog::getIsbn).collect(toList()),
                contains("0-930267-39-7", "0-671-73246-3"));
    }

    @Test
    public void selectOneTest() {
        var catalogItem = dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, "0-671-73246-3")));
        assertThat(catalogItem.getIsbn(), is("0-671-73246-3"));
        assertThat(catalogItem.getPublisher().getName(), is("Simon & Schuster"));
    }

    @Test
    public void selectListTestByCondition() {
        var catalogItems = dataBaseSteps.get(selected(
                listOfTypeByIds(Catalog.class, -3, "0-671-73246-3", "0-930267-39-7")
                        .criteria("A book with title `Ruslan and Ludmila`", catalog -> catalog
                                .getBook().getName().equalsIgnoreCase("ruslan and ludmila"))));
        assertThat(catalogItems, hasSize(1));
        var catalogItem = catalogItems.get(0);
        assertThat(catalogItem.getBook().getName(), equalTo("Ruslan and Ludmila"));
    }

    @Test
    public void selectOneTestByCondition() {
        var author = dataBaseSteps.get(selected(aSingleOfTypeById(Author.class, 1)
                .criteria("Wrote `Ruslan and Ludmila`", author1 -> null != author1.getBooks().stream()
                                .filter(book -> "ruslan and ludmila".equalsIgnoreCase(book.getName()))
                                .findFirst().orElse(null))));
        assertThat(author.getLastName(), equalTo("Pushkin"));
    }

    @Test
    public void selectEmptyListTestWithDefaultTime() {
        long start = currentTimeMillis();
        var catalogItems = dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, -1, -2)));
        long end = currentTimeMillis();

        Duration fiveSeconds = ofSeconds(5);
        assertThat(catalogItems, hasSize(0));
        assertThat(end - start, greaterThanOrEqualTo(fiveSeconds.toMillis()));
        assertThat(end - start - fiveSeconds.toMillis(), lessThanOrEqualTo(700L));
    }

    @Test
    public void selectNullTestWithDefaultTime() {
        long start = currentTimeMillis();
        var catalogItem = dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, -2)));
        long end = currentTimeMillis();

        Duration fiveSeconds = ofSeconds(5);
        assertThat(catalogItem, nullValue());
        assertThat(end - start, greaterThanOrEqualTo(fiveSeconds.toMillis()));
        assertThat(end - start - fiveSeconds.toMillis(), lessThanOrEqualTo(700L));
    }

    @Test
    public void selectEmptyListByIdWithDefinedTime() {
        Duration sixSeconds = ofSeconds(6);
        long start = currentTimeMillis();
        var catalogItems = dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, -1, -2)
                .timeOut(sixSeconds)));
        long end = currentTimeMillis();

        assertThat(catalogItems, hasSize(0));
        assertThat(end - start, greaterThanOrEqualTo(sixSeconds.toMillis()));
        assertThat(end - start - sixSeconds.toMillis(), lessThanOrEqualTo(700L));
    }

    @Test
    public void selectNullByIdWithDefinedTime() {
        Duration sixSeconds = ofSeconds(6);
        long start = currentTimeMillis();
        var catalogItem = dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, -2).timeOut(sixSeconds)));
        long end = currentTimeMillis();

        assertThat(catalogItem, nullValue());
        assertThat(end - start, greaterThanOrEqualTo(sixSeconds.toMillis()));
        assertThat(end - start - sixSeconds.toMillis(), lessThanOrEqualTo(700L));
    }

    @Test
    public void selectEmptyListByIdyWithTimeDefinedByProperty() {
        WAITING_FOR_SELECTION_RESULT_TIME_UNIT.accept("SECONDS");
        WAITING_FOR_SELECTION_RESULT_TIME_VALUE.accept("2");

        Duration twoSeconds = ofSeconds(2);
        long start = currentTimeMillis();
        var catalogItems = dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, -1, -2)));
        long end = currentTimeMillis();

        try {
            assertThat(catalogItems, hasSize(0));
            assertThat(end - start, greaterThanOrEqualTo(twoSeconds.toMillis()));
            assertThat(end - start - twoSeconds.toMillis(), lessThanOrEqualTo(700L));
        }
        finally {
            System.getProperties().remove(WAITING_FOR_SELECTION_RESULT_TIME_UNIT.getPropertyName());
            System.getProperties().remove(WAITING_FOR_SELECTION_RESULT_TIME_VALUE.getPropertyName());
        }
    }

    @Test
    public void selectNullByIdWithTimeDefinedByProperty() {
        WAITING_FOR_SELECTION_RESULT_TIME_UNIT.accept("SECONDS");
        WAITING_FOR_SELECTION_RESULT_TIME_VALUE.accept("2");

        Duration twoSeconds = ofSeconds(2);
        long start = currentTimeMillis();
        var catalogItem = dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, -2)));
        long end = currentTimeMillis();

        try {
            assertThat(catalogItem, nullValue());
            assertThat(end - start, greaterThanOrEqualTo(twoSeconds.toMillis()));
            assertThat(end - start - twoSeconds.toMillis(), lessThanOrEqualTo(700L));
        }
        finally {
            System.getProperties().remove(WAITING_FOR_SELECTION_RESULT_TIME_UNIT.getPropertyName());
            System.getProperties().remove(WAITING_FOR_SELECTION_RESULT_TIME_VALUE.getPropertyName());
        }
    }

    @Test
    public void selectEmptyListByIdAndConditionWithDefaultTime() {
        long start = currentTimeMillis();
        var catalogItems = dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, 1)
                .criteria("ISBN is 0-671-73246-3", catalog -> catalog
                        .getIsbn().equals("0-671-73246-3"))));
        long end = currentTimeMillis();

        Duration fiveSeconds = ofSeconds(5);
        assertThat(catalogItems, hasSize(0));
        assertThat(end - start, greaterThanOrEqualTo(fiveSeconds.toMillis()));
        assertThat(end - start - fiveSeconds.toMillis(), lessThanOrEqualTo(700L));
    }

    @Test
    public void selectNullByIdAndConditionWithDefaultTime() {
        long start = currentTimeMillis();
        var catalogItem = dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, 1)
                .criteria("ISBN is 0-671-73246-3", catalog -> catalog
                        .getIsbn().equals("0-671-73246-3"))));
        long end = currentTimeMillis();

        Duration fiveSeconds = ofSeconds(5);
        assertThat(catalogItem, nullValue());
        assertThat(end - start, greaterThanOrEqualTo(fiveSeconds.toMillis()));
        assertThat(end - start - fiveSeconds.toMillis(), lessThanOrEqualTo(700L));
    }

    @Test
    public void selectEmptyListByIdAndConditionWithDefinedTime() {
        Duration sixSeconds = ofSeconds(6);
        long start = currentTimeMillis();
        var catalogItems = dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, 1)
                .criteria("ISBN is 0-671-73246-3", catalog -> catalog
                        .getIsbn().equals("0-671-73246-3"))
                .timeOut(sixSeconds)));
        long end = currentTimeMillis();

        assertThat(catalogItems, hasSize(0));
        assertThat(end - start, greaterThanOrEqualTo(sixSeconds.toMillis()));
        assertThat(end - start - sixSeconds.toMillis(), lessThanOrEqualTo(700L));
    }

    @Test
    public void selectNullByIdAndConditionWithDefinedTime() {
        Duration sixSeconds = ofSeconds(6);
        long start = currentTimeMillis();
        var catalogItem = dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, 1)
                .criteria("ISBN is 0-671-73246-3", catalog -> catalog.getIsbn().equals("0-671-73246-3"))
                .timeOut(sixSeconds)));
        long end = currentTimeMillis();

        assertThat(catalogItem, nullValue());
        assertThat(end - start, greaterThanOrEqualTo(sixSeconds.toMillis()));
        assertThat(end - start - sixSeconds.toMillis(), lessThanOrEqualTo(700L));
    }

    @Test
    public void selectEmptyListByIdAndConditionWithTimeDefinedByProperty() {
        WAITING_FOR_SELECTION_RESULT_TIME_UNIT.accept( "SECONDS");
        WAITING_FOR_SELECTION_RESULT_TIME_VALUE.accept("2");

        Duration twoSeconds = ofSeconds(2);
        long start = currentTimeMillis();
        var catalogItems = dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, 1)
                .criteria("ISBN is 0-671-73246-3", catalog -> catalog.getIsbn().equals("0-671-73246-3"))));
        long end = currentTimeMillis();

        try {
            assertThat(catalogItems, hasSize(0));
            assertThat(end - start, greaterThanOrEqualTo(twoSeconds.toMillis()));
            assertThat(end - start - twoSeconds.toMillis(), lessThanOrEqualTo(500L));
        }
        finally {
            System.getProperties().remove(WAITING_FOR_SELECTION_RESULT_TIME_UNIT.getPropertyName());
            System.getProperties().remove(WAITING_FOR_SELECTION_RESULT_TIME_VALUE.getPropertyName());
        }
    }

    @Test
    public void selectNullByIdAndConditionWithTimeDefinedByProperty() {
        WAITING_FOR_SELECTION_RESULT_TIME_UNIT.accept("SECONDS");
        WAITING_FOR_SELECTION_RESULT_TIME_VALUE.accept("2");

        Duration twoSeconds = ofSeconds(2);
        long start = currentTimeMillis();
        var catalogItem = dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, 1)
                .criteria("ISBN is 0-671-73246-3", catalog -> catalog.getIsbn().equals("0-671-73246-3"))));
        long end = currentTimeMillis();

        try {
            assertThat(catalogItem, nullValue());
            assertThat(end - start, greaterThanOrEqualTo(twoSeconds.toMillis()));
            assertThat(end - start - twoSeconds.toMillis(), lessThanOrEqualTo(500L));
        }
        finally {
            System.getProperties().remove(WAITING_FOR_SELECTION_RESULT_TIME_UNIT.getPropertyName());
            System.getProperties().remove(WAITING_FOR_SELECTION_RESULT_TIME_VALUE.getPropertyName());
        }
    }

    @Test(expectedExceptions = NothingIsSelectedException.class, expectedExceptionsMessageRegExp = "Test exception")
    public void selectEmptyListByIdWithExceptionThrowing() {
        dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, -1)
                .throwWhenResultEmpty(TEST_SUPPLIER)));
    }

    @Test(expectedExceptions = NothingIsSelectedException.class, expectedExceptionsMessageRegExp = "Test exception")
    public void selectNullByIdWithExceptionThrowing() {
        dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, -1)
                .throwWhenResultEmpty(TEST_SUPPLIER)));
    }

    @Test(expectedExceptions = NothingIsSelectedException.class, expectedExceptionsMessageRegExp = "Test exception")
    public void selectEmptyListByIdAndConditionWithExceptionThrowing() {
        dataBaseSteps.get(selected(listOfTypeByIds(Catalog.class, 1)
                .criteria("ISBN is 0-671-73246-3",
                        catalog -> catalog.getIsbn().equals("0-671-73246-3"))
                .throwWhenResultEmpty(TEST_SUPPLIER)));
    }

    @Test(expectedExceptions = NothingIsSelectedException.class, expectedExceptionsMessageRegExp = "Test exception")
    public void selectNullByIdAndConditionWithExceptionThrowing() {
        dataBaseSteps.get(selected(aSingleOfTypeById(Catalog.class, 1)
                .criteria("ISBN is 0-671-73246-3", catalog -> catalog
                        .getIsbn().equals("0-671-73246-3"))
                .throwWhenResultEmpty(TEST_SUPPLIER)));
    }

    @Test
    public void selectOfListWithConnectionDataSupplier() {
        var query = selected(listOfTypeByIds(Catalog.class, "0-930267-39-7"))
                .useConnection(ConnectionDataSupplierForTestBase2.class);
        var query2 = selected(listOfTypeByIds(Catalog.class, "0-930267-39-7"));

        assertThat(dataBaseSteps.get(query), hasSize(0));
        assertThat(dataBaseSteps.get(query2), hasSize(1));
    }

    @Test
    public void selectOfOneWithConnectionDataSupplier() {
        var query = selected(aSingleOfTypeById(Catalog.class, "0-930267-39-7"))
                .useConnection(ConnectionDataSupplierForTestBase2.class);
        var query2 = selected(aSingleOfTypeById(Catalog.class, "0-930267-39-7"));

        assertThat(dataBaseSteps.get(query), nullValue());
        assertThat(dataBaseSteps.get(query2), not(nullValue()));
    }

    @Test
    public void selectOfListWithConnection() {
        var query = selected(listOfTypeByIds(Catalog.class, "0-930267-39-7"))
                .useConnection(getKnownConnection(ConnectionDataSupplierForTestBase2.class, true));
        var query2 = selected(listOfTypeByIds(Catalog.class, "0-930267-39-7"));

        assertThat(dataBaseSteps.get(query), hasSize(0));
        assertThat(dataBaseSteps.get(query2), hasSize(1));
    }

    @Test
    public void selectOfOneWithConnectionChangeByPersistenceManagerFactory() {
        var query = selected(aSingleOfTypeById(Catalog.class, "0-930267-39-7"))
                .useConnection(getKnownConnection(ConnectionDataSupplierForTestBase2.class,true));
        var query2 = selected(aSingleOfTypeById(Catalog.class, "0-930267-39-7"));

        assertThat(dataBaseSteps.get(query), nullValue());
        assertThat(dataBaseSteps.get(query2), not(nullValue()));
    }
}
