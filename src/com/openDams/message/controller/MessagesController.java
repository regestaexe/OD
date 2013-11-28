package com.openDams.message.controller;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.openDams.bean.Departments;
import com.openDams.bean.MessageTypes;
import com.openDams.bean.Messages;
import com.openDams.bean.Users;
import com.openDams.security.UserDetails;
import com.openDams.services.OpenDamsService;
import com.regesta.framework.util.DateUtility;

public class MessagesController implements Controller{
	private OpenDamsService service ;
	private  int page_size = 1;
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = null;
		if(arg0.getParameter("action")!=null){
			UserDetails user = (UserDetails) ((SecurityContext) SecurityContextHolder.getContext()).getAuthentication().getPrincipal();
			if(arg0.getParameter("action").equals("getMessageList")){
				mav = new ModelAndView("messages/messageList");					
				try {
					int limit = Integer.parseInt(arg0.getParameter("limit"));
					int start = Integer.parseInt(arg0.getParameter("start"));
					if(arg0.getParameter("messageType").equals("500")){
						List<Messages> messagesList = (List<Messages>)service.getPagedListFromSQL(Messages.class, "SELECT * FROM messages where ref_id_user_sender="+user.getId()+" and ref_id_user is null  order by date desc", start, limit);
						mav.addObject("totMessages",service.getCountFromSQL("SELECT count(id_message) FROM messages where ref_id_user_sender="+user.getId()+" and ref_id_user is null;").intValue());
						mav.addObject("messagesList", messagesList);
					}else if(arg0.getParameter("messageType").equals("501")){
						List<Messages> messagesList = (List<Messages>)service.getPagedListFromSQL(Messages.class, "SELECT * FROM messages where ref_id_user="+user.getId()+" and interest=1 order by date desc", start, limit);
						mav.addObject("totMessages",service.getCountFromSQL("SELECT count(id_message) FROM messages where ref_id_user="+user.getId()+" and interest=1").intValue());
						mav.addObject("messagesList", messagesList);
					}else if(arg0.getParameter("messageType").equals("1") || arg0.getParameter("messageType").equals("2")){
						List<Messages> messagesList = (List<Messages>)service.getPagedListFromSQL(Messages.class, "SELECT * FROM messages where ref_id_user="+user.getId()+" and (ref_id_message_type=1 or ref_id_message_type=2) and interest=0 order by date desc", start, limit);
						mav.addObject("totMessages",service.getCountFromSQL("SELECT count(id_message) FROM messages where ref_id_user="+user.getId()+" and (ref_id_message_type=1 or ref_id_message_type=2) and interest=0").intValue());
						mav.addObject("messagesList", messagesList);
					}else{
						List<Messages> messagesList = (List<Messages>)service.getPagedListFromSQL(Messages.class, "SELECT * FROM messages where ref_id_user="+user.getId()+" and ref_id_message_type="+arg0.getParameter("messageType")+" and interest=0 order by date desc", start, limit);
						mav.addObject("totMessages",service.getCountFromSQL("SELECT count(id_message) FROM messages where ref_id_user="+user.getId()+" and ref_id_message_type="+arg0.getParameter("messageType")+" and interest=0").intValue());
						mav.addObject("messagesList", messagesList);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				//service.getListFromSQL(Notes.class,"SELECT * FROM notes where ref_id_user="+arg0.getParameter("idUser")+" AND ref_id_archive="+arg0.getParameter("idArchive")+" AND ref_id_note_type="+NoteType.ARCHIVE_NOTE+" order by date desc;"));
			}else if(arg0.getParameter("action").equals("messageDetail")){
				mav = new ModelAndView("messages/messageDetail");				
				try {
					Messages messages = (Messages)service.getObject(Messages.class, new Integer(arg0.getParameter("idMessage")));
					messages.setReaded(true);
					service.update(messages);
					mav.addObject("messages", messages);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(arg0.getParameter("action").equals("checkMessageReceived")){
				mav = new ModelAndView("messages/checkMessageReceived");	
				//System.out.println("SELECT * FROM messages where ref_id_user="+user.getId()+" and readed=0 and interest=0 and (validity is null or validity>='"+DateUtility.getMySQLSystemDate()+" "+DateUtility.getMySQLTime()+"') order by date desc");
				List<Messages> messagesList = (List<Messages>)service.getPagedListFromSQL(Messages.class, "SELECT * FROM messages where ref_id_user="+user.getId()+" and readed=0 and interest=0 and (validity is null or validity>='"+DateUtility.getMySQLSystemDate()+" "+DateUtility.getMySQLTime()+"') order by date desc", 0, page_size);
				mav.addObject("messagesList", messagesList);
			}else if(arg0.getParameter("action").equals("leftMessagePanel")){
				mav = new ModelAndView("messages/leftMessagePanel");			
				mav.addObject("personalMessageCount", service.getCountFromSQL("SELECT count(id_message) FROM messages where ref_id_message_type="+MessageTypes.PERSONAL_MESSAGE+" and readed=0 and ref_id_user="+user.getId()+";"));
				mav.addObject("departmentMessageCount", service.getCountFromSQL("SELECT count(id_message) FROM messages where ref_id_message_type="+MessageTypes.DEPARTMENT_MESSAGE+" and readed=0 and ref_id_user="+user.getId()+";"));
				mav.addObject("systemMessageCount", service.getCountFromSQL("SELECT count(id_message) FROM messages where ref_id_message_type="+MessageTypes.SYSTEM_MESSAGE+" and readed=0 and ref_id_user="+user.getId()+";"));
			}else if(arg0.getParameter("action").equals("getUsersList")){
				mav = new ModelAndView("messages/usersList");	
				try {
					List<Users> usersList = null;
					Users users = (Users)service.getObject(Users.class, user.getId());
					if(arg0.getParameter("type").equalsIgnoreCase("my_dep")){
						if(users.getDepartments()!=null){							
							usersList = (List<Users>)service.getListFromSQL(Users.class, "SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where users.id_user!="+user.getId()+" and users.ref_id_department="+users.getDepartments().getIdDepartment()+" order by users_profile.lastname,users_profile.name;");
						}else{
							//usersList = (List<Users>)service.getListFromSQL(Users.class, "SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user where users.id_user!="+user.getId()+" order by users_profile.lastname,users_profile.name;");
							usersList = (List<Users>)service.getListFromSQL(Users.class, "SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user inner join departments on users.ref_id_department=departments.id_department where users.id_user!="+user.getId()+" order by departments.description,users_profile.lastname,users_profile.name;");
						}						
					}else if(arg0.getParameter("type").equalsIgnoreCase("other_dep")){
						if(users.getDepartments()!=null){							
							usersList = (List<Users>)service.getListFromSQL(Users.class, "SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user inner join departments on users.ref_id_department=departments.id_department where users.id_user!="+user.getId()+" and users.ref_id_department!="+users.getDepartments().getIdDepartment()+" order by departments.description,users_profile.lastname,users_profile.name;");
						}else{
							usersList = (List<Users>)service.getListFromSQL(Users.class, "SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user inner join departments on users.ref_id_department=departments.id_department where users.id_user!="+user.getId()+" order by departments.description,users_profile.lastname,users_profile.name;");
						}
					}else if(arg0.getParameter("type").equalsIgnoreCase("all_dep")){
							usersList = (List<Users>)service.getListFromSQL(Users.class, "SELECT users.* FROM users inner join users_profile on users.id_user=users_profile.ref_id_user inner join departments on users.ref_id_department=departments.id_department order by departments.description,users_profile.lastname,users_profile.name;");
					}					
					mav.addObject("usersList", usersList);
					mav.addObject("users", users);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(arg0.getParameter("action").equals("sendMessage")){
				mav = new ModelAndView("messages/sendMessage");
				Users users = (Users)service.getObject(Users.class, user.getId());
				String[] sentoTo = arg0.getParameter("sendTo").split(",");
				String object = arg0.getParameter("message_object");
				String messageText = arg0.getParameter("message_text");
				String messageMode = arg0.getParameter("messageMode");
				String startDate = arg0.getParameter("startDate");
				String endDate = arg0.getParameter("endDate");
				boolean modal = false;
				boolean system = false;
				Date expireDate = null;
				if(messageMode.equalsIgnoreCase("modal")){
					modal=true;
				}else if(messageMode.equalsIgnoreCase("system")){
					modal=true;
					system=true;
					//System.out.println("endDate "+endDate);
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:m:s");
					expireDate = formatter.parse(endDate);
					//System.out.println("expireDate "+expireDate);
				}
				for (int i = 0; i < sentoTo.length; i++) {
					String send = sentoTo[i];					
					if(send.equalsIgnoreCase("all_users")){
						List<Users> usersList = (List<Users>)service.getListFromSQL(Users.class, "SELECT users.* FROM users where users.id_user!="+user.getId()+";");
						for (int x = 0; x < usersList.size(); x++) {
							Messages messages = new Messages();
							messages.setValidity(expireDate);
							messages.setModal(modal);
							if(system){
								messages.setMessageTypes((MessageTypes)service.getObject(MessageTypes.class, MessageTypes.SYSTEM_MESSAGE));
							}else{
								messages.setMessageTypes((MessageTypes)service.getObject(MessageTypes.class, MessageTypes.PERSONAL_MESSAGE));
							}
							messages.setMessageText(messageText);
							messages.setObject(object);
							messages.setUsersByRefIdUser(usersList.get(x));
							messages.setUsersByRefIdUserSender(users);
							messages.setDate(new Date());
							messages.setInterest(false);
							Messages sent = copyMessage(messages);
							sent.setUsersByRefIdUser(null);
							service.add(messages);
							service.add(sent);
						}											
					}else if(send.startsWith("dep_")){
						int idDepartment = Integer.parseInt(StringUtils.substringAfter(send, "dep_"));
						List<Users> usersList = (List<Users>)service.getListFromSQL(Users.class, "SELECT * FROM users where ref_id_department="+idDepartment+" and users.id_user!="+user.getId()+";");
						for (int x = 0; x < usersList.size(); x++) {
							Messages messages = new Messages();
							messages.setValidity(expireDate);
							messages.setModal(modal);
							if(system){
								messages.setMessageTypes((MessageTypes)service.getObject(MessageTypes.class, MessageTypes.SYSTEM_MESSAGE));
							}else{
								messages.setMessageTypes((MessageTypes)service.getObject(MessageTypes.class, MessageTypes.DEPARTMENT_MESSAGE));
								messages.setDepartments((Departments)service.getObject(Departments.class, idDepartment));
							}
							messages.setMessageText(messageText);
							messages.setObject(object);
							messages.setUsersByRefIdUser(usersList.get(x));
							messages.setUsersByRefIdUserSender(users);
							messages.setDate(new Date());
							messages.setInterest(false);
							Messages sent = copyMessage(messages);
							sent.setUsersByRefIdUser(null);
							service.add(messages);
							service.add(sent);
						}						
					}else if(send.startsWith("user_")){
						int idUserToSend = Integer.parseInt(StringUtils.substringAfter(send, "user_"));
						Messages messages = new Messages();
						messages.setValidity(expireDate);
						messages.setModal(modal);
						if(system){
							messages.setMessageTypes((MessageTypes)service.getObject(MessageTypes.class, MessageTypes.SYSTEM_MESSAGE));
						}else{
							messages.setMessageTypes((MessageTypes)service.getObject(MessageTypes.class, MessageTypes.PERSONAL_MESSAGE));
						}
						messages.setMessageText(messageText);
						messages.setObject(object);
						messages.setUsersByRefIdUser((Users)service.getObject(Users.class, idUserToSend));
						messages.setUsersByRefIdUserSender(users);
						messages.setDate(new Date());
						messages.setInterest(false);
						Messages sent = copyMessage(messages);
						sent.setUsersByRefIdUser(null);
						service.add(messages);
						service.add(sent);
					}
				}
			}else if(arg0.getParameter("action").equals("updateMessages")){
				String[] selectedMessages = arg0.getParameter("selectedMessages").split(";");
				for (int x = 0; x < selectedMessages.length; x++) {
					Messages messages = (Messages)service.getObject(Messages.class, Integer.parseInt(selectedMessages[x]));
					if(arg0.getParameter("actionType").equals("readed")){
						messages.setReaded(true);
						service.update(messages);
					}else if(arg0.getParameter("actionType").equals("unreaded")){
						messages.setReaded(false);
						service.update(messages);
					}else if(arg0.getParameter("actionType").equals("interest")){
						messages.setInterest(true);
						service.update(messages);
					}else if(arg0.getParameter("actionType").equals("delete")){
						messages.setUsersByRefIdUser(null);
						service.remove(messages);
					}
					
				}   
			}
		}
		return mav;
	}
	private Messages copyMessage(Messages original){
		Messages copy = new Messages();
		copy.setDate(original.getDate());
		copy.setDepartments(original.getDepartments());
		copy.setInterest(original.isInterest());
		copy.setMessageText(original.getMessageText());
		copy.setMessageTypes(original.getMessageTypes());
		copy.setModal(original.isModal());
		copy.setObject(original.getObject());
		copy.setReaded(original.isReaded());
		copy.setUsersByRefIdUser(original.getUsersByRefIdUser());
		copy.setUsersByRefIdUserSender(original.getUsersByRefIdUserSender());
		copy.setUsersByRefIdUserTo(original.getUsersByRefIdUser());
		copy.setValidity(original.getValidity());
		return copy;
	}
	public void setService(OpenDamsService service) {
		this.service = service;
	}
	public void setPage_size(int pageSize) {
		page_size = pageSize;
	}
}
