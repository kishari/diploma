package hu.messaging.client.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
* <p>Java class for anonymous complex type.
* 
* <p>The following schema fragment specifies the expected content contained within this class.
* 
* <pre>
* &lt;complexType>
*   &lt;complexContent>
*     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*       &lt;sequence>
*         &lt;element name="sender" type="{}userInfo"/>
*         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
*         &lt;element name="subject" type="{http://www.w3.org/2001/XMLSchema}string"/>
*         &lt;element name="sent_at" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
*         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
*         &lt;element name="contentDescription" type="{}contentDescription"/>
*       &lt;/sequence>
*     &lt;/restriction>
*   &lt;/complexContent>
* &lt;/complexType>
* </pre>
* 
* 
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
 "sender",
 "id",
 "subject",
 "sentAt",
 "status",
 "contentDescription"
})
@XmlRootElement(name = "messageInfoContainer")
public class MessageInfoContainer {

 @XmlElement(required = true)
 protected UserInfo sender;
 @XmlElement(required = true)
 protected String id;
 @XmlElement(required = true)
 protected String subject;
 @XmlElement(name = "sent_at", required = true)
 @XmlSchemaType(name = "dateTime")
 protected XMLGregorianCalendar sentAt;
 @XmlElement(required = true)
 protected String status;
 @XmlElement(required = true)
 protected ContentDescription contentDescription;

 /**
  * Gets the value of the sender property.
  * 
  * @return
  *     possible object is
  *     {@link UserInfo }
  *     
  */
 public UserInfo getSender() {
     return sender;
 }

 /**
  * Sets the value of the sender property.
  * 
  * @param value
  *     allowed object is
  *     {@link UserInfo }
  *     
  */
 public void setSender(UserInfo value) {
     this.sender = value;
 }

 /**
  * Gets the value of the id property.
  * 
  * @return
  *     possible object is
  *     {@link String }
  *     
  */
 public String getId() {
     return id;
 }

 /**
  * Sets the value of the id property.
  * 
  * @param value
  *     allowed object is
  *     {@link String }
  *     
  */
 public void setId(String value) {
     this.id = value;
 }

 /**
  * Gets the value of the subject property.
  * 
  * @return
  *     possible object is
  *     {@link String }
  *     
  */
 public String getSubject() {
     return subject;
 }

 /**
  * Sets the value of the subject property.
  * 
  * @param value
  *     allowed object is
  *     {@link String }
  *     
  */
 public void setSubject(String value) {
     this.subject = value;
 }

 /**
  * Gets the value of the sentAt property.
  * 
  * @return
  *     possible object is
  *     {@link XMLGregorianCalendar }
  *     
  */
 public XMLGregorianCalendar getSentAt() {
     return sentAt;
 }

 /**
  * Sets the value of the sentAt property.
  * 
  * @param value
  *     allowed object is
  *     {@link XMLGregorianCalendar }
  *     
  */
 public void setSentAt(XMLGregorianCalendar value) {
     this.sentAt = value;
 }

 /**
  * Gets the value of the status property.
  * 
  * @return
  *     possible object is
  *     {@link String }
  *     
  */
 public String getStatus() {
     return status;
 }

 /**
  * Sets the value of the status property.
  * 
  * @param value
  *     allowed object is
  *     {@link String }
  *     
  */
 public void setStatus(String value) {
     this.status = value;
 }

 /**
  * Gets the value of the contentDescription property.
  * 
  * @return
  *     possible object is
  *     {@link ContentDescription }
  *     
  */
 public ContentDescription getContentDescription() {
     return contentDescription;
 }

 /**
  * Sets the value of the contentDescription property.
  * 
  * @param value
  *     allowed object is
  *     {@link ContentDescription }
  *     
  */
 public void setContentDescription(ContentDescription value) {
     this.contentDescription = value;
 }

}
