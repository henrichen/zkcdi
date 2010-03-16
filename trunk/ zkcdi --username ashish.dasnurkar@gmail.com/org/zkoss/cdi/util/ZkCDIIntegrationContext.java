/**
 * 
 */
package org.zkoss.cdi.util;

import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.xel.impl.ExecutionResolver;

/**
 * @author ashish
 *
 */
public class ZkCDIIntegrationContext {

	private static ThreadLocal<Component> components = new ThreadLocal<Component>();

	public static Component getContextComponent() {
		return components.get();
	}

	public static void setContextComponent(Component component) {
		components.set(component);
	}
	
	public static void setSelfContextComponent(Component component) {
		final Execution exec = Executions.getCurrent();
		final VariableResolver vresolver = exec.getVariableResolver();
		((ExecutionResolver)vresolver).setSelf(component);
	}
}
