/* ComponentId.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 2, 2010 10:30:14 AM, Created by ashish
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.inject;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * used to annotate ZK component injection points inside a ZK MVC controller class.
 * @author ashish
 */
@Qualifier
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD })
@Inherited
public @interface ComponentId {
	/** component id */
	public String value();
}
