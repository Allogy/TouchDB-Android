package com.couchbase.touchdb.replicator;

import com.couchbase.touchdb.TDBlobKey;

import java.io.IOException;
import java.util.Map;

public class MultipartResponse
{
    private final Map<String, Object> document;
    private final Map<String, TDBlobKey> attachmentToBlobKey;

    public MultipartResponse(Map<String, Object> document, Map<String,TDBlobKey> attachmentToBlobKey) throws IOException
    {
        this.document = document;
        this.attachmentToBlobKey = attachmentToBlobKey;
    }

    public Map<String, Object> getDocument()
    {
        return document;
    }

    public Map<String, TDBlobKey> getAttachmentToBlobKey()
    {
        return attachmentToBlobKey;
    }
}
