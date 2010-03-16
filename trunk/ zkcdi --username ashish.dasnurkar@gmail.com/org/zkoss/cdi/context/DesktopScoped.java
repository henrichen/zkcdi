/* DesktopScoped.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 1:27:16 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.cdi.context;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.NormalScope;

/**
 * <p>Specifies that a bean is desktop scoped.</p>
 * <p>The desktop context is active whenever a desktop is active.</p>
 *
 * @author henrichen
 */
@Target({TYPE, METHOD, FIELD })
@Retention(RUNTIME)
@Documented
@NormalScope(passivating = true)
@Inherited
public @interface DesktopScoped {
}
