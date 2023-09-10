/**********************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matthew Khouzam - Initial API and implementation
 *   Alexandre Montplaisir - Add UST callstack state system
 *   Marc-Andre Laperle - Handle BufferOverflowException (Bug 420203)
 **********************************************************************/

package org.pinsight.core.trace;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.pinsight.core.Activator;
import org.pinsight.core.aspect.PInsightBinaryAspect;
import org.pinsight.core.aspect.PInsightFunctionAspect;
import org.pinsight.core.aspect.PInsightSourceAspect;
import org.pinsight.omp.trace.PInsightOmpEventLayout;
import org.eclipse.tracecompass.lttng2.ust.core.trace.Messages;
import org.eclipse.tracecompass.lttng2.ust.core.trace.layout.ILttngUstEventLayout;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.aspect.ITmfEventAspect;
import org.eclipse.tracecompass.tmf.core.event.lookup.TmfCallsite;
import org.eclipse.tracecompass.tmf.core.exceptions.TmfTraceException;
import org.eclipse.tracecompass.tmf.core.trace.TmfEventTypeCollectionHelper;
import org.eclipse.tracecompass.tmf.core.trace.TraceValidationStatus;
import org.eclipse.tracecompass.tmf.ctf.core.event.CtfTmfEventFactory;
import org.eclipse.tracecompass.tmf.ctf.core.trace.CtfTmfTrace;
import org.eclipse.tracecompass.tmf.ctf.core.trace.CtfTraceValidationStatus;
import org.eclipse.tracecompass.internal.lttng2.common.core.trace.ILttngTrace;
import org.eclipse.tracecompass.lttng2.ust.core.trace.LttngUstTrace;
import org.eclipse.tracecompass.lttng2.ust.core.trace.LttngUstTrace.SymbolProviderConfig;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.BinaryCallsite;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.FunctionLocation;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.UstDebugInfoBinaryAspect;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.UstDebugInfoSourceAspect;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.UstDebugInfoFunctionAspect;

import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.ContextVpidAspect;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.ContextVtidAspect;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.UstTracefAspect;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst29EventLayout;

import org.pinsight.core.aspect.PInsightAspectContainer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * Class to contain LTTng-UST traces
 *
 * @author Matthew Khouzam
 */
public class PInsightTrace extends CtfTmfTrace implements ILttngTrace {

    /**
     * Name of the tracer that generates this trace type, as found in the CTF
     * metadata.
     *
     * @since 2.0
     */
    public static final String TRACER_NAME = "lttng-ust"; //$NON-NLS-1$

    private static final int CONFIDENCE = 100;

    private static final @NonNull Collection<ITmfEventAspect<?>> PINSIGHT_ASPECTS;
    

    static {
        ImmutableSet.Builder<ITmfEventAspect<?>> builder = ImmutableSet.builder();
        builder.addAll(CtfTmfTrace.CTF_ASPECTS);
        
        builder.add(
        	new PInsightAspectContainer<PInsightBinaryAspect, BinaryCallsite, PInsightOmpEventLayout>("PInsight OMP Binary Location", "", PInsightBinaryAspect.INSTANCE)
        );     
        builder.add(
            new PInsightAspectContainer<PInsightSourceAspect, TmfCallsite, PInsightOmpEventLayout>("PInsight OMP Source Location", "", PInsightSourceAspect.INSTANCE)
        );  
        builder.add(
            new PInsightAspectContainer<PInsightFunctionAspect, FunctionLocation, PInsightOmpEventLayout>("PInsight OMP Function Location", "", PInsightFunctionAspect.INSTANCE)
        );  
        
        builder.add(
        	new PInsightAspectContainer<PInsightBinaryAspect, BinaryCallsite, PInsightOmpEventLayout>("PInsight MPI Binary Location", "", PInsightBinaryAspect.INSTANCE)
        );     
        builder.add(
            new PInsightAspectContainer<PInsightSourceAspect, TmfCallsite, PInsightOmpEventLayout>("PInsight MPI Source Location", "", PInsightSourceAspect.INSTANCE)
        );  
        builder.add(
            new PInsightAspectContainer<PInsightFunctionAspect, FunctionLocation, PInsightOmpEventLayout>("PInsight MPI Function Location", "", PInsightFunctionAspect.INSTANCE)
        );  
            
        builder.add(
        	new PInsightAspectContainer<PInsightBinaryAspect, BinaryCallsite, PInsightOmpEventLayout>("PInsight CUDA Binary Location", "", PInsightBinaryAspect.INSTANCE)
        );     
        builder.add(
            new PInsightAspectContainer<PInsightSourceAspect, TmfCallsite, PInsightOmpEventLayout>("PInsight CUDA Source Location", "", PInsightSourceAspect.INSTANCE)
        );  
        builder.add(
            new PInsightAspectContainer<PInsightFunctionAspect, FunctionLocation, PInsightOmpEventLayout>("PInsight CUDA Function Location", "", PInsightFunctionAspect.INSTANCE)
        );  
        //builder.add(PInsightBinaryAspect.INSTANCE);
        //builder.add(PInsightSourceAspect.INSTANCE);
        //builder.add(PInsightFunctionAspect.INSTANCE);
        PINSIGHT_ASPECTS = builder.build();
    }

