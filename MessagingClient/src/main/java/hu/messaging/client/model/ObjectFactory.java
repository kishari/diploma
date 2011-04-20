package hu.messaging.client.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MessageInfoContainer }
     * 
     */
    public MessageInfoContainer createMessageInfoContainer() {
        return new MessageInfoContainer();
    }

    /**
     * Create an instance of {@link ContentDescription }
     * 
     */
    public ContentDescription createContentDescription() {
        return new ContentDescription();
    }

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