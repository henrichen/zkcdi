/* Events.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 07, 2010 14:04:54 PM, Created by ashish
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/

package org.zkoss.cdi.event;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.event.Observes;
import javax.inject.Qualifier;

/**
 * <p>Used as method parameter annotation along with {@link javax.enterprise.event.Observes @Observes}
 * The value indicates observed event in the form of target component 
 * and event name for eg. for a onClick event of a button component 
 * with id myButton Events annotation can be used in following manner 
 * </p>
 * <pre><code>
 *    public void sayHello({@link javax.enterprise.event.Observes @Observes} {@link org.zkoss.cdi.event.Events @Events}("myButton.onClick") MouseEvent evt) {
 *      ...
 *     }
 * </code></pre>
 * 
 * <p>Multiple events can also be specified by separating them using comma for example</p>
 * 
 * <pre><code>
 *     public void sayHello({@link javax.enterprise.event.Observes @Observes} {@link org.zkoss.cdi.event.Events @Events}("myButton.onClick,myButton1.onClick") MouseEvent evt) {
 *       ...
 *    }
 * </code></pre>
 * 
 * @author ashish
 * @see Observes
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({PARAMETER})
@Inherited
public @interface Events {
	/** Event specified in the form of component id followed by a dot (.) followed by event name. 
	 * Multiple events can be separated by comma */
	public String value();
}
