/*
 * Copyright (C) 2006-2007 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package edu.internet2.middleware.grouper.app.gsh;

/**
 * Utility Methods.
 * <p />
 * @author  blair christensen.
 * @version $Id: GshUtil.java,v 1.1 2008-07-21 21:01:59 mchyzer Exp $
 * @since   0.0.1
 */
class GshUtil {

  // PRIVATE CLASS CONSTANTS //
  private static final String Q_CLOSE = "' ";
  private static final String Q_OPEN  = "'";


  // PROTECTED CLASS METHODS //

  // @since 0.0.1
  protected static String q(String txt) {
    return Q_OPEN + txt + Q_CLOSE;
  } // protected static String q(txt)

} // class U

