package com.example.demo.util;

import com.example.demo.entities.Versionable;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.data.Offset;
import org.jetbrains.annotations.Nullable;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;

/**
 * A generic class that can generate populated beans. The data population is
 * done either by a few predefined rules, a registered mock value (see
 * {@link #registerMockValueForClass(Class, Object)}) or a mockito mock.
 *
 * @author Oleg
 */
public final class MockBeanGenerator {

	/** Prefix for setter-methods. */
	private static final String SET_METHOD = "set";

	/** Setter-method for id. */
	private static final String SET_ID_METHOD = "setId";

	/** Prefix for getter-methods. */
	private static final String GET_METHOD = "get";

	/** Prefix for getter-methods of boolean values. */
	private static final String IS_METHOD = "is";

	private static final List<String> DEFAULT_MAPPING_EXCEPTIONS = new ArrayList<>();

	/** the logger. */
	private static final Logger LOG = LoggerFactory.getLogger(MockBeanGenerator.class);
	private static final Random random = new SecureRandom();

	static {
		DEFAULT_MAPPING_EXCEPTIONS.add("getClass");
		DEFAULT_MAPPING_EXCEPTIONS.add("getVetoableChangeListeners");
		DEFAULT_MAPPING_EXCEPTIONS.add("getPropertyChangeListeners");
		DEFAULT_MAPPING_EXCEPTIONS.add("getCallbacks");
	}

	/**
	 * This is a hash where subclasses can register their own dummy values for
	 * particular classes.
	 *
	 * @see #registerMockValueForClass(Class, Object) for more details.
	 */
	private final Map<Class<?>, Object> registeredMockValues = new HashMap<>();
	/**
	 * This is a hash where subclasses can register their own dummy values for
	 * particular classes.
	 *
	 * @see #registerMockForSetterMethod(String, Object) for more details.
	 */
	private final Map<String, Object> registeredMockPropertyValues = new HashMap<>();

	/** Default constructor. */
	public MockBeanGenerator() {
		registerMockValueForClass(LocalDate.class, LocalDate.now());
		registerMockValueForClass(LocalDateTime.class, LocalDateTime.now());
		registerMockValueForClass(OffsetDateTime.class, OffsetDateTime.now());
		registerMockValueForClass(ZonedDateTime.class, ZonedDateTime.now());
		registerMockValueForClass(String.class, "I am Mock!");
	}

	/**
	 * Goes through each getter method and makes sure that the return value is
	 * not null, {@code
	 * false} however, is allowed and will not produce an exception.
	 *
	 * @param dto
	 *            the object to test
	 * @param exceptions
	 *            an optional list of exceptions in the form of "{@code
	 *     TestClass.getParameter}".
	 */
	public static <T> void assertGettersAreNotNull(final T dto, final String... exceptions) {
		assertGettersAreNotNull(dto, true, exceptions);
	}

	/**
	 * Goes through each getter method and makes sure that the return value is
	 * not null or {@code
	 * false}.
	 *
	 * @param dto
	 *            the object to test
	 * @param exceptions
	 *            an optional list of exceptions in the form of "{@code
	 *     TestClass.getParameter}".
	 */
	public static <T> void assertGettersAreNotNullOrFalse(final T dto, final String... exceptions) {
		assertGettersAreNotNull(dto, false, exceptions);
	}

