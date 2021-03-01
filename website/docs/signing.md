---
id: signing
sidebar_label: PDF Signing API Calls
---


# PDF Signing API Calls

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

### Errors
Please refer to section [Errors](errors.md) to see all possible Errors
