/* IdSpaceContext.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 4:10:23 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.weld.context;

import org.zkoss.cdi.context.IdSpaceScoped;

/**
 * IdSpace context for {@link IdSpaceScoped}.
 * @author henrichen
 */
public class IdSpaceContext extends AbstractZKContext {
	public IdSpaceContext() {
		super(IdSpaceScoped.class);
	}
	protected String getZKScopeName() {
		return "spaceOwner";
	}
}
