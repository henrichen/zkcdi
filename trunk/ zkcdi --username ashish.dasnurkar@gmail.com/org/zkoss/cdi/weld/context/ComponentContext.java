/* ComponentContext.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 4:30:55 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.weld.context;

import org.zkoss.cdi.context.ComponentScoped;

/**
 * Component context for {@link ComponentScoped}.
 * @author henrichen
 */
public class ComponentContext extends AbstractZKContext {
	public ComponentContext() {
		super(ComponentScoped.class);
	}
	protected String getZKScopeName() {
		return "self";
	}
}
