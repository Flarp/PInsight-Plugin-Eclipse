package org.pinsight.core.trace;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.ctf.core.event.IEventDeclaration;
import org.eclipse.tracecompass.ctf.core.event.IEventDefinition;
import org.eclipse.tracecompass.tmf.core.event.lookup.ITmfCallsite;
import org.eclipse.tracecompass.tmf.core.timestamp.ITmfTimestamp;
import org.eclipse.tracecompass.tmf.ctf.core.trace.CtfTmfTrace;
import org.pinsight.core.aspect.PInsightSourceAspect;
import org.pinsight.omp.trace.PInsightOmpEventLayout;
import org.eclipse.tracecompass.lttng2.ust.core.trace.LttngUstEvent;
import org.eclipse.tracecompass.lttng2.ust.core.trace.layout.ILttngUstEventLayout;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst29EventLayout;

/**
 * 
 * @author Ethan Dorta
 *
 * This class wraps around the LttngUstEvent (the event type for LTTng-UST traces)
 * and assigns different event layouts depending on what type of event we are
 * dealing with. Event layouts are used to determine where to find pieces of information
 * that different aspects require.
 */
@NonNullByDefault
public class PInsightEvent extends LttngUstEvent {
	
	private ILttngUstEventLayout layout;
	
	public PInsightEvent() {
		super();
	}
	
	protected PInsightEvent(CtfTmfTrace trace, long rank, ITmfTimestamp timestamp,
            String channel, int cpu, IEventDeclaration declaration, IEventDefinition eventDefinition) {
        super(trace, rank, timestamp, channel, cpu, declaration, eventDefinition);
        
        if (declaration.getName().startsWith("ompt")) {
        	layout = PInsightOmpEventLayout.getInstance();
        } else {
        	layout = LttngUst29EventLayout.getInstance();
        }
    }
	
	@Override
    public @Nullable ITmfCallsite getCallsite() {
		return PInsightSourceAspect.INSTANCE.resolve(this);
	}
	
	public @NonNull ILttngUstEventLayout getLayout() {
		return layout;
	}

}
