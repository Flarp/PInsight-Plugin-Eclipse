package org.pinsight.core.aspect;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.lttng2.ust.core.trace.layout.ILttngUstEventLayout;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.aspect.ITmfEventAspect;
import org.pinsight.core.trace.PInsightEvent;

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
