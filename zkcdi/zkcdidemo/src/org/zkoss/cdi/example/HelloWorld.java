/**
 * 
 */
package org.zkoss.cdi.example;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.zkoss.cdi.event.Events;
import org.zkoss.cdi.inject.ComponentId;
import org.zkoss.cdi.util.GenericComposer;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * @author ashish
 *
 */
@Named
@SessionScoped
public class HelloWorld extends GenericComposer implements Serializable {

	@Inject @ComponentId("guestName") Textbox guestName;
	@Inject @ComponentId("sayHelloBtn") Button sayHelloBtn;
	@Inject @ComponentId("helloWindow") Window helloWindow;
	
	public void sayHello(@Observes @Events("sayHelloBtn.onClick") MouseEvent evt) {
		helloWindow.setTitle("Hello " + guestName.getValue());
	}
}
