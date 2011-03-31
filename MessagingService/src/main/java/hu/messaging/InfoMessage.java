package hu.messaging;

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
    "infoType",
    "infoDetail"
})
@XmlRootElement(name = "infoMessage")
public class InfoMessage {

    @XmlElement(name = "info_type", required = true)
    protected String infoType;
    @XmlElement(name = "info_detail", required = true)
    protected InfoMessage.InfoDetail infoDetail;

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String value) {
        this.infoType = value;
    }

    public InfoMessage.InfoDetail getInfoDetail() {
        return infoDetail;
    }

    public void setInfoDetail(InfoMessage.InfoDetail value) {
        this.infoDetail = value;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "sender",
        "id",
        "mimeType",
        "subject",
        "sentAt",
        "recipients"
    })
    public static class InfoDetail {

        protected InfoMessage.InfoDetail.Sender sender;
        protected String id;
        @XmlElement(name = "mime_type")
        protected String mimeType;
        protected String subject;
        @XmlElement(name = "sent_at")
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar sentAt;
        protected InfoMessage.InfoDetail.Recipients recipients;

        public InfoMessage.InfoDetail.Sender getSender() {
            return sender;
        }

        public void setSender(InfoMessage.InfoDetail.Sender value) {
            this.sender = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String value) {
            this.id = value;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String value) {
            this.mimeType = value;
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

        public InfoMessage.InfoDetail.Recipients getRecipients() {
            return recipients;
        }

        public void setRecipients(InfoMessage.InfoDetail.Recipients value) {
            this.recipients = value;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "recipient"
        })
        public static class Recipients {

            @XmlElement(required = true)
            protected List<InfoMessage.InfoDetail.Recipients.Recipient> recipient;

            public List<InfoMessage.InfoDetail.Recipients.Recipient> getRecipient() {
                if (recipient == null) {
                    recipient = new ArrayList<InfoMessage.InfoDetail.Recipients.Recipient>();
                }
                return this.recipient;
            }

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

}