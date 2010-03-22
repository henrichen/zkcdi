/* AbstractZKContext.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 2:54:49 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.weld.context;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.jboss.weld.context.AbstractMapContext;
import org.jboss.weld.context.api.BeanStore;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.Scope;


/**
 * Skeleton class for implement ZK related context.
 * @author henrichen
 */
public abstract class AbstractZKContext extends AbstractMapContext {
	protected final String BEAN_STORE = "zkoss.weld.BEAN_STORE";
	
	public AbstractZKContext(Class<? extends Annotation> scopeType) {
		super(scopeType);
	}
	
	protected BeanStore getBeanStore() {
		final Map attrs = getScopeAttributes();
		if (attrs != null) {
			BeanStore beanstore = (BeanStore) attrs.get(BEAN_STORE);
			if (beanstore == null) {
				beanstore = new AbstractBeanStore();
				attrs.put(BEAN_STORE, beanstore);
			}
			return beanstore;
		}
		return null;
	}
	
	protected boolean isCreationLockRequired() {
		return false;
	}
	
	public boolean isActive() {
		return getZKScope() != null;
	}
	
	private Map getScopeAttributes() {
		final Scope scope = getZKScope(); 
		return scope != null ? scope.getAttributes() : null;
	}
	
	protected Scope getZKScope() {
		final Execution exec = Executions.getCurrent();
		if (exec != null) {
			final VariableResolver vresolver = exec.getVariableResolver();
			if (vresolver != null) {
				return (Scope) vresolver.resolveVariable(getZKScopeName());
			}
		}
		return null;
	}
	
	abstract protected String getZKScopeName();
}