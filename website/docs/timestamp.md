---
id: timestamp
title: PDF Timestamp API Calls
sidebar_label: PDF Timestamp API Calls
keywords:
  - eseal
  - e-seal
  - timestamp
  - api
  - doc
  - docs
  - documentation
  - documents
  - pdf
  - grnet
---


## POST - Timestamp PDF Document

### Request

<b>POST</b> @ <i>/api/v1/timestamping/remoteTimestampDocument</i>

### Request Body

 - `toTimestampDocument.bytes` : The pdf document in base64 encoded format

 - `toTimestampDocument.name` : Placeholder name for the pdf document

 - `tspSource(optional)` : The timestamp server that generates the timestamped document. 
The possible values are APED or HARICA. The default value is APED.


```json
{
  "tspSource" : "APED",
  "toTimestampDocument" : {
    "bytes" : "JVBERi...",
    "name" : "important.pdf"
  }
}
```

### Response

```json
{
    "timestampedDocumentBytes": "JVBERi0xL..."
}
```

### Errors
Please refer to section [Errors](errors.md) to see all possible Errors

