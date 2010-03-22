/* AbstractBeanStore.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 31, 2009 3:42:07 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.cdi.weld.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.weld.context.api.BeanStore;
import org.jboss.weld.context.api.ContextualInstance;

/**
 * Generic BeanStore implementation for ZK Scopes.
 * @author henrichen
 */
public class AbstractBeanStore implements BeanStore {
	private Map<String, Object> _store = new HashMap<String, Object>();
	
	public void clear() {
		_store.clear();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> ContextualInstance<T> get(String id) {
		return (ContextualInstance<T>) _store.get(id);
	}

	@Override
	public Collection<String> getContextualIds() {
		return (Collection<String>) _store.keySet();
	}

	@Override
	public <T> void put(String id, ContextualInstance<T> contextualInstance) {
		_store.put(id, contextualInstance);
	}
}
