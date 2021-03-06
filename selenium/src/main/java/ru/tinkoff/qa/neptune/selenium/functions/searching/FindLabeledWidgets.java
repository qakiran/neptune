package ru.tinkoff.qa.neptune.selenium.functions.searching;

import ru.tinkoff.qa.neptune.selenium.api.widget.Labeled;
import ru.tinkoff.qa.neptune.selenium.api.widget.Widget;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static ru.tinkoff.qa.neptune.selenium.functions.searching.FindByBuilder.getAnnotations;

class FindLabeledWidgets<R extends Widget> extends FindWidgets<R> {

    private FindLabeledWidgets(Class<R> classOfAWidget) {
        super(classOfAWidget, clazz -> !Modifier.isAbstract(clazz.getModifiers())
                && Labeled.class.isAssignableFrom(clazz)

                && nonNull(getAnnotations(clazz))

                && (Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constructor -> {
                    var parameters = constructor.getParameterTypes();
                    return parameters.length == 1 &&
                            WebElement.class.isAssignableFrom(parameters[0]);
                }).collect(toList()).size() > 0));
    }

    List<Class<? extends R>> getSubclasses() {
        var resultList = findSubclasses();

        if (resultList.size() > 0) {
            return resultList;
        }
        throw new IllegalArgumentException(String.format("There is no any non-abstract subclass of %s that " +
                        "is annotated by any org.openqa.selenium.support.Find* annotation " +
                        "and has a constructor with only one parameter of a type extending %s. " +
                        "Also convenient classes should implement %s",
                Widget.getWidgetName(classOfAWidget), WebElement.class.getName(), Labeled.class.getName()));
    }

    static <R extends Widget> FindLabeledWidgets<R> labeledWidgets(Class<R> classOfAWidget) {
        return new FindLabeledWidgets<>(classOfAWidget);
    }
}
