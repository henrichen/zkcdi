/* EventBinding.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 25, 2010 17:43:54 PM, Created by ashish
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.annotation.impl;

import javax.enterprise.util.AnnotationLiteral;

import org.zkoss.cdi.event.Events;

/**
 * @author ashish
 *
 */
@SuppressWarnings("serial")
public abstract class EventBinding extends AnnotationLiteral<Events> 
	implements Events {
}
