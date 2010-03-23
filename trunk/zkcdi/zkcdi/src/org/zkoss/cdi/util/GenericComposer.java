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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.zkoss.cdi.annotation.impl.EventBinding;
import org.zkoss.cdi.event.Events;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Composer;


/**
 * <p>An abstract comopser that you can extend and use JEE6 platform CDI features such as dependancy injection, 
 * custom context representing ZK scopes and use CDI notification model to write event handling methods. 
 * Any subclass of this class can be applied to ZK component using "apply" attribute providing EL expression.
 * Using CDI Inject annotation along with ZK custom ComponentId annotation you can inject child components 
 * of parent component to which subclass is applied. You can also use CDI defined Observes annotation for 
 * method parameters along with ZK custom Events annotation to write your event handler methods for 
 * specific events such as MouseEvent or SelectEvent.</p>
 * 
 * <p>Following is an example of subclass that extends from GenericComposer and uses above mentioned CDI features.
 * </p>
 * <pre><code>
 * MyComposer.java
 * 
 * {@link javax.inject.Named @Named}
 * {@link javax.enterprise.context.SessionScoped @SessionScoped}
 * public void MyComposer extends GenericComposer implements Serializable {
 *      {@link javax.inject.Inject @Inject} {@link org.zkoss.cdi.inject.ComponentId @ComponentId}("myLabel") Label myLabel;
 *      {@link javax.inject.Inject @Inject} {@link org.zkoss.cdi.inject.ComponentId @ComponentId}("myTextbox") Textbox myTextbox;
 *      {@link javax.inject.Inject @Inject} {@link org.zkoss.cdi.inject.ComponentId @ComponentId}("myButton") Button myButton;
 * 
 *      public void sayHello({@link javax.enterprise.event.Observes @Observes} {@link org.zkoss.cdi.event.Events @Events}("myButton.onClick") MouseEvent evt) {
 *          myLabel.setValue("You just entered: "+ myTextbox.getValue());
 *      }
 * }
 * 
 * hello.zul
 * 
 * &lt;window id="mywin" apply="${myComposer}"&gt;
 *     &lt;textbox id="myTextbox"/&gt;
 *     &lt;textbox id="myButton"/&gt;
 *     &lt;label id="myLabel"/&gt;
 * &lt;/window&gt;
 * </code></pre>
 * @author ashish
 */
abstract public class GenericComposer implements Composer, EventListener {

	private static final Log log = Log.lookup(GenericComposer.class);

	/** javax.enterprise.event.Event instance used to publish ZK Event(s)*/
	@Inject	private javax.enterprise.event.Event<Event> zkevent;
	
	/** map of individual componet events and associated Events annotation values */
	private Map<String,List<String>> eventsMap = null;

	/**
	 * Auto inject ZK compoents referenced as composer fields. Setup composer as ZK event publisher.
	 * Forward child component events to composer.
	 * @param comp parent Component
	 * @throws Exception
	 */
	public void doAfterCompose(Component comp) throws Exception {
		try {
			// to setup context component i.e. component to which this composer is applied
			ZkCDIIntegrationContext.setContextComponent(comp);
			ZkCDIIntegrationContext.setSelfContextComponent(comp);
			// to trigger zk component injection
			accessFields();

			// to setup this composer as CDI event publisher
			setupControllerAsEventPublisher(comp, this);
			
			// forward all child events to this composer
			addForwards(comp, this);
		} finally {
			ZkCDIIntegrationContext.clearContextComponent();
		}
	}

	/**
	 * accesses individual fields of composer instance to trigger component injection
	 * @throws IllegalAccessException
	 */
	private void accessFields() throws IllegalAccessException {
		Class cls = this.getClass();
		Field[] flds = cls.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		for (int j = 0; j < flds.length; ++j) {
			Field f = flds[j];
			f.setAccessible(true);
			Object o = f.get(this);
			sb.append(o == null ? "" : o.toString());
		}
	}

	/**
	 * sets this composer as event listner to all child events specified 
	 * in Events annotation
	 * @param comp
	 * @param controller
	 */
	private void setupControllerAsEventPublisher(Component comp, Object controller) {
		final Method[] metds = getController().getClass().getMethods();
		eventsMap = new HashMap<String, List<String>>();
		for (int i = 0; i < metds.length; i++) {
			final Method md = metds[i];
			String mdname = md.getName();
			if (mdname.equals("onEvent")) {
				comp.addEventListener(mdname, this);
			} else {
				String annotationValue = getEventsParameterAnnotation(md);
				processEventsAnnotation(comp, annotationValue);						
			}
		}
	}

