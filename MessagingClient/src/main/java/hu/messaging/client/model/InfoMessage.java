package hu.messaging.client.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "messageType",
    "details"
})
@XmlRootElement(name = "infoMessage")
public class InfoMessage {

	    @XmlElement(name = "message_type", required = true)
	    protected String messageType;
	    @XmlElement(required = true)
	    protected List<InfoMessage.Details> details;

	    public String getMessageType() {
	        return messageType;
	    }

	    public void setMessageType(String value) {
	        this.messageType = value;
	    }
	    
	    public List<InfoMessage.Details> getDetails() {
	        if (details == null) {
	        	details = new ArrayList<InfoMessage.Details>();
	        }
	        return this.details;
	    }

	    @XmlAccessorType(XmlAccessType.FIELD)
	    @XmlType(name = "", propOrder = {
	        "status",
	        "sender",
	        "id",
	        "mimeType",
	        "subject",
	        "sentAt",
	        "recipients"
	    })
	    public static class Details {

	        protected String status;
	        protected InfoMessage.Details.Sender sender;
	        protected String id;
	        @XmlElement(name = "mime_type")
	        protected String mimeType;
	        protected String subject;
	        @XmlElement(name = "sent_at")
	        @XmlSchemaType(name = "date")
	        protected XMLGregorianCalendar sentAt;
	        protected InfoMessage.Details.Recipients recipients;

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
	         * Gets the value of the sender property.
	         *
	         * @return
	         *     possible object is
	         *     {@link InfoMessage.Message.Sender }
	         *
	         */
	        public InfoMessage.Details.Sender getSender() {
	            return sender;
	        }

	        /**
	         * Sets the value of the sender property.
	         *
	         * @param value
	         *     allowed object is
	         *     {@link InfoMessage.Message.Sender }
	         *
	         */
	        public void setSender(InfoMessage.Details.Sender value) {
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
	         * Gets the value of the recipients property.
	         *
	         * @return
	         *     possible object is
	         *     {@link InfoMessage.Message.Recipients }
	         *
	         */
	        public InfoMessage.Details.Recipients getRecipients() {
	            return recipients;
	        }

	        /**
	         * Sets the value of the recipients property.
	         *
	         * @param value
	         *     allowed object is
	         *     {@link InfoMessage.Message.Recipients }
	         *
	         */
	        public void setRecipients(InfoMessage.Details.Recipients value) {
	            this.recipients = value;
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
	         *         &lt;element name="recipient" maxOccurs="unbounded">
	         *           &lt;complexType>
	         *             &lt;complexContent>
	         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	         *                 &lt;sequence>
	         *                   &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
	         *                   &lt;element name="sip_uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
	         *                 &lt;/sequence>
	         *               &lt;/restriction>
	         *             &lt;/complexContent>
	         *           &lt;/complexType>
	         *         &lt;/element>
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
	        public static class Recipients {

	            @XmlElement(required = true)
	            protected List<InfoMessage.Details.Recipients.Recipient> recipient;

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
	             * {@link InfoMessage.Message.Recipients.Recipient }
	             *
	             *
	             */
	            public List<InfoMessage.Details.Recipients.Recipient> getRecipient() {
	                if (recipient == null) {
	                    recipient = new ArrayList<InfoMessage.Details.Recipients.Recipient>();
	                }
	                return this.recipient;
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
	             *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
	             *         &lt;element name="sip_uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
	                "name",
	                "sipUri"
	            })
	            public static class Recipient {

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
	         *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
	         *         &lt;element name="sip_uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
	            "name",
	            "sipUri"
	        })
	        public static class Sender {

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

	    }
}
