package com.couchbase.touchdb.support;

import android.os.Handler;
import com.allogy.couch.multipart.CouchMultipart;
import com.allogy.couch.multipart.CouchMultipartAttachments;
import com.couchbase.touchdb.TDBlobKey;
import com.couchbase.touchdb.TDBlobStore;
import com.couchbase.touchdb.replicator.MultipartResponse;
import org.apache.http.Header;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TDMultipartRemoteRequest extends TDRemoteRequest
{
    private TDBlobStore blobStore;

    public TDMultipartRemoteRequest(Handler handler, HttpClientFactory clientFactory, TDBlobStore blobStore, URL url, Object body, TDRemoteRequestCompletionBlock onCompletion, String method)
    {
        super(handler, clientFactory, method, url, body, onCompletion);
        this.blobStore = blobStore;
    }

    @Override
    protected String getAcceptHeader()
    {
        return "multipart/related";
    }

    @Override
    protected Object getFullBodyObject(InputStream stream, List<Header> allHeaders) throws IOException
    {
        CouchMultipart couchMultipart = new CouchMultipart(stream, allHeaders);
        Map<String, TDBlobKey> attachmentNameToKey = couchMultipart.hasAttachments() ?
            processMultipartAttachments(couchMultipart) :
            null;

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> document = (Map<String, Object>) objectMapper.readValue(couchMultipart.getDocument(), Object.class);

        return new MultipartResponse(document, attachmentNameToKey);
    }

    private Map<String, TDBlobKey> processMultipartAttachments(CouchMultipart couchMultipart)
    {
        Map<String, TDBlobKey> attachmentNameToKey;
        CouchMultipartAttachments attachments = couchMultipart.getAttachments();
        attachmentNameToKey = new HashMap<String, TDBlobKey>();
        try
        {
            while (attachments.nextInputStream())
            {
                TDBlobKey blobKey = new TDBlobKey();

                if(!blobStore.storeBlobStream(attachments.getAttachmentInputStream(), blobKey))
                    throw new IOException("Unable to store attachment BLOB");

                attachmentNameToKey.put(attachments.getAttachmentName(), blobKey);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return attachmentNameToKey;
    }
}
