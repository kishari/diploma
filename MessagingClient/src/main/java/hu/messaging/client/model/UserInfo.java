package hu.messaging.client.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userInfo", propOrder = {
 "name",
 "sipUri"
})
public class UserInfo {

 @XmlElement(required = true)
 protected String name;
 @XmlElement(name = "sip_uri", required = true)
 protected String sipUri;

 /**
  * Gets the value of the name property.
  * 
  * @return
  *     possible object is
  *     {@link String }
  *     
  */
 public String getName() {
     return name;
 }

 /**
  * Sets the value of the name property.
  * 
  * @param value
  *     allowed object is
  *     {@link String }
  *     
  */
 public void setName(String value) {
     this.name = value;
 }

 /**
  * Gets the value of the sipUri property.
  * 
  * @return
  *     possible object is
  *     {@link String }
  *     
  */
 public String getSipUri() {
     return sipUri;
 }

 /**
  * Sets the value of the sipUri property.
  * 
  * @param value
  *     allowed object is
  *     {@link String }
  *     
  */
 public void setSipUri(String value) {
     this.sipUri = value;
 }

}

