package ru.tinkoff.qa.neptune.data.base.api;

import java.io.Serializable;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;

/**
 * This class is designed to implement classes of composite key objects.
 * It is supposed to be used at the case below
 * {@code @PersistenceCapable(table = "TABLE_NAME", objectIdClass = CompositeKeySubclass.class)}
 *
 * WARNING: it is necessary to override following methods:
 * <p>{@link #hashCode()}</p>
 * <p>{@link #equals(Object)}</p>
 * <p>{@link #toString()}</p>
 *
 * It is enough
 * <p>
 *     {@code 'public int hashCode() {
 *             return super.hashCode();
 *            }'}
 * </p>
 */
public abstract class CompositeKey extends OrmObject implements Serializable {

    @Override
    public int hashCode() {
        var result = 0;
        Class<?> clazz = this.getClass();
        while (!clazz.equals(Object.class)) {
            var fields = stream(clazz.getDeclaredFields()).filter(field -> !isStatic(field.getModifiers()))
                    .collect(Collectors.toList());
            for (var f : fields) {
                f.setAccessible(true);
                try {
                    var v = f.get(this);
                    if (v == null) {
                        continue;
                    }
                    result = result ^ v.hashCode();
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return result;
    }

    @Override
    public String toString() {
        return format("%s:%s", this.getClass(), hashCode());
    }
}