    /** Default collections of aspects */
    private @NonNull Collection<ITmfEventAspect<?>> fPinsightTraceAspects = ImmutableSet.copyOf(PINSIGHT_ASPECTS);

    /**
     * Default constructor
     */
    public PInsightTrace() {
        super(PInsightEventFactory.instance());
    }

    /**
     * Protected constructor for child classes. Classes extending this one may
     * have extra fields coming from the event itself and may pass their own
     * event factory.
     *
     * @param factory
     *            The event factory for this specific trace
     * @since 3.0
     */
    protected PInsightTrace(@NonNull CtfTmfEventFactory factory) {
        super(factory);
    }


    @Override
    public void initTrace(IResource resource, String path,
            Class<? extends ITmfEvent> eventType) throws TmfTraceException {
        super.initTrace(resource, path, eventType);

        ImmutableSet.Builder<ITmfEventAspect<?>> builder = ImmutableSet.builder();
        builder.addAll(PINSIGHT_ASPECTS);
        
        ILttngUstEventLayout layout = LttngUst29EventLayout.getInstance();
        
        if (checkFieldPresent(layout.contextVtid())) {
            builder.add(new ContextVtidAspect(layout));
        }
        if (checkFieldPresent(layout.contextVpid())) {
            builder.add(new ContextVpidAspect(layout));
        }
        if (getContainedEventTypes().stream().anyMatch(potentialEvent -> potentialEvent.getName().startsWith(layout.eventTracefPrefix()))) {
            builder.add(UstTracefAspect.getInstance());
        }
        
        builder.addAll(createCounterAspects(this));
        fPinsightTraceAspects = builder.build();
    }

    private boolean checkFieldPresent(@NonNull String field) {
        final Multimap<@NonNull String, @NonNull String> traceEvents = TmfEventTypeCollectionHelper.getEventFieldNames((getContainedEventTypes()));

        return traceEvents.containsValue(field);
    }


    @Override
    public Iterable<ITmfEventAspect<?>> getEventAspects() {
        return fPinsightTraceAspects;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation sets the confidence to 100 if the trace is a valid
     * CTF trace in the "ust" domain.
     */
    @Override
    public IStatus validate(final IProject project, final String path) {
        IStatus status = super.validate(project, path);
        if (status instanceof CtfTraceValidationStatus) {
            Map<String, String> environment = ((CtfTraceValidationStatus) status).getEnvironment();
            /* Make sure the domain is "ust" in the trace's env vars */
            String domain = environment.get("domain"); //$NON-NLS-1$
            if (domain == null || !domain.equals("\"ust\"")) { //$NON-NLS-1$
                return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.LttngUstTrace_DomainError);
            }
            return new TraceValidationStatus(CONFIDENCE, Activator.PLUGIN_ID);
        }
        return status;
    }

    // ------------------------------------------------------------------------
    // Fields/methods bridging the Debug-info symbol provider
    // ------------------------------------------------------------------------

    private @NonNull SymbolProviderConfig fCurrentProviderConfig =
            /* Default settings for new traces */
            new SymbolProviderConfig(false, ""); //$NON-NLS-1$


    /**
     * Get the current symbol provider configuration for this trace.
     *
     * @return The current symbol provider configuration
     * @since 2.0
     */
    //@Override
    public @NonNull SymbolProviderConfig getSymbolProviderConfig() {
        return fCurrentProviderConfig;
    }

    /**
     * Set the symbol provider configuration for this trace.
     *
     * @param config
     *            The new symbol provider configuration to use
     * @since 2.0
     */
    //@Override
    public void setSymbolProviderConfig(@NonNull SymbolProviderConfig config) {
        fCurrentProviderConfig = config;
        PInsightBinaryAspect.invalidateSymbolCache();
    }
    
}
