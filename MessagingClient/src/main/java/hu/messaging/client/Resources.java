package hu.messaging.client;

import java.util.HashMap;
import java.util.Map;

public class Resources {

	public static final Map<String, String> resources = new HashMap<String, String>(){
		{
            put("application.title", "Messaging Client");
            put("application.start.error","Error starting the client");
            put("buddy.group.default", "Others");
            put("menu.messages", "Messages");
            put("menu.messages.box", "Message Box");
            put("menu.messages.pull", "Pull New Messages");
            put("menu.file", "File");                        
            put("menu.file.exit", "Exit");
            put("menu.edit", "Edit");
            put("menu.edit.buddy.add", "Add Buddy...");
            put("menu.edit.buddy.edit", "Rename Buddy...");
            put("menu.edit.buddy.move", "Move to Group...");
            put("menu.edit.buddy.remove", "Remove Buddy");
            put("menu.edit.group.add", "Add Group...");
            put("menu.edit.group.remove", "Remove Group");
            put("menu.edit.group.edit", "Rename Group...");
            put("menu.edit.sort.ascending", "Sort Ascending");
            put("menu.edit.sort.descending", "Sort Descending");            
            put("button.cancel", "Cancel");
            put("button.ok", "Ok");
            put("button.add", "Add");
            put("button.remove", "Remove");
            put("button.delete", "Delete");
            put("button.send", "Send");
            put("button.play", "Play");
            put("button.stop", "Stop");
            put("button.show", "Show");
            put("button.capture", "Capture");     
            put("dialog.group.edit.title", "Modify Group");
            put("dialog.group.name.label", "Group name:");
            put("icon.nouser", "No User");
            put("add.buddy.error","Error adding buddy");
            put("add.group.error","Error adding group ");
            put("remove.buddy.error","Error removing buddy");
            put("remove.group.error","Error removing group ");
            put("update.buddy.error","Error updating buddy");
            put("update.group.error","Error updating group ");
            put("capture.window.title", "Capture content");
            put("detail.message.sender.label", "Feladó: ");
            put("detail.message.subject.label", "Tárgy: ");
            put("detail.message.mimetype.label", "Üzenet típusa: ");
            put("menu.edit.sendmessage", "Send message...");
            put("dialog.group.add.title", "New Group");                       
            put("dialog.buddy.add.title", "New Buddy");
            put("dialog.buddy.group.label", "Add to Group:");
            put("dialog.buddy.name", "Buddy Name");
            put("dialog.buddy.name.label", "Buddy Name:");
            put("dialog.buddy.uri.label", "Buddy URI:");
            put("dialog.buddy.edit.title", "Modify Buddy");
            put("dialog.contact.move.title", "Move to group");
            put("dialog.contact.move.label", "Select new group:");
            put("messagepane.label.sent", "Elküldött üzenetek");
            put("messagepane.label.inbox", "Bejövõ üzenetek");
            put("message.remove.group.default.title","Cannot remove group");
            put("message.remove.group.default","Default group cannot be deleted");
            //---------------------------------
                                                

            put("dialog.message.sender", "Sender:");
            put("dialog.help.about.title", "About Messaging Client");
            put("dialog.instant.message.send.title", "Message to send");
            put("dialog.instant.message.send.message", "Type the message to send:");
            put("dialog.instant.message.receive.title", "Message received from {0}");
            put("dialog.communication.sendMessage", "Send message to groups");
            put("dialog.communication.incoming.title", "Incoming Communication");
            put("dialog.communication.incoming.label", "You have an incoming {0} from {1}. Do you want to accept it?");
            put("dialog.communication.remaining", "Remaining time to connect:");
            put("dialog.communication.elapsed", "Elapsed time:");
            put("dialog.communication.duration", "Communication duration:");
            put("chat.session.title", "Chat session with {0}");
            put("voice.call.title", "Voice over IP call with {0}");
            put("dialog.communication.menu.file", "File");
            put("dialog.communication.menu.file.save", "Save...");
            put("dialog.communication.menu.file.end", "End Communication");
            put("dialog.call.hold", "Hold Call");
            put("dialog.call", "phone call");
            put("dialog.chat", "chat session");
            put("dialog.file", "file");
            put("dialog.select", "Select");
            put("dialog.select.title", "Select a file");
            put("dialog.image.description", "GIF or Jpeg image");
            put("dialog.communication.menu.option", "Options");
            put("dialog.communication.menu.option.transfert.file", "Send an image...");
            put("dialog.yes", "Yes");
            put("dialog.no", "No");
            put("dialog.you", "You");
            put("unit.plurial", "s");
            put("users.in.call","Users In The Call");
            put("add.user.in.call","Add User In Call");
            put("add.user.uri","User URI:");
            put("add.user.invite","Invite");
            put("user.rejected.invite","User {0} rejected the invitation");
            put("call.notes","Notes");
            put("call.save.notes.title","Choose a file in which the notes will be saved");
            put("call.save.notes","Save Notes");
            put("chat.save","Choose a file in which the chat will be saved");
            put("status.online","Online");
            put("status.offline","Offline");
            put("status.blacklisted","");
            
            put("message.communication.failed","Unable to initiate the communication with the remote party");
            put("message.communication.refused","The remote party declined the invitation");
            put("message.communication.end","Communication ended by the remote party");
           
            put("save.error","Error saving file");
            put("dialog.chat.error","Error processing the chat session");
            put("dialog.call.error","Error processing the voice call");
            put("add.user.error","Error adding user");
            
            put("frame.messagebox.title", "Messages");
            put("frame.message.details.title", "Message Details");
        }
    };
    
    public static final String serverSipURI = "sip:weblogic@ericsson.com";
    
    public static final String workingDirectory = "C:\\diploma\\testing\\";
    public static final String messagesDirectory = workingDirectory + "messages\\";
    public static final String messageContentsDirectory = messagesDirectory + "contents\\";
    
}
