package hu.messaging.client.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
* <p>Java class for contentDescription complex type.
* 
* <p>The following schema fragment specifies the expected content contained within this class.
* 
* <pre>
* &lt;complexType name="contentDescription">
*   &lt;complexContent>
*     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
*       &lt;sequence>
*         &lt;element name="mimeType">
*           &lt;simpleType>
*             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
*               &lt;enumeration value="image/jpeg"/>
*               &lt;enumeration value="image/bmp"/>
*               &lt;enumeration value="image/gif"/>
*               &lt;enumeration value="image/png"/>
*               &lt;enumeration value="audio/mpeg"/>
*               &lt;enumeration value="audio/x-wav"/>
*               &lt;enumeration value="video/x-msvideo"/>
*               &lt;enumeration value="video/mpeg"/>
*             &lt;/restriction>
*           &lt;/simpleType>
*         &lt;/element>
*         &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
*         &lt;element name="content_available" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
*       &lt;/sequence>
*     &lt;/restriction>
*   &lt;/complexContent>
* &lt;/complexType>
* </pre>
* 
* 
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contentDescription", propOrder = {
 "mimeType",
 "size",
 "contentAvailable"
})
public class ContentDescription {

 @XmlElement(required = true)
 protected String mimeType;
 @XmlElement(required = true)
 @XmlSchemaType(name = "positiveInteger")
 protected BigInteger size;
 @XmlElement(name = "content_available")
 protected boolean contentAvailable;

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
  * Gets the value of the size property.
  * 
  * @return
  *     possible object is
  *     {@link BigInteger }
  *     
  */
 public BigInteger getSize() {
     return size;
 }

 /**
  * Sets the value of the size property.
  * 
  * @param value
  *     allowed object is
  *     {@link BigInteger }
  *     
  */
 public void setSize(BigInteger value) {
     this.size = value;
 }

 /**
  * Gets the value of the contentAvailable property.
  * 
  */
 public boolean isContentAvailable() {
     return contentAvailable;
 }

 /**
  * Sets the value of the contentAvailable property.
  * 
  */
 public void setContentAvailable(boolean value) {
     this.contentAvailable = value;
 }

}