	/**
	 * Goes through each getter method and makes sure that the return value is
	 * not null.
	 *
	 * @param dto
	 *            the object to test
	 * @param allowFalse
	 *            if {@code false} then if any property returns {@code false},
	 *            this will raise an exception
	 * @param exceptions
	 *            an optional list of exceptions in the form of "{@code
	 *     TestClass.getParameter}".
	 */
	@SuppressWarnings({ "java:S3776", "java:S112" })
	public static <T> void assertGettersAreNotNull(final T dto, boolean allowFalse, final String... exceptions) {
		final List<String> exceptionGetters = Arrays.asList(exceptions);

		SoftAssertions softAssert = new SoftAssertions();
		for (final Method method : dto.getClass().getMethods()) {
			final String methodName = method.getName();
			if (isGetter(method)) {
				final String classMethodName = dto.getClass().getSimpleName() + "." + methodName;
				if (!exceptionGetters.contains(classMethodName)) {
					final Object result;
					try {
						result = method.invoke(dto);
					} catch (Exception e) {
						throw new RuntimeException("error invoking getter on dto", e);
					}
					final String methodString = "method ";
					softAssert.assertThat(result)
							.describedAs(methodString + classMethodName + "() should not return null")
							.isNotNull();
					if (result instanceof Number number) {
						softAssert.assertThat(number.doubleValue())
								.describedAs(
										methodString + classMethodName + "() should not return '0.0' but was " + result)
								.isNotCloseTo(0.0, Offset.offset(0.01));
					} else if ((result instanceof Boolean bool) && !allowFalse) {
						softAssert.assertThat(bool)
								.describedAs(methodString + classMethodName + "() should not return 'false' but was "
										+ result)
								.isTrue();

					}
				}
			}
		}

		softAssert.assertAll();
	}

	/**
	 * Checks if all getters of both objects return the same value and that all
	 * of them return values <code>!=
	 * null</code>.
	 *
	 * @param actual
	 *            the object to test
	 * @param reference
	 *            the reference object to use as a comparison
	 * @param exceptions
	 *            an optional list of exceptions in the form of "{@code
	 *     TestClass.getParameter}".
	 */
	public static void assertGettersMatchAndAreNotNull(final Object actual, final Object reference,
			final String... exceptions) {
		final Class<?> actualClass = actual.getClass();
		final Class<?> referenceClass = reference.getClass();

		final SoftAssertions softAssert = new SoftAssertions();

		final Set<String> checkedMethods = doAssertSourceGettersMatch(softAssert, false, reference, actual,
				referenceClass, actualClass, exceptions);

		for (final Method actualMethod : actualClass.getMethods()) {

			final String actualMethodName = actualMethod.getName();
			final String actualClassMethodName = actualClass.getSimpleName() + "." + actualMethodName;
			final String referenceClassMethodName = referenceClass.getSimpleName() + "." + actualMethodName;

			boolean result = checkedMethods.contains(actualMethodName)
					|| Arrays.asList(exceptions).contains(actualClassMethodName)
					|| DEFAULT_MAPPING_EXCEPTIONS.contains(actualMethodName) || !isGetter(actualMethod);
			softAssert.assertThat(result)
					.describedAs(reference + " does not contain a getter with name '" + referenceClassMethodName
							+ "'. \nIf this is intentional add an exception for '" + actualClassMethodName + "'\n")
					.isTrue();
		}
		softAssert.assertAll();
	}

	/**
	 * Checks if all getters from the source object match with getters from the
	 * target objects and return the same value and that all of them return
	 * values <code>!= null</code>.
	 *
	 * <p>
	 * Getters which are only present for the target object are ignored.
	 *
	 * @param actual
	 *            the target object (actual)
	 * @param source
	 *            the source object (the reference)
	 * @param exceptions
	 *            an optional list of exceptions in the form of "{@code
	 *                   TestClass.getParameter}".
	 */
	public static void assertSourceGettersMatchAndAreNotNull(final Object actual, final Object source,
			final String... exceptions) {
		final Class<?> class1 = source.getClass();
		final Class<?> class2 = actual.getClass();
		final SoftAssertions softAssert = new SoftAssertions();
		doAssertSourceGettersMatch(softAssert, false, source, actual, class1, class2, exceptions);
		softAssert.assertAll();
	}

	/**
	 * Checks if all getters from the source object match with getters from the
	 * target objects and return the same value.
	 *
	 * <p>
	 * Getters which are only present for the target object are ignored.
	 *
	 * @param source
	 *            the source object (the reference)
	 * @param target
	 *            the target object (actual)
	 * @param exceptions
	 *            an optional list of exceptions in the form of "{@code
	 *     TestClass.getParameter}".
	 */
	public static void assertSourceGettersMatch(final Object source, final Object target, final String... exceptions) {
		final Class<?> class1 = source.getClass();
		final Class<?> class2 = target.getClass();
		assertSourceGettersMatch(source, target, class1, class2, exceptions);
	}

