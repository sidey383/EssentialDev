package ru.sidey383.essential.dev.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * use on public static fields
 * for load value from
 * ConfigurationLoader
 * **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {

	public String Path();
	
	public String Name() default "";
	
}
