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

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import mpicbg.models.Point;
import edu.utexas.clm.archipelago.network.MessageXC;
import edu.utexas.clm.archipelago.network.translation.Bottle;

/**
 * A PointBottle to support synchronized point objects across cluster nodes
 */
public class PointBottle implements Bottle<Point>
{
    public class IdPoint extends Point
    {
        private final long id;

        public IdPoint(final long id, final Point pt)
        {
            super(pt.getL(), pt.getW());
            this.id = id;
        }

        public long getID()
        {
            return id;
        }
    }


    private static final Map<Long, Point> idPointMap =
            Collections.synchronizedMap(new HashMap<Long, Point>());

    private static final Map<Point, Long> pointIdMap =
            Collections.synchronizedMap(new IdentityHashMap<Point, Long>());
    private static final ReentrantLock idPointLock = new ReentrantLock();
    private static final ReentrantLock idIdLock = new ReentrantLock();
    private static final AtomicLong idGenerator = new AtomicLong(1);

    private static void mapIdToPoint(final Point point, final long original)
    {
        idIdLock.lock();

        pointIdMap.put(point, original);

        idIdLock.unlock();
    }

    private static long getId(final Point point, final long idDefault)
    {
        final Long id;
        idIdLock.lock();

        id = pointIdMap.get(point);

        idIdLock.unlock();

        return id == null ? idDefault : id;
    }

    private static void mapPointToID(final long orig, final Point point)
    {
        idPointLock.lock();

        idPointMap.put(orig, point);

        idPointLock.unlock();
    }

    private static boolean existsOrPut(final long id, final Point point)
    {
        idPointLock.lock();

        if (idPointMap.containsKey(id))
        {
            idPointLock.unlock();
            return true;
        }
        else
        {
            idIdLock.lock();
            idPointMap.put(id, point);
            pointIdMap.put(point, id);
            idIdLock.unlock();
            idPointLock.unlock();
            return false;
        }
    }

    public static Point getPoint(final long orig, final Point localPoint)
    {
        final Point point;
        idPointLock.lock();
        point = idPointMap.get(orig);
        idPointLock.unlock();

        return point == null ? localPoint : point;
    }

    private final double[] w, l;
    private final long id;
    private final boolean fromOrigin;

    /**
     * Creates a Bottle containing a Point
     * @param point the Point to bottle
     * @param isOrigin true if we're bottling from the root-node perspective, false if from the
     *                 client-node perspective.
     */
    public PointBottle(final Point point, final boolean isOrigin)
    {
        // This constructor should only be called from PointBottler.bottle, which is sync'ed.

        // Assume identityHashCode does not change for a given Object
        final int idHash = System.identityHashCode(point);

        w = point.getW();
        l = point.getL();
        fromOrigin = isOrigin;

        if (fromOrigin)
        {
            /*
            Sending from root node to client node
            */

            //Check to see if we've sent this point before.
            if (pointIdMap.containsKey(point))
            {
                // If we have, just re-use the existing id.
                id = getId(point, idHash);
            }
            else
            {
                // If we haven't, generate a new id...
                id = idGenerator.getAndIncrement();
                // Map that id to the idHash
                mapIdToPoint(point, id);
                // Map the point to the id.
                mapPointToID(id, point);
            }
        }
        else
        {
            // Sending from client to root.
            if (point instanceof IdPoint)
            {
                id = ((IdPoint)point).getID();
            }
            else
            {
                id = -1;
            }
        }
    }

    @Override
    public Point unBottle(final MessageXC xc)
    {


        if (fromOrigin)
        {
            final Point point = new IdPoint(id, new Point(l, w));
            // Operating on a client node
            if (existsOrPut(id, point))
            {
                /*
                If we've already seen this point, return the new point that was generated last time.
                 */
                return getPoint(id, point);
            }
            else
            {
                return point;
            }
        }
        else
        {
            if (id >= 0)
            {
                final Point point = new Point(l, w);
                final Point origPoint = getPoint(id, point);
                syncPoint(origPoint, point);
                return origPoint;
            }
            else
            {
                return new Point(l, w);
            }
        }
    }

    private static synchronized void syncPoint(final Point to, final Point from)
    {
        if (from != null && to != from)
        {
            final double[] wTo = to.getW(), wFrom = from.getW(),
                    lTo = to.getL(), lFrom = from.getL();

            for (int j = 0; j < wTo.length; ++j)
            {
                wTo[j] = wFrom[j];
            }
            for (int j = 0; j < lTo.length; ++j)
            {
                lTo[j] = lFrom[j];
            }
        }
    }
}
