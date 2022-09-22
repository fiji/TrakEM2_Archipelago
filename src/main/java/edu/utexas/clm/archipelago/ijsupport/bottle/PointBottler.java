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

import edu.utexas.clm.archipelago.network.MessageXC;
import edu.utexas.clm.archipelago.network.translation.Bottle;
import edu.utexas.clm.archipelago.network.translation.Bottler;

import java.io.IOException;
import java.io.ObjectInputStream;

import mpicbg.models.Point;

/**
 *
 */
public class PointBottler implements Bottler<Point>
{
    private boolean isOrigin = true;

    public boolean accepts(final Object o)
    {
        return o instanceof Point;
    }

    public synchronized Bottle<Point> bottle(final Object o, final MessageXC xc)
    {
        return new PointBottle((Point)o, isOrigin);
    }

    public boolean transfer()
    {
        return true;
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException
    {
        ois.defaultReadObject();

        isOrigin = false;
    }
}