	/**
	 * Checks if all getters from the source object match with getters from the
	 * target objects and return the same value.
	 *
	 * <p>
	 * Getters which are only present for the target object are ignored.
	 *
	 * @param source
	 *            the source object (the reference)
	 * @param target
	 *            the target object (actual)
	 * @param exceptions
	 *            an optional list of exceptions in the form of "{@code
	 *     TestClass.getParameter}".
	 */
	public static Set<String> assertSourceGettersMatch(final Object source, final Object target,
			final Class<?> srcClass, final Class<?> targetClass, final String... exceptions) {

		final SoftAssertions softAssert = new SoftAssertions();
		Set<String> result = doAssertSourceGettersMatch(softAssert, true, source, target, srcClass, targetClass,
				exceptions);
		softAssert.assertAll();
		return result;
	}

	@SuppressWarnings({ "java:S3776", "java:S112" })
	private static Set<String> doAssertSourceGettersMatch(final SoftAssertions softAssert, final boolean allowNulls,
			final Object source, final Object target, final Class<?> srcClass, final Class<?> targetClass,
			final String... exceptions) {
		final Set<String> checkedMethods = new HashSet<>();

		for (final Method method1 : srcClass.getMethods()) {
			final String methodName = method1.getName();
			PropertyDescriptor propertyForMethod = BeanUtils.findPropertyForMethod(method1);
			final String propertyName = propertyForMethod != null ? propertyForMethod.getName() : null;

			if (propertyName != null && isGetter(method1) && !methodName.equals("getClass")) {
				String srcClassSimpleName = srcClass.getSimpleName();
				String targetClassSimpleName = targetClass.getSimpleName();

				// when creating mocks via mockito (siehe createPopulatedMock)
				// then we get an adapted class name
				// (MyClass$$EnhancerByMockito) which
				// causes our exceptions not to match

				if (srcClassSimpleName.contains("$$EnhancerByMockito")) {
					srcClassSimpleName = srcClassSimpleName.replaceFirst("\\$\\$EnhancerByMockito.*", "");
				}
				if (targetClassSimpleName.contains("$$EnhancerByMockito")) {
					targetClassSimpleName = targetClassSimpleName.replaceFirst("\\$\\$EnhancerByMockito.*", "");
				}
				final String classMethodName = srcClassSimpleName + "." + methodName;
				final String targetClassMethodName = targetClassSimpleName + "." + methodName;

				if (!isExcludedMethod(method1, classMethodName, Arrays.asList(exceptions))
						&& !isExcludedMethod(method1, targetClassMethodName, Arrays.asList(exceptions))) {

					Method method2 = getGetterMethod(softAssert, target, targetClass, methodName, classMethodName,
							propertyName);
					if (method2 == null)
						continue;
					final Object sourceResult;
					final Object targetResult;
					try {
						sourceResult = method1.invoke(source);
						targetResult = method2.invoke(target);
					} catch (Exception e) {
						throw new RuntimeException("error invoking getter/setter", e);
					}
					final String methodString = "method ";
					if (!allowNulls) {
						softAssert.assertThat(sourceResult)
								.describedAs("source object " + methodString + srcClassSimpleName + "." + methodName
										+ "() returned NULL, if this is intentional add an exception for "
										+ classMethodName)
								.isNotNull();
						softAssert.assertThat(targetResult)
								.describedAs("target object " + methodString + targetClass.getSimpleName() + "."
										+ methodName + "() returned NULL, if this is "
										+ "intentional add an exception for " + targetClassMethodName)
								.isNotNull();
					}
					softAssert.assertThat(targetResult)
							.describedAs(methodString + methodName + "() does not return the same value ("
									+ classMethodName + ")")
							.isEqualTo(sourceResult);

				}
			}
			checkedMethods.add(methodName);
		}
		return checkedMethods;
	}

