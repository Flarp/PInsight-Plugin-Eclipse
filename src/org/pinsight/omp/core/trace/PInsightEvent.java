package org.pinsight.omp.core.trace;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.ctf.core.event.IEventDeclaration;
import org.eclipse.tracecompass.ctf.core.event.IEventDefinition;
import org.eclipse.tracecompass.tmf.core.event.lookup.ITmfCallsite;
import org.eclipse.tracecompass.tmf.core.timestamp.ITmfTimestamp;
import org.eclipse.tracecompass.tmf.ctf.core.trace.CtfTmfTrace;
import org.eclipse.tracecompass.lttng2.ust.core.trace.LttngUstEvent;

import org.pinsight.omp.core.aspect.PInsightSourceAspect;

@NonNullByDefault
public class PInsightEvent extends LttngUstEvent {

	public PInsightEvent() {
		super();
	}
	
	protected PInsightEvent(CtfTmfTrace trace, long rank, ITmfTimestamp timestamp,
            String channel, int cpu, IEventDeclaration declaration, IEventDefinition eventDefinition) {
        super(trace, rank, timestamp, channel, cpu, declaration, eventDefinition);
    }
	
	@Override
    public @Nullable ITmfCallsite getCallsite() {
        return PInsightSourceAspect.INSTANCE.resolve(this);
    }

}
