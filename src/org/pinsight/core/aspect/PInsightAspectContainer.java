package org.pinsight.core.aspect;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.lttng2.ust.core.trace.layout.ILttngUstEventLayout;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.aspect.ITmfEventAspect;
import org.pinsight.core.trace.PInsightEvent;

/**
 * 
 * @author Ethan Dorta
 * 
 * This class is an aspect that wraps another aspect to allow for a kind of "type-checking"
 * before it is ran. This is overly complicated in a way and could be done a lot better, but
 * as a minimum viable product, it works. 
 *
 * @param <A> The aspect that is being wrapped
 * @param <T> The return type of calling "resolve" on the above aspect
 * @param <E> The acceptable event layout type
 */
public class PInsightAspectContainer
	<A extends ITmfEventAspect<T>, 
	T, 
	E extends ILttngUstEventLayout> 
implements ITmfEventAspect<T> {
	
	String name;
	String helpText;
	A instance;
	Class<E> encapsulate;
	E layout;
	
	public PInsightAspectContainer(String _name, String _helpText, A _instance) {
		name = _name;
		helpText = _helpText;
		instance = _instance;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getHelpText() {
		return helpText;
	}

	@Override
	public @Nullable T resolve(ITmfEvent event) {
		PInsightEvent castEvent = (PInsightEvent) event;
		if (castEvent == null || castEvent.getClass().isInstance(encapsulate)) return null;
		
		return instance.resolve(event);
	}

}
