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

import edu.utexas.clm.archipelago.ijsupport.TrakEM2Archipelago;
import edu.utexas.clm.archipelago.network.MessageXC;
import edu.utexas.clm.archipelago.network.translation.Bottle;
import ini.trakem2.Project;
import ini.trakem2.display.Patch;

import java.io.File;

/**
 *
 */
public class PatchBottle implements Bottle<Patch>
{
    private final File projectFile;
    private final long id;

    public PatchBottle(final Patch patch)
    {
        projectFile = TrakEM2Archipelago.getFile(patch.getProject());
        id = patch.getId();
    }

    public Patch unBottle(final MessageXC xc)
    {
        final Project p = TrakEM2Archipelago.getProject(projectFile);
        return (Patch)p.findById(id);
    }
}