	@Nullable
	private static Method getGetterMethod(SoftAssertions softAssert, Object target, Class<?> targetClass,
			String methodName, String classMethodName, String propertyName) {
		Method method2 = null;
		try {
			method2 = targetClass.getMethod(methodName);
		} catch (final NoSuchMethodException e) {
			if (!targetClass.isRecord()) {
				softAssert.fail(target + " does not contain a getter with name " + methodName
						+ ".\nIf this is intentional add an exception for '" + classMethodName + "'\n", e);
				return null;
			}
		}
		if (method2 == null) {
			try {
				method2 = targetClass.getMethod(propertyName);
			} catch (final NoSuchMethodException e) {
				softAssert.fail(target + " does not contain a getter with name " + methodName
						+ ".\nIf this is intentional add an exception for '" + classMethodName + "'\n", e);
				return null;
			}
		}
		return method2;
	}

	private static boolean isGetter(final Method method) {
		return (method.getName().startsWith(GET_METHOD) || method.getName().startsWith(IS_METHOD))
				&& method.getParameterTypes().length == 0;
	}

	private static boolean isExcludedMethod(final Method method, String classMethodName, List<String> exclusions) {
		PropertyDescriptor propertyForMethod = BeanUtils.findPropertyForMethod(method);
		if (propertyForMethod == null) {
			return true;
		}

		return (exclusions.contains(classMethodName) || DEFAULT_MAPPING_EXCEPTIONS.contains(method.getName()));
	}

	/**
	 * Register a default mock return value for objects of a certain type. This
	 * allows subclasses to provide concrete mock values for special objects
	 * which the DTO or entity classes need. This is useful in case the mapper
	 * does some non-trivial mapping and requires valid (populated) objects to
	 * do the work.
	 *
	 * @param argClass
	 *            the class of the property
	 * @param returnValue
	 *            the value to return for this property class.
	 */
	public void registerMockValueForClass(final Class<?> argClass, final Object returnValue) {
		registeredMockValues.put(argClass, returnValue);
	}

	/**
	 * Register a default mock return value for a specific property. This allows
	 * subclasses to provide concrete mock values for special objects which the
	 * DTO or entity classes need. This is useful in case the mapper does some
	 * non-trivial mapping and requires valid (populated) objects to do the
	 * work.
	 *
	 * @param setterMethodName
	 *            the name of the property setter method, if the method is
	 *            called {@code setId} then the property name is "setId".
	 * @param returnValue
	 *            the value to return for this property.
	 */
	public void registerMockForSetterMethod(final String setterMethodName, final Object returnValue) {
		registeredMockPropertyValues.put(setterMethodName, returnValue);
	}

	/**
	 * Generates a DTO or entity and fills it with non-null values.
	 *
	 * @param clazz
	 *            the class of the instance to create
	 * @param exceptions
	 *            a list of exception setter methods which should not be set.
	 *            Format {@code
	 *     setZone}.
	 * @return the populated object
	 */
	@SuppressWarnings("java:S112")
	public <T> T createPopulatedBean(final Class<T> clazz, String... exceptions) {
		final T newDTO;
		try {
			newDTO = clazz.getConstructor().newInstance();
			populateBean(clazz, newDTO, false, exceptions);

			final String debugInfo = ToStringBuilder.reflectionToString(newDTO);
			LOG.info("Created new bean {}", debugInfo);
			return newDTO;
		} catch (Exception e) {
			throw new RuntimeException("cannot populate bean", e);
		}
	}

	/**
	 * Generates a DTO or entity and fills it with random values.
	 *
	 * @param clazz
	 *            the class of the instance to create
	 * @param exceptions
	 *            a list of exception setter methods which should not be set.
	 *            Format {@code
	 *     setZone}.
	 * @return the populated object
	 */
	@SuppressWarnings("java:S112")
	public <T> T createRandomlyPopulatedBean(final Class<T> clazz, String... exceptions) {
		try {
			final T newDTO = clazz.getConstructor().newInstance();
			populateBean(clazz, newDTO, true, exceptions);

			final String debugInfo = ToStringBuilder.reflectionToString(newDTO);
			LOG.info("Created new random bean {}", debugInfo);
			return newDTO;
		} catch (Exception e) {
			throw new RuntimeException("error creating random bean", e);
		}
	}

	/**
	 * Populates the given bean with mock values.
	 *
	 * @param <T>
	 *            the type parameter
	 * @param newDTO
	 *            the new dTO
	 */
	public <T> void populateBean(final T newDTO) {
		populateBean(newDTO.getClass(), newDTO, false);
	}

