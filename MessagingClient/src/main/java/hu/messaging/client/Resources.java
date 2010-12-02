package hu.messaging.client;

import java.util.HashMap;
import java.util.Map;

public class Resources {

	public static final Map<String, String> resources = new HashMap<String, String>(){
		{
            put("application.title", "Messaging Client");
            put("application.name", "IMS Multicast Messaging Client");
            put("application.version", "Version 1.0");
            put("buddy.group.default", "Others");
            put("status.disconnected", "Disconnected");
            put("button.cancel", "Cancel");
            put("button.ok", "Ok");
            put("button.add", "Add");
            put("button.remove", "Remove");
            put("button.send", "Send");
            put("menu.file", "File");
            put("menu.file.status", "Change status...");
            put("menu.file.transfer", "Transfer...");
            put("menu.file.exit", "Exit");
            put("menu.edit", "Edit");
            put("menu.edit.buddy.add", "Add Buddy...");
            put("menu.edit.buddy.edit", "Rename Buddy...");
            put("menu.edit.buddy.move", "Move to Group...");
            put("menu.edit.buddy.remove", "Remove Buddy");
            put("menu.edit.sendmessage", "Send message...");
            put("menu.edit.group.add", "Add Group...");
            put("menu.edit.group.remove", "Remove Group");
            put("menu.edit.group.edit", "Rename Group...");
            put("menu.edit.sort.ascending", "Sort Ascending");
            put("menu.edit.sort.descending", "Sort Descending");
            put("menu.help", "Help");
            put("menu.help.about", "About...");
            put("menu.session", "Session");
            put("menu.session.start.chat", "Start chat session...");
            put("menu.session.start.voice", "Start voice session...");
            put("menu.view", "View");
            put("menu.view.blacklist", "Black list...");
            put("dialog.group.add.title", "New Group");
            put("dialog.group.name.label", "Group name:");
            put("dialog.group.edit.title", "Modify Group");
            put("dialog.buddy.add.title", "New Buddy");
            put("dialog.buddy.group.label", "Add to Group:");
            put("dialog.buddy.name", "Buddy Name");
            put("dialog.buddy.name.label", "Buddy Name:");
            put("dialog.buddy.uri.label", "Buddy URI:");
            put("dialog.buddy.edit.title", "Modify Buddy");
            put("dialog.contact.move.title", "Move to group");
            put("dialog.contact.move.label", "Select new group:");
            put("dialog.blacklist.title", "Black List");
            put("dialog.blacklist.label", "Select the buddies to put on the black list");
            put("dialog.blacklist.buddy", "Buddy:");
            put("dialog.help.about.title", "About Windows Client");
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
            put("unit.day", "Day{0}");
            put("unit.hour", "Hour{0}");
            put("unit.minute", "Minute{0}");
            put("unit.second", "Second{0}");
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
            put("status.blacklisted","Black Listed");
            put("message.remove.group.default.title","Cannot remove group");
            put("message.remove.group.default","Default group cannot be deleted");
            put("message.communication.failed","Unable to initiate the communication with the remote party");
            put("message.communication.refused","The remote party declined the invitation");
            put("message.communication.end","Communication ended by the remote party");
            put("application.start.error","Error starting the client");
            put("save.error","Error saving file");
            put("dialog.chat.error","Error processing the chat session");
            put("dialog.call.error","Error processing the voice call");
            put("send.file.error"," Error sending file");
            put("add.user.error","Error adding user");
            put("add.buddy.error","Error adding buddy");
            put("add.group.error","Error adding group ");
            put("remove.buddy.error","Error removing buddy");
            put("remove.group.error","Error removing group ");
            put("update.buddy.error","Error updating buddy");
            put("update.group.error","Error updating group ");
            put("call.hold.error","Error holding call");
            put("cannot.establish.call","Error establishing call");
            put("cannot.establish.chat","Error establishing chat session");
            put("save.file.error","Error saving file");
            put("sending.instant.message.failed.title","Error sending instant message");
            put("sending.instant.message.failed.message","Failed to reach the other party");

        }
    };	
    
}
