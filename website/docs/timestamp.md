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

<b>POST</b> @ <i>/api/v1/timestamping/timestampDocument</i>

### Request Body

`bytes` : The pdf document in base64 encoded format

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

