---
id: signing
title: PDF Signing API Calls
sidebar_label: PDF Signing API Calls
keywords:
  - eseal
  - e-seal
  - signing
  - api
  - doc
  - docs
  - documentation
  - documents
  - pdf
  - grnet
---


## POST - Sign a PDF document

### Request

<b>POST</b> @ <i>/api/v1/signing/remoteSignDocument</i>

### Request Body

- `username` : Username to be used at the remote provider API call.
The username/password pair maps to a specific eseal.

- `password` : Password to be used at the remote provider API call.
The username/password pair maps to a specific eseal.

- `key` :  Key that will be used with the TOTP generation.
Each username/password pair corresponds to a specific key.

- `toSignDocument.bytes` : PDF document to be signed in base64 encoded format

- `toSignDocument.name`: Placeholder name for the pdf document


```json
{
	"username": "example-user",
	"password": "example-password",
	"key": "example-key",
	"toSignDocument": {
		"name": "document.pdf",
		"bytes": "JVBERi0xLjM..."
		}
}
```

### Response Body

```json
{
    "signedDocumentBytes": "JVBER..=="
}
```

## POST - Sign a PDF document detached

This API call does not send the entire PDF to the remote eseal provider for signing
but rather only the digest(hash) of the provided document and finally it combines
the returned signature with the original pdf document.The signature will also be visible
containing an image and the CN/OU from the signing certificate.

### Request

<b>POST</b> @ <i>/api/v1/signing/remoteSignDocumentDetached</i>

### Request Body

- `username` : Username to be used at the remote provider API call.
The username/password pair maps to a specific eseal.

- `password` : Password to be used at the remote provider API call.
The username/password pair maps to a specific eseal.

- `key` :  Key that will be used with the TOTP generation.
Each username/password pair corresponds to a specific key.

- `imageVisibility(optional, default=true)` : true or false, about the signature containing
a visual representation as well.

- `imageBytes(optional)` :  Custom image to be included into the visible signature and
override the default, in base64 encoded format.

- `toSignDocument.bytes` : PDF document to be signed in base64 encoded format

- `toSignDocument.name`: Placeholder name for the pdf document


```json
{
  "username": "example-user",	  
  "password": "example-password",	  
  "key": "example-key",	  
  "imageVisibility": true,	  
  "imageBytes": "MJIDdijo...", 
  "toSignDocument": {	    
    "name": "document.pdf",
    "bytes": "JVBERi0xLjM..."
  }
}
```

### Response Body

```json
{
    "signedDocumentBytes": "JVBER..=="
}
```

### Handling of TOTP

The generated tokens are being created with the use of the `SHA1 hashing algorithm`.
They are `6 digits long` and have a
`30 seconds duration`, counting from the beginning of the unix epoch.
There are 2 TOTP tokens per minute, one from the `0th - 30th` second and one 
from the `30th - 60th` second.
In order to compensate for network latency we apply a fail safe mechanism of 5 seconds before
we send any generated TOTP to the provider's backend, meaning that tokens acquired 
between the `25th - 30th` and the `55th - 60th`
seconds of a minute will be waited to expire so a new one can be generated, with enough remaining time,
in order to give a higher success probability for the request.



### Errors
Please refer to section [Errors](errors.md) to see all possible Errors