	@SuppressWarnings({ "java:S3011", "java:S3776" })
	private <T> void populateBean(final Class<? extends T> clazz, final T newDTO, final boolean mockedValues,
			final String... exceptions) {
		final List<String> exceptionSetters = Arrays.asList(exceptions);
		// Now fill the DTO with mocks
		for (final Method method : clazz.getMethods()) {
			String methodName = method.getName();
			if (methodName.startsWith(SET_METHOD) && method.getParameterTypes().length == 1
					&& !exceptionSetters.contains(methodName)) {
				// avoid setId(Comparable)
				if (methodName.equals(SET_ID_METHOD)
						&& method.getParameterTypes()[0].isAssignableFrom(Comparable.class)) {
					continue;
				}
				injectSetterValue(newDTO, method, mockedValues);
			}
		}
		if (Versionable.class.isAssignableFrom(clazz)) {
			Method versionSetter;
			try {
				// 1. get public methods from this class or super classes
				versionSetter = clazz.getDeclaredMethod("setVersion", Long.class);
			} catch (NoSuchMethodException ite) {
				// 2. get private method from this class or super classes
				versionSetter = getPrivateMethod(clazz, "setVersion", Long.class);
			}
			if (versionSetter != null && !versionSetter.canAccess(newDTO)) {
				AccessibleObject.setAccessible(new AccessibleObject[] { versionSetter }, true);
			}
			if (versionSetter == null) {
				throw new IllegalStateException("could not get version setter");
			}
			injectSetterValue(newDTO, versionSetter, false);
		}
	}

	/**
	 * Returns the private method from the provided class or one of its super
	 * classes.
	 */
	private <T> Method getPrivateMethod(final Class<T> clazz, final String name, final Class<?>... parameters) {
		Method method = null;
		try {
			// 1. try this class
			method = clazz.getMethod(name, parameters);
		} catch (NoSuchMethodException nsme) {
			// 2. try super classes
			final Class<? super T> superClass = clazz.getSuperclass();
			if (superClass != null) {
				return getPrivateMethod(superClass, name, parameters);
			}
		}
		return method;
	}

	/**
	 * Creates a mock object that returns smart random values for all its getter
	 * methods.
	 *
	 * @param clazz
	 *            the class to generate a mock for
	 * @param <T>
	 *            the return type
	 * @return the mock
	 */
	public <T> T createPopulatedMock(final Class<T> clazz) {
		final T newDTO = Mockito.mock(clazz,
				Mockito.withSettings().strictness(Strictness.LENIENT).defaultAnswer(invocation -> {
					final Method method = invocation.getMethod();
					if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
						final Class<?> resultClass = method.getReturnType();

						return getPredefinedSetterValue(method, resultClass);
					}
					return null;
				}));

