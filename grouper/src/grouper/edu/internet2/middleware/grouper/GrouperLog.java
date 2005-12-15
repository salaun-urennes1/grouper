/*
  Copyright 2004-2005 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2005 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;


import  edu.internet2.middleware.subject.*;
import  edu.internet2.middleware.subject.provider.*;
import  java.io.Serializable;
import  java.util.*;
import  org.apache.commons.lang.time.*;
import  org.apache.commons.logging.*;


/** 
 * Grouper API logging.
 * <p />
 * @author  blair christensen.
 * @version $Id: GrouperLog.java,v 1.9 2005-12-15 06:31:11 blair Exp $
 *     
*/
class GrouperLog implements Serializable {

  // Protected Class Constants
  // TODO Move to _ErrorLog_
  protected static final String ERR_CMGR  = "unable to get cache manager: ";
  protected static final String ERR_GRS   = "unable to start root session: ";
  protected static final String MSG_EC    = "emptied cache ";


  // Protected Class Methods

  protected static void debug(Log log, GrouperSession s, String msg) {
    log.debug( _formatMsg(s, msg) );
  } // protected static void debug(log, s, msg)

  protected static void error(Log log, GrouperSession s, String msg) {
    log.error( _formatMsg(s, msg) );
  } // protected static void error(log, s, msg)

  protected static void fatal(Log log, GrouperSession s, String msg) {
    log.fatal( _formatMsg(s, msg) );
  } // protected static void fatal(log, s, msg)

  protected static void info(Log log, GrouperSession s, String msg) {
    log.info( _formatMsg(s, msg) );
  } // protected static void info(log, s, msg)

  // For effective membership event logging
  protected static void info(
    Log log, String sessionToString, String msg
  ) 
  {
    log.info("[" + sessionToString + "] " + msg);
  } // protected static void info(log, sessionToString, msg, sw)

  // For all other event logging
  protected static void info(
    Log log, String sessionToString, String msg, StopWatch sw
  ) 
  {
    log.info("[" + sessionToString + "] " + msg + " (" + sw.getTime() + "ms)");
  } // protected static void info(log, sessionToString, msg, sw)

  protected static void warn(Log log, GrouperSession s, String msg) {
    log.warn( _formatMsg(s, msg) );
  } // protected static void warn(log, s, msg)


  // Private Class Methods
  private static String _formatMsg(GrouperSession s, String msg) {
    return "[" + s.toString() + "] " + msg;
  } // private static String _formatMsg(s, msg)

} // protected class GrouperLog

