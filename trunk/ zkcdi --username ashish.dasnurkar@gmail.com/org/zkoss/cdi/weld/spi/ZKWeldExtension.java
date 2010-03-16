/* ZKWeldExtension.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 7:53:34 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.weld.spi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.StringMemberValue;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.manager.BeanManagerImpl;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.zkoss.cdi.inject.ComponentId;
import org.zkoss.cdi.weld.Version;
import org.zkoss.cdi.weld.context.ComponentContext;
import org.zkoss.cdi.weld.context.DesktopContext;
import org.zkoss.cdi.weld.context.ExecutionContext;
import org.zkoss.cdi.weld.context.IdSpaceContext;
import org.zkoss.cdi.weld.context.PageContext;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * ZK Weld Extension service provider.
 * @author henrichen
 *
 */
public class ZKWeldExtension implements Extension {
	
	private static final Log log = Log.lookup(ZKWeldExtension.class);
	
	/* Map holding auto generated producer method ids (method return type + injection point member name */
	private static Set<String> producerMethodKeys = new HashSet<String>();
	
	/**
	 * Container lifecycle callback method to pre-process Composer classes to auto generate 
	 * producer methods for ZK components injected in Composer classes. 
	 * @param event
	 * @param manager
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event, BeanManager manager) throws RuntimeException {
		try {
			ClassPool cp = ClassPool.getDefault();
			cp.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
			cp.importPackage("org.zkoss.zul");
			cp.importPackage("org.zkoss.zk.ui");
			cp.importPackage("org.zkoss.cdi.util");
			
			CtClass mainclas = cp.makeClass("org.zkoss.zkplus.cdi.ZKComponentProducerMethods");
			final List<URL> l = getUrlsForCurrentClasspath();
			
			Reflections reflections = new Reflections(
					new ConfigurationBuilder().setUrls(l)
					.setScanners(new FieldAnnotationsScanner()));
			
			Set<Field> fields = reflections.getFieldsAnnotatedWith(ComponentId.class);
			for (Iterator iterator2 = fields.iterator(); iterator2
					.hasNext();) {
				Field field = (Field) iterator2.next();
				CtClass cls = cp.get(field.getType().getName());
				String pckgName = cls.getPackageName();
				if (Component.class.isAssignableFrom(field.getType()) || !pckgName.endsWith("zul.api")) {
					addProducerMethod(field, mainclas);
				} 
			}
			Class c = mainclas.toClass(Thread.currentThread().getContextClassLoader(), this.getClass().getProtectionDomain());

			// This will register dynamically generated ZKComponentProducerMethods class as a bean in turn all producer methods
			// inside this class will be used for resolving injection points
			event.addAnnotatedType(((BeanManagerImpl)manager).createAnnotatedType(c));

		} catch (NotFoundException e) {
			log.debug("Unexpected error in ZK Extension: " + e.getMessage());
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			log.debug("Unexpected error in ZK Extension: " + e.getMessage());
			throw new RuntimeException(e);
		} catch (CannotCompileException e) {
			log.debug("Unexpected error in ZK Extension: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(new Exception("Error while pre-processing ZK component injection point(s)"));
		} catch (IOException e) {
			log.debug("Unexpected error in ZK Extension: " + e.getMessage());
			throw new RuntimeException(e);
		} catch(Exception e) {
			log.debug("Unexpected error in ZK Extension: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * returns list of URL paths to application jar files in WEB-INF/lib and WEB-INF/classes
	 * @return List
	 */
    public List<URL> getUrlsForCurrentClasspath() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        //is URLClassLoader?
        if (loader instanceof URLClassLoader) {
            return ImmutableList.of(((URLClassLoader) loader).getURLs());
        }

        List<URL> urls = Lists.newArrayList();

        //get from java.class.path
        String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null) {

            for (String path : javaClassPath.split(File.pathSeparator)) {
                try {
                    urls.add(new File(path).toURI().toURL());
                } catch (Exception e) {
                    throw new ReflectionsException("could not create url from " + path, e);
                }
            }
        }

        return urls;
    }

	/**
	 * Adds ZK custom contexts such as Desktop, Page, IdSpace, Component and Execution
	 * @param event
	 * @param manager
	 */
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery event, BeanManager manager) {
		//register ZK contexts
		event.addContext(new DesktopContext());
		event.addContext(new PageContext());
		event.addContext(new IdSpaceContext());
		event.addContext(new ComponentContext());
		event.addContext(new ExecutionContext());
		log.info("ZK Weld Extension "+Version.UID);
	}
	
	/**
	 * Adds new producer method for deployment time resolution of ZK component injection points 
	 * @param field
	 * @param mainClass
	 * @param beanclas 
	 * @throws ClassNotFoundException
	 * @throws CannotCompileException
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	private void addProducerMethod(Field field, CtClass mainClass) throws ClassNotFoundException,
			CannotCompileException, NotFoundException, IOException {

		CtMethod mnew = null;
		String injectedFieldType = field.getType().getName();
		String injectedFieldName = field.getName();
		if (producerMethodKeys.contains(injectedFieldType + injectedFieldName)) {
			return;
		} else {
			producerMethodKeys.add(injectedFieldType + injectedFieldName);
		}
		// construct producer method body
		StringBuilder sb = new StringBuilder();
		sb.append("public ");
		sb.append(injectedFieldType);
		sb.append(" get");
		sb.append(injectedFieldName);
		sb.append("() {");
		sb.append(injectedFieldType + " f = null;");
		sb.append("Component c = ZkCDIIntegrationContext.getContextComponent();");
		sb.append("if(c == null) {");
//		sb.append("System.out.println(\"returning dummy instance of " +
//		injectedFieldType);
//		sb.append("\");");
		sb.append("return new " + injectedFieldType + "();} else {");
		sb.append("f = (" + injectedFieldType + ")c.getFellow(\""
				+ injectedFieldName + "\");");
		// sb.append("System.out.println(\"returning component retrieved from component tree\" + f);");
		sb.append("}");
		sb.append("return f;");
		sb.append("}");
		
		mnew = CtNewMethod.make(sb.toString(), mainClass);

		ConstPool cp = mnew.getMethodInfo().getConstPool();
		AnnotationsAttribute attr = new AnnotationsAttribute(cp,
				AnnotationsAttribute.visibleTag);

		javassist.bytecode.annotation.Annotation producesAnnotation = new javassist.bytecode.annotation.Annotation(
				"javax.enterprise.inject.Produces", cp);
		javassist.bytecode.annotation.Annotation zkScopedAnnotation = new javassist.bytecode.annotation.Annotation(
				"org.zkoss.cdi.context.IdSpaceScoped", cp);
		javassist.bytecode.annotation.Annotation zkCompAnnotation = new javassist.bytecode.annotation.Annotation(
				"org.zkoss.cdi.inject.ComponentId", cp);
		zkCompAnnotation.addMemberValue("value", new StringMemberValue(field
				.getName(), cp));
		
		attr.addAnnotation(producesAnnotation);
		attr.addAnnotation(zkScopedAnnotation);
		attr.addAnnotation(zkCompAnnotation);
		mnew.getMethodInfo().addAttribute(attr);
		
		mainClass.addMethod(mnew);
	}

	
}