		final String beanInfo = ToStringBuilder.reflectionToString(newDTO);
		LOG.info("Created new bean {}", beanInfo);
		return newDTO;
	}

	/**
	 * Injects a non-null value into the passed object.
	 *
	 * @param dto
	 *            the object to populate
	 * @param method
	 *            the setter method to call
	 * @return the value set to this setter.
	 */
	public <T> Object injectSetterValue(final T dto, final Method method, final boolean mockedValues) {
		final Object arg;
		final Class<?> valueClass = method.getParameterTypes()[0];

		if (mockedValues) {
			arg = getRandomSetterValue(method, valueClass);
		} else {
			arg = getPredefinedSetterValue(method, valueClass);
		}
		if (arg != null) {
			LOG.debug("MockBeanGenerator.injectSetterValue({}, {}) = {}", dto, method.getName(), arg);
			injectSetterValue(dto, method, arg);
		}
		return arg;
	}

	/**
	 * Checks whether every setter/getter pair matches. I.e. a value set with a
	 * setter should be retrievable with its corresponding getter.
	 *
	 * <p>
	 *
	 * <p>
	 * If a getter returns a different value than was set with its setter, an
	 * <code>
	 * AssertionError
	 * </code> is thrown.
	 *
	 * <p>
	 *
	 * <p>
	 * If a setter has a corresponding getter and vice versa is not checked.
	 *
	 * <p>
	 *
	 * @param clazz
	 *            the class which should be checked.
	 */
	@SuppressWarnings("java:S112")
	public <T> void assertAllSetterAndGettersMatch(final Class<T> clazz) {
		final T bean;
		try {
			bean = clazz.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		SoftAssertions softAssert = new SoftAssertions();
		// now fill the dto with mocks
		for (final Method setterMethod : clazz.getMethods()) {
			if (setterMethod.getName().startsWith(SET_METHOD) && setterMethod.getParameterTypes().length == 1) {
				final String property = setterMethod.getName().substring(SET_METHOD.length());
				final Object setterValue = injectSetterValue(bean, setterMethod, true);
				if (setterValue != null) {
					for (final Method getterMethod : clazz.getMethods()) {
						assertGetterMatches(setterMethod, getterMethod, property, bean, softAssert, setterValue);
					}
				}
			}
		}
		softAssert.assertAll();
	}

	@SuppressWarnings("java:S112")
	private static <T> void assertGetterMatches(Method setterMethod, Method getterMethod, String property, T bean,
			SoftAssertions softAssert, Object setterValue) {
		final String methodName = getterMethod.getName();
		if ((methodName.equals(GET_METHOD + property) || methodName.equals(IS_METHOD + property))
				&& getterMethod.getParameterTypes().length == 0) {
			final Object getterResult;
			try {
				getterResult = getterMethod.invoke(bean);
			} catch (Exception e) {
				throw new RuntimeException("cannot invoke getter", e);
			}
			final String failureMessage = "Getter " + methodName + " and Setter " + setterMethod.getName()
					+ " for property " + property + " did not match.";
			if (ClassUtils.isPrimitiveOrWrapper(getterResult.getClass())) {
				// assertSame() does not work for wrapper
				// classes because the
				// wrapper might have
				// changed (though the wrapped value is
				// the same).
				softAssert.assertThat(getterResult).describedAs(failureMessage).isEqualTo(setterValue);
			} else {
				softAssert.assertThat(getterResult).describedAs(failureMessage).isSameAs(setterValue);
			}
		}
	}

	/**
	 * Returns an object suitable for injecting into the given setter method.
	 *
	 * <p>
	 *
	 * <p>
	 * For some predefined classes a predefined value is returned. For all other
	 * classes, a mock value registered with {@link #registeredMockValues} or a
	 * new mock created with Mockito is returned.
	 *
	 * <p>
	 *
	 * <p>
	 * This method does not work for argument classes which are not predefined,
	 * not registered and final (generic mocking will not work).
	 *
	 * @param method
	 *            the setter method for which an argument is wanted.
	 * @return a value suitable to be an argument for the given setter method
	 */
	@SuppressWarnings({ "UnnecessaryBoxing", "rawtypes", "java:S3776" })
	private Object getPredefinedSetterValue(final Method method, final Class<?> argClass) {
		final Object arg;

		if (registeredMockPropertyValues.containsKey(method.getName())) {
			arg = registeredMockPropertyValues.get(method.getName());
		} else if (Integer.class.equals(argClass) || int.class.equals(argClass)) {
			arg = Integer.valueOf(123);
		} else if (Long.class.equals(argClass) || long.class.equals(argClass)) {
			arg = Long.valueOf(((long) Integer.MAX_VALUE) + 123);
		} else if (Double.class.equals(argClass) || double.class.equals(argClass)) {
			arg = Double.valueOf(0.23);
		} else if (Float.class.equals(argClass) || float.class.equals(argClass)) {
			arg = Float.valueOf(0.49f);
		} else if (Boolean.class.equals(argClass) || boolean.class.equals(argClass)) {
			arg = Boolean.TRUE;
		} else if (Short.class.equals(argClass) || short.class.equals(argClass)) {
			arg = (short) 23;
		} else if (UUID.class.equals(argClass)) {
			arg = UUID.fromString("9c46cf20-6f06-11e3-981f-0800200c9a66");
		} else if (LocalTime.class.equals(argClass)) {
			arg = LocalTime.of(20, 0);
		} else if (Character.class.equals(argClass) || char.class.equals(argClass)) {
			arg = 'c';
		} else if (argClass.equals(Object.class) && SET_ID_METHOD.equals(method.getName())) {
			// this is a special case where setId appears multiple times with
			// different signatures,
			// skip
			// this one
			// and wait for the correct one to show up
			return null;
		} else if (argClass.isEnum()) {
			arg = argClass.getEnumConstants()[0];
		} else if (List.class.equals(argClass)) {
			arg = new ArrayList();
		} else if (registeredMockValues.containsKey(argClass)) {
			arg = registeredMockValues.get(argClass);
		} else if (Set.class.equals(argClass)) {
			arg = Collections.emptySet();
		} else if (Collection.class.equals(argClass)) {
			arg = new ArrayList();
		} else if (Comparable.class.equals(argClass)) {
			// ignore Comparable
			return null;
		} else {
			try {
				arg = Mockito.mock(argClass);
			} catch (MockitoException e) {
				throw new MockitoException(
						String.format("Error creating parameter of class %s for method %s", argClass, method), e);
			}
		}
		return arg;
	}

	/**
	 * Returns an object suitable for injecting into the given setter method.
	 *
	 * <p>
	 *
	 * <p>
	 * For some predefined classes a randomly generated value is returned. For
	 * all other classes, a new mock created with Mockito is returned.
	 *
	 * <p>
	 *
	 * <p>
	 * This method does not work for argument classes which are not predefined
	 * and final (generic mocking will not work).
	 *
	 * @param method
	 *            the setter method for which an argument is wanted.
	 * @return a value suitable to be an argument for the given setter method
	 */
	@SuppressWarnings({ "java:S3776", "UnnecessaryBoxing", "java:S2245" })
	private Object getRandomSetterValue(final Method method, final Class<?> valueClass) {
		Object value;
		if (Integer.class.equals(valueClass) || int.class.equals(valueClass)) {
			value = random.nextInt();
		} else if (Long.class.equals(valueClass) || long.class.equals(valueClass)) {
			value = random.nextLong();
		} else if (Double.class.equals(valueClass) || double.class.equals(valueClass)) {
			value = Double.valueOf(random.nextDouble());
		} else if (Boolean.class.equals(valueClass) || boolean.class.equals(valueClass)) {
			value = random.nextBoolean();
		} else if (UUID.class.equals(valueClass)) {
			value = UUID.randomUUID();
		} else if (valueClass.equals(Object.class) && SET_ID_METHOD.equals(method.getName())) {
			// this is a special case where setId appears multiple times with
			// different signatures,
			// skip
			// this one
			// and wait for the correct one to show up
			return null;
		} else if (valueClass.isEnum()) {
			final Object[] enumConstants = valueClass.getEnumConstants();
			value = enumConstants[random.nextInt(enumConstants.length)];
		} else if (String.class.equals(valueClass)) {
			value = RandomStringUtils.randomAlphanumeric(8);
		} else if (OffsetDateTime.class.equals(valueClass)) {
			value = OffsetDateTime.now();
		} else if (ZonedDateTime.class.equals(valueClass)) {
			value = ZonedDateTime.now();
		} else if (LocalDate.class.equals(valueClass)) {
			value = LocalDate.now();
		} else if (Character.class.equals(valueClass)) {
			value = 'C';
		} else if (List.class.equals(valueClass)) {
			value = new ArrayList<>();
		} else if (Map.class.equals(valueClass)) {
			value = new HashMap<>();
		} else if (Set.class.equals(valueClass)) {
			value = new HashSet<>();
		} else if (Collection.class.equals(valueClass)) {
			value = new ArrayList<>();
		} else {
			value = Mockito.mock(valueClass);
		}
		return value;
	}

	/**
	 * Invokes the given setter method with the given value on the given bean.
	 */
	@SuppressWarnings("java:S112")
	private <T> void injectSetterValue(final T bean, final Method method, final Object value) {
		try {
			method.invoke(bean, value);
		} catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) { // works
			throw new RuntimeException(
					String.format("Error calling injection method %s on %s, %s)", method, bean, value), e);
		}
	}
}
