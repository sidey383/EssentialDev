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

	/**
	 * path to field in YamlConfiguration
	 * **/
	String Path();

	/**
	 * name of the associated configuration
	 * **/
	String Name() default "";

	/**
	 * field mask for Enum's
	 * required for non-static Enum fields
	 * the path to the non-static Enum field is Path() + "." + Enum.toString() + "." + FieldMask()
	 * **/
	String FieldMask() default "";

}
