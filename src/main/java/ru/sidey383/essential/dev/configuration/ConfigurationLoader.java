package ru.sidey383.essential.dev.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

/**
 * @author sidey383
 * Use for load YanlConfiguration
 * **/
@SuppressWarnings("rawtypes")
public class ConfigurationLoader {

	private final Logger logger;
	private YamlConfiguration config;
	private File file;
	private final ArrayList<Class> classToLoad = new ArrayList<>();
	private final ArrayList<Class> enumToLoad = new ArrayList<>();
	private String name = "";

	/**
	 * @param file - configuration file
	 * @param defaultFile - path to resource with default configuration
	 * @param logger - logger for this class
	 * **/
	public ConfigurationLoader(File file, String defaultFile, Logger logger) throws IOException {
		this.logger = logger;
		load(file, defaultFile);
	}

	/**
	 * @param file - configuration file
	 * @param defaultFile - path to resource with default configuration
	 * @param name - name of configuration
	 *  only fields with this name in property will be loaded
	 * @param logger - logger for this class
	 * **/
	public ConfigurationLoader(File file, String defaultFile, String name, Logger logger) throws IOException {
		this.logger = logger;
		this.name = name;
		load(file, defaultFile);
	}

	/**
	 * @param file - configuration file
	 * @param defaultFile - path to resource with default configuration
	 * **/
	public ConfigurationLoader(File file, String defaultFile) throws IOException {
		logger = Bukkit.getLogger();
		load(file, defaultFile);
	}

	/**
	 * @param file - configuration file
	 * @param defaultFile - path to resource with default configuration
	 * @param name - name of configuration
	 *  only fields with this name in property will be loaded
	 * **/
	public ConfigurationLoader(File file, String defaultFile, String name) throws IOException {
		this.name = name;
		logger = Bukkit.getLogger();
		load(file, defaultFile);
	}

	private void load(File file, String defaultFile) throws IOException {
		File dir = file.getParentFile();
		if(!dir.exists() && !dir.mkdirs())
			throw new IOException("cant create directory for files");
		if(!file.exists()) {
			if (!file.createNewFile()) {
				throw new IOException("cant create new file");
			}
			if (defaultFile != null) {
				try (OutputStream out = new FileOutputStream(file)){
					URL u = getClass().getClassLoader().getResource(defaultFile);
					if (u!= null) {
						try (InputStream in = u.openStream()) {
								byte[] buffer = new byte[1024];
								int length;
								while ((length = in.read(buffer)) > 0) {
									out.write(buffer, 0, length);
								}
						}
					}
				}
			}
			logger.log(Level.INFO, "create configuration file "+ file.getName());
		}
		setFile(file);
	}

	/**
	 * @param file - load configuration from new file
	 * **/
	public void setFile(File file) {
		config = YamlConfiguration.loadConfiguration(file);
		this.file = file;
		loadAll();
	}

	/**
	 * @param clazz - class to load fields
	 * **/
	public void addClassToLoad(Class clazz) {
		classToLoad.add(clazz);
		loadClass(clazz);
	}

	/**
	 * @param enam - enum to load fields
	 * **/
	public void addEnumToLoad(Class enam) {
		if (!enam.isEnum()) return;
		enumToLoad.add(enam);
		loadEnum(enam);
	}

	public YamlConfiguration getFileConfiguration() {
		return config;
	}

	/**
	 * reloads all fields saved classes
	 * **/
	public void loadAll() {
		for (Class clazz: classToLoad)
			loadClass(clazz);
		for (Class enam: enumToLoad)
			loadEnum(enam);
	}

	public ArrayList<Class> getClassesToLoad() {
		return classToLoad;
	}

	/**
	 * reload YamlConfiguration from file
	 * does not overwrite fields
	 * **/
	public void reload() {
		try {
			config.load(file);
			loadAll();
		}  catch (Exception e) {
			logger.log(Level.SEVERE, " cant reload configuration ", e);
		}
	}

	private void loadClass(Class clazz) {
		if (clazz == null)
			logger.log(Level.INFO, "null class");
		if (clazz == null) return;
		for (Field f: clazz.getFields()) {
			loadField(f, clazz);
		}
	}

	private void loadEnum(Class enam) {
		if (enam == null)
			logger.log(Level.INFO, "null class");
		if (enam == null) return;
		for (Field f: enam.getFields()) {
			loadEnum(f, enam);
		}
	}

	private void loadField(Field f, Class clazz) {
		String path = null;
		if (f == null || clazz == null) return;
		if (!Modifier.isStatic(f.getModifiers())) return;
		try {
			boolean canNull = f.getAnnotationsByType(Nullable.class).length > 0;
			ConfigProperty cf = f.getAnnotation(ConfigProperty.class);
			if(cf == null)
			{
				return;
			}
			f.setAccessible(true);
			path = cf.Path();
			String name = cf.Name();
			if ( !( name.equals("") || name.equals(this.name) ))
				return;
			objectInField(f, null, path, canNull);
		} catch (Exception e) {
			logger.warning("cant load field "+path+" from config "+config.getName()+" to class "+clazz.getName());
		}
	}

	public void loadEnum(Field f, Class enam)
	{
		if (f == null || enam == null) return;
		if (!enam.isEnum()) return;
		if (Modifier.isStatic(f.getModifiers())) return;
		boolean canNull = f.getAnnotationsByType(Nullable.class).length > 0;
		ConfigProperty cf = f.getAnnotation(ConfigProperty.class);
		String name = cf.Name();
		if (!( name.equals("") || name.equals(this.name)))
			return;
		String fieldMask = cf.FieldMask();
		if (fieldMask == null || fieldMask.equals(""))
			fieldMask = f.getName();
		for (Object eConst: enam.getEnumConstants()) {
			String path = null;
			try {
				path = cf.Path()+"."+eConst.toString()+"."+fieldMask;
				objectInField(f, eConst, path, canNull);
			} catch (Exception e) {
				logger.warning("cant load field "+path+" from config "+config.getName()+" to enum "+enam.getName()+" object "+eConst.toString());
				e.printStackTrace();
			}
		}
	}

	private void objectInField(Field field, Object instance, String path, boolean canNull) throws IllegalArgumentException, IllegalAccessException {
		Object obj = config.get(path);

		List<String> list = config.getStringList(path);
		if(field.getType().equals(String.class) && list.size() != 0) {
			StringBuilder result = new StringBuilder();
			for(String str : list) {
				result.append(str).append("\n");
			}
			obj = result.toString();
		}

		if(field.getType().isEnum() && (obj instanceof String)) {
			for(Object eobj: field.getType().getEnumConstants()) {
				if(eobj.toString().equalsIgnoreCase(((String)obj).toLowerCase())) {
					obj = eobj;
					break;
				}
			}
			if(obj instanceof String) {
				logger.warning("cant load field "+path+" from config "+config.getName()+" don't found enum constant " + obj + " in " + field.getType().getCanonicalName());
				return;
			}
		}

		if(obj == null && !canNull) {
			logger.warning("cant load field "+path+" from config "+config.getName()+" cant be null in class " + field.getDeclaringClass().getCanonicalName());
			return;
		}
		if(obj instanceof String)
			obj = ((String) obj).replace('&', '§');
		if(obj instanceof List) {
			List listN = (List<?>) obj;
			if(!listN.isEmpty() && listN.get(0) instanceof String) {
				obj = ((List<String>) listN).stream().map( e -> e.replace('&', '§')).collect(Collectors.toList());
			}
		}
		field.set(instance, obj);
	}
}