	/**
	 * adds this composer as event listner for each event specified in Events annotation value
	 * @param comp
	 * @param annotationValue
	 */
	private void processEventsAnnotation(Component comp, String annotationValue) {
		if (annotationValue == null) {
			return;
		}
		List<String> annotationValueTokens = (List<String>) CollectionsX.parse(new ArrayList<String>(), annotationValue, ',');
		for (String annotationValueToken : annotationValueTokens) {
			String srccompid = annotationValueToken.substring(0, annotationValueToken.indexOf('.'));
			String srcevt  = annotationValueToken.substring(annotationValueToken.indexOf('.') + 1, annotationValueToken.length());
			String eventName = srcevt + "." + srccompid;
			List<String> annotationSelector = eventsMap.get(eventName); 
			if ( annotationSelector == null ) {
				annotationSelector = new ArrayList<String>();
				annotationSelector.add(annotationValue);
				eventsMap.put(eventName, annotationSelector);
			} else {
				annotationSelector.add(annotationValue);
				eventsMap.put(eventName, annotationSelector);
			}
			comp.addEventListener(eventName, this);
		}
	}

	/**
	 * returns Events annotation value if present on any method parameter
	 * @param md
	 * @return annotationValue if any parameter is annotated with Events annotation returns its value or else returns null
	 */
	private String getEventsParameterAnnotation(Method md) {
		Annotation[][] anex = md.getParameterAnnotations();
		for (int j = 0; j < anex.length; j++) {
			Annotation[] annotations = anex[j];
			if (annotations != null && annotations.length > 1) {
				Annotation a = annotations[1];
				if (a instanceof Events) {
					return ((Events) a).value();
				}
			}
		}
		return null;
	}

	/**
	 * for each individual event fires an CDI event 
	 * @param evt 
	 */
	@SuppressWarnings("serial")
	@Override
	public void onEvent(Event evt) throws Exception {
		final Event evt1 = evt;
		Component originalSelf = null;
		try {
			if (evt != null && evt.getName() != null) {
				originalSelf = ZkCDIIntegrationContext.getSelfContextComponent(); 
				ZkCDIIntegrationContext.setSelfContextComponent(evt.getTarget());
				Event evtOrig = org.zkoss.zk.ui.event.Events.getRealOrigin((ForwardEvent) evt); 
				
				final List<String> annotationSelectors = eventsMap.get(evt
						.getName());
				for (Iterator iterator = annotationSelectors.iterator(); iterator
						.hasNext();) {
					final String annotationSelector = (String) iterator.next();
					zkevent.select(new EventBinding() {
						public String value() {
							return annotationSelector;
						}
					}).fire(evtOrig);
				}
			}
		} finally {
			ZkCDIIntegrationContext.setSelfContextComponent(originalSelf);
		}
	}
	
	protected Object getController() {
		return this;
	}

	/**
	 * forwards component events to controller methods annotated with Events qualifier
	 * @param comp
	 * @param controller
	 */
	@SuppressWarnings("unchecked")
	public void addForwards(Component comp, Object controller) {
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
	
	@SuppressWarnings("unchecked")
	public void addForward(Method md, Annotation a, Component comp) {
		String mdname = md.getName();
		Component xcomp = comp;
		String annotationValue = ((Events) a).value();
		List<String> annotationValueTokens = (List<String>) CollectionsX.parse(
				new ArrayList<String>(), annotationValue, ',');
		for (String annotationValueToken : annotationValueTokens) {
			String srccompid = annotationValueToken.substring(0,
					annotationValueToken.indexOf('.'));
			String srcevt = annotationValueToken.substring(annotationValueToken
					.indexOf('.') + 1, annotationValueToken.length());

			// TODO: get component instance from bean manager
			// try EL resolver or check any api/spi interface
			Object srccomp = xcomp.getAttributeOrFellow(srccompid, true);
			if (srccomp == null) {
				Page page = xcomp.getPage();
				if (page != null)
					srccomp = page.getXelVariable(null, null, srccompid, true);
			}
			if (srccomp == null || !(srccomp instanceof Component)) {
				log.debug("Cannot find the associated component to forward event: "
								+ mdname);
			} else {
				((Component) srccomp).addForward(srcevt, xcomp, srcevt + "."
						+ srccompid);
			}
		}
	}
}
