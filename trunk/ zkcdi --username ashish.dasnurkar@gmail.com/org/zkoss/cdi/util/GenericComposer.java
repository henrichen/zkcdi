/* GenericComposer.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 2, 2010 10:30:14 AM, Created by ashish
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.zkoss.cdi.annotation.impl.EventBinding;
import org.zkoss.cdi.event.Events;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;


/**
 * @author ashish
 *
 */
public class GenericComposer implements Composer, EventListener {

	/**
	 * 
	 */
	@Inject	private javax.enterprise.event.Event<Event> zkevent;

	/**
	 * 
	 * @param comp
	 * @param eventsComposer
	 * @throws Exception
	 */
	public void doAfterCompose(Component comp) throws Exception {
		// set context component for later use during the component injection using producer methods
		ZkCDIIntegrationContext.setContextComponent(comp);
		ZkCDIIntegrationContext.setSelfContextComponent(comp);
		bindComponents(comp, this);
		bindEventHandlers(comp, this);
	}

	/**
	 * 
	 * @param comp
	 * @param controller
	 */
	private void bindComponents(Component comp, Object controller) {
		final Method[] metds = getController().getClass().getMethods();
		for (int i = 0; i < metds.length; i++) {
			final Method md = metds[i];
			String mdname = md.getName();
			if (mdname.equals("onEvent")) {
				comp.addEventListener(mdname, this);
			} else {
				Annotation[][] anex = md.getParameterAnnotations();
				for (int j = 0; j < anex.length; j++) {
					Annotation[] annotations = anex[j];
					if (annotations != null && annotations.length > 1) {
						Annotation a = annotations[1];
						if (a instanceof Events) {
							String annotationValue = ((Events) a).value();
							String srccompid = annotationValue.substring(0,
									annotationValue.indexOf('.'));
							String srcevt = annotationValue.substring(
									annotationValue.indexOf('.') + 1,
									annotationValue.length());
							comp.addEventListener(srcevt + "." + srccompid,
									this);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("serial")
	@Override
	public void onEvent(Event evt) throws Exception {
		final Event evt1 = evt;
		if (evt != null && evt.getName() != null) {

			ZkCDIIntegrationContext.setContextComponent(evt.getTarget());
			ZkCDIIntegrationContext.setSelfContextComponent(evt.getTarget());

			zkevent.select(new EventBinding() {
				public String value() {
					String annotationValue = evt1.getName();
					String srccompid = annotationValue.substring(0,
							annotationValue.indexOf('.'));
					String srcevt = annotationValue.substring(annotationValue
							.indexOf('.') + 1, annotationValue.length());
					return srcevt + "." + srccompid;
				}
			}).fire(evt);
		}
	}
	
	protected Object getController() {
		return this;
	}

	/** Shortcut to call Messagebox.show(String).
	 * @since 3.0.7 
	 */
	private static Method _alert;
	protected void alert(String m) {
		//zk.jar cannot depends on zul.jar; thus we call Messagebox.show() via
		//reflection. kind of weird :-).
		try {
			if (_alert == null) {
				final Class mboxcls = Classes.forNameByThread("org.zkoss.zul.Messagebox");
				_alert = mboxcls.getMethod("show", new Class[] {String.class});
			}
			_alert.invoke(null, new Object[] {m});
		} catch (InvocationTargetException e) {
			throw UiException.Aide.wrap(e);
		} catch (Exception e) {
			//ignore
		}
	}
	
	/**
	 * forwards component events to controller methods annotated with Events qualifier
	 * @param comp
	 * @param controller
	 */
	@SuppressWarnings("unchecked")
	public static void bindEventHandlers(Component comp, Object controller) {
		// TODO Auto-generated method stub
		final Class cls = controller.getClass();
		final Method[] mtds = cls.getMethods();
		for (int j = 0; j < mtds.length; ++j) {
			final Method md = mtds[j];
			Annotation[][] anex = md.getParameterAnnotations();
			for (int k = 0; k < anex.length; k++) {
				Annotation[] annotations = anex[k];
				if (annotations != null && annotations.length > 1) {
					Annotation a = annotations[1];
					if (a instanceof Events) {
						addForward(md, a, comp);
					}
				}
			}
		}		
	}
	/**
	 * method to add forward event from child component to parent or composer component
	 * @param md method annotated with EventHandler annotation
	 * @param a EventHandler Annotation on md method
	 * @param comp
	 */
	
	public static void addForward(Method md, Annotation a, Component comp) {
		String mdname = md.getName();
		Component xcomp = comp;
		String annotationValue = ((Events)a).value();
		final String srccompid = annotationValue.substring(0, annotationValue.indexOf('.'));
		final String srcevt  = annotationValue.substring(annotationValue.indexOf('.') + 1, annotationValue.length());
		
		// get component instance from bean manager
		// try EL resolver or check any api/spi interface
		Object srccomp = xcomp.getAttributeOrFellow(srccompid, true);
		if (srccomp == null) {
			Page page = xcomp.getPage();
			if (page != null)
				srccomp = page.getXelVariable(null, null, srccompid, true);
		}
		if (srccomp == null || !(srccomp instanceof Component)) {
				System.out.println("Cannot find the associated component to forward event: "+mdname);
		} else {
			((Component)srccomp).addForward(srcevt, xcomp, srcevt + "." + srccompid);
		}
	}
}
