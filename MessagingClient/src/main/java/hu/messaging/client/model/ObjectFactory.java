package hu.messaging.client.model;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {


 public ObjectFactory() { }

 public InfoMessage.InfoDetail createInfoMessageInfoDetail() {
     return new InfoMessage.InfoDetail();
 }

 public InfoMessage.InfoDetail.Recipients createInfoMessageInfoDetailRecipients() {
     return new InfoMessage.InfoDetail.Recipients();
 }

 public InfoMessage createInfoMessage() {
     return new InfoMessage();
 }

 public InfoMessage.InfoDetail.Sender createInfoMessageInfoDetailSender() {
     return new InfoMessage.InfoDetail.Sender();
 }

 public InfoMessage.InfoDetail.Recipients.Recipient createInfoMessageInfoDetailRecipientsRecipient() {
     return new InfoMessage.InfoDetail.Recipients.Recipient();
 }

}
