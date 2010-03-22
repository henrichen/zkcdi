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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;


@Qualifier
@Documented
@Retention(RUNTIME)
@Target({METHOD, TYPE, PARAMETER})
@Inherited
public @interface Events {
	public String value();
}
