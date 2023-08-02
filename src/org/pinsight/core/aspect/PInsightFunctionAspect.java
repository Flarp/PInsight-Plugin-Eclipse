/*******************************************************************************
 * Copyright (c) 2016 EfficiOS Inc., Alexandre Montplaisir
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.pinsight.core.aspect;

import static org.eclipse.tracecompass.common.core.NonNullUtils.nullToEmptyString;

import java.io.File;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.lttng2.ust.core.analysis.debuginfo.FileOffsetMapper;
import org.eclipse.tracecompass.internal.lttng2.ust.core.analysis.debuginfo.UstDebugInfoSymbolProvider;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.BinaryCallsite;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.Messages;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.FunctionLocation;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.aspect.ITmfEventAspect;
import org.pinsight.core.analysis.PInsightSymbolProvider;
import org.pinsight.core.aspect.PInsightBinaryAspect;
import org.pinsight.core.trace.PInsightTrace;

/**
 * Aspect for the function location obtained with the UST debug info.
 *
 * @author Alexandre Montplaisir
 * @since 2.0
 */
public class PInsightFunctionAspect implements ITmfEventAspect<FunctionLocation> {

    /** Singleton instance */
    public static final PInsightFunctionAspect INSTANCE = new PInsightFunctionAspect();

    private PInsightFunctionAspect() {
        // Do nothing
    }

    @Override
    public String getName() {
        return nullToEmptyString(Messages.UstDebugInfoAnalysis_FunctionAspectName);
    }

    @Override
    public String getHelpText() {
        return nullToEmptyString(Messages.UstDebugInfoAnalysis_FunctionAspectHelpText);
    }

    @Override
    public @Nullable FunctionLocation resolve(ITmfEvent event) {
    	if (!(event.getTrace() instanceof PInsightTrace)) {
            return null;
        }
    	
        /* Resolve the binary callsite first. */
        BinaryCallsite bc = PInsightBinaryAspect.INSTANCE.resolve(event);
        if (bc == null) {
            return null;
        }

        String functionName = PInsightSymbolProvider.getFunctionNameFromSS(bc, event.getTrace());
        /*
         * Return function information only if it is non null, otherwise try to
         * resolve the symbol in another way (see code below).
         */
        if (functionName != null) {
            return new FunctionLocation(functionName, null);
        }

        return getFunctionFromBinaryLocation(bc);
    }

    /**
     * Get a function location starting directly from a binary callsite, instead
     * of from a trace event.
     *
     * @param bc
     *            The binary callsite, representing a binary and offset within
     *            this binary
     * @return The corresponding function location
     * @since 2.1
     */
    public static @Nullable FunctionLocation getFunctionFromBinaryLocation(BinaryCallsite bc) {
        String functionName = FileOffsetMapper.getFunctionNameFromOffset(
                new File(bc.getBinaryFilePath()),
                bc.getBuildId(),
                bc.getOffset());
        if (functionName == null) {
            return null;
        }

        /* We do not track the offset inside the function at this time */
        return new FunctionLocation(functionName, null);
    }

}
