>本文转载自：[https://pjmike.github.io/2018/11/03/Bean映射工具之Apache-BeanUtils-VS-Spring-BeanUtils/](https://pjmike.github.io/2018/11/03/Bean映射工具之Apache-BeanUtils-VS-Spring-BeanUtils/),作者 pjmike 。

## 前言

在我们实际项目开发过程中，我们经常需要将不同的两个对象实例进行属性复制，从而基于源对象的属性信息进行后续操作，而不改变源对象的属性信息,比如DTO数据传输对象和数据对象DO，我们需要将DO对象进行属性复制到DTO，但是对象格式又不一样，所以我们需要编写映射代码将对象中的属性值从一种类型转换成另一种类型。

## 对象拷贝

在具体介绍两种 BeanUtils 之前，先来补充一些基础知识。它们两种工具本质上就是对象拷贝工具，而对象拷贝又分为深拷贝和浅拷贝，下面进行详细解释。

### 什么是浅拷贝和深拷贝

在Java中，除了 **基本数据类型**之外，还存在 **类的实例对象**这个引用数据类型，而一般使用 “=”号做赋值操作的时候，对于基本数据类型，实际上是拷贝的它的值，但是对于对象而言，其实赋值的只是这个对象的引用，将原对象的引用传递过去，他们实际还是指向的同一个对象。

而浅拷贝和深拷贝就是在这个基础上做的区分，如果在拷贝这个对象的时候，只对基本数据类型进行了拷贝，而对引用数据类型只是进行引用的传递，而没有真实的创建一个新的对象，则认为是**浅拷贝**。反之，在对引用数据类型进行拷贝的时候，创建了一个新的对象，并且复制其内的成员变量，则认为是**深拷贝**。

简单来说:

- **浅拷贝**：对基本数据类型进行值传递，对引用数据类型进行引用传递般的拷贝，此为浅拷贝
- **深拷贝**： 对基本数据类型进行值传递，对引用数据类型，创建一个新的对象，并复制其内容，此为深拷贝。

![deep and shallow copy](https://my-blog-to-use.oss-cn-beijing.aliyuncs.com/2019-7/java-deep-and-shallow-copy.jpg)

## BeanUtils

前面简单讲了一下对象拷贝的一些知识，下面就来具体看下两种 BeanUtils 工具

### Apache 的 BeanUtils

首先来看一个非常简单的BeanUtils的例子

```java
public class PersonSource  {
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    // getters/setters omiited
}
public class PersonDest {
    private Integer id;
    private String username;
    private Integer age;
    // getters/setters omiited
}
public class TestApacheBeanUtils {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
       //下面只是用于单独测试
        PersonSource personSource = new PersonSource(1, "pjmike", "12345", 21);
        PersonDest personDest = new PersonDest();
        BeanUtils.copyProperties(personDest,personSource);
        System.out.println("persondest: "+personDest);
    }
}
persondest: PersonDest{id=1, username='pjmike', age=21}
```

从上面的例子可以看出，对象拷贝非常简单，BeanUtils最常用的方法就是:

```java
//将源对象中的值拷贝到目标对象
public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
    BeanUtilsBean.getInstance().copyProperties(dest, orig);
}
```

默认情况下，使用`org.apache.commons.beanutils.BeanUtils`对复杂对象的复制是引用，这是一种**浅拷贝**

但是由于 Apache下的BeanUtils对象拷贝性能太差，不建议使用，而且在**阿里巴巴Java开发规约插件**上也明确指出：

> Ali-Check | 避免用Apache Beanutils进行属性的copy。

commons-beantutils 对于对象拷贝加了很多的检验，包括类型的转换，甚至还会检验对象所属的类的可访问性,可谓相当复杂，这也造就了它的差劲的性能，具体实现代码如下：

```java
public void copyProperties(final Object dest, final Object orig)
        throws IllegalAccessException, InvocationTargetException {

        // Validate existence of the specified beans
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }
        if (log.isDebugEnabled()) {
            log.debug("BeanUtils.copyProperties(" + dest + ", " +
                      orig + ")");
        }

        // Copy the properties, converting as necessary
        if (orig instanceof DynaBean) {
            final DynaProperty[] origDescriptors =
                ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (DynaProperty origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                // Need to check isReadable() for WrapDynaBean
                // (see Jira issue# BEANUTILS-61)
                if (getPropertyUtils().isReadable(orig, name) &&
                    getPropertyUtils().isWriteable(dest, name)) {
                    final Object value = ((DynaBean) orig).get(name);
                    copyProperty(dest, name, value);
                }
            }
        } else if (orig instanceof Map) {
            @SuppressWarnings("unchecked")
            final
            // Map properties are always of type <String, Object>
            Map<String, Object> propMap = (Map<String, Object>) orig;
            for (final Map.Entry<String, Object> entry : propMap.entrySet()) {
                final String name = entry.getKey();
                if (getPropertyUtils().isWriteable(dest, name)) {
                    copyProperty(dest, name, entry.getValue());
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            final PropertyDescriptor[] origDescriptors =
                getPropertyUtils().getPropertyDescriptors(orig);
            for (PropertyDescriptor origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (getPropertyUtils().isReadable(orig, name) &&
                    getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        final Object value =
                            getPropertyUtils().getSimpleProperty(orig, name);
                        copyProperty(dest, name, value);
                    } catch (final NoSuchMethodException e) {
                        // Should not happen
                    }
                }
            }
        }

    }
```

### Spring 的 BeanUtils

使用spring的BeanUtils进行对象拷贝:

```java
public class TestSpringBeanUtils {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {

       //下面只是用于单独测试
        PersonSource personSource = new PersonSource(1, "pjmike", "12345", 21);
        PersonDest personDest = new PersonDest();
        BeanUtils.copyProperties(personSource,personDest);
        System.out.println("persondest: "+personDest);
    }
}
```

spring下的BeanUtils也是使用 `copyProperties`方法进行拷贝，只不过它的实现方式非常简单，就是对两个对象中相同名字的属性进行简单的get/set，仅检查属性的可访问性。具体实现如下:

```Java
private static void copyProperties(Object source, Object target, @Nullable Class<?> editable,
			@Nullable String... ignoreProperties) throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
						"] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		for (PropertyDescriptor targetPd : targetPds) {
			Method writeMethod = targetPd.getWriteMethod();
			if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null) {
					Method readMethod = sourcePd.getReadMethod();
					if (readMethod != null &&
							ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
						try {
							if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
								readMethod.setAccessible(true);
							}
							Object value = readMethod.invoke(source);
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						}
						catch (Throwable ex) {
							throw new FatalBeanException(
									"Could not copy property '" + targetPd.getName() + "' from source to target", ex);
						}
					}
				}
			}
		}
	}
```



可以看到，成员变量赋值是基于目标对象的成员列表，并且会跳过ignore的以及在源对象中不存在，所以这个方法是安全的，不会因为两个对象之间的结构差异导致错误，但是**必须保证同名的两个成员变量类型相同**

## 小结

以上简要的分析两种BeanUtils，因为Apache下的BeanUtils性能较差，不建议使用，可以使用 Spring的BeanUtils ，或者使用其他拷贝框架，比如：**[Dozer](http://dozer.sourceforge.net/documentation/gettingstarted.html)**、**[ModelMapper](http://modelmapper.org/)**等等，在后面的文章中我会对这些拷贝框架进行介绍。

## 参考资料 & 鸣谢

- [谈谈 Java 开发中的对象拷贝](http://www.importnew.com/26306.html)
- [细说 Java 的深拷贝和浅拷贝](https://segmentfault.com/a/1190000010648514)