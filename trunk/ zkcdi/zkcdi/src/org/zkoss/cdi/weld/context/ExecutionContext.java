/* ExecutionContext.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 5:13:25 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.weld.context;

import org.zkoss.cdi.context.ExecutionScoped;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.Scope;

/**
 * Execution context for {@link ExecutionScoped}.
 * @author henrichen
 */
public class ExecutionContext extends AbstractZKContext {
	public ExecutionContext() {
		super(ExecutionScoped.class);
	}
	protected Scope getZKScope() {
		return Executions.getCurrent();
	}
	protected String getZKScopeName() {
		//dummy method. Never call here.
		return null;
	}
}
