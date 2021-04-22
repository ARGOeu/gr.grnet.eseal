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

- `imageBytes(optional)` :  Custom image to be included into the visible signature and
override the default in base64 encoded format.

- `toSignDocument.bytes` : PDF document to be signed in base64 encoded format

- `toSignDocument.name`: Placeholder name for the pdf document


```json
{
	"username": "example-user",
	"password": "example-password",
	"key": "example-key",
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

### Errors
Please refer to section [Errors](errors.md) to see all possible Errors
