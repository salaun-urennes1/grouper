<%@ include file="../assetsJsp/commonTaglib.jsp"%>

                    <!-- start group/groupMoreActionsButtonContents.jsp -->

                    <c:if test="${grouperRequestContainer.groupContainer.canUpdate && grouperRequestContainer.groupContainer.showAddMember}">

                      <a id="show-add-block" href="javascript:void(0);" onclick="showHideMemberAddBlock()" 
                      	class="btn btn-medium btn-primary btn-block" role="button">
                      		<i class="fa fa-plus"></i> ${textContainer.text['groupViewMoreActionsAddMembers'] }
                      </a>

                    </c:if>
                    <div class="btn-group btn-block">
                    
                      <a data-toggle="dropdown" href="#" aria-label="${textContainer.text['ariaLabelGuiMoreGroupActions']}" id="more-action-button" class="btn btn-medium btn-block dropdown-toggle" 
                      	aria-haspopup="true" aria-expanded="false" role="menu" onclick="$('#group-more-options').is(':visible') === true ? $(this).attr('aria-expanded','false') : $(this).attr('aria-expanded',function(index, currentValue) { $('#group-more-options li').first().focus();return true;});">
                      		${textContainer.text['groupViewMoreActionsButton'] } <span class="caret"></span></a>
  
                      <ul class="dropdown-menu dropdown-menu-right" id="group-more-options">
                        <%-- add or remove to/from my favorites, this causes a success message --%>
                        <c:choose>
                          <c:when test="${grouperRequestContainer.groupContainer.favorite}">
                            <li><a href="#" 
                            onclick="ajax('../app/UiV2Group.removeFromMyFavorites?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;" 
                            >${textContainer.text['groupViewMoreActionsRemoveFromMyFavorites'] }</a></li>
                          </c:when>
                          <c:otherwise>
                            <li><a href="#" 
                            onclick="ajax('../app/UiV2Group.addToMyFavorites?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;" 
                            >${textContainer.text['groupViewMoreActionsAddToMyFavorites']}</a></li>
                          </c:otherwise>
                        </c:choose>

                        <c:if test="${grouperRequestContainer.groupContainer.directMember && grouperRequestContainer.groupContainer.canOptout }">
                          <li><a href="#" onclick="ajax('../app/UiV2Group.leaveGroup?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;" >${textContainer.text['groupViewLeaveGroupButton']}</a></li>
                        </c:if>
                        <c:if test="${!grouperRequestContainer.groupContainer.directMember && grouperRequestContainer.groupContainer.canJoin }">
                          <li><a href="#" onclick="ajax('../app/UiV2Group.joinGroup?groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;">${textContainer.text['groupViewJoinGroupButton']}</a></li>
                        </c:if>

                        <c:if test="${grouperRequestContainer.groupContainer.canUpdate }">
                          <li class="divider"></li>
                        </c:if>
                        <c:if test="${grouperRequestContainer.groupContainer.canAdmin }">
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.groupCopy&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['groupViewCopyGroupButton'] }</a></li>
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.groupDelete&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                            >${textContainer.text['groupViewDeleteGroupButton'] }</a></li>
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.groupEdit&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                            >${textContainer.text['groupViewEditGroupButton'] }</a></li>
                            
                          <c:if test="${grouperRequestContainer.groupContainer.guiGroup.typeRole}">
                            <li><a href="#" onclick="return guiV2link('operation=UiV2Role.roleEditInheritance&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                            >${textContainer.text['roleViewEditInheritanceButton'] }</a></li>                      
                          </c:if>  
                        </c:if>
                        <c:if test="${grouperRequestContainer.groupContainer.canUpdate && grouperRequestContainer.groupContainer.canRead }">
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.groupEditComposite&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                            >${textContainer.text['groupViewEditGroupCompositeButton'] }</a></li>
                        </c:if>
                        <c:if test="${grouperRequestContainer.groupContainer.canAdmin }">
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.groupMove&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['groupViewMoveGroupButton'] }</a></li>
                        </c:if>
                        
                        <li class="divider"></li>
                        <c:if test="${grouperRequestContainer.groupContainer.canRead}">
                          <li><a href="#" onclick="return guiV2link('operation=UiV2GroupImport.groupExport&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;">${textContainer.text['groupExportMenuButton'] }</a></li>
                        </c:if>
                        <c:if test="${!grouperRequestContainer.groupContainer.guiGroup.hasComposite && grouperRequestContainer.groupContainer.canUpdate}">
                          <li><a href="#" onclick="return guiV2link('operation=UiV2GroupImport.groupImport&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}&backTo=group'); return false;">${textContainer.text['groupImportMenuButton'] }</a></li>
                          <c:if test="${grouperRequestContainer.groupContainer.guiGroup.canInviteExternalUsers}">
                            <li><a href="#" onclick="return guiV2link('operation=UiV2ExternalEntities.inviteExternal&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['inviteExternalMenuLink']}</a></li>
                          </c:if>
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.groupRemoveAllMembers&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['groupViewRemoveAllMembersButton'] }</a></li>
                        </c:if>
                        <li class="divider"></li>
                        <c:if test="${grouperRequestContainer.groupContainer.canRead}">
                          <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2Attestation.groupAttestation&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['attestationButton'] }</a></li>
                        </c:if>                
                        <c:if test="${grouperRequestContainer.deprovisioningContainer.canReadDeprovisioning}">
                          <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2Deprovisioning.deprovisioningOnGroupReport&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                            >${textContainer.text['deprovisioningMoreActionsMenuLabel'] }</a></li>
                        </c:if> 
                        
                        <c:if test="${grouperRequestContainer.provisioningContainer.canReadProvisioning}">
                            <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2Provisioning.viewProvisioningOnGroup&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['provisioningMoreActionsMenuLabel'] }</a></li>
                          </c:if>
                                
                        <%-- --%>
                        <c:if test="${grouperRequestContainer.groupContainer.canAdmin}">
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.viewAudits&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}&auditType=group'); return false;"
                              >${textContainer.text['groupViewAuditButton'] }</a></li>
                              
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.viewAudits&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}&auditType=membership'); return false;"
                              >${textContainer.text['groupViewMembershipAuditButton'] }</a></li>
                          
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.viewAudits&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}&auditType=actions'); return false;"
                              >${textContainer.text['groupViewActionAuditButton'] }</a></li>
                          
                          <li><a href="#" onclick="return guiV2link('operation=UiV2Group.viewAudits&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}&auditType=privileges'); return false;"
                              >${textContainer.text['groupViewPrivilegeAuditButton'] }</a></li>
                          
                        </c:if>
                                                
                        <c:if test="${grouperRequestContainer.groupContainer.canReadAttributes}">
	                        <li class="divider"></li>
	                        <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2GroupPermission.groupPermission&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['groupViewPermissionsButton'] }</a></li>
	                        <li class="divider"></li>
                          <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2GroupAttributeAssignment.viewAttributeAssignments&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['groupAttributeAssignmentsButton'] }</a></li>
                          <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2GroupAttributeAssignment.viewMembershipAttributeAssignments&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['groupAttributeMembershipAssignmentsButton'] }</a></li>
                        </c:if>
                        
                        <c:if test="${grouperRequestContainer.groupContainer.canView && grouperRequestContainer.workflowContainer.canViewElectronicForm}">
                          <li class="divider"></li>
                          <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2GrouperWorkflow.viewForms&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                              >${textContainer.text['groupWorkflowElectronicForms'] }</a></li>
                        </c:if>
                        
                        <c:if test="${grouperRequestContainer.objectTypeContainer.canReadObjectType}">
                          <li class="divider"></li>
                          <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2GrouperObjectTypes.viewObjectTypesOnGroup&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                            >${textContainer.text['objectTypeMoreActionsMenuLabel'] }</a></li>
                          
                        </c:if>

                        <li class="divider"></li>
                        <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2Visualization.groupView&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
                          >${textContainer.text['visualization.title'] }</a></li>
                          
                        <c:if test="${grouperRequestContainer.grouperReportContainer.reportingEnabled}">
	                        <li class="divider"></li>
	                        <li><a href="javascript:void(0)" onclick="return guiV2link('operation=UiV2GrouperReport.viewReportConfigsOnGroup&groupId=${grouperRequestContainer.groupContainer.guiGroup.group.id}'); return false;"
	                          >${textContainer.text['groupViewReportButton'] }</a></li>
                        </c:if>

                      </ul>
                    </div>

                    <!-- end group/groupMoreActionsButtonContents.jsp -->
