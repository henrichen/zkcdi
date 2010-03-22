/* PageContext.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 4:30:55 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.weld.context;

import org.zkoss.cdi.context.PageScoped;

/**
 * Page context for {@link PageScoped}.
 * @author henrichen
 */
public class PageContext extends AbstractZKContext {
	public PageContext() {
		super(PageScoped.class);
	}
	protected String getZKScopeName() {
		return "page";
	}
}
