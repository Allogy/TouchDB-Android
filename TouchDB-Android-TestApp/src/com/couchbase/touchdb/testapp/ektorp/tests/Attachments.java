package com.couchbase.touchdb.testapp.ektorp.tests;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import com.couchbase.touchdb.ektorp.TouchDBHttpClient;
import com.couchbase.touchdb.testapp.tests.TouchDBTestCase;

public class Attachments extends TouchDBTestCase {

    public void testAttachments() throws IOException {

        HttpClient httpClient = new TouchDBHttpClient(server);
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);

        CouchDbConnector dbConnector = dbInstance.createConnector(DEFAULT_TEST_DB, true);

        TestObject test = new TestObject(1, false, "ektorp");

        //create a document
        dbConnector.create(test);

        //attach file to it
        byte[] attach1 = "This is the body of attach1".getBytes();
        ByteArrayInputStream b = new ByteArrayInputStream(attach1);
        AttachmentInputStream a = new AttachmentInputStream("attach", b, "text/plain");
        dbConnector.createAttachment(test.getId(), test.getRevision(), a);

        AttachmentInputStream readAttachment = dbConnector.getAttachment(test.getId(), "attach");
        Assert.assertEquals("text/plain", readAttachment.getContentType());
        Assert.assertEquals("attach", readAttachment.getId());

        BufferedReader br = new BufferedReader(new InputStreamReader(readAttachment));
        Assert.assertEquals("This is the body of attach1", br.readLine());
    }

}
