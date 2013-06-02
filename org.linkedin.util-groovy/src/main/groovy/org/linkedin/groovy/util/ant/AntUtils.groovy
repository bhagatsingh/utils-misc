/*
 * Copyright 2010-2010 LinkedIn, Inc
 * Portions Copyright (c) 2011-2013 Yan Pujante
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.linkedin.groovy.util.ant

import org.apache.tools.ant.Project
import org.apache.tools.ant.BuildException
import org.linkedin.groovy.util.io.GroovyIOUtils

/**
 * Helper methods for ant
 *
 * @author ypujante@linkedin.com
 */
class AntUtils
{
  /**
   * Executes the closure with a builder and make sure to catch <code>BuildException</code>
   * to propertly unwrap them
   */
  static def withBuilder(Closure closure)
  {
    AntBuilder6068 builder = new AntBuilder6068()
    // removes info messages...
    builder.project.buildListeners[0].messageOutputLevel = Project.MSG_WARN
    try
    {
      return closure(builder)
    }
    catch(BuildException e)
    {
      if(e.cause)
        throw e.cause
      else
        throw e
    }
  }

  /**
   * Creates the directory and parents of the provided directory. Returns dir.
   * @deprecated use {@link GroovyIOUtils#mkdirs(java.io.File)} instead
   */
  @Deprecated
  static File mkdirs(File dir)
  {
    GroovyIOUtils.mkdirs(dir)
  }

  /**
   * Returns a temp file located in <code>System.getProperty('java.io.tmpdir')</code>
   */
  static File tempFile()
  {
    return tempFile([:])
  }

  /**
   * Creates a temp file:
   *
   * @param args.destdir where the file should be created (optional (will go in
   *                     <code>System.getProperty('java.io.tmpdir')</code>)
   * @param args.prefix a prefix for the file (optional)
   * @param args.suffix a suffix for the file (optional)
   * @param args.deleteonexit if the temp file should be deleted on exit (default to
   *                          <code>false</code>)
   * @param args.createParents if the parent directories should be created (default to
   * <code>true</code>)
   * @return a file (note that it is just a file object and that the actual file has *not* been
   *         created and the parents may have been depending on the args.createParents value)
   */
  static File tempFile(args)
  {
    args = args ?: [:]
    args = new HashMap(args)
    args.destdir = args.destdir ?: System.getProperty('java.io.tmpdir')
    args.prefix = args.prefix ?: ''
    args.deleteonexit = args.deleteonexit ?: false
    args.property = 'p'
    def tempFile = AntUtils.withBuilder { ant -> ant.tempfile(args).project.getProperty('p') }
    tempFile = new File(tempFile)
    if(args.createParents == null ? true : args.createParent)
    {
      GroovyIOUtils.mkdirs(tempFile.parentFile)
    }
    return tempFile
  }

}
