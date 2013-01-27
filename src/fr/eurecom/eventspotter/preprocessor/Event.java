package fr.eurecom.eventspotter.preprocessor;

public class Event 
{
  private String eventId;
  private String eventTitle;
  private String publisher;
  private String date;
  private String location;
  private String category;
  private String agent;
  private String eventDescription;
  
  
  public Event() 
  {}

   
  public Event(String eventId, String eventTitle, String publisher, String date, String location, String category, String agent, String eventDescription) 
  {
    super();
    this.eventId = eventId;
    this.eventTitle = eventTitle;
    this.publisher = publisher;
    this.date = date;
    this.location = location;
    this.category = category;
    this.agent = agent;
    this.eventDescription = eventDescription;
  }
  
  public String geteventId() 
  {
    return eventId;
  } 
  public String geteventTitle() 
  {
    return eventTitle;
  }
  public String getpublisher() 
  {
    return publisher;
  }
  public String getdate() 
  {
    return date;
  }
  public String getlocation() 
  {
    return location;
  }
  public String getcategory() 
  {
    return category;
  }
  public String getagent() 
  {
    return agent;
  }
  public String geteventDescription() 
  {
    return eventDescription;
  }
  
  public void seteventId(String eventId) 
  {
    this.eventId = eventId ;
  }
  public void seteventTitle(String eventTitle) 
  {
    this.eventTitle = eventTitle;
  }
  public void setpublisher(String publisher) 
  {
    this.publisher = publisher;
  }
  public void setdate(String date) 
  {
    this.date = date;
  }
  public void setlocation(String location) 
  {
    this.location = location;
  }
  public void setcategory(String category) 
  {
    this.category = category;
  }
  public void setagent(String agent) 
  {
	  this.agent = agent;
  }
  public void seteventDescription(String eventDescription ) 
  {
	  this.eventDescription = eventDescription;
  }
  public String toString() {
    //return "event[eventId = " + eventId + ", eventTitle = " + eventTitle  + ", publisher = " + publisher + "]";
	  return eventId + eventTitle + publisher + date + location + category + agent + eventDescription;
  }
}
   