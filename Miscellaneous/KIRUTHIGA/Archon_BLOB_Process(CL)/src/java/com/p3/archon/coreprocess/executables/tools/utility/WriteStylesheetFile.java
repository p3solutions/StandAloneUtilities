/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package com.p3.archon.coreprocess.executables.tools.utility;


import static java.nio.file.Files.copy;

import java.nio.file.Paths;

public final class WriteStylesheetFile
{

  public static void main(final String[] args)
    throws Exception
  {
    try
    {
      copy(WriteStylesheetFile.class.getResourceAsStream("/sc.css"),
           Paths.get(args[0]));
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
