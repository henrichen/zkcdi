/* DesktopContext.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 3:14:30 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.weld.context;

import org.zkoss.cdi.context.DesktopScoped;

/**
 * Desktop context for {@link DesktopScoped}.
 * @author henrichen
 */
public class DesktopContext extends AbstractZKContext {
	public DesktopContext() {
		super(DesktopScoped.class);
	}
	protected String getZKScopeName() {
		return "desktop";
	}
}
