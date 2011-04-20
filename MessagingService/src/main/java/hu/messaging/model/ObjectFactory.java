package hu.messaging.model;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {


 public ObjectFactory() {}

 /**
  * Create an instance of {@link InfoMessage.DetailList }
  * 
  */
 public InfoMessage.DetailList createInfoMessageDetailList() {
     return new InfoMessage.DetailList();
 }

 /**
  * Create an instance of {@link UserInfo }
  * 
  */
 public UserInfo createUserInfo() {
     return new UserInfo();
 }

 /**
  * Create an instance of {@link InfoDetail }
  * 
  */
 public InfoDetail createInfoDetail() {
     return new InfoDetail();
 }

 /**
  * Create an instance of {@link InfoMessage }
  * 
  */
 public InfoMessage createInfoMessage() {
     return new InfoMessage();
 }

 /**
  * Create an instance of {@link InfoDetail.RecipientList }
  * 
  */
 public InfoDetail.RecipientList createInfoDetailRecipientList() {
     return new InfoDetail.RecipientList();
 }

}
