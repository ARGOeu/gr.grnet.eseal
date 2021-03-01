---
id: timestamp
title: PDF Timestamp API Calls
---


## POST - Timestamp PDF Document

### Request

<b>POST</b> @ <i>/timestamping/timestampDocument</i>### Request Body`bytes`: The pdf document in base64 encoded format
```json
{
  "timestampParameters" : {
    "digestAlgorithm" : "SHA512",
    "timestampContainerForm" : "PDF"
  },
  "toTimestampDocument" : {
    "bytes" : "JVBERi...",
    "name" : "important.pdf"
  }
}
```

### Response

```json
{
    "bytes": "JVBERi0xL...",
    "name": "important-timestamped.pdf"
}
```

