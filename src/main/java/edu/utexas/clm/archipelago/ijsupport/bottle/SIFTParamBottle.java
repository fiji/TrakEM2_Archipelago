/*-
 * #%L
 * Fiji distribution of ImageJ for the life sciences.
 * %%
 * Copyright (C) 2013 - 2022 Fiji developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
package edu.utexas.clm.archipelago.ijsupport.bottle;

import java.io.IOException;

import mpicbg.trakem2.align.RegularizedAffineLayerAlignment;
import edu.utexas.clm.archipelago.network.MessageXC;
import edu.utexas.clm.archipelago.network.translation.Bottle;

/**
 *
 */
public class SIFTParamBottle implements Bottle
{
    private final int fdSize;
    private final int fdBins;
    private final int maxOctaveSize;
    private final int minOctaveSize;
    private final int steps;
    private final float initialSigma;

    private final float maxEpsilon;
    private final float minInlierRatio;
    private final int minNumInliers;
    private final int expectedModelIndex;
    private final boolean multipleHypotheses;
    private final boolean widestSetOnly;
    private final boolean rejectIdentity;
    private final float identityTolerance;
    private final int maxNumNeighbors;
    private final int maxNumFailures;
    private final int desiredModelIndex;
    private final int maxIterationsOptimize;
    private final int maxPlateauwidthOptimize;
    private final boolean visualize;
    private final int maxNumThreads;
    private final boolean regularize;
    private final int regularizerIndex;
    private final double lambda;

    private final float rod;
    private final boolean clearCache;
    private final int maxNumThreadsSift;

    public SIFTParamBottle(final RegularizedAffineLayerAlignment.Param param)
    {
        fdSize = param.ppm.sift.fdSize;
        fdBins = param.ppm.sift.fdBins;
        steps = param.ppm.sift.steps;
        maxOctaveSize = param.ppm.sift.maxOctaveSize;
        minOctaveSize = param.ppm.sift.minOctaveSize;
        initialSigma = param.ppm.sift.initialSigma;
        rod = param.ppm.rod;
        clearCache = param.ppm.clearCache;
        maxNumThreadsSift = param.ppm.maxNumThreadsSift;

        maxEpsilon = param.maxEpsilon;
        minInlierRatio = param.minInlierRatio;
        minNumInliers = param.minNumInliers;
        expectedModelIndex = param.expectedModelIndex;
        multipleHypotheses = param.multipleHypotheses;
        widestSetOnly = param.widestSetOnly;
        rejectIdentity = param.rejectIdentity;
        identityTolerance = param.identityTolerance;
        maxNumNeighbors = param.maxNumNeighbors;
        maxNumFailures = param.maxNumFailures;
        desiredModelIndex = param.desiredModelIndex;
        maxIterationsOptimize = param.maxIterationsOptimize;
        maxPlateauwidthOptimize = param.maxPlateauwidthOptimize;
        visualize = param.visualize;
        maxNumThreads = param.maxNumThreads;
        regularize = param.regularize;
        regularizerIndex = param.regularizerIndex;
        lambda = param.lambda;


    }

    @Override
    public Object unBottle(final MessageXC xc) throws IOException
    {
        return
                new RegularizedAffineLayerAlignment.Param(
            fdBins,
            fdSize,
            initialSigma,
            maxOctaveSize,
            minOctaveSize,
            steps,

            clearCache,
            maxNumThreadsSift,
            rod,

            desiredModelIndex,
            expectedModelIndex,
            identityTolerance,
            lambda,
            maxEpsilon,
            maxIterationsOptimize,
            maxNumFailures,
            maxNumNeighbors,
            maxNumThreads,
            maxPlateauwidthOptimize,
            minInlierRatio,
            minNumInliers,
            multipleHypotheses,
            widestSetOnly,
            regularize,
            regularizerIndex,
            rejectIdentity,
            visualize );
    }
}
