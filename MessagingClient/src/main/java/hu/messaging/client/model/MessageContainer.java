package hu.messaging.client.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
 "sender",
 "id",
 "subject",
 "sentAt",
 "processedAt",
 "status",
 "mimeType",
 "contentAvailable"
})
@XmlRootElement(name = "message")
public class MessageContainer {

 @XmlElement(required = true)
 protected MessageContainer.Sender sender;
 @XmlElement(required = true)
 protected String id;
 @XmlElement(required = true)
 protected String subject;
 @XmlElement(name = "sent_at")
 @XmlSchemaType(name = "dateTime")
 protected XMLGregorianCalendar sentAt;
 @XmlElement(name = "processed_at")
 @XmlSchemaType(name = "dateTime")
 protected XMLGregorianCalendar processedAt;
 @XmlElement(required = true)
 protected String status;
 @XmlElement(name = "mime_type", required = true)
 protected String mimeType;
 @XmlElement(name = "content_available")
 protected boolean contentAvailable;

 public MessageContainer.Sender getSender() {
     return sender;
 }

 public void setSender(MessageContainer.Sender value) {
     this.sender = value;
 }
 
 public String getId() {
     return id;
 }

 public void setId(String value) {
     this.id = value;
 }

 public String getSubject() {
     return subject;
 }

 public void setSubject(String value) {
     this.subject = value;
 }

 public XMLGregorianCalendar getSentAt() {
     return sentAt;
 }

 public void setSentAt(XMLGregorianCalendar value) {
     this.sentAt = value;
 }

 public XMLGregorianCalendar getProcessedAt() {
     return processedAt;
 }

 public void setProcessedAt(XMLGregorianCalendar value) {
     this.processedAt = value;
 }

 public String getStatus() {
     return status;
 }

 public void setStatus(String value) {
     this.status = value;
 }

 public String getMimeType() {
     return mimeType;
 }

 public void setMimeType(String value) {
     this.mimeType = value;
 }
 
 public boolean isContentAvailable() {
     return contentAvailable;
 }

 public void setContentAvailable(boolean value) {
     this.contentAvailable = value;
 }

 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "", propOrder = {
     "name",
     "sipUri"
 })
 public static class Sender {

     @XmlElement(required = true)
     protected String name;
     @XmlElement(name = "sip_uri", required = true)
     protected String sipUri;

     public String getName() {
         return name;
     }

     public void setName(String value) {
         this.name = value;
     }

     public String getSipUri() {
         return sipUri;
     }

     public void setSipUri(String value) {
         this.sipUri = value;
     }

 }

}
