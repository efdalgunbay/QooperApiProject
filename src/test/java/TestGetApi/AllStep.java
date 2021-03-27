package TestGetApi;

import org.testng.annotations.Test;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import static io.restassured.RestAssured.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import static org.hamcrest.Matchers.*;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;

public class AllStep {

	private static String Endpoint = "https://api.mocki.io/v1/4862d8e7";

    
	// Step 1 
	//-Making a status control 
	//-check all countries under notification is BR or AR Otherwise display warn
	@Test
	public void Step1() {
 
		given().
		get(Endpoint)
		.then()
		.assertThat()
		.statusCode(200)
		.body("data.notifications.metadata.country",hasItems("TR", "AR"));

	}

	// Step 2 
	//-Get perpagevalue as integer (perpagevalue)
	//-Get number of notifications retrieved as an integer(numberofnotificationvalue)
	//-Make an assure check with assertequal.
	//-Otherwise display a warn
	@Test
	public void Step2() {

		int perpagevalue = get(Endpoint).path("data.pageState.perPage");
		int numberofnotificationvalue = get(Endpoint).path("data.notifications.size()");

		Assert.assertEquals(perpagevalue, numberofnotificationvalue);

		System.out.println(" Per Page Value is " + perpagevalue);
		System.out.println(" Number of notification value " + numberofnotificationvalue);
	}
    
	//Steps 3-4-5-6-7 include this method
	@Test
	public static void OtherSteps() throws JSONException, SAXException, IOException, ParserConfigurationException {
    	
		//I used this method to get Json data asstring
		 RestAssured.baseURI = Endpoint;
		 RequestSpecification httpRequest = RestAssured.given();
		 Response response = httpRequest.get();
		 ResponseBody body = response.getBody();
		 
		 // By using the ResponseBody.asString() method, we can convert the  body
		 String stringJson =body.asString();
   	
		 //I used this method to parse String Json Data as object and array
		 JSONObject req = new JSONObject(stringJson);
		 
		 //Define as object jsondata
		 JSONObject obj = req.getJSONObject("data");
		 
		 //Define as array JsonObject
		 JSONArray arr = obj.getJSONArray("notifications");
   	
   	
		 for (int i = 0; i < arr.length(); ++i) {
   		
   		
   	    JSONObject rec = arr.getJSONObject(i);
   	    //Get all content data as string in a for loop
   	    String content = rec.getString("content");

   	    
   	    // STEP 3 
   	    // Control the content of notifications should be a xml encoded on Base64
   	    boolean isBase64 = Base64.isArrayByteBase64(content.getBytes());
   	    // Otherwise display a warn and fail step
   		Assert.assertTrue(isBase64);
  
   	    //I use this to decoding the data is that encoding on Base64
   		byte [] contentdecoded = Base64.decodeBase64(content);
   		System.out.println(new String(contentdecoded, "UTF-8") + "\n");
   		
   
   		String stringdecoded = new String(contentdecoded);
   		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource src = new InputSource();
		src.setCharacterStream(new StringReader(stringdecoded));
		org.w3c.dom.Document doc = builder.parse(src);
		
		
		//STEP 4
		//Get all notificationId in Json in for loop
   	    String notificationid = rec.getString("notificationId");
	    System.out.println(isUUID(notificationid) + "\n"+ "NotificationId is a valid GUID");
	   
	    //STEP 5
	   //Get all ID in decoding XML element in for loop 
	   String ID = doc.getElementsByTagName("ID").item(0).getTextContent();
		
	if (notificationid.equals(ID)) {
		
		//if notificaiton id is equal ID show this messeage 
		System.out.println("notificationId is equal ID");
	
		
	} else {
		//if notificaiton id is not equal ID show this messeage 

		System.out.println("notificationId is not equal ID");

	
	}
	
		// Get all Text in decoding XML in for loop
		String Text = doc.getElementsByTagName("Text").item(0).getTextContent();
			
		// Get all StatusreasonCode in decoding XML in for loop
		String StatusReasonCode = doc.getElementsByTagName("StatusReasonCode").item(0).getTextContent();
			
		//Get all statusreason in decoding XML in for loop
		String StatusReason = doc.getElementsByTagName("StatusReason").item(0).getTextContent();
		
		
		System.out.println(StatusReasonCode);
		
		
		//STEP 6
		//Checking status reason code and if its 200 
	  
       if (StatusReasonCode.contains("200")) {
   	    
       //Checking text for status reason code is 200
   	   boolean text200 =Text.contains("Document authorized successfully");
   	   //if text is not true display a warn and failed the step
   	   Assert.assertTrue("Text is not true for 200 notifications", text200);
   	    
   	   //Checking status reason for status reason code is 200
   	   boolean Sreason200=StatusReason.contains("Document Authorized");
   	   
   	   //if status reason is not true display a warn and failed the step
   	   Assert.assertTrue("Status reason is not true for 200 notifications", Sreason200);
   	   
   	   }
   	    
     //STEP 7
     //Checking status reason code and if its 400 
    if (StatusReasonCode.contains("400")){
   	    
        //Checking text for status reason code is 400
   	    boolean text400=Text.contains("Document was rejected by tax authority");
    	
   	    //if text is not true display a warn and failed the step
   	    Assert.assertTrue("Text is not true for 400 notifications", text400);
	
    	//Checking status reason for status reason code is 400
   	    boolean Sreason400 = StatusReason.contains("Document Rejected");
    	
   	    //if status reason is not true display a warn and failed the step
   	    Assert.assertTrue("Status reason is not true for 400 notifications", Sreason400);

   	    } 
			
   	    }
   	    
   	}
   
		     
		 
		private  static boolean isUUID(String string) {
		      try {
		         UUID.fromString(string);
		         return true;
		      } catch (Exception ex) {
		         return false;
		      }
		   }
		
		
		
}

