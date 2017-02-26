package com.ajourdesign.testng;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.List;

public class Methods
{
    @Test
    public void REST_getTotalOrderNumber() throws Exception
    {
        int n = getOrderList().size();
        System.out.println( "\n\tREST_getTotalOrderNumber: "+ n );
        Reporter.log("Total <big><b><i>"+ n +"</i></b></big>");
    }

    @Test
    public void REST_postNewOrder() throws Exception
    {
        int             code;
        String          addr = System.getenv("APPLICATION_URL"), data, json;
        HttpPost        post;
        HttpResponse    resp;

        data = "{\"firstName\":\"Post\",\"lastName\":\"Restful\",\"street\":\"Street\",\"city\":\"City\",\"zip\":\"ZIP\",\"phone\":\"+1 (123) 456-7890\",\"status\":\"closed\",\"placedOn\":0}";

        post = new HttpPost( addr += "/order");
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity( data ));

        resp = HttpClientBuilder.create().build().execute( post );
        code = resp.getStatusLine().getStatusCode();
        Assert.assertEquals( code, 200 );

        json = EntityUtils.toString( resp.getEntity() );
        Assert.assertEquals( json.contains("closed"), true );

        System.out.println( "\n\tREST_postNewOrder "+ addr +"\t"+ json );
    }

    @Test
    public void pullRecentlyAddedOrderDetails() throws Exception
    {
        String json = getOrderById( getRecentOrderId() );
        Assert.assertEquals( json.contains("]"), true );

        System.out.println( "\n\tpullRecentlyAddedOrderDetails " + json );
        Reporter.log("<big><b><i>REST "+ Application.printUnixTimeStamp( json ) +" GMT\n</i></b></big><br>"+ json );
    }

//  Utilities ----------------------------------------------------------------------------------------------------------

    private List<String> getOrderList() throws Exception
    {
        int             code;
        String          addr = System.getenv("APPLICATION_URL"), json;
        HttpGet         hget;
        HttpResponse    resp;

        hget = new HttpGet( addr + "/order");
        hget.addHeader("Content-Type", "application/json");

        resp = HttpClientBuilder.create().build().execute( hget );
        code = resp.getStatusLine().getStatusCode();
        Assert.assertEquals( code, 200 );

        json = EntityUtils.toString( resp.getEntity() );
        Assert.assertEquals( json.contains("]"), true );

        return Application.getOrderIdList( json );
    }

    private String getRecentOrderId() throws Exception
    {
        int     from, indx;
        long    unix, time = 0;
        String  json, poid = null;

        for( String s : getOrderList() )
        {
            json = getOrderById( s );
            from = json.indexOf("\"placedOn\":");
            unix = Long.parseLong(json.substring( from + 11, from + 21 ));
            if( unix > time )
            {
                time = unix;
                indx = json.indexOf("\"id\":\"");
                poid = json.substring( indx + 6, indx + 42 );
            }
        }
        return poid;
    }

    private String getOrderById( String id ) throws Exception
    {
        int             code;
        String          addr = System.getenv("APPLICATION_URL"), json;
        HttpGet         hget;
        HttpResponse    resp;

        hget = new HttpGet( addr += "/order/id/" + id );
        hget.addHeader("Content-Type", "application/json");

        resp = HttpClientBuilder.create().build().execute( hget );
        code = resp.getStatusLine().getStatusCode();
        Assert.assertEquals( code, 200 );

        json = EntityUtils.toString( resp.getEntity() );
        Assert.assertEquals( json.contains("]"), true );

        System.out.println( "\n\tgetOrderById "+ addr );
        return json;
    }
}
/*      function submitNewOrder() {
            var firstName = $("#newOrderFirstName").val();
            var lastName = $("#newOrderLastName").val();
            var street = $("#newOrderStreet").val();
            var city = $("#newOrderCity").val();
            var zip = $("#newOrderZip").val();
            var status = "pending.approval";
            var JSONobj = {
                "firstName": firstName,
                "lastName": lastName,
                "street": street,
                "city": city,
                "zip": zip,
                "status": status
            };
            var data = JSON.stringify(JSONobj);
            $.ajax({
                async: false,
                type: "POST",
                contentType: "application/json",
                url: "order",
                data: data,
                dataType: "json",
                success: function(data) {
                    closeAllDialogs();
                },
                failure: function(errMsg) {
                    alert(errMsg);
                }
            });
        }
        function approveOrder() {
            var id = $("#selectedID").val();
            var phone = $("#approveOrderPhone").val();
            var jqxhr = $.ajax("order/id/" + id).done(function(data) {
                var JSONobj = {
                    "firstName": data[0].firstName,
                    "lastName": data[0].lastName,
                    "street": data[0].street,
                    "city": data[0].city,
                    "zip": data[0].zip,
                    "status": "pending.activation",
                    "id": id,
                    "phone": phone,
                    "timestamp": data[0].timestamp
                };
                var data = JSON.stringify(JSONobj);
                $.ajax({
                    async: false,
                    url: "order",
                    type: "PUT",
                    data: data,
                    contentType: "application/json",
                    success: function(result) {
                        closeAllDialogs();
                    }
                });
            })
        }
        function activateOrder() {
            var id = $("#selectedID").val();
            var jqxhr = $.ajax("order/id/" + id).done(function(data) {
                var JSONobj = {
                    "firstName": data[0].firstName,
                    "lastName": data[0].lastName,
                    "street": data[0].street,
                    "city": data[0].city,
                    "zip": data[0].zip,
                    "status": "closed",
                    "id": id,
                    "phone": data[0].phone,
                    "timestamp": data[0].timestamp
                };
                var data = JSON.stringify(JSONobj);
                $.ajax({
                    async: false,
                    url: "order",
                    type: "PUT",
                    data: data,
                    contentType: "application/json",
                    success: function(result) {
                        closeAllDialogs();
                    }
                });
            })
        }
*/
