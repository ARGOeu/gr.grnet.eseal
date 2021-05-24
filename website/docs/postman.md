---
id: postman
title: Communicate with E-Signature API
sidebar_label: Communicate with E-Signature API
keywords:
  - eseal
  - e-seal
  - validation
  - api
  - doc
  - docs
  - documentation
  - documents
  - pdf
  - grnet
  - postman
  - json
---


### Prerequisites

- Postman

### Instructions

In order to communicate with E-Signature Rest API the following provided collection should be imported on Postman platform.

<a target="_blank" href="/assets/e_signature_rest_api.postman_collection.json">Postman Collection</a>


Into collection, you can find the environment variables which are important in order to execute requests on API.

The following variables are predefined and should not be changed.

		{
			"key": "protocol",
			"value": "https"
		},
		{
			"key": "base_url",
			"value": "eseal.devel.einfra.grnet.gr"
		},
		{
			"key": "port",
			"value": "443"
		},
		{
			"key": "signing_path",
			"value": "api/v1/signing"
		},
		{
			"key": "signing_endpoint",
			"value": "remoteSignDocumentDetached"
		},
		{
			"key": "validation_path",
			"value": "api/v1/validation"
		},
		{
			"key": "validation_endpoint",
			"value": "validateDocument"
		},
		{
			"key": "timestamp_path",
			"value": "api/v1/timestamping"
		},
		{
			"key": "timestamp_endpoint",
			"value": "remoteTimestampDocument"
		}

The following variables are empty and should be filled in approprielly. The `username`, `password` and `key` will be provided by GRNET and the rest variables should be filled in by user.

		{
			"key": "username",
			"value": ""
		},
		{
			"key": "password",
			"value": ""
		},
		{
			"key": "key",
			"value": ""
		},
		{
			"key": "pdf_base64_to_sign",
			"value": ""
		},
		{
			"key": "pdf_base64_to_validate",
			"value": ""
		},
		{
			"key": "pdf_base64_to_timestamp",
			"value": ""
		}

Finally , you can find the following 3 POST requests : 

1. [_PDF Signing_](signing.md#post---sign-a-pdf-document-detached)

2. [_PDF Timestamp_](timestamp.md)

3. [_PDF Validation_](validation.md)



### PDF Signing

In order to sign a pdf :

1. [_Convert the pdf to Base64 format_](https://base64.guru/converter/encode/pdf)

2. _Fill in the variable pdf_base64_to_sign_

3. _Execute the PDF Signing request_

*Response Body :* 

`{
"signedDocumentBytes": "JVBER..=="
}`

In order to convert the `signedDocumentBytes` to PDF you can use the following platform [Base64 to PDF](https://base64.guru/converter/decode/pdf).



### PDF Timestamp

In order to timestamp a pdf :

1. [_Convert the pdf to Base64 format_](https://base64.guru/converter/encode/pdf)

2. _Fill in the variable pdf_base64_to_timestamp_

3. _Execute the PDF Timestamp request_

*Response body :*

`{
"timestampedDocumentBytes": "JVBERi0xL..."
}`

In order to convert the `timestampedDocumentBytes` to PDF you can use the following platform [Base64 to PDF](https://base64.guru/converter/decode/pdf).

### PDF Validation

In order to validate a signed/timestamped pdf :

1) _Get the signedDocumentBytes/timestampedDocumentBytes response parameter respectively_

2) _Fill in the variable pdf_base64_to_validate_

3) _Execute the PDF Validation request_















