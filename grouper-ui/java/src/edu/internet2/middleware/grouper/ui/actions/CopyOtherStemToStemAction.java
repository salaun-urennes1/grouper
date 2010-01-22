package edu.internet2.middleware.grouper.ui.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import edu.internet2.middleware.grouper.GrouperHelper;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.ui.Message;

/**
 * @author shilen
 * @version $Id: CopyOtherStemToStemAction.java,v 1.1 2009-05-08 12:03:37 shilen Exp $
 */
public class CopyOtherStemToStemAction extends GrouperCapableAction {

  static final private String FORWARD_CopyStem = "CopyOtherStemToStem";

  public ActionForward grouperExecute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response,
      HttpSession session, GrouperSession grouperSession)
      throws Exception {
    
    DynaActionForm stemForm = (DynaActionForm) form;
    
    String curNode = (String)stemForm.get("stemId");
    Stem destinationStem = StemFinder.findByUuid(grouperSession, curNode, true);
    
    // get the options selected by the user
    String[] selections = request.getParameterValues("selections");
    
    // find the stem to copy
    String stemSelection = request.getParameter("stemSelection");
    if (stemSelection.equals("other")) {
      stemSelection = request.getParameter("otherStemSelection");
    }
    
    if (stemSelection == null || stemSelection.equals("")) {
      request.setAttribute("message", new Message(
          "stems.message.error.invalid-stem", true));
      return mapping.findForward(FORWARD_CopyStem);
    }
    
    Stem stem = StemFinder.findByName(grouperSession, stemSelection, false);
    
    if (stem == null) {
      request.setAttribute("message", new Message(
          "stems.message.error.invalid-stem", true));
      return mapping.findForward(FORWARD_CopyStem);
    }

    Stem newStem = GrouperHelper.copyStem(stem, destinationStem, selections);
    
    request.setAttribute("message", new Message(
        "stems.message.stem-copied", newStem.getName()));
    
    return new ActionForward("/populate" + getBrowseMode(session) + "Groups.do");
  }

}
