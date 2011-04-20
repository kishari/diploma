package hu.messaging.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfoDetail", propOrder = {
 "sender",
 "id",
 "mimeType",
 "subject",
 "sentAt",
 "recipientList"
})
public class InfoDetail {

 protected UserInfo sender;
 @XmlElement(required = true)
 protected String id;
 @XmlElement(name = "mime_type")
 protected String mimeType;
 protected String subject;
 @XmlElement(name = "sent_at")
 @XmlSchemaType(name = "dateTime")
 protected XMLGregorianCalendar sentAt;
 protected InfoDetail.RecipientList recipientList;

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
  * Gets the value of the mimeType property.
  * 
  * @return
  *     possible object is
  *     {@link String }
  *     
  */
 public String getMimeType() {
     return mimeType;
 }

 /**
  * Sets the value of the mimeType property.
  * 
  * @param value
  *     allowed object is
  *     {@link String }
  *     
  */
 public void setMimeType(String value) {
     this.mimeType = value;
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
  * Gets the value of the recipientList property.
  * 
  * @return
  *     possible object is
  *     {@link InfoDetail.RecipientList }
  *     
  */
 public InfoDetail.RecipientList getRecipientList() {
     return recipientList;
 }

 /**
  * Sets the value of the recipientList property.
  * 
  * @param value
  *     allowed object is
  *     {@link InfoDetail.RecipientList }
  *     
  */
 public void setRecipientList(InfoDetail.RecipientList value) {
     this.recipientList = value;
 }


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
  *         &lt;element name="recipient" type="{}userInfo" maxOccurs="unbounded"/>
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
     "recipient"
 })
 public static class RecipientList {

     @XmlElement(required = true)
     protected List<UserInfo> recipient;

     /**
      * Gets the value of the recipient property.
      * 
      * <p>
      * This accessor method returns a reference to the live list,
      * not a snapshot. Therefore any modification you make to the
      * returned list will be present inside the JAXB object.
      * This is why there is not a <CODE>set</CODE> method for the recipient property.
      * 
      * <p>
      * For example, to add a new item, do as follows:
      * <pre>
      *    getRecipient().add(newItem);
      * </pre>
      * 
      * 
      * <p>
      * Objects of the following type(s) are allowed in the list
      * {@link UserInfo }
      * 
      * 
      */
     public List<UserInfo> getRecipient() {
         if (recipient == null) {
             recipient = new ArrayList<UserInfo>();
         }
         return this.recipient;
     }

 }

}
